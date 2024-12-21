class CartService {
    static getCartKey(tableNumber, restaurantId) {
        return `cart_${restaurantId}_${tableNumber}`;
    }

    static getCartItems(tableNumber, restaurantId) {
        const cartKey = this.getCartKey(tableNumber, restaurantId);
        try {
            const items = localStorage.getItem(cartKey);
            return items ? JSON.parse(items) : [];
        } catch (e) {
            console.error('Failed to get cart items:', e);
            return [];
        }
    }

    static addToCart(tableNumber, restaurantId, menuItem) {
        if (!tableNumber || !restaurantId || !menuItem) {
            console.error('Invalid input parameters');
            return null;
        }

        const cartKey = this.getCartKey(tableNumber, restaurantId);
        const cartItems = this.getCartItems(tableNumber, restaurantId);

        const cartItem = {
            id: menuItem.id,
            name: menuItem.name,
            price: menuItem.price,
            quantity: menuItem.quantity || 1,
            restaurantId: restaurantId
        };

        // 수량 검증
        if (!Number.isInteger(cartItem.quantity) || cartItem.quantity <= 0) {
            throw new Error('올바르지 않은 수량입니다.');
        }

        // 가격 검증
        if (!Number.isFinite(cartItem.price) || cartItem.price <= 0) {
            throw new Error('올바르지 않은 가격입니다.');
        }

        const existingItem = cartItems.find((item) => item.id === cartItem.id);

        if (existingItem) {
            const newQuantity = existingItem.quantity + cartItem.quantity;
            if (newQuantity > 99) {
                throw new Error('최대 주문 수량을 초과했습니다.');
            }
            existingItem.quantity = newQuantity;
        } else {
            cartItems.push(cartItem);
        }

        try {
            localStorage.setItem(cartKey, JSON.stringify(cartItems));
        } catch (e) {
            console.error('Failed to save cart:', e);
            throw new Error('장바구니 저장에 실패했습니다.');
        }

        return cartItems;
    }

    static updateQuantity(tableNumber, restaurantId, itemId, action) {
        if (!tableNumber || !restaurantId || !itemId || !action) {
            console.error('Invalid input parameters');
            return null;
        }

        const cartItems = this.getCartItems(tableNumber, restaurantId);
        const item = cartItems.find((item) => item.id === itemId);

        if (item) {
            if (action === "increase") {
                if (item.quantity >= 99) {
                    throw new Error('최대 주문 수량을 초과했습니다.');
                }
                item.quantity += 1;
            } else if (action === "decrease") {
                if (item.quantity <= 1) {
                    throw new Error('최소 주문 수량은 1개입니다.');
                }
                item.quantity -= 1;
            }
            this.saveCartItems(tableNumber, restaurantId, cartItems);
        }
        return cartItems;
    }

    static removeFromCart(tableNumber, restaurantId, itemId) {
        const cartItems = this.getCartItems(tableNumber, restaurantId);
        const updatedItems = cartItems.filter(item => item.id !== itemId);
        this.saveCartItems(tableNumber, restaurantId, updatedItems);
        return updatedItems;
    }

    static clearCart(tableNumber, restaurantId) {
        const cartKey = this.getCartKey(tableNumber, restaurantId);
        localStorage.removeItem(cartKey);
    }

    static saveCartItems(tableNumber, restaurantId, items) {
        if (!tableNumber || !restaurantId || !Array.isArray(items)) {
            console.error('Invalid input parameters');
            return;
        }

        try {
            const cartKey = this.getCartKey(tableNumber, restaurantId);
            localStorage.setItem(cartKey, JSON.stringify(items));
        } catch (e) {
            console.error('Failed to save cart:', e);
            throw new Error('장바구니 저장에 실패했습니다.');
        }
    }

    static getTotalPrice(tableNumber, restaurantId) {
        const cartItems = this.getCartItems(tableNumber, restaurantId);
        return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
    }
}
