import { AdminService } from './admin-service.js';

const adminService = new AdminService();

// 인증 체크
if (!adminService.checkAuth()) {
    window.location.href = 'admin-login.html';
}

// 로그아웃 처리
window.handleLogout = () => {
    adminService.logout();
    window.location.href = 'admin-login.html';
}; 