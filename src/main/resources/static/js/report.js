$(document).ready(function() {
    $('.date').datepicker({
        format: "dd.mm.yyyy"
    });

    $('body').show();
});

$(".reportComplain").click(function(){
    var theme = $(this).siblings(".reportTheme").text();
    var descr = $(this).find(".com-description").text();
    var location = $(this).find(".com-location").text();
    var startDate = $("#startDate").find("input").val();
    var endDate = $("#endDate").find("input").val();

    var d = JSON.stringify({
        endDate : endDate,
        startDate: startDate,
        location: location,
        theme: theme,
        description : descr
    });

    console.log(d);

    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/reportedit",
      data: d,
      success: function(response) {
                   },
      error: function(xhr, ajaxOptions, thrownError) {
        console.log(xhr);
        console.log(ajaxOptions);
        console.log(thrownError);
                 }
    }).done(function(response) {
        $("#reportedit").html(response);
        $('html, body').animate({
            scrollTop: $("#reportedit").offset().top-200
        }, 300);
    });
});
