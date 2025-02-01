let _KEYVALUES = '';
const _FOUND = '302';
const _NOTFOUND = '404';
const _SERVICEUNAVAILABLE = '503';

function verifyRequired(name) {
	let el = document.getElementById(name);
	if (el != null) {
		let fromTable = el.value.trim();
		if (fromTable.length == 0) {
			setMsg('infomsg','you must enter a fromtable value ');
			el.focus();
			return false;
		}
	}
	return true;
}
function verifyTables(...names) {

	let param = "?";
  	for (let name of names) {
    	param += name+"="+document.getElementById(name).value+"&";
 	 }
	 if (!verifyRequired('fromTable')) return;
	 if (!verifyRequired('fromDatabase')) {
		dataSourceReq();
		return;
	 }
	var obj = new ajaxObj(_RESTBASE+"/verifyTables"+param);
	obj.ajaxFunc = function(objResp) {
		let json = JSON.parse(objResp.ajaxmsg);
		if (json["status"] === _NOTFOUND) {	//table does not exist in todatabase it is ok to continue
			return bldPrimarykey(json);
		} else {
			setMsg('infomsg',json['infomsg']);
			return false;			
		}
	};
	ajaxRequest(obj,"GET");
	return false;
}
function dataSourceReq() {
	if (!verifyRequired('fromTable')) return;
	setMsg('infomsg','');
	showMyModal('newConnModal');
}
function bldPrimarykey(json) {
	let serverTable = json["table"];
	if (serverTable != null) {
		let field = document.getElementById("table404inputs");
		field.innerHTML = serverTable;
	}
	setMsg('json404Infomsg',json['infomsg']);
	showMyModal("json404Modal");
	return false;
}
function bldTableCall(...names) {
	let param = "?";
  	for (let name of names) {
    	param += name+"="+document.getElementById(name).value+"&";
    	lastFld = name;
 	}
 	param += "keyfields="+_KEYVALUES;
 	resetFields(lastFld);
	var obj = new ajaxObj(_RESTBASE+"/bldTable"+param);
	obj.ajaxFunc = function(objResp) {
		let json = JSON.parse(objResp.ajaxmsg);
		let serverTable = json["table"];
		if (serverTable != null) {
			let field = document.getElementById("table"+json["status"]+"inputs");
			field.innerHTML = serverTable;
		}
		setMsg('json404Infomsg',json['infomsg'])
		setMsg('infomsg',json['infomsg'])

		if (json["status"] === '200') exitTask('json404Modal');
		return false;
	};
	ajaxRequest(obj,"GET");
	return false;
}
function bldConnection() {
	json = {};
	if (!dataSourceInputs(json)) return;
	if (Object.keys(json).length == 0) {
		exitTask('newConnModal');
		return;
	}
	
	let obj = new ajaxObj(_RESTBASE+"/bldConnection");
	obj.body = JSON.stringify(json);
	obj.ajaxFunc = function(objResp) {
		let msg = objResp.ajaxmsg;
		let json = JSON.parse(msg);
		setMsg('newConnInfomsg',json['infomsg']);
		if (json["status"] === _SERVICEUNAVAILABLE) {		//service unavailable
			showMyModal('jarUploadModal');
			return;
		}
		document.getElementById("fromDatabase").value = json['fromDatabase'];
		document.getElementById("toDatabase").value = json['toDatabase'];		
		exitTask('newConnModal');
	}
	ajaxRequest(obj,"POST");
	return false;
}
function jarUploadFiles() {
	uploadFiles("jarUploadFiles");
}
function uploadFiles(restMethod) {
	if (typeof restMethod === "undefined") restMethod = 'uploadFile';

	let uploadfile = document.getElementById("uploadfile");
	const formData = new FormData();
	const len = uploadfile.files.length;
	let filesize = "'";
	for (let i=0;i<len;i++) {
		let file = uploadfile.files[i];
		formData.append('file', file);
		filesize += file.name+":"+file.size+",";
	}
	filesize += "'";
	parm = "?classname=DownloadFile&filesize="+filesize;	//&jsonitem="+encodeURIComponent(JSON.stringify(obj)));
	let obj = new ajaxObj(_RESTBASE+"/"+restMethod+parm);
	obj.body = formData;
//	obj.ajaxReqHeaders["Content-Type"] ="multipart/form-data; boundary=123";
	obj.ajaxFunc = function(objResp) {
		alert("serverResp( objResp.ajaxmsg.trim())");
	}
	ajaxRequest(obj,"POST");
	return false;
}
function addfilesize(event) {
  event.files[0];
}


