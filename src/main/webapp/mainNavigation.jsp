<!DOCTYPE html> 
<html xmlns="http://www.w3.org/1999/xhtml">
	<div id="navigationframe"> 
	  <nav>
	    <a href="#" class="nav-toggle">
			<img class="nav-toggle" onclick="return fadeOut()" src="includes/images/bt_prev.gif" id="idFadeOut"/>
			<img class="nav-toggle" onclick="return fadeIn()" src="includes/images/bt_next.gif" id="idFadeIn" />
		</a>
		<ul>
		  <li><a href="#" style="cursor: unset;"> Data base commands</a></li>
		  <li><a class="expand" href="javascript:openPage('tableCopy.jsp')">Copy table to any DB </a></li>
		  <li><a class="expand" href="javascript:openPage('newConnection.jsp')">Build connection to DB (testing)</a></li>
		  <li>&#160;</li>
		  <li>
		    <div class="dropbtn" >
				<div id="plusImg2"><img height="12" width="12" src="includes/images/plus.png" onclick="expand('adminGroup',2)"/> Administrator's functions</div>
				<div id="minusImg2" style="display:none"><img height="12" width="12" src="includes/images/minus.png" onclick="expand('adminGroup',2)"/> Administrator's functions</div>
			  <div id="adminGroup" class="groups">
			  </div>
		    </div>
		  </li> 
		</ul>
	  </nav>
	</div>
</html>
