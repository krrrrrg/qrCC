import { CartService } from './cart-service.js';

// 이벤트 리스너 초기화 함수
function initializeEventListeners() {
    const menuButton = document.querySelector('.menu-button');
    const dropdownMenu = document.querySelector('.dropdown-menu');
    const orderHistoryBtn = document.getElementById('orderHistoryBtn');
    const loginBtn = document.getElementById('loginBtn');
    const cartButton = document.getElementById('view-cart');

    console.log('menuButton:', menuButton);
    console.log('dropdownMenu:', dropdownMenu);

    // 햄버거 메뉴 토글
    if (menuButton && dropdownMenu) {
        console.log('Adding click event listener to menuButton');
        menuButton.addEventListener('click', () => {
            console.log('Menu button clicked');
            dropdownMenu.classList.toggle('active');
        });

        // 메뉴 외부 클릭 시 닫기
        document.addEventListener('click', (e) => {
            if (!dropdownMenu.contains(e.target) && !menuButton.contains(e.target)) {
                dropdownMenu.classList.remove('active');
            }
        });

        // 스크롤 시 메뉴 닫기
        document.addEventListener('scroll', () => {
            dropdownMenu.classList.remove('active');
        });
    }

    // 주문내역 버튼
    if (orderHistoryBtn) {
        orderHistoryBtn.addEventListener('click', () => {
            const { tableNumber, tableId } = getTableInfo();
            
            if (!tableNumber) {
                alert('테이블 정보가 없습니다.');
                return;
            }

            const orders = JSON.parse(localStorage.getItem('orders') || '[]');
            const tableOrders = orders.filter(order => order.tableNumber === tableNumber);

            if (tableOrders.length === 0) {
                alert('주문 내역이 없습니다.');
                return;
            }

            window.location.href = `order-history.html?table=${tableNumber}&id=${tableId}`;
        });
    }

    // 로그인/로그아웃 버튼 처리
    if (loginBtn) {
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        
        if (currentUser) {
            // 로그인 상태
            loginBtn.innerHTML = `
                <i class="fas fa-sign-out-alt"></i>
                로그아웃
            `;
            loginBtn.addEventListener('click', () => {
                localStorage.removeItem('currentUser');
                window.location.reload();
            });
        } else {
            // 비로그인 상태
            loginBtn.innerHTML = `
                <i class="fas fa-sign-in-alt"></i>
                로그인
            `;
            loginBtn.addEventListener('click', () => {
                const urlParams = new URLSearchParams(window.location.search);
                const tableNo = urlParams.get('table');
                const id = urlParams.get('id');
                
                if (tableNo && id) {
                    window.location.href = `/login?table=${tableNo}&id=${id}`;
                } else {
                    window.location.href = '/login';
                }
            });
        }
    }

    // 장바구니 버튼
    if (cartButton) {
        cartButton.addEventListener('click', () => {
            const { tableNumber, tableId } = getTableInfo();
            if (!tableNumber) {
                alert('테이블 정보가 없습니다.');
                return;
            }
            window.location.href = `cart.html?table=${tableNumber}&id=${tableId}`;
        });
    }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded');
    initializeTableInfo();
    initializeMenus();
    initializeEventListeners();  // 이벤트 리스너 초기화 추가
    
    // 가게 정보 업데이트
    const storeSettings = JSON.parse(localStorage.getItem('storeSettings'));
    if (storeSettings) {
        updateStoreInfo();
    }
    
    // 메뉴 리스트가 있으면 추천 메뉴 표시
    if (document.getElementById('menu-list')) {
        renderMenuItems('recommended');
        document.querySelector('a[href="#recommended"]')?.parentElement.classList.add('active');
    }
    
    // 장바구니 카운트 업데이트
    updateCartCount();
    
    // 로그인 상태에 따른 UI 업데이트
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    if (currentUser) {
        // 로그인 상태일 때 추가적인 UI 변경이 필요하다면 여기에 작성
        console.log('Logged in as:', currentUser.name);
    }
});

// 카테고리 네비게이션 처리
const categoryLinks = document.querySelectorAll('.category-nav a');
categoryLinks.forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        const category = link.getAttribute('href').replace('#', '');
        
        document.querySelector('.category-nav li.active')?.classList.remove('active');
        link.parentElement.classList.add('active');
        
        renderMenuItems(category);
    });
});

// 장바구니 관련 함수들 수정
function getCartKey() {
    const { tableNumber } = getTableInfo();
    return `cart_table_${tableNumber}`;
}

// 장바구니 수량 표시 함수 수정
function updateCartCount() {
    const { tableNumber } = getTableInfo();
    if (!tableNumber) {
        const cartCount = document.querySelector('.cart-count');
        if (cartCount) {
            cartCount.style.display = 'none';
        }
        return;
    }

    const cartItems = CartService.getCartItems(tableNumber);
    const totalItems = cartItems.reduce((sum, item) => sum + (item.quantity || 0), 0);
    
    const cartCount = document.querySelector('.cart-count');
    if (cartCount) {
        if (totalItems > 0) {
            cartCount.textContent = totalItems;
            cartCount.style.display = 'block';
        } else {
            cartCount.style.display = 'none';
        }
    }
}

// 메뉴 아이템 클릭 시 상세 페이지로 이동
function handleMenuClick(item) {
    const { tableNumber, tableId } = getTableInfo();
    if (!tableNumber) {
        alert('테이블 정보가 없습니다.');
        return;
    }
    localStorage.setItem("menuDetail", JSON.stringify(item));
    window.location.href = `menu-detail.html?table=${tableNumber}&id=${tableId}`;
}

