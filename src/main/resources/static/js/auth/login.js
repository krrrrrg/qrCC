// 로그인 처리
function handleLogin(event) {
    
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;

    // 아이디 유효성 검사
    if (userId.length < 4) {
        alert('아이디는 4자 이상이어야 합니다.');
        return false;
    }

    // 비밀번호 길이 체크
    if (password.length < 8) {
        alert('비밀번호는 8자 이상이어야 합니다.');
        return false;
    }

    // 모든 유효성 검사를 통과하면 폼 제출
    return true;
}

// 회원가입 처리
function handleSignup(event, role = 'user') {
    event.preventDefault();
    
    const name = document.getElementById('name').value;
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;
    const passwordConfirm = document.getElementById('passwordConfirm').value;
    const phone = document.getElementById('phone').value;

    // 아이디 유효성 검사
    if (userId.length < 4) {
        alert('아이디는 4자 이상이어야 합니다.');
        return;
    }

    // 전화번호 유효성 검사
    if (!/^[0-9]{11}$/.test(phone)) {
        alert('올바른 전화번호를 입력해주세요.');
        return;
    }

    // 비밀번호 확인
    if (password !== passwordConfirm) {
        alert('비밀번호가 일치하지 않습니다.');
        return;
    }

    // 비밀번호 길이 체크
    if (password.length < 8) {
        alert('비밀번호는 8자 이상이어야 합니다.');
        return;
    }

    // 기존 사용자 확인
    const users = JSON.parse(localStorage.getItem('users')) || [];
    const owners = JSON.parse(localStorage.getItem('owners')) || [];

    // 통합 아이디 중복 체크
    const isUserIdExists = users.some(user => user.userId === userId);
    const isOwnerIdExists = owners.some(owner => owner.userId === userId);
    const isAdminId = userId === 'admin'; // admin 계정과의 중복도 체크

    if (isUserIdExists || isOwnerIdExists || isAdminId) {
        alert('이미 사용 중인 아이디입니다. 다른 아이디를 선택해주세요.');
        return;
    }

    // 새 사용자 정보
    const newUser = {
        name,
        userId,
        password,
        phone,
        role,
        createdAt: new Date().toISOString()
    };

    // 권한에 따라 다른 스토리지에 저장
    if (role === 'owner') {
        owners.push(newUser);
        localStorage.setItem('owners', JSON.stringify(owners));
        alert('점주 회원가입이 완료되었습니다.');
        window.location.href = '/owner/login';
    } else {
        users.push(newUser);
        localStorage.setItem('users', JSON.stringify(users));
        alert('회원가입이 완료되었습니다.');
        window.location.href = '/login';
    }
}

// 비밀번호 찾기 처리
function handleFindPassword(event, role = 'user') {
    event.preventDefault();
    
    const userId = document.getElementById('userId').value;
    const name = document.getElementById('name').value;
    const phone = document.getElementById('phone').value;

    // 권한에 따라 다른 스토리지 검색
    const storage = role === 'owner' ? 'owners' : 'users';
    const users = JSON.parse(localStorage.getItem(storage)) || [];
    const user = users.find(u => u.userId === userId && u.name === name && u.phone === phone);

    if (user) {
        // 임시 비밀번호 생성
        const tempPassword = generateTempPassword();
        user.password = tempPassword;
        localStorage.setItem(storage, JSON.stringify(users));

        // 결과 표시
        const resultDiv = document.getElementById('searchResult');
        if (resultDiv) {
            resultDiv.innerHTML = `
                <div class="result-box">
                    <p>임시 비밀번호가 발급되었습니다.</p>
                    <p class="temp-password">${tempPassword}</p>
                    <p class="warning">보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요.</p>
                    <button onclick="location.href='${role === 'owner' ? '/owner/login' : '/login'}'" 
                            class="primary-button">로그인하기</button>
                </div>
            `;
        } else {
            alert(`임시 비밀번호는 ${tempPassword} 입니다.\n로그인 비밀번호를 변경해주세요.`);
            window.location.href = role === 'owner' ? '/owner/login' : '/login';
        }
    } else {
        alert('일치하는 사용자 정보가 없습니다.');
    }
}

