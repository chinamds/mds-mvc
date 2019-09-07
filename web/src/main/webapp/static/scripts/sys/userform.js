<script type="text/javascript">
	$(document).ready(function() {
		var options = {
        		theme: "bootstrap",
        		ajax: {
        		    url: '${ctx}/services/api/roles/organization/select2',
        		    dataType: 'json',
        		    data: function (params) {
        		      var query = {
        		        q: params.term,
        		        oid: $('#organizationId').val()
        		      }

        		      // Query parameters will be ?q=[term]
        		      return query;
        		    }
        		},
        		placeholder: "<fmt:message key='user.roles.tip'/>",
        		allowClear: true
    		};

   		$("#userRoles").select2(options);
	});
</script>