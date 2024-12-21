document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('orderId');
    
    loadOrderStatus();
    // 5초마다 주문 상태 업데이트
    setInterval(loadOrderStatus, 5000);
});

async function loadOrderStatus() {
    try {
        const response = await fetch(`/api/orders/${orderId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch order status');
        }

        const order = await response.json();
        updateOrderStatus(order);
    } catch (error) {
        console.error('Error loading order status:', error);
    }
}

function updateOrderStatus(order) {
    // 레스토랑 정보 업데이트
    document.querySelector('.restaurant-name').textContent = order.restaurant.name;
    document.querySelector('.table-number').textContent = `테이블 ${order.table.tableNumber}번`;

    // 주문 정보 업데이트
    document.querySelector('.order-number').textContent = `주문번호: ${order.id}`;
    document.querySelector('.order-date').textContent = 
        new Date(order.regDate).toLocaleString();

    // 주문 상태 업데이트
    updateStatusTimeline(order.status);

    // 주문 항목 업데이트
    const orderItemsContainer = document.querySelector('.order-items');
    orderItemsContainer.innerHTML = ''; // 기존 항목 제거

    order.orderItems.forEach(item => {
        const itemElement = document.createElement('div');
        itemElement.className = 'order-item';
        itemElement.innerHTML = `
            <span class="item-name">${item.menu.name}</span>
            <span class="item-quantity">x${item.quantity}</span>
            <span class="item-price">${formatPrice(item.price)}원</span>
        `;
        orderItemsContainer.appendChild(itemElement);
    });

    // 총 금액 업데이트
    document.querySelector('.total-amount').textContent = 
        formatPrice(order.totalAmount) + '원';
}

function updateStatusTimeline(currentStatus) {
    const steps = document.querySelectorAll('.status-step');
    const statusOrder = ['PENDING', 'ACCEPTED', 'COMPLETED', 'CANCELLED'];
    const currentIndex = statusOrder.indexOf(currentStatus);

    steps.forEach((step, index) => {
        if (currentStatus === 'CANCELLED') {
            step.classList.remove('active', 'current');
            if (step.getAttribute('data-status') === 'CANCELLED') {
                step.classList.add('active', 'current', 'cancelled');
            }
            return;
        }

        const status = step.getAttribute('data-status');
        if (index <= currentIndex) {
            step.classList.add('active');
            if (index === currentIndex) {
                step.classList.add('current');
            } else {
                step.classList.remove('current');
            }
        } else {
            step.classList.remove('active', 'current');
        }
    });
}

function formatPrice(price) {
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}