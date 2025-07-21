
$(document).ready(function (){
    document.addEventListener('DOMContentLoaded', function() {
        // 모든 미디어 컨테이너에 호버 이벤트 설정
        const containers = document.querySelectorAll('.media-container');

        containers.forEach(container => {
            const video = container.querySelector('.product-video');

            if(video && video.dataset.src) {
                // 호버 시 비디오 로드 및 재생
                container.addEventListener('mouseenter', function() {
                    if(!video.getAttribute('src')) {
                        video.setAttribute('src', video.dataset.src);
                    }
                    video.play();
                });

                // 호버 해제 시 비디오 정지
                container.addEventListener('mouseleave', function() {
                    video.pause();
                    video.currentTime = 0;
                });
            }
        });
    });
})