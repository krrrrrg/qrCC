// 주문 상태 상수
const ORDER_STATUS = {
    PENDING: 'pending',
    PREPARING: 'preparing',
    COMPLETED: 'completed',
    CANCELLED: 'cancelled'
};

class OrderService {
    constructor() {
        this.ordersKey = 'orders';
        this.orderHistoryKey = 'orderHistory';
        this.orderStatusKey = 'orderStatus';
    }

    // 새로운 주문 생성
    async createOrder(orderData) {
        const orders = this.getOrders();
        const orderId = `order_${Date.now()}`;
        
        const order = {
            id: orderId,
            ...orderData,
            status: ORDER_STATUS.PENDING,
            createdAt: new Date().toISOString()
        };

        // 주문 목록에 추가
        orders.push(order);
        localStorage.setItem(this.ordersKey, JSON.stringify(orders));

        // 주문 이력에 추가
        this.addToOrderHistory(order);

        // 주문 상태 업데이트
        this.updateOrderStatus(orderId, ORDER_STATUS.PENDING);

        return order;
    }

    // 주문 목록 가져오기
    getOrders() {
        return JSON.parse(localStorage.getItem(this.ordersKey)) || [];
    }

    // 주문 이력에 추가
    addToOrderHistory(order) {
        const history = JSON.parse(localStorage.getItem(this.orderHistoryKey)) || [];
        history.push(order);
        localStorage.setItem(this.orderHistoryKey, JSON.stringify(history));
    }

    // 주문 상태 업데이트
    updateOrderStatus(orderId, status) {
        const statusMap = JSON.parse(localStorage.getItem(this.orderStatusKey)) || {};
        statusMap[orderId] = status;
        localStorage.setItem(this.orderStatusKey, JSON.stringify(statusMap));

        // 주문 목록에서도 상태 업데이트
        const orders = this.getOrders();
        const updatedOrders = orders.map(order => 
            order.id === orderId ? { ...order, status } : order
        );
        localStorage.setItem(this.ordersKey, JSON.stringify(updatedOrders));
    }

    // 주문 상태 조회
    getOrderStatus(orderId) {
        const statusMap = JSON.parse(localStorage.getItem(this.orderStatusKey)) || {};
        return statusMap[orderId] || null;
    }

    // 주문 이력 조회
    getOrderHistory() {
        return JSON.parse(localStorage.getItem(this.orderHistoryKey)) || [];
    }
}