// menuData 객체 제거하고 대신 localStorage에서 데이터를 가져오는 함수 추가
function getMenuData() {
    const savedData = localStorage.getItem('menuData');
    if (savedData) {
        const data = JSON.parse(savedData);
        return {
            categories: data.categories || [],
            menus: data.menus || [],
            // 기존 menuData 구조와 맞추기 위한 변환
            all: data.menus || [],
            recommended: (data.menus || []).filter(item => item.isPopular),
            // 카테고리별 메뉴 분류
            ...data.categories.reduce((acc, category) => ({
                ...acc,
                [category.id]: (data.menus || []).filter(menu => menu.category === category.id)
            }), {})
        };
    }
    return {
        categories: [],
        menus: [],
        all: [],
        recommended: []
    };
}

// 메뉴 데이터 초기화 함수 수정
function initializeMenus() {
    const menuData = getMenuData();
    renderMenuItems('recommended'); // 추천 메뉴를 기본으로 표시
}

// 메뉴 렌더링 함수 수정
function renderMenuItems(category) {
    const menuList = document.getElementById('menu-list');
    if (!menuList) return;
    
    menuList.innerHTML = '';
    const menuData = getMenuData();

    // 해당 카테고리의 메뉴 가져오기
    let menus = category === 'all' ? menuData.menus :
                category === 'recommended' ? menuData.menus.filter(item => item.isPopular) :
                menuData.menus.filter(item => item.category === category);

    menus.forEach(item => {
        if (item.isSoldOut) return; // 품절 메뉴는 표시하지 않음

        const menuItem = document.createElement('div');
        menuItem.className = 'menu-item';
        menuItem.innerHTML = `
            <img src="${item.image || 'placeholder.jpg'}" alt="${item.name}">
            <div class="menu-info">
                ${category === 'recommended' || category === 'all' ? 
                    `<span class="category-tag">${getCategoryName(item.category, menuData.categories)}</span>` : ''}
                <h3 class="menu-name">${item.name}</h3>
                <p class="menu-price">${item.price.toLocaleString()}원</p>
                <p class="menu-description">${item.description}</p>
            </div>
        `;

        menuItem.addEventListener("click", () => handleMenuClick(item));

        menuList.appendChild(menuItem);
    });
}

// 카테고리 이름 가져오기 헬퍼 함수
function getCategoryName(categoryId, categories) {
    const category = categories.find(c => c.id === categoryId);
    return category ? category.name : '';
}

// 테이블 정보 관련 함수들
function getTableInfo() {
    const urlParams = new URLSearchParams(window.location.search);
    return {
        tableNumber: urlParams.get('table'),
        tableId: urlParams.get('id')
    };
}

function initializeTableInfo() {
    const { tableNumber } = getTableInfo();
    const tableInfoElements = document.querySelectorAll('.table-info');
    
    tableInfoElements.forEach(element => {
        if (!tableNumber) {
            element.style.display = 'none';
            return;
        }

        // 테이블 번호 표시
        element.textContent = `테이블 ${tableNumber}번`;
        element.style.display = 'inline-block';
    });

    // 테이블 번호가 있는 경우 가게 정보도 업데이트
    if (tableNumber) {
        updateStoreInfo();
    }
}

// 가게 정보 업데이트
function updateStoreInfo() {
    const storeSettings = JSON.parse(localStorage.getItem('storeSettings'));
    console.log('현재 가게 설정:', storeSettings); // 디버깅용

    // 가게 이름 업데이트
    const storeNameElement = document.querySelector('.store-name');
    if (storeNameElement) {
        storeNameElement.textContent = storeSettings?.name || '투스 카페';
    }

    // 공지사항 업데이트
    const promotionBanner = document.querySelector('.promotion-banner');
    if (promotionBanner) {
        if (storeSettings?.notice) {
            promotionBanner.innerHTML = `
                <span class="promotion-icon">📢</span>
                ${storeSettings.notice}
            `;
        } else {
            promotionBanner.innerHTML = `
                <span class="promotion-icon">📢</span>
                1인당 1메뉴 부탁드려요 :)
            `;
        }
    }

    // SNS 링크 업데이트
    if (storeSettings?.snsLink) {
        // 기존 SNS 링크가 있다면 제거
        const existingSnsLink = document.querySelector('.sns-link');
        if (existingSnsLink) {
            existingSnsLink.remove();
        }

        // 새로운 SNS 링크 추가
        const snsLink = document.createElement('a');
        snsLink.href = storeSettings.snsLink;
        snsLink.className = 'sns-link';
        snsLink.target = '_blank';
        snsLink.innerHTML = `
            <i class="fab fa-instagram"></i>
            <span>Instagram</span>
        `;

        // 드롭다운 메뉴에 SNS 링크 추가
        const menuList = document.querySelector('.menu-list');
        if (menuList) {
            menuList.appendChild(snsLink);
        }
    }
}

// 메뉴 드롭다운 업데이트
function updateMenuDropdown() {
    const snsSection = document.getElementById('snsSection');
    const snsLink = document.getElementById('snsLink');
    const storeSettings = JSON.parse(localStorage.getItem('storeSettings'));
    
    if (storeSettings?.snsLink?.trim()) {
        snsSection.style.display = 'block';
        snsLink.href = storeSettings.snsLink;
    } else {
        snsSection.style.display = 'none';
    }
}

// 공지사항 업데이트
function updateNoticeBanner() {
    const promotionBanner = document.querySelector('.promotion-banner');
    const storeSettings = JSON.parse(localStorage.getItem('storeSettings'));
    
    if (storeSettings?.notice) {
        promotionBanner.innerHTML = `
            <span class="promotion-icon">📢</span>
            ${storeSettings.notice}
        `;
    }
}
