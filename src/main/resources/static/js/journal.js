$(document).ready(function() {
    $('body').show();
});

$(".post-row").click(function(e){
    if ($(e.target).is(".refresh-button") || $(e.target).is(".glyphicon-refresh")){
        return;
    }
    var dirtyHackId = parseInt($(this).attr("post-id"));
    window.location.href = "/record?strict=true&postId="+dirtyHackId;
})

var progressBar = '<div class="progress"><div class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width:100%; height: 40px"></div></div>';

$('.refresh-button').click(function(){
    var netId = $(this).attr("net-id");
    var link =  $(this).attr("post-link");
    var parent = $(this).parent();
    show_overlay();
    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/fefreshPost",
      data: JSON.stringify({
            postNetId : netId,
            postNetLink : link
        }),
      success: function(response) {
            console.log(response);
            parent.siblings('.jrnl-post-processed').html(response.processed);
            parent.siblings('.jrnl-post-unprocessed').html(response.unprocessed);
            parent.siblings('.jrnl-post-text').html(response.text);
            showSuccess("пост обновлен");
         },
      error: function(xhr, ajaxOptions, thrownError) {
            console.log(xhr);
            console.log(ajaxOptions);
            console.log(thrownError);
            hide_overlay();
            showException("ошибка при обновлении!");
        }
    }).done(function(response) {
        hide_overlay();
    });
})