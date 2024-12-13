import { CartService } from './store/cart-service.js';

document.addEventListener('DOMContentLoaded', function() {
    const menuDetail = JSON.parse(localStorage.getItem("menuDetail"));
    const quantityDisplay = document.getElementById('quantity-display');
    const decreaseBtn = document.getElementById('decrease-quantity');
    const increaseBtn = document.getElementById('increase-quantity');
    const addToCartBtn = document.getElementById('add-to-cart');
    let quantity = 1;

    // 메뉴 상세 정보 렌더링
    if (menuDetail) {
        document.getElementById("menu-image").src = menuDetail.image;
        document.getElementById("menu-title").textContent = menuDetail.name;
        document.getElementById("menu-price").textContent = 
            `${menuDetail.price.toLocaleString()}원`;
        document.getElementById("menu-description").textContent = menuDetail.description;
        
        // 초기 총 가격 설정
        updateTotalPrice();
    }

    // 총 가격 업데이트 함수
    function updateTotalPrice() {
        if (menuDetail) {
            const totalPrice = menuDetail.price * quantity;
            const totalPriceElement = document.getElementById('total-price');
            if (totalPriceElement) {
                totalPriceElement.textContent = `${totalPrice.toLocaleString()}원 담기`;
            }
        }
    }

    // 수량 감소
    decreaseBtn.addEventListener('click', () => {
        if (quantity > 1) {
            quantity--;
            quantityDisplay.textContent = quantity;
            updateTotalPrice();
        }
    });

    // 수량 증가
    increaseBtn.addEventListener('click', () => {
        if (quantity < 99) {
            quantity++;
            quantityDisplay.textContent = quantity;
            updateTotalPrice();
        }
    });

    // 장바구니 담기
    addToCartBtn.addEventListener('click', () => {
        const urlParams = new URLSearchParams(window.location.search);
        const tableNumber = urlParams.get('table');
        const tableId = urlParams.get('id');
        
        if (!tableNumber) {
            alert('테이블 정보가 없습니다.');
            return;
        }

        // CartService 사용
        CartService.addToCart(tableNumber, {
            ...menuDetail,
            quantity: quantity,
            tableNumber: tableNumber
        }, quantity);

        alert('장바구니에 담았습니다.');
        
        // 장바구니 카운트 업데이트
        const cartCount = document.querySelector('.cart-count');
        if (cartCount) {
            const cartItems = CartService.getCartItems(tableNumber);
            const totalItems = cartItems.reduce((sum, item) => sum + (item.quantity || 0), 0);
            cartCount.textContent = totalItems;
            cartCount.style.display = 'block';
        }
        
        window.location.href = `cart.html?table=${tableNumber}&id=${tableId}`;
    });
}); 