$(document).ready(function () {
    // 모달 관련 함수 정의
    function openModal() {
        $('#pop').css({"display": "block"});
    }

    function closeModal() {
        $('#pop').css({"display": "none"});
    }

    // 이벤트 리스너 등록 - 직접 요소를 선택하여 확인
    console.log("title_right 요소 수: " + $(".title_right").length);

    // 이벤트 리스너 연결
    $(document).on("click", ".title_right", openModal); // 동적 요소에도 작동
    $(document).on("click", "#close", closeModal);
});
