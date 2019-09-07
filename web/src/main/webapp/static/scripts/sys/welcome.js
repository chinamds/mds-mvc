<script>
    $(function() {
    	var data = {
    			i18n:{
    				myprofile:'<fmt:message key="menu.personal.myprofiles.title"/>',
    				mymessage:'<fmt:message key="menu.personal.mymessages.title"/>',
    				viewallnotifications:'<fmt:message key="menu.viewallnotifications"/>',
    				mynotifications:'<fmt:message key="menu.personal.mynotifications.title"/>',
    				ok:'<fmt:message key="button.ok" />',
    				close:'<fmt:message key="button.close" />',
    				addevent:'<fmt:message key="myCalendar.addevent" />',
    				viewevent:'<fmt:message key="myCalendar.viewevent" />',
    				deleteevent:'<fmt:message key="myCalendar.deleteevent" />',
    				suretodelete:'<fmt:message key="myCalendar.suretodelete" />',
    			},
    		};
    	    	
    	$.mainframe.initOptions(data);
        $.mainframe.initCommonBtn();
        $("legend").click(function() {
            var next = $(this).next();
            if(next.is(":hidden")) {
                $(this).find("i").removeClass("fa fa-angle-double-up");
                $(this).find("i").addClass("fa fa-angle-double-down");
                next.slideDown(300);
            } else {
                next.slideUp(300);
                $(this).find("i").removeClass("fa fa-angle-double-down");
                $(this).find("i").addClass("fa fa-angle-double-up");
            }
        });
        $.mainframe.initCalendar();
    });
</script>