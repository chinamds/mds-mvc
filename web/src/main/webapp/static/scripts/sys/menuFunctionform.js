<script type="text/javascript">
	$(document).ready(function() {
		var options = {
        		theme: "bootstrap",
        		ajax: {
        		    url: '${ctx}/services/api/permissions/select2',
        		    dataType: 'json',
        		    data: function (params) {
        		      var query = {
        		        q: params.term
        		      }

        		      // Query parameters will be ?q=[term]
        		      return query;
        		    }
        		},
        		placeholder: "<fmt:message key='menuFunction.permission.tip'/>",
        		allowClear: true
    		};

   		$("#menuPermissions").select2(options);
   		
   		refreshControl();
   		
   		$('#resourceId').change(function(){ 
   			refreshControl();
   			if ($("#resourceId  option:selected").val() !='' 
   				&& $("#resourceId  option:selected").val()!='${ResourceId.none}'){
	   			$("#href").val("");
				$("#code").val($("#resourceId  option:selected").val());
   			}
   		}) 
   		
   		
        $("input[type='text']:visible:enabled:first", document.forms['menuFunctionForm']).focus();
	});
	
	function refreshControl() {
		if ($("#resourceId  option:selected").val()=='${ResourceId.none}'
			|| $("#resourceId  option:selected").val()==''){
			$("#code").attr("readOnly",false);
			$("#href").attr("readOnly",false);
		}else{
			$("#code").attr("readOnly",true);
			if ($("#resourceId  option:selected").val()!='${ResourceId.home}'){
				$("#href").attr("readOnly",true);
			}
		}
    };
</script>