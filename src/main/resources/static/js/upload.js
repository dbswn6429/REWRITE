function uploadImage() {
    const fileInput = document.getElementById('file');
    const file = fileInput.files[0];

    if (!file) {
        alert('파일을 선택해주세요.');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('folderPath', 'profile_img'); // 이

    fetch('/api/files', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 오류: ' + response.status);
            }
            return response.text();
        })
        .then(url => {
            document.getElementById('result').style.display = 'block';
            document.getElementById('imageUrl').textContent = url;
            document.getElementById('imagePreview').src = url;
        })
        .catch(error => {
            console.error('Error:', error);
            alert('업로드 실패: ' + error.message);
        });
}