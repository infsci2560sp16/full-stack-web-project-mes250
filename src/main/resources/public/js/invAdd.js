$.ajaxSetup({
    contentType: "application/json; charset=utf-8",
    dataType: "json"
});

$(document).ready(function(){
    $('#input').click(function() { 
        //Pull new data from form
        var manufacturerValue = document.getElementById('manufacturer').value;
        var modelValue = document.getElementById('model').value;
        var typeValue = document.getElementById('type').value;
        var serialValue = document.getElementById('serial').value;
        var processorValue = document.getElementById('processor').value;
        var ramValue = document.getElementById('ram').value;

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

        //Convert form data to JSON
        var obj = $('#form').serializeJSON();
        var send = JSON.stringify(obj);

        //output JSON for verification
        console.log(send);

        $.ajax({
            //url: "http://localhost:5000/api/invadd",
            url: 'https://stark-earth-7570.herokuapp.com/api/invadd', 
            type: "POST",
            datatype: "json",
            data: send,
            error: function(xhr, error) {
                   alert('Error!  Status = ' + xhr.status + ' Message = ' + error);
            },
            success: function(data) {
                    alert("Device added successfully.");
                    window.location.href='/inventory-list.html';

            }
        });//end AJAX
        return false; 
    });//end click function
});//end ready function