<script type="text/javascript">
    $(function () {
        var options = {
        		theme: "bootstrap",
        		placeholder: "<fmt:message key='permission.permission.tip'/>",
        		allowClear: true
    		};

   		$("#selectActions").select2(options);
        
        $("input[type='text']:visible:enabled:first", document.forms['permissionForm']).focus();
    });
</script>
