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
    <script type="text/javascript" src="includes/js/globalFields.js"></script>
  </head>
<body onload="onloadLogin()">
  <form id="loginform" >
	<div id='Layer1' style="width: 100%;height: 100%;">
	  <div id="logo"><img src="includes/images/cooplogo_84x84.png" /></div>
		<div id="banner">${caller.contextPath} login</div>
		<div id='loginError'>To use '${caller.contextPath}' you must login<br/>
			${caller.infomsg}</div>
		<div id="controls">
		  <table id="logintbl">
		  	<tr><td>Login Id</td><td>*</td><td><input id="loginid" value="${caller.email}" size="35" /></td></tr>
		  	<tr><td>Password</td><td>*</td><td><input type="password" id="psw" value="" size="35" /></td></tr>
		  	<tr><table class='passwordChange'>
		  		<tr><td>&nbsp;</td></tr>
		  		<tr><td>&nbsp;</td></tr>
			  	<tr><td>New Password</td><td>*</td><td><input type="password"id="newpsw" value="" size="35" /></td></tr>
			  	<tr><td>Confirm Password</td><td>*</td><td><input type="password" id="confpsw" value="" size="35" /></td></tr>
			    <tr><td colspan="2"><button class='passwordChange' onclick="return ajaxRestChgPsw()">Submit</button></td></tr>
		  	</table></tr>
	  		<tr class='loginbutton'><td colspan="2"><button class='loginbutton' onclick="return ajaxRestLogin()">login</button></td></tr>
		  	<tr><td>&nbsp;</td></tr>
		    <tr><td colspan="2"><button class='loginbutton' onclick="return chgPswButton('passwordChange')">change Password</button></td></tr>
		    <tr><td colspan="2"><button class='loginbutton' onclick="return loginForgotButton('myModal')">Forgot Id/Password</button></td></tr>
		  	<tr><td>&nbsp;</td></tr>
		    <tr><td colspan="2"><button class='loginbutton' onclick="return loginRegistrationButton()">Register</button></td></tr>
		  </table>
	    </div>
	  </div>
	  
	  <div id="myModal" class="modal">
		<div class="modal-content">
	  	  <b>forgot your login id</b><br/>
	  	  Enter your email:<input id="forgotId" type="text"/><br/>
	  	  and click <a href="javascript:ajaxRestForgotId('forgotId')">Here</a>
		  <br/>&nbsp;<hr><br/>
	  	  <b>forgot your password</b><br/>
	  	  Enter loginid:<input id="forgotPsw" type="text"/><br/>
	  	  and click <a href="javascript:ajaxRestForgotPsw('forgotPsw')">Here</a>
		  <br/>&nbsp;<br/><button style="left: -20px;position: relative;" class='loginbutton' onclick="return exitTask('myModal')">Exit</button>
		</div>
	  </div>
	  
  </form>
</body>
</html>
