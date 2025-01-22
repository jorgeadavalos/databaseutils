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
	<form action="REST/uploadFile" method="post" enctype="multipart/form-data">
  	  <label for="comment">Comment:</label>
	  <input id="uploadfile" type="file" name="file" multiple>
  	  <button type="submit">Submit</button>
	</form>

  </body> 
</html>
