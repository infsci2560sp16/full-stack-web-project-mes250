function getData() {
    $.ajax({
        //url: 'localhost:5000/api/invlist',
    	url: 'https://stark-earth-7570.herokuapp.com/api/invlist',
        type: 'GET',
    	dataType: 'json',
        success : handleData
    });
}

function handleData(data) {
    var trHTML = '';
        $.each(data, function (i, item) {
            trHTML += '<tr><td>' + item.OWNER + '</td><td>' + item.DEVICE_NAME + '</td><td>' + item.MANUFACTURER + '</td><td>' + item.MODEL + '</td><td>' + item.TYPE_DESC + '</td><td>' + item.IP_ADDRESS + '</td><td>' + item.SERIAL + '</td><td>' + item.PROCESSOR + '</td><td>' + item.RAM + '</td><td>' + item.LOCATION + '</td></tr>';
        });
        $('#inventory').append(trHTML);
}

function getDataXML() {
    $.ajax({
        //url: 'localhost:5000/api/invlistXML',
        url: 'https://stark-earth-7570.herokuapp.com/api/invlistXML',
    	type: 'GET',
    	dataType: 'XML',
        success : handleDataXML
    });
}

function handleDataXML(data) {
    $(data).find('device').each(function(){	
    var owner = $(this).find('owner').text();
    var device_name = $(this).find('device_name').text();
    var manufacturer = $(this).find('manufacturer').text();
    var model = $(this).find('model').text();
    var type = $(this).find('type').text();
    var ip_address = $(this).find('ip_address').text();
    var serial = $(this).find('serial').text();
    var processor = $(this).find('processor').text();
    var ram = $(this).find('ram').text();
    var location = $(this).find('location').text();
    $('<tr></tr>').html('<td>'+owner+'</td><td>'+device_name+'</td><td>'+manufacturer+'</td><td>'+model+'</td><td>'+type+'</td><td>'+ip_address+'</td><td>'+serial+'</td><td>'+processor+'</td><td>'+ram+'</td><td>'+location+'</td>').appendTo('#inventory');
   });
}

function deleteRow(btn) {
	var row = btn.parentNode.parentNode;
	row.parentNode.removeChild(row);
}// end deleteRow

function retireDevice(btn) {
    var row = btn.parentNode.parentNode;
    row.style.backgroundColor = '#ff8080';
    alert("Device has been marked as retired.");
}

function redirectMe() {
	var x = document.getElementById("form-1").elements.namedItem("loginUsername").value;
    if (x === null || x === "") {
        alert("User Name must be filled out");
        return false;
    }
    x = document.getElementById("form-1").elements.namedItem("loginPassword").value;
    if (x === null || x === "") {
        alert("Password must be filled out");
        return false;
    }
  	window.location.replace("inventory-list.html");
  	return false;
}// end redirectME

function redirectMe2() {
	var x = document.getElementById("form-2").elements.namedItem("createUsername").value;
    if (x === null || x === "") {
        alert("User Name must be filled out");
        return false;
    }
  	window.location.replace("contact-us.html");
  	return false;
}// end redirectME2