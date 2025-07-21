document.addEventListener('DOMContentLoaded',function () {
    const searchForm = document.querySelector('.main_nav form');
    const searchInput = document.querySelector('.search_sec input');
    const searchButton = document.querySelector('.search_foot');
    const searchIcon = document.querySelector('.search_foot img');

    //검색
    function  performSearch(){
        const searchTerm = searchInput.value.trim();
        if(searchTerm.length < 1){
            alert('검색어는 1글자 이상 입력해주세요.');
            return;
        }
        window.location.href =`/search?keyword=${encodeURIComponent(searchTerm)}`;
    }

    //검색버튼
    searchButton.addEventListener('click',function (){
        event.preventDefault();
        performSearch();
    });

    //검색아이콘
    searchIcon.addEventListener('click',function (){
        event.preventDefault();
        performSearch();
    });

    searchInput.addEventListener('keypress',function (){
        if(event.ket === 'Enter'){
            event.preventDefault();
            performSearch();
        }
    });

    // 폼 제출 이벤트도 처리
    searchForm.addEventListener('submit', function() {
        event.preventDefault();
        performSearch();
    });
})