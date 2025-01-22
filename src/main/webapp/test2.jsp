<!DOCTYPE html>
<html> 
<head>
    <script type="text/javascript" src="includes/js/restAjax.js"></script>
    <script type="text/javascript" src="includes/js/database.js"></script>
    <script type="text/javascript" src="includes/js/globalFunctions.js"></script>
    <script type="text/javascript" src="includes/js/globalFields.js"></script>
    <link href="includes/css/modal.css" type="text/css" rel="stylesheet" ></link>
</head>  
  <body> 
	<form>
	  <input id="uploadfile" type="file" name="file" multiple onchange="addfilesize(this)">
	  <table>
		<tr class='loginbutton'><td colspan="2"><button class='loginbutton' onclick="return uploadFiles()">continue</button></td></tr>
	  </table>
	  <input type="submit">
	</form>
  </body> 
</html>
