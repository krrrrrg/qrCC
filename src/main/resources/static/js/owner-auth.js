// 점주 회원가입 유효성 검사
function validateOwnerSignup(event) {
    const name = document.getElementById('name').value;
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
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
    if (password !== confirmPassword) {
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

// axios 기본 설정
axios.defaults.baseURL = '/api';
axios.defaults.headers.common['Content-Type'] = 'application/json';

// CSRF 토큰 설정
const token = document.querySelector('meta[name="_csrf"]')?.content;
const header = document.querySelector('meta[name="_csrf_header"]')?.content;
if (token && header) {
    axios.defaults.headers.common[header] = token;
}

// 로그인 처리
async function handleOwnerLogin(event) {
    event.preventDefault();
    
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;

    try {
        const response = await axios.post('/owner/login', {
            userId: userId,
            password: password
        });

        if (response.data.success) {
            window.location.href = '/owner/dashboard';
        } else {
            alert('로그인에 실패했습니다.');
        }
    } catch (error) {
        console.error('로그인 오류:', error);
        alert('로그인 처리 중 오류가 발생했습니다.');
    }
}

// 로그아웃 처리
async function handleAdminLogout() {
    try {
        await axios.post('/owner/logout');
        window.location.href = '/owner/login';
    } catch (error) {
        console.error('로그아웃 오류:', error);
        alert('로그아웃 처리 중 오류가 발생했습니다.');
    }
}

// 계정 설정 관련 이벤트 리스너
document.addEventListener('DOMContentLoaded', function() {
    // 비밀번호 변경 폼
    const passwordChangeForm = document.getElementById('passwordChangeForm');
    if (passwordChangeForm) {
        passwordChangeForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (newPassword !== confirmPassword) {
                alert('새 비밀번호가 일치하지 않습니다.');
                return;
            }

            try {
                await axios.put('/owner/password', {
                    currentPassword,
                    newPassword
                });
                
                alert('비밀번호가 성공적으로 변경되었습니다.');
                passwordChangeForm.reset();
            } catch (error) {
                console.error('비밀번호 변경 실패:', error);
                alert('비밀번호 변경에 실패했습니다.');
            }
        });
    }

    // 계정 삭제 폼
    const deleteAccountForm = document.getElementById('deleteAccountForm');
    if (deleteAccountForm) {
        deleteAccountForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            if (!confirm('정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
                return;
            }

            const password = document.getElementById('deleteConfirmPassword').value;

            try {
                await axios.delete('/owner/account', {
                    data: { password }
                });
                
                alert('계정이 성공적으로 삭제되었습니다.');
                window.location.href = '/owner/login';
            } catch (error) {
                console.error('계정 삭제 실패:', error);
                alert('계정 삭제에 실패했습니다.');
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 현재 로그인한 점주의 가게 정보 불러오기
        const { data: storeInfo } = await axios.get('/owner/restaurant');
        
        console.log('가게 정보:', storeInfo);

        // DOM 요소 업데이트 함수
        const updateElement = (id, value) => {
            const element = document.getElementById(id);
            if (element && value) element.value = value;
        };

        // 가게 정보 설정
        updateElement('storeName', storeInfo.name);
        updateElement('storeAddress', storeInfo.address);
        updateElement('storeCategory', storeInfo.category);
        updateElement('storePhone', storeInfo.phoneNumber);
        updateElement('openTime', storeInfo.openTime);
        updateElement('closeTime', storeInfo.closeTime);
        updateElement('storeNotice', storeInfo.notice);
        updateElement('storeSns', storeInfo.snsLink);
        
        // 사이드바 가게 이름 업데이트
        const sidebarStoreName = document.querySelector('.admin-profile .store-name');
        if (sidebarStoreName && storeInfo.name) {
            sidebarStoreName.textContent = storeInfo.name;
        }
        
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
            phoneNumber: document.getElementById('storePhone').value,
            openTime: document.getElementById('openTime').value,
            closeTime: document.getElementById('closeTime').value,
            notice: document.getElementById('storeNotice').value,
            snsLink: document.getElementById('storeSns').value
        };
        
        await axios.post('/owner/restaurant', storeInfo);
        
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