
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