// 임시 비밀번호 생성 함수
function generateTempPassword() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const numbers = '0123456789';
    let password = '';
    
    // 최소 8자리, 영문자와 숫자 조합
    for(let i = 0; i < 8; i++) {
        if(i < 2) { // 최소 2개의 숫자 보장
            password += numbers.charAt(Math.floor(Math.random() * numbers.length));
        } else {
            password += chars.charAt(Math.floor(Math.random() * chars.length));
        }
    }
    
    return password;
}

// 로그아웃 처리
function handleLogout() {
    const urlParams = new URLSearchParams(window.location.search);
    const tableNo = urlParams.get('table');
    const id = urlParams.get('id');
    
    localStorage.removeItem('currentUser');
    
    // 테이블 정보가 있으면 유지하면서 리다이렉트
    if (tableNo && id) {
        window.location.href = `/?table=${tableNo}&id=${id}`;
    } else {
        window.location.href = '/';
    }
}

// 로그인 상태 확인
function checkLoginStatus() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return currentUser !== null;
}

// 아이디 찾기 처리 - 개선버전
function handleFindId(event, role = 'user') {
    event.preventDefault();
    
    const name = document.getElementById('name').value;
    const phone = document.getElementById('phone').value;

    // 권한에 따라 다른 스토리지 검색
    const storage = role === 'owner' ? 'owners' : 'users';
    const users = JSON.parse(localStorage.getItem(storage)) || [];
    const user = users.find(u => u.name === name && u.phone === phone);

    if (user) {
        // 아이디 마스킹 처리 (앞 2자리 제외하고 마스킹)
        const length = user.userId.length;
        const maskedId = user.userId.substring(0, 2) + '*'.repeat(length - 2);
        
        // 결과 표시
        const resultDiv = document.getElementById('searchResult');
        if (resultDiv) {
            resultDiv.innerHTML = `
                <div class="result-box">
                    <p>찾으시는 아이디는 <strong>${maskedId}</strong> 입니다.</p>
                    <button onclick="location.href='${role === 'owner' ? '/owner/login' : '/login'}'" 
                            class="primary-button">로그인하기</button>
                </div>
            `;
        } else {
            alert(`찾으시는 아���디는 ${maskedId} 입니다.`);
            window.location.href = role === 'owner' ? '/owner/login' : '/login';
        }
    } else {
        alert('일치하는 사용자 정보가 없습니다.');
    }
}

// 관리자 로그인 처리
function handleAdminLogin(event) {
    event.preventDefault();
    
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;

    // 점주 계정 확인
    const owners = JSON.parse(localStorage.getItem('owners')) || [];
    const owner = owners.find(o => o.userId === userId && o.password === password);

    if (owner || (userId === 'admin' && password === 'admin123')) { // 기존 admin 계정도 유지
        // 로그인 성공 시 관리자 정보를 localStorage에 저장
        const adminUser = owner || {
            userId: userId,
            name: owner ? owner.name : 'Admin',
            password: password,
            isLoggedIn: true
        };
        
        localStorage.setItem('adminUser', JSON.stringify(adminUser));
        window.location.href = 'admin-dashboard';
    } else {
        alert('아이디 또는 비밀번호가 올바르지 않습니다.');
    }
}

// 로그인 성공 후 처리
function handleLoginSuccess(user) {
    const redirectUrl = localStorage.getItem('checkoutRedirect');
    if (redirectUrl) {
        localStorage.removeItem('checkoutRedirect');
        window.location.href = redirectUrl;
    } else {
        window.location.href = 'index';
    }
}

// 로그인 상태 체크 및 UI 업데이트
function updateAuthUI() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const authButton = document.getElementById('authButton');
    
    if (!authButton) return; // authButton이 없는 페이지에서는 실행하지 않음
    
    const authIcon = document.getElementById('authIcon');
    const authText = document.getElementById('authText');
    
    if (currentUser) {
        // 로그인 상태
        authIcon.className = 'fas fa-sign-out-alt';
        authText.textContent = '로그아웃';
        authButton.onclick = handleLogout;
    } else {
        // 로그아웃 상태
        authIcon.className = 'fas fa-sign-in-alt';
        authText.textContent = '로그인';
        authButton.onclick = () => window.location.href = 'login';
    }
}

// 페이지 로드시 실행
document.addEventListener('DOMContentLoaded', function() {
    updateAuthUI();
}); 