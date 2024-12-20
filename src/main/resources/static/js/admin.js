// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 메뉴 탭 전환
    const menuItems = document.querySelectorAll('.admin-menu a');
    const sections = document.querySelectorAll('.admin-section');

    // 현재 활성화된 섹션 ID를 저장
    let currentSection = '';

    menuItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href').substring(1);

            // 이전 섹션의 모달 닫기
            if (currentSection) {
                const prevModal = document.querySelector(`#${currentSection} .modal`);
                if (prevModal) {
                    prevModal.style.display = 'none';
                }
            }

            // 활성 탭 변경
            menuItems.forEach(menuItem => menuItem.classList.remove('active'));
            this.classList.add('active');

            // 섹션 표시/숨김
            sections.forEach(section => {
                if (section.id === targetId) {
                    section.classList.add('active');
                    currentSection = targetId;

                    // 주문 관리 탭이 활성화되면 주문 목록 새로고침
                    if (targetId === 'order-management') {
                        loadOrders();
                    }
                } else {
                    section.classList.remove('active');
                }
            });
        });
    });

    // 메뉴 목록 로드
    loadMenuList();

    // 현재 선택된 레스토랑 ID
    let currentRestaurantId = null; // 전역 변수로 선언

    // 레스토랑 선택 이벤트
    const restaurantSelector = document.getElementById('restaurantSelector');
    if (restaurantSelector) {
        restaurantSelector.addEventListener('change', function() {
            currentRestaurantId = this.value; // 선택된 레스토랑 ID 업데이트
            if (currentRestaurantId) {
                loadRestaurantInfo(currentRestaurantId);
            } else {
                document.getElementById('storeSettingsForm').reset();
            }
        });
        
        // 초기 가게 목록 로드
        loadRestaurantList().then(() => {
            // 가게 목록을 불러온 후, 첫 번째 가게의 정보를 표시
            if (restaurantSelector.options.length > 1) {
                const firstRestaurantId = restaurantSelector.options[1].value;
                loadRestaurantInfo(firstRestaurantId);
            }
        });
    }

    // 레스토랑 정보 로드
    async function loadRestaurantInfo(restaurantId) {
        if (!restaurantId) {
            // 새 가게 등록 모드
            document.getElementById('storeSettingsForm').reset();
            currentRestaurantId = null;
            document.getElementById('deleteRestaurantBtn').style.display = 'none';
            return;
        }

        try {
            const response = await fetch(`/api/owner/restaurants/${restaurantId}`, {
                headers: {
                    'Accept': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error('가게 정보를 불러오는데 실패했습니다.');
            }
            const storeData = await response.json();
            
            // 폼에 데이터 설정
            document.getElementById('restaurantId').value = storeData.id || '';
            document.getElementById('storeName').value = storeData.name || '';
            document.getElementById('storeAddress').value = storeData.address || '';
            document.getElementById('storeCategory').value = storeData.category || '';
            document.getElementById('storePhone').value = storeData.phoneNumber || '';
            document.getElementById('openTime').value = storeData.openTime || '';
            document.getElementById('closeTime').value = storeData.closeTime || '';
            document.getElementById('storeNotice').value = storeData.description || '';
            document.getElementById('storeSns').value = storeData.refLink || '';
            
            document.getElementById('deleteRestaurantBtn').style.display = 'inline-block';
            currentRestaurantId = restaurantId;
        } catch (error) {
            console.error('Error:', error);
            alert(error.message);
        }
    }

    // 가게 설정 저장
    async function handleStoreSettings(e) {
        e.preventDefault();
        
        const formData = {
            id: currentRestaurantId || null,
            name: document.getElementById('storeName').value,
            address: document.getElementById('storeAddress').value,
            category: document.getElementById('storeCategory').value,
            phoneNumber: document.getElementById('storePhone').value,
            openTime: document.getElementById('openTime').value,
            closeTime: document.getElementById('closeTime').value,
            description: document.getElementById('storeNotice').value,
            refLink: document.getElementById('storeSns').value
        };

        const method = currentRestaurantId ? 'PUT' : 'POST';
        const url = currentRestaurantId 
            ? `/api/owner/restaurants/${currentRestaurantId}` 
            : '/api/owner/restaurants';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                throw new Error('가게 정보 저장에 실패했습니다.');
            }

            if (!currentRestaurantId) {
                // 새로 생성된 레스토랑의 ID를 받아옴
                const newId = await response.json();
                currentRestaurantId = newId;
            }

            alert('가게 정보가 성공적으로 저장되었습니다.');
            location.reload(); // 페이지 새로고침
        } catch (error) {
            console.error('Error:', error);
            alert(error.message);
        }
    }

    // 레스토랑 삭제
    async function deleteRestaurant(restaurantId) {
        if (!confirm('정말로 이 레스토랑을 삭제하시겠습니까?')) {
            return;
        }

        try {
            const response = await fetch(`/api/owner/restaurants/${restaurantId}`, {
                method: 'DELETE',
                headers: {
                    'Accept': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('레스토랑 삭제에 실패했습니다.');
            }

            alert('레스토랑이 성공적으로 삭제되었습니다.');
            location.reload(); // 페이지 새로고침
        } catch (error) {
            console.error('Error:', error);
            alert(error.message);
        }
    }

    // 삭제 버튼 이벤트 핸들러
    document.getElementById('deleteRestaurantBtn').addEventListener('click', function() {
        if (currentRestaurantId) {
            deleteRestaurant(currentRestaurantId);
        }
    });

    // 폼 이벤트 리스너
    const storeSettingsForm = document.getElementById('storeSettingsForm');
    if (storeSettingsForm) {
        storeSettingsForm.addEventListener('submit', handleStoreSettings);
    }

    // 테이블 관리 기능 초기화
    if (document.querySelector('.table-list')) {
        renderTables();
    }

    // 주문 목록 초기 로드 및 자동 갱신 설정
    if (document.querySelector('.order-list')) {
        loadOrders();
        // 주문 목록 자동 갱신 (5초마다)
        setInterval(loadOrders, 5000);
    }

    // 디버깅용 - localStorage 내용 확인
    console.log('현재 주문 목록:', JSON.parse(localStorage.getItem('orders')));

    // 비밀번호 변경 처리
    document.getElementById('accountSettingsForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const currentPassword = document.getElementById('currentPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        // 현재 저장된 관리자 정보 가져오기
        const adminUser = JSON.parse(localStorage.getItem('adminUser'));

        if (!adminUser) {
            alert('로그인 정보를 찾을 수 없습니다.');
            return;
        }

        if (newPassword !== confirmPassword) {
            alert('새 비밀번호가 일치하지 않습니다.');
            return;
        }

        // 현재 비밀번호 확인 - 하드코딩된 비밀번호와 비교
        if (currentPassword !== 'admin123' && currentPassword !== adminUser.password) {
            alert('현재 비밀번호가 일치하지 않습니다.');
            return;
        }

        try {
            // 새 비밀번호로 업데이트
            adminUser.password = newPassword;
            localStorage.setItem('adminUser', JSON.stringify(adminUser));
            
            alert('비밀번호가 성공적으로 변경되었습니다.');
            document.getElementById('accountSettingsForm').reset();
        } catch (error) {
            console.error('Error:', error);
            alert('비밀번호 변경 중 오류가 발생했습니다.');
        }
    });

    // 계정 삭제 모달 관련 변수들
    const deleteAccountModal = document.getElementById('deleteAccountModal');
    const showDeleteAccountBtn = document.getElementById('showDeleteAccountBtn');
    const closeDeleteModalBtn = document.getElementById('closeDeleteModalBtn');
    const deleteAccountForm = document.getElementById('deleteAccountForm');

    // 계정 삭제 모달 표시
    if (showDeleteAccountBtn) {
        showDeleteAccountBtn.addEventListener('click', function() {
            if (deleteAccountModal) {
                deleteAccountModal.style.display = 'block';
            }
        });
    }

    // 계정 삭제 모달 닫기
    if (closeDeleteModalBtn) {
        closeDeleteModalBtn.addEventListener('click', function() {
            if (deleteAccountModal) {
                deleteAccountModal.style.display = 'none';
            }
        });
    }

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(event) {
        if (event.target === deleteAccountModal) {
            deleteAccountModal.style.display = 'none';
        }
    });

    // 계정 삭제 폼 제출
    if (deleteAccountForm) {
        deleteAccountForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            if (confirm('정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
                try {
                    const response = await fetch('/api/members/delete', {
                        method: 'DELETE',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    });

                    if (response.ok) {
                        alert('계정이 성공적으로 삭제되었습니다.');
                        window.location.href = '/logout';
                    } else {
                        throw new Error('계정 삭제에 실패했습니다.');
                    }
                } catch (error) {
                    console.error('Error:', error);
                    alert(error.message);
                }
            }
        });
    }

    // 각 섹션별 모달 관리
    sections.forEach(section => {
        const modal = section.querySelector('.modal');
        if (modal) {
            // 모달 외부 클릭시 닫기
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    modal.style.display = 'none';
                }
            });

            // 닫기 버튼 클릭시 모달 닫기
            const closeBtn = modal.querySelector('.cancel-button, .close');
            if (closeBtn) {
                closeBtn.addEventListener('click', () => {
                    modal.style.display = 'none';
                });
            }
        }
    });
});

// 로그아웃 함수
function handleAdminLogout() {
    const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    
    fetch('/owner/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        credentials: 'same-origin'
    })
    .then(response => {
        if (response.ok) {
            window.location.href = '/owner/login';
        } else {
            console.error('로그아웃 실패');
        }
    })
    .catch(error => {
        console.error('로그아웃 중 오류 발생:', error);
    });
}

// 레스토랑 목록 로드
async function loadRestaurantList() {
    try {
        const response = await fetch('/api/owner/restaurants', {
            headers: {
                'Accept': 'application/json'
            }
        });
        if (!response.ok) {
            throw new Error('가게 목록을 불러오는데 실패했습니다.');
        }
        const data = await response.json();
        const restaurants = data.dtoList || []; // PageResponseDTO에서 dtoList 추출
        
        const selector = document.getElementById('restaurantSelector');
        if (!selector) return;

        // 기존 옵션 제거 (첫 번째 '새 가게 등록' 옵션 제외)
        while (selector.options.length > 1) {
            selector.remove(1);
        }

        // 가게 목록 추가
        if (restaurants && restaurants.length > 0) {
            for (let i = 0; i <restaurants.length; i++) {
                const restaurant = restaurants[i];
                const option = document.createElement('option');
                option.value = restaurant.id;
                option.textContent = restaurant.name;
                selector.appendChild(option);
            }
        }

        // 현재 선택된 레스토랑이 있다면 선택
        if (currentRestaurantId) {
            selector.value = currentRestaurantId;
        }
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

// 메뉴 관리 함수들
function loadMenuList() {
    const menuData = JSON.parse(localStorage.getItem('menuData') || '{"menus": []}');
    const menuList = document.getElementById('adminMenuList');
    
    if (!menuList) return;

    if (!menuData.menus || !Array.isArray(menuData.menus)) {
        menuList.innerHTML = '<p>등록된 메뉴가 없습니다.</p>';
        return;
    }

    menuList.innerHTML = menuData.menus.map(menu => `
        <div class="menu-item">
            <img src="${menu.image || 'placeholder.jpg'}" alt="${menu.name}">
            <div class="menu-info">
                <div class="menu-header">
                    <h3>${menu.name}</h3>
                    <div class="menu-actions">
                        <button onclick="editMenu('${menu.id}')" class="edit-btn">수정</button>
                        <button onclick="deleteMenu('${menu.id}')" class="delete-btn">삭제</button>
                    </div>
                </div>
                <p class="menu-category">${menu.category}</p>
                <p class="menu-price">${menu.price.toLocaleString()}원</p>
            </div>
        </div>
    `).join('');
}

function showAddMenuModal() {
    const currentSection = document.querySelector('.admin-section.active');
    if (currentSection) {
        const modal = currentSection.querySelector('#menuModal');
        if (modal) {
            modal.style.display = 'block';
        }
    }
}

function closeMenuModal() {
    const currentSection = document.querySelector('.admin-section.active');
    if (currentSection) {
        const modal = currentSection.querySelector('#menuModal');
        if (modal) {
            modal.style.display = 'none';
        }
    }
}

function handleMenuForm(event) {
    event.preventDefault();
    
    const formData = {
        name: document.getElementById('menuName').value,
        category: document.getElementById('menuCategory').value,
        price: parseInt(document.getElementById('menuPrice').value),
        description: document.getElementById('menuDescription').value,
        isPopular: document.getElementById('isPopular').checked
    };

    // 이미지 처리 (실제 구현에서는 서버에 업로드)
    const imageFile = document.getElementById('menuImage').files[0];
    if (imageFile) {
        const reader = new FileReader();
        reader.onload = function(e) {
            formData.image = e.target.result;
            saveMenu(formData);
        };
        reader.readAsDataURL(imageFile);
    } else {
        saveMenu(formData);
    }
}

function saveMenu(menuData) {
    let menus = JSON.parse(localStorage.getItem('menuData')) || [];
    menuData.id = Date.now();
    menus.push(menuData);
    localStorage.setItem('menuData', JSON.stringify(menus));
    
    closeMenuModal();
    loadMenuList();
}

// 테이블 관리 함수들
function addTable() {
    const tableNumber = document.getElementById('newTableNumber').value;
    if (!tableNumber) return alert('테이블 번호를 입력하세요');

    const tables = JSON.parse(localStorage.getItem('tables') || '[]');
    
    // 중복 체크
    if (tables.some(table => table.number === tableNumber)) {
        return alert('이미 존재하는 테이블 번호입니다');
    }

    // 현재 도메인 가져오기
    const currentDomain = window.location.origin;
    
    // 새 테이블 추가
    const newTable = {
        number: tableNumber,
        id: Date.now().toString(36),
        url: `${currentDomain}/?table=${tableNumber}&id=${Date.now().toString(36)}`,
        active: true,
        createdAt: new Date().toISOString()
    };

    tables.push(newTable);
    localStorage.setItem('tables', JSON.stringify(tables));
    renderTables();
}

function deleteTable(tableNumber) {
    if (confirm(`${tableNumber}번 테이블을 삭제하시겠습니까?`)) {
        const tables = JSON.parse(localStorage.getItem('tables') || '[]');
        const filteredTables = tables.filter(table => table.number !== tableNumber);
        localStorage.setItem('tables', JSON.stringify(filteredTables));
        renderTables();
    }
}

function generateTableUrl(tableNumber) {
    // 기본 URL + 테이블 번호 + 유니크 식별자
    const baseUrl = window.location.origin;
    const uniqueId = Date.now().toString(36);
    return `${baseUrl}/?table=${tableNumber}&id=${uniqueId}`;
}

function renderTables() {
    const tableList = document.querySelector('.table-list');
    const tables = JSON.parse(localStorage.getItem('tables') || '[]');
    
    tableList.innerHTML = tables.map(table => `
        <div class="table-item">
            <span class="table-number">테이블 ${table.number}번</span>
            <div class="table-actions">
                <button onclick="showQR('${table.number}')" class="edit-btn">
                    <i class="fas fa-qrcode"></i> QR코드
                </button>
                <button onclick="deleteTable('${table.number}')" class="delete-btn">
                    <i class="fas fa-trash"></i> 삭제
                </button>
            </div>
        </div>
    `).join('');
}

// QR 코드 표시 함수
function showQR(tableNumber) {
    const tables = JSON.parse(localStorage.getItem('tables') || '[]');
    const table = tables.find(t => t.number === tableNumber);
    if (!table) return;

    // QR 코드 미리보기 영역 업데이트
    const qrPreview = document.querySelector('.qr-preview');
    const qrInfo = qrPreview.querySelector('.qr-info');
    
    // 테이블 정보 업데이트
    qrPreview.querySelector('.table-title').textContent = `테이블 ${tableNumber}번`;
    qrPreview.querySelector('#tableLink').textContent = table.url;
    qrPreview.querySelector('#tableLink').href = table.url;
    
    // QR 코드 생성
    const qrCode = document.getElementById('qrCode');
    qrCode.innerHTML = ''; // 기존 QR 코드 제거
    new QRCode(qrCode, {
        text: table.url,
        width: 128,
        height: 128
    });

    // QR 코드 다운로드 버튼 이벤트 추가
    const downloadBtn = qrPreview.querySelector('.download-btn');
    downloadBtn.onclick = () => downloadQR(tableNumber);
}

// QR 코드 다운로드 함수 추가
function downloadQR(tableNumber) {
    const canvas = document.querySelector('#qrCode canvas');
    if (!canvas) return;

    // Canvas를 이미지로 변환
    const image = canvas.toDataURL("image/png");
    
    // 다운로드 링크 생성
    const link = document.createElement('a');
    link.download = `table_${tableNumber}_qr.png`;
    link.href = image;
    link.click();
}

// 페이지 로드 시 테이블 목록 렌더링
document.addEventListener('DOMContentLoaded', function() {
    if (document.querySelector('.table-list')) {
        renderTables();
    }
});

// 주문 관리 함수들
function loadOrders() {
    const orderList = document.querySelector('.order-list');
    const orders = JSON.parse(localStorage.getItem('orders')) || [];
    
    orderList.innerHTML = orders.map(order => `
        <div class="order-card">
            <div class="order-header">
                <span class="table-number">테이블 ${order.tableNumber}번</span>
                <span class="order-time">${new Date(order.orderTime).toLocaleString()}</span>
            </div>
            <div class="order-items">
                ${order.items.map(item => `
                    <div class="order-item">
                        <span>${item.name} x ${item.quantity}</span>
                        <span>${(item.price * item.quantity).toLocaleString()}원</span>
                    </div>
                `).join('')}
            </div>
            <div class="order-status-control">
                <select onchange="updateOrderStatus('${order.id}', this.value)">
                    <option value="pending" ${order.status === 'pending' ? 'selected' : ''}>주문접수</option>
                    <option value="cooking" ${order.status === 'cooking' ? 'selected' : ''}>조리중</option>
                    <option value="completed" ${order.status === 'completed' ? 'selected' : ''}>완료</option>
                </select>
            </div>
        </div>
    `).join('');
}

// 주문 총액 계산 함수
function calculateOrderTotal(items) {
    return items.reduce((total, item) => total + (item.price * item.quantity), 0);
}

// 주문 상태 업데이트
function updateOrderStatus(orderId, newStatus) {
    const orders = JSON.parse(localStorage.getItem('orders')) || [];
    const orderIndex = orders.findIndex(order => order.id.toString() === orderId);
    
    if (orderIndex !== -1) {
        orders[orderIndex].status = newStatus;
        localStorage.setItem('orders', JSON.stringify(orders));
        
        // 상태 변경 알림
        alert(`주문 상태가 ${getStatusText(newStatus)}(으)로 변경되었습니다.`);
    }
}

// 상태 텍스트 변환
function getStatusText(status) {
    const statusMap = {
        'pending': '주문접수',
        'cooking': '조리중',
        'completed': '완료'
    };
    return statusMap[status] || status;
}

// DB에서 가게 정보 불러오기
document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/owner/restaurants/1', {
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => response.json())
        .then(storeData => {
            if (storeData) {
                document.getElementById('storeName').value = storeData.name || '';
                document.getElementById('storeAddress').value = storeData.address || '';
                document.getElementById('storeCategory').value = storeData.category || '';
                document.getElementById('storePhone').value = storeData.phoneNumber || '';
                document.getElementById('openTime').value = storeData.openTime || '09:00';
                document.getElementById('closeTime').value = storeData.closeTime || '22:00';
                document.getElementById('storeNotice').value = storeData.description || '';
                document.getElementById('storeSns').value = storeData.refLink || '';
                
                // 사이드바 가게 이름 업데이트
                const sidebarStoreName = document.querySelector('.admin-profile .store-name');
                if (sidebarStoreName) {
                    sidebarStoreName.textContent = storeData.name || '';
                }
            }
        })
        .catch(error => {
            console.error('가게 정보를 불러오는데 실패했습니다:', error);
        });
});
