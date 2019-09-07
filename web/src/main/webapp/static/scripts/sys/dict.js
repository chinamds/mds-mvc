<script type="text/javascript">
	$(document).ready(function() {
		$.mdsTable.initTable("table", '${ctx}/services/api/dicts/table');
		$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/dicts/table');
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
				top.$.mdsDialog.confirm("<fmt:message key="table.message.deleteconfirm" />","<fmt:message key="dictList.dict" />", {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/dicts/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="dict.deleted"/>');
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
		//table sort
		//tableSort({callBack : page});
		/*$('#searchadv').on('click', function (e) {
            e.preventDefault;
            this.blur();
            $('#catalogue').val('');
        });
		
		$('#advanceSearchForm').on('click', function (e) {
            e.preventDefault;
            this.blur();
            e.stopPropagation();
        });*/
		
		$('[data-tooltip="popover"]').webuiPopover('destroy').webuiPopover({
	        content: function() {
	            return $("#advanceSearchBox").html();
	        },
	        placement: "bottom",
	        constrains: 'horizontal'
	        //container: $(".box") //NOTE HERE
	    }).on('shown.webui.popover',function(){
           $('#catalogue').focus();
        });
		
	    $('[data-tooltip="popover"]').on('click', function(e) {
	        e.stopPropagation();
	    });
	    
	    $('body').on('click','#advanceSearchForm',function(e){
	    	console.log(e);
	    	if ($(e.target).attr("id") != 'btnSubmit'){
		    	e.preventDefault;
	            this.blur();
	            e.stopPropagation();
	    	}
        });
		/*$('#searchadv').on('click', function (e) {
			var notice = new PNotify({
			    text: $('#advanceSearchForm').html(),
			    icon: false,
			    width: 'auto',
			    hide: false,
			    buttons: {
			        closer: false,
			        sticker: false
			    },
			    insert_brs: false
			});
			notice.get().find('form.pf-form').on('click', '[name=cancel]', function() {
			    notice.remove();
			}).submit(function() {
			    var username = $(this).find('input[name=username]').val();
			    if (!username) {
			        alert('Please provide a username.');
			        return false;
			    }
			    notice.update({
			        title: 'Welcome',
			        text: 'Successfully logged in as ' + username,
			        icon: true,
			        width: PNotify.prototype.options.width,
			        hide: true,
			        buttons: {
			            closer: true,
			            sticker: true
			        },
			        type: 'success'
			    });
			    return false;
			});
		});*/
		
		$("#btnExport").click(function(){
			top.$.mdsDialog.confirm("<fmt:message key="message.export.confirm" />","<fmt:message key="exportform.title" />", {ok: function(){
				$.fileDownload("${ctx}/sys/dicts/export")
	                .done(function () { $.mdsDialog.alert("<fmt:message key="message.export.successed" />", "<fmt:message key="exportform.title"/>"); })
	                .fail(function () { $.mdsDialog.alert("<fmt:message key="message.export.failed" />", "<fmt:message key="exportform.title"/>"); });
				//$("#searchForm").attr("action","${ctx}/sys/dicts/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){	
			$.mdsDialog.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/sys/dicts/import",
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
        '<a class="update" href="${ctx}/dictform?id=' + value + '" title="<fmt:message key="button.edit.tip"/>"><i class="fa fa-edit"></i></a>',
        '<a class="remove" href="javascript:" title="<fmt:message key="button.delete.tip" />"><i class="fa fa-times-circle"></i></a>',
    ].join('');
	}*/

	function wordFormatter(value, row) {
			var title = '<a href="${ctx}/sys/dictform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
			return title;
	}
	
	// update and delete events
	window.actionEvents = {
	    'click .remove': function (e, value, row) {
	    	//var msgParam = '<fmt:message key="dictList.dict"/>';
	    	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="dictList.dict"/></fmt:param></fmt:message>';
	        if (confirmMessage(msgDelConfirm)) {
	            $.ajax({
	                /*url: '${ctx}/dictform?delete=&id=' + row.id,
	                type: 'post',*/
	            	url: '${ctx}/services/api/dicts/' + row.id,
	                type: 'DELETE',
	                success: function () {
	                	alert('<fmt:message key="dict.deleted"/>');
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