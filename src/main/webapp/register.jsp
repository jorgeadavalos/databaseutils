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
<body onload="onloadRegister()">
  <form id="registerform" >
	<div id='Layer1'>
	  <div id="logo"><img src="includes/images/cooplogo_84x84.png" /></div>
		<div id="banner" style="white-space: nowrap;">!{caller.contextPath} registration</div>
		<div id="banner2" style="white-space: nowrap;">* Required field</div>
		<div id='loginError' style="white-space: nowrap;">Registration form for: '!{caller.contextPath}'<br/>
			!{caller.infomsg}</div>
		<div id="controls">
		  <table id="registertbl">
		  	<tr><td>Login Id</td><td>*</td><td><input id="loginid" value="!{callee.loginid}" size="35" /></td></tr>
		  	<tr><td>Password</td><td>*</td><td><input type="password" id="password" value="" size="35" /></td></tr>
		  	<tr><td>Verify password</td><td>*</td><td><input type="password" id="vpassword" value="" size="35" /></td></tr>
		  	<tr><td>&nbsp;</td></tr>
		  	<tr><td>First name</td><td>&nbsp;</td><td><input id="firstname" value="" size="35" /></td></tr>
		  	<tr><td>Last name</td><td>&nbsp;</td><td><input id="lastname" value="" size="35" /></td></tr>
	  		<tr class='loginbutton'><td colspan="2"><button class='loginbutton' onclick="return ajaxRestRegistration()">continue</button></td></tr>
		  </table>
	    </div>
	  </div>
  </form>
</body>
</html>
