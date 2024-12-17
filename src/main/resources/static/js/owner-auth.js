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

// axios 기본 설정


axios.defaults.baseURL = '/api';
axios.defaults.headers.common['Content-Type'] = 'application/json';

// CSRF 토큰 설정
const token = document.querySelector('meta[name="_csrf"]')?.content;
const header = document.querySelector('meta[name="_csrf_header"]')?.content;
if (token && header) {
    axios.defaults.headers.common[header] = token;
}

document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 가게 정보 불러오기
        const { data: storeInfo } = await axios.get('/restaurant/1');
        
        console.log('가게 정보:', storeInfo);

        // DOM 요소 업데이트 함수
        const updateElement = (id, value) => {
            const element = document.getElementById(id);
            if (element) element.value = value;
        };

        // 가게 정보 설정
        updateElement('storeName', storeInfo.name);
        updateElement('storeAddress', storeInfo.address);
        updateElement('storeCategory', storeInfo.category);
        updateElement('storePhone', storeInfo.phone);
        
        // 사이드바 가게 이름 업데이트
        const sidebarStoreName = document.querySelector('.admin-profile .store-name');
        if (sidebarStoreName && storeInfo.name) {
            sidebarStoreName.textContent = storeInfo.name;
        }

        // 추가 필드 업데이트
        document.querySelector('input[placeholder="오전 09:00"]')?.value = storeInfo.openTime || '';
        document.querySelector('input[placeholder="오후 10:00"]')?.value = storeInfo.closeTime || '';
        document.querySelector('input[placeholder="안녕하세요 1인 1메뉴 해주세요"]')?.value = storeInfo.notice || '';
        document.querySelector('input[placeholder="ddd"]')?.value = storeInfo.snsLink || '';
        
    } catch (error) {
        console.error('가게 정보 로딩 실패:', error);
    }
});

// 가게 정보 저장
document.getElementById('storeSettingsForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    
    try {
        const storeInfo = {
            name: document.getElementById('storeName').value,
            address: document.getElementById('storeAddress').value,
            category: document.getElementById('storeCategory').value,
            phone: document.getElementById('storePhone').value,
            openTime: document.querySelector('input[placeholder="오전 09:00"]').value,
            closeTime: document.querySelector('input[placeholder="오후 10:00"]').value,
            notice: document.querySelector('input[placeholder="안녕하세요 1인 1메뉴 해주세요"]').value,
            snsLink: document.querySelector('input[placeholder="ddd"]').value
        };
        
        await axios.post('/restaurant', storeInfo);
        
        // 사이드바 가게 이름 업데이트
        const sidebarStoreName = document.querySelector('.admin-profile .store-name');
        if (sidebarStoreName) {
            sidebarStoreName.textContent = storeInfo.name;
        }
        
        alert('가게 정보가 성공적으로 저장되었습니다.');
        
    } catch (error) {
        console.error('가게 정보 저장 실패:', error);
        alert('가게 정보 저장에 실패했습니다. 다시 시도해주세요.');
    }
});