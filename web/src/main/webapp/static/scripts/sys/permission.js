<script type="text/javascript">
    $(function () {
    	$.mdsTable.initTable("table", '${ctx}/services/api/permissions/table', {q: $("#query").val()});
		$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/permissions/table', {q: $("#query").val()});
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
				top.$.mdsDialog.confirm("<fmt:message key="table.message.deleteconfirm" />","<fmt:message key="permissionList.permission" />", {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/permissions/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="permission.deleted"/>');
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
		
		$("#btnExport").click(function(){
			top.$.mdsDialog.confirm("<fmt:message key="message.export.confirm" />","<fmt:message key="exportform.title" />", {ok: function(){
				$.fileDownload("${ctx}/sys/permissions/export")
	                .done(function () { $.mdsDialog.alert("<fmt:message key="message.export.successed" />", "<fmt:message key="exportform.title"/>"); })
	                .fail(function () { $.mdsDialog.alert("<fmt:message key="message.export.failed" />", "<fmt:message key="exportform.title"/>"); });
				//$("#searchForm").attr("action","${ctx}/sys/areas/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){	
			$.mdsDialog.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/sys/permissions/import",
			     isReadOnly: false,
			     isImportForm: true,
			     template: $("#importBox").html(),
			     formId: "#importForm",
			     postType: "multipart",
			     waitingMsg:'<fmt:message key="importform.importing"/>',
			     onPostSuccess: function(data) {
			    	 top.$.mdsDialog.confirm(data.message,"<fmt:message key="importform.title" />",
											{
												buttonsFocus:1,
												ok: function() {
													window.location.reload();
												}
											});
			     }
			});
		});
    });
	
	/*function actionFormatter(value) {
        return [
            '<a class="update" href="${ctx}/permissionform?id=' + value + '" title="<fmt:message key="button.edit.tip"/>"><i class="fa fa-edit"></i></a>',
            '<a class="remove" href="javascript:" title="<fmt:message key="button.delete.tip" />"><i class="fa fa-times-circle"></i></a>',
        ].join('');
    }*/
    
    function nameFormatter(value, row) {
   		var title = '<a href="${ctx}/sys/permissionform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }
    
    function permissionFormatter(value, row) {
    	var permissionkey = value.split(",");
   		var title = "";
   		for(x in permissionkey){
   			if (title == ""){
   				title = '<fmt:message key="${x}" />';
   			}else{
   				title += ', <fmt:message key="${x}" />';
   			}
		}
    	//var title=value;
		
   		return title;
    }

    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	//var msgParam = '<fmt:message key="permissionList.permission"/>';
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="permissionList.permission"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                    /*url: '${ctx}/permissionform?delete=&id=' + row.id,
                    type: 'post',*/
                	url: '${ctx}/services/api/permissions/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="permission.deleted"/>');
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
