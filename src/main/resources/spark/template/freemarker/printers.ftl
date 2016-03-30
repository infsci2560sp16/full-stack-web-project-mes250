<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <link rel="stylesheet" type="text/css" href="stylesheets/inventory.css">
  <script type="text/javascript" src="http://code.jquery.com/jquery-1.12.0.min.js"></script>
  <script type="text/javascript" src="js/invControl.js"></script>
  <!--Used http://datatables.net for advanced table sort-->
  <script type="text/javascript" src="https://cdn.datatables.net/t/dt/jq-2.2.0,pdfmake-0.1.18,dt-1.10.11,b-1.1.2,b-html5-1.1.2/datatables.min.js"></script>
  <link rel="stylesheet" type="text/css" href="stylesheets/datatable.css"/>
</head>

<body>

<body>
	
    <div class="nav">
    <ul>
        <li><a href="login.html">Login</a></li>
        <li>
            <a href="inventory-list.html">Inventory</a>
            <ul>
                <li><a href="inventory-listXML.html">Inventory XML</a></li>
                <li><a href="add-inventory.html">Add Device</a></li>
                <li><a href="/printers">Printers</a></li>
                <li><a href="delete-inventory.html">Delete Devices</a></li>
                <li><a href="retire-inventory.html">Retire Devices</a></li>
            </ul>
        </li>
        <li><a href="contact-us.html">Contact Us</a></li>
    </ul>
	</div>

    <div class="header">
	<div class="header-shadow"></div>
		<div class="header-gradient">
		<br>
		<h1>Printers</h1>
		</div> <!-- header-gradient -->
	</div> <!-- header -->
	<br>
	<h2 id="tbldesc">All printers and their attributes are listed in the table below.  Sort columns by clicking on the column name.</h2>
    <br>
    
    <!-- Check day for toner orders -->
    <#assign aDateTime = .now>
    <#assign day = aDateTime?string["EEEE"]>
    <#if day == "Thursday">
    <div class="alert">Check Printer Toner Quantity for Orders</div>
    <br>
    <#else>
    </#if>
    
    <!-- Create table using FreeMarker -->
    <table id="inventory" class="display" aria-describedby="tbldesc">
    	<thead>
    		<tr><th>Owner</th><th>Manufacturer</th><th>Model</th><th>IP Address</th><th>Serial</th><th>Location</th></tr>
    	</thead>
    	<tbody>
    		<#list results?chunk(6) as row>
    		<tr>
    			<#list row as cell>
    				<td>${cell}</td>
    			</#list>
    		</tr>
    		</#list>
    	</tbody>
    </table>
    
    
   	<!-- Make table Dynamic using DataTables -->
    <script>
    $(document).ready(function() {
    $('#inventory').DataTable()
    });
	</script>


</body>
</html>
