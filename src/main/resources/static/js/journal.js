$(document).ready(function() {
    $('body').show();
});

$(".journal-post").click(function(){
    var dirtyHackId = parseInt($(this).find(".idinput").val());
    window.location.href = "/record?strict=true&postId="+dirtyHackId;
})