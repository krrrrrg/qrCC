// 점주 회원가입 유효성 검사
function validateOwnerSignup(event) {
    const name = document.getElementById('name').value;
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;
    const passwordConfirm = document.getElementById('passwordConfirm').value;
    const phone = document.getElementById('phone').value;

    // 아이디 유효성 검사
    if (userId.length < 4) {
        alert('아이디는 4자 이상이어야 합니다.');
        return false;
    }

    // 전화번호 유효성 검사
    if (!/^[0-9]{11}$/.test(phone)) {
        alert('올바른 전화번호를 입력해주세요.');
        return false;
    }

    // 비밀번호 확인
    if (password !== passwordConfirm) {
        alert('비밀번호가 일치하지 않습니다.');
        return false;
    }

    // 비밀번호 길이 체크
    if (password.length < 8) {
        alert('비밀번호는 8자 이상이어야 합니다.');
        return false;
    }

    return true;
}

// 점주 로그인 처리
function handleOwnerLogin(event) {
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;

    // 간단한 유효성 검사
    if (!userId || !password) {
        alert('아이디와 비밀번호를 모두 입력해주세요.');
        event.preventDefault();
        return false;
    }

    // 아이디 형식 검사
    if (userId.length < 4) {
        alert('아이디는 4자 이상이어야 합니다.');
        event.preventDefault();
        return false;
    }

    // 비밀번호 길이 검사
    if (password.length < 8) {
        alert('비밀번호는 8자 이상이어야 합니다.');
        event.preventDefault();
        return false;
    }

    // form이 정상적으로 제출되도록 true 반환
    return true;
}