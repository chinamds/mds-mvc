<script type="text/javascript">
	var data = {
		Resource:{
			myprofile:'<fmt:message key="menu.myprofile"/>',
			mymessage:'<fmt:message key="menu.mymessage"/>',
			viewallnotifications:'<fmt:message key="menu.viewallnotifications"/>',
			mynotifications:'<fmt:message key="menu.mynotifications"/>',
			ok:'<fmt:message key="button.ok" />',
			close:'<fmt:message key="button.close" />',
			addevent:'<fmt:message key="myCalendar.addevent" />',
		},
	};
	
    $(function() {
    	if (typeof window.Mds === "undefined" || !window.Mds){
    		window.Mds = {};
    	}
    	window.Mds.AppRoot = "${ctx}";
    	window.Mds.locale = "${languageTag}";
    	if (!(typeof(PNotify) === "undefined") && PNotify){
    		/*PNotify.prototype.options.styling = 'bootstrap3'; // Bootstrap version 3
    		PNotify.prototype.options.icons = 'bootstrap3'; // glyphicons*/
    		PNotify.defaults.styling = 'bootstrap4'; // Bootstrap version 4
    		PNotify.defaults.icons = 'fontawesome5'; // fontawesome version 5
    	}
    	if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0 || $('.modal-dialog').length > 0){
        	top.location = "${ctx}";
        }
    	
    	var url = Mds.AppRoot + "/i18n/localizedResources/jsResource";
        $.ajax({
            url: url,
            async: false,
            cache: false,
            dataType : "json",
            success: function (data) {
            	window.Mds.i18n = data;
            },
            error: function (re) {
            }
        });
        //$('.metismenu').metisMenu();
        //alert(window.Mds.i18n.buttonclose);
        $.mainframe.initialize();
    });
</script>