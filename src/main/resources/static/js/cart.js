// 장바구니 상태 관리
let cartItems = [];
let totalPrice = 0;

// 페이지 로드 시 장바구니 정보 로드
async function loadCartItems() {
    try {
        // 로컬 스토리지에서 장바구니 데이터 가져오기
        const cart = JSON.parse(localStorage.getItem('cart')) || [];
        
        // 메뉴 정보 가져오기
        const menuPromises = cart.map(async item => {
            const response = await fetch(`/api/restaurants/${item.restaurantId}/menus/${item.menuId}`);
            if (!response.ok) throw new Error('메뉴 정보를 가져오는데 실패했습니다.');
            const menuData = await response.json();
            return { ...item, menuData };
        });
        
        cartItems = await Promise.all(menuPromises);
        renderCartItems();
        updateTotalPrice();
    } catch (error) {
        console.error('장바구니 로드 실패:', error);
        alert('장바구니 정보를 불러오는데 실패했습니다.');
    }
}

// 장바구니 아이템 렌더링
function renderCartItems() {
    const cartItemsContainer = document.getElementById('cartItems');
    cartItemsContainer.innerHTML = '';
    
    cartItems.forEach((item, index) => {
        const itemElement = document.createElement('div');
        itemElement.className = 'cart-item';
        itemElement.innerHTML = `
            <div class="item-image">
                <img src="${item.menuData.imageSet && item.menuData.imageSet[0] ? 
                    `/api/restaurants/${item.restaurantId}/menus/display?fileName=${item.menuData.imageSet[0].uuid}_${item.menuData.imageSet[0].fileName}` : 
                    '/images/default-menu.jpg'}" 
                    alt="${item.menuData.name}">
            </div>
            <div class="item-info">
                <h3>${item.menuData.name}</h3>
                <p class="item-price">${item.menuData.price.toLocaleString()}원</p>
                <div class="quantity-control">
                    <button class="quantity-btn" onclick="window.decreaseQuantity(${index})">-</button>
                    <span class="quantity">${item.quantity}</span>
                    <button class="quantity-btn" onclick="window.increaseQuantity(${index})">+</button>
                </div>
            </div>
            <button class="remove-btn" onclick="window.removeItem(${index})">×</button>
        `;
        cartItemsContainer.appendChild(itemElement);
    });
    
    updateTotalItemCount();
}

// 수량 증가
function increaseQuantity(index) {
    cartItems[index].quantity++;
    updateCart();
}

// 수량 감소
function decreaseQuantity(index) {
    if (cartItems[index].quantity > 1) {
        cartItems[index].quantity--;
        updateCart();
    }
}

// 아이템 제거
function removeItem(index) {
    cartItems.splice(index, 1);
    updateCart();
}

// 총 가격 업데이트
function updateTotalPrice() {
    totalPrice = cartItems.reduce((sum, item) => 
        sum + (item.menuData.price * item.quantity), 0);
    document.getElementById('totalPrice').textContent = `${totalPrice.toLocaleString()}원`;
}

// 총 아이템 수 업데이트
function updateTotalItemCount() {
    const totalCount = cartItems.reduce((sum, item) => sum + item.quantity, 0);
    document.getElementById('totalItemCount').textContent = totalCount;
}

// 장바구니 업데이트
function updateCart() {
    // 로컬 스토리지 업데이트
    localStorage.setItem('cart', JSON.stringify(cartItems.map(item => ({
        restaurantId: item.restaurantId,
        tableId: item.tableId,
        menuId: item.menuId,
        quantity: item.quantity
    }))));
    
    renderCartItems();
    updateTotalPrice();
}

// 주문하기
async function placeOrder() {
    try {
        const cart = JSON.parse(localStorage.getItem('cart')) || [];
        if (cart.length === 0) {
            alert('장바구니가 비어있습니다.');
            return;
        }

        // 주문 데이터 준비
        const orderData = {
            restaurantId: cart[0].restaurantId,
            tableId: cart[0].tableId,
            items: cart.map(item => ({
                menuId: item.menuId,
                quantity: item.quantity
            }))
        };

        // 주문 API 호출
        const response = await fetch('/api/orders', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(orderData)
        });

        if (!response.ok) {
            throw new Error('주문 처리 중 오류가 발생했습니다.');
        }

        // 주문 성공
        localStorage.removeItem('cart');
        alert('주문이 완료되었습니다.');
        
        // URL에서 restaurantId와 tableId 가져오기
        const urlParams = new URLSearchParams(window.location.search);
        const restaurantId = urlParams.get('restaurantId');
        const tableId = urlParams.get('tableId');
        
        // 메인 페이지로 리다이렉트
        window.location.href = `/?restaurantId=${restaurantId}&tableId=${tableId}`;
    } catch (error) {
        console.error('주문 실패:', error);
        alert('주문에 실패했습니다.');
    }
}

// 전역 함수로 등록
window.increaseQuantity = increaseQuantity;
window.decreaseQuantity = decreaseQuantity;
window.removeItem = removeItem;

// 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', () => {
    loadCartItems();
    
    // 주문하기 버튼 이벤트
    document.getElementById('orderButton').addEventListener('click', placeOrder);
    
    // 뒤로가기 버튼 이벤트
    document.querySelector('.back-button').addEventListener('click', () => {
        const urlParams = new URLSearchParams(window.location.search);
        const restaurantId = urlParams.get('restaurantId');
        const tableId = urlParams.get('tableId');
        window.location.href = `/?restaurantId=${restaurantId}&tableId=${tableId}`;
    });
});
