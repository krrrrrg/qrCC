document.addEventListener('DOMContentLoaded', function() {
    loadOrderHistory();
});

async function loadOrderHistory() {
    try {
        // URL에서 restaurantId와 tableId 가져오기
        const urlParams = new URLSearchParams(window.location.search);
        const restaurantId = urlParams.get('restaurantId');
        const tableId = urlParams.get('tableId');

        // 주문 내역 가져오기
        const response = await fetch(`/api/orders/history?restaurantId=${restaurantId}&tableId=${tableId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch order history');
        }

        const orders = await response.json();
        
        // 주문 내역이 없는 경우
        if (!orders || orders.length === 0) {
            document.querySelector('.no-orders').style.display = 'block';
            return;
        }

        displayOrders(orders);
    } catch (error) {
        console.error('Error loading order history:', error);
        document.querySelector('.no-orders').style.display = 'block';
    }
}

function displayOrders(orders) {
    const orderList = document.querySelector('.order-list');
    const template = document.getElementById('order-template');

    orders.forEach(order => {
        const orderElement = template.content.cloneNode(true);

        // 주문 날짜
        orderElement.querySelector('.order-date').textContent = 
            new Date(order.regDate).toLocaleString();

        // 주문 상태
        orderElement.querySelector('.order-status').textContent = 
            getOrderStatusText(order.status);

        // 레스토랑 정보
        orderElement.querySelector('.restaurant-name').textContent = 
            order.restaurant.name;
        orderElement.querySelector('.table-number').textContent = 
            `테이블 ${order.table.tableNumber}번`;

        // 주문 항목들
        const orderItemsContainer = orderElement.querySelector('.order-items');
        order.orderItems.forEach(item => {
            const itemElement = document.createElement('div');
            itemElement.className = 'order-item-detail';
            itemElement.innerHTML = `
                <span class="item-name">${item.menu.name}</span>
                <span class="item-quantity">x${item.quantity}</span>
                <span class="item-price">${formatPrice(item.price)}원</span>
            `;
            orderItemsContainer.appendChild(itemElement);
        });

        // 총 금액
        orderElement.querySelector('.total-amount').textContent = 
            formatPrice(order.totalAmount);

        orderList.appendChild(orderElement);
    });
}

function getOrderStatusText(status) {
    const statusMap = {
        'PENDING': '주문 대기',
        'ACCEPTED': '주문 접수',
        'COMPLETED': '완료',
        'CANCELLED': '취소'
    };
    return statusMap[status] || status;
}

function formatPrice(price) {
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}