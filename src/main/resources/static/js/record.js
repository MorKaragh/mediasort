$(document).ready(function() {
    $('#locationSelect').select2({
        tags: true
    });
    $('#authorSelect').select2({
        tags: true
    });
    $('#themeSelect').select2({
        tags: true
    });

    var taglist = [];
    $.ajax({
        url : "rectags",
        type : "get",
        async: false,
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
     });

    var tags = new Bloodhound({
        datumTokenizer: function(d) { return Bloodhound.tokenizers.whitespace(d.tag); },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        local: taglist
    });

    tags.initialize();

    $('.tag-input').tagInput({
      // tags separator
      tagDataSeparator: '|',
      // allow duplicate tags
      allowDuplicates: false,
      // enable typehead.js
      typeahead: true,
      // tyhehead.js options
      typeaheadOptions: {
          highlight: true
      },
      // typehead dataset options
      typeaheadDatasetOptions: {
        displayKey: 'tag',
        source: tags.ttAdapter()
      }
    });

    $('body').show();
    $("#authorSelect").focus();

    $('.date').datepicker({
        format: "dd.mm.yyyy"
    });

});


$('#authorSelect').on('select2:select', function (e) {
    $("#locationSelect").focus();
});
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
    var authorr = $( "#authorSelect option:selected" ).text();
    var themeVar = $( "#themeSelect option:selected" ).text();
    var tagz = [];
    $('.tag-input .label').each(function(elem){
        tagz.push($(this).attr("data-tag"));
    })
    var desc = $('#comment').val();
    if (!valid(location, tagz)){
        showError("внесите тэги и место!")
        return;
    }
    console.log(JSON.stringify({tags : tagz, description: desc, author: authorr, location: locationn, theme: themeVar}));
    $.ajax({
      method: "POST",
      contentType: "application/json",
      url: "/sendRecord",
      data: JSON.stringify({tags : tagz, description: desc, author: authorr, location: locationn, theme: themeVar}),
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
