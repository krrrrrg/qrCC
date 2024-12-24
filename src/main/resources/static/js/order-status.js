let orderId;

document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    orderId = urlParams.get('orderId');
    
    // 초기 로드
    loadOrderStatus();

    // 5초마다 주문 상태 확인
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
            <div class="item-info">
                <span class="item-name">${item.menuName}</span>
                <span class="item-quantity">x${item.quantity}</span>
            </div>
            <span class="item-price">${formatPrice(item.price * item.quantity)}원</span>
        `;
        orderItemsContainer.appendChild(itemElement);
    });

    // 총 금액 업데이트
    document.querySelector('.total-value').textContent = formatPrice(order.totalAmount) + '원';
}

function updateStatusTimeline(currentStatus) {
    const steps = document.querySelectorAll('.status-step');
    const progressFill = document.querySelector('.progress-fill');
    
    // 진행 상태에 따른 프로그레스 바 너비 설정
    let progressWidth = '0%';
    if (currentStatus === 'PENDING') progressWidth = '0%';
    else if (currentStatus === 'ACCEPTED') progressWidth = '50%';
    else if (currentStatus === 'COMPLETED') progressWidth = '100%';
    else if (currentStatus === 'CANCELLED') progressWidth = '100%';
    
    progressFill.style.width = progressWidth;

    steps.forEach(step => {
        const stepStatus = step.getAttribute('data-status');
        step.classList.remove('active');
        
        if (currentStatus === 'CANCELLED') {
            if (stepStatus === 'CANCELLED') {
                step.classList.add('active');
            }
        } else {
            const statusOrder = ['PENDING', 'ACCEPTED', 'COMPLETED'];
            const currentIndex = statusOrder.indexOf(currentStatus);
            const stepIndex = statusOrder.indexOf(stepStatus);
            
            if (stepIndex <= currentIndex) {
                step.classList.add('active');
            }
        }
    });
}

function formatPrice(price) {
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

function goBack() {
    const urlParams = new URLSearchParams(window.location.search);
    const restaurantId = urlParams.get('restaurantId');
    const tableId = urlParams.get('tableId');
    
    window.location.href = `/order/history?restaurantId=${restaurantId}&tableId=${tableId}`;
}