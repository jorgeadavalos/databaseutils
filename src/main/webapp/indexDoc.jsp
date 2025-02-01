<!DOCTYPE html> 
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:ui="http://java.sun.com/jsf/facelets">
	<head>
	</head>
	<body>
		<h2>DatabaseUtils.</h2>
		<h2>Copy table across database or across database managers.</h2><br/>
	  <ul id='anchors' style="padding-left: 10px;">
		<li><a href="#overview" onclick='anchorTag(this)'>Overview</a></li>
		<li><a href="#download" onclick='anchorTag(this)'>Download war file</a></li>
		<li><a href="#execute" onclick='anchorTag(this)'>Instruction to execute spring boot service</a></li>
		<li><a href="#top" onclick='anchorTag(this)'>Top</a></li>
	  </ul>
	  <br/>
	  <div style="padding:15px;width:55%;">
		<h3 id="overview">Overview</h3>
		Spring boot start service<br/>
		This DB-utility allows for tables to be copied to same database, different database and to different database manager. At the moment it has been tested
		with <b>Postgresql</b> and <b>Mysql.</b><br/>
		You could do the following tasks:
		<ul style="padding-left: 30px;">
			<li>Create a new database</li>
			<li>copy a table from posgres to Mysql and viceverse.</li>
			<li>the new table will be cloned from the database you are copying from.</li>
		</ul>
		<div>
		  <br/>if you have a question on how to use it or add functionality please click 
		  <a href="mailto:ja_davalos@comcast.net?subject=questions about database utils">here</a> to send me an email 
		</div>
		<br/>
		<div>
		  <h3 id="download">Download war file</h3>
		  click  <a href="javascript:downloadJar('databaseutils-1.0.war')">here</a> to download war-file 'databaseutils-1.0.war'
		  <br/>
		</div>
		<br/>
		<div>
		  <h3 id="execute">Instruction to execute spring boot service</h3>
		  <ul style="padding-left: 30px;">
		  	<li>cd to directory withdatabaseutils-1.0.war</li>
		  	<li>Modify <b>-cp</b>command to point to the jars with the driver class. Separate with semi colons</li>
			<li>include at end of <b>-cp</b> command <b>;databaseutils-1.0.war</b></li>
			<li>execute command bellow include the modifications.</li>
		  </ul><br/>
		  <div style="width:900px;">
	  		cd \dir\with\war\file <br/>
			java -cp "\replace\with\dirs\for\mysql-connector-j-9.1.0.jar;databaseutils-1.0.war" org.springframework.boot.loader.launch.WarLauncher
		  </div>
		</div>
	  </div>
	</body>
</html>