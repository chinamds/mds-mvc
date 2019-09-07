<script type="text/javascript">
	$(document).ready(function() {
		$.mdsTable.initTable("table", '${ctx}/services/api/localizedResources/table', {q: $("#query").val()});
		$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/localizedResources/table', {q: $("#query").val()});
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
				top.$.mdsDialog.confirm("<fmt:message key="table.message.deleteconfirm" />","<fmt:message key="localizedResourceList.localizedResource" />", {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/localizedResources/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="localizedResource.deleted"/>');
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
		
		$(".btn-moreactions-import,.btn-moreactions-export,.btn-moreactions-init").click(function() {
            var a = $(this);
            if(a.is(".btn-moreactions-import")) {
            	$.mdsDialog.showFormDialog({
	   			     title: '<fmt:message key="importform.title" />',
	   			     postUrl: "${ctx}/i18n/localizedResources/import",
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
            	top.$.mdsDialog.confirm("<fmt:message key="localizedResourceList.export.confirm" />","<fmt:message key="exportform.title" />",
						{
							buttonsFocus:1,
							ok: function() {
								$("#searchForm").attr("action","${ctx}/i18n/localizedResources/export").submit();
							}
						});
            } else if(a.is(".btn-moreactions-init")) {
            	$.mdsDialog.waiting();
    			$.ajax({
         			type: "post",
         			async: true,
         			url: "${ctx}/i18n/localizedResources/initialize",
         			dataType : "json",
         			success: function (data) {
         				$.mdsDialog.waitingOver();
       					$.mdsShowResult(data, "<fmt:message key="localizedResource.initialize"/>");
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
     			url: "${ctx}/i18n/localizedResources/initialize",
     			dataType : "json",
     			success: function (data) {
     				$.mdsDialog.waitingOver();
   					$.mdsShowResult(data, "<fmt:message key="localizedResource.initialize"/>");
     			},
     			error: function (response) {
     				$.mdsDialog.waitingOver();
     				alert(response.responseText);
     			}
     		});
		});
		
		$("#btnExport").click(function(){
			top.$.mdsDialog.confirm("<fmt:message key="message.export.confirm" />","<fmt:message key="exportform.title" />", {ok: function(){
				$.fileDownload("${ctx}/i18n/localizedResources/export")
	                .done(function () { $.mdsDialog.alert("<fmt:message key="message.export.successed" />", "<fmt:message key="exportform.title"/>"); })
	                .fail(function () { $.mdsDialog.alert("<fmt:message key="message.export.failed" />", "<fmt:message key="exportform.title"/>"); });
				//$("#searchForm").attr("action","${ctx}/i18n/areas/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){
			$.mdsDialog.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/i18n/localizedResources/import",
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

	/*function actionFormatter(value) {
        return [
            '<a class="update" href="${ctx}/localizedResourceform?id=' + value + '" title="<fmt:message key="button.edit.tip"/>"><i class="fa fa-edit"></i></a>',
            '<a class="remove" href="javascript:" title="<fmt:message key="button.delete.tip" />"><i class="fa fa-times-circle"></i></a>',
        ].join('');
    }*/
    
    function nameFormatter(value, row) {
   		var title = '<a href="${ctx}/i18n/localizedResourceform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }
    
    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	//var msgParam = '<fmt:message key="localizedResourceList.localizedResource"/>';
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="localizedResourceList.localizedResource"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                    /*url: '${ctx}/localizedResourceform?delete=&id=' + row.id,
                    type: 'post',*/
                	url: '${ctx}/services/api/localizedResources/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="localizedResource.deleted"/>');
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