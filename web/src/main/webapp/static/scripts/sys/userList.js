<script type="text/javascript">
    $(function () {
    	$.mdsTable.initTable("table", '${ctx}/services/api/users/table', {q: $("#query").val()});
		$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/users/table', {q: $("#query").val()});
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
				top.$.mdsDialog.confirm('<fmt:message key="table.message.deleteconfirm" />','<fmt:message key="userList.user" />', {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/users/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="user.deleted"/>');
		                	$("#table").bootstrapTable('refresh');
		                },
		                error: function (response) {
		                	alert(response.responseText);
		                }
		            })
				}, buttonsFocus:1});
			}else{
				alert('<fmt:message key="table.message.norecordselected" />');
			}
		});
		
		$("#btnExport").click(function(){
			top.$.mdsDialog.confirm('<fmt:message key="message.export.confirm" />','<fmt:message key="exportform.title" />', {ok: function(){
				$.fileDownload("${ctx}/sys/users/export")
	                .done(function () { $.mdsDialog.alert('<fmt:message key="message.export.successed" />', '<fmt:message key="exportform.title"/>'); })
	                .fail(function () { $.mdsDialog.alert('<fmt:message key="message.export.failed" />', '<fmt:message key="exportform.title"/>'); });
				//$("#searchForm").attr("action","${ctx}/sys/users/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){	
			$.mdsDialog.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/sys/users/import",
			     isReadOnly: false,
			     isImportForm: true,
			     template: $("#importBox").html(),
			     formId: "#importForm",
			     postType: "multipart",
			     waitingMsg:'<fmt:message key="importform.importing"/>',
			     onPostSuccess: function(data) {
			    	 top.$.mdsDialog.confirm(data.message,'<fmt:message key="importform.title" />',
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
	
	function actionFormatter(value) {
        return [
            '<a class="update" href="${ctx}/sys/userform?id=' + value + 'from=list" title="<fmt:message key="button.edit.tip"/>"><i class="fa fa-edit"></i></a>',
            '<a class="addchild" href="${ctx}/sys/userform/menuPermissions?id=' + value + '" title="<fmt:message key="menuFunctionPermissionList.heading" />"><i class="fa fa-list"></i></a>',
        ].join('');
    }
    
    function nameFormatter(value, row) {
   		var title = '<a href="${ctx}/sys/userform?id=' + row.id + '&from=list" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }

    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	//var msgParam = '<fmt:message key="userList.user"/>';
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="userList.user"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                    /*url: '${ctx}/userform?delete=&id=' + row.id,
                    type: 'post',*/
                	url: '${ctx}/services/api/users/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="user.deleted"/>');
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
