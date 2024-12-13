export class AdminService {
    async login(credentials) {
        // 실제 구현에서는 서버 API 호출
        const { userId, password } = credentials;
        
        // 임시 로그인 체크 (실제로는 서버에서 검증해야 함)
        if (userId === 'admin' && password === 'admin123') {
            const adminData = {
                id: 'admin',
                storeName: '투스 카페',
                role: 'owner'
            };
            localStorage.setItem('adminUser', JSON.stringify(adminData));
            return true;
        }
        return false;
    }

    checkAuth() {
        const adminUser = localStorage.getItem('adminUser');
        return adminUser ? JSON.parse(adminUser) : null;
    }

    logout() {
        localStorage.removeItem('adminUser');
    }
} 