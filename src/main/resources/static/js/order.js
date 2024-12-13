// 주문 관리 클래스
class OrderManager {
    constructor() {
        this.orders = JSON.parse(localStorage.getItem('orders')) || [];
    }

    // 새 주문 생성
    createOrder(cart, userId = null) {
        const order = {
            orderId: this.generateOrderId(),
            userId: userId,
            storeId: cart.storeId,
            tableNumber: cart.tableNumber,
            items: cart.items,
            totalAmount: cart.getTotalAmount(),
            status: 'pending',
            orderDate: new Date().toISOString()
        };

        this.orders.push(order);
        this.saveOrders();
        return order;
    }

    // 주문 ID 생성
    generateOrderId() {
        return 'ORD' + Date.now().toString(36).toUpperCase();
    }

    // 주문 상태 업데이트
    updateOrderStatus(orderId, status) {
        const order = this.orders.find(o => o.orderId === orderId);
        if (order) {
            order.status = status;
            this.saveOrders();
        }
    }

    // 주문 내역 저장
    saveOrders() {
        localStorage.setItem('orders', JSON.stringify(this.orders));
    }

    // 사용자의 주문 내역 조회
    getUserOrders(userId) {
        return this.orders.filter(order => order.userId === userId);
    }
} 