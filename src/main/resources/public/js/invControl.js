        function generateTable() {
            //Create a HTML Table element.
            var table = document.createElement("TABLE");
            table.border = "1";

            //Get the count of columns.
            var columnCount = inventory[0].length;

            //Add the header row.
            var row = table.insertRow(-1);
            for (var i = 0; i < columnCount; i++) {
                var headerCell = document.createElement("TH");
                headerCell.innerHTML = inventory[0][i];
                row.appendChild(headerCell);
            }
            
            //Create empty header for delete
            var headerCell = document.createElement("TH");
            headerCell.innerHTML = "";
            row.appendChild(headerCell);

            //Add the data rows.
            for (var i = 1; i < inventory.length; i++) {
                row = table.insertRow(-1);
            	
            	//add data
                for (var j = 0; j < columnCount; j++) {
                    var cell = row.insertCell(-1);
                    cell.innerHTML = inventory[i][j] ;
                }
                
                //Create delete button for each row
                row = row.insertCell(-1);
    			row.innerHTML = "<input type='button' value='Remove Device' onClick='deleteRow(this);'>"; 
            }

            var invTable = document.getElementById("invTable");
            invTable.innerHTML = "";
            invTable.appendChild(table);
        } //end Generate Table
        
        function insert(){
			//Pull new data from form
			var ownerValue = document.getElementById('Owner').value;
			var manufacturerValue = document.getElementById('Manufacturer').value;
    		var modelValue = document.getElementById('Model').value;
    		var typeValue = document.getElementById('Type').value;
    		var ipValue = document.getElementById('IP').value;
    		var serialValue = document.getElementById('Serial').value;
    		var processorValue = document.getElementById('Processor').value;
    		var ramValue = document.getElementById('RAM').value;
    		var locationValue = document.getElementById('Location').value;
    		
    		//Add new data to Inventory
    		inventory.push ([ownerValue, manufacturerValue, modelValue, typeValue, ipValue, serialValue, processorValue, ramValue, locationValue]);
    		
    		//Refresh table
    		generateTable()
  		}// end insert
  		
  		function deleteRow(btn) {
    		var row = btn.parentNode.parentNode;
  			row.parentNode.removeChild(row);
		}// end deleteRow
