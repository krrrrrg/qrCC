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
            const { tableId, restaurantId } = getTableInfo();
            
            if (!tableId || !restaurantId) {
                alert('테이블 정보가 없습니다.');
                return;
            }

            const orders = JSON.parse(localStorage.getItem('orders') || '[]');
            const tableOrders = orders.filter(order => order.tableId === tableId);

            if (tableOrders.length === 0) {
                alert('주문 내역이 없습니다.');
                return;
            }

            window.location.href = `/order-history?restaurantId=${restaurantId}&tableId=${tableId}`;
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
                const tableId = urlParams.get('tableId');
                const restaurantId = urlParams.get('restaurantId');
                
                if (tableId && restaurantId) {
                    window.location.href = `/login?restaurantId=${restaurantId}&tableId=${tableId}`;
                } else {
                    window.location.href = '/login';
                }
            });
        }
    }

    // 장바구니 버튼
    if (cartButton) {
        cartButton.addEventListener('click', () => {
            const urlParams = new URLSearchParams(window.location.search);
            const tableId = urlParams.get('tableId');
            const restaurantId = urlParams.get('restaurantId');
            
            if (!tableId || !restaurantId) {
                alert('테이블 정보가 없습니다.');
                return;
            }
            window.location.href = `/cart?restaurantId=${restaurantId}&tableId=${tableId}`;
        });
    }
}

