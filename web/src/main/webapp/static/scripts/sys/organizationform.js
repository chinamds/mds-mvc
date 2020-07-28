<script type="text/javascript">
	var uploadfileOk = true;
	function validateForm(form) {                     
		if (bCancel==true)
			return true;
		
	    var valid = validateOrganization(form);
	    if (valid == false || uploadfileOk == false) {
	    	valid = false;
	        $(".form-group").addClass('error');
	    }
	    return valid;
	}
	
	$(document).ready(function() {
		/*var areaTreeId = $.zTreeD.initSelectTree({
			zNodes : [],
			nodeType : "default",
			fullName: true,
			loadUrl : '${ctx}' + "/sys/areas/treeData",
			async : true,
			asyncLoadAll : true,
			onlyDisplayShow: false,
			lazy : true,
			select : {
				btn : $("#areaButton, #areaName"),
				id : "areaId",
				name : "areaName",
				btnId : "areaButton",
				includeRoot: false
			},
			autocomplete : {
				enable : true
			}
		});

		var parentTreeId = $.zTreeD.initSelectTree({
			zNodes : [],
			nodeType : "default",
			fullName: true,
			loadUrl : '${ctx}' + "/sys/organizations/treeData",
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
		});*/
         $("input[type='text']:visible:enabled:first", document.forms['organizationForm']).focus();
	});
	
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
		        /*processResults: function (data) {
		            // Tranforms the top-level key of the response object from 'items' to 'results'
		            return {
		              results: data.items
		            };
		       }*/
    		},
    		placeholder: "<fmt:message key='organization.preferredlanguage.tip'/>",
    		allowClear: true
		};

		$("#preferredlanguage").select2(options);
				
		var fileinputOpt = {
				theme: 'fa',
				overwriteInitial: true,
			    maxFileSize: 1024,
			    showClose: false,
			    showCaption: false,
			    browseLabel: '',
			    removeLabel: '',
			    previewZoomButtonIcons: {
		            prev: '<i class="fa fa-caret-left"></i>',
		            next: '<i class="fa fa-caret-right"></i>',
		            toggleheader: '<i class="fa fa-arrows-alt-v"></i>',
		            fullscreen: '<i class="fa fa-arrows-alt"></i>',
		            borderless: '<i class="fa fa-expand"></i>',
		            close: '<i class="fa fa-times"></i>'
		        },
			    previewFileIcon: '<i class="fa fa-file"></i>',
			    browseIcon: '<i class="fa fa-folder-open"></i>',
			    removeIcon: '<i class="fa fa-times"></i>',
		        uploadIcon: '<i class="fa fa-upload"></i>',
		        cancelIcon: '<i class="fa fa-ban"></i>',
		        msgValidationErrorIcon: '<i class="fa fa-exclamation-circle"></i> ',
			    removeTitle: 'Cancel or reset changes',			    
   	   			showUpload: false,
   	   			layoutTemplates: {main2: '{preview} {remove} {browse}'},
   	   			defaultPreviewContent: '<img src="${ctx}/static/images/logo.png" alt="organization logo">',
   	   	        allowedFileExtensions: ["jpg", "jpeg", "png", "gif", "bmp"],
   		   	    resizeImage: false
   	   	    };
   		if ($("#id").val().length > 0){
   			fileinputOpt = $.extend(fileinputOpt, {
   				initialPreview: ['${ctx}/services/api/organizations/logo/' + $("#id").val()],
   		   	    initialPreviewAsData: true, // defaults markup class="file" data-show-upload="false" data-show-caption="false"  data-allowed-file-extensions='["jpg", "png", "gif", "bmp"]'
   		   	    initialPreviewFileType: 'image', // image is the default and can be overridden in config below
   		   	    //initialPreviewDownloadUrl: '${ctx}/services/api/organizations/logo/' + $("#id").val()
   			});
   		}
   		$("#logofile").fileinput(fileinputOpt);
   		$("#logofile").on('fileloaded', function(event, file, previewId, index) {
   			uploadfileOk = true;
   			$("#removelogo").val(false);
        });
   		$('#logofile').on('fileerror', function(event, data, msg) {
   			uploadfileOk = false;
   		});
   		$('#logofile').on('fileimageresizeerror', function(event, data, msg) {
   			uploadfileOk = false;
   		});
   		$('#logofile').on('fileuploaderror', function(event, data, msg) {
   			uploadfileOk = false;
   		});
   		$('#logofile').on('fileremoved', function(event, id, index) {
   			$("#removelogo").val(true);
   		});
   		$('#logofile').on('filecleared', function(event) {
   			$("#removelogo").val(true);
   		});
	
	$.zTreeD = {
		    index : 1,
		    treeTemplate :
		        '<div id="treeSelect{id}" class="ztree"></div>',

		    selectTreeTemplate :
		    	'<ul class="dropdown-menu dropdown-menu-right"><li id="liTreeContent{id}"><div id="treeContent{id}" class="ztreepicker-widget treeContent">{tree}</div></li></ul>',
		        //'<form class="form-horizontal"><div id="treeContent{id}" class="container treeContent"><div class="row">{tree}</div></div></form>',
		        //'<ul id="treeContent{id}" class="dropdown-menu treeContent">{tree}</ul>',style="display:none; position: absolute;" '<span for="searchName{id}" class="control-label small">Name</label>' +


		    autocompleteTemplate :
			    '<form class="px-4 py-0"><div class="form-group row">' +
		            '<label for="searchName{id}" class="control-label col-sm-3">Name</label>' +
		            '<input type="text" id="searchName{id}" class="form-control col-sm-9" placeholder="Fuzzy matching, search by enter"/>' +
		        '</div></form>',

		    /**
		     * Initializes a removable tree
		     */
		    initMovableTree : function(config) {

		        config.renameUrl = config.renameUrl || (config.urlPrefix + "/ajax/{id}/rename?newName={newName}");
		        config.removeUrl = config.removeUrl || (config.urlPrefix + "/ajax/{id}/delete");
		        config.addUrl = config.addUrl || (config.urlPrefix + "/ajax/{id}/appendChild");
		        config.moveUrl = config.moveUrl || (config.urlPrefix + "/ajax/{sourceId}/{targetId}/{moveType}/move");
		        config.asyncLoadAll = config.asyncLoadAll || false;
		        config.loadUrl = config.loadUrl || (config.urlPrefix + "/ajax/load" +
		                "?async=" + config.async +
		                "&asyncLoadAll=" + config.asyncLoadAll +
		                (config.excludeId ? "&excludeId=" + config.excludeId : "") +
		                (config.onlyDisplayShow ? "&search.show_eq=true" : ""));

		        if(config.editable != false) {
		            config.editable = true;
		        }

		        if(!config.permission) {
		            config.permission = {};
		        }
		        config.permission = $.extend({
		            create: false,
		            update: false,
		            remove : false,
		            move : false
		        }, config.permission);


		        var setting = {
		            noSwitchIcon:true,
		            async: {
		                enable: config.async,
		                url: config.loadUrl,
		                autoParam:["id"],
		                dataFilter: $.zTree.filter
		            },
		            view: {
		                addHoverDom: config.permission.create ? addHoverDom : null,
		                removeHoverDom: config.permission.create ? removeHoverDom : null,
		                selectedMulti: false
		            },
		            edit: {
		                enable: true,
		                editNameSelectAll: true,
		                showRemoveBtn : config.permission.remove ? function(treeId, treeNode) {return !treeNode.root;} : null,
		                showRenameBtn: config.permission.update,
		                removeTitle: "Remove",
		                renameTitle: "Rename",
		                drag : {
		                    isMove: config.permission.move,
		                    isCopy : false,
		                    prev: drop,
		                    inner: drop,
		                    next: drop
		                }
		            },
		            data: {
		                simpleData: {
		                    enable: true
		                }
		            },
		            callback:{
		                beforeRemove: function(treeId, treeNode) { return confirm("Are you sure to delete?")},
		                beforeRename : beforeRename,
		                onRemove: onRemove,
		                onRename: onRename,
		                onDrop : onDrop
		            }
		        };

		        if(!config.editable) {
		            setting.edit = {};
		            setting.view.addHoverDom = null;
		            setting.view.removeHoverDom = null;
		        }

		        if(config,setting) {
		            setting = $.extend(true, config.setting, setting);
		            config.setting = setting;
		        }

		        function drop(treeId, nodes, targetNode) {
		            if(!targetNode || !targetNode.getParentNode()) {
		                return false;
		            }
		            for (var i = 0, l = nodes.length; i < l; i++) {
		                if (nodes[i].root === true) {
		                    return false;
		                }
		            }
		            return true;
		        }


		        function addHoverDom(treeId, treeNode) {
		            var sObj = $("#" + treeNode.tId + "_span");
		            if (treeNode.editNameFlag || $("#addBtn_" + treeNode.id).length > 0) return;
		            var addStr = "<span class='button add' id='addBtn_" + treeNode.id
		                + "' title='添加子节点' onfocus='this.blur();'></span>";
		            sObj.after(addStr);
		            var btn = $("#addBtn_" + treeNode.id);
		            if (btn)
		                btn.bind("click", function (e) {
		                    onAdd(e, treeId, treeNode);
		                    return false;
		                });
		        }
		        function removeHoverDom(treeId, treeNode) {
		            $("#addBtn_" + treeNode.id).unbind().remove();
		        }

		        function beforeRename(treeId, treeNode, newName) {
		            var oldName = treeNode.name;
		            if (newName.length == 0) {
		                $.mdsDialog.alert({
		                    message : "节点名称不能为空。"
		                });
		                return false;
		            }
		            if(!confirm("确认重命名吗？")) {
		                var zTree = $.fn.zTree.getZTreeObj(treeId);
		                zTree.cancelEditName(treeNode.name);
		                return false;
		            }
		            return true;
		        }
		        /**
		         * 重命名结束
		         * @param e
		         * @param treeId
		         * @param treeNode
		         */
		        function onRename(e, treeId, treeNode) {
		            var url = config.renameUrl.replace("{id}", treeNode.id).replace("{newName}",treeNode.name);
		            $.mdsDialog.waiting("操作中...", true);
		            $.getJSON(url, function (data) {
		                $.mdsDialog.waitingOver();
		            });
		        }
		        /**
		         * 重命名结束
		         * @param e
		         * @param treeId
		         * @param treeNode
		         */
		        function onRemove(e, treeId, treeNode) {
		            var url = config.removeUrl.replace("{id}", treeNode.id);
		            $.mdsDialog.waiting("操作中...", true);
		            $.getJSON(url, function (data) {
		                $.mdsDialog.waitingOver();
		            });
		        }

		        /**
		         * 添加新节点
		         * @param e
		         * @param treeId
		         * @param treeNode
		         */
		        function onAdd(e, treeId, treeNode) {
		            var url = config.addUrl.replace("{id}", treeNode.id);
		            $.mdsDialog.waiting("操作中...", true);
		            $.getJSON(url, function(newNode) {
		                var node = { id:newNode.id, pId:newNode.pId, name:newNode.name, iconSkin:newNode.iconSkin, open: true,
		                    click : newNode.click, root :newNode.root,isParent:newNode.isParent};
		                var newNode = zTree.addNodes(treeNode, node)[0];
//		                zTree.selectNode(newNode);
		                $("#" + newNode.tId + "_a").click();

		                $.mdsDialog.waitingOver();
		            });
		        }

		        /**
		         * 移动结束
		         * @param event
		         * @param treeId
		         * @param treeNodes
		         * @param targetNode
		         * @param moveType
		         * @param isCopy
		         */
		        function onDrop(event, treeId, treeNodes, targetNode, moveType, isCopy) {
		            if(!targetNode || treeNodes.length == 0) {
		                return;
		            }
		            var sourceId = treeNodes[0].id;
		            var targetId = targetNode.id;
		            var moveType = moveType;
		            var url = config.moveUrl.replace("{sourceId}", sourceId).replace("{targetId}", targetId).replace("{moveType}", moveType);
		            $.mdsDialog.waiting("操作中...", true);
		            $.getJSON(url, function (newNode) {
		                $.mdsDialog.waitingOver();
		            });
		        }

		        var autocomplateEnable = config.autocomplete && config.autocomplete.enable;

		        var id = this.index++;
		        var treeStr = (autocomplateEnable ? this.autocompleteTemplate : '') + this.treeTemplate;
		        var container = null;
		        if(config.containerId) {
		            container = $("#" + config.containerId);
		        } else {
		            container = $("body");
		        }
		        container.append(treeStr.replace(/{id}/g, id));
		        var treeSelect = "treeSelect" + id;
		        var zTree = $.fn.zTree.init($("#" + treeSelect), setting, config.zNodes);


		        if(autocomplateEnable) {
		            if(!config.autocomplete.minLength) config.autocomplete.minLength = 0;
		            config.autocomplete.enterSearch = true;
		            config.autocomplete.input = $("#searchName" + id);
		            config.autocomplete.async = config.autocomplete.async || config.async;
		            config.autocomplete.select = config.autocomplete.select || function(event, ui) { //按照名字搜索
		                var searchName = ui.item.value;
		                var url = config.loadUrl + "&searchName=" + searchName;
		                zTree.destroy();
		                $.getJSON(url, function(zNodes) {
		                    if(zNodes.length > 0) { //如果没找到节点就不必展示
		                        zTree = $.fn.zTree.init($("#" + treeSelect), setting, zNodes);
		                    }
		                });
		            };
		            config.autocomplete.source = config.autocomplete.source
		                || config.urlPrefix + "/ajax/autocomplete?1=1" +
		                  (config.excludeId ? "&excludeId=" + config.excludeId : "") +
		                  (config.onlyDisplayShow ? "&search.show_eq=true" : "");

		            config.treeId = treeSelect;
		            window.Mds.initAutocomplete(config.autocomplete);
		        }

		        return treeSelect;

		    },

		    /**
		     * @param nodeType node type
		     * @param zNodes all nodes
		     * @param idDomId 要保存的编号的dom id
		     * @param nameDomId 要保存的名称的dom id
		     * nodeType, zNodes, async, loadUrl, btn, idDomId, nameDomId, autocomplete, autocompleteUrl
		     */
		    initSelectTree : function(config) {
		        config.asyncLoadAll = config.asyncLoadAll || false;
		        config.loadUrl =
		            config.loadUrl || (config.urlPrefix + "/ajax/load" +
		                "?async=" + config.async +
		                "&asyncLoadAll=" + config.asyncLoadAll +
		                (config.excludeId ? "&excludeId=" + config.excludeId : "") +
		                (config.onlyDisplayShow ? "&search.show_eq=true" : "") +
		                "&onlyCheckLeaf=" + ((config.setting && config.setting.check && config.setting.check.onlyCheckLeaf) ? true : false));
		        var autocomplateEnable = config.autocomplete && config.autocomplete.enable;

		        var id = this.index++;
		        var treeStr = (autocomplateEnable ? this.autocompleteTemplate : '') + this.treeTemplate;
		        var treeContentStr = this.selectTreeTemplate.replace("{tree}", treeStr);
		        //$("body").append(treeContentStr.replace(/{id}/g, id)); .append($('<ul class="dropdown-menu dropdown-menu-right"></ul>')
		        //$("#selectAreaBtn").append(treeContentStr.replace(/{id}/g, id));
		        
		        var $id = $("#" + config.select.id);
		        var $name = $("#" + config.select.name);
		        var $btn = $("#" + config.select.btnId);
		        //var parent = $name.after($(treeContentStr.replace(/{id}/g, id))).parent();
		        var parent = $btn.after($(treeContentStr.replace(/{id}/g, id))).parent();
		        
		        var treeContent = "treeContent" + id;
		        var liTreeContent = "liTreeContent" + id;
		        var $treeContent = $("#" + treeContent);
		        var treeSelect = "treeSelect" + id;
		        //hideMenu();      
		        var nameOffset = $name.offset();
	            var position = $name.position();
	            $treeContent.css({
	                width: $name.outerWidth() + $btn.outerWidth()
	            });

		        var setting = {
		            noSwitchIcon:true,
		            async: {
		                enable: config.async,
		                url:config.loadUrl,
		                autoParam:["id"],
		                dataFilter: $.zTree.filter
		            },
		            view: {
		                dblClickExpand: false
		            },
		            data: {
		                simpleData: {
		                    enable: true
		                }
		            },
		            callback: {
		                onClick: selectNode,
		                onCheck: selectNode
		            }
		        };

		        if(config.setting) {
		            setting = $.extend(true, config.setting, setting);
		        }

		        function fullName(node) {
		            var names = node.name;

		            while((node = node.getParentNode())) {
		                if(node.root && !config.select.includeRoot) {
		                    break;
		                }
		                names = node.name + " > " + names;
		            }
		            return names;
		        }

		        function selectNode(e, treeId, treeNode) {
		            if(!setting.check || !setting.check.enable) {
		                var nodes = zTree.getSelectedNodes();
		                var lastNode = nodes[nodes.length - 1];
		                $name.prop("value", fullName(lastNode));
		                $id.prop("value", lastNode.id);
		            } else {
		                var nodes = zTree.getCheckedNodes(true);
		                var names = "";
		                var ids = "";
		                var onlySelectLeaf = config.setting.check && config.setting.check.onlySelectLeaf;
		                for (var i = 0, l = nodes.length; i < l; i++) {
		                    var node = nodes[i];
		                    if(onlySelectLeaf && node.isParent) {
		                        continue;
		                    }
		                    names += fullName(node) + (i != l - 1 ? "," : "");
		                    ids += node.id + (i != l - 1 ? "," : "");
		                }

		                $name.prop("value", names);
		                $name.change();
		                $id.prop("value", ids);
		                $id.change();
		            }
		            hideMenu();
		        }

		        function hideMenu() {
		        	$("#" + liTreeContent).parent('.dropdown-menu').dropdown('toggle');
		            $("body").unbind("mousedown", onBodyDown);
		        }
		        
		        $("#" + liTreeContent).parent('.dropdown-menu').on('shown.bs.dropdown', function () {
		        	$("body").bind("mousedown", onBodyDown);
		        })
		        
		        $("#" + liTreeContent).on('click', function (e) {
		            e.preventDefault;
		            this.blur();
		            e.stopPropagation();
		        });

		        function onBodyDown(event) {
		            var isBtn = false;
		            config.select.btn.each(function() {
		                isBtn = isBtn ||
		                        event.target == this ||
		                        event.target.parentNode == this ||
		                        (event.target.parentNode ? event.target.parentNode.parentNode : null) == this;
		            });
		            if (!(isBtn || $(event.target).closest(".ui-autocomplete").length > 0  || event.target.id == treeContent 
		            		|| $(event.target).closest("#" + treeContent).length > 0 || event.target.id == liTreeContent 
		            		|| $(event.target).closest("#" + liTreeContent).length > 0)) {
		                hideMenu();
		            }
		        }

		        var zTree = null;
		        var initTree = function() {
		            $.zTree.prepareZNodes(config.zNodes, config);
		            zTree = $.fn.zTree.init($("#" + treeSelect), setting, config.zNodes);

		            if(autocomplateEnable) {
		                if(!config.autocomplete.minLength) config.autocomplete.minLength = 0;
		                config.autocomplete.enterSearch = true;
		                config.autocomplete.input = $("#searchName" + id);
		                config.autocomplete.async = config.autocomplete.async || config.async;
		                config.autocomplete.select = config.autocomplete.select || function(event, ui) { //search by name
		                    var searchName = ui.item.value;
		                    var url = config.loadUrl + "&searchName=" + searchName;
		                    zTree.destroy();
		                    $.getJSON(url, function(zNodes) {
		                        var zNodesLength = zNodes.length;
		                        if(zNodesLength > 0) { //If the specified node not found, no need to expand tree
		                            $.zTree.prepareZNodes(zNodes, config);
		                            zTree = $.fn.zTree.init($("#" + treeSelect), setting, zNodes);
		                        }
		                    });
		                };
		                config.autocomplete.source = config.autocomplete.source
		                    || config.urlPrefix + "/ajax/autocomplete?1=1" +
		                       (config.excludeId ? "&excludeId=" + config.excludeId : "") +
		                       (config.onlyDisplayShow ? "&search.show_eq=true" : "");

		                config.treeId = treeSelect;
		                window.Mds.initAutocomplete(config.autocomplete);
		            }
		        };
		        var initialize = false;
		        if(config.lazy) {
		            config.select.btn.click(function() {
		                if(!initialize) {
		                    initTree();
		                    initialize = true;
		                }
		            });
		        } else {
		            initTree();
		        }

		        return treeSelect;

		    },
		    prepareZNodes : function(zNodes, config) {
		        if(!zNodes) {
		            return;
		        }
		        var zNodesLength = zNodes.length;
		        if(!zNodesLength) {
		            return;
		        }
		        var onlySelectLeaf = config.setting && config.setting.check && config.setting.check.onlySelectLeaf;

		        for(var i = 0; i < zNodesLength; i++) {
		            var node = zNodes[i];

		            if(onlySelectLeaf && node.isParent) {
		                node.nocheck = true;
		            } else {
		                node.nocheck = false;
		            }
		        }
		    },
		    initMaintainBtn : function(maintainUrlPrefix, tableId, async) {
		        var updateUrl = maintainUrlPrefix + "/{id}/update",
		            deleteUrl = maintainUrlPrefix + "/batch/delete",
		            moveTreeUrl = maintainUrlPrefix + "/{id}/move?async=" + async;

		        $("#moveTree").off("click").on("click", function () {
		            var table = $("#" + tableId);
		            var checkbox = $.table.getFirstSelectedCheckbox(table);
		            if(!checkbox.length) {
		                return;
		            }

		            if(checkbox.filter("[root='true']").length) {
		                $.mdsDialog.alert({
		                    message : "根节点不能移动！"
		                });
		                return;
		            }
		            window.location.href = moveTreeUrl.replace("{id}", checkbox.val()) + "&BackURL=" + $.table.encodeTableURL(table);
		            return false;
		        });

		        $("#updateTree").off("click").on("click", function() {
		            var table = $("#" + tableId);
		            var checkbox = $.table.getFirstSelectedCheckbox(table);
		            if(!checkbox.length) {
		                return;
		            }
		            window.location.href = updateUrl.replace("{id}", checkbox.val()) + "?BackURL=" + $.table.encodeTableURL(table);
		        });

		        $("#deleteTree").off("click").on("click", function () {
		            var table = $("#" + tableId);
		            var checkbox = $.table.getAllSelectedCheckbox(table);
		            if(!checkbox.length) {
		                return;
		            }

		            if(checkbox.filter("[root='true']").length) {
		                $.mdsDialog.alert({
		                    message : "您删除的数据中包含根节点，根节点不能删除！"
		                });
		                return;
		            }
		            $.mdsDialog.confirm({
		                width:500,
		                message : "确认删除吗？",
		                ok : function() {
		                    window.location.href = deleteUrl + "?" + checkbox.serialize() + "&BackURL=" + $.table.encodeTableURL(table);
		                }
		            });
		            return false;
		        });


		    },
		    initMoveBtn : function() {
		        $("#moveAsPrev").click(function() {
		            $("#moveType").val("prev");
		        });
		        $("#moveAsNext").click(function() {
		            $("#moveType").val("next");
		        });
		        $("#moveAsInner").click(function() {
		            $("#moveType").val("inner");
		        });

		    }
		    ,
		    split : function( val ) {
		    return val.split( /,\s*/ );
		    },
		    extractLast : function( term ) {
		        return this.split( term ).pop();
		    }
		    ,
		    filter : function(treeId, parentNode, childNodes) {
		        if (!childNodes) return null;
//		        for (var i=0, l=childNodes.length; i<l; i++) {
//		            childNodes[i].name = childNodes[i].name.replace(/\.n/g, '.');
//		        }
		        return childNodes;
		    }

		}
</script>