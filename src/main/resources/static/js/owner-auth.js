// 점주 로그인 처리
function handleOwnerLogin(event) {
    event.preventDefault();
    
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;

    const owners = JSON.parse(localStorage.getItem('owners')) || [];
    const owner = owners.find(o => o.userId === userId && o.password === password);

    if (owner) {
        // 로그인 성공
        localStorage.setItem('currentOwner', JSON.stringify({
            id: owner.userId,
            storeName: owner.storeName,
            role: 'owner'
        }));
        alert('로그인 되었습니다.');
        window.location.href = 'owner-dashboard.html';
    } else {
        alert('아이디 또는 비밀번호가 올바르지 않습니다.');
    }
}

// 점주 회원가입 처리
function handleOwnerSignup(event) {
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
    if (users.some(user => user.userId === userId)) {
        alert('이미 사용 중인 아이디입니다.');
        return;
    }

    // 점주 정보 저장 (role: 'owner' 추가)
    const ownerData = {
        name,
        userId,
        password,
        phone,
        role: 'owner'  // 점주 권한 부여
    };

    users.push(ownerData);
    localStorage.setItem('users', JSON.stringify(users));

    alert('점주 회원가입이 완료되었습니다.');
    window.location.href = 'owner-login.html';
} 