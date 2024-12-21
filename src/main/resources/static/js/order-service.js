// 주문 상태 상수
const ORDER_STATUS = {
    PENDING: 'PENDING',
    ACCEPTED: 'ACCEPTED',
    COMPLETED: 'COMPLETED',
    CANCELLED: 'CANCELLED'
};

class OrderService {
    constructor() {
        this.apiBaseUrl = '/api/orders';
    }

    // 새로운 주문 생성
    async createOrder(orderData) {
        try {
            if (!orderData.restaurantId || !orderData.tableId || !orderData.items || !orderData.items.length) {
                throw new Error('Invalid order data');
            }

            const response = await fetch(this.apiBaseUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    restaurantId: orderData.restaurantId,
                    tableId: orderData.tableId,
                    orderItems: orderData.items.map(item => ({
                        menu: { id: item.id },
                        quantity: item.quantity
                    }))
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to create order');
            }

            const order = await response.json();
            this.notifyOrderUpdate(order);
            return order;
        } catch (error) {
            console.error('Error creating order:', error);
            throw error;
        }
    }

    // 주문 상태 업데이트
    async updateOrderStatus(orderId, status) {
        try {
            if (!orderId || !status || !Object.values(ORDER_STATUS).includes(status)) {
                throw new Error('Invalid order status update data');
            }

            const response = await fetch(`${this.apiBaseUrl}/${orderId}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ status })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to update order status');
            }

            const updatedOrder = await response.json();
            this.notifyOrderUpdate(updatedOrder);
            return updatedOrder;
        } catch (error) {
            console.error('Error updating order status:', error);
            throw error;
        }
    }

    // 주문 목록 조회
    async getOrders(tableId, status) {
        try {
            let url = this.apiBaseUrl;
            const params = new URLSearchParams();
            if (tableId) params.append('tableId', tableId);
            if (status && Object.values(ORDER_STATUS).includes(status)) {
                params.append('status', status);
            }
            if (params.toString()) url += '?' + params.toString();

            const response = await fetch(url);
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to fetch orders');
            }

            const orders = await response.json();
            return orders;
        } catch (error) {
            console.error('Error fetching orders:', error);
            throw error;
        }
    }

    // 주문 이력 조회
    async getOrderHistory(restaurantId, tableId) {
        try {
            if (!restaurantId || !tableId) {
                throw new Error('Restaurant ID and Table ID are required');
            }

            const url = `${this.apiBaseUrl}/history?restaurantId=${restaurantId}&tableId=${tableId}`;
            const response = await fetch(url);
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to fetch order history');
            }

            const history = await response.json();
            return history;
        } catch (error) {
            console.error('Error fetching order history:', error);
            throw error;
        }
    }

    // 주문 상태 변경 알림
    notifyOrderUpdate(order) {
        const event = new CustomEvent('orderUpdate', { detail: order });
        window.dispatchEvent(event);
    }

    // 주문 상태 변경 이벤트 리스너 등록
    onOrderUpdate(callback) {
        window.addEventListener('orderUpdate', (event) => callback(event.detail));
    }
}

// 전역 객체로 export
window.orderService = new OrderService();