// URL에서 파라미터 가져오기
function getUrlParameter(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

// 테이블 정보 설정
function setTableInfo() {
    const tableId = getUrlParameter('tableId');
    const restaurantId = getUrlParameter('restaurantId');
    const tableIndicator = document.querySelector('.table-indicator');
    
    if (tableId && restaurantId) {
        // 테이블 정보를 세션 스토리지에 저장
        sessionStorage.setItem('tableId', tableId);
        sessionStorage.setItem('restaurantId', restaurantId);
        
        // 테이블 번호 표시
        fetch(`/api/tables/${tableId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('테이블 정보를 가져올 수 없습니다.');
                }
                return response.json();
            })
            .then(table => {
                const tableNumberElement = document.getElementById('tableNumber');
                if (tableNumberElement) {
                    tableNumberElement.textContent = table.tableNumber;
                    tableIndicator.style.display = 'inline-block';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                if (tableIndicator) {
                    tableIndicator.style.display = 'none';
                }
            });
    } else {
        if (tableIndicator) {
            tableIndicator.style.display = 'none';
        }
    }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded');
    setTableInfo();
    initializeTableInfo();
    initializeMenus();
    initializeEventListeners();  // 이벤트 리스너 초기화 추가
    
    // 가게 정보 업데이트
    updateStoreInfo();
    
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
    const { tableId } = getTableInfo();
    return `cart_table_${tableId}`;
}

// 장바구니 수량 표시 함수 수정
function updateCartCount() {
    const { tableId } = getTableInfo();
    if (!tableId) {
        const cartCount = document.querySelector('.cart-count');
        if (cartCount) {
            cartCount.style.display = 'none';
        }
        return;
    }

    const cartItems = CartService.getCartItems(tableId);
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
    const urlParams = new URLSearchParams(window.location.search);
    const tableId = urlParams.get('tableId');
    const restaurantId = urlParams.get('restaurantId');
    
    if (!tableId || !restaurantId) {
        alert('테이블 정보가 없습니다.');
        return;
    }
    
    window.location.href = `/menu-detail?restaurantId=${restaurantId}&tableId=${tableId}&menuId=${item.id}`;
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

// 메뉴 표시 함수
function displayMenus(menus) {
    const menuContainer = document.querySelector('.menu-container');
    if (!menuContainer) return;

    // 메뉴 카테고리별로 그룹화
    const menusByCategory = menus.reduce((acc, menu) => {
        const category = menu.menuCategory || '기타';
        if (!acc[category]) {
            acc[category] = [];
        }
        acc[category].push(menu);
        return acc;
    }, {});

    // 카테고리별로 메뉴 표시
    let html = '';
    Object.keys(menusByCategory).sort().forEach(category => {
        html += `
            <div class="menu-category">
                <h2 class="category-title">${category}</h2>
                <div class="menu-items">
                    ${menusByCategory[category].map(menu => `
                        <div class="menu-item">
                            ${menu.imageURL ? 
                                `<img src="${menu.imageURL}" alt="${menu.name}" class="menu-image">` : 
                                '<div class="menu-image-placeholder"></div>'
                            }
                            <div class="menu-details">
                                <h3 class="menu-name">${menu.name}</h3>
                                <p class="menu-price">${menu.price.toLocaleString()}원</p>
                                <button onclick="addToCart(${menu.id})" class="add-to-cart-btn">
                                    장바구니 담기
                                </button>
                            </div>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    });

    menuContainer.innerHTML = html;
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
        tableId: urlParams.get('tableId'),
        restaurantId: urlParams.get('restaurantId')
    };
}

function initializeTableInfo() {
    const { tableId } = getTableInfo();
    const tableInfoElements = document.querySelectorAll('.table-info');
    
    tableInfoElements.forEach(element => {
        if (!tableId) {
            element.style.display = 'none';
            return;
        }

        // 테이블 번호 표시
        element.textContent = `테이블 ${tableId}번`;
        element.style.display = 'inline-block';
    });

    // 테이블 번호가 있는 경우 가게 정보도 업데이트
    if (tableId) {
        updateStoreInfo();
    }
}

// 가게 정보 업데이트
function updateStoreInfo() {
    // URL에서 레스토랑 ID 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const restaurantId = urlParams.get('restaurantId');
    
    if (!restaurantId) {
        console.log('레스토랑 ID가 없습니다.');
        return;
    }

    // DB에서 가게 정보 가져오기
    fetch(`/api/restaurants/${restaurantId}`)
        .then(response => response.json())
        .then(storeData => {
            console.log('현재 가게 설정:', storeData);

            // 가게 이름 업데이트
            const storeNameElement = document.querySelector('.store-name');
            if (storeNameElement) {
                storeNameElement.textContent = storeData?.name || '투스 카페';
            }

            // 공지사항 업데이트
            const promotionBanner = document.querySelector('.promotion-banner');
            if (promotionBanner) {
                if (storeData?.description) {
                    promotionBanner.innerHTML = `
                        <span class="promotion-icon">📢</span>
                        ${storeData.description}
                    `;
                }
            }

            // SNS 링크 업데이트
            if (storeData?.snsLink) {
                // 기존 SNS 링크가 있다면 제거
                const existingSnsLink = document.querySelector('.sns-link');
                if (existingSnsLink) {
                    existingSnsLink.remove();
                }

                // 새로운 SNS 링크 추가
                const snsLink = document.createElement('a');
                snsLink.href = storeData.snsLink;
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
        })
        .catch(error => {
            console.error('가게 정보를 불러오는데 실패했습니다:', error);
        });
}

// SNS 링크 업데이트
function updateSnsLink() {
    const snsSection = document.getElementById('snsSection');
    const snsLink = document.getElementById('snsLink');
    
    fetch('/api/restaurant/1')
        .then(response => response.json())
        .then(storeData => {
            if (storeData?.snsLink?.trim()) {
                snsSection.style.display = 'block';
                snsLink.href = storeData.snsLink;
            } else {
                snsSection.style.display = 'none';
            }
        })
        .catch(error => {
            console.error('가게 정보를 불러오는데 실패했습니다:', error);
            snsSection.style.display = 'none';
        });
}

// 공지사항 업데이트
function updatePromotionBanner() {
    const promotionBanner = document.querySelector('.promotion-banner');
    
    fetch('/api/restaurant/1')
        .then(response => response.json())
        .then(storeData => {
            if (storeData?.description) {
                promotionBanner.innerHTML = `
                    <span class="promotion-icon">📢</span>
                    ${storeData.description}
                `;
            }
        })
        .catch(error => {
            console.error('가게 정보를 불러오는데 실패했습니다:', error);
        });
}
