// 메뉴 관리를 위한 전역 상태
const menuStore = {
  selectedRestaurantId: null,
  menus: [],
};

// 메뉴 모달 관련 함수들
function openAddMenuModal() {
  const modal = document.getElementById('menuModal');
  const modalTitle = modal.querySelector('.modal-title');
  modalTitle.textContent = '메뉴 추가';
  
  // 폼 초기화
  document.getElementById('menuForm').reset();
  document.getElementById('menuId').value = '';
  document.getElementById('menuCategory').value = '';
  document.getElementById('previewContainer').innerHTML = '';
  
  modal.style.display = 'block';
}

function openEditMenuModal(menu) {
  const modal = document.getElementById('menuModal');
  const modalTitle = modal.querySelector('.modal-title');
  modalTitle.textContent = '메뉴 수정';

  // 폼 데이터 설정
  document.getElementById('menuId').value = menu.id;
  document.getElementById('menuName').value = menu.name;
  document.getElementById('menuCategory').value = menu.menuCategory || '';
  document.getElementById('price').value = menu.price;
  document.getElementById('description').value = menu.description || '';

  // 이미지 미리보기 설정
  const previewContainer = document.getElementById('previewContainer');
  if (menu.menuImageDTOList && menu.menuImageDTOList.length > 0) {
    const imageUrl = `/display?fileName=${menu.menuImageDTOList[0].imageUrl}`;
    previewContainer.innerHTML = `
      <div class="image-preview">
        <img src="${imageUrl}" alt="${menu.name}">
      </div>`;
  } else {
    previewContainer.innerHTML = '';
  }

  modal.style.display = 'block';
}

function closeMenuModal() {
  const modal = document.getElementById('menuModal');
  modal.style.display = 'none';
}

// 이미지 미리보기 처리
function handleImagePreview(event) {
  const file = event.target.files[0];
  const previewContainer = document.getElementById('previewContainer');
  
  if (file) {
    const reader = new FileReader();
    reader.onload = function(e) {
      previewContainer.innerHTML = `
        <div class="image-preview">
          <img src="${e.target.result}" alt="미리보기">
        </div>`;
    };
    reader.readAsDataURL(file);
  } else {
    previewContainer.innerHTML = '';
  }
}

// 메뉴 데이터 로드
async function loadMenuData() {
  if (!menuStore.selectedRestaurantId) return;

  try {
    const response = await fetch(`/api/restaurants/${menuStore.selectedRestaurantId}/menus`);
    if (!response.ok) {
      throw new Error('메뉴 데이터를 불러오는데 실패했습니다.');
    }

    const data = await response.json();
    menuStore.menus = data;
    renderMenus(data);
  } catch (error) {
    console.error('Error loading menus:', error);
  }
}

// 메뉴 저장 처리
async function handleMenuSubmit(event) {
  event.preventDefault();

  const menuId = document.getElementById('menuId').value;
  const menuName = document.getElementById('menuName').value;
  const menuCategory = document.getElementById('menuCategory').value;
  const price = document.getElementById('price').value;
  const description = document.getElementById('description').value;
  const imageFile = document.getElementById('menuImage').files[0];

  if (!menuName || !price) {
    alert('메뉴 이름과 가격은 필수 입력 항목입니다.');
    return;
  }

  try {
    const formData = new FormData();
    formData.append('id', menuId || '');
    formData.append('name', menuName);
    formData.append('menuCategory', menuCategory || '기타');
    formData.append('price', price);
    formData.append('description', description || '');
    formData.append('restaurantId', menuStore.selectedRestaurantId);

    if (imageFile) {
      formData.append('file', imageFile);
    }

    const response = await fetch(
      `/api/restaurants/${menuStore.selectedRestaurantId}/menus${menuId ? `/${menuId}` : ''}`,
      {
        method: menuId ? 'PUT' : 'POST',
        body: formData
      }
    );

    if (!response.ok) {
      throw new Error('메뉴 저장에 실패했습니다.');
    }

    await loadMenuData();
    closeMenuModal();
  } catch (error) {
    console.error('Error saving menu:', error);
    alert(error.message);
  }
}

