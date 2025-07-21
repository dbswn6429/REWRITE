$(document).ready(function () {

    //간단한 파일 목록 표시 스크립트 (선택적)

    const fileInput = document.getElementById('fileAttachment');
    const fileListDisplay = document.getElementById('file-list');

    if (fileInput && fileListDisplay) {
        fileInput.addEventListener('change', function () {
            let fileNames = [];
            for (let i = 0; i < this.files.length; i++) {
                fileNames.push(this.files[i].name);
            }
            fileListDisplay.textContent = fileNames.join(', ');
            if (fileNames.length > 0) {
                fileListDisplay.style.display = 'inline'; // 파일 선택 시 보이도록
            } else {
                fileListDisplay.style.display = 'none'; // 파일 없을 때 숨김
            }
        });
    }
})