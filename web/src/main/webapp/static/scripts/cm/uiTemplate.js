<script type="text/javascript">
    $(function () {
        //new bootstrap.Offcanvas('#offcanvasRight');
        
		initTable();
		$("#buttonSearch").click(function(){
			initTable();
		});
		
		$("#delete").click(function(){
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
				top.$.mdsForm.confirm('<fmt:message key="table.message.deleteconfirm" />','<fmt:message key="uiTemplateList.uiTemplate" />', {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/uiTemplates/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="uiTemplate.deleted"/>');
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
    });

	function initTable(){
		var searchTerm = $("#query").val();
		var defaults = {
				 url: '${ctx}/services/api/uiTemplates/table',   
				 queryParams: function(params) {
					var temp = {   
								limit: params.limit,
								offset: params.offset,
								q: searchTerm,
						};
					return temp;
				},
				//toolbar: "#toolbar",  
				sidePagination: "server",  
				pageNumber: 1,    
				pageSize: 10,   
				pageList: [
					10, 20, 50, 100, 200 
				],
				pagination: true,  
				showRefresh: false, 
				showColumns: false, 
				searchOnEnterKey: true,  
				search:false    
			};
		
		$("#table").bootstrapTable(defaults);
	}
	
	function addAction() {
        var offcanvasEdit = bootstrap.Offcanvas.getInstance('#offcanvasEdit');
        if (offcanvasEdit){
            offcanvasEdit.hide();
        }
        
        adjustOffcanvasWithBreakpoint($("#offcanvasAdd"));
                
        var iframe = $("#uiTemplateAddFrame");
        iframe.prop("src", '${ctx}/cm/uiTemplateform');
        frameOnLoad(iframe, '#offcanvasAdd');
        
        $('#offcanvasAddLabel').text('<fmt:message key="uiTemplateDetail.title"/>');
        bootstrap.Offcanvas.getOrCreateInstance('#offcanvasAdd').show();
    }
    
    function editAction(id) {
        adjustOffcanvasWithBreakpoint($("#offcanvasEdit"));
        
        var iframe = $("#uiTemplateEditFrame");
        iframe.prop("src", '${ctx}/cm/uiTemplateform?id=' + id);

        frameOnLoad(iframe, '#offcanvasEdit');
        
        $('#offcanvasEditLabel').text('<fmt:message key="uiTemplateDetail.title"/>');
        bootstrap.Offcanvas.getOrCreateInstance('#offcanvasEdit').show();
    }
    
    function adjustOffcanvasWithBreakpoint($offcanvas){
        var currentBreakpoint = bootstrapDetectBreakpoint();
        if (currentBreakpoint){
            if (currentBreakpoint.index > 1){
                if ($offcanvas.hasClass('w-100'))
                    $offcanvas.removeClass('w-100');
                $offcanvas.width(600);
            }else{
                if (!$offcanvas.hasClass('w-100'))
                    $offcanvas.addClass('w-100');
            }
        }
    }
    
    function frameOnLoad(iframe1, offcanvasId){
        $(iframe1).on("load", function() {
            var form =iframe1.contents().find("#uiTemplateForm");
            //var form =$(panel.content).find("#editForm");
            form.find("#cancel").click(function(){
                var windowjQuery = iframe1[0].contentWindow.$; 
                if (!windowjQuery.data(form[0], 'changed')) {
                    // close the form
                    bootstrap.Offcanvas.getInstance(offcanvasId).hide();
                }else{
                    if (confirm('<fmt:message key="discardChanges.confirm"/>')){
                        bootstrap.Offcanvas.getInstance(offcanvasId).hide();
                    }
                }
            });
            form.find("#delete").click(function(){
                var templateId = $("#id", form).val();
                deleteTemplate(templateId, function() {bootstrap.Offcanvas.getInstance(offcanvasId).hide();});
            });
            form.find("#save").click(function() {
                var form =iframe1.contents().find("#uiTemplateForm");
                if(!iframe1[0].contentWindow.validateUiTemplate(form.get(0))) {
                    form.find(".form-group").addClass('error');
                    
                    return;
                }
                
                var url = "${ctx}/cm/uiTemplateform";
                $.ajax({
                    url: url,
                    type: 'POST',
                    cache: false,
                    data: new FormData(form[0]),
                    processData: false,
                    contentType: false,
                    success: function (result) {
                        if(result){
                            $.mdsShowResult(result, '<fmt:message key="uiTemplateList.uiTemplate"/>');
                            if(result.status == 200){
                                bootstrap.Offcanvas.getInstance(offcanvasId).hide();
                                $("#table").bootstrapTable('refresh');
                            }
                         }
                    },
                    error: function (err) {
                        alert(response.responseText);
                    }
                });
            });
        });
    }
	
	function actionFormatter(value, row) {
        var editTip = '<fmt:message key="button.edit.tip"/>';
        var delTip = '<fmt:message key="button.delete.tip"/>';
		if (row.permanent){
<c:if test="${fns:isPermitted('cm:uiTemplates:edit')}">
	        return [
	            '<a class="update" href="javascript:editAction(' + value + ')" title="' + editTip + '"><i class="fa fa-edit"></i></a>',
	        ].join('');
</c:if>		
			return '';
		}
		
		<c:if test="${fns:isPermitted('cm:uiTemplates:delete') and fns:isPermitted('cm:uiTemplates:edit')}">
        return [
            '<a class="update" href="javascript:editAction(' + value + ')" title="' + editTip + '"><i class="fa fa-edit"></i></a>',
            '<a class="remove" href="javascript:" title="' + delTip + '"><i class="fa fa-times-circle"></i></a>',
        ].join('');
        </c:if>
        <c:if test="${fns:isPermitted('cm:uiTemplates:delete')}">
        return [
            '<a class="remove" href="javascript:" title="' + delTip + '"><i class="fa fa-times-circle"></i></a>',
        ].join('');
        </c:if>
        <c:if test="${fns:isPermitted('cm:uiTemplates:edit')}">
        return [
            '<a class="update" href="javascript:editAction(' + value + ')" title="' + editTip + '"><i class="fa fa-edit"></i></a>',
        ].join('');
        </c:if>
    }
    
    function stateFormatter(value, row) {
    	if (row.permanent){
			return {
		        disabled: true
		      }
		}
	    
	    return value
    }
    
    function dateTimeFormatter(value) {
        return new Date(parseInt(value.substr(6))).format("yyyy-MM-dd hh:mm"); // hh:mm:ss
    }
    
    function deleteTemplate(id, callback) {
        var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="uiTemplateList.uiTemplate"/></fmt:param></fmt:message>';
        if (confirmMessage(msgDelConfirm)) {
            $.ajax({
                /*url: '${ctx}/uiTemplateform?delete=&id=' + row.id,
                type: 'post',*/
                url: '${ctx}/services/api/uiTemplates/' + id,
                type: 'DELETE',
                success: function () {
                    alert('<fmt:message key="uiTemplate.deleted"/>');
                    $("#table").bootstrapTable('refresh');
                    if (callback)
                        callback();
                },
                error: function (response) {
                    $.mdsForm.alert(response.responseText, '<fmt:message key="delete.deletefailed" />');
                }
            });
        }
    }

    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	//var msgParam = '<fmt:message key="uiTemplateList.uiTemplate"/>';
        	deleteTemplate(row.id);
        }
    };
</script>
