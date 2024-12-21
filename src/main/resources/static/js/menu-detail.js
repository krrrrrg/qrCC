// URL에서 파라미터 가져오기
const urlParams = new URLSearchParams(window.location.search);
const restaurantId = urlParams.get('restaurantId');
const tableId = urlParams.get('tableId');
const menuId = urlParams.get('menuId');

// 수량 조절 함수
function increaseQuantity() {
    const quantityInput = document.getElementById('quantity');
    let currentValue = parseInt(quantityInput.value);
    quantityInput.value = currentValue + 1;
}

function decreaseQuantity() {
    const quantityInput = document.getElementById('quantity');
    let currentValue = parseInt(quantityInput.value);
    if (currentValue > 1) {
        quantityInput.value = currentValue - 1;
    }
}

// 메뉴 상세 정보 로드
async function loadMenuDetails() {
    try {
        const response = await fetch(`/api/restaurants/${restaurantId}/menus/${menuId}`);
        if (!response.ok) throw new Error('메뉴 정보를 가져오는데 실패했습니다.');
        
        const menuData = await response.json();
        
        // 메뉴 정보 표시
        document.getElementById('menuName').textContent = menuData.name;
        document.getElementById('menuPrice').textContent = `${menuData.price.toLocaleString()}원`;
        document.getElementById('menuDescription').textContent = menuData.description || '';
        
        // 이미지 표시
        if (menuData.imageSet && menuData.imageSet.length > 0) {
            const imageUrl = `/api/restaurants/${restaurantId}/menus/display?fileName=${menuData.imageSet[0].uuid}_${menuData.imageSet[0].fileName}`;
            document.getElementById('menuImage').src = imageUrl;
        }
        
    } catch (error) {
        console.error('메뉴 정보 로드 실패:', error);
        alert('메뉴 정보를 불러오는데 실패했습니다.');
    }
}

// 장바구니에 추가
function addToCart() {
    const quantity = parseInt(document.getElementById('quantity').value);
    
    // 현재 장바구니 가져오기
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    
    // 같은 메뉴가 있는지 확인
    const existingItemIndex = cart.findIndex(item => 
        item.restaurantId === restaurantId && 
        item.menuId === menuId
    );
    
    if (existingItemIndex !== -1) {
        // 이미 있는 메뉴면 수량만 증가
        cart[existingItemIndex].quantity += quantity;
    } else {
        // 새로운 메뉴 추가
        cart.push({
            restaurantId,
            tableId,
            menuId,
            quantity
        });
    }
    
    // 장바구니 저장
    localStorage.setItem('cart', JSON.stringify(cart));
    
    // 성공 메시지 표시
    alert('장바구니에 추가되었습니다.');
    
    // 메인 페이지로 이동
    window.location.href = `/cart?restaurantId=${restaurantId}&tableId=${tableId}`;
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    loadMenuDetails();
    
    // 수량 조절 버튼 이벤트
    document.querySelector('.quantity-btn.decrease').addEventListener('click', decreaseQuantity);
    document.querySelector('.quantity-btn.increase').addEventListener('click', increaseQuantity);
    
    // 장바구니 담기 버튼 이벤트
    document.getElementById('addToCartBtn').addEventListener('click', addToCart);
});