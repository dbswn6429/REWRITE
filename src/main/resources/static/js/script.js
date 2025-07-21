$(document).ready(function () {
    /* txt_slide */
    var txt = $("#ts_mid div");
    var cnt = 0;
    var max = txt.length - 1;

    setInterval(next, 1500);

    function next() {
        if (txt.is(":animated")) return false;
        $(txt[cnt]).animate({"top": "-100%"}).siblings().css({"top": "100%"});
        cnt++;
        if (cnt > max) cnt = 0;
        $(txt[cnt]).animate({"top": 0});
    } // 닫는 중괄호 추가

    function openModal() {
        $('#pop').css({"display": "block"});
        // $('#semi_wrap').css({"position":"sticky"});
        // $('section').css({"margin-top":"0"});
    } // 닫는 중괄호 추가

    // 모달 닫기 함수
    function closeModal() {
        $('#pop').css({"display": "none"});
        // $('#semi_wrap').css({"position":"fixed"});
        // $('section').css({"margin-top":"101px"});
    } // 닫는 중괄호 추가

    // 이벤트 리스너 연결
    $(".title_right").on("click", openModal); // openModal로 변경
    $("#close").on("click", closeModal); // closeModal로 변경

});


/* infinity rolling */

$(document).ready(function () {
    setFlowBanner();

    // 상품 카드 클릭 이벤트 처리
    $(document).on('click', '.flow_banner .card', function () {
        const prodId = $(this).data('prod-id');
        if (prodId) {
            window.location.href = `/prod/prodDetail?prodId=${prodId}`;
        }
    });
});


function setFlowBanner() {
    const $wrap = $('.flow_banner'); // 배너 전체를 감싸는 요소
    const $list = $('.flow_banner .list'); // 배너 안의 리스트 (ul 등)
    let wrapWidth = 0; // $wrap의 가로 크기 저장용
    let listWidth = 0; // $list의 가로 크기 저장용
    const displayTime = 2; // 각 아이템을 보여줄 시간 (초 단위)

    // 원본 리스트 복제본 (기준용)
    const $baseClone = $list.clone();

    // 페이지 로드 시 실행
    $(window).on('load', function () {
        $wrap.append($baseClone.clone()); // 초기 복제 리스트 1개 추가
        flowBannerAct(); // 배너 롤링 시작
    });

    // 창 크기 변경 시 실행
    $(window).on('resize', function () {
        const wrapWidth = $wrap.width(); // 현재 배너 영역 너비
        const listCount = $wrap.find('.list').length; // 리스트 개수
        const listWidth = $wrap.find('.list').width(); // 리스트 너비

        // 리스트 총 너비가 배너 너비의 2배보다 크면 다시 세팅하지 않음
        if (listCount * listWidth > wrapWidth * 2) {
            return;
        }
        flowBannerAct(); // 배너 다시 설정
    });

    // 배너 롤링을 설정하는 함수
    function flowBannerAct() {
        // 이전 애니메이션 초기화
        $wrap.find('.list').css('animation', 'none'); // 애니메이션 제거
        $wrap.find('.list').slice(2).remove(); // 기존 복제 리스트 제거

        // 현재 너비 값 측정
        wrapWidth = $wrap.width();
        listWidth = $list.width();

        // 속도 계산 (전체 길이 / 총 시간)
        const speed = listWidth / ($list.find('div').length * displayTime);

        // 필요한 만큼 리스트 복제하여 추가
        const listCount = Math.ceil(wrapWidth * 2 / listWidth);
        for (let i = 2; i < listCount; i++) {
            const $newClone = $baseClone.clone(); // 원본 기준으로 복제
            $wrap.append($newClone);
        }

        // 애니메이션 적용
        $wrap.find('.list').css({
            'animation': `${listWidth / speed}s linear infinite flowRolling`
        });
    }

    // 마우스를 올리면 애니메이션 일시정지
    $wrap.on('mouseenter', function () {
        $wrap.find('.list').css('animation-play-state', 'paused');
        // 마우스를 떼면 다시 실행
    }).on('mouseleave', function () {
        $wrap.find('.list').css('animation-play-state', 'running');
    });


    document.addEventListener('DOMContentLoaded', function () {
        // flow_banner 내 카드에 이벤트 리스너 추가
        const flowBannerCards = document.querySelectorAll('.flow_banner .card');

        flowBannerCards.forEach(card => {
            const mediaContainer = card.querySelector('.media-container');
            const hoverText = mediaContainer ? mediaContainer.querySelector('.hover-text') : null;

            // 마우스 오버 이벤트
            card.addEventListener('mouseenter', () => {
                if (hoverText) hoverText.classList.remove('active');
            });

            // 마우스 아웃 이벤트
            card.addEventListener('mouseleave', () => {
                if (hoverText) hoverText.classList.add('active');
            });

            // 카드 클릭 이벤트 - 상세 페이지로 이동
            card.addEventListener('click', function () {
                const prodId = this.getAttribute('data-prod-id');
                if (prodId) {
                    window.location.href = `/prod/prodDetail?prodId=${prodId}`;
                }
            });
        });

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
                const bottomValue = windowHeight - footerTop + 100; // 100px 여유 공간
                topButton.style.transition = '0.01s';
                topButton.style.bottom = bottomValue + 'px';
            } else {
                // 기본 위치로 복원
                topButton.style.bottom = '100px';
            }
        });


    });


// 방법 2: 순수 JavaScript만 사용
//     document.addEventListener('DOMContentLoaded', function() {
//         // 스크롤 이벤트 리스너 추가
//         window.addEventListener('scroll', function() {
//             const topButton = document.querySelector('.top');
//             const footer = document.querySelector('footer');
//
//             // footer 위치 계산
//             const footerTop = footer.getBoundingClientRect().top;
//             const windowHeight = window.innerHeight;
//
//             // footer가 화면에 보이기 시작하면 버튼 위치 조정
//             if (footerTop < windowHeight) {
//                 // footer까지의 거리에 따라 버튼 위치 조정
//                 const bottomValue = windowHeight - footerTop + 100;
//                 topButton.style.transition = '0.01s';
//                 topButton.style.bottom = bottomValue + 'px';
//             } else {
//                 // 기본 위치로 복원
//                 topButton.style.bottom = '100px';
//             }
//         });
//     });
}




