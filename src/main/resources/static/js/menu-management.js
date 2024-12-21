// 메뉴 데이터 구조
const menuStore = {
  selectedRestaurantId: null,
  menus: [],
};

// 초기화
function initializeMenuManagement() {
  const restaurantSelect = document.getElementById("restaurantSelect");
  const selectedValue = restaurantSelect ? restaurantSelect.value : null;

  if (selectedValue !== menuStore.selectedRestaurantId) {
    menuStore.selectedRestaurantId = selectedValue;

    // 메뉴 추가 버튼 활성화/비활성화
    const addMenuBtn = document.getElementById("addMenuBtn");
    if (addMenuBtn) {
      addMenuBtn.disabled = !menuStore.selectedRestaurantId;
    }

    // 메뉴 관리 컨텐츠 영역 표시/숨김
    const menuContent = document.querySelector(".menu-content");
    if (menuContent) {
      menuContent.style.display = menuStore.selectedRestaurantId
        ? "block"
        : "none";
    }

    if (menuStore.selectedRestaurantId) {
      loadMenuData();
    }
  }
}

// 메뉴 데이터 로드
async function loadMenuData() {
  try {
    if (!menuStore.selectedRestaurantId) {
      console.error("선택된 레스토랑이 없습니다.");
      return;
    }

    const response = await fetch(
      `/api/restaurants/${menuStore.selectedRestaurantId}/menus`,
      {
        headers: getCommonHeaders(),
      }
    );
    if (!response.ok) {
      throw new Error("메뉴 데이터를 불러오는데 실패했습니다.");
    }
    const data = await response.json();
    menuStore.menus = data.dtoList || [];

    // 메뉴 목록 렌더링
    renderMenus();

    // 카테고리 필터 업데이트
    updateCategoryFilter();
  } catch (error) {
    console.error("메뉴 데이터 로드 중 오류:", error);
  }
}

// 카테고리(dishes) 필터 업데이트
function updateCategoryFilter() {
  const categoryFilter = document.getElementById("categoryFilter");
  if (!categoryFilter) return;

  // 중복 제거된 dishes 목록 가져오기
  const categories = [...new Set(menuStore.menus.map((menu) => menu.dishes))]
    .filter(Boolean)
    .sort();

  categoryFilter.innerHTML = `
        <option value="all">전체 카테고리</option>
        ${categories
          .map(
            (category) => `
            <option value="${category}">${category}</option>
        `
          )
          .join("")}
    `;
}

// 메뉴 필터링
function filterMenus() {
  const categoryFilter = document.getElementById("categoryFilter");
  const searchInput = document.getElementById("menuSearch");

  const selectedCategory = categoryFilter ? categoryFilter.value : "all";
  const searchQuery = searchInput ? searchInput.value.toLowerCase() : "";

  const filteredMenus = menuStore.menus.filter((menu) => {
    const matchesCategory =
      selectedCategory === "all" || menu.dishes === selectedCategory;
    const matchesSearch = menu.name.toLowerCase().includes(searchQuery);
    return matchesCategory && matchesSearch;
  });

  renderMenus(filteredMenus);
}

// 메뉴 렌더링
function renderMenus(menuList = menuStore.menus) {
  const menuContainer = document.querySelector(".menu-items");
  if (!menuContainer) return;

  if (menuList.length === 0) {
    menuContainer.innerHTML =
      '<p class="no-results">등록된 메뉴가 없습니다.</p>';
    return;
  }

  const menuHTML = menuList
    .map(
      (menu) => `
        <div class="menu-item">
            <div class="menu-info">
                <h4>${menu.name}</h4>
                <p class="category">${menu.dishes || "카테고리 없음"}</p>
                <p class="price">${menu.price}원</p>
                <p class="description">${menu.description}</p>
            </div>
            <div class="menu-actions">
                <button onclick="editMenu(${menu.id})" class="edit-btn">
                    수정
                </button>
                <button onclick="deleteMenu(${menu.id})" class="delete-btn">
                    삭제
                </button>
            </div>
        </div>
    `
    )
    .join("");

  menuContainer.innerHTML = menuHTML;
}

// 이미지 처리 (Base64로 변환)
async function handleImageUpload(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = function (e) {
      // 파일 이름에서 특수 문자 제거
      const cleanFileName = file.name.replace(/[^a-zA-Z0-9.-]/g, "_");
      const fileName = `${Date.now()}_${cleanFileName}`;
      resolve(fileName);
    };
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}

// 이미지 미리보기 처리
function handleImagePreview(event) {
  const file = event.target.files[0];
  if (!file) {
    return;
  }

  // 이미지 파일 검증
  if (!file.type.startsWith("image/")) {
    alert("이미지 파일만 업로드 가능합니다.");
    event.target.value = "";
    return;
  }

  const reader = new FileReader();
  reader.onload = function (e) {
    const previewContainer = document.getElementById("imagePreviewContainer");
    previewContainer.innerHTML = `<img src="${e.target.result}" alt="메뉴 이미지" style="max-width: 200px;">`;
    previewContainer.style.display = "block";
  };
  reader.readAsDataURL(file);
}

