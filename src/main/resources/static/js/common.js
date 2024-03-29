function show_overlay() {
    var modalLoading = '<div class="modal" id="pleaseWaitDialog" data-backdrop="static" data-keyboard="false role="dialog">\
        <div class="modal-dialog">\
            <div class="modal-content">\
                <div class="modal-header">\
                    <h4 class="modal-title">Подождите...</h4>\
                </div>\
                <div class="modal-body">\
                    <div class="progress">\
                      <div class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar"\
                      aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width:100%; height: 40px">\
                      </div>\
                    </div>\
                </div>\
            </div>\
        </div>\
    </div>';
    $(document.body).append(modalLoading);
    $("#pleaseWaitDialog").modal("show");
}


function hide_overlay() {
    $("#pleaseWaitDialog").modal("hide");
}

function showError(errorMsg){
    $.notify({
    	message: errorMsg
    },{
    	type: 'warning'
    });
}

function showException(errorMsg){
    $.notify({
    	message: errorMsg
    },{
    	type: 'danger'
    });
}

function showSuccess(errorMsg){
    $.notify({
    	message: errorMsg
    },{
    	type: 'success'
    });
}

function setSelect(id, value){
    $(id).find('option').each(function(){
        if ($(this).text() === value) {
            $(this).attr("selected","selected");
        } else {
            $(this).removeAttr("selected");
        }
        $(id).select2({
            tags: true
        });
    })
}
