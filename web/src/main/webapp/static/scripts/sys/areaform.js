<script type="text/javascript">
	$(document).ready(function() {
		var areaTreeId = $.zTree.initSelectTree({
			zNodes : [],
			nodeType : "default",
			fullName: true,
			loadUrl : '${ctx}' + "/sys/areas/treeData",
			async : true,
			asyncLoadAll : true,
			onlyDisplayShow: false,
			lazy : true,
			select : {
				btn : $("#parentButton, #parentName"),
				id : "parentId",
				name : "parentName",
				btnId : "parentButton",
				includeRoot: false
			},
			autocomplete : {
				enable : false
			}
		});
	});
</script>