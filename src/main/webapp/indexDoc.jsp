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
			<li>Create a new database</l1>
			<li>copy a table from posgres to Mysql and viceverse.</li>
			<li>the new table will be cloned from the database you are copying from.</li>
		</ul>
		
		<br/><br/>
		<img src="includes/images/root.png"/>
	  </div>
	  <div id="configuration" style="padding:15px;width:55%;">
		<h3>JSF server configuration</h3>
		<h4>Set environmental variable:</h4>
		<div  style="padding:15px;">
		Set(export) LBHANDSHAKE=http://ourfamilycontacts.com/loadbalancer/loadinstance/includes/js/lbhandshake.js<br/>
		Test:<br/>
		Set(export) LBHANDSHAKE=http://10.0.0.246:8080/loadbalancer/loadinstance/includes/js/lbhandshake.js<br/>
		</div>
		<div  style="padding:15px;">
		  <h3>faces-config.xml</h3>
			&#60;lifecycle&#62;<br/>
			<div  style="padding-left:15px;">
				&#60;phase-listener&#62;com.assoc.jad.loadbalancer.lbinstance.LifeCycleListener&#60;/phase-listener&#62;<br/>
			</div>
			&#60;/lifecycle&#62;
		  </div>
		<div  style="padding-left:15px;">
		  <h3>Web.xml</h3>
		  &#60;filter&#62;<br/>
		<div  style="padding-left:15px;">
			&#60;filter-name&#62;jsftrace filter&#60;/filter-name&#62;<br/>
			&#60;filter-class&#62;com.assoc.jad.loadbalancer.lbinstance.LoadBalancerFilter&#60;/filter-class&#62;<br/>
		</div>
		  &#60;/filter&#62;<br/>
		  &#60;filter-mapping&#62;<br/>
		  <div  style="padding-left:15px;">
			&#60;filter-name&#62;jsftrace filter&#60;/filter-name&#62;<br/>
			&#60;servlet-name&#62;Faces Servlet&#60;/servlet-name&#62;<br/>
			&#60;url-pattern&#62;/*&#60;/url-pattern&#62;<br/>
		  </div>
		  &#60;/filter-mapping&#62;<br/>
		</div>
		<div  style="padding:15px;">
		   <h3>JVM:</h3>
		   --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED<br/>
		</div>
		<div  id="download" style="padding:15px;">
		  <h3>Download jar file</h3>
		  click  <a href="javascript:downloadJar()">here</a>  download jar-file and add it to your lib-directory.
		  <h3>INCLUDE SIMPLE JSON IN POM</h3>
		  <br/>
		</div>
	  </div>
	</body>
</html>