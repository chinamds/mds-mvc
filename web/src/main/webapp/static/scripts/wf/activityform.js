<script type="text/javascript">
	var users;
    $(function () {
    	//$('#tabs').smartTab({autoProgress: false,stopOnFocus:true,transitionEffect:'vSlide'});
    	users = $('#users').bootstrapDualListbox({
            nonSelectedListLabel: 'Non-selected',
            selectedListLabel: 'Selected',
            preserveSelectionOnMove: 'moved',
            moveOnSelect: false,
            //nonSelectedFilter: 'ion ([7-9]|[1][0-2])'
          });
    	
    	loadOrganizationsTree();
    });
    
    var selectChanged = function(e, treeId, treeNode) {
    	loadOrganizationsTree();
    	reloadUsers();
    }
    
    function loadOrganizationsTree() {
    	$.ajax({
 			type: "get",
 			async: true,
 			url: "${ctx}/services/api/activities/" + ($("#id").val() ? $("#id").val() : '0') + "/organizations/treeView?oid="+ $("#organizationId").val(),//
 			contentType : 'application/json',
            dataType : "json",
 			success: function (data) {
 				if(data && !data.length){
 					return $("#organizationsTree").html('<code>Organizations not found</code>');
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
    }
    
    function reloadUsers() {
    	var chkIds = [];
	   	 $("#users option:selected").each(function() {
	   		 chkIds.push($(this).val());
	   	 });

    	$.ajax({
 			type: "get",
 			async: true,
 			url: "${ctx}/services/api/activities/" + ($("#id").val() ? $("#id").val() : '0') + "/users/dualListbox?oid="+ $("#organizationId").val() + "&oids=" + $("#organizationIds").val() + "&uids=" + chkIds.join(','),//
 			contentType : 'application/json',
            dataType : "json",
 			success: function (data) {
 				//users.bootstrapDualListbox('destroy');
 				$('#users').empty();
 				if(data && data.length){
	 				$.each(data, function(key, val) {
	 		            /*var o = document.createElement("option")
	 		            o.value = val.id;
	 		            o.text = val.username;
	 		            if (val.selected){
	 		            	o.selected = "selected";
	 					}
	 					$('#users')[0].options.add(o);*/
	 					users.append('<option ' + (val.selected ? 'selected="selected"' : '') + ' value="' + val.id + '">' + val.username + '</option>');
	 		        });
 				}
 				/*users = $('#users').bootstrapDualListbox({
 		            nonSelectedListLabel: 'Non-selected',
 		            selectedListLabel: 'Selected',
 		            preserveSelectionOnMove: 'moved',
 		            moveOnSelect: false,
 		            //nonSelectedFilter: 'ion ([7-9]|[1][0-2])'
 		        });*/
 				users.bootstrapDualListbox('refresh');
 			},
 			error: function (response) {
 				$.mdsForm.alert(response.responseText);
 			}
 		});
    }
	 
    var checkChildNodes = function(node) {
    	var children = [];
    	children = getChildNodes(node);
    	for(x in children){
        	$('#organizationsTree').treeview('checkNode',  [children[x].nodeId, { silent: true }]);
        }
    	getCheckedNodes();
    	reloadUsers();
      };
      
      var uncheckChildNodes = function(node) {
    	  	//$('#organizationsTree').treeview('uncheckNode',  [node.nodeId, { silent: true }]);
	      	for(x in node.nodes){
	      		$('#organizationsTree').treeview('uncheckNode',  [node.nodes[x].nodeId, { silent: true }]);
	      		uncheckChildNodes(node.nodes[x]);
	      	}
	      	getCheckedNodes();
	      	reloadUsers();
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
    		if (!window.Mds.isNullOrEmpty(nodeChildren))
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
			//var tags = window.Mds.isNullOrEmpty(allNodes[x].tags);
			//var state = window.Mds.isNullOrEmpty(allNodes[x].state);
			//var checked = allNodes[x].state.checked;
			if (allNodes[x].state.checked){
				children.push(allNodes[x].id);
			}
		}
		$('#organizationIds').val(children.join(','));
	};
</script>
