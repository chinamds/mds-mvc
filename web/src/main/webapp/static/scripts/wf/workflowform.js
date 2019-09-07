<script type="text/javascript">
    $(function () {
    	if (!$('#method').val() || $('#method').val()==''){
	    	$.ajax({
	 			type: "get",
	 			async: true,
	 			url: "${ctx}/services/api/workflows/workflowdetail/" + ($("#id").val() ? $("#id").val() : 0),//
	 			contentType : 'application/json',
	            dataType : "json",
	 			success: function (data) {
	 				initAppendGrid(data);
	 			},
	 			error: function (response) {
	 				$.mdsForm.alert(response.responseText);
	 			}
	 		});
    	}else{
    		//var json1 = $('#method').val();
    		//var json2 = '${method}';
    		initAppendGrid($.parseJSON($('#method').val()));
    	}
    	
    	function initAppendGrid(data) {
    		$('#tblAppendGrid').appendGrid('init', {
				initRows: 1,
	            columns: [
	                { name: 'activity', display: '<fmt:message key="workflowDetail.activity"/>', type: 'custom', ctrlAttr: { maxlength: 200 }, ctrlCss: { width: '160px'},
	                	customBuilder: function (parent, idPrefix, name, uniqueIndex) {
		                    // Prepare the control ID/name by using idPrefix, column name and uniqueIndex
		                    var ctrlId = idPrefix + '_' + name + '_' + uniqueIndex;
		                    // Create a span as a container
		                    var ctrl = document.createElement('select');
		                    
		                    // Set the ID and name to container and append it to parent control which is a table cell
		                    $(ctrl).attr({ id: ctrlId, name: ctrlId }).css('width', '160px').appendTo(parent);
		                    
		                    var options = {
		 		            		theme: "bootstrap",
		 		            		ajax: {
		 		            		    url: '${ctx}/services/api/activities/select2',
		 		            		    dataType: 'json',
		 		            		    data: function (params) {
		 		            		      var query = {
		 		            		        q: params.term
		 		            		      }
	
		 		            		      // Query parameters will be ?q=[term]
		 		            		      return query;
		 		            		    }
		 		            		},
		 		            		placeholder: "<fmt:message key='workflow.activity.tip'/>",
		 		            		allowClear: true
		 		        		};
	
		                    $(ctrl).select2(options);
		                    
		                    // Finally, return the container control	 		              	
		                    return ctrl;
		                },
		                customGetter: function (idPrefix, name, uniqueIndex) {
		                    // Prepare the control ID/name by using idPrefix, column name and uniqueIndex
		                    var ctrlId = idPrefix + '_' + name + '_' + uniqueIndex;
		                    // Return the formatted duration
		                    return $('#' + ctrlId).val();
		                },
		                customSetter: function (idPrefix, name, uniqueIndex, value) {
		                    // Prepare the control ID/name by using idPrefix, column name and uniqueIndex
		                    var ctrlId = idPrefix + '_' + name + '_' + uniqueIndex;
		                    var sep = value.split(',');
		                    var newOption = new Option((sep && sep.length>1) ? sep[1] : '' , (sep && sep.length>0) ? sep[0] : '', true, true);
		                    // Append it to the select
		                    $('#' + ctrlId).append(newOption);
		                    // Set the value to different spinners
		                    //$('#' + ctrlId).val(value);
	
		                }
		            },
	                { name: 'apply', display: '<fmt:message key="workflowDetail.apply"/>', type: 'checkbox' },
	                { name: 'approval', display: '<fmt:message key="workflowDetail.approval"/>', type: 'checkbox' },
	                { name: 'email', display: '<fmt:message key="workflowDetail.email"/>', type: 'checkbox' },
	                { name: 'activityId', type: 'hidden', value: 0 },
	                { name: 'id', type: 'hidden', value: 0 }
	            ],
	            initData: data.rows,
	            hideRowNumColumn: false,
	            rowButtonsInFront: true,
	            hideButtons: {
	            	insert: false,
	            	append: false
	           },
			});
    	};
  	 
    	var options = {
     		theme: "bootstrap",
     		ajax: {
     		    url: '${ctx}/services/api/organizationWorkflowTypes/select2',
     		    dataType: 'json',
     		    data: function (params) {
     		      var query = {
     		        q: params.term
     		      }

     		      // Query parameters will be ?q=[term]
     		      return query;
     		    }
     		},
     		placeholder: "<fmt:message key='workflow.workflowType.tip'/>",
     		allowClear: true
 		};

		$("#workflowType").select2(options);
		
		$("#workflowType").on("change", function (e) {
		});
    });
</script>
