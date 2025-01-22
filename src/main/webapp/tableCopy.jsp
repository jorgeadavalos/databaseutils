<!DOCTYPE html>
<html>
  <head>
	<title>steps to copy tables</title>
    <script type="text/javascript" src="includes/js/restAjax.js"></script>
    <script type="text/javascript" src="includes/js/database.js"></script>
    <script type="text/javascript" src="includes/js/globalFunctions.js"></script>
    <script type="text/javascript" src="includes/js/globalFields.js"></script>
    <script type="text/javascript" src="includes/js/snippets.js"></script>
    <script type="text/javascript" src="includes/js/menuSupport.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>

    <link href="includes/css/modal.css" type="text/css" rel="stylesheet" ></link>
    <link href="includes/css/scrollNav.css" type="text/css" rel="stylesheet" ></link>
    <link href="includes/css/frames.css" type="text/css" rel="stylesheet" ></link>
  </head>  
  <body onload="snippets()"> 
	<div class="snippets" title="mainNavigation.jsp"></div>
	<div id="contentframe" class="content">
		<div id="myContent" class="modal-content">
		  <h3 id='infomsg'></h3><br/>
		  <h4>Copy from tableA to tableB via Spring RESTFUL </h4>
		  <h4>fields with asterics * are required </h4>
			<table id="inputs" border="0">	
			  <tr><td>From Table name:</td><td>*</td><td><input type="text" id="fromTable"/></td></tr>  
			  <tr><td>To   Table name:</td><td></td><td><input type="text" id="toTable"/></td></tr>  
			  <tr><td>From database name:</td><td></td><td><input type="text" id="fromDatabase" title="default to active dataSource"/></td></tr>  
			  <tr><td>To database name:</td><td></td><td><input type="text" id="toDatabase" title="default to active dataSource"/></td></tr>  
		  	  <tr><td>Build DataSource:</td><td>*</td><td><button type="button" onclick="showMyModal('newConnModal')">Click Here!</button></td></tr>  
			</table>
			<div style="padding-top: 10px;">
				<button type="button" onclick="exitTask('myModal')" id="modalButton"><b>exit</b></button>
				<button type="button" onclick="verifyTables('fromTable','toTable','fromDatabase','toDatabase')" id="modalButton2"><b>continue</b></button>
			</div>
		</div>

		<div id="json404Modal" class="modal">
			<div id="myContent" class="modal-content2">
				<h4 id='json404Infomsg' style="padding-top: 11px;">more info to continue Spring RESTFUL </h4><br/>
				<h3>If you would like to change the primary key select fields bellow or just hit continue</h3>
				<h3>Please select field(s). Selected order is important</h3>
				<table id="table404inputs" border="1">
				</table>
				<div style="padding-top: 10px;">
					<button type="button" onclick="exitTask('json404Modal')" id="modalButton"><b>exit</b></button>
					<button type="button" onclick="resetFields('table404inputs')" id="modalButton3"><b>reset</b></button>
					<button type="button" onclick="bldTableCall('fromTable','toTable','fromDatabase','toDatabase','table404inputs')" id="modalButton2"><b>continue</b></button>
				</div>
			</div>
		</div>
		<div id="newConnModal" class="modal">
			<div id="myContent" class="modal-content2">
				<h4 id='newConnInfomsg' style="padding-top: 11px;">Enter fields to build a new dataSource  </h4>
				<h4>For hints mouse over an input field</h4><br/>
				<h3></h3>
				<table id="newConnTable" border="0">
				  <tr><th colspan='4'>Source (From) Data source</th></tr>
				  <tr><th>URL</th><td></td><td><div title="jdbc:postgresql://hostname:1234/testDB"><input type="text" size='40' id="fromurl" value=""/></div></td></tr> 
				  <tr><th>user id</th><td></td><td><div title="database login user id"><input type="text" id="fromuserid" size='40' value="" /></div></td></tr> 
				  <tr><th>Password</th><td></td><td><div title="database password"><input type="password" id="frompsw" size='40' value="" /></div></td></tr> 
			 	  <tr><th colspan='4'>Destination (To) Data source</th></tr>
			      <tr><th>URL</th><td></td><td><div title="jdbc:postgresql://hostname:1234/testDB"><input type="text" size='40' id="tourl" value=""/></div></td></tr> 
			  	  <tr><th>user id</th><td></td><td><div title="database login user id"><input type="text" id="touserid" size='40' value="" /></div></td></tr> 
			  	  <tr><th>Password</th><td></td><td><div title="database password"><input type="password" id="topsw" size='40' value="" /></div></td></tr> 
				</table>
				<div style="padding-top: 10px;">
					<button type="button" onclick="exitTask('newConnModal')" id="modalButton"><b>exit</b></button>
					<button type="button" onclick="bldConnection()" id="modalButton2"><b>continue</b></button>
				</div>
			</div>
		</div>
		<div id="jarUploadModal" class="modal">
			<div id="myContent" class="modal-content2">
				<h4 id='newConnInfomsg' style="padding-top: 11px;">Upload jar-file to access the class to create dataSource  </h4><br/>
				  <input id="uploadfile" type="file" name="file" multiple onchange="addfilesize(this)">
				  <table>
					<tr class='loginbutton'><td colspan="2"><button class='loginbutton' onclick="return jarUploadFiles()">continue</button></td></tr>
				  </table>
			</div>
		</div>
	</div>
  </body>
</html>
