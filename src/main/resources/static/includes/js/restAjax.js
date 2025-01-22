ajaxObjs = {};
function ajaxObj(name) {
	this.ajaxURL     	= name;
	this.ajaxDone    	= false;
	this.ajaxError   	= false;
	this.ajaxFunc    	= null;
	this.ajaxReqHeaders	= {};
	this.body;
	this.ajaxmsg;
}
function ajaxRequest(obj,method) {
	if (method == null) alert("METHOD must be added");
	var pageRequest;
	if (window.XMLHttpRequest) pageRequest = new XMLHttpRequest();
	else if (window.ActiveXObject)  pageRequest = new ActiveXObject("Microsfot.XMLHTTP"); 
		 else
			return;
	
	var readyStateChange = function() {
		if (pageRequest.readyState == 4 ) {
			if (pageRequest.status != 200) obj.ajaxError = true;
			serverGetData(pageRequest.responseText,obj);
		}
	};
	pageRequest.onreadystatechange = readyStateChange;
	
	obj.ajaxDone  = false;
	pageRequest.open(method,obj.ajaxURL,true);
	setHeaders(obj,pageRequest);
	pageRequest.send(obj.body);
}
function serverGetData(msg,obj) {
	obj.ajaxDone  = true;
	obj.ajaxmsg = msg;
	obj.ajaxFunc(obj);
}
function setHeaders(obj,req) {
	req.setRequestHeader("Content-Type", "application/json");
	keys = Object.keys(obj.ajaxReqHeaders);
	for (var i=0;i<keys.length;i++) {
		req.setRequestHeader(keys[i],obj.ajaxReqHeaders[keys[i]]);
	}
}