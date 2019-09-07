<script type="text/javascript">
    $(function() {
    	/*var organizations = $('#organizations').bootstrapDualListbox({
            nonSelectedListLabel: 'Non-selected',
            selectedListLabel: 'Selected',
            preserveSelectionOnMove: 'moved',
            moveOnSelect: false,
            //nonSelectedFilter: 'ion ([7-9]|[1][0-2])'
          });*/
    	
    	$.ajax({
 			type: "get",
 			async: true,
 			url: "${ctx}/services/api/roles/" + $("#id").val() + "/organizations/treeView",//
 			contentType : 'application/json',
            dataType : "json",
 			success: function (data) {
 				if(data && !data.length){
 					return $("#organizationsTree").html('<code>Role Permission not found</code>');
 				}
 				
 				$('#organizationsTree').treeview({
 		          levels: 5,//level
 		          color: "#428bca",
 		          nodeIcon: "fa fa-list",
 		          showTags: true,//show tags
 		          showCheckbox: true,
 		          data: data,//data
 		          onNodeChecked: function(event, node) {
	        		  checkChildNodes(node);
 		          },
 		          onNodeUnchecked: function(event, node) {
		        	  //if (node.nodes && node.nodes.length > 0)
		        		  uncheckChildNodes(node);
		          },
 		        });
 			},
 			error: function (response) {
 				$.mdsForm.alert(response.responseText);
 			}
 		});
    });
    
    var checkChildNodes = function(node) {
    	var children = [];
    	children = getChildNodes(node);
    	for(x in children){
        	$('#organizationsTree').treeview('checkNode',  [children[x], { silent: true }]);
        }
      };
      
      var uncheckChildNodes = function(node) {
    	  	//$('#organizationsTree').treeview('uncheckNode',  [node.nodeId, { silent: true }]);
	      	for(x in node.nodes){
	      		node.nodes[x].state.checked = true;
	      		$('#organizationsTree').treeview('uncheckNode',  [node.nodes[x], { silent: true }]);
	      		uncheckChildNodes(node.nodes[x]);
	      	}
        };
    
      function getChildNodes(node) {
    	var children = [];
    	for(x in node.nodes){
    		//$('#organizationsTree').treeview('checkNode',  [x, { silent: true }]);
    		children.push(node.nodes[x]);
    		//children = getChildNodes(node.nodes[x], children);
    	}
    	
    	for(y in node.nodes){
    		var nodeChildren = getChildNodes(node.nodes[y]);
    		if (!window.Dcm.isNullOrEmpty(nodeChildren))
    			children = children.concat(nodeChildren);
    	}
    	
    	return children;
      };
      
	function getCheckedNodes() {
		//var node = $('#organizationsTree').treeview('getNode', 1);
		var allNodes = $('#organizationsTree').treeview('getEnabled', 1);
		var children = [];
		for(x in allNodes){
			var node = allNodes[x];
			//var tags = window.Dcm.isNullOrEmpty(allNodes[x].tags);
			//var state = window.Dcm.isNullOrEmpty(allNodes[x].state);
			//var checked = allNodes[x].state.checked;
			if (allNodes[x].state.checked){
				children.push(allNodes[x].id);
			}
		}
		$('#organizationIds').val(children.join(','));
	};
</script>