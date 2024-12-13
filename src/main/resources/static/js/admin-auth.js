// 관리자 로그인 처리
function handleAdminLogin(event) {
    event.preventDefault();
    
    const adminId = document.getElementById('adminId').value;
    const password = document.getElementById('adminPassword').value;

    // 실제 구현에서는 서버에서 인증을 처리해야 합니다
    // 여기서는 데모를 위해 간단한 체크만 수행합니다
    if (adminId === 'admin' && password === 'admin123') {
        // 로그인 성공
        const adminUser = {
            id: adminId,
            name: '관리자',
            role: 'admin'
        };
        
        localStorage.setItem('adminUser', JSON.stringify(adminUser));
        window.location.href = 'admin-dashboard.html';
    } else {
        alert('아이디 또는 비밀번호가 올바르지 않습니다.');
    }
}

// 관리자 로그인 상태 확인
function checkAdminAuth() {
    const adminUser = JSON.parse(localStorage.getItem('adminUser'));
    if (!adminUser) {
        window.location.href = 'admin-login.html';
    }
}

// 관리자 로그아웃
function handleAdminLogout() {
    localStorage.removeItem('adminUser');
    window.location.href = 'admin-login.html';
}

// 페이지 로드 시 관리자 인증 확인
if (window.location.pathname.includes('admin-dashboard')) {
    checkAdminAuth();
} 