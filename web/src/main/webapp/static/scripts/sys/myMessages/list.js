<script type="text/javascript">
	var ctx = '${ctx}';
    $(function() {
    	/*$(".table").each(function() {
	        $.table.initTable($(this));
	    });*/
    	//$(".table").bootstrapTable({pageNumber: 1, pageSize: 10, pageList: [10, 20, 50, 100, 200], pagination: true});
    	//$.table.initTable($(this));
        
    	$.mdsTable.initTable("table", '${ctx}/services/api/myMessages/<shiro:principal property="id"/>/${state}/table', {q: $("#query").val()});
    	$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/myMessages/<shiro:principal property="id"/>/${state}/table', {q: $("#query").val()});
		});
    	
    	$(".btn-archive,.btn-recycle-or-delete,.btn-clear,.btn-mark-read").click(function() {
            var a = $(this);
            var seletions = $("#table").bootstrapTable('getAllSelections');
            if (seletions.length == 0){
            	top.$.mdsDialog.alert("<fmt:message key="table.message.norecordselected" />");
            	return;
            }
            
            var ids = seletions.map(function(elem){
                return elem.id;
            }).join(",");
            if(a.is(".btn-recycle-or-delete")) {
            	top.$.mdsDialog.confirm("<fmt:message key="table.message.deleteconfirm" />","<fmt:message key="myMessageList.myMessage" />", {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/myMessages/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	$.mdsDialog.alert('<fmt:message key="myMessage.deleted"/>');
		                	$("#table").bootstrapTable('refresh');
		                },
		                error: function (response) {
		                	$.mdsDialog.alert(response.responseText, "<fmt:message key="delete.deletefailed" />");
		                }
		            })
				}, buttonsFocus:1});
            } else if(a.is(".btn-moreactions-export")) {
            	top.$.mdsDialog.confirm("<fmt:message key="myMessageList.export.confirm" />","<fmt:message key="exportform.title" />",
						{
							buttonsFocus:1,
							ok: function() {
								$("#searchForm").attr("action","${ctx}/services/api/myMessages/export").submit();
							}
						});
            } else if(a.is(".btn-archive")) {
            	$.mdsDialog.waiting();
    			$.ajax({
         			type: "put",
         			async: true,
         			url: '${ctx}/services/api/myMessages/archive/<shiro:principal property="id"/>/' + ids,
         			success: function () {
         				$.mdsDialog.waitingOver();
         				$.mdsDialog.alert("<fmt:message key="myMessage.archived"/>");
       					$("#table").bootstrapTable('refresh');
         			},
         			error: function (response) {
         				$.mdsDialog.waitingOver();
         				$.mdsDialog.alert(response.responseText);
         			}
         		});
            } else if(a.is(".btn-mark-read")) {
            	$.mdsDialog.waiting();
    			$.ajax({
         			type: "put",
         			async: true,
         			url: '${ctx}/services/api/myMessages/markread/<shiro:principal property="id"/>/' + ids,
         			success: function () {
         				$.mdsDialog.waitingOver();
         				$.mdsDialog.alert("<fmt:message key="myMessage.markasread"/>");
       					$("#table").bootstrapTable('refresh');
         			},
         			error: function (response) {
         				$.mdsDialog.waitingOver();
         				$.mdsDialog.alert(response.responseText);
         			}
         		});
            }
        });

        //$.personal.message.initBtn();
    });
    
    function titleFormatter(value, row) {
    	if (row.messageFolder=="drafts"){
    		return '<a href="${ctx}/sys/messageform/draft/' + row.id + '/send" class="btn btn-link no-padding">' + value + '</a>';
    	}else{
    		var title = '<a href="${ctx}/sys/messageform/' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
    		if (row.read == false){
    			title +='&nbsp;&nbsp;<span class="label label-warning"><fmt:message key="myMessage.unread"/></span>'
    		}
    		
    		return title;
    	}
    }
        
    function actionFormatter(value) {
        return [
            '<a class="update" href="${ctx}/messageform?id=' + value + '" title="<fmt:message key="button.edit.tip"/>"><i class="fa fa-edit"></i></a>',
            '<a class="remove" href="javascript:" title="<fmt:message key="button.delete.tip" />"><i class="fa fa-times-circle"></i></a>',
        ].join('');
    }
    
	function dateFormatter(value) {
        return new Date(parseInt(value.substr(6))).format("yyyy-MM-dd hh:mm:ss");
    }

    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="myMessageList.myMessage"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                	url: '${ctx}/services/api/myMessages/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="myMessage.deleted"/>');
                    	$("#table").bootstrapTable('refresh');
                    },
                    error: function (response) {
                    	alert(response.responseText);
                    }
                })
            }
        }
    };
</script>