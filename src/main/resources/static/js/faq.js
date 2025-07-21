$(document).ready(function () {
    $(".faq-list li").click(function () {
        const answer = $(this).find(".answer");

        // 다른 답변 닫기
        $(".faq-list .answer").not(answer).slideUp();
        $(".faq-list li .arrow").not($(this).find(".arrow")).text("▼");

        // 클릭한 항목 토글
        answer.slideToggle();

        // 화살표 방향 토글
        const arrow = $(this).find(".arrow");
        if (arrow.text() === "▼") {
            arrow.text("▲");
        } else {
            arrow.text("▼");
        }
    });
});