function resetFields(name) {
	_KEYVALUES = "";
	let table = document.getElementById(name);
	let rows = table.rows;
	let cols = rows[0].cells;
	let len = rows.length;
	let lastCol = cols.length-1;
	for (let i=0;i<len;i++) {
		checkBox = rows[i].cells[lastCol].lastChild;
		if (checkBox.type === 'checkbox') {
			checkBox.checked =  false;
		}
	}
}
function collectcheckedValues(checkBox) {
	let text = checkBox.parentElement.parentElement.cells[1].textContent;
	let value = document.getElementById("setSize").value;
	if (value !== 'true') {
		_KEYVALUES += checkBox.value+",";
		return;	
	}
	if ( text == null) text = '';
	if (text.toUpperCase() === "TEXT") {
		let size = 'a';
		while (isNaN(size)) {
			size = prompt("(Mysql) because type is 'text' you must enter a size:");
		}
		text = '';
		if (size > 0) text = '('+size+')';
	}
	_KEYVALUES += checkBox.value +text+",";
}
function showMyModal(elementID) {
	var modal = document.getElementById(elementID);
    modal.style.display = "block";
	//document.getElementById('modalButton').focus();
	return false;
}
function exitTask(parm) {
	if (parm !=null) {
		var modal = document.getElementById(parm);
		modal.style.display = "none";
	}
	return false;
}
function dataSourceInputs(json) {
	let inputs = [];
	let fromurl = document.getElementById("fromurl");
	if (fromurl == null || fromurl.value.length == 0) {
		let text = "You must enter Source (From) Data source";
		fromurl.focus();
		setMsg('newConnInfomsg',text)
		return false;
	}
	inputs["fromurl"] = fromurl;
	inputs["fromuserid"]  = document.getElementById("fromuserid");
	inputs["frompsw"] = document.getElementById("frompsw");
	let url = inputs["fromurl"];
	if (!checkDataSourceInputs(url,inputs,json)) return false;

	inputs = [];
	inputs["tourl"] = document.getElementById("tourl");
	inputs["touserid"]  = document.getElementById("touserid");
	inputs["topsw"] = document.getElementById("topsw");
	url = inputs["tourl"];
	return checkDataSourceInputs(url,inputs,json);

}
function checkDataSourceInputs(url,inputs,json) {
	let value = url.value;
	if (value != null && value.length > 0) {
		if (!value.startsWith("jdbc:")) {
			let text = "if you enter data to 'URL' it must start with 'jdbc:' and the 'user id' and 'password' must be enterred";
			setMsg('newConnInfomsg',text)
			return false;
		}
	} else return true;
	
	for (index in inputs) {
		element = inputs[index];
		const value = element.value;
		if (value == null || value.length == 0) {
			let id = getTableRow(element);
			element.focus();
			setMsg('newConnInfomsg',id+" is a required field. You must enter a value");
			return false;
		}
	}
	updateJson(inputs,json);
	return true;
}
function getTableRow(element) {
	let td = element.parentNode.parentNode;
	if (td.nodeName !== "TD") return;
	let tr = td.parentNode;
	if (tr.nodeName !== "TR") return;
	let cells = tr.cells;
	cells[1].innerText = "*";
	return cells[0].innerText;
}
function updateJson(inputs,json) {
	for (index in inputs) {	
		element = inputs[index];
		json[index] = element.value;
	}
}
function downloadJar(filename) {
	url = "docservlet?resource=downloadfile&filename="+filename;
	window.open(url,'_blank','titlebar=no,status=no,menubar=no');
}
function mailBody() {
	window.open( "mailto:"+"ja_davalos@comcast.net"+""+"&Subject=question%20%20for%20copytable.js&body="+"HELLO", "_parent" ); 
}
function anchorTag(anchor) {
	let landingTags = document.getElementsByClassName('landingTag');
	for (let i=0;i<landingTags.length;i++) {
		landingTags[i].className = "";
	}
	let nanchorEl = document.getElementById(anchor.hash.substring(1));
	nanchorEl.className = "landingTag";
}
