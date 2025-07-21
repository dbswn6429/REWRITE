$(document).ready(function(){
    // 하트 아이콘 요소
    const heart = document.getElementById('heart');
    let liked = false;

    // 상품 ID 가져오기 - HTML에 추가 필요
    const productId = $('.price_heart').data('product-id');
    // 또는 URL에서 추출 (예: /product/123 형식이라면)
    // const productId = window.location.pathname.split('/').pop();

    console.log('상품 ID:', productId);

    // 페이지 로드 시 찜 상태 확인
    $.ajax({
        url: '/wishlist/check',
        type: 'GET',
        data: { prodId: productId },
        dataType: 'json',
        success: function(response) {
            console.log('찜 상태 확인 응답:', response);
            liked = response.isWishlisted;
            updateHeartUI(liked);
        },
        error: function(err) {
            console.error('찜 상태 확인 오류:', err);
            updateHeartUI(false);
        }
    });

    // 하트 클릭 이벤트 (하나로 통합)
    heart.addEventListener('click', function() {
        console.log('하트 클릭됨, 상품 ID:', productId);
        // 서버 통신
        $.ajax({
            url: '/wishlist/toggle',
            type: 'POST',
            data: { prodId: productId },
            dataType: 'json',
            success: function(response) {
                console.log('찜 상태 변경 응답:', response);
                liked = response.isWishlisted;
                updateHeartUI(liked);
            },
            error: function(err) {
                console.error('찜하기 오류:', err);
            }
        });
    });

    // 하트 UI 업데이트 함수
    function updateHeartUI(isLiked) {
        console.log('하트 UI 업데이트:', isLiked);
        if (isLiked) {
            heart.classList.remove('fa-heart-o');
            heart.classList.add('fa-heart');
            heart.style.color = 'red';
        } else {
            heart.classList.remove('fa-heart');
            heart.classList.add('fa-heart-o');
            heart.style.color = 'black';
        }
    }

    // 이미지+동영상 슬라이더 (수정된 코드)
    const mediaData = document.getElementById('mediaData');

    // 미디어 데이터 가져오기 및 null/빈 값 필터링
    const mediaList = mediaData.dataset.media.split(',')
        .map(item => item.trim())
        .filter(item => item && item !== 'null' && item !== 'undefined');

    // 슬라이드 버튼 요소
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const mediaWrapper = document.getElementById('mediaWrapper');

    // 현재 인덱스 초기화
    let currentIndex = 0;

    // 슬라이드 버튼 표시 여부 결정
    if (mediaList.length <= 1) {
        prevBtn.style.display = 'none';
        nextBtn.style.display = 'none';
    }

    // 미디어 요소 생성 함수 (기존 코드 유지)
    function createMediaElement(src) {
        if (src.match(/\.(mp4|webm|ogg)$/i)) {
            const video = document.createElement('video');
            video.src = src;
            video.width = 400;
            video.height = 400;
            video.controls = true;
            video.muted = true;
            video.autoplay = true;
            video.playsInline = true;
            video.style.objectFit = "cover";
            video.style.borderRadius = "10px";
            video.currentTime = 0;
            setTimeout(()=>{video.play().catch(()=>{});},100);
            return video;
        } else if (src.match(/\.(jpg|jpeg|png|gif|bmp|webp)$/i)) {
            const img = document.createElement('img');
            img.src = src;
            img.style.width = "400px";
            img.style.height = "400px";
            img.style.objectFit = "cover";
            img.style.borderRadius = "10px";
            return img;
        } else {
            const div = document.createElement('div');
            div.innerText = "지원하지 않는 파일 형식입니다.";
            return div;
        }
    }

    // 슬라이드 전환 함수 (기존 코드 유지)
    function slideTo(newIndex, direction) {
        if (newIndex === currentIndex) return;
        const oldMedia = mediaWrapper.firstChild;
        const newMedia = createMediaElement(mediaList[newIndex].trim());

        // 방향에 따라 위치 설정
        let oldTarget, newStart;
        if (direction === 'left') {
            oldTarget = -400;
            newStart = 400;
        } else {
            oldTarget = 400;
            newStart = -400;
        }

        newMedia.style.position = 'absolute';
        newMedia.style.left = newStart + "px";
        newMedia.style.top = "0";
        newMedia.style.transition = "left 0.5s cubic-bezier(.77,0,.18,1)";
        mediaWrapper.appendChild(newMedia);

        if (oldMedia) {
            oldMedia.style.position = 'absolute';
            oldMedia.style.left = "0px";
            oldMedia.style.top = "0";
            oldMedia.style.transition = "left 0.5s cubic-bezier(.77,0,.18,1)";
            setTimeout(() => {
                oldMedia.style.left = oldTarget + "px";
                newMedia.style.left = "0px";
            }, 10);
            setTimeout(() => {
                if (mediaWrapper.contains(oldMedia)) mediaWrapper.removeChild(oldMedia);
                newMedia.style.position = '';
                newMedia.style.left = '';
                newMedia.style.top = '';
                newMedia.style.transition = '';
            }, 510);
        } else {
            newMedia.style.position = '';
            newMedia.style.left = '';
            newMedia.style.top = '';
            newMedia.style.transition = '';
        }

        currentIndex = newIndex;
    }

    // 초기 미디어 표시 함수
    function showInitialMedia() {
        mediaWrapper.innerHTML = "";
        // 미디어 목록이 비어있지 않은 경우에만 미디어 표시
        if (mediaList.length > 0) {
            const media = createMediaElement(mediaList[currentIndex].trim());
            mediaWrapper.appendChild(media);
        } else {
            // 미디어가 없는 경우 기본 이미지나 메시지 표시
            const noMedia = document.createElement('div');
            noMedia.innerText = "이미지가 없습니다";
            noMedia.style.width = "400px";
            noMedia.style.height = "400px";
            noMedia.style.display = "flex";
            noMedia.style.alignItems = "center";
            noMedia.style.justifyContent = "center";
            noMedia.style.background = "#f5f5f5";
            noMedia.style.borderRadius = "10px";
            mediaWrapper.appendChild(noMedia);
        }
    }

    // 버튼 이벤트 리스너 (기존 코드 유지)
    prevBtn.addEventListener('click', () => {
        const newIndex = (currentIndex - 1 + mediaList.length) % mediaList.length;
        slideTo(newIndex, 'right');
    });

    nextBtn.addEventListener('click', () => {
        const newIndex = (currentIndex + 1) % mediaList.length;
        slideTo(newIndex, 'left');
    });

    // 초기 미디어 표시
    showInitialMedia();

    // prodDetail.js
    $('#bumpBtn').on('click', function (event) {
        event.preventDefault();

        if (!productId) {
            alert('상품 정보를 찾을 수 없습니다.');
            return;
        }
        $.ajax({
            url: '/prod/bump',
            type: 'POST',
            data: {prodId: productId},
            success: function (response) {
                if (response === 'ok') {
                    alert('끌어올리기가 완료되었습니다!');
                    window.location.href = '/prod/prodDetail?prodId=' + productId;
                } else if (response === 'forbidden') {
                    alert('본인 글만 끌어올릴 수 있습니다.');
                } else if (response === 'unauthorized') {
                    alert('로그인이 필요합니다.');
                } else {
                    alert('알 수 없는 오류가 발생했습니다: ' + response);
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert('끌어올리기에 실패했습니다. (' + textStatus + ': ' + errorThrown + ')');
            }
        });
    });

    // // 끌어올리기 버튼 클릭 이벤트
    // $('#bumpBtn').on('click', function() {
    //     if (!productId) {
    //         alert('상품 정보를 찾을 수 없습니다.');
    //         return;
    //     }
    //     $.ajax({
    //         url: '/prod/bump',
    //         type: 'POST',
    //         data: { prodId: productId },
    //         success: function(response) {
    //             if (response === 'ok') {
    //                 alert('끌어올리기가 완료되었습니다!');
    //                 // 페이지 이동 코드 추가
    //                 window.location.href = '/prod/prodDetail?prodId=' + productId;
    //             } else if (response === 'forbidden') {
    //                 alert('본인 글만 끌어올릴 수 있습니다.');
    //             } else if (response === 'unauthorized') {
    //                 alert('로그인이 필요합니다.');
    //                 // 선택적으로 로그인 페이지로 이동
    //                 // window.location.href = '/user/login';
    //             } else {
    //                 alert('알 수 없는 오류가 발생했습니다.');
    //             }
    //         },
    //         error: function() {
    //             alert('끌어올리기에 실패했습니다.');
    //         }
    //     });
    // });

    // $('#bumpBtn').on('click', function() {
    //     if (!productId) {
    //         alert('상품 정보를 찾을 수 없습니다.');
    //         return;
    //     }
    //     $.ajax({
    //         url: '/prod/bump', // 서버의 끌어올리기 처리 URL
    //         type: 'POST',
    //         data: { prodId: productId },
    //         success: function(response) {
    //             if (response === 'ok') {
    //                 alert('끌어올리기가 완료되었습니다!');
    //                 // 작성일자 텍스트를 즉시 변경 (예시: #regDate가 작성일자 표시하는 요소)
    //                 const now = new Date();
    //                 const formatted = now.getFullYear() + '-' +
    //                     String(now.getMonth()+1).padStart(2,'0') + '-' +
    //                     String(now.getDate()).padStart(2,'0') + ' ' +
    //                     String(now.getHours()).padStart(2,'0') + ':' +
    //                     String(now.getMinutes()).padStart(2,'0');
    //                 $('#regDate').text(formatted);
    //             } else if (response === 'forbidden') {
    //                 alert('본인 글만 끌어올릴 수 있습니다.');
    //             } else if (response === 'unauthorized') {
    //                 alert('로그인이 필요합니다.');
    //             } else {
    //                 alert('알 수 없는 오류가 발생했습니다.');
    //             }
    //         },
    //         error: function() {
    //             alert('끌어올리기에 실패했습니다.');
    //         }
    //     });
    // });

    const prodStatus = /*[[${product.prodStatus}]]*/ '';

// 장바구니 버튼
    document.getElementById('cartBtn')?.addEventListener('click', function(e) {
        if (prodStatus === '판매완료') {
            e.preventDefault();
            alert('판매완료된 상품입니다.');
        }
    });

// 끌어올리기 버튼
    document.getElementById('bumpBtn')?.addEventListener('click', function(e) {
        if (prodStatus === '판매완료') {
            e.preventDefault();
            alert('판매완료된 상품입니다.');
        }
    });

});