// 메뉴 삭제
async function deleteMenu(menuId) {
  if (!confirm('정말 이 메뉴를 삭제하시겠습니까?')) {
    return;
  }

  try {
    const response = await fetch(
      `/api/restaurants/${menuStore.selectedRestaurantId}/menus/${menuId}`,
      {
        method: 'DELETE',
      }
    );

    if (!response.ok) {
      throw new Error('메뉴 삭제에 실패했습니다.');
    }

    loadMenuData();
  } catch (error) {
    console.error('Error deleting menu:', error);
    alert(error.message);
  }
}

// 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', function() {
  // 메뉴 추가 버튼 클릭 핸들러
  const addMenuButton = document.getElementById('addMenuButton');
  if (addMenuButton) {
    addMenuButton.addEventListener('click', function() {
      if (!menuStore.selectedRestaurantId) {
        alert('먼저 레스토랑을 선택해주세요.');
        return;
      }
      openAddMenuModal();
    });
  }

  // 레스토랑 선택 핸들러
  const restaurantSelect = document.getElementById('restaurantSelect');
  if (restaurantSelect) {
    restaurantSelect.addEventListener('change', function(event) {
      menuStore.selectedRestaurantId = event.target.value;
      if (menuStore.selectedRestaurantId) {
        loadMenuData();
      }
    });
  }

  // 모달 닫기 버튼 이벤트
  const closeButtons = document.querySelectorAll('.close');
  closeButtons.forEach(button => {
    button.addEventListener('click', closeMenuModal);
  });

  // 이미지 미리보기 이벤트
  const menuImage = document.getElementById('menuImage');
  if (menuImage) {
    menuImage.addEventListener('change', handleImagePreview);
  }

  // 메뉴 폼 제출 이벤트
  const menuForm = document.getElementById('menuForm');
  if (menuForm) {
    menuForm.addEventListener('submit', handleMenuSubmit);
  }

  // 초기 데이터 로드
  const initialRestaurantId = restaurantSelect?.value;
  if (initialRestaurantId) {
    menuStore.selectedRestaurantId = initialRestaurantId;
    loadMenuData();
  }
});

// 메뉴 렌더링
function renderMenus(menuList = menuStore.menus) {
  const menuListContainer = document.getElementById('menuList');
  if (!menuListContainer) return;

  if (!menuList || menuList.length === 0) {
    menuListContainer.innerHTML = '<div class="alert">등록된 메뉴가 없습니다.</div>';
    return;
  }

  // 카테고리별로 메뉴 그룹화
  const menusByCategory = menuList.reduce((acc, menu) => {
    const category = menu.menuCategory || '기타';
    if (!acc[category]) {
      acc[category] = [];
    }
    acc[category].push(menu);
    return acc;
  }, {});

  // 카테고리별로 메뉴 렌더링
  const menuHtml = Object.entries(menusByCategory).map(([category, menus]) => `
    <div class="menu-category">
      <h2>${category}</h2>
      <div class="menu-grid">
        ${menus.map(menu => `
          <div class="menu-item">
            <div class="menu-content">
              <div class="menu-image">
                ${menu.menuImageDTOList && menu.menuImageDTOList.length > 0 
                  ? `<img src="/display?fileName=${menu.menuImageDTOList[0].imageUrl}" alt="${menu.name}">`
                  : '<div class="no-image">이미지 없음</div>'}
              </div>
              <div class="menu-details">
                <h3>${menu.name}</h3>
                <p class="price">${menu.price ? menu.price.toLocaleString() : 0}원</p>
                ${menu.description ? `<p class="description">${menu.description}</p>` : ''}
              </div>
              <div class="menu-actions">
                <button onclick="editMenu(${menu.id})" class="edit-btn">수정</button>
                <button onclick="deleteMenu(${menu.id})" class="delete-btn">삭제</button>
              </div>
            </div>
          </div>
        `).join('')}
      </div>
    </div>
  `).join('');

  menuListContainer.innerHTML = menuHtml;
}

// 메뉴 수정
function editMenu(menuId) {
  const menu = menuStore.menus.find(menu => menu.id === menuId);
  if (menu) {
    openEditMenuModal(menu);
  }
}
