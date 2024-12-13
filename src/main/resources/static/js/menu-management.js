// 메뉴 데이터 구조
const menuStore = {
    categories: [],
    menus: []
};

// 초기화
function initializeMenuManagement() {
    loadMenuData();
    initializeDefaultData();
    renderCategories();
    renderMenus();
}

// 메뉴 데이터 로드
function loadMenuData() {
    const savedData = localStorage.getItem('menuData');
    if (savedData) {
        const data = JSON.parse(savedData);
        menuStore.categories = data.categories || [];
        menuStore.menus = data.menus || [];
    }
}

// 메뉴 데이터 저장
function saveMenuData() {
    localStorage.setItem('menuData', JSON.stringify(menuStore));
}

// 이미지 처리 (Base64로 변환)
async function handleImageUpload(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = (e) => resolve(e.target.result);
        reader.onerror = (e) => reject(e);
        reader.readAsDataURL(file);
    });
}

// 메뉴 추가/수정
async function handleMenuSubmit(event) {
    event.preventDefault();
    
    const menuId = document.getElementById('menuForm').dataset.menuId;
    const imageFile = document.getElementById('menuImage').files[0];
    
    const menuData = {
        id: menuId || Date.now().toString(),
        name: document.getElementById('menuName').value,
        category: document.getElementById('menuCategory').value,
        price: parseInt(document.getElementById('menuPrice').value),
        description: document.getElementById('menuDescription').value,
        isPopular: document.getElementById('menuRecommended').checked,
        isSoldOut: document.getElementById('menuSoldOut').checked
    };
    
    if (imageFile) {
        menuData.image = await handleImageUpload(imageFile);
    }
    
    if (menuId) {
        // 수정
        const index = menuStore.menus.findIndex(m => m.id === menuId);
        if (index !== -1) {
            menuStore.menus[index] = { ...menuStore.menus[index], ...menuData };
        }
    } else {
        // 추가
        menuStore.menus.push(menuData);
    }
    
    saveMenuData();
    renderMenus();
    closeMenuModal();
}

// 카테고리 관리 함수들
function renderCategories() {
    const categoryItems = document.querySelector('.category-items');
    const categorySelect = document.getElementById('menuCategory');
    const categoryFilter = document.getElementById('categoryFilter');
    
    if (!categoryItems) return;

    // 카테고리 목록 HTML 생성
    const categoriesHTML = menuStore.categories.map(category => `
        <div class="category-row">
            <span>${category.name}</span>
            <div class="category-actions">
                <button onclick="editCategory('${category.id}')" class="edit-btn">
                    <i class="fas fa-edit"></i>
                </button>
                <button onclick="deleteCategory('${category.id}')" class="delete-btn">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>
    `).join('');
    
    categoryItems.innerHTML = categoriesHTML;

    // 카테고리 선택 옵션 업데이트
    const optionsHTML = menuStore.categories.map(category => 
        `<option value="${category.id}">${category.name}</option>`
    ).join('');
    
    if (categorySelect) {
        categorySelect.innerHTML = optionsHTML;
    }

    if (categoryFilter) {
        categoryFilter.innerHTML = `
            <option value="all">전체 카테고리</option>
            ${optionsHTML}
        `;
    }
}

// 메뉴 필터링 함수
function filterMenus() {
    const categoryFilter = document.getElementById('categoryFilter');
    const searchInput = document.getElementById('menuSearch');
    
    if (!categoryFilter) return;
    
    const category = categoryFilter.value;
    const searchQuery = searchInput ? searchInput.value : '';
    
    renderMenus(category, searchQuery);
}

