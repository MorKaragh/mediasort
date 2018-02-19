$(document).ready(function() {
    $('.date').datepicker({
        format: "dd.mm.yyyy"
    });

    fillChart();

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

function fillChart(){

    var labelsArray = [];
    var vkCounts = [];
    var instagramCounts = [];

    $(".reportrow").each(function(){
        labelsArray.push($(this).find(".reportTheme").text());
        vkCounts.push($(this).find(".vkcnt").val())
        instagramCounts.push($(this).find(".instagramcnt").val())
    })
    console.log(labelsArray);


    var ctx = document.getElementById("chart").getContext('2d');
    var myChart = new Chart(ctx, {
        type: 'horizontalBar',
        data: {
            labels: labelsArray,
            datasets: [{
                label: 'VK',
                data: vkCounts,
                backgroundColor: 'rgba(102, 102, 255, 0.2)',
                borderColor: 'rgba(102, 102, 255, 1)',
                borderWidth: 1
            },
            {
                label: 'Instagram',
                data: instagramCounts,
                backgroundColor: 'rgba(0, 153, 255, 0.2)',
                borderColor: 'rgba(0, 153, 255, 1)',
                borderWidth: 1
            }]
        },
        options: {
            title:{
                display:true,
                text:"Всего жалоб: 100"
            },
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero:true
                    },
                    stacked: true
                }],
                xAxes: [{
                    stacked: true
                }]
            }
        }
    });
}