<script type="text/javascript">
	$(document).ready(function() {
		$.mdsTable.initTable("table", '${ctx}/services/api/neutralResources/table', {q: $("#query").val()});
		$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/neutralResources/table', {q: $("#query").val()});
		});

		$("#delete").click(function(){
			//alert('getSelections: ' + JSON.stringify($("#table").bootstrapTable('getSelections')));
			var seletions = $("#table").bootstrapTable('getAllSelections');
			var ids="";
			for(x in seletions){
				if (ids.length>0){
					ids+=",";
					ids+=seletions[x].id;
				}else{
					ids += seletions[x].id;
				}
			}
			if (ids.length>0){
				top.$.mdsDialog.confirm("<fmt:message key="table.message.deleteconfirm" />","<fmt:message key="neutralResourceList.neutralResource" />", {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/neutralResources/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="neutralResource.deleted"/>');
		                	$("#table").bootstrapTable('refresh');
		                },
		                error: function (response) {
		                	$.mdsDialog.alert(response.responseText, "<fmt:message key="delete.deletefailed" />");
		                }
		            })
				}, buttonsFocus:1});
			}else{
				alert("<fmt:message key="table.message.norecordselected" />");
			}
		});
		
		//$("#treeTable").treeTable({expandLevel : 5});
		/*<%
			out.write("alert(\"test data ok\");");
	    %>*/
		//<c:out value = "${testdata}" escapeXml = "false" />
		$(".btn-moreactions-import,.btn-moreactions-export,.btn-moreactions-init").click(function() {
            var a = $(this);
            if(a.is(".btn-moreactions-import")) {
            	$.mdsDialog.showFormDialog({
	   			     title: '<fmt:message key="importform.title" />',
	   			     postUrl: "${ctx}/i18n/neutralResources/import",
	   			     isReadOnly: false,
	   			     isImportForm: true,
	   			     template: $("#importBox").html(),
	   			     formId: "#importForm",
	   			     postType: "multipart",
	   			     waitingMsg:'<fmt:message key="importform.importing"/>',
	   			     onPostSuccess: function(data) {
	   			    	 $.mdsDialog.alert(data.message, "<fmt:message key="importform.title" />");
	   			    	 //window.location.reload();
	   			     }
	   			});
            } else if(a.is(".btn-moreactions-export")) {
            	top.$.mdsDialog.confirm("<fmt:message key="neutralResourceList.export.confirm" />","<fmt:message key="exportform.title" />",
						{
							buttonsFocus:1,
							ok: function() {
								$("#searchForm").attr("action","${ctx}/i18n/neutralResources/export").submit();
							}
						});
            } else if(a.is(".btn-moreactions-init")) {
            	$.mdsDialog.waiting();
    			$.ajax({
         			type: "post",
         			async: true,
         			url: "${ctx}/i18n/neutralResources/initialize",
         			dataType : "json",
         			success: function (data) {
         				$.mdsDialog.waitingOver();
       					$.mdsShowResult(data, "<fmt:message key="neutralResource.initialize"/>");
         			},
         			error: function (response) {
         				$.mdsDialog.waitingOver();
         				alert(response.responseText);
         			}
         		});
            }
        });
		
		$("#btnInit").click(function(){
			$.mdsDialog.waiting();
			$.ajax({
     			type: "post",
     			async: true,
     			url: "${ctx}/i18n/neutralResources/initialize",
     			dataType : "json",
     			success: function (data) {
     				$.mdsDialog.waitingOver();
   					$.mdsShowResult(data, "<fmt:message key="neutralResource.initialize"/>");
     			},
     			error: function (response) {
     				$.mdsDialog.waitingOver();
     				alert(response.responseText);
     			}
     		});
		});
		
		$("#btnExport").click(function(){
			top.$.mdsDialog.confirm("<fmt:message key="neutralResourceList.export.confirm" />","<fmt:message key="exportform.title" />",
									{
										buttonsFocus:1,
										ok: function() {
											$("#searchForm").attr("action","${ctx}/i18n/neutralResources/export").submit();
										}
									});
		});
		
		$("#btnImport").click(function(){
			$.mdsDialog.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/i18n/neutralResources/import",
			     isReadOnly: false,
			     isImportForm: true,
			     template: $("#importBox").html(),
			     formId: "#importForm",
			     postType: "multipart",
			     waitingMsg:'<fmt:message key="importform.importing"/>',
			     onPostSuccess: function(data) {
			    	 $.mdsDialog.alert(data.message, "<fmt:message key="importform.title" />");
			    	 //window.location.reload();
			     }
			});
		});
	});
	
	function nameFormatter(value, row) {
   		var title = '<a href="${ctx}/i18n/neutralResourceform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }
    
    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	//var msgParam = '<fmt:message key="neutralResourceList.neutralResource"/>';
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="neutralResourceList.neutralResource"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                    /*url: '${ctx}/neutralResourceform?delete=&id=' + row.id,
                    type: 'post',*/
                	url: '${ctx}/services/api/neutralResources/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="neutralResource.deleted"/>');
                    	$("#table").bootstrapTable('refresh');
                    },
                    error: function (response) {
                    	$.mdsDialog.alert(response.responseText, "<fmt:message key="delete.deletefailed" />");
                    }
                })
            }
        }
    };
</script>