// 메뉴 렌더링 함수 수정
function renderMenus(category = 'all', searchQuery = '') {
    const menuItems = document.querySelector('.menu-items');
    if (!menuItems) return;

    let filteredMenus = menuStore.menus;

    // 카테고리 필터링
    if (category !== 'all') {
        filteredMenus = filteredMenus.filter(menu => menu.category === category);
    }

    // 검색어 필터링
    if (searchQuery) {
        const query = searchQuery.toLowerCase();
        filteredMenus = filteredMenus.filter(menu => 
            menu.name.toLowerCase().includes(query) ||
            menu.description.toLowerCase().includes(query)
        );
    }

    // 메뉴 목록 렌더링
    menuItems.innerHTML = filteredMenus.length ? 
        filteredMenus.map(menu => `
            <div class="menu-item">
                <div class="menu-info">
                    <h4>${menu.name}</h4>
                    <p>${menu.price.toLocaleString()}원</p>
                </div>
                <div class="menu-actions">
                    <button onclick="editMenu('${menu.id}')" class="edit-btn">
                        <i class="fas fa-edit"></i> 수정
                    </button>
                    <button onclick="deleteMenu('${menu.id}')" class="delete-btn">
                        <i class="fas fa-trash"></i> 삭제
                    </button>
                    <button onclick="toggleSoldOut('${menu.id}')" class="soldout-btn">
                        ${menu.isSoldOut ? '판매중' : '품절'}
                    </button>
                </div>
            </div>
        `).join('') :
        '<p class="no-results">검색 결과가 없습니다.</p>';
}

// 카테고리 추가/수정 모달
function openAddCategoryModal(categoryId = null) {
    const category = categoryId ? 
        menuStore.categories.find(c => c.id === categoryId) : null;
    
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.id = 'categoryModal';
    modal.innerHTML = `
        <div class="modal-content">
            <h3>${category ? '카테고리 수정' : '새 카테고리 추가'}</h3>
            <form id="categoryForm">
                <div class="form-group">
                    <label>카테고리명</label>
                    <input type="text" id="categoryName" value="${category?.name || ''}" required>
                </div>
                <div class="modal-buttons">
                    <button type="submit" class="primary-button">저장</button>
                    <button type="button" onclick="closeCategoryModal()" class="secondary-button">취소</button>
                </div>
            </form>
        </div>
    `;

    // 기존 모달이 있면 제거
    const existingModal = document.getElementById('categoryModal');
    if (existingModal) {
        existingModal.remove();
    }

    document.body.appendChild(modal);
    modal.style.display = 'block';
    
    // 폼 제출 이벤트
    document.getElementById('categoryForm').addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('categoryName').value;
        
        if (category) {
            // 카테고리 수정
            const index = menuStore.categories.findIndex(c => c.id === categoryId);
            if (index !== -1) {
                menuStore.categories[index].name = name;
            }
        } else {
            // 새 카테고리 추가
            menuStore.categories.push({
                id: Date.now().toString(),
                name: name
            });
        }
        
        saveMenuData();
        renderCategories();
        closeCategoryModal();
    });
}

// 카테고리 모달 닫기
function closeCategoryModal() {
    const modal = document.getElementById('categoryModal');
    if (modal) {
        modal.remove();
    }
}

// 메뉴 추가 모달 열기
function openAddMenuModal(menuId = null) {
    const menu = menuId ? menuStore.menus.find(m => m.id === menuId) : null;
    const menuForm = document.getElementById('menuForm');
    
    // 폼 초기화
    menuForm.reset();
    menuForm.dataset.menuId = menuId || '';
    
    // 기존 메뉴 데이터 설정
    if (menu) {
        document.getElementById('menuName').value = menu.name;
        document.getElementById('menuCategory').value = menu.category;
        document.getElementById('menuPrice').value = menu.price;
        document.getElementById('menuDescription').value = menu.description;
        document.getElementById('menuRecommended').checked = menu.isPopular;
        document.getElementById('menuSoldOut').checked = menu.isSoldOut;
        
        // 이미지 미리보기
        if (menu.image) {
            const preview = document.getElementById('imagePreview');
            preview.innerHTML = `<img src="${menu.image}" alt="메뉴 이미지">`;
        }
    }

    // 모달 표시
    const menuModal = document.getElementById('menuModal');
    menuModal.style.display = 'block';
}

// 메뉴 모달 닫기
function closeMenuModal() {
    const menuModal = document.getElementById('menuModal');
    menuModal.style.display = 'none';
}

