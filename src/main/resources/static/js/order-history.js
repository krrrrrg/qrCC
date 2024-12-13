document.addEventListener('DOMContentLoaded', function() {
    const orders = JSON.parse(localStorage.getItem('orders')) || [];
    const orderHistoryList = document.getElementById('orderHistoryList');

    orders.reverse().forEach(order => {
        const orderElement = document.createElement('div');
        orderElement.className = 'order-history-item';
        orderElement.innerHTML = `
            <div class="order-header">
                <div class="order-date">
                    ${new Date(order.orderDate).toLocaleDateString()}
                </div>
                <div class="order-status ${order.status}">
                    ${getStatusText(order.status)}
                </div>
            </div>
            <div class="order-items">
                ${order.items.map(item => `
                    <div class="item">
                        <span>${item.name}</span>
                        <span>x${item.quantity}</span>
                    </div>
                `).join('')}
            </div>
            <div class="order-total">
                총 결제금액: ${order.total.toLocaleString()}원
            </div>
            <button onclick="viewOrderDetail('${order.id}')" class="view-detail-btn">
                상세보기
            </button>
        `;
        orderHistoryList.appendChild(orderElement);
    });
});

function viewOrderDetail(orderId) {
    window.location.href = `order-status.html?orderId=${orderId}`;
} 