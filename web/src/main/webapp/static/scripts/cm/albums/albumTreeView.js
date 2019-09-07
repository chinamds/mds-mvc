<script type="text/javascript">
    var async = ${not empty param.async and param.async eq true};
    $(function() {
    	$.post("${ctx}/cm/albums/tree",{},function(data){
			if(data && !data.length){
				return $("#albumTree").html('<code>No Folder</code>');
			}
			
			$('#albumTree').treeview({
	          levels: 5,//level
	          color: "#428bca",
	          nodeIcon: "fa fa-user",
	          showTags: true,//show tags
	          data: data,//data
	          onNodeSelected: function(event, data) {
	        	  parent.frames['listFrame'].location.href='${ctx}/sys/albums/' + data.id + "/update?async=" + async ;
	          },
	        });
		},'json');
    });
</script>