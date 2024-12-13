import { CartService } from "./store/cart-service.js";

document.addEventListener("DOMContentLoaded", function () {
  const urlParams = new URLSearchParams(window.location.search);
  const tableNo = urlParams.get("table");
  const id = urlParams.get("id");

  if (!tableNo) {
    alert("테이블 정보가 없습니다.");
    window.location.href = "index.html";
    return;
  }

  // 장바구니 데이터 가져오기 및 표시
  function updateCart() {
    console.log("Updating cart for table:", tableNo); // 디버깅용
    const cartItems = CartService.getCartItems(tableNo);
    console.log("Cart items:", cartItems); // 디버깅용

    const cartContainer = document.querySelector(".cart-items");
    const totalPriceElement = document.getElementById("totalPrice");

    if (!cartItems || cartItems.length === 0) {
      cartContainer.innerHTML =
        '<div class="empty-cart-message">장바구니가 비어있습니다.</div>';
      totalPriceElement.textContent = "0";
      return;
    }

    // 장바구니 아이템 표시
    cartContainer.innerHTML = "";
    let total = 0;

    cartItems.forEach((item) => {
      const itemElement = document.createElement("div");
      itemElement.className = "cart-item";
      itemElement.innerHTML = `
        <span>${item.name} × ${item.quantity}</span>
        <span>${(item.price * item.quantity).toLocaleString()}원</span>
      `;
      cartContainer.appendChild(itemElement);
      total += item.price * item.quantity;
    });

    totalPriceElement.textContent = total.toLocaleString();
  }

  // 초기 장바구니 표시
  updateCart();

  // 결제하기 버튼 이벤트
  const checkoutButton = document.getElementById("checkoutButton");
  if (checkoutButton) {
    checkoutButton.addEventListener("click", function () {
      const cartItems = CartService.getCartItems(tableNo);
      console.log("Checkout cart items:", cartItems); // 디버깅용

      if (!cartItems || cartItems.length === 0) {
        alert("장바구니가 비어있습니다.");
        return;
      }

      // 체크아웃 페이지로 이동
      window.location.href = `checkout.html?table=${tableNo}&id=${id}`;
    });
  }
});
