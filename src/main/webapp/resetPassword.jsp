<!DOCTYPE html>
<html> 
  <head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>h:search products</title>
	<link rel="stylesheet" type="text/css" href="includes/css/login.css" />
	<link rel="stylesheet" type="text/css" href="includes/css/modal.css" />
	<script type="text/javascript" src="includes/js/login.js"></script>
	<script type="text/javascript" src="includes/js/projects.js"></script>
	<script type="text/javascript" src="includes/js/restAjax.js"></script>
	<script type="text/javascript" src="includes/js/globalFunctions.js"></script>
	<script type="text/javascript" src="includes/js/snippets.js"></script>
    <script type="text/javascript" src="/includes/js/globalFields.js"></script>
  </head>
<body onload="onloadResetPSW()">
  <form id="resetpsw" >
	<div id='Layer1'>
	  <div id="logo"><img src="includes/images/cooplogo_84x84.png" /></div>
		<div id="banner">Reset Password for user:  </div>
		<div id='loginError'>Reset Password for service: </div>
		<div id="controls">
		  <table id="logintbl">
			<tr><td>New Password</td><td>*</td><td><input type="password"id="newpsw" value="" size="35" /></td></tr>
			<tr><td>Confirm Password</td><td>*</td><td><input type="password" id="confpsw" value="" size="35" /></td></tr>
		    <tr><td colspan="2"><button class='loginbutton' onclick="return resetPswButton()">Reset Password</button></td></tr>
		  </table>
	    </div>
	  </div>
  </form>
</body>
</html>
