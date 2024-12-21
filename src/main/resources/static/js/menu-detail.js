import { CartService } from './store/cart-service.js';

let quantity = 1;
let currentMenu = null;

// 수량 조절 함수들
window.decreaseQuantity = function() {
    if (quantity > 1) {
        quantity--;
        updateQuantityDisplay();
    }
};

window.increaseQuantity = function() {
    quantity++;
    updateQuantityDisplay();
};

function updateQuantityDisplay() {
    const quantityElement = document.getElementById('quantity');
    if (quantityElement) {
        quantityElement.textContent = quantity;
    }
}

// 메뉴 데이터 로드 및 표시
async function loadMenuDetail() {
    try {
        const urlParams = new URLSearchParams(window.location.search);
        const menuId = urlParams.get('id');
        
        // localStorage에서 메뉴 데이터 가져오기
        const menuDataStr = localStorage.getItem('menuData');
        const menuData = menuDataStr ? JSON.parse(menuDataStr) : [];
        currentMenu = menuData.find(item => item.id === menuId);

        if (currentMenu) {
            // 이미지 설정
            const menuImage = document.getElementById('menuImage');
            if (menuImage) {
                menuImage.src = currentMenu.imageUrl || '';
                menuImage.alt = currentMenu.name;
            }

            // 이름 설정
            const menuName = document.getElementById('menuName');
            if (menuName) {
                menuName.textContent = currentMenu.name;
            }

            // 가격 설정
            const menuPrice = document.getElementById('menuPrice');
            if (menuPrice) {
                menuPrice.textContent = `${currentMenu.price.toLocaleString()}원`;
            }

            // 설명 설정
            const menuDescription = document.getElementById('menuDescription');
            if (menuDescription) {
                menuDescription.textContent = currentMenu.description || '';
            }

            // 카테고리 설정
            const menuCategory = document.getElementById('menuCategory');
            if (menuCategory) {
                menuCategory.textContent = currentMenu.menuCategory || '';
            }
        }
    } catch (error) {
        console.error('메뉴 데이터 로드 중 오류 발생:', error);
    }
}

// 장바구니 담기
document.getElementById('addToCartButton')?.addEventListener('click', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const tableNumber = urlParams.get('table');
    
    if (!tableNumber) {
        alert('테이블 정보가 없습니다.');
        return;
    }

    if (currentMenu) {
        CartService.addToCart(tableNumber, currentMenu, quantity);
        alert('장바구니에 추가되었습니다.');
        window.location.href = `index.html?table=${tableNumber}`;
    } else {
        alert('메뉴 정보를 찾을 수 없습니다.');
    }
});

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    loadMenuDetail();
    updateQuantityDisplay();
});

// 이미지 데이터 대신 URL만 저장
function saveMenuDetail(menuData) {
    const menuDataToSave = {
        ...menuData,
        image: menuData.imageUrl || null // 실제 이미지 데이터 대신 URL만 저장
    };
    
    localStorage.setItem('menuDetail', JSON.stringify(menuDataToSave));
}

// 썸네일 생성 함수 (예시)
function createThumbnail(imageData) {
    // 이미지 크기를 줄이는 로직
    // 예: Canvas를 사용하여 이미지 리사이징
    // 또는 이미지 품질을 낮추는 등의 방법
    return compressedImageData;
}