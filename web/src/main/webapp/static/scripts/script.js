// This function is used by the login screen to validate user/pass
// are entered.
function validateRequired(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    oRequired = new required();

    for (x in oRequired) {
        if ((form[oRequired[x][0]].type == 'text' || form[oRequired[x][0]].type == 'textarea' || form[oRequired[x][0]].type == 'select-one' || form[oRequired[x][0]].type == 'radio' || form[oRequired[x][0]].type == 'password') && form[oRequired[x][0]].value == '') {
           if (i == 0)
              focusField = form[oRequired[x][0]];

           fields[i++] = oRequired[x][1];

           bValid = false;
        }
    }

    if (fields.length > 0) {
       focusField.focus();
       alert(fields.join('\n'));
    }

    return bValid;
}

// This function is a generic function to create form elements
function createFormElement(element, type, name, id, value, parent) {
    var e = document.createElement(element);
    e.setAttribute("name", name);
    e.setAttribute("type", type);
    e.setAttribute("id", id);
    e.setAttribute("value", value);
    parent.appendChild(e);
}

function confirmDelete(obj) {
    var msg = "Are you sure you want to delete this " + obj + "?";
    ans = confirm(msg);
    return ans;
}

// 18n version of confirmDelete. Message must be already built.
function confirmMessage(obj) {
    var msg = "" + obj;
    ans = confirm(msg);
    return ans;
}

/**
 * Readonly form
 * @param form
 */
readonlyForm = function(form, removeButton) {
    var inputs = $(form).find(":input");
    inputs.not(":submit,:button").prop("readonly", true);
    if(removeButton) {
        inputs.remove(":button,:submit");
    }
}

function callAjax(url, postData) {
	var self = this;
	if (!postData)
		postData = {};

	var jqxhr = $.ajax({
			url : url,
			data : postData,
			type : 'POST',
			dataType : 'json'
		});

	jqxhr.fail(function (jqXHR, textStatus, errorThrown) {
		alert("Server Communication Error" + "<br><br>" + "Error Code: " + jqXHR.status + "<br>" + "Error: " + jqXHR.statusText);
	});

	return jqxhr;
}

function showMsg(msg) {
	$div = $('#MsgDiv');
	$div.empty();
	$div.html(msg);

	var dialogOptions = {
		title : 'Message',
		modal : true,
	};
	$div.dialog(dialogOptions);
}

//引入js和css文件
function include(id, path, file){
	if (document.getElementById(id)==null){
        var files = typeof file == "string" ? [file] : file;
        for (var i = 0; i < files.length; i++){
            var name = files[i].replace(/^\s|\s$/g, "");
            var att = name.split('.');
            var ext = att[att.length - 1].toLowerCase();
            var isCSS = ext == "css";
            var tag = isCSS ? "link" : "script";
            var attr = isCSS ? " type='text/css' rel='stylesheet' " : " type='text/javascript' ";
            var link = (isCSS ? "href" : "src") + "='" + path + name + "'";
            document.write("<" + tag + (i==0?" id="+id:"") + attr + link + "></" + tag + ">");
        }
	}
}

// open new window
function windowOpen(url, name, width, height){
	var top=parseInt((window.screen.height-height)/2,10),left=parseInt((window.screen.width-width)/2,10),
		options="location=no,menubar=no,toolbar=no,dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes,"+
		"resizable=yes,scrollbars=yes,"+"width="+width+",height="+height+",top="+top+",left="+left;
	window.open(url ,name , options);
}

//table sort
function tableSort( configuration ){
	
	var defaults = {
		orderBy : '#orderBy', // sort field
		contentTable : '#contentTable', // table
		sortClass : 'sort', // flag sort class
		callBack : $.noop // callback
	};
	
	var config = $.extend({}, defaults, configuration);
	
	var $orderBy = $(config.orderBy),
		  $sortCol = $(config.contentTable + " th." + config.sortClass),
	 	  orderBy = $orderBy.val().split(" ");

	$sortCol.each(function(){
		if ($(this).hasClass(orderBy[0])){
			orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase() == "DESC" ? "down" : "up";
			$(this).html($(this).html() + " <i class=\"icon icon-arrow-" + orderBy[1] + "\"></i>");
		}
	});
	
	$sortCol.click(function(){
		var order = $(this).attr("class").split(" "),
			  sort = $orderBy.val().split(" ");
		
		for(var i=0; i<order.length; i++){
			if (order[i] == config.sortClass){order = order[i+1]; break;}
		}
		
		if (order == sort[0]){
			sort = (sort[1]&&sort[1].toUpperCase()=="DESC" ? "ASC" : "DESC");
			$orderBy.val(order + " DESC" != order+" " + sort ? "" : order + " " + sort);
		}else{
			$orderBy.val(order + " ASC");
		}
		
		config.callBack();
	});
}

jQuery.fn.center = function () {
    this.css("position","absolute");
    this.css("top", Math.max(0, (($(window).height() - $(this).outerHeight()) / 2) + 
                                                $(window).scrollTop()) + "px");
    this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) + 
                                                $(window).scrollLeft()) + "px");
    return this;
}

//=========================================================
//Miscellaneous utility methods
//=========================================================

//Open a popup window (or bring to front if already open)
function popup_window(winURL, winName)
{
	var props = 'scrollBars=yes,resizable=yes,toolbar=no,menubar=no,location=no,directories=no,width=640,height=480';
	popupWindow = window.open(winURL, winName, props);
	popupWindow.focus();
}


//Select all options in a <SELECT> list
function selectAll(sourceList)
{
	for(var i = 0; i < sourceList.options.length; i++)
	{
	    if ((sourceList.options[i] != null) && (sourceList.options[i].value != ""))
	        sourceList.options[i].selected = true;
	}
	return true;
}

//Deletes the selected options from supplied <SELECT> list
function removeSelected(sourceList)
{
	var maxCnt = sourceList.options.length;
	for(var i = maxCnt - 1; i >= 0; i--)
	{
	    if ((sourceList.options[i] != null) && (sourceList.options[i].selected == true))
	    {
	        sourceList.options[i] = null;
	    }
	}
}


//Disables accidentally submitting a form when the "Enter" key is pressed.
//Just add "onkeydown='return disableEnterKey(event);'" to form.
function disableEnterKey(e)
{
	 var key;
	
	 if(window.event)
	      key = window.event.keyCode;     //Internet Explorer
	 else
	      key = e.which;     //Firefox & Netscape
	
	 if(key == 13)  //if "Enter" pressed, then disable!
	      return false;
	 else
	      return true;
}



function isNetscape(v) {
	  return isBrowser("Netscape", v);
}

function isMicrosoft(v) {
	  return isBrowser("Microsoft", v);
}

function isMicrosoft() {
	  return isBrowser("Microsoft", 0);
}

//Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) \
//Chrome/42.0.2311.135 Safari/537.36 Edge/12.10136
function isMicrosoftEdge() {
	return window.navigator.userAgent.indexOf("Edge") > -1;
 }


function isBrowser(b,v) {
	  browserOk = false;
	  versionOk = false;

	  browserOk = (navigator.appName.indexOf(b) != -1);
	  if (v == 0) versionOk = true;
	  else  versionOk = (v <= parseInt(navigator.appVersion));
	  return browserOk && versionOk;
}

/*$(document).ready(function() {
	//combobox with select2
	$("select").select2();
	$('.fancybox').fancybox();
});*/


