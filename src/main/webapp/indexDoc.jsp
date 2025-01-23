<!DOCTYPE html> 
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:ui="http://java.sun.com/jsf/facelets">
	<head>
	</head>
	<body>
	  <h2>Copy table across database or across database managers.</h2><br/>
	  <ul style="padding-left: 10px;">
		<li><a href="#overview">Overview</a></li>
		<li><a href="#configuration">JSF server configuration</a></li>
		<li><a href="#download">Download jar file</a></li>
		<li><a href="#top">Top</a></li>
	  </ul>
	  <br/>
	  <div id="overview" style="padding:15px;width:55%;">
		<h3>Overview</h3><br/>
		Spring boot start service<br/>
		This DB-utility allows for tables to be copied to same database, different database and to different database manager. At the moment it has been tested
		with <b>Postgresql</b> and <b>Mysql.</b><br/>
		You could do the following tasks:
		<ul style="padding-left: 30px;">
			<li>Create a new database</li>
			<li>copy a table from posgres to Mysql and viceverse.</li>
			<li>the new table will be cloned from the database you are copying from.</li>
		</ul>
		
		<br/>
		<div  id="download" style="padding:15px;">
		  <h3>Download war file</h3>
		  click  <a href="javascript:downloadJar()">here</a> to download war-file
		  <br/>
		</div>
	  </div>
	</body>
</html>