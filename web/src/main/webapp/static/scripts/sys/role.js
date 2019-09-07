<script type="text/javascript">
    $(function () {
    	$.mdsTable.initTable("table", '${ctx}/services/api/roles/table', {q: $("#query").val()}, {locale:'${languageTag}'});
		$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/roles/table', {q: $("#query").val()}, {locale:'${languageTag}'});
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
				top.$.mdsDialog.confirm('<fmt:message key="table.message.deleteconfirm" />','<fmt:message key="roleList.role" />', {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/roles/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="role.deleted"/>');
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
				$.fileDownload("${ctx}/sys/roles/export")
	                .done(function () { $.mdsDialog.alert('<fmt:message key="message.export.successed" />', '<fmt:message key="exportform.title"/>'); })
	                .fail(function () { $.mdsDialog.alert('<fmt:message key="message.export.failed" />', '<fmt:message key="exportform.title"/>'); });
				//$("#searchForm").attr("action","${ctx}/sys/roles/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){	
			$.mdsDialog.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/sys/roles/import",
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
	
	function actionFormatter(value, row) {
		if (row.type=='${RoleType.ad}'){ //row.type=='${RoleType.sa}' || 
	        return [
	            '<a class="update" href="${ctx}/sys/roleAccessOrganizationform?id=' + value + '" title="<fmt:message key="role.accessPermission.tip"/>"><i class="fa fa-sitemap"></i></a>',
	            '<a class="addchild" href="${ctx}/sys/roleMenuPermissionform?id=' + value + '" title="<fmt:message key="role.menuPermission.tip" />"><i class="fa fa-list"></i></a>',
	        ].join('');
		}else if (row.type=='${RoleType.oa}'){//Organization administrator include all children
			return [
	            '<a class="update" href="${ctx}/sys/roleAccessOrganizationform?id=' + value + '" title="<fmt:message key="role.accessPermission.tip"/>"><i class="fa fa-sitemap"></i></a>',
	            '<a class="addchild" href="${ctx}/sys/roleMenuPermissionform?id=' + value + '" title="<fmt:message key="role.menuPermission.tip" />"><i class="fa fa-list"></i></a>',
	        ].join('');
		}else if (row.type=='${RoleType.ou}'){//Organization user - only owner organization
			return [
	            '<a class="update" href="${ctx}/sys/roleAccessGalleryform?id=' + value + '" title="<fmt:message key="role.accessPermission.tip"/>"><i class="fa fa-sitemap"></i></a>',
	            '<a class="addchild" href="${ctx}/sys/roleMenuPermissionform?id=' + value + '" title="<fmt:message key="role.menuPermission.tip" />"><i class="fa fa-list"></i></a>',
	        ].join('');
		}else if (row.type=='${RoleType.ga}'){//Gallery administrator include all album
			return [
				'<a class="update" href="${ctx}/sys/roleAccessAlbumform?id=' + value + '" title="<fmt:message key="role.accessPermission.tip"/>"><i class="fas fa-compact-disc"></i></a>',
	            '<a class="addchild" href="${ctx}/sys/roleMenuPermissionform?id=' + value + '" title="<fmt:message key="role.menuPermission.tip" />"><i class="fa fa-list"></i></a>',
	        ].join('');
		}else if (row.type=='${RoleType.gu}'){ //Gallery user include selected albums 
			return [
				'<a class="update" href="${ctx}/sys/roleAccessAlbumform?id=' + value + '" title="<fmt:message key="role.accessPermission.tip"/>"><i class="fas fa-compact-disc"></i></a>',
	            '<a class="addchild" href="${ctx}/sys/roleMenuPermissionform?id=' + value + '" title="<fmt:message key="role.menuPermission.tip" />"><i class="fa fa-list"></i></a>',
	        ].join('');
		}
    }
    
    function nameFormatter(value, row) {
   		var title = '<a href="${ctx}/sys/roleform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }

    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	//var msgParam = '<fmt:message key="roleList.role"/>';
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="roleList.role"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                    /*url: '${ctx}/roleform?delete=&id=' + row.id,
                    type: 'post',*/
                	url: '${ctx}/services/api/roles/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="role.deleted"/>');
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
