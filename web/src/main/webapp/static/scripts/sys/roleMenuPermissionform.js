<script type="text/javascript">
    $(function() {
    	$.ajax({
 			type: "get",
 			async: true,
 			url: "${ctx}/services/api/roles/" + $("#id").val() + "/menuPermissions/treeView",//
 			contentType : 'application/json',
            dataType : "json",
 			success: function (data) {
 				if(data && !data.length){
 					return $("#menuFunctionPermissionTree").html('<code>Role Permission not found</code>');
 				}
 				
 				$('#menuFunctionPermissionTree').treeview({
 		          levels: 5,//level
 		          color: "#428bca",
	 		      expandIcon: 'fa fa-plus',
	 			  collapseIcon: 'fa fa-minus',
	 			  loadingIcon: 'fa fa-hourglass',
	 			  emptyIcon: 'fa',
	 			  checkedIcon: 'far fa-check-square',
	 			  partiallyCheckedIcon: 'fa fa-expand',
	 			  uncheckedIcon: 'far fa-square',
	 			  tagsClass:'badge badge-primary badge-pill',
 		          showTags: true,//show tags
 		          showCheckbox: true,
 		          data: data,//data
 		          onNodeChecked: function(event, node) {
 		        	  //if (node.nodes && node.nodes.length > 0)
 		        		  checkChildNodes(node);
 		        	  //var children = getChildNodes(node, children);
 		        	 //parent.frames['listFrame'].location.href='${ctx}/sys/albums/' + data.id + "/update?async=" + async ;
 		        	 //$('#menuFunctionPermissionTree').treeview('checkNode',  [children, { silent: true }]);
 		          },
 		          onNodeUnchecked: function(event, node) {
		        	  //if (node.nodes && node.nodes.length > 0)
		        		  uncheckChildNodes(node);
		          },
 		        });
 			},
 			error: function (response) {
 				$.mdsDialog.alert(response.responseText);
 			}
 		});
    });
    
    var checkChildNodes = function(node) {
    	//$('#menuFunctionPermissionTree').treeview('checkNode',  [node.nodeId, { silent: true }]);
    	//for(x in node.nodes){
    	//	$('#menuFunctionPermissionTree').treeview('checkNode',  [node.nodes[x].nodeId, { silent: false }]);
    		//checkChildNodes(node.nodes[x]);
    	//}
    	
    	var children = [];
    	children = getChildNodes(node);
    	for(x in children){
    		//if (children[x].state){
    			$('#menuFunctionPermissionTree').treeview('checkNode',  [children[x], { silent: true }]);
    			children[x].state.checked = true;
    		//}
        }
    	for(x in children){
   			children[x].state.checked = true;
        }
    	/*for(x in node.nodes){
    		checkChildNodes(node.nodes[x]);
    	}*/
      };
      
      var uncheckChildNodes = function(node) {
    	  	//$('#menuFunctionPermissionTree').treeview('uncheckNode',  [node.nodeId, { silent: true }]);
	      	for(x in node.nodes){
	      		node.nodes[x].state.checked = true;
	      		$('#menuFunctionPermissionTree').treeview('uncheckNode',  [node.nodes[x], { silent: true }]);
	      		uncheckChildNodes(node.nodes[x]);
	      	}
        };
    
      function getChildNodes(node) {
    	var children = [];
    	for(x in node.nodes){
    		//$('#menuFunctionPermissionTree').treeview('checkNode',  [x, { silent: true }]);
    		children.push(node.nodes[x]);
    		//children = getChildNodes(node.nodes[x], children);
    	}
    	
    	for(y in node.nodes){
    		var nodeChildren = getChildNodes(node.nodes[y]);
    		if (!window.Mds.isNullOrEmpty(nodeChildren))
    			children = children.concat(nodeChildren);
    	}
    	
    	return children;
      };
      
	function getCheckedNodes() {
		//var node = $('#menuFunctionPermissionTree').treeview('getNode', 1);
		var allNodes = $('#menuFunctionPermissionTree').treeview('getEnabled', 1);
		var children = [];
		for(x in allNodes){
			var node = allNodes[x];
			//var tags = window.Mds.isNullOrEmpty(allNodes[x].tags);
			//var state = window.Mds.isNullOrEmpty(allNodes[x].state);
			//var checked = allNodes[x].state.checked;
			if (!allNodes[x].tags && allNodes[x].state.checked){
				children.push(allNodes[x].id);
			}
		}
		$('#menuPermissions').val(children.join(','));
	};
</script>