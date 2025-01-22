function initFocus()
{
    var f = document.getElementById("j_username");
    if (f != undefined)
    {
        f.focus();
    }
}
function centerLoginBox(target)
{
    if (target == undefined)
        return;
    var w = 0;
    var h = 0;
    if (self.innerHeight) // all except Explorer
    {
        w = self.innerWidth;
        h = self.innerHeight;
    }
    else if (document.documentElement && document.documentElement.clientHeight)
        // Explorer 6 Strict Mode
    {
        w = document.documentElement.clientWidth;
        h = document.documentElement.clientHeight;
    }
    else if (document.body) // other Explorers
    {
        w = document.body.clientWidth;
        h = document.body.clientHeight;
    }
    target.style.position = 'relative';
    target.style.width = w + "px";
    target.style.height = h + "px";
}
function ChgPsw(parm) {
	var flds = getElementsByClass(parm);
	for (var i = 0;i<flds.length;i++) {
		flds[i].style.display = "block";
	}
	return false;
}
function getElementsByClass(searchClass) { 
	var el = new Array();
	var tags = document.getElementsByTagName('*');
	var tcl = " "+searchClass+" ";
	for(i=0,j=0; i<tags.length; i++) { 
		var test = " " + tags[i].className + " ";
		if (test.indexOf(tcl) != -1) 
			el[j++] = tags[i];
	} 
	return el;
}
function verifyPsw(psw,vPsw) {
    var form = document.getElementById("formname");
	const field1 = document.getElementById(form.value+":"+psw);
	const field2 = document.getElementById(form.value+":"+vPsw);
	const email = document.getElementById(form.value+":loginid");
	if (field1 == null || field2 == null || email == null) return false;
	
	const regex = /^.*\@.*\..+$/;
	if (!email.value.match(regex)) {
		alert("invalid email "+email.value);
		return false;
	}
	const value1 = field1.value;
	const value2 = field2.value;
	if (value1 === value2) return true;
	alert("fields 'password' and 'verify password' must have the same value");
	return false;
}