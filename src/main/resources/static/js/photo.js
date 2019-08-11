$(document).ready( function() {
    	$(document).on('change', '.btn-file :file', function() {
		var input = $(this),
			label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
		input.trigger('fileselect', [label]);
		});

		$('.btn-file :file').on('fileselect', function(event, label) {

		    var input = $(this).parents('.input-group').find(':text'),
		        log = label;

		    if( input.length ) {
		        input.val(log);
		    } else {
		        if( log ) alert(log);
		    }

		});
		function readURL(input) {
		    if (input.files && input.files[0]) {
		        var reader = new FileReader();

		        reader.onload = function (e) {
		            $('#img-upload').attr('src', e.target.result);
		            image = e.target.result;
		        }

		        reader.readAsDataURL(input.files[0]);
		    }
		}

		$("#imgInp").change(function(){
		    readURL(this);
		});
	});

$(document).ready( function() {
	$("#tagBox").tagging();
	$('#locationSelect').select2({
        tags: true
    });
	$('#authorSelect').select2({
        tags: true
    });
});

$("#sendBtn").click(function(){
    var tagz = $("#tagBox").tagging( "getTags" );
    var reader = new FileReader();
    var file    = document.querySelector('input[type=file]').files[0];
    var locationn = $( "#locationSelect option:selected" ).text();
    var authorr = $( "#authorSelect option:selected" ).text();
    reader.readAsDataURL(file);

    reader.onload = function (e) {
        $.ajax({
              method: "POST",
              contentType: "application/json",
              url: "/send",
              data: JSON.stringify({tags : tagz, img: e.target.result, author: authorr, location: locationn}),
              success: function(response) {
                           },
              error: function(xhr, ajaxOptions, thrownError) {
                             console.log(xhr);
                         }
            }).done(function(response) {
            });
    }
})
