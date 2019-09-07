<script type="text/javascript">
    $(function () {
    	//$.mdsTable.initTable("table", '${ctx}/services/api/contentLists/table', {q: $("#query").val()});
    	/*if ($("#method").val()){
    		$.mdsTable.initTable("table2", '${ctx}/services/api/contentLists/contentlistitem/'+ $("#id").val() ? $("#id").val() : 0, null, {pagination: false, striped: false});
    	}*/
    	if (!$('#jsonItems').val() || $('#jsonItems').val()==''){
	    	$.ajax({
	 			type: "get",
	 			async: true,
	 			url: "${ctx}/services/api/contentLists/contentlistitem/" + ($("#id").val() ? $("#id").val() : 0),//
	 			contentType : 'application/json',
	            dataType : "json",
	 			success: function (data) {
	 				initAppendGrid(data);
	 			},
	 			error: function (response) {
	 				$.mdsForm.alert(response.responseText);
	 			}
	 		});
    	}else{
    		//var json1 = $('#jsonItems').val();
    		//var json2 = '${jsonItems}';
    		initAppendGrid($.parseJSON($('#jsonItems').val()));
    	}
    	
    	function initAppendGrid(data) {
    		$('#tblAppendGrid').appendGrid('init', {
				initRows: 1,
	            columns: [
	            	{ name: 'content', display: '<fmt:message key="contentListZone.content"/>', type: 'text', ctrlAttr: { maxlength: 200 }, ctrlCss: { width: '160px'} },
	                { name: 'thumbnailHtml', display: '<fmt:message key="contentListZone.fileName"/>', type: 'custom', ctrlAttr: { maxlength: 200, readonly:'readonly' }, ctrlCss: { width: '160px'},
		               customBuilder: function (parent, idPrefix, name, uniqueIndex) {
		                    // Prepare the control ID/name by using idPrefix, column name and uniqueIndex
		                    var ctrlId = idPrefix + '_' + name + '_' + uniqueIndex;
		                    // Create a span as a container
		                    var ctrl = document.createElement('div');
		                    //$(ctrl).addClass("dcm_ns").appendTo(parent);
		                    //ctrl = document.createElement('ui');
		                  
		                    // Set the ID and name to container and append it to parent control which is a table cell
		                    $(ctrl).attr({ id: ctrlId, name: ctrlId }).addClass("dcm_ns").appendTo(parent);
	
		                    // Finally, return the container control	 		              	
		                    return ctrl;
		                },
		                customGetter: function (idPrefix, name, uniqueIndex) {
		                    // Return the formatted duration
		                    return '';
		                },
		                customSetter: function (idPrefix, name, uniqueIndex, value) {
		                    // Prepare the control ID/name by using idPrefix, column name and uniqueIndex
		                    var ctrlId = idPrefix + '_' + name + '_' + uniqueIndex;
		                    // Set the value to different spinners
		                    $('#' + ctrlId).html(value);
	
		                }
	                },
	                { name: 'contentTypeDisplay', display: '<fmt:message key="contentListZone.contentType"/>', type: 'text', ctrlAttr: { maxlength: 50, readonly:'readonly' }, ctrlCss: { width: '100px'} },
	                { name: 'duration', display: '<fmt:message key="contentListZone.duration"/>', type: 'text', ctrlAttr: { maxlength: 20 }, ctrlCss: { width: '50px', 'text-align': 'right' }, value: 0 },
	                { name: 'mute', display: '<fmt:message key="contentListZone.mute"/>', type: 'checkbox' },
	                { name: 'aspectRatio', display: '<fmt:message key="contentListZone.aspectRatio"/>', type: 'checkbox' },
	                { name: 'timeFrom', display: '<fmt:message key="contentListZone.timeFrom"/>', type: 'time', ctrlCss: { width: '100px'} },
	                { name: 'timeTo', display: '<fmt:message key="contentListZone.timeTo"/>', type: 'time', ctrlCss: { width: '100px'} },
	                { name: 'contentObjectId', type: 'hidden', value: 0 },
	                { name: 'contentListItemId', type: 'hidden', value: 0 },
	                { name: 'id', type: 'hidden', value: 0 },
	                { name: 'contentType', type: 'hidden', value: 0 },
	                { name: 'fileName', type: 'hidden', value: '' }
	            ],
	            initData: data.rows,
	            hideRowNumColumn: false,
	            rowButtonsInFront: true,
	            hideButtons: {
	            	insert: true,
	            	append: true
	           },
	           customFooterButtons: [
	                { uiButton: { icon: 'ui-icon-folder-open', label: '<fmt:message key="button.import"/>' }, btnAttr: { title: '<fmt:message key="button.import"/>' }, click: function (evt) {
	                	openAlbumTreePicker();
	                	/*$('#rootwizard').smartWizard("next");
	                	$.mdsForm.waiting();
	                	setTimeout(function () {
	                		var instance = $('#albumTreeView').jstree(true);
	                    instance.deselect_all();
	                    instance.select_node('tv_${selectedAlbumIds[0]}');
	                   $.mdsForm.waitingOver();
	       			}, 1000);*/
	                	}, atTheFront: true }
	           ]
			});
    	}
    	       
    	
    	
    	// Toolbar extra buttons
        var btnFinish = $('<button></button>').text('<fmt:message key="button.wizard.finish" />')
                                         .addClass('btn btn-info btn-finish')
                                         .on('click', function(){
                                        	 var chkIds = [];
                                        	 $("#albumThumbView .thmb input:checked").each(function() {
                                        		 chkIds.push($(this).val());
                                        	 });
                                        	 if (chkIds.length > 0){
                                        		 $.ajax({
                                     	 			type: "get",
                                     	 			async: true,
                                     	 			url: "${ctx}/services/api/contentLists/gencontentlistitems",//
                                     	 			data: {
                                						// Query string parms to be added to the AJAX request
                                						ids: chkIds.join(',')
                                					},
                                     	 			contentType : 'application/json',
                                     	            dataType : "json",
                                     	 			success: function (data) {
			                                        	 $('#tblAppendGrid').appendGrid('appendRow', data);
	                                        		 },
	                                 	 			error: function (response) {
	                                 	 				$.mdsForm.alert(response.responseText);
	                                 	 			}
                                        		 });
                                        	 }
                                        	 $('#rootwizard').smartWizard("prev"); });
        var btnCancel = $('<button></button>').text('<fmt:message key="button.wizard.cancel" />')
                                         .addClass('btn btn-danger btn-cancel')
                                         .on('click', function(){ $('#rootwizard').smartWizard("prev"); });
        var btnSelectAll = $('<button></button>').text('<fmt:message key="button.selectall" />')
								        .addClass('btn btn-primary btn-selectallthumb')
								        .on('click', function(){ 
								          var checkAll = !$(this).hasClass("active"); // true when we want to check all; otherwise false
								          if (checkAll){
								        	  $(this).addClass("active");
								          }else{
								        	  $(this).removeClass("active");
								          }
								          
								          $("#albumThumbView .thmb input[type=checkbox]").prop("checked", checkAll);

								          return false; });

        // Smart Wizard
        $('#rootwizard').smartWizard({
                selected: 0,
                theme: 'arrows',
                transitionEffect:'fade',
                toolbarSettings: {toolbarPosition: 'bottom',
                				  toolbarButtonPosition: 'end',
                				  showNextButton: false, // show/hide a Next button
                		          showPreviousButton: false, // show/hide a Previous button
                                  toolbarExtraButtons: [btnSelectAll, btnFinish, btnCancel]
                                },
                lang: { // Language variables for button
                                    next: '<fmt:message key="button.wizard.next" />',
                                    previous: '<fmt:message key="button.wizard.prev" />'
                                }
             });
        
        if ($('#currentStep').val() == 0){
        	$('.btn-selectallthumb').addClass('hide');
            $('.btn-finish').addClass('hide');
            $('.btn-cancel').addClass('hide');
            //$('#rootwizard').smartWizard("prev");
        }
                
        $("#rootwizard").on("showStep", function(e, anchorObject, stepNumber, stepDirection) {
            // Enable finish button only on last step
        	$('#currentStep').val(stepNumber);
            if(stepNumber == 1){
            	$('.btn-selectallthumb').removeClass('hide');
                $('.btn-finish').removeClass('hide');
                $('.btn-cancel').removeClass('hide');
               /* var instance = $('#albumTreeView').jstree(true);
                instance.deselect_all();
                instance.select_node('tv_1');
                $('#albumTreeView').trigger('ready', instance.get_node('tv_1'));*/
                //$('#albumTreeView').trigger('ready');
                //$('#albumTreeView').jstree("select_node", "1");
                /*$('#albumThumbView .thmb').equalSize();
                $('#albumTreeView').css({
    			    width: $('#albumName').outerWidth() + $('#albumButton').outerWidth()
    			});*/
                //$(".sw-btn-prev").addClass('hide');
                //$(".sw-btn-next").addClass('hide');
            }else{
            	//$('.btn-cancel').removeClass('disabled');
            	$('.btn-selectallthumb').addClass('hide');
                //$(".sw-btn-prev").addClass('hide');
                //$(".sw-btn-next").addClass('hide');
                $('.btn-finish').addClass('hide');
                $('.btn-cancel').addClass('hide');
                //$('.btn-finish').addClass('disabled');
            }
        });
        $('#tab2').on('show', function() {
            //console.log('#foo is now visible');
        	$('#albumThumbView .thmb').equalSize();
            $('#albumTreeView').css({
			    width: $('#albumName').outerWidth() + $('#albumButton').outerWidth()
			});
        });

        
        $("#rootwizard").on("leaveStep", function(e, anchorObject, stepNumber, stepDirection) {
            //var elmForm = $("#form-step-" + stepNumber);
            // stepDirection === 'forward' :- this condition allows to do the form validation
            // only on forward navigation, that makes easy navigation on backwards still do the validation when going next
            if(stepDirection === 'forward'){
            	if(stepNumber == 0){
            	}
            	if(stepNumber == 1){     
                }
            }
            
            return true;
        });
        
    	 $('.date:not(.custom)').each(function() {
             var $date = $(this);

             if($date.attr("initialized") == "true") {
                 return;
             }

             var dateformat = $(this).find("[data-format]").data("format");
             $date.datetimepicker({
             	format: dateformat
             });
             $date.find(":input").click(function() {$date.find(".icon-calendar,.icon-time,.icon-date").click();});
             $date.attr("initialized", true);
         });
    	 
    	 
    	 var options = {
     		theme: "bootstrap",
     		ajax: {
     		    url: '${ctx}/services/api/galleries/select2',
     		    dataType: 'json',
     		    data: function (params) {
     		      var query = {
     		        q: params.term
     		      }

     		      // Query parameters will be ?q=[term]
     		      return query;
     		    }
     		},
     		placeholder: "<fmt:message key='contentList.gallery.tip'/>",
     		allowClear: true,
     		disabled: $("#method").val()=='Add'? false : true
 		};

		$("#gallery").select2(options);
		
		$("#gallery").on("change", function (e) {
		});
    		
    	 		
		function openAlbumTreePicker(gId, aId) {
            var url = "${ctx}/cm/albumtreepickers";
            var queryAtp;
            if (gId){
            	queryAtp = 'gid=' + gId;
            }
            if (aId){
            	if (queryAtp)
            		queryAtp += '&aid=' + aId;
            	else
            		queryAtp = 'aid=' + aId;
            }
            if (queryAtp){
            	url += "?";
            	url += queryAtp;
            }
            
            //var okTitle = '<fmt:message key="albumtreepicker.title"/>';
            $.mdsForm.modalDialog('<fmt:message key="albumtreepicker.title"/>', "iframe:" + url, {  
                draggable: true,
                height:480,
                width:640,
                maxHeight:600,
                maxWidth:800,
                //iframeScrolling:'no',
                buttons:[{
                    icon : 'glyphicon glyphicon-unchecked',
                    label : '<fmt:message key="button.selectall" />',
                    cssClass : 'btn-info btn-selectallthumb',
                    action : function(dialogItself) {
                    	var checkAll = !$(this).hasClass("active"); // true when we want to check all; otherwise false
				          if (checkAll){
				        	  $(this).addClass("active");
				        	  $(this).find(".glyphicon").removeClass("glyphicon-unchecked").addClass("glyphicon-check");
				          }else{
				        	  $(this).removeClass("active");
				        	  $(this).find(".glyphicon").removeClass("glyphicon-check").addClass("glyphicon-unchecked");
				          }
				          
				          var iframe1 = $(".bootstrap-dialog-iframe", dialogItself.getModalBody());
		                  var albumtreepicker =iframe1.contents().find(".albumtreepicker");
				          $("#albumThumbView .thmb input[type=checkbox]", albumtreepicker).prop("checked", checkAll);
                    }}, 
                    {
                    icon : 'glyphicon glyphicon-ok',
                    label : 'OK',
                    cssClass : 'btn-primary',
                    action : function(dialogItself) {
                    	var chkIds = [];
                    	var iframe1 = $(".bootstrap-dialog-iframe", dialogItself.getModalBody());
		                var albumtreepicker =iframe1.contents().find(".albumtreepicker");
	                   	$("#albumThumbView .thmb input:checked", albumtreepicker).each(function() {
	                   		chkIds.push($(this).val());
	                   	});
	                   	if (chkIds.length > 0){
	                   		$.ajax({
                	 			type: "get",
                	 			async: true,
                	 			url: "${ctx}/services/api/contentLists/gencontentlistitems",//
                	 			data: {
           						// Query string parms to be added to the AJAX request
           						ids: chkIds.join(',')
           					},
                	 			contentType : 'application/json',
                	            dataType : "json",
                	 			success: function (data) {
                               	 $('#tblAppendGrid').appendGrid('appendRow', data);
                       		 },
                	 			error: function (response) {
                	 				$.mdsForm.alert(response.responseText);
                	 			}
	                   		});
	                   	}
                    	dialogItself.close();
                    	$.mdsForm.cancelModelDialog();
                    }}, 
                    {
                    icon : 'glyphicon glyphicon-remove',
                    label : 'Cancel',
                    action : function(dialogItself) {
                    	dialogItself.close();
                    	$.mdsForm.cancelModelDialog();
                    }
                } ],
                ok : function(modal) {
                    return true;
                }
            });
        }
		
		/*OnoffCanvas.autoinit({
		   createDrawer: true,
		   hideByEsc: true
    	});*/
    });
   
    var contentPreview;
    var renderPreview = function(moid) {
    	$.ajax({
 			type: "get",
 			async: true,
 			url: "${ctx}/services/api/contentLists/contentpreview?moid=" + moid,//
 			contentType : 'application/json',
            dataType : "json",
 			success: function (data) {
 				// Compile the HTML and javascript templates
 				$.templates({
 					contentpreview_a_tmpl_html : data.Settings.ContentTmplName,
 					contentpreview_a_tmpl_script : data.Settings.HeaderTmplName
 				});

 				data.Settings.ClientId='dcm_dl';
 				data.Settings.HeaderTmplName = 'contentpreview_a_tmpl_html';
				data.Settings.HeaderClientId = 'dcm_dl_mediaHtml';
				data.Settings.ThumbnailTmplName = 'contentpreview_a_tmpl_html';
				data.Settings.ThumbnailClientId = 'dcm_dl_mediaHtml';
				data.Settings.ContentTmplName = 'contentpreview_a_tmpl_html';
				data.Settings.ContentClientId = 'dcm_dl_mediaHtml';
				data.Settings.LeftPaneTmplName = 'contentpreview_a_tmpl_html';
				data.Settings.LeftPaneClientId = 'dcm_dl_mediaHtml';
				data.Settings.RightPaneTmplName = 'contentpreview_a_tmpl_html';
				data.Settings.RightPaneClientId = 'dcm_dl_mediaHtml';
				
				window.dcm_dl = {};
				window.dcm_dl.p = function(){return $('#dcm_dl');};
				window.dcm_dl.dcmData = data;

 				// Pass the script template to the renderer and then execute. It is expected the script contains code for
 				// rendering the HTML template.
 				var script = $.render["contentpreview_a_tmpl_script"](window.dcm_dl.dcmData);
 				if (console) console.log(script); // Send to console (useful for debugging)
 				(new Function(script))(); // Execute the script
 				if (!contentPreview){
 					contentPreview = new OnoffCanvas('#onoffcanvas-dialog').on('show.onoffcanvas', (event)=>{
 					   console.log(event.type); // show.onoffcanvas
 					 }).on('hide.onoffcanvas',(event)=>{
 					   //console.log(event.target); // <div class="onoffcanvas ...
 						$('#dcm_dl').find('.dcm_mvContentView').remove();
 					 });
 				}
 				contentPreview.show();
 				//$('#onoffcanvas-dialog').addClass('is-open');
 			},
 			error: function (response) {
 				$.mdsForm.alert(response.responseText);
 			}
   		});    	
	};
        
    ;(function($, window, undefined) {
    	    	
    	//#region dcmTreePicker plug-in
    	$.fn.dcmTreePicker = function (data, options) {
    		var self = this;
    		var settings = $.extend({}, $.fn.dcmTreePicker.defaults, options);
    	
    		var getTreeDataAndRender = function () {
    			$.ajax({
    				type: "GET",
    				url: options.treeDataUrl,
    				contentType: "application/json; charset=utf-8",
    				complete: function () {
    					self.removeClass('dcm_wait');
    				},
    				success: function (tagTreeJson) {
    					var tv = new DcmTreePicker(self, $.parseJSON(tagTreeJson), settings);
    					tv.render();
    				},
    				error: function (response) {
    					$.dcmShowMsg("Action Aborted", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
    				}
    			});
    		};
    	
    		if (data == null) {
    			getTreeDataAndRender();
    		} else {
    			var dcmTv = new DcmTreePicker(this, data, settings);
    			dcmTv.render();
    		}
    	
    		return this;
    	};
    	
    	$.fn.dcmTreePicker.defaults = {
    		clientId: '', // 
    		allowMultiSelect: false, // Indicates whether more than one node can be selected at a time
    		albumIdsToSelect: null, // An array of the album IDs of any nodes to be selected during rendering
    		checkedAlbumIdsHiddenFieldClientId: '', // The client ID of the hidden input field that stores a comma-separated list of the album IDs of currently checked nodes
    		checkedAlbumNamesFieldClientId: '', // The client ID of the hidden input field that stores a comma-separated list of the album IDs of currently checked nodes
    		theme: 'default', // Used to generate the CSS class name that is applied to the HTML DOM element that contains the treeview. Ex: "dcm" is rendered as CSS class "jstree-dcm"
    		requiredSecurityPermissions: 1, //ViewAlbumOrContentObject
    		navigateUrl: '', // The URL to the current page without query string parms. Used during lazy load ajax call. Example: "/dev/ds/gallery.aspx"
    		enableCheckboxPlugin: false, // Indicates whether a checkbox is to be rendered for each node
    		selectChanged: null, //select Changed callback
    		treeDataUrl: '' // The URL for retrieving tree data. Ignored when tree data is passed via data parameter
    	};
    	
    	window.DcmTreePicker = function (target, data, options) {
    		this.$target = target; // A jQuery object to receive the rendered treeview.
    		this.TreeViewOptions = options;
    		this.Data = data;
    	};
    	
    	DcmTreePicker.prototype.render = function () {
    		var self = this;
    		
    		this._updateNodeDataWithAlbumIdsToSelect();
    	
    		var jstreeOptions = {
    			core: {
    				data: function (node, cb) {
    					if (node.id === '#') {
    						return cb(self.Data);
    					}
    	
    					$.ajax({
    						url: window.Dcm.AppRoot + '/services/api/albumrests/gettreeview',
    						data: {
    							// Query string parms to be added to the AJAX request
    							id: node.li_attr['data-id'],
    							secaction: self.TreeViewOptions.requiredSecurityPermissions,
    							sc: $.inArray('checkbox', this.settings.plugins) >= 0, // Whether checkboxes are being used
    							navurl: self.TreeViewOptions.navigateUrl
    						},
    						dataType: "json",
    						error: function (response, textStatus, errorThrown) {
    							if (textStatus == "error") {
    								alert("Oops! An error occurred while retrieving the treeview data. It has been logged in the gallery's event log.");
    							}
    						},
    						success: function (data) {
    							return cb(data);
    						}
    					});
    					return null;
    				},
    				multiple: this.TreeViewOptions.allowMultiSelect,
    				themes: {
    					name: this.TreeViewOptions.theme,
    					dots: false,
    					icons: false,
    					responsive: false
    				}
    			},
    		};
    	
    		if (this.TreeViewOptions.enableCheckboxPlugin) {
    			jstreeOptions.plugins = ['checkbox'];
    			jstreeOptions.checkbox = {
    				keep_selected_style: false,
    				three_state: this.TreeViewOptions.allowMultiSelect
    			};
    		}
    	
    		this.$target.jstree(jstreeOptions)
    			.on("ready.jstree", function (e, data) {
    				self.onLoaded(e, data);
    			})
    			.on("changed.jstree", function (e, data) {
    				self.onChangeState(e, data);
    			})
    			.on("deselect_node.jstree", function (e, data) {
    				self.onDeselectNode(e, data);
    			});
    	};
    	
    	DcmTreePicker.prototype._fullName = function (data, node) {
    		var node = data.instance.get_node(node ? node : data.node);
            var names = node.text;
            if (node.parent === '#'){
            	return names;
            }

            while((node = data.instance.get_node(node.parent))) {
                /*if(node.root && !config.select.includeRoot) {
                    break;
                }*/
                names = node.text + " > " + names;
                if (node.parent === '#'){
                	break
                }
            }
            return names;
        };
    	
    	DcmTreePicker.prototype._storeSelectedNodesInHiddenFormField = function (data) {
    		// Grab the data-id values from the top selected nodes, concatenate them and store them in a hidden
    		// form field. This can later be retrieved by server side code to determine what was selected.
    		if (this.TreeViewOptions.checkedAlbumIdsHiddenFieldClientId == null || this.TreeViewOptions.checkedAlbumIdsHiddenFieldClientId.length == 0)
    			return;
    	
    		var topSelectedNodes = data.instance.get_top_selected(true);
    		var albumIds = $.map(topSelectedNodes, function (val, i) {
    			return val.li_attr['data-id'];
    		}).join();
    	
    		$('#' + this.TreeViewOptions.checkedAlbumIdsHiddenFieldClientId).val(albumIds);
    		if (!Dcm.isNullOrEmpty(this.TreeViewOptions.selectChanged)){
				this.TreeViewOptions.selectChanged(albumIds);
			}
    	};
    	
    	DcmTreePicker.prototype._storeSelectedNamesInFormField = function (data) {
    		// Grab the data-id values from the top selected nodes, concatenate them and store them in a hidden
    		// form field. This can later be retrieved by server side code to determine what was selected.
    		if (this.TreeViewOptions.checkedAlbumNamesFieldClientId == null || this.TreeViewOptions.checkedAlbumNamesFieldClientId.length == 0)
    			return;
    	
    		var albumNames = "";
    		var topSelectedNodes = data.instance.get_top_selected(true);
    		if (!this.TreeViewOptions.allowMultiSelect && topSelectedNodes) {
    			albumNames = this._fullName(data, topSelectedNodes[0]);
    		}else{
	    		albumNames = $.map(topSelectedNodes, function (val, i) {
	    			return data.instance.get_node(val).text;
	    		}).join();
    		}
    	
    		$('#' + this.TreeViewOptions.checkedAlbumNamesFieldClientId).val(albumNames);
    	};
    	
    	DcmTreePicker.prototype._updateNodeDataWithAlbumIdsToSelect = function () {
    		// Process the albumIdsToSelect array - find the matching node in the data and change state.selected to true
    		// Note that in many cases the nodes are pre-selected in server side code. This function isn't needed in those cases.
    		if (Dcm.isNullOrEmpty(this.TreeViewOptions.albumIdsToSelect))
    			return;
    	
    		var findMatch = function (nodeArray, dataId) {
    			// Search nodeArray for a node having data-id=dataId, acting recursively
    			if (Dcm.isNullOrEmpty(nodeArray))
    				return null;
    	
    			var matchingNode = $.grep(nodeArray, function (n) { return n.li_attr['data-id'] === dataId; })[0] || null;
    	
    			if (matchingNode != null)
    				return matchingNode;
    	
    			// Didn't find it, so recursively search node data
    			$.each(nodeArray, function (idx, n) {
    				matchingNode = findMatch(n.children, dataId);
    	
    				if (matchingNode != null) {
    					return false; // Break out of $.each
    				}
    			});
    	
    			return matchingNode;
    		};
    	
    		var self = this;
    		var topSelectedNodes = [];
    		$.each(this.TreeViewOptions.albumIdsToSelect, function (idx, id) {
    			var node = findMatch(self.Data, id);
    	
    			if (node != null) {
    				node.state.selected = true;
    				topSelectedNodes.push(node);
    			}
    		});
    		
    		var albumNames = "";
    		if (!this.TreeViewOptions.allowMultiSelect) {
    			var node = topSelectedNodes[0];
    			if (node.parent === '#'){
    				albumNames = node.text;
                }else{
	                while((node = findMatch(self.Data, node.li_attr['data-id']))) {
	                    names = node.text + " > " + names;
	                    if (node.parent === '#'){
	                    	break
	                    }
	                }
                }
    		}else{
	    		albumNames = $.map(topSelectedNodes, function (val, i) {
	    			return val.text;
	    		}).join();
    		}
    	
    		$('#' + this.TreeViewOptions.checkedAlbumNamesFieldClientId).val(albumNames);
    	};
    	
    	DcmTreePicker.prototype.onChangeState = function (e, data) {
    		if (data.action == 'select_node') {
    			var url = data.instance.get_node(data.node, true).children('a').attr('href');
    	
    			if (url != null && url.length > 1) {
    				// Selected node is a hyperlink with an URL, so navigate to it.
    				document.location = url;
    				return;
    			}
    		}
    	
    		if (data.action == 'deselect_node' || data.action == 'select_node') {
    			this._storeSelectedNodesInHiddenFormField(data);
    			this._storeSelectedNamesInFormField(data);
    			    			
    			$('#' + this.TreeViewOptions.checkedAlbumIdsHiddenFieldClientId).siblings(".input-group-btn").removeClass("open");
    			
    			this.$target.css({
	       			 width: $('#' + this.TreeViewOptions.checkedAlbumNamesFieldClientId).outerWidth() + $('#' + this.TreeViewOptions.clientId + "Button").outerWidth()
	       		});
    		}
    	};
    	
    	DcmTreePicker.prototype.onDeselectNode = function (e, data) {
    		// Don't let user deselect the only selected node when allowMultiSelect=false
    		if (!this.TreeViewOptions.allowMultiSelect && data.instance.get_selected().length == 0) {
    			data.instance.select_node(data.node);
    		}
    	};
    	
    	DcmTreePicker.prototype.onLoaded = function (e, data) {
    		this._storeSelectedNodesInHiddenFormField(data);
    		this._storeSelectedNamesInFormField(data);

    		// Scroll the left pane if necessary so that the selected node is visible
    		if (this.TreeViewOptions.clientId.length < 1)
    			return;
    		
    		/*var namewidth = $('#' + this.TreeViewOptions.checkedAlbumNamesFieldClientId).outerWidth();
    		var btnwidth = $('#' + this.TreeViewOptions.clientId + "Button").outerWidth();*/
    		this.$target.css({
    			 width: $('#' + this.TreeViewOptions.checkedAlbumNamesFieldClientId).outerWidth() + $('#' + this.TreeViewOptions.clientId + "Button").outerWidth()
    		});
    	};
    	
    	//#endregion dcmTreePicker plug-in
    	
    	//#region dcmThumbPicker plug-in

    	$.fn.dcmThumbPicker = function (data, options) {
    		var self = this;
    		var settings = $.extend({}, $.fn.dcmThumbPicker.defaults, options);

    		var getThumbDataAndRender = function () {
    			$.ajax({
    				//type: "GET",
    				url: options.thumbPickerUrl,
    				data: {
						// Query string parms to be added to the AJAX request
						id: options.albumIdsToSelect[0],
						secaction: options.requiredSecurityPermissions,
						sc: true, // Whether checkboxes are being used
						navurl: ''
					},
					dataType: "json",
    				complete: function () {
    					self.removeClass('dcm_wait');
    				},
    				success: function (contentItems) {
    					var tc = new DcmThumbPicker(self, contentItems, settings);
    					tc.render();
    				},
    				error: function (response) {
    					$.dcmShowMsg("Action Aborted", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
    				}
    			});
    		};

    		if (data == null) {
    			getThumbDataAndRender();
    		} else {
    			var dcmTc = new DcmThumbPicker(this, data, settings);
    			dcmTc.render();
    		}

    		return this;
    	};

    	$.fn.dcmThumbPicker.defaults = {
    		clientId: '',
    		thumbPickerType: 'contentobject', // 'album' or 'contentobject' or 'all'
    		thumbPickerUrl: '', // The URL for retrieving thumb data. Ignored when tag data is passed via data parameter
    		albumIdsToSelect: null, // An array of the album IDs of any nodes to be selected during rendering
    		checkedContentItemIdsHiddenFieldClientId: '', // The client ID of the hidden input field that stores a comma-separated list of the album IDs of currently checked nodes
    		requiredSecurityPermissions: 1, //ViewAlbumOrContentObject    			
    	};

    	window.DcmThumbPicker = function (target, data, options) {
    		this.$target = target; // A jQuery object to receive the tag cloud.
    		this.ThumbPickerOptions = options;
    		this.Data = data;
    	};

    	DcmThumbPicker.prototype.render = function () {
    		var self = this;   		
    		
    		var thumbhtml = [];
    		//thumbhtml.push("	  </div>\n");
            $.each(this.Data, function (i, el) {
            	/*thumbhtml.push("		<div class='col-sm-4 col-md-3'>"); 	
            	thumbhtml.push("	    <div class='thumb insides'>");
            	thumbhtml.push(el.thumbnailHtml); 	
            	thumbhtml.push("	      <div class='test'>"); 	
            	thumbhtml.push("	        <p><label class='checkbox dcm_go_t' style='max-width:", el.titleWidth, "px;'><input type='checkbox' id='chk",  el.id, "'>", el.text, "</label></p>"); 	
            	thumbhtml.push("	      </div>");
            	thumbhtml.push("	    </div>");	
            	thumbhtml.push("	  </div>");*/
            	thumbhtml.push("		<li class='", el.thumbnailCssClass, "'>"); 	
            	thumbhtml.push(el.thumbnailHtml);
            	if (el.id.startsWith('a')){
            		if (self.ThumbPickerOptions.thumbPickerType=='album' || self.ThumbPickerOptions.thumbPickerType=='all'){
            			thumbhtml.push("	        <p><label class='checkbox dcm_go_t' style='width:", el.titleWidth, "px;'><input type='checkbox' id='chk",  el.id, "' value='",  el.id, "'>", el.text, "</label></p>");
            		}else{
            			thumbhtml.push("	        <p><label class='checkbox dcm_go_t' style='width:", el.titleWidth, "px;'><input type='checkbox' id='chk",  el.id, "' value='",  el.id, "' class='hidden d-none'>", el.text, "</label></p>");
            		}
            	}else{
            		if (self.ThumbPickerOptions.thumbPickerType=='contentobject' || self.ThumbPickerOptions.thumbPickerType=='all'){
            			thumbhtml.push("	        <p><label class='checkbox dcm_go_t' style='width:", el.titleWidth, "px;'><input type='checkbox' id='chk",  el.id, "' value='",  el.id, "'>", el.text, "</label></p>");
            		}else{
            			thumbhtml.push("	        <p><label class='checkbox dcm_go_t' style='width:", el.titleWidth, "px;'><input type='checkbox' id='chk",  el.id, "' value='",  el.id, "' class='hidden d-none'>", el.text, "</label></p>");
            		}
            	}
            	thumbhtml.push("	    </li>");	
            });
            //thumbhtml.push("	  </div>\n");
            self.$target.html(thumbhtml.join(''));
            /*$('.dcm_i_c', self.$target).each(function() {
            	$(this).css({
            		width: $('.dcm_thmb_img', this).width(),
       			 	height: $('.dcm_thmb_img', this).height()
            	});
            });*/
            $('.thmb', self.$target).equalSize(); // Make all thumbnail tags the same width & height
            $('.dcm_go_t', self.$target).css('width', '').css('display', '');// Remove the width that was initially set, allowing title to take the full width of thumbnail
    	};

    	//#endregion

    })(jQuery, this);    
</script>
