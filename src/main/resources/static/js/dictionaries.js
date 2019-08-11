$(document).ready(function() {
    $('#locationSelect').select2({
        tags: true
    });

    $('#themeSelect').select2({
        tags: true
    });

    $('#newLocationSelect').select2({
        tags: true
    });

    $('#newThemeSelect').select2({
        tags: true
    });

    $('body').show();
});

$("#sendBtn").dblclick(function(){
    show_overlay();
    sendRecord();
})

$("#rollbackBtn").dblclick(function(){
    show_overlay();
    rollback();
})


function rollback(){
    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/rollbackDicts",
      data: JSON.stringify({
                            rollback : "true"
                       }),
      success: function(response) {
        console.log("CHANGE_DICTS  " + response);
                   },
      error: function(xhr, ajaxOptions, thrownError) {
        console.log(xhr);
        console.log(ajaxOptions);
        console.log(thrownError);
                 }
    }).done(function(response) {
        hide_overlay();
        location.reload();
    });
}


function sendRecord(){
    var oldLocation = $( "#locationSelect option:selected" ).text();
    var oldTheme = $( "#themeSelect option:selected" ).text();
    var newLocation = $( "#newLocationSelect option:selected" ).text();
    var newTheme = $( "#newThemeSelect option:selected" ).text();


    var sendData = JSON.stringify({
                               oldLocation : oldLocation,
                               oldTheme: oldTheme,
                               newLocation: newLocation,
                               newTheme: newTheme
                           });

    console.log(sendData);

    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/changeDicts",
      data: sendData,
      success: function(response) {
        console.log("CHANGE_DICTS  " + response);
                   },
      error: function(xhr, ajaxOptions, thrownError) {
        console.log(xhr);
        console.log(ajaxOptions);
        console.log(thrownError);
                 }
    }).done(function(response) {
        hide_overlay();
        location.reload();
    });
}
