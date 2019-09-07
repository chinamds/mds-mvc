<script type="text/javascript">
    $(function () {
    	$.mdsTable.initTable("table", '${ctx}/services/api/galleries/table', {q: $("#query").val()});
		$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/galleries/table', {q: $("#query").val()});
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
				top.$.mdsForm.confirm("<fmt:message key="table.message.deleteconfirm" />","<fmt:message key="galleryList.gallery" />", {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/galleries/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="gallery.deleted"/>');
		                	$("#table").bootstrapTable('refresh');
		                },
		                error: function (response) {
		                	$.mdsForm.alert(response.responseText, "<fmt:message key="delete.deletefailed" />");
		                }
		            })
				}, buttonsFocus:1});
			}else{
				alert("<fmt:message key="table.message.norecordselected" />");
			}
		});
		
		$("#btnExport").click(function(){
			top.$.mdsForm.confirm("<fmt:message key="message.export.confirm" />","<fmt:message key="exportform.title" />", {ok: function(){
				$.fileDownload("${ctx}/cm/galleries/export")
	                .done(function () { $.mdsForm.alert("<fmt:message key="message.export.successed" />", "<fmt:message key="exportform.title"/>"); })
	                .fail(function () { $.mdsForm.alert("<fmt:message key="message.export.failed" />", "<fmt:message key="exportform.title"/>"); });
				//$("#searchForm").attr("action","${ctx}/cm/areas/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){	
			$.mdsForm.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/cm/galleries/import",
			     isReadOnly: false,
			     isImportForm: true,
			     template: $("#importBox").html(),
			     formId: "#importForm",
			     postType: "multipart",
			     waitingMsg:'<fmt:message key="importform.importing"/>',
			     onPostSuccess: function(data) {
			    	 top.$.mdsForm.confirm(data.message,"<fmt:message key="importform.title" />",
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
            '<a class="update" href="${ctx}/galleryform?id=' + value + '" title="<fmt:message key="button.edit.tip"/>"><i class="fa fa-edit"></i></a>',
            '<a class="remove" href="javascript:" title="<fmt:message key="button.delete.tip" />"><i class="fa fa-remove-circle"></i></a>',
        ].join('');
    }*/ 
    
    function templateFormatter(value) {
   		var title = '<input type="checkbox" disabled="disabled"' +  (value ? ' checked="checked"' : '') + '/>';
		
   		return title;
    }
    
    function nameFormatter(value, row) {
   		var title = '<a href="${ctx}/cm/galleryform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }
    
    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	//var msgParam = '<fmt:message key="galleryList.gallery"/>';
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="galleryList.gallery"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                    /*url: '${ctx}/galleryform?delete=&id=' + row.id,
                    type: 'post',*/
                	url: '${ctx}/services/api/galleries/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="gallery.deleted"/>');
                    	$("#table").bootstrapTable('refresh');
                    },
                    error: function (response) {
                    	$.mdsForm.alert(response.responseText, "<fmt:message key="delete.deletefailed" />");
                    }
                })
            }
        }
    };
</script>
