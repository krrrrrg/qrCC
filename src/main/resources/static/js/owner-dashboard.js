document.addEventListener('DOMContentLoaded', function() {
    // 초기 로드
    loadOrders();
    loadTables();

    // 필터 이벤트 리스너
    document.getElementById('tableFilter').addEventListener('change', loadOrders);
    document.getElementById('statusFilter').addEventListener('change', loadOrders);

    // 5초마다 주문 목록 업데이트
    setInterval(loadOrders, 5000);
});

async function loadTables() {
    try {
        const response = await fetch('/api/tables');
        if (!response.ok) {
            throw new Error('Failed to fetch tables');
        }

        const tables = await response.json();
        const tableFilter = document.getElementById('tableFilter');
        
        tables.forEach(table => {
            const option = document.createElement('option');
            option.value = table.id;
            option.textContent = `테이블 ${table.tableNumber}번`;
            tableFilter.appendChild(option);
        });
    } catch (error) {
        console.error('Error loading tables:', error);
    }
}

async function loadOrders() {
    try {
        const tableFilter = document.getElementById('tableFilter').value;
        const statusFilter = document.getElementById('statusFilter').value;

        let url = '/api/orders';
        const params = new URLSearchParams();
        if (tableFilter) params.append('tableId', tableFilter);
        if (statusFilter) params.append('status', statusFilter);
        if (params.toString()) url += '?' + params.toString();

        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Failed to fetch orders');
        }

        const orders = await response.json();
        displayOrders(orders);
    } catch (error) {
        console.error('Error loading orders:', error);
    }
}

function displayOrders(orders) {
    const orderList = document.querySelector('.order-list');
    const template = document.getElementById('order-template');
    orderList.innerHTML = '';

    orders.forEach(order => {
        const orderElement = template.content.cloneNode(true);

        // 테이블 번호와 주문 시간
        orderElement.querySelector('.table-number').textContent = 
            `테이블 ${order.tableNumber}번`;
        orderElement.querySelector('.order-date').textContent = 
            new Date(order.regDate).toLocaleString();

        // 메뉴 항목들
        const menuItems = orderElement.querySelector('.menu-items');
        order.orderItems.forEach(item => {
            const itemElement = document.createElement('div');
            itemElement.className = 'menu-item';
            itemElement.innerHTML = `
                <span>${item.menu.name} x ${item.quantity}</span>
                <span>${formatPrice(item.price)}원</span>
            `;
            menuItems.appendChild(itemElement);
        });

        // 총 금액
        orderElement.querySelector('.total-amount').textContent = 
            formatPrice(order.totalAmount);

        // 현재 상태 선택
        const statusSelect = orderElement.querySelector('.status-select');
        statusSelect.value = order.status;

        // 상태 변경 버튼
        const updateButton = orderElement.querySelector('.update-status-btn');
        updateButton.addEventListener('click', () => updateOrderStatus(order.id, statusSelect.value));

        orderList.appendChild(orderElement);
    });
}

async function updateOrderStatus(orderId, newStatus) {
    try {
        const response = await fetch(`/api/orders/${orderId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ status: newStatus })
        });

        if (!response.ok) {
            throw new Error('Failed to update order status');
        }

        // 주문 목록 새로고침
        loadOrders();
    } catch (error) {
        console.error('Error updating order status:', error);
        alert('주문 상태 변경에 실패했습니다.');
    }
}

function formatPrice(price) {
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}
