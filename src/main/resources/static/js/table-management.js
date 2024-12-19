let currentRestaurantId = null;

document.addEventListener("DOMContentLoaded", function () {
  initializeTableManagement();
});

function initializeTableManagement() {
  const tableRestaurantSelect = document.getElementById(
    "tableRestaurantSelect"
  );
  const addTableBtn = document.getElementById("addTableBtn");

  if (tableRestaurantSelect) {
    // 레스토랑 선택 이벤트
    tableRestaurantSelect.addEventListener("change", async function () {
      currentRestaurantId = parseInt(this.value, 10); // 문자열을 숫자로 변환
      addTableBtn.disabled = !currentRestaurantId;

      if (currentRestaurantId) {
        try {
          await loadTables(currentRestaurantId);
        } catch (error) {
          console.error("테이블 로드 실패:", error);
          clearTables();
        }
      } else {
        clearTables();
      }
    });
  }
}

async function loadTables(restaurantId) {
  try {
    const response = await fetch(`/api/tables/restaurant/${restaurantId}`);
    if (!response.ok) {
      throw new Error("테이블 목록을 불러오는데 실패했습니다.");
    }

    const tables = await response.json();
    displayTables(tables);
  } catch (error) {
    console.error("Error:", error);
    clearTables();
  }
}

function displayTables(tables) {
  const tableList = document.querySelector(".table-list");
  if (!tableList) return;

  if (!tables || tables.length === 0) {
    tableList.innerHTML =
      '<p class="text-center">등록된 테이블이 없습니다.</p>';
    return;
  }

  tableList.innerHTML = tables
    .map(
      (table) => `
            <div class="table-item">
                <h3>테이블 ${table.tableNumber}</h3>
                <p class="restaurant-name">${table.restaurantName}</p>
                <div class="table-actions">
                    <button onclick="generateQR(${table.id})" class="btn btn-primary">QR 생성</button>
                    <button onclick="deleteTable(${table.id})" class="btn btn-danger">삭제</button>
                </div>
            </div>
        `
    )
    .join("");
}

async function addTable() {
  if (!currentRestaurantId) {
    alert("레스토랑을 선택해주세요.");
    return;
  }

  try {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch("/api/tables", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        [header]: token,
      },
      body: JSON.stringify({ restaurantId: currentRestaurantId }),
    });

    const data = await response.json();
    
    if (!response.ok) {
      throw new Error(data.message || "테이블 추가에 실패했습니다.");
    }

    await loadTables(currentRestaurantId);
  } catch (error) {
    console.error("Error:", error);
    alert(error.message);
  }
}

async function generateQR(tableId) {
  try {
    const response = await fetch(`/api/tables/${tableId}/qr`);
    if (!response.ok) throw new Error("QR 코드 생성에 실패했습니다.");

    const qrData = await response.json();

    // QR 코드 생성
    const qrDiv = document.getElementById("qrCode");
    qrDiv.innerHTML = "";

    new QRCode(qrDiv, {
      text: qrData.url,
      width: 128,
      height: 128,
    });

    // QR 정보 업데이트
    document.querySelector(
      ".table-number"
    ).textContent = `테이블 ${qrData.tableNumber}`;
    document.querySelector(".restaurant-name").textContent =
      qrData.restaurantName;
  } catch (error) {
    console.error("Error:", error);
    alert(error.message);
  }
}

async function downloadQR() {
  const qrCanvas = document.querySelector("#qrCode canvas");
  if (!qrCanvas) return;

  const link = document.createElement("a");
  link.download = "table-qr.png";
  link.href = qrCanvas.toDataURL();
  link.click();
}

async function deleteTable(tableId) {
  if (!confirm("정말로 이 테이블을 삭제하시겠습니까?")) return;

  try {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch(`/api/tables/${tableId}`, {
      method: "DELETE",
      headers: {
        [header]: token,
      },
    });

    if (!response.ok) throw new Error("테이블 삭제에 실패했습니다.");

    await loadTables(currentRestaurantId);
  } catch (error) {
    console.error("Error:", error);
    alert(error.message);
  }
}

function clearTables() {
  const tableList = document.querySelector(".table-list");
  if (tableList) {
    tableList.innerHTML = '<p class="text-center">레스토랑을 선택해주세요.</p>';
  }
}
