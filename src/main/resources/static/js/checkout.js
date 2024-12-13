import { CartService } from './scripts/store/cart-service.js';
import { OrderService } from './scripts/store/order-service.js';

// 디버그 로그 함수
function debugLog(message, data) {
    console.log(`[DEBUG] ${message}:`, data);
}

// 장바구니 관리 함수들
function getCartKey(tableNumber) {
    return `cart_table_${tableNumber}`; // cart_table_1 형식으로 변경
}

function getTotalAmount(tableNumber) {
    const cartItems = CartService.getCartItems(tableNumber);
    return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
}

function clearCart(tableNumber) {
    CartService.clearCart(tableNumber);
}

document.addEventListener('DOMContentLoaded', async function() {
    const { tableNumber, tableId } = getTableInfo();
    debugLog('테이블 정보', { tableNumber, tableId });
    
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    debugLog('현재 사용자', currentUser);
    
    const cartItems = CartService.getCartItems(tableNumber);
    debugLog('장바구니 아이템', cartItems);

    if (!cartItems || cartItems.length === 0) {
        alert('장바구니가 비어있습니다.');
        window.location.href = `cart.html?table=${tableNumber}&id=${tableId}`;
        return;
    }

    const checkoutButton = document.getElementById('checkoutButton');
    if (checkoutButton) {
        checkoutButton.addEventListener('click', async () => {
            if (!currentUser) {
                localStorage.setItem('checkoutRedirect', window.location.href);
                window.location.href = `login.html?table=${tableNumber}&id=${tableId}`;
                return;
            }

            try {
                const orderService = new OrderService();
                const orderData = {
                    tableNumber,
                    tableId,
                    userId: currentUser.userId,
                    items: cartItems,
                    totalAmount: getTotalAmount(tableNumber)
                };

                const order = await orderService.createOrder(orderData);
                CartService.clearCart(tableNumber);
                window.location.href = `order-complete.html?orderId=${order.id}&table=${tableNumber}&id=${tableId}`;
            } catch (error) {
                alert('주문 처리 중 오류가 발생했습니다.');
                console.error('Order error:', error);
            }
        });
    }
});

function getTableInfo() {
    const urlParams = new URLSearchParams(window.location.search);
    const tableNumber = urlParams.get('table');
    const tableId = urlParams.get('id');
    
    if (!tableNumber) {
        alert('올바르지 않은 접근입니다.');
        window.location.href = 'index.html';
        return {};
    }
    
    return { tableNumber, tableId };
}