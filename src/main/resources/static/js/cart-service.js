export class CartService {
  static getCartKey(tableNumber) {
    return `cart_table_${tableNumber}`;
  }

  static getCartItems(tableNumber) {
    const cartKey = this.getCartKey(tableNumber);
    console.log("Getting cart items for key:", cartKey);
    const items = localStorage.getItem(cartKey);
    console.log("Cart items found:", items);
    return items ? JSON.parse(items) : [];
  }

  static addToCart(tableNumber, menuItem) {
    const cartKey = this.getCartKey(tableNumber);
    const cartItems = this.getCartItems(tableNumber);

    const cartItem = {
      id: menuItem.id,
      name: menuItem.name,
      price: menuItem.price,
      quantity: menuItem.quantity,
    };

    const existingItem = cartItems.find((item) => item.id === cartItem.id);

    if (existingItem) {
      existingItem.quantity += cartItem.quantity;
    } else {
      cartItems.push(cartItem);
    }

    localStorage.setItem(cartKey, JSON.stringify(cartItems));
    return cartItems;
  }

  static saveCartItems(tableNumber, items) {
    localStorage.setItem(this.getCartKey(tableNumber), JSON.stringify(items));
  }

  static updateQuantity(tableNumber, itemId, action) {
    const cartItems = this.getCartItems(tableNumber);
    const item = cartItems.find((item) => item.id === itemId);

    if (item) {
      if (action === "increase") {
        item.quantity += 1;
      } else if (action === "decrease" && item.quantity > 1) {
        item.quantity -= 1;
      }
      this.saveCartItems(tableNumber, cartItems);
    }
    return cartItems;
  }

  static removeItem(tableNumber, itemId) {
    const cartItems = this.getCartItems(tableNumber);
    const updatedItems = cartItems.filter((item) => item.id !== itemId);
    this.saveCartItems(tableNumber, updatedItems);
    return updatedItems;
  }

  static clearCart(tableNumber) {
    const cartKey = this.getCartKey(tableNumber);
    localStorage.removeItem(cartKey);
  }

  static getTotalAmount(tableNumber) {
    const cartItems = this.getCartItems(tableNumber);
    return cartItems.reduce(
      (total, item) => total + item.price * item.quantity,
      0
    );
  }
}