// 이미�� 미리보기
document.getElementById('menuImage')?.addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('imagePreview');
            preview.innerHTML = `<img src="${e.target.result}" alt="메뉴 이미지 미리보기">`;
        }
        reader.readAsDataURL(file);
    }
});

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('menu-management')) {
        initializeMenuManagement();
        
        // 이벤트 리스너 등록
        const menuSearch = document.getElementById('menuSearch');
        const categoryFilter = document.getElementById('categoryFilter');
        const menuImage = document.getElementById('menuImage');
        const menuForm = document.getElementById('menuForm');
        
        // 새 카테고리 추가 버튼 이벤트 리스너
        const addCategoryBtn = document.querySelector('.add-category-btn');
        if (addCategoryBtn) {
            addCategoryBtn.addEventListener('click', () => openAddCategoryModal());
        }
        
        // 기존 이벤트 리스너들...
        if (menuSearch) {
            menuSearch.addEventListener('input', filterMenus);
        }
        
        if (categoryFilter) {
            categoryFilter.addEventListener('change', filterMenus);
        }
        
        if (menuImage) {
            menuImage.addEventListener('change', handleImagePreview);
        }
        
        if (menuForm) {
            menuForm.addEventListener('submit', handleMenuSubmit);
        }
    }
});

// 품절 상태 토글
function toggleSoldOut(menuId) {
    const index = menuStore.menus.findIndex(m => m.id === menuId);
    if (index !== -1) {
        menuStore.menus[index].isSoldOut = !menuStore.menus[index].isSoldOut;
        saveMenuData();
        renderMenus();
    }
}

// 메뉴 수정
function editMenu(menuId) {
    openAddMenuModal(menuId); // 기존 모달을 수정 모드로 열기
}

// 메뉴 삭제
function deleteMenu(menuId) {
    if (confirm('정말 이 메뉴를 삭제하시겠습니까?')) {
        const index = menuStore.menus.findIndex(m => m.id === menuId);
        if (index !== -1) {
            menuStore.menus.splice(index, 1);
            saveMenuData();
            renderMenus();
        }
    }
}

// 카테고리 수정
function editCategory(categoryId) {
    openAddCategoryModal(categoryId);
}

// 카테고리 삭제
function deleteCategory(categoryId) {
    if (confirm('이 카테고리를 삭제��시겠습니까?\n(카테고리에 속한 메뉴들은 삭제되지 않습니다)')) {
        const index = menuStore.categories.findIndex(c => c.id === categoryId);
        if (index !== -1) {
            menuStore.categories.splice(index, 1);
            saveMenuData();
            renderCategories();
            renderMenus(); // 카테고리 목록이 변경되었으므로 메뉴도 다시 렌더링
        }
    }
}

// 초기 데이터 설정 (처음 실행시 기본 카테고리 생성)
function initializeDefaultData() {
    if (menuStore.categories.length === 0) {
        menuStore.categories = [
            { id: 'coffee', name: '커피' },
            { id: 'beverages', name: '음료' },
            { id: 'desserts', name: '디저트' },
            { id: 'sandwiches', name: '샌드위치' }
        ];
        saveMenuData();
    }
}

// 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', function() {
    const categoryFilter = document.getElementById('categoryFilter');
    const searchInput = document.getElementById('menuSearch');
    
    if (categoryFilter) {
        categoryFilter.addEventListener('change', filterMenus);
    }
    
    if (searchInput) {
        searchInput.addEventListener('input', filterMenus);
    }
});

// 나머지 필요한 함수들 (렌더링, 필터링, ��색 등)... 

// 이미지 미리보기 핸들러
function handleImagePreview(e) {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('imagePreview');
            if (preview) {
                preview.innerHTML = `<img src="${e.target.result}" alt="메뉴 이미지 미리보기">`;
            }
        }
        reader.readAsDataURL(file);
    }
}

// 메뉴 데이터 구조 통일
function saveMenu(formData) {
    const newMenu = {
        id: formData.id || Date.now().toString(), // ID를 문자열로 통일
        name: formData.name,
        category: formData.category,
        price: parseInt(formData.price),
        description: formData.description,
        image: formData.image || 'images/default-menu.jpg', // 기본 이미지 경로 설정
        isPopular: formData.isPopular || false,
        isSoldOut: formData.isSoldOut || false
    };

    let storedMenuData = JSON.parse(localStorage.getItem('menuData')) || { menus: [] };
    
    // 기존 메뉴 수정 또는 새 메뉴 추가
    const existingIndex = storedMenuData.menus.findIndex(m => m.id === newMenu.id);
    if (existingIndex !== -1) {
        storedMenuData.menus[existingIndex] = newMenu;
    } else {
        storedMenuData.menus.push(newMenu);
    }

    localStorage.setItem('menuData', JSON.stringify(storedMenuData));
    return storedMenuData;
}