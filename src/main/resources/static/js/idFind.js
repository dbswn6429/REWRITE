$(document).ready(function (){
    // 페이지가 완전히 로드된 후 실행
    document.addEventListener('DOMContentLoaded', function() {
        // 폼 요소 가져오기
        const form = document.querySelector('.section form');

        // 폼 제출 이벤트 처리
        form.addEventListener('submit', function(event) {
            // 기본 제출 동작 방지
            event.preventDefault();

            // 입력값 가져오기
            const name = form.querySelector('input[name="name"]').value.trim();
            const phone = form.querySelector('input[name="phone"]').value.trim();
            const email = form.querySelector('input[name="email"]').value.trim();

            // 입력값 유효성 검사
            if (!name) {
                alert('이름을 입력해주세요.');
                return;
            }

            if (!phone) {
                alert('전화번호를 입력해주세요.');
                return;
            }

            if (!email) {
                alert('이메일을 입력해주세요.');
                return;
            }

            // 이메일 형식 검사
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                alert('유효한 이메일 주소를 입력해주세요.');
                return;
            }

            // 전화번호 형식 검사 (010-1234-5678 형식)
            const phoneRegex = /^\d{3}-\d{3,4}-\d{4}$/;
            if (!phoneRegex.test(phone)) {
                alert('전화번호 형식이 올바르지 않습니다. 010-1234-5678 형식으로 입력해주세요.');
                return;
            }

            // API 요청에 필요한 데이터 구성
            const requestData = {
                name: name,
                phone: phone,
                email: email
            };

            // 로딩 메시지 표시
            showMessage('처리 중입니다...', 'info');

            // API 호출
            fetch('/api/auth/find-id', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            })
                .then(response => {
                    // 응답을 JSON으로 변환 (API가 항상 JSON 응답을 반환한다고 가정)
                    return response.json().then(data => {
                        // response와 data를 함께 반환
                        return { status: response.status, data: data };
                    });
                })
                .then(result => {
                    // 성공 시 처리 (200 OK)
                    if (result.status === 200) {
                        showMessage(result.data.message || '아이디 정보가 이메일로 발송되었습니다.', 'success');
                    }
                    // 실패 시 처리 (404 Not Found 등)
                    else {
                        showMessage(result.data.message || '요청 처리 중 오류가 발생했습니다.', 'error');
                    }
                })
                .catch(error => {
                    // 네트워크 오류 등 예외 처리
                    console.error('API 요청 오류:', error);
                    showMessage('서버 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', 'error');
                });
        });

        // ->동적 요소 생성임. 무조건 있는게 아니라, message-box가 없어도 지가 알아서 추가하는것.
        // 쿼리셀렉터에 없었다면, 그냥 지가 알아서 만들고, 메세지 삽입하는걸로 넣어버려서 동적 추가 처리.
        // 메시지 표시 함수
        function showMessage(message, type) {
            // 기존 메시지 요소가 있으면 제거
            const existingMessage = document.querySelector('.message-box');
            if (existingMessage) {
                existingMessage.remove();
            }

            // 새 메시지 요소 생성
            const messageBox = document.createElement('div');
            messageBox.className = `message-box ${type}`;
            messageBox.textContent = message;

            // 폼 아래에 메시지 삽입
            form.parentNode.insertBefore(messageBox, form.nextSibling);

            // 성공 메시지는 5초 후 자동으로 사라지게 설정
            if (type === 'success' || type === 'info') {
                setTimeout(() => {
                    messageBox.remove();
                }, 5000);
            }
        }
    });
})