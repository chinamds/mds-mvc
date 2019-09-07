<script type="text/javascript">
	$(document).ready(function() {
		/*var options = {
        		theme: "bootstrap",
        		placeholder: "<fmt:message key='menuFunction.permission.tip'/>",
        		allowClear: true
    		};

   		$("#menuPermissions").select2(options);
   		
		var organizationTreeId = $.zTree.initSelectTree({
			zNodes : [],
			nodeType : "default",
			fullName: true,
			loadUrl : '${ctx}' + "/sys/organizations/treeData",
			async : true,
			asyncLoadAll : true,
			onlyDisplayShow: false,
			lazy : true,
			select : {
				btn : $("#organizationButton, #organizationName"),
				id : "organizationId",
				name : "organizationName",
				btnId : "organizationButton",
				includeRoot: false
			},
			autocomplete : {
				enable : false
			}
		});*/
		var options = {
     		theme: "bootstrap",
     		ajax: {
     		    url: '${ctx}/services/api/galleries/organization/select2',
     		    dataType: 'json',
     		    data: function (params) {
     		      var query = {
     		        q: params.term,
    		        oid: $('#organizationId').val(),
    		        rtype: $('#type').val()
     		      }

     		      // Query parameters will be ?q=[term]
     		      return query;
     		    }
     		},
     		placeholder: "<fmt:message key='role.gallery.tip'/>",
     		allowClear: true
 		};

		$("#roleGalleries").select2(options);
				
		$('#type').change(function(){ 
   			if ($("#type  option:selected").val() !='' 
   				&& ($("#type  option:selected").val()=='${RoleType.ga}' 
   					|| $("#type  option:selected").val()=='${RoleType.gu}' 
   						|| $("#type  option:selected").val()=='${RoleType.gg}')){
	   			$(".rolegalleries").removeClass("hidden d-none");
	   			$("#roleGalleries").select2(options);
   			}else{
   				if (!$(".rolegalleries").hasClass("hidden"))
   					$(".rolegalleries").addClass("hidden d-none");
   			}
   			
   		})
   		
   		$("input[type='text']:visible:enabled:first", document.forms['roleForm']).focus();
	});
</script>