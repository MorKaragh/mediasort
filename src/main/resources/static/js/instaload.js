$(document).ready(function() {
  $('#summernote').summernote();
  $('.note-toolbar').hide();
  $('body').show();
});

$("#sendBtn").click(function(){
    var dt = $('#summernote').summernote('code');
    console.log(JSON.stringify({data : dt}));
    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/parseInstagram",
      data: JSON.stringify({data : dt}),
      success: function(response) {
                   },
      error: function(xhr, ajaxOptions, thrownError) {
        console.log(xhr);
        console.log(ajaxOptions);
        console.log(thrownError);
                 }
    }).done(function(response) {

    });
});
