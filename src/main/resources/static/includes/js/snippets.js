// 1. check if there is an include request 'snippets'
// 2. save in session storage as snippets cached.
function initSession() {
	sessionStorage.clear();
}
function snippets() {
	const includes = document.getElementsByClassName("snippets");
	const len = includes.length;
	let fadinFlag = false;
	if (len == 0) collectBeanTags();
	for (var i=0;i<len;i++) {
		const ele = includes[i];
		 if (isSnippetsCached(ele)) continue;
		const ndx = i;
		var obj = new ajaxObj(ele.title);
		obj.ajaxFunc = function(objResp) {
			sessionStorage.setItem(ele.title,objResp.ajaxmsg);
			ele.insertAdjacentHTML("afterbegin",objResp.ajaxmsg);
			if (ndx == len-1) {
				collectBeanTags();
				fadinFlag = true;
			}
		}
		ajaxRequest(obj,"GET");
	}
//	if (!fadinFlag) collectBeanTags();
}
function isSnippetsCached(domElement) {
	let key = domElement.title;
	let cached = sessionStorage.getItem(key);
	if (cached != null && cached.length > 0) {
		domElement.insertAdjacentHTML("afterbegin",cached);
		return true;	
	}
	return false;
} 
function collectBeanTags() {
	fadeIn();
	let beans = {};
	beans = collectBeanNames(document.body,beans);
	let len = Object.keys(beans).length;
//	if (len > 0) getBeansFromServer(beans);
	if (len > 0) getBeansFromCallee(beans);
}
function collectBeanNames(parent,beans) {
	const children = parent.childNodes;
	const len = children.length;
	for (let i=0;i<len;i++) {
		if (children[i].childNodes.length > 0) collectBeanNames(children[i],beans);
		var beanName = getBeanName(children[i]);
		if (beanName != null) beans[beanName] = beanName;
	}
	return beans;
}
function collectBeanFields(parent,fields) { //fields is an array
	const children = parent.childNodes;
	const len = children.length;
	for (let i=0;i<len;i++) {
		if (children[i].childNodes.length > 0) collectBeanFields(children[i],fields);
		var node = children[i];
		let text = getNodeText(node);
		if (text == null) continue;
		
		fields.push(node);
	}
	return fields;
}
function getNodeText(node) {
	let text = node.innerText;
	if (text == null || text.indexOf(_BEANPREFIX) == -1) {
		text = node.value;
		if (typeof text != 'number')
			if (text == null || text.indexOf(_BEANPREFIX) == -1) text = node.nodeValue;
		if (text == null || typeof text == 'number' || text.indexOf(_BEANPREFIX) == -1) return null;
	}
	return text;
}
function setNodeText(node,value,pattern,readonly) {
	if (typeof value == 'object' && value != null) return; 
	if (readonly) node.readOnly = true;
	if (value == null) value = "";
	let text = node.innerText;
	if (text != null && text.indexOf(pattern) != -1) node.innerText		= text.replace(pattern,value); 
	text = node.value;
	if (typeof text != 'number' && text != null && text.indexOf(pattern) != -1) node.value = text.replace(pattern,value); 
	text = node.nodeValue;
	if (text != null && text.indexOf(pattern) != -1) node.nodeValue		= text.replace(pattern,value); 
	text = node.textContent;
	if (text != null && text.indexOf(pattern) != -1) node.textContent	= text.replace(pattern,value); 
}
function getBeanName(node) {
	let text = getNodeText(node);
	if (text == null) return text;
	const wrkArray = text.split(_BEANPREFIX);
	for (var j=0;j<wrkArray.length;j++) {
		var ndx = wrkArray[j].indexOf(_BEANSUFFIX);
		if (ndx == -1) continue;
		var beanName = wrkArray[j].substring(0,ndx);
		var ndx = beanName.indexOf(".");
		beanName = beanName.substr(0,ndx);
	}
	return beanName;
}
function getBeansFromCallee(beans) {
	let callee = JSON.parse(localStorage.getItem("callee"));
	if (callee == null) {
		getBeansFromServer(beans);
		return;
	}
	populateFromJsonObjs(beans);
}
function getBeansFromServer(beans) {
	var beanReq = "REST/getBeans/"+encodeURIComponent(JSON.stringify(beans));
	var obj = new ajaxObj(beanReq);
	obj.ajaxFunc = function(objResp) {
		var serverJson = JSON.parse(objResp.ajaxmsg);
		populateFromServerJson(serverJson);
	}
	ajaxRequest(obj,"GET");
}
function populateFromJsonObjs(beans) {
	let keys = Object.keys(beans);
	keys.forEach ( element => console.log(element));
	let fields = [];
	fields = collectBeanFields(document.body,fields);
	for (let element of fields) {
		var nodeText = getNodeText(element);
		if (nodeText == null) continue;
		
		var patterns = nodeText.split(_BEANPREFIX);
		for (pattern of patterns) {
			const ndx = pattern.indexOf(_BEANSUFFIX);
			if (ndx == -1) continue;
			
			pattern = pattern.substring(0,ndx);
			var items = pattern.split(".");
			var jsonObj = JSON.parse(localStorage.getItem(items[0]));
			
			var value = null;
			for (var j=0,jlen=keys.length;j<jlen;j++) {
				const readonly = jsonObj["readonly"];
				for (var k=1,klen=items.length;k<klen;k++) {
					if (items[k] === 'object') {
						jsonObj = jsonObj[items[k]];
						continue;
					}
					value = jsonObj[items[k]];
					break;
				}
				setNodeText(element,value,_BEANPREFIX+pattern+_BEANSUFFIX,readonly);
			}
		}
	}
}
function populateFromServerJson(serverJson) {
	let fields = [];
	fields = collectBeanFields(document.body,fields);
	const beanJsonArray = serverJson["beans"];
	for (let element of fields) {
		var pattern = getNodeText(element);
		if (pattern == null) continue;
		
		var items = pattern.replace(_BEANPREFIX,"").replace(_BEANSUFFIX,"").split(".");
		for (var j=0,jlen=beanJsonArray.length;j<jlen;j++) {
			var beanJson = beanJsonArray[j];
			const readonly = beanJson["readonly"];
			for (var k=1,klen=items.length;k<klen;k++) {
				beanJson = beanJson[items[k]];
				if (beanJson == null) break;
			}
			setNodeText(element,beanJson,pattern,readonly);
		}
	}
}
