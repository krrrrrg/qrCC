// CSRF 토큰 가져오기
const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content;

document.addEventListener('DOMContentLoaded', function() {
    const passwordForm = document.querySelector('#passwordChangeForm');
    const deleteAccountForm = document.getElementById('deleteAccountForm');

    if (passwordForm) {
        passwordForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const currentPassword = document.querySelector('#currentPassword').value;
            const newPassword = document.querySelector('#newPassword').value;
            const confirmPassword = document.querySelector('#confirmPassword').value;
            
            if (newPassword !== confirmPassword) {
                alert('새 비밀번호가 일치하지 않습니다.');
                return;
            }
            
            try {
                const headers = {
                    'Content-Type': 'application/json'
                };
                
                // CSRF 토큰이 있으면 헤더에 추가
                if (csrfHeader && csrfToken) {
                    headers[csrfHeader] = csrfToken;
                }

                const response = await fetch('/api/owner/password', {
                    method: 'PUT',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify({
                        currentPassword: currentPassword,
                        newPassword: newPassword
                    })
                });
                
                if (response.ok) {
                    alert('비밀번호가 성공적으로 변경되었습니다.');
                    passwordForm.reset();
                } else {
                    const error = await response.text();
                    alert('비밀번호 변경 실패: ' + error);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('비밀번호 변경 중 오류가 발생했습니다.');
            }
        });
    }

    if (deleteAccountForm) {
        deleteAccountForm.addEventListener('submit', handleAccountDeletion);
    }
});

// 계정 탈퇴 처리
async function handleAccountDeletion(event) {
    event.preventDefault();
    
    const confirmDelete = confirm('정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.');
    if (!confirmDelete) {
        return;
    }
    
    const password = document.querySelector('#deleteAccountPassword').value;
    
    try {
        const headers = {
            'Content-Type': 'application/json'
        };
        
        // CSRF 토큰이 있으면 헤더에 추가
        if (csrfHeader && csrfToken) {
            headers[csrfHeader] = csrfToken;
        }

        const response = await fetch('/api/owner/account', {
            method: 'DELETE',
            headers: headers,
            credentials: 'include',
            body: JSON.stringify({
                password: password
            })
        });

        if (response.ok) {
            alert('계정이 성공적으로 삭제되었습니다.');
            window.location.href = '/'; // 메인 페이지로 리다이렉트
        } else {
            const error = await response.text();
            alert('계정 삭제 실패: ' + error);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('계정 삭제 중 오류가 발생했습니다.');
    }
}
