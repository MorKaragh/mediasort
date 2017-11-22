$(document).ready(function() {
    $('#locationSelect').select2({
        tags: true
    });

    $('#themeSelect').select2({
        tags: true
    });

    var taglist = [];
    $.ajax({
        url : "rectags",
        type : "get",
        success : function(result) {
            if (result != null){
                result.forEach(function(item, i, arr){
                     taglist.push({tag: item.name});
                })
            }
        },
        error: function() {
           alert("ERROR GETTING TAGS!");
        }
     }).done(function(response) {
        var tags = new Bloodhound({
            datumTokenizer: function(d) { return Bloodhound.tokenizers.whitespace(d.tag); },
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            local: taglist
        });
        tags.initialize();
        $('.tag-input').tagInput({
          tagDataSeparator: '|',
          allowDuplicates: false,
          typeahead: true,
          typeaheadOptions: {
              highlight: true
          },
          typeaheadDatasetOptions: {
            displayKey: 'tag',
            source: tags.ttAdapter()
          }
        });
        $('.date').datepicker({
            format: "dd.mm.yyyy"
        });
        $('body').show();
    });
});


$("#nextBtn").click(function(){
    show_overlay();
    window.location.href = "/record?postId="+$("#postId").val();
});

$('.post-edit-comment-box')
    .mouseenter( function() {
                   $(this).addClass("comment-box-selected");
                   $(this).find(".cancel-buttons").show();
                 } )
    .mouseleave( function() {
                   $(this).removeClass("comment-box-selected");
                   if(!$(this).hasClass("post-edit-active")){
                     $(this).find(".cancel-buttons").hide();
                   }
                 } )
    .click(function(e){
        if($(this).hasClass("post-edit-active")){
            return;
        }
        if ($(e.target).is(".btn-xs")){
            return;
        }
        var wantedId = $(this).find(".item-id").val();
        takeToWork(wantedId, $(this));

});

function takeToWork(itemId, elem) {
    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/takeToWork",
      data: JSON.stringify({
            commentId : itemId
        }),
      success: function(response) {
                   },
      error: function(xhr, ajaxOptions, thrownError) {
        console.log(xhr);
        console.log(ajaxOptions);
        console.log(thrownError);
                 }
    }).done(function(response) {
        var respo = JSON.parse(response);
        if (respo.available != "true"){
            showError(respo.error);
            markAsProcessed(itemId);
        } else {
            $("#editor").show();
            $("#editor").appendTo(elem.find(".post-comment"));
            $("#locationSelect").focus();
            $(".post-edit").each(function(){
                if ($(this).hasClass("post-edit-active")){
                    if (itemId != $(this).find(".item-id").val()){
                        release($(this).find(".item-id").val());
                    }
                    $(this).removeClass("post-edit-active");
                    $(this).find(".cancel-buttons").hide();
                }
            });
            elem.find(".cancel-buttons").show();
            $("#author-name").val($("#editor").closest(".post-edit-head").find(".post-author").html());
            elem.closest(".post-edit").addClass("post-edit-active");
            $('html, body').animate({
                    scrollTop: $("#editor").offset().top-200
                }, 300);
        }
    });
}

function release(itemId) {
    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/release",
      data: JSON.stringify({
            commentId : itemId
        }),
      success: function(response) {
                   },
      error: function(xhr, ajaxOptions, thrownError) {
        console.log(xhr);
        console.log(ajaxOptions);
        console.log(thrownError);
                 }
    }).done(function(response) {

    });
}

function markAsProcessed(itemId) {
    $(".item-id").each(function(){
        if ($(this).val() == itemId){
            $(this).closest(".post-edit").fadeOut(500);
        }
    });
}

$(".no-theme-btn").dblclick(function(){
    var itemId = $(this).closest(".post-edit").find(".item-id").val();
    setStatus(itemId,"NO_THEME");
    markAsProcessed(itemId);
})

$(".no-place-btn").dblclick(function(){
    var itemId = $(this).closest(".post-edit").find(".item-id").val();
    setStatus(itemId,"NO_PLACE");
    markAsProcessed(itemId);
})

function setStatus(itemId,sts){
    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/setStatus",
      data: JSON.stringify({
            commentId : itemId,
            status : sts
        }),
      success: function(response) {
                   },
      error: function(xhr, ajaxOptions, thrownError) {
        console.log(xhr);
        console.log(ajaxOptions);
        console.log(thrownError);
                 }
    }).done(function(response) {
        markAsProcessed(itemId);
    });
}

$('#locationSelect').on('select2:select', function (e) {
    $("#themeSelect").focus();
});

$('#themeSelect').on('select2:select', function (e) {
    $(".mab-jquery-taginput-input").focus();
});

$("#sendBtn").blur(function(){
    $("#locationSelect").focus();
})

$("#sendBtn").click(function(){
    var locationn = $( "#locationSelect option:selected" ).text();
//    var authorr = $( "#authorSelect option:selected" ).text();
//    var authorr = $("#editor").closest(".post-edit").find(".post-author").text();;
    var themeVar = $( "#themeSelect option:selected" ).text();
    var tagz = [];
    $('.tag-input .label').each(function(elem){
        tagz.push($(this).attr("data-tag"));
    })
    var cId = $("#editor").closest(".post-edit").find(".item-id").val()
    var desc = $('#comment').val();
    if (!valid(location, tagz)){
        showError("внесите тэги и место!")
        return;
    }
    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/sendRecord",
      data: JSON.stringify({
            tags : tagz,
            description: desc,
//            author: authorr,
            location: locationn,
            theme: themeVar,
            commentId : cId
        }),
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

})

function valid(location, tags){
    $( "#locationSelect option:selected" ).removeClass("inError");
    $( ".inptlbl" ).removeClass("inError");
    var err = false;
    if (location === '') {
            $( ".locationLbl" ).addClass("inError");
            err = true;
    }
    if (tags.length === 0){
        $( ".tagLbl" ).addClass("inError");
        err = true;
    }
    return !err;
}

function showError(errorMsg){
    $.notify({
    	// options
    	message: errorMsg
    },{
    	// settings
    	type: 'warning'
    });
}