// 메뉴 추가/수정
async function handleMenuSubmit(event) {
  event.preventDefault();

  try {
    const menuId = document.getElementById("menuId").value;
    const menuName = document.getElementById("menuName").value;
    const price = document.getElementById("price")?.value;
    const menuCategory = document.getElementById("menuCategory")?.value;
    const description = document.getElementById("description")?.value;
    const imageInput = document.getElementById("menuImage");

    const formData = new FormData();
    formData.append("id", menuId || "");
    formData.append("name", menuName);
    formData.append("category", menuCategory);
    formData.append("price", price);
    formData.append("description", description || "");
    formData.append("restaurantId", menuStore.selectedRestaurantId);

    if (imageInput.files.length > 0) {
      formData.append("image", imageInput.files[0]);
    }

    const url = menuId
      ? `/api/restaurants/${menuStore.selectedRestaurantId}/menus/${menuId}`
      : `/api/restaurants/${menuStore.selectedRestaurantId}/menus`;

    const response = await fetch(url, {
      method: menuId ? "PUT" : "POST",
      headers: {
        ...getCsrfHeader(),
      },
      body: formData,
    });

    await loadMenuData();
    closeMenuModal();
    alert(menuId ? "메뉴가 수정되었습니다." : "새 메뉴가 추가되었습니다.");
  } catch (error) {
    console.error("메뉴 저장 중 오류:", error);
    alert("메뉴 저장에 실패했습니다.");
  }
}

// CSRF 토큰 가져오기
function getCsrfToken() {
  return document.querySelector('meta[name="_csrf"]')?.content;
}

function getCsrfHeader() {
  return document.querySelector('meta[name="_csrf_header"]')?.content;
}

// API 요청 시 공통으로 사용할 헤더
function getCommonHeaders() {
  const headers = {};

  const csrfToken = getCsrfToken();
  const csrfHeader = getCsrfHeader();

  if (csrfToken && csrfHeader) {
    headers[csrfHeader] = csrfToken;
  }

  return headers;
}

// 메뉴 추가 모달 열기
async function openAddMenuModal(menuId = null) {
  const modal = document.getElementById("menuModal");
  const modalTitle = document.getElementById("menuModalTitle");
  const form = document.getElementById("menuForm");
  const previewContainer = document.getElementById("imagePreviewContainer");

  modalTitle.textContent = menuId ? "메뉴 수정" : "새 메뉴 추가";
  form.reset();
  previewContainer.innerHTML = ""; // 이미지 미리보기 초기화

  if (menuId) {
    const menu = menuStore.menus.find((m) => m.id === menuId);
    if (menu) {
      document.getElementById("menuId").value = menu.id;
      document.getElementById("menuName").value = menu.name;
      document.getElementById("menuCategory").value = menu.category || "";
      document.getElementById("price").value = menu.price;
      document.getElementById("description").value = menu.description || "";

      // 기존 이미지가 있는 경우 표시
      if (menu.imageUrl) {
        previewContainer.innerHTML = `
          <img src="${menu.imageUrl}" alt="메뉴 이미지" style="max-width: 200px;">
        `;
        previewContainer.style.display = "block";
      }
    }
  } else {
    document.getElementById("menuId").value = "";
    previewContainer.style.display = "none";
  }

  modal.style.display = "block";
}

// 메뉴 모달 닫기
function closeMenuModal() {
  const menuModal = document.getElementById("menuModal");
  menuModal.style.display = "none";
}

// 이미지 미리보기
document
  .getElementById("menuImage")
  ?.addEventListener("change", handleImagePreview);

// 레스토랑 목록 로드
async function loadRestaurants() {
  try {
    const response = await fetch("/api/restaurants", {
      headers: getCommonHeaders(),
    });
    if (!response.ok) {
      throw new Error("레스토랑 목록을 불러오는데 실패했습니다.");
    }
    const restaurants = await response.json();

    const restaurantSelect = document.getElementById("restaurantSelect");
    if (!restaurantSelect) {
      console.error("레스토랑 선택 엘리먼트를 찾을 수 없습니다.");
      return;
    }

    restaurantSelect.innerHTML =
      '<option value="">레스토랑을 선택하세요</option>';

    if (restaurants && restaurants.length > 0) {
      restaurants.forEach((restaurant) => {
        const option = document.createElement("option");
        option.value = restaurant.id;
        option.textContent = restaurant.name;
        restaurantSelect.appendChild(option);
      });
    } else {
      console.log("등록된 레스토랑이 없습니다.");
      const option = document.createElement("option");
      option.value = "";
      option.textContent = "등록된 레스토랑이 없습니다";
      option.disabled = true;
      restaurantSelect.appendChild(option);
    }
  } catch (error) {
    console.error("레스토랑 목록을 불러오는데 실패했습니다:", error);
    const restaurantSelect = document.getElementById("restaurantSelect");
    if (restaurantSelect) {
      restaurantSelect.innerHTML =
        '<option value="">레스토랑 목록을 불러오는데 실패했습니다</option>';
    }
  }
}

