$(document).ready(function() {
    $('body').show();
});



$('#uploadBtn').click(function(){

    if (!$("#post-text").val()) {
        showError("введите текст поста")
        return;
    }

    if ($('#xls-file-input').get(0).files.length === 0) {
        showError("выберите файл");
        return;
    }

    show_overlay();
    event.preventDefault();
    var form = $('#xls-form')[0];
    var data = new FormData(form);
    $("#uploadBtn").prop("disabled", true);
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/uploadInstagramXls",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
            if (data === "OK") {
                showSuccess("Загружено");
            } else {
                showException("Ошибка загрузки: " + data);
            }
            $("#uploadBtn").prop("disabled", false);
        },
        error: function (e) {
            showException("Неожиданная ошибка загрузки, земля пухом");
            console.log("ERROR : ", e);
            $("#uploadBtn").prop("disabled", false);
        }
    }).done(function(response) {
         hide_overlay();
    });

});

//$('#loadByInstagramRef').click(function(){
//    show_overlay();
//     $.ajax({
//       method: "POST",
//       contentType: "application/json",
//       url: "/loadFromInstagram",
//       data: JSON.stringify({instagramRef : $("#postref").val()}),
//       success: function(response) {
//                    },
//       error: function(xhr, ajaxOptions, thrownError) {
//         console.log(xhr);
//         console.log(ajaxOptions);
//         console.log(thrownError);
//                  }
//     }).done(function(response) {
//        hide_overlay();
//        //location.reload();
//     });
//});
