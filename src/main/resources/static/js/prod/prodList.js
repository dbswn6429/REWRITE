document.addEventListener('DOMContentLoaded', function() {
    // 모든 카드 요소 선택 (미디어 컨테이너가 아닌 카드 전체)
    const cards = document.querySelectorAll('.card');

    // 각 카드에 이벤트 리스너 추가
    cards.forEach(card => {
        const mediaContainer = card.querySelector('.media-container');
        const video = mediaContainer ? mediaContainer.querySelector('.prod-video') : null;
        const hoverText = mediaContainer ? mediaContainer.querySelector('.hover-text') : null;
        let previewTimeout = null;

        // 비디오가 있는 경우에만 이벤트 리스너 추가
        if (video) {
            // 비디오 소스 설정 (지연 로딩)
            const videoSrc = video.getAttribute('data-src');
            if (videoSrc) {
                // 소스 요소 생성 및 추가
                const source = document.createElement('source');
                source.src = videoSrc;
                source.type = 'video/mp4';
                video.appendChild(source);
            }

            // 마우스 진입 이벤트 - 카드 전체에 적용
            card.addEventListener('mouseenter', () => {
                // 비디오 시작 위치 설정 (1초부터)
                video.currentTime = 1;
                video.playbackRate = 0.5; // 50% 속도로 재생

                // 비디오 재생
                const playPromise = video.play();

                // 재생 프로미스 처리
                if (playPromise !== undefined) {
                    playPromise.then(() => {
                        // 컨테이너에 비디오 활성화 클래스 추가
                        mediaContainer.classList.add('video-active');
                        // 호버 텍스트 숨기기
                        if (hoverText) hoverText.classList.remove('active');

                        // 4초 후 자동 정지
                        previewTimeout = setTimeout(() => {
                            video.pause();
                            mediaContainer.classList.remove('video-active');
                            if (hoverText) hoverText.classList.add('active');
                        }, 4000);
                    }).catch(error => {
                        console.error('비디오 재생 실패:', error);
                    });
                }
            });

            // 마우스 이탈 이벤트 - 카드 전체에 적용
            card.addEventListener('mouseleave', () => {
                // 타임아웃 정리
                if (previewTimeout) {
                    clearTimeout(previewTimeout);
                    previewTimeout = null;
                }

                // 비디오 정지 및 초기화
                video.pause();
                video.currentTime = 0;

                // 비디오 숨기기
                mediaContainer.classList.remove('video-active');
                // 호버 텍스트 표시
                if (hoverText) hoverText.classList.add('active');
            });
        }

        // 카드 클릭 이벤트 - 상세 페이지로 이동 (미디어 컨테이너 예외처리 제거)
        card.addEventListener('click', function() {
            // 상품 ID를 사용하여 상세 페이지로 이동
            const prodId = this.getAttribute('data-prod-id');
            if (prodId) {
                window.location.href = `/prod/prodDetail?prodId=${prodId}`;
            }
        });
    });

    // 페이지 맨 위로 스크롤하는 함수
    window.scrollToTop = function() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    };

    document.querySelectorAll('.heart-container i').forEach(function(heart) {
        heart.addEventListener('click', function(event) {
            event.stopPropagation(); // 카드 클릭 이벤트와 분리
            const prodId = this.getAttribute('data-prod-id');
            const icon = this;
            // AJAX 요청 예시 (URL은 실제 찜 토글 API로 변경)
            $.ajax({
                url: '/wishlist/toggle',
                type: 'POST',
                data: { prodId: prodId },
                dataType: 'json',
                success: function(response) {
                    if (response.isWishlisted) {
                        icon.classList.remove('fa-heart-o');
                        icon.classList.add('fa-heart');
                        icon.style.color = 'red';
                    } else {
                        icon.classList.remove('fa-heart');
                        icon.classList.add('fa-heart-o');
                        icon.style.color = '#bbb';
                    }
                },
                error: function() {
                    alert('로그인이 필요합니다.');
                }
            });
        });
    });



});

// 드롭다운 토글
function toggleDropdown(event) {
    event.stopPropagation(); // 다른 곳 클릭 시 닫히는 것 방지
    document.querySelector('.dropdown-content').classList.toggle('show');
}

// 옵션 선택 시
function selectSort(text, value) {
    document.querySelector('.dropbtn_content').innerText = text;
    document.querySelector('.dropdown-content').classList.remove('show');
    // 정렬 적용 (페이지 이동)
    const url = new URL(window.location.href);
    url.searchParams.set('sortBy', value);
    window.location.href = url.toString();
}

function changeSellerSort(label, sortBy) {
    const urlParams = new URLSearchParams(window.location.search);
    const uid = urlParams.get('uid');
    if (!uid) {
        alert('판매자 정보가 없습니다.');
        return;
    }
    window.location.href = `/prod/sellerProdList?uid=${uid}&sortBy=${sortBy}`;
}

// 바깥 클릭 시 드롭다운 닫기
window.onclick = function(event) {
    if (!event.target.matches('.dropbtn')) {
        var dropdowns = document.getElementsByClassName("dropdown-content");
        for (var i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
};



// 페이지 로드 시 현재 정렬 기준 표시
window.onload = function() {
    const params = new URLSearchParams(window.location.search);
    const sortBy = params.get('sortBy');
    let text = "최신순";
    if (sortBy === "latest") text = "최신순";
    else if (sortBy === "views") text = "조회수순";
    else if (sortBy === "priceAsc") text = "가격낮은순";
    else if (sortBy === "priceDesc") text = "가격높은순";
    document.querySelector('.dropbtn_content').innerText = text;
};


// 스크롤 이벤트 리스너 추가
window.addEventListener('scroll', function() {
    const topButton = document.querySelector('.top');
    const footer = document.querySelector('footer'); // footer 선택자에 맞게 수정

    // footer 위치 계산
    const footerTop = footer.getBoundingClientRect().top;
    const windowHeight = window.innerHeight;

    // footer가 화면에 보이기 시작하면 버튼 위치 조정
    if (footerTop < windowHeight) {
        // footer까지의 거리에 따라 버튼 위치 조정
        const bottomValue = windowHeight - footerTop + 100; // 20px은 여유 공간
        topButton.style.transition = '0.01s';
        topButton.style.bottom = bottomValue + 'px';
    } else {
        // 기본 위치로 복원
        // topButton.style.transition = '0.3s';
        topButton.style.bottom = '100px';

    }
});