// 레스토랑 선택 이벤트 핸들러
function handleRestaurantSelect(event) {
  initializeMenuManagement();
}

// 페이지 로드 시 초기화
document.addEventListener("DOMContentLoaded", function () {
  loadRestaurants(); // 레스토랑 목록 로드

  // 레스토랑 선택 이벤트 리스너 등록
  const restaurantSelect = document.getElementById("restaurantSelect");
  if (restaurantSelect) {
    restaurantSelect.addEventListener("change", handleRestaurantSelect);
  }

  if (document.getElementById("menu-management")) {
    initializeMenuManagement();

    // 이벤트 리스너 등록
    const menuSearch = document.getElementById("menuSearch");
    const categoryFilter = document.getElementById("categoryFilter");
    const menuForm = document.getElementById("menuForm");

    // 기존 이벤트 리스너들...
    if (menuSearch) {
      menuSearch.addEventListener("input", filterMenus);
    }

    if (categoryFilter) {
      categoryFilter.addEventListener("change", filterMenus);
    }

    if (menuForm) {
      menuForm.addEventListener("submit", handleMenuSubmit);
    }
  }
});

// 품절 상태 토글
function toggleSoldOut(menuId) {
  const index = menuStore.menus.findIndex((m) => m.id === menuId);
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
async function deleteMenu(menuId) {
  if (!confirm("정말 이 메뉴를 삭제하시겠습니까?")) {
    return;
  }

  try {
    const response = await fetch(
      `/api/restaurants/${menuStore.selectedRestaurantId}/menus/${menuId}`,
      {
        method: "DELETE",
        headers: getCommonHeaders(),
      }
    );

    if (!response.ok) {
      throw new Error("메뉴 삭제에 실패했습니다.");
    }

    // 성공적으로 삭제된 경우 UI 업데이���
    const index = menuStore.menus.findIndex((m) => m.id === menuId);
    if (index !== -1) {
      menuStore.menus.splice(index, 1);
      renderMenus();
    }
  } catch (error) {
    console.error("메뉴 삭제 중 오류:", error);
    alert("메뉴 삭제에 실패했습니다.");
  }
}

// 레스토랑 선택기 초기화
async function initializeRestaurantSelect() {
  try {
    const response = await fetch("/api/owner/restaurants");
    if (!response.ok) throw new Error("Failed to fetch restaurants");

    const restaurants = await response.json();
    const select = document.getElementById("restaurantSelect");
    select.innerHTML = '<option value="">레스토랑을 선택하세요</option>';

    restaurants.forEach((restaurant) => {
      const option = document.createElement("option");
      option.value = restaurant.id;
      option.textContent = restaurant.name;
      select.appendChild(option);
    });
  } catch (error) {
    console.error("Error:", error);
  }
}

// 페이지 로드 시 초기화
document.addEventListener("DOMContentLoaded", function () {
  initializeRestaurantSelect();

  // 레스토랑 선택 이벤트
  const restaurantSelect = document.getElementById("restaurantSelect");
  if (restaurantSelect) {
    restaurantSelect.addEventListener("change", function () {
      const selectedId = this.value;
      const addMenuBtn = document.getElementById("addMenuBtn");
      const menuContent = document.querySelector(".menu-content");

      if (selectedId) {
        addMenuBtn.disabled = false;
        menuContent.style.display = "block";
        loadMenus(selectedId);
      } else {
        addMenuBtn.disabled = true;
        menuContent.style.display = "none";
      }
    });
  }
});

// 메뉴 목록 로드
async function loadMenus(restaurantId) {
  try {
    const response = await fetch(`/api/restaurants/${restaurantId}/menus`);
    if (!response.ok) throw new Error("Failed to fetch menus");

    const menus = await response.json();
    displayMenus(menus);
  } catch (error) {
    console.error("Error:", error);
  }
}

// 메뉴 표시
function displayMenus(menus) {
  const menuItems = document.querySelector(".menu-items");
  menuItems.innerHTML = menus
    .map(
      (menu) => `
        <div class="menu-item card mb-3">
            <div class="card-body">
                <h5 class="card-title">${menu.name}</h5>
                <p class="card-text">${menu.description || ""}</p>
                <p class="card-text"><strong>${menu.price}원</strong></p>
                <button class="btn btn-sm btn-primary" onclick="editMenu(${
                  menu.id
                })">수정</button>
                <button class="btn btn-sm btn-danger" onclick="deleteMenu(${
                  menu.id
                })">삭제</button>
            </div>
        </div>
    `
    )
    .join("");
}
