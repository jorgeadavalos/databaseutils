var _selectedObj = "";
function buildModal(objResp) {
	var table = document.getElementById('serverTable');
	if (table == null) {
		alert("element 'serverTable' not found in html ");
		return false;
	}
	table.innerHTML = "";
	var ajaxmsg = objResp.ajaxmsg.trim();
	if (ajaxmsg.startsWith("<!")) {
		table.innerHTML = ajaxmsg;
		return;
	}
	var resp = JSON.parse(JSON.stringify(objResp.ajaxmsg));
	if (resp == null) 
		resp = JSON.parse('['+objResp.ajaxmsg+']');

	for (ndx in resp) {
		for (jsonKey in resp[ndx]) {
			if (resp[ndx].hasOwnProperty(jsonKey)) {
				var row = table.insertRow(-1);
				var cell = row.insertCell(-1);
				cell.innerHTML = "<b>"+jsonKey+"</b>";
				var cell = row.insertCell(-1);
				cell.innerHTML = resp[ndx][jsonKey];
			}
		}
	}
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
function restGetSingle(key,reqMapping,modal) {
	var element = findElement(key);
	var value = element.value;
	if (value == null || value.length == 0 ) {
		element.focus();
		alert("must enter a value");
		return false;
	}
	var url = reqMapping+"/"+value;
	var obj = new ajaxObj(url);
	obj.ajaxFunc = function(objResp) {
		buildModal(objResp)
		showMyModal(modal)
	};
	ajaxRequest(obj,"GET");
	return false;
}
function restGetLogin() {
	var email = findElement("email");
	var psw = findElement("psw");
	if (email == null || psw == null) alert("not a login panel");
	var value = email.value.trim();
	if (value == null || value.length == 0 ) {
		email.focus();
		alert("must enter a value");
		return false;
	}
	value = psw.value.trim();
	if (value == null || value.length == 0 ) {
		psw.focus();
		alert("must enter a value");
		return false;
	}
	showWindow(email.value.trim(),psw.value.trim());
	return false;
}
function showWindow(email,psw) {
	var path = window.location.pathname.substring(1);
	let ndx = path.indexOf("/");
	let serverPath = "/";
	if (ndx == -1) serverPath += path+"/";
	else serverPath += path.substring(0,++ndx);
	var url = serverPath +"startloginReq?email=" +email+"&psw="+psw;
	window.open(url,"_parent");
}
function bldObjectNames(tblname,modalname) {
	var element = findElement(tblname);
	if (element == null ) {
		alert("element "+tblname+" does not exist!!");
		return false;
	}
	var obj = new ajaxObj("config");
	obj.ajaxFunc = function(objResp) {
		listClassNames(objResp,element);
	};
	ajaxRequest(obj,"GET");
	return false;
}
function listClassNames(objResp,table) {
	table.innerHTML = "";
	var resp = JSON.parse(objResp.ajaxmsg);

	for (ndx in resp) {
		var row = table.insertRow(-1);
		var cell = row.insertCell(-1);
		cell.innerHTML = "<b>"+ndx+"</b>";
		var cell = row.insertCell(-1);
		cell.innerText = resp[ndx]+" ";
		cell.appendChild( bldCheckBox(resp[ndx],table));
	}
}
function restAddRecord() {
	
}
function selectObject(modalTbl,modalName) {
	var element = findElement(modalTbl);
	if (element == null) {
		alert("element "+modalTbl+" does not exist!!");
		return false;
	}
	var obj = new ajaxObj("selectObj");
	obj.ajaxFunc = function(objResp) {
		builListOfObjects(objResp,modalName);
		showMyModal(modal);
	};
	ajaxRequest(obj,"GET");
	return false;
}
function builListOfObjects(objResp,name) {
	var table = document.getElementById(name);
	table.innerHTML = "";
	var resp = JSON.parse(objResp.ajaxmsg);

	for (jsonKey in resp) {
		if (resp.hasOwnProperty(jsonKey)) {
			var row = table.insertRow(-1);
			var cell = row.insertCell(-1);
			cell.innerHTML = "<b>"+jsonKey+"</b>";
			var cell = row.insertCell(-1);
			cell.innerHTML = "<input type='text' id='aname'/>";
		}
	}
}
function bldCheckBox(name,table) {
	var checkbox = document.createElement("input"); 
	checkbox.type ="checkbox";
	checkbox.name = "checkedObj";
	checkbox.value = name;
	checkbox.onclick = function() {updateSingleItem(checkbox,table)};
	checkbox.defaultChecked = false;
	return checkbox;
}
function updateSingleItem(item,table) {
	if (item.checked ) _selectedObj = item.value;
	var checkbox = table.querySelectorAll('input[type="checkbox"]:checked');
	for (var i=0;i<checkbox.length;i++) {
		checkbox[i].checked = false;
		item.checked = true;
	}
	alert(item.value);
	var obj = new ajaxObj("setclassname?classname="+item.value.toLowerCase());
	obj.ajaxFunc = function(objResp) {
		_selectedObj = objResp.ajaxDestEl.innerHTML;
	};
	ajaxRequest(obj,"GET");
}
function primer() {
	var obj = new ajaxObj("primer");
	obj.ajaxFunc = function(objResp) {
		_selectedObj = objResp.ajaxDestEl.innerHTML;
	};
	ajaxRequest(obj,"GET");
}
