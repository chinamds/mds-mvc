<script type="text/javascript">
(function($) {
	// return if disabled
	/*if (!$("#${id}Button").hasClass("disabled")){*/
		// Initialize zTree
		var zTreeId = $.zTree.initSelectTree({
			zNodes : [],
			nodeType : "default",
			fullName: true,
			loadUrl : '${ctx}' + "${url}",
			async : true,
			asyncLoadAll : true,
			onlyDisplayShow: false,
			lazy : true,
			select : {
				btn : $("#${id}Button, #${id}Name"),
				id : "${id}Id",
				name : "${id}Name",
				includeRoot: ${allowSelectRoot ? true : false} 
			},
			autocomplete : {
				enable : false
			}
		});
	/*}*/
});
</script>