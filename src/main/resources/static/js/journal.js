$(document).ready(function() {
    $('body').show();
});

$(".journal-post").click(function(){
    var dirtyHackId = parseInt($(this).find(".idinput").val())+1; //Господи, прости меня
    window.location.href = "/record?postId="+dirtyHackId;
})