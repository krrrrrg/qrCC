import { AdminService } from './admin-service.js';

const adminService = new AdminService();

// 이미 로그인되어 있다면 대시보드로 이동
if (adminService.checkAuth()) {
    window.location.href = 'owner-dashboard.html';
}

document.getElementById('ownerLoginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;

    try {
        const success = await adminService.login({ userId, password });
        if (success) {
            window.location.href = 'owner-dashboard.html';
        } else {
            alert('아이디 또는 비밀번호가 올바르지 않습니다.');
        }
    } catch (error) {
        alert('로그인 중 오류가 발생했습니다.');
        console.error(error);
    }
}); 