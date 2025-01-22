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
		  <li><a class="expand" href="javascript:openPage('tableCopy.jsp')">&#160;&#160;&#160;&#160;Copy table to any DB </a></li>
		  <li><a class="expand" href="javascript:openPage('newConnection.jsp')">&#160;&#160;&#160;&#160;Build connection to DB</a></li>
		  <li><a href="javascript:openPage('bldLandingPage.xhtml')"> Create landing page </a></li> 
		  <li><a href="javascript:resetOpenPage('viewFamily.xhtml')"> View your family Tree </a></li>
		  <li><a href="javascript:resetOpenPage('selectFamily.xhtml')">select family via email </a></li>
		  <li>&#160;</li>
		  <li>
		    <div class="dropbtn" >
				<div id="plusImg1"><img height="12" width="12" src="includes/images/plus.png" onclick="expand('familyGroup',1)"/> Create shopping List</div>
				<div id="minusImg1" style="display:none"><img height="12" width="12" src="includes/images/minus.png" onclick="expand('familyGroup',1)"/> Create shopping List</div>
			  <div id="familyGroup" class="groups">
				  <a class="expand" href="javascript:openPage('addFamily.xhtml')">create Root</a>
				  <a class="expand" href="javascript:openPage('addEmails.xhtml')">Add emails</a>
				  <a class="expand" href="javascript:openPage('loadFiles.xhtml')">add shop list from a file</a>
			  </div>
		    </div>
		  </li>
		  <li>
		    <div class="dropbtn" >
				<div id="plusImg2"><img height="12" width="12" src="includes/images/plus.png" onclick="expand('adminGroup',2)"/> Administrator's functions</div>
				<div id="minusImg2" style="display:none"><img height="12" width="12" src="includes/images/minus.png" onclick="expand('adminGroup',2)"/> Administrator's functions</div>
			  <div id="adminGroup" class="groups">
				  <a class="expand" href="javascript:openPage('addItems.xhtml')">Add family member(s)</a>
				  <a class="expand" href="javascript:openPage('deleteItems.xhtml')">Delete family member(s)</a>
				  <a class="expand" href="javascript:openPage('updateItems.xhtml')">Update family member(s)</a>
			  </div>
		    </div>
		  </li> 
		</ul>
	  </nav>
	</div>
</html>
