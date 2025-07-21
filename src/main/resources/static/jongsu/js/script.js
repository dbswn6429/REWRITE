$(document).ready(function () {

    /* txt_slide */
    var txt= $("#ts_mid div");
    var cnt=0;
    var max=txt.length-1;
    setInterval(next ,1500);
    
    function next(){
        if(txt.is(":animated")) return false;
        $(txt[cnt]).animate({"top":"-100%"}).siblings().css({"top":"100%"});
        cnt++;
        if(cnt>max) cnt=0;
        $(txt[cnt]).animate({"top":0});
        }
});