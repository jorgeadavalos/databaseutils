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
	<div class="snippets" title="mainNavigation.jsp"/>
	<div id="contentframe" class="content">
	  <div>
		<h3>Build programmatically connection to a new database provider</h3>
		<h3>fields with * are required</h3>
		<table id="database" border="1">
		  <tr><th colspan='4'>Source (From) Data source</th></tr>
		  <tr><th>URL</th><td>*</td><td><div title="jdbc:postgresql://hostname:1234/testDB"><input type="text" id="url" size="40"/></div></td></tr> 
		  <tr><th>user id</th><td>*</td><td><div title="database login user id"><input type="text" id="userid" value="" /></div></td></tr> 
		  <tr><th>Password</th><td>*</td><td><div title="database password"><input type="password" id="psw" value="" /></div></td></tr> 
		</table>
		<button type="button" onclick="bldConnection()"><b>submit</b></button>
	  </div>
	</div>
  </body>
</html>
