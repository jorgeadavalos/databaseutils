function itemNode(parm,artistid,itemid) {
	this.name		= parm;
	this.artistid	= artistid;
	this.itemid		= itemid;
}
var jsonNode = null;

function findElement(nam) {
	var namVar = document.getElementById(nam);
	if (namVar) return namVar;
	for (var i=0;i<document.forms.length;i++) {
		namVar = document.getElementById(document.forms[i].id+":"+nam);
		if (namVar) return namVar;
	}
	var elements = document.getElementsByTagName('*');
	if (elements) {
		for (var i = 0;i<elements.length;i++) {
			if (elements[i].id.indexOf(nam) != -1) {
				return elements[i];
			}
		}
	}
	return null;
}
function imgResize() {
	var imgs = document.getElementsByTagName("img");
	if (imgs == null ) return;
	for (var i=0;i<imgs.length;i++) {
		var percentH = imgs[i].naturalHeight* imgs.height/100;
		imgs[i].clientWidth = imgs.naturalWidth * percentH;
	}
}
function setMsg(fldName,msg) {
	_FLD = findElement(fldName); 
	if (_FLD != null) {
		_FLD.innerHTML = msg;
		_LAUNCHTID = setInterval(launchJNLP,500);
	}
//	imgResize();
	return false;
}
function launchJNLP() { 
	if (_FLD == null) return;
    _FLD.style.color = _FLD.style.color == "red" ? "blue" : "red";
} 
function setjsonitem(name,artistid,itemid) {
	var fld = findElement('jsonitem'); 
	if (fld == null) {
		fld = document.createElement("input");
	}
	jsonNode = new itemNode(name,artistid,itemid); 
	fld.value = encodeURIComponent(JSON.stringify(jsonNode)); //must be done here
}
function setObjectData(pagename,modalname) {
	var el = findElement(modalname);
	if (el != null) {
		var ndx = el.data.lastIndexOf("/");
		if (ndx != -1 ) el.data = el.data.substring(0,++ndx) + pagename;
	}
}
function UploadImage(parm,mymodal,page) {
	var url = "ajaxs/ajaxResetInfomsg.xhtml";
	var obj = new ajaxObj(url);
	obj.ajaxFunc = function(objResp) {
		setObjectData(page,"htmlModal3");
		showMyModal(parm,mymodal);
	};
	ajaxRequest(obj);
	return false;
}
function popUp(url) {
	window.open(url,"pop","toolbar=no,location=no,scrollbars=yes,directories=no,status=yes,menubar=no,resizable=yes,width=500,height=350");
}
function displayImage(title,artistid,itemid) {
	setjsonitem(title,artistid,itemid);
	var ndx = window.document.baseURI.lastIndexOf("/");
	var url = window.document.baseURI;
	if (ndx != -1 ) url = url.substring(0,ndx);
	var page = url+"/image/displayPhotos.xhtml?jsonitem="+encodeURIComponent(JSON.stringify(jsonNode));
	popUp(page);
	return false;
}
function resetInfomsg() {
} 
function getParameterByName(name) {
    url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
	results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
function openPage(pageName) {
	var genPopUp = window.open(pageName,"_parent");
 }
function bldJsonFromHTMLElements() {
 	json = searchForInputsElements(document.body,{});
 	return json;
}
function searchForInputsElements(parent,json) {
	const children = parent.childNodes;
	const len = children.length;
	for (let i=0;i<len;i++) {
		var child = children[i];
		if (child.childNodes.length > 0) searchForInputsElements(child,json);
		if (child.nodeName.localeCompare('INPUT') != 0) continue;
		
		if (child.inputText != null) json[child.id] = child.inputText;
		else if (child.value != null) json[child.id] = child.value;
	}
	return json;
}

document.onclick = function(ev) {
	if (_FLD == null) return;
//	if (_FLD.id !== "loginError") return false;
	if (_FLD.id !== ev.explicitOriginalTarget.id) return;
	
	if (_LAUNCHTID != null) clearInterval(_LAUNCHTID);
	_LAUNCHTID = null;
	_FLD = null;
}
