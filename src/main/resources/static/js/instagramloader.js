$(document).ready(function() {
    $('body').show();
});



$('#loadByInstagramRef').click(function(){
    show_overlay();
     $.ajax({
       method: "POST",
       contentType: "application/json",
       url: "/loadFromInstagram",
       data: JSON.stringify({instagramRef : $("#postref").val()}),
       success: function(response) {
                    },
       error: function(xhr, ajaxOptions, thrownError) {
         console.log(xhr);
         console.log(ajaxOptions);
         console.log(thrownError);
                  }
     }).done(function(response) {
        hide_overlay();
        //location.reload();
     });
});
