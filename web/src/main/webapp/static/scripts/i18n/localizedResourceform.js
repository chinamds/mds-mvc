<script type="text/javascript">
	$(document).ready(function() {
		var options = {
        		theme: "bootstrap",
        		ajax: {
        		    url: '${ctx}/services/api/cultures/select2',
        		    dataType: 'json',
        		    data: function (params) {
        		      var query = {
        		        q: params.term
        		      }

        		      // Query parameters will be ?q=[term]
        		      return query;
        		    }
        		},
        		placeholder: "<fmt:message key='localizedResource.culture.tip'/>",
        		allowClear: true
    		};

   		$("#culture").select2(options);
   		
   		var options = {
        		theme: "bootstrap",
        		ajax: {
        		    url: '${ctx}/services/api/neutralResources/' + $("#resourceCategory").val() + '/select2',
        		    dataType: 'json',
        		    data: function (params) {
        		      var query = {
        		        q: params.term
        		      }

        		      // Query parameters will be ?q=[term]
        		      return query;
        		    }
        		},
        		placeholder: "<fmt:message key='localizedResource.neutralResource.tip'/>",
        		allowClear: true
    		};

   		$("#neutralResource").select2(options);
	});
</script>