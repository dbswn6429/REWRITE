// $(document).ready(function () {
//
//     //간단한 파일 목록 표시 스크립트 (선택적)
//
//     const fileInput = document.getElementById('fileAttachment');
//     const fileListDisplay = document.getElementById('file-list');
//
//     if (fileInput && fileListDisplay) {
//         fileInput.addEventListener('change', function () {
//             let fileNames = [];
//             for (let i = 0; i < this.files.length; i++) {
//                 fileNames.push(this.files[i].name);
//             }
//             fileListDisplay.textContent = fileNames.join(', ');
//             if (fileNames.length > 0) {
//                 fileListDisplay.style.display = 'inline'; // 파일 선택 시 보이도록
//             } else {
//                 fileListDisplay.style.display = 'none'; // 파일 없을 때 숨김
//             }
//         });
//     }
// })

$(document).ready(function (){
    document.addEventListener('DOMContentLoaded', function() {
        // 파일 목록 표시 기능
        const fileInput = document.getElementById('fileAttachment');
        const fileListDisplay = document.getElementById('file-list');

        fileInput.addEventListener('change', function() {
            let fileNames = [];
            for (let i = 0; i < this.files.length; i++) {
                fileNames.push(this.files[i].name);
            }
            fileListDisplay.textContent = fileNames.join(', ');
        });

        // 폼 제출 처리
        const form = document.getElementById('inquiryForm');
        const submitBtn = document.getElementById('submitBtn');

        submitBtn.addEventListener('click', submitInquiry);

        function submitInquiry() {
            // 입력 유효성 검사
            const category = document.getElementById('inquiryType').value;
            const title = document.getElementById('inquirySubject').value;
            const content = document.getElementById('inquiryContent').value;

            if (!category || !title || !content) {
                alert('모든 필수 항목을 입력해주세요.');
                return;
            }

            // FormData 객체 생성 및 데이터 추가
            const formData = new FormData();
            formData.append('category', category);
            formData.append('title', title);
            formData.append('content', content);

            // 파일 추가
            const file = fileInput.files[0];
            if (file) {
                formData.append('file', file); // 'files' 대신 'file'로 이름 변경
            }

            // REST API로 데이터 전송
            fetch('/api/qna', {
                method: 'POST',
                body: formData
                // 멀티파트 폼데이터를 보낼 때는 Content-Type 헤더를 설정하지 않음
                // 브라우저가 자동으로 boundary 설정
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('서버 오류: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    // 성공 처리
                    alert('문의글 업로드 성공했습니다')
                    window.location.href = '/'
                    //  window.location.href = '/user/qnalist' 로 보내서, 자기가 작성한 문의글 볼 수 있게 나중에 설정
                    // 폼 초기화
                    form.reset();
                    fileListDisplay.textContent = '';

                    // 선택적: 성공 후 페이지 이동
                    // setTimeout(() => { window.location.href = '/qna/list'; }, 2000);
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('문의 접수 실패: ' + error.message);
                });
        }
    });
})