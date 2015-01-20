//whether the hidden lines are hidden
var isHide=true;

function sw(itemId){
	if(isNaN(itemId)) {
		swSubstep(itemId);
	} else {
		swStep(itemId);
	}
}

function closeall() {	
	//close all test step
	for ( var j = 1; (document.getElementById('tbody_' + j)) != null; j++) {
		var tbody = document.getElementById('tbody_' + j);
		if (tbody) {
			if (tbody.style.display == "") {
				tbody.style.display = "none";
				var expandimage = document.getElementById('expand_' + j);
				expandimage.src = "img/nolines_plus.gif";
			}
		}
	}
	
	//close all sub-test step
	var img;
	var xpath;
	for ( var j = 1; (img = document.getElementById('subStep-' + j)) != null; j++) {
		img.src = "img/nolines_plus.gif";
		xpath = 'subStep-' + j;
		$("tr." + xpath).each(function() {
			this.style.display = "none";
			if ($(this).hasClass("hidden")) {				
				$(this).addClass("closed");				
			}
		});
	}
	
}

function openall() {

	//open all test step
	for ( var j = 1; (document.getElementById('tbody_' + j)) != null; j++) {
		var tbody = document.getElementById('tbody_' + j);
		if (tbody) {
			if (tbody.style.display == "none") {
				tbody.style.display = "";
				var expandimage = document.getElementById('expand_' + j);
				expandimage.src = "img/nolines_minus.gif";
			}
		}
	}
	
	//open all sub-test step
	var img;
	var xpath;
	for ( var j = 1; (img = document.getElementById('subStep-' + j)) != null; j++) {
		img.src = "img/nolines_minus.gif";
		xpath = 'subStep-' + j;
		$("tr." + xpath).each(function() {		
			if (!$(this).hasClass("hidden")) {
				this.style.display = "";
			} else {
				if(!isHide){
					this.style.display = "";
				}				
				$(this).removeClass("closed");				
			}
		});
	}
}

function hideUnhideRows(type) {	

	var label = document.getElementById(type);
	if (label.innerHTML.indexOf('Show') != -1) {
		labelText = label.innerHTML.split('Show')[1];
		label.innerHTML = 'Hide ' + labelText;
		isHide=false;
	} else {
		labelText = label.innerHTML.split('Hide')[1];
		label.innerHTML = 'Show ' + labelText;
		isHide=true;
	}
	
	$("tr.hidden").each(function(){
		if(isHide){
			this.style.display = 'none';
		}else{
			if(!$(this).hasClass("closed")){
				this.style.display = '';
			}			
		}
	});
}

function loadAnchor() {	
	var lookupAnchor = unescape(self.document.location.hash.substring(1));

	// No anchor? Silently return
	if (lookupAnchor === undefined || lookupAnchor == null
			|| lookupAnchor.length < 1) {
		return;
	}

	var tds = document.getElementsByTagName("td");
	var classPattern = /^message$/;

	var i = tds.length - 1;
	var td;

	for (; i > 0; i--) {
		td = tds[i];

		if (td.className === undefined || td.className.length < 1
				|| !classPattern.test(td.className)) {
			continue;
		}

		var anchors = td.getElementsByTagName("a");

		// No anchors, continue with next tbody tag
		if (anchors.length < 1) {
			continue;
		}

		var j = 0, length = anchors.length;
		var anchor;

		for (; j < length; j++) {
			anchor = anchors[j];
			if (anchor.name === undefined || anchor.name.length < 1) {
				continue;
			}

			if (anchor.name == lookupAnchor) {
				// A bit ugly, yes
				var numberNode = anchor.parentNode.parentNode.parentNode.parentNode.nextSibling;

				// Something has change in the structure, or it's not browser
				// compatible
				if (numberNode === undefined) {
					return;
				}

				var number = Number(numberNode.id.replace(/[^0-9]/g, ""));

				// Not a real number
				if (number == Number.NaN) {
					return;
				}

				// Only if it's hidden
				if (numberNode.style.display == "none") {
					sw(number);
				}

				// And we're done!				
				return;
			}
		}		
	}
}

function swStep(itemId) {//switch from closed to open
	var tbody = document.getElementById('tbody_' + itemId);	
	
	if (tbody.style.display == "none") {
		tbody.style.display = "";
		var expandimage = document.getElementById('expand_' + itemId);
		expandimage.src = "img/nolines_minus.gif";
		
	} else {//switch from open to closed
		tbody.style.display = "none";
		var expandimage = document.getElementById('expand_' + itemId);
		expandimage.src = "img/nolines_plus.gif";
	}	
}

function swSubstep(itemId){		
	var xpath = "tr."+itemId;
	var fnode = $(xpath)[0];	
	var expandimage = document.getElementById(itemId);	
	if(fnode==null) {
		var reg = new RegExp(".*nolines_plus.gif$");		
		if (reg.exec(expandimage.src)) {
			expandimage.src = "img/nolines_minus.gif";			
		} else {
			expandimage.src = "img/nolines_plus.gif";	
		}
		return;
	}
	if(fnode.style.display==""){//to close the sub-step
		$(xpath).each(function(){
			this.style.display="none";	
			if($(this).hasClass("hidden")){
				$(this).addClass("closed");
			}            			
		});	
		expandimage.src = "img/nolines_plus.gif";
	}else{//to open the sub-step
		$(xpath).each(function(){		
			if(!$(this).hasClass("hidden")){
				this.style.display="";
			}else{
				if(!isHide){
					this.style.display="";
				}
				$(this).removeClass("closed");
			}			
		});
		expandimage.src = "img/nolines_minus.gif";	
	}
}

function checkBrowser(){
    var bro=$.browser;	
	if(bro.mozilla||bro.safari) {//firefox and chrome
		$(".time").css({"width":"140px"});		
		$(".level").css({"width":"40px"});		
	}else if(bro.msie) {//IE
		$(".time").css({"width":"160px"});		
		$(".level").css({"width":"40px"});
	}
}