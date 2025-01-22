let _JSONFOLDERS;
let _CALLEROBJ	= {};
let _FOLDERNAME;
function popUp(url) {
	genPopUp = window.open(url,"pop","toolbar=no,location=no,scrollbars=yes,directories=no,status=yes,menubar=no,resizable=yes,width=500,height=350");
	genPopUp.focus();
}
function loadRootFolders() {
	_CALLEROBJ["method"] = function() {callSubDir()};
	var obj = new ajaxObj(_RESTBASE+"/rootFolders");
	var domMusicTbl = document.getElementById("music");
	domMusicTbl.className = 'wait';
	obj.ajaxFunc = function(objResp) {
		_JSONFOLDERS = JSON.parse(objResp.ajaxmsg);
		domMusicTbl.className = 'initial';
		return serverResp();
	};
	ajaxRequest(obj,"GET");
	
	return false;
	
}
function serverResp() {
	var domMusicTbl = document.getElementById("music");
	domMusicTbl.innerHTML = '';
	let musicTable = document.createElement("TABLE");
	let wrkKeys = Object.keys(_JSONFOLDERS);
	let sorted = Object.keys(wrkKeys).sort(function(a,b){return wrkKeys[a].localeCompare(wrkKeys[b])});
	for (ndx in sorted) {
		let key = wrkKeys[sorted[ndx]];
		let folder = _JSONFOLDERS[key];
		buildTable(folder,musicTable);
	}
	domMusicTbl.appendChild(musicTable);
}
function buildTable(jsonFolder,musicTable) {
	bldRow(jsonFolder,musicTable,'1px');
	let files = jsonFolder.files;
	if (typeof files === 'undefined') return;
	
	let len = files.length;
	for (let i=0;i<len;i++) {
		bldRow(files[i],musicTable,'10px');
	}
}
function bldRow(json,tbl,margin) {
	if (typeof json["hideit"] !== 'undefined' && json["hideit"]) return;
	let row = tbl.insertRow(-1);
	
	let cell = row.insertCell(-1);
	let cellImg = document.createElement("IMG");
	cellImg.style.marginLeft = margin;
	cellImg.src = "includes/images/"+json.image;
	cell.appendChild(cellImg);
	
	cell = row.insertCell(-1);
	let name = json.name;
	
	let ndx = name.lastIndexOf("\\");
	if (ndx == -1) ndx = name.lastIndexOf("/");
	if (ndx != -1 ) name = name.substring(ndx);	
	name = name.replaceAll("\\","").replaceAll("/","");
	
	if (json.type === 'folder') { 
		populatFolder(cell,row,name);
		_FOLDERNAME = json.name;
	} else 
		populatFile(cell,row,name);
	
	cell = row.insertCell(-1);
	cell.textContent = _FOLDERNAME;
	cell.style.visibility = 'hidden';
	cell.style.display = 'none';
	
	tbl.appendChild(row);
}
function populatFolder(cell,row,name) {
	cell.textContent = name;
	cell.onclick = function() {nextStep(this);};
	row.appendChild(cell);
	
	cell = row.insertCell(-1);
	cell.style.cursor = 'not-allowed';		

}
function populatFile(cell,row,name) {
	cell.textContent = name;
//	cell.onclick = function() {nextStep(this);};
	row.appendChild(cell);
	
	cell = row.insertCell(-1);
	cell.innerHTML = '<input type="image" title="download" id="view" value="view" src="includes/images/downloadArrow.jpg" width="20" height="20" onclick="return DownloadFileWebServer(this)">';	
}
function nextStep(parm) {
	_CALLEROBJ["obj"] = parm;
	_CALLEROBJ.method();
}
function search() {
	let value = document.getElementById("chooseItem").value.toUpperCase();
		if (typeof value === 'undefined' || value.length <= 0) return;

	for (var name in _JSONFOLDERS) {
		var jsonObj = _JSONFOLDERS[name]; 
		isSearchable(jsonObj,value);
		let files = jsonObj["files"];
		for (var ndx in files) {
			isSearchable(files[ndx],value);
		}
	}
	serverResp();
	return false;
}
function isSearchable(jsonObj,value) {
	var cmpNameDesc = jsonObj.name;					// + parmNodes[name].descriptions;

	jsonObj["hideit"] = false;
	if (cmpNameDesc.toUpperCase().indexOf(value) == -1) 
		jsonObj["hideit"] = true;
}
function clearSearch(parm) {
	parm.parentElement.childNodes[0].value ='';
	for (var name in _JSONFOLDERS) {
		var jsonObj = _JSONFOLDERS[name]; 
		jsonObj["hideit"] =false;
		let files = jsonObj["files"];
		for (var ndx in files) {
			files[ndx]["hideit"] = false;
		}
	}
	serverResp()
	return false;
}
function callSubDir() {
	let parm = _CALLEROBJ["obj"];
	var trTag = parm.parentNode;
	let uri = "?foldertree="+encodeURIComponent(trTag.lastChild.innerText)+"&filename="+encodeURIComponent(parm.innerText);
	var obj = new ajaxObj(_RESTBASE+"/folderContent"+uri);
	var domMusicTbl = document.getElementById("music");
	domMusicTbl.className = 'wait';
	obj.ajaxFunc = function(objResp) {
		_JSONFOLDERS = JSON.parse(objResp.ajaxmsg);
		_CALLEROBJ["method"] = function() {playFileWebServer()};
		domMusicTbl.className = 'initial';
		return serverResp();
	};
	ajaxRequest(obj,"GET");
	
	return false;
}
function playFileWebServer() {
	let parm = _CALLEROBJ["obj"];
	var trTag = parm.parentNode;
	if (trTag.firstChild.innerHTML.indexOf('folder') != -1) {
		callSubDir();
		return;
	}
	var name = parm.lastChild.textContent;
	obj = {};
	obj["folder"] = trTag.lastChild.textContent;
	obj["file"] = name;
	var ndx = window.document.baseURI.lastIndexOf("/");
	var url = window.document.baseURI;
	if (ndx != -1 ) url = url.substring(0,ndx);

	popUp("/music/image/dummy.xhtml?jsonitem="+encodeURIComponent(JSON.stringify(obj)));
	return false;
}
function expandFolder(row) {
	let x=0;
}
function DownloadFileWebServer(parm) {
	var trTag = parm.parentNode.parentNode;
	var filename = trTag.cells[1].innerText.replace(/^\s+|\s+$/g, '');
	var folder = trTag.lastChild.textContent;
	if (folder.indexOf(filename) != -1) filename = "";
	obj = {};
	obj["folder"] = folder;
	obj["file"] = filename;
	
	var ndx = window.document.baseURI.lastIndexOf("/");
	var url = window.document.baseURI;
	if (ndx != -1 ) url = url.substring(0,ndx);

//	popUp(url+"/image/download?jsonitem="+encodeURIComponent(JSON.stringify(obj)));
	popUp(url+"/docservlet/download?classname=DownloadFile&jsonitem="+encodeURIComponent(JSON.stringify(obj)));
	return false;
}
function testSteps() {
	
}