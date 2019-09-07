<script type="text/javascript">
    $(function () {
    	$.mdsTable.initTable("table", '${ctx}/services/api/players/organization/table', {q: $("#query").val()});
    	 
		$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/players/organization/table', {q: $("#query").val()});
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
				top.$.mdsForm.confirm('<fmt:message key="table.message.deleteconfirm" />','<fmt:message key="playerList.player" />', {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/players/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="player.deleted"/>');
		                	$("#table").bootstrapTable('refresh');
		                },
		                error: function (response) {
		                	$.mdsForm.alert(response.responseText, '<fmt:message key="delete.deletefailed" />');
		                }
		            })
				}, buttonsFocus:1});
			}else{
				alert('<fmt:message key="table.message.norecordselected" />');
			}
		});
		
		$("#btnExport").click(function(){
			top.$.mdsForm.confirm('<fmt:message key="message.export.confirm" />','<fmt:message key="exportform.title" />', {ok: function(){
				$.fileDownload("${ctx}/pm/players/export")
	                .done(function () { $.mdsForm.alert('<fmt:message key="message.export.successed" />', '<fmt:message key="exportform.title"/>'); })
	                .fail(function () { $.mdsForm.alert('<fmt:message key="message.export.failed" />', '<fmt:message key="exportform.title"/>'); });
				//$("#searchForm").attr("action","${ctx}/pm/areas/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){	
			$.mdsForm.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/pm/players/import",
			     isReadOnly: false,
			     isImportForm: true,
			     template: $("#importBox").html(),
			     formId: "#importForm",
			     postType: "multipart",
			     waitingMsg:'<fmt:message key="importform.importing"/>',
			     onPostSuccess: function(data) {
			    	 top.$.mdsForm.confirm(data.message,'<fmt:message key="importform.title" />',
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
            '<a class="update" href="${ctx}/pm/playerform?&id=' + value + '" title="<fmt:message key="button.edit.tip"/>"><i class="fa fa-edit"></i></a>'
            //'<a class="remove" href="javascript:" title="<fmt:message key="button.delete.tip" />"><i class="glyphicon glyphicon-remove-circle"></i></a>',
        ].join('');
    } 
    
    function thumbnailFormatter(value) {
    	var title = '<div class="mds_ns">'; 
    	title += value;
    	title += '</div>';

   		return title;
    }
    
    function dateFormatter(value) {
    	if (value && value.length > 6){
    		return new Date(parseInt(value.substr(6))).format("yyyy-MM-dd"); // hh:mm:ss
    	}
    	
    	return '';
    }
    
    function dateTimeFormatter(value) {
    	if (value && value.length > 6){
    		return new Date(parseInt(value.substr(6))).format("yyyy-MM-dd hh:mm:ss"); // hh:mm:ss
    	}
    	
    	return '';
    }
	
	function timeFormatter(value) {
		if (value && value.length > 6){
			return new Date(parseInt(value.substr(6))).format("hh:mm"); // hh:mm:ss
		}
		
		return '';
    }
    
    function nameFormatter(value, row) {
   		var title = '<a href="${ctx}/pm/playerform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }
    
    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	//var msgParam = '<fmt:message key="playerList.player"/>';
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="playerList.player"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                    /*url: '${ctx}/playerform?delete=&id=' + row.id,
                    type: 'post',*/
                	url: '${ctx}/services/api/players/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="player.deleted"/>');
                    	$("#table").bootstrapTable('refresh');
                    },
                    error: function (response) {
                    	$.mdsForm.alert(response.responseText, '<fmt:message key="delete.deletefailed" />');
                    }
                })
            }
        }
    };
</script>
