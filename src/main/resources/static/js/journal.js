$(document).ready(function() {
    $('body').show();
});

$(".post-row").click(function(){
    var dirtyHackId = parseInt($(this).attr("post-id"));
    window.location.href = "/record?strict=true&postId="+dirtyHackId;
})