function getData() {
    $.ajax({
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


function generateTableRetire() {
	//Create a HTML Table element
	var table = document.createElement("TABLE");
	table.border = "1";
	table.id = "inventory";
	table.summary = "This table lists all devices in the inventory with a delete button";

	//Get the count of columns
	var columnCount = inventory[0].length;

	//Add the header row
	var row = table.insertRow(-1);
	var headerCell;
	
	//Insert data for each header column
	for (var i = 0; i < columnCount; i++) {
		headerCell = document.createElement("TH");
		headerCell.innerHTML = inventory[0][i];
		row.appendChild(headerCell);
	}
	
	//Create empty header for delete column
	headerCell = document.createElement("TH");
	headerCell.innerHTML = "";
	row.appendChild(headerCell);

	//Add the data rows
	for (i = 1; i < inventory.length; i++) {
		row = table.insertRow(-1);
		
		//add data cells
		for (var j = 0; j < columnCount; j++) {
			var cell = row.insertCell(-1);
			cell.innerHTML = inventory[i][j] ;
		}
		
		//Create delete button cell for each row
		row = row.insertCell(-1);
		row.innerHTML = "<input type='button' value='Retire Device' onClick='retireDevice(this);'>"; 
	}

	var invTable = document.getElementById("invTable");
	invTable.innerHTML = "";
	invTable.appendChild(table);
} //end Generate Retire Table

function generateTableDel() {
	//Create a HTML Table element
	var table = document.createElement("TABLE");
	table.border = "1";
	table.id = "inventory";
	table.summary = "This table lists all devices in the inventory with a delete button";

	//Get the count of columns
	var columnCount = inventory[0].length;

	//Add the header row
	var row = table.insertRow(-1);
	var headerCell;
	
	//Insert data for each header column
	for (var i = 0; i < columnCount; i++) {
		headerCell = document.createElement("TH");
		headerCell.innerHTML = inventory[0][i];
		row.appendChild(headerCell);
	}
	
	//Create empty header for delete column
	headerCell = document.createElement("TH");
	headerCell.innerHTML = "";
	row.appendChild(headerCell);

	//Add the data rows
	for (i = 1; i < inventory.length; i++) {
		row = table.insertRow(-1);
		
		//add data cells
		for (var j = 0; j < columnCount; j++) {
			var cell = row.insertCell(-1);
			cell.innerHTML = inventory[i][j] ;
		}
		
		//Create delete button cell for each row
		row = row.insertCell(-1);
		row.innerHTML = "<input type='button' value='Remove Device' onClick='deleteRow(this);'>"; 
	}

	var invTable = document.getElementById("invTable");
	invTable.innerHTML = "";
	invTable.appendChild(table);
} //end Generate Delete Table

function insert(){
	//Pull new data from form
	var ownerValue = document.getElementById('Owner').value;
	var devicenameValue = document.getElementByID('DeviceName').value;
	var manufacturerValue = document.getElementById('Manufacturer').value;
	var modelValue = document.getElementById('Model').value;
	var typeValue = document.getElementById('Type').value;
	var ipValue = document.getElementById('IP').value;
	var serialValue = document.getElementById('Serial').value;
	var processorValue = document.getElementById('Processor').value;
	var ramValue = document.getElementById('RAM').value;
	var locationValue = document.getElementById('Location').value;
	
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
	
	//Add new data to Inventory
	inventory.push ([ownerValue, manufacturerValue, modelValue, typeValue, ipValue, serialValue, processorValue, ramValue, locationValue]);
	
	//Refresh table
	generateTable();
}// end insert

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