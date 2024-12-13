document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('orderId');
    
    // 주문 정보 초기 로드
    updateOrderStatus();
    
    // 주문 상태 주기적으로 체크 (3초마다)
    setInterval(updateOrderStatus, 3000);
    
    function updateOrderStatus() {
        const orders = JSON.parse(localStorage.getItem('orders')) || [];
        const order = orders.find(o => o.id.toString() === orderId);
        
        if (!order) return;
        
        // 주문번호 표시
        document.getElementById('orderNumber').textContent = order.id;
        
        // 상태에 따른 UI 업데이트
        const statusSteps = document.querySelectorAll('.status-step');
        statusSteps.forEach(step => {
            const status = step.dataset.status;
            if (getStatusProgress(status) <= getStatusProgress(order.status)) {
                step.classList.add('active');
            } else {
                step.classList.remove('active');
            }
        });
        
        // 프로그레스 바 업데이트
        const progressBar = document.getElementById('progressBar');
        progressBar.style.width = getProgressWidth(order.status);
        
        // 주문 내역 표시
        const orderItems = document.getElementById('orderItems');
        orderItems.innerHTML = order.items.map(item => `
            <div class="order-item">
                <span>${item.name} x ${item.quantity}</span>
                <span>${(item.price * item.quantity).toLocaleString()}원</span>
            </div>
        `).join('');
    }
    
    function getStatusProgress(status) {
        const progress = {
            'pending': 0,
            'cooking': 1,
            'completed': 2
        };
        return progress[status] || 0;
    }
    
    function getProgressWidth(status) {
        const widths = {
            'pending': '33%',
            'cooking': '66%',
            'completed': '100%'
        };
        return widths[status] || '0%';
    }
}); 