<script type="text/javascript">
	var ctx = '${ctx}';
	$(document).ready(function() {
		//$("#menuFunctionList").treeTable({expandLevel : 5});
		var searchTerm = $("#query").val();
		$.mdsTreeTable.initTable("table", '${ctx}/services/api/menuFunctions/treeTable', {q: searchTerm}, {pagination: false, striped: false});
		
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
				top.$.mdsDialog.confirm("<fmt:message key="table.message.deleteconfirm" />","<fmt:message key="menuFunctionList.menuFunction" />", {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/menuFunctions/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="menuFunction.deleted"/>');
		                	$("#table").bootstrapTable('refresh');
		                },
		                error: function (response) {
		                	alert(response.responseText);
		                }
		            })
				}, buttonsFocus:1});
			}else{
				alert("<fmt:message key="table.message.norecordselected" />");
			}
			
		});
		
		$("#btnExport").click(function(){
			top.$.mdsDialog.confirm("<fmt:message key="message.export.confirm" />","<fmt:message key="exportform.title" />", {ok: function(){
				$.fileDownload("${ctx}/sys/menuFunctions/export")
	                .done(function () { $.mdsDialog.alert("<fmt:message key="message.export.successed" />", "<fmt:message key="exportform.title"/>"); })
	                .fail(function () { $.mdsDialog.alert("<fmt:message key="message.export.failed" />", "<fmt:message key="exportform.title"/>"); });
				//$("#searchForm").attr("action","${ctx}/sys/menuFunctions/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){
			$.mdsDialog.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/sys/menuFunctions/import",
			     isReadOnly: false,
			     isImportForm: true,
			     //templateUrl: '${ctx}/sys/menuFunctions',
			     template: $("#importBox").html(),
			     formId: "#importForm",
			     postType: "multipart",
			     waitingMsg:'<fmt:message key="importform.importing"/>',
			     onPostSuccess: function(data) {
			    	 top.$.mdsDialog.confirm(data.message,"<fmt:message key="importform.title" />",
											{
												buttonsFocus:1,
												ok: function() {
													//window.location.reload();
													$("#table").bootstrapTable('refresh');
												}
											});
			     }
			});
		});
	});
	
	function codeFormatter(value, row) {
   		var title = '<a href="${ctx}/sys/menuFunctionform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }
	        
    function actionFormatter(value) {
        return [
            //'<a class="update" href="${ctx}/sys/menuFunctionform?id=' + value + '" title="<fmt:message key="button.edit.tip"/>"><i class="fa fa-edit"></i></a>',
            '<a class="addchild" href="${ctx}/sys/menuFunctionform?parentId=' + value + '" title="<fmt:message key="button.addchild.tip" />"><i class="fas fa-level-down-alt"></i></a>',
            //'<a class="remove" href="javascript:" title="<fmt:message key="button.delete.tip" />"><i class="fa fa-times-circle"></i></a>',
        ].join('');
    }

    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="menuFunctionList.menuFunction"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                	url: '${ctx}/services/api/menuFunctions/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="menuFunction.deleted"/>');
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