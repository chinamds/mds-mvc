<script type="text/javascript">
	var ctx = '${ctx}';
    $(function() {
    	//view.jsp
        $('.mymessage .accordion-toggle').on('click', function () {
            var toggleBtn = $(this);
            var target = $(toggleBtn.attr("href"));
            var id = target.attr("id").replace("collapse", "");
            if(!target.data("loaded")) {
                target.data("loaded", true);
                toggleBtn.append("<img class='loading' src=ctx + '/static/images/loading.gif' style='height:20px'>");
                target.find(".accordion-inner").load(ctx + "/sys/myMessages/" + id + "/content", function() {
                    toggleBtn.find(".loading").remove();
                });
                
                initViewBtn(target);
            }
        });        
    });
    
    function initViewBtn(target) {
        target.find(".btn-view-delete").click(function() {
            var href = $(this).data("href");
            var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="myMessageList.myMessage"/></fmt:param></fmt:message>';
            top.$.mdsDialog.confirm(msgDelConfirm,"<fmt:message key="myMessage.messageoperate.delete" />",
			{
				buttonsFocus:1,
				ok: function() {
					location.href = href;
				}
			});
            
            return false;
        });
        
        target.find(".btn-view-discard").click(function() {
            var href = $(this).data("href");
            top.$.mdsDialog.confirm("<fmt:message key="myMessage.suretodiscard" />","<fmt:message key="myMessage.messageoperate.discard" />",
			{
				buttonsFocus:1,
				ok: function() {
					location.href = href;
				}
			});
            
            return false;
        });
    }
</script>
