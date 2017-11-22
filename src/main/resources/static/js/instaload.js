$(document).ready(function() {
  $('#summernote').summernote();
  $('.note-toolbar').hide();
  $('#saveBtn').hide();
  $('#backBtn').hide();
  $('body').show();
});

$("#sendBtn").click(function(){
    show_overlay();
    var dt = $('#summernote').summernote('code');
    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/parseInstagram",
      data: JSON.stringify({data : dt}),
      success: function(response) {},
      error: function(xhr, ajaxOptions, thrownError) {
        console.log(xhr);
        console.log(ajaxOptions);
        console.log(thrownError);
      }
    }).done(function(response) {
        $(".parsed").html(response);
        $(".editor").hide();
        $('#sendBtn').hide();
        $('#saveBtn').show();
        $('#backBtn').show();
        hide_overlay();
    });
});

$('#saveBtn').click(function(){
    show_overlay();
     var dt = $('#summernote').summernote('code');
     $.ajax({
       method: "POST",
       contentType: "application/json",
       url: "/savePost",
       data: JSON.stringify({data : dt}),
       success: function(response) {
                    },
       error: function(xhr, ajaxOptions, thrownError) {
         console.log(xhr);
         console.log(ajaxOptions);
         console.log(thrownError);
                  }
     }).done(function(response) {
        location.reload();
     });
 });

$('#backBtn').click(function(){
    $(".editor").show();
    $('#sendBtn').show();
    $(".parsed").html("");
    $('#saveBtn').hide();
    $('#backBtn').hide();
});

