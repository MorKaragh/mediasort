$(document).ready(function() {
    $('.date').datepicker({
        format: "dd.mm.yyyy"
    });

    fillChart();

    $('body').show();
});

var collapseMode = false;

$("#collapseBtn").click(function(){
    collapseMode = !collapseMode;
    if (!collapseMode) {
        $(".reportComplain").each(function(){
            $(this).removeClass("collapsing-block");
        })
                $("#confirmCollapse").hide();
                $("#collapseComplainInput").hide();
    } else {
        $("#confirmCollapse").show();
        $("#collapseComplainInput").show();
    }
    $(this).removeClass("btn-warning");
    $(this).removeClass("btn-danger");
    $(this).addClass(collapseMode ? "btn-danger" : "btn-warning");
    $(this).html(collapseMode ? "Отменить" : "Схлопывание жалоб")
})


$(".reportComplain").click(function(){

    if (collapseMode) {
        if ($(this).hasClass("collapsing-block")) {
            $(this).removeClass("collapsing-block");
        } else {
            $(this).addClass("collapsing-block");
        }
        $("#collapseComplainInputFld").val($(this).find(".com-description").text());

        return;
    }

    var theme = $(this).siblings(".reportThemeShort").val();
    var descr = $(this).find(".com-description").text();
    var address = $(this).find(".com-address").text();
    var location = $(this).find(".com-location").val();
    var startDate = $("#startDate").find("input").val();
    var endDate = $("#endDate").find("input").val();

    var d = JSON.stringify({
        endDate : endDate,
        startDate: startDate,
        location: location,
        theme: theme,
        description : descr,
        address : address
    });

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
        labelsArray.push($(this).find(".reportThemeShort").val() + "  ("
            + ( parseInt($(this).find(".vkcnt").val()) +  parseInt($(this).find(".instagramcnt").val()))
        + ")  ");
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
                label: 'Вконтакте',
                data: vkCounts,
                backgroundColor: 'rgba(102, 102, 255, 0.2)',
                borderColor: 'rgba(102, 102, 255, 1)',
                borderWidth: 1
            },
            {
                label: 'Инстаграм',
                data: instagramCounts,
                backgroundColor: 'rgba(0, 153, 255, 0.2)',
                borderColor: 'rgba(0, 153, 255, 1)',
                borderWidth: 1
            }]
        },
        options: {
            plugins: {
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
                text:"Всего жалоб " + total + ", из них " + totalVk + " \"Вконтакте\", " + totalInstagram + " \"Инстаграм\""
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