// 수량 관련 변수
let quantity = 1;

// URL에서 파라미터 가져오기
const urlParams = new URLSearchParams(window.location.search);
const restaurantId = urlParams.get('restaurantId');
const tableId = urlParams.get('tableId');
const menuId = urlParams.get('menuId');

// 장바구니 키 생성
function getCartKey(restaurantId, tableId) {
    return `cart_${restaurantId}_${tableId}`;
}

// 메뉴 정보 로드
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

// 수량 증가
function increaseQuantity() {
    const quantityInput = document.getElementById('quantity');
    let currentValue = parseInt(quantityInput.value);
    quantityInput.value = currentValue + 1;
}

// 수량 감소
function decreaseQuantity() {
    const quantityInput = document.getElementById('quantity');
    let currentValue = parseInt(quantityInput.value);
    if (currentValue > 1) {
        quantityInput.value = currentValue - 1;
    }
}

// 장바구니에 추가
async function addToCart() {
    try {
        const quantity = parseInt(document.getElementById('quantity').value);
        
        if (!restaurantId || !tableId || !menuId) {
            throw new Error('필수 정보가 누락되었습니다.');
        }

        const cartKey = getCartKey(restaurantId, tableId);
        const existingCart = JSON.parse(sessionStorage.getItem(cartKey)) || [];
        
        // 같은 메뉴가 있는지 확인
        const existingItemIndex = existingCart.findIndex(item => item.menuId === menuId);
        
        if (existingItemIndex !== -1) {
            // 기존 메뉴 수량 업데이트
            existingCart[existingItemIndex].quantity += quantity;
        } else {
            // 새 메뉴 추가
            existingCart.push({
                menuId: menuId,
                restaurantId: restaurantId,
                quantity: quantity
            });
        }
        
        // 장바구니 저장
        sessionStorage.setItem(cartKey, JSON.stringify(existingCart));
        
        alert('장바구니에 추가되었습니다.');
        window.location.href = `/cart?restaurantId=${restaurantId}&tableId=${tableId}`;
    } catch (error) {
        console.error('장바구니 추가 실패:', error);
        alert('장바구니에 추가하는데 실패했습니다.');
    }
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