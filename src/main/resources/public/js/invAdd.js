function validateForm(){
    //Pull new data from form
	var manufacturerValue = document.getElementById('manufacturer').value;
	var modelValue = document.getElementById('model').value;
	var typeValue = document.getElementById('type').value;
	var serialValue = document.getElementById('serial').value;
	var processorValue = document.getElementById('processor').value;
	var ramValue = document.getElementById('ram').value;
        var check;
	
	//Check data input
	if (manufacturerValue === null || manufacturerValue === "") {
        alert("Manufacturer must be filled out");
        return;
    }
    if (modelValue === null || modelValue === "") {
        alert("Model must be filled out");
        return;
    }
    if (typeValue === null || typeValue === "") {
        alert("Type must be filled out");
        return;
    }
    if (serialValue === null || serialValue === "") {
        alert("Serial must be filled out");
        return;
    }
    if (processorValue === null || processorValue === "") {
        alert("Processor must be filled out");
        return;
    }
    if (ramValue === null || ramValue === "") {
        alert("RAM must be filled out");
        return;
    }
}

$.ajaxSetup({
        contentType: "application/json; charset=utf-8",
        dataType: "json"
});
$(document).ready(function(){
        $('#input').click(function() {   
            var send = $('#form').serializeJSON();
            console.log(send);
            
            $.ajax({
                url: "http://localhost:5000/api/invadd",
                type: "POST",
                data: send,
                error: function(xhr, error) {
                       alert('Error!  Status = ' + xhr.status + ' Message = ' + error);
                },
                success: function(data) {
                        alert("Device added successfully.");

                }
            });
            return false; 
        });
});