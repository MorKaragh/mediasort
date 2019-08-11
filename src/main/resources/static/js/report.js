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
        console.log("reported");
        console.log(response);
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
        vkCounts.push(parseInt($(this).find(".vkcnt").val()))
        instagramCounts.push(parseInt($(this).find(".instagramcnt").val()))
    })

    var totalVk = vkCounts.reduce((a, b) => a + b, 0);
    var totalInstagram = instagramCounts.reduce((a, b) => a + b, 0);
    var total = totalInstagram + totalVk;

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
            plugins: {
                    // Change options for ALL labels of THIS CHART
                    datalabels: {
                        color: 'black',
                        font: {
                            size: '10'
                        },
                        display: function(context) {
                                    var index = context.dataIndex;
                                    var value = context.dataset.data[index];
                                    return value > 0 ? true : false;
                                 }
                    }
                },
            title:{
                display:true,
                text:"Всего жалоб: " + total + ", из них VK: " + totalVk + ", INSTAGRAM: " + totalInstagram
            },
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero:true
                    },
                    stacked: true,
                }],
                xAxes: [{
                    stacked: true
                }]
            }
        }
    });
}