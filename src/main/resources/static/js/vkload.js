$(document).ready(function() {
  $('body').show();
});

$("#moreBtn").click(function(){
    show_overlay();
    offset = parseInt($("#offset").val()) + 10;
    window.location.href = "/vkload?offset="+offset;
});