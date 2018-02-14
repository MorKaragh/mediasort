$(document).ready(function() {
    $('#locationSelect').select2({
        tags: true
    });

    $('#themeSelect').select2({
        tags: true
    });

    reloadTagInput(function(){
        $('body').show();
    });

    $(".status").each(function(){
        var postBox = $(this).closest(".post-edit");
        var itemId = postBox.find(".item-id").val();
        markByStatus(itemId,$(this).val());
    });

});

function sendRecord(){
    var locationn = $( "#locationSelect option:selected" ).text();
//    var authorr = $( "#authorSelect option:selected" ).text();
//    var authorr = $("#editor").closest(".post-edit").find(".post-author").text();;
    var themeVar = $( "#themeSelect option:selected" ).text();
    var addText = $("#additionalText").val();
    var tagz = [];
    $('.tag-input .label').each(function(elem){
        tagz.push($(this).attr("data-tag"));
    })
    var cId = $("#editor").closest(".post-edit").find(".item-id").val()
    var desc = $('#comment').val();
    if (!valid(location, themeVar)){
        showError("внесите категорию и место!")
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
            commentId : cId,
            additionalText : addText
        }),
      success: function(response) {
        console.log("SEND RECORD  " + response);
                   },
      error: function(xhr, ajaxOptions, thrownError) {
        console.log(xhr);
        console.log(ajaxOptions);
        console.log(thrownError);
                 }
    }).done(function(response) {
        location.reload();
    });
}

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
//        if (respo.available != "true"){
//            showError(respo.error);
//            markByStatus(itemId,respo.status);
//        } else {
            if (respo.available != "true" && !["NO_PLACE","NO_THEME"].includes(respo.status)){
                markByStatus(itemId,respo.status);
                $(".post-edit").each(function(){
                    if (itemId === $(this).find(".item-id").val()){
                        $(this).find(".comment-text").attr('value',respo.recordText);
                        $(this).find(".comment-category").attr('value',respo.recordTheme);
                        $(this).find(".comment-location").attr('value',respo.recordLocation);
                        $(this).find(".comment-tags").attr('value',respo.recordTags);
                        $(this).find(".additionalText").attr('value',respo.additionalText);
                        fillEditor(itemId);
                    }
                });
                console.log("RESPO: " + response);
                showError(respo.error);
            } else {
                clearEditor();
            }
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
//        }
    });
}

function reloadTagInput(afterEffect){
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
        afterEffect();
    });
}

function clearEditor(){
    show_overlay();
    setSelect("#locationSelect","");
    setSelect("#themeSelect","");
    $("#comment").val("");
    $(".tag-input").val("");
    $("#additionalText").val("");
    reloadTagInput(function(){
        hide_overlay();
    });
}

function fillEditor (itemId){
    $(".post-edit").each(function(){
        if (itemId === $(this).find(".item-id").val()){
            show_overlay();
            var descr = $(this).find(".comment-text").val();
            var category = $(this).find(".comment-category").val();
            var location = $(this).find(".comment-location").val();
            var tags = $(this).find(".comment-tags").val();
            var addText = $(this).find(".additionalText").val();
            setSelect("#locationSelect",location);
            setSelect("#themeSelect",category);
            $("#comment").val(descr);
            $(".tag-input").val(tags);
            console.log("ADD TEXT: " + addText);
            $("#additionalText").val(addText);
            reloadTagInput(function(){
                hide_overlay();
            });
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

function markByStatus(itemId, status) {
    if (status === "FREE"){
        return;
    } else if (status === "IN_PROGRESS") {
        markAsHeld(itemId);
    } else if (status === "DONE") {
        markAsProcessed(itemId);
    } else {
        markAsTrash(itemId);
    }
}

function markAsHeld(itemId) {
    mark(itemId,"in-progress")
}

function markAsProcessed(itemId) {
    mark(itemId,"processed")
}

function markAsTrash(itemId) {
    mark(itemId,"trash")
}

function mark(itemId, status) {
    $(".item-id").each(function(){
        if ($(this).val() == itemId){
            $(this).closest(".post-edit").addClass(status);
        }
    });
}

$(".no-theme-btn").dblclick(function(){
    var itemId = $(this).closest(".post-edit").find(".item-id").val();
    setStatus(itemId,"NO_THEME");
    markAsTrash(itemId);
})

$(".no-place-btn").dblclick(function(){
    var itemId = $(this).closest(".post-edit").find(".item-id").val();
    setStatus(itemId,"NO_PLACE");
    markAsTrash(itemId);
})

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
    sendRecord();
})

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

function valid(location, theme){
    $( "#locationSelect option:selected" ).removeClass("inError");
    $( ".inptlbl" ).removeClass("inError");
    var err = false;
    if (location === '') {
            $( ".locationLbl" ).addClass("inError");
            err = true;
    }
    if (theme === '') {
            $( ".themeLbl" ).addClass("inError");
            err = true;
    }
    return !err;
}

