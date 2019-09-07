;(function($, window, undefined) {
    	    	
    	//#region mdsTreePicker plug-in
	$.fn.mdsTreePicker = function (data, options) {
		var self = this;
		var settings = $.extend({}, $.fn.mdsTreePicker.defaults, options);
	
		var getTreeDataAndRender = function () {
			$.ajax({
				type: "GET",
				url: options.treeDataUrl,
				contentType: "application/json; charset=utf-8",
				complete: function () {
					self.removeClass('mds_wait');
				},
				success: function (tagTreeJson) {
					var tv = new MdsTreePicker(self, $.parseJSON(tagTreeJson), settings);
					tv.render();
				},
				error: function (response) {
					$.mdsShowMsg("Action Aborted", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
				}
			});
		};
	
		if (data == null) {
			getTreeDataAndRender();
		} else {
			var mdsTv = new MdsTreePicker(this, data, settings);
			mdsTv.render();
		}
	
		return this;
	};
	
	$.fn.mdsTreePicker.defaults = {
		clientId: '', // 
		allowMultiSelect: false, // Indicates whether more than one node can be selected at a time
		albumIdsToSelect: null, // An array of the album IDs of any nodes to be selected during rendering
		checkedAlbumIdsHiddenFieldClientId: '', // The client ID of the hidden input field that stores a comma-separated list of the album IDs of currently checked nodes
		checkedAlbumNamesFieldClientId: '', // The client ID of the hidden input field that stores a comma-separated list of the album IDs of currently checked nodes
		theme: 'default', // Used to generate the CSS class name that is applied to the HTML DOM element that contains the treeview. Ex: "mds" is rendered as CSS class "jstree-mds"
		requiredSecurityPermissions: 1, //ViewAlbumOrContentObject
		navigateUrl: '', // The URL to the current page without query string parms. Used during lazy load ajax call. Example: "/dev/ds/gallery.aspx"
		enableCheckboxPlugin: false, // Indicates whether a checkbox is to be rendered for each node
		selectChanged: null, //select Changed callback
		treeDataUrl: '' // The URL for retrieving tree data. Ignored when tree data is passed via data parameter
	};
	
	window.MdsTreePicker = function (target, data, options) {
		this.$target = target; // A jQuery object to receive the rendered treeview.
		this.TreeViewOptions = options;
		this.Data = data;
	};
	
	MdsTreePicker.prototype.render = function () {
		var self = this;
		
		this._updateNodeDataWithAlbumIdsToSelect();
	
		var jstreeOptions = {
			core: {
				data: function (node, cb) {
					if (node.id === '#') {
						return cb(self.Data);
					}
	
					$.ajax({
						url: window.Mds.AppRoot + '/services/api/albumrests/gettreeview',
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
	
	MdsTreePicker.prototype._fullName = function (data, node) {
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
	
	MdsTreePicker.prototype._storeSelectedNodesInHiddenFormField = function (data) {
		// Grab the data-id values from the top selected nodes, concatenate them and store them in a hidden
		// form field. This can later be retrieved by server side code to determine what was selected.
		if (this.TreeViewOptions.checkedAlbumIdsHiddenFieldClientId == null || this.TreeViewOptions.checkedAlbumIdsHiddenFieldClientId.length == 0)
			return;
	
		var topSelectedNodes = data.instance.get_top_selected(true);
		var albumIds = $.map(topSelectedNodes, function (val, i) {
			return val.li_attr['data-id'];
		}).join();
	
		$('#' + this.TreeViewOptions.checkedAlbumIdsHiddenFieldClientId).val(albumIds);
		if (!Mds.isNullOrEmpty(this.TreeViewOptions.selectChanged)){
			this.TreeViewOptions.selectChanged(albumIds);
		}
	};
	
	MdsTreePicker.prototype._storeSelectedNamesInFormField = function (data) {
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
	
	MdsTreePicker.prototype._updateNodeDataWithAlbumIdsToSelect = function () {
		// Process the albumIdsToSelect array - find the matching node in the data and change state.selected to true
		// Note that in many cases the nodes are pre-selected in server side code. This function isn't needed in those cases.
		if (Mds.isNullOrEmpty(this.TreeViewOptions.albumIdsToSelect))
			return;
	
		var findMatch = function (nodeArray, dataId) {
			// Search nodeArray for a node having data-id=dataId, acting recursively
			if (Mds.isNullOrEmpty(nodeArray))
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
	
	MdsTreePicker.prototype.onChangeState = function (e, data) {
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
	
	MdsTreePicker.prototype.onDeselectNode = function (e, data) {
		// Don't let user deselect the only selected node when allowMultiSelect=false
		if (!this.TreeViewOptions.allowMultiSelect && data.instance.get_selected().length == 0) {
			data.instance.select_node(data.node);
		}
	};
	
	MdsTreePicker.prototype.onLoaded = function (e, data) {
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
	
	//#endregion mdsTreePicker plug-in
	
	//#region mdsThumbPicker plug-in

	$.fn.mdsThumbPicker = function (data, options) {
		var self = this;
		var settings = $.extend({}, $.fn.mdsThumbPicker.defaults, options);

		var getThumbDataAndRender = function () {
			$.ajax({
				//type: "GET",
				url: options.thumbPickerUrl,
				data: {
					// Query string parms to be added to the AJAX request
					id: $.isArray(settings.albumIdsToSelect) ? settings.albumIdsToSelect[0] : settings.albumIdsToSelect,
					secaction: settings.requiredSecurityPermissions,
					sct: settings.thumbShowType,
					ts: settings.controlPlugin,
					sc: settings.showCheckbox > 0 ? false : true, // Whether checkboxes are being used
					navurl: ''
				},
				dataType: "json",
				complete: function () {
					self.removeClass('mds_wait');
				},
				success: function (contentItems) {
					var tc = new MdsThumbPicker(self, contentItems, settings);
					tc.render();
				},
				error: function (response) {
					$.mdsShowMsg("Action Aborted", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
				}
			});
		};

		if (data == null) {
			getThumbDataAndRender();
		} else {
			var mdsTc = new MdsThumbPicker(this, data, settings);
			mdsTc.render();
		}

		return this;
	};

	$.fn.mdsThumbPicker.defaults = {
		clientId: '',
		thumbPickerType: 'contentobject', // can picker 'album' or 'contentobject' or 'contentobjectid' or 'all'
		thumbShowType: 'all',// show 'album' or 'contentobject' or 'all'
		thumbPickerUrl: '', // The URL for retrieving thumb data. Ignored when tag data is passed via data parameter
		albumIdsToSelect: null, // An array of the album IDs of any nodes to be selected during rendering
		checkedContentItemIdsHiddenFieldClientId: '', // The client ID of the hidden input field that stores a comma-separated list of the album IDs of currently checked nodes
		requiredSecurityPermissions: 1, //ViewAlbumOrContentObject
		controlPlugin: 'checkbox',
		showFileSize: false,
		showWarningMsg: false,
		showCheckbox: 0
	};

	window.MdsThumbPicker = function (target, data, options) {
		this.$target = target; // A jQuery object to receive the tag cloud.
		this.ThumbPickerOptions = options;
		this.Data = data;
	};

	MdsThumbPicker.prototype.render = function () {
		var self = this;   		
		
		var thumbhtml = [];
		//thumbhtml.push("	  </div>\n");
        $.each(this.Data, function (i, el) {
        	/*thumbhtml.push("		<div class='col-sm-4 col-md-3'>"); 	
        	thumbhtml.push("	    <div class='thumb insides'>");
        	thumbhtml.push(el.thumbnailHtml); 	
        	thumbhtml.push("	      <div class='test'>"); 	
        	thumbhtml.push("	        <p><label class='checkbox mds_go_t' style='max-width:", el.titleWidth, "px;'><input type='checkbox' id='chk",  el.id, "'>", el.text, "</label></p>"); 	
        	thumbhtml.push("	      </div>");
        	thumbhtml.push("	    </div>");	
        	thumbhtml.push("	  </div>");*/
        	thumbhtml.push("		<li class='", el.thumbnailCssClass, "'>"); 	
        	thumbhtml.push(el.thumbnailHtml);
        	if (self.ThumbPickerOptions.controlPlugin && self.ThumbPickerOptions.controlPlugin.length > 0){
	        	if (self.ThumbPickerOptions.controlPlugin =='textarea'){
	            	thumbhtml.push('	        <p><textarea id="ta" rows="5" cols="17" class="textareaEditCaption" onfocus="javascript:this.select();" name="ta">' + el.text + '</textarea>');
	            	thumbhtml.push('	        <input id="Hidden1" name="Hidden1" type="hidden" value="' + el.id + '" /></p>');
	        	}else if (self.ThumbPickerOptions.controlPlugin =='radio'){
	        		//thumbhtml.push('	        <p class="' + self.ThumbPickerOptions.clientId + '_caption">');
	        		thumbhtml.push('	        <div class="radio mds_go_t ' + self.ThumbPickerOptions.clientId + '_caption" style="width:' + el.titleWidth + 'px;"><label>');
	        		thumbhtml.push('	          <input type="radio" name="thmb" id="rb' + el.id + '" value="' + el.id + '"' + (el.isAlbumThumbnail ? ' checked' : '') +  '>' + el.text);
	        		thumbhtml.push('	        </label></div>');
	        		/*var capture = '	          <div class="custom-control custom-radio mds_go_t" style="width:' + el.titleWidth + 'px; ">';
	        		thumbhtml.push(capture);
	        		thumbhtml.push('	          <input id="Hidden1" name="Hidden1" type="hidden" value="' + el.id + '" />');
	        		thumbhtml.push('	          <input type="radio" id="rb' + el.id + '" name="thmb" class="custom-control-input"' + (el.isAlbumThumbnail ? ' checked' : '') +  '/>'); // value="' + el.id + '"
	        		thumbhtml.push('	          <label class="custom-control-label" for="rb' + el.id + '">' + el.text + '</label>');
	        		thumbhtml.push('	        </div></p>');            	*/
	        	}else if (self.ThumbPickerOptions.controlPlugin =='rotate'){
	        		thumbhtml.push('	        <div class="btn-group">');
	        		thumbhtml.push('	        	<button type="button" class="btn btn-primary mds_hor" data-method="rotate" data-option="-90" title="Rotate Left">');
	        		thumbhtml.push('	        		<span class="docs-tooltip" data-toggle="tooltip" title="cropper.rotate(-90)">');
	        		thumbhtml.push('	        			<span class="fa fa-undo-alt"></span>');
	        		thumbhtml.push('	        		</span>');
	        		thumbhtml.push('	        	</button>');
	        		thumbhtml.push('	        	<button type="button" class="btn btn-primary mds_vert" data-method="rotate" data-option="90" title="Rotate Right">');
	        		thumbhtml.push('	        		<span class="docs-tooltip" data-toggle="tooltip" title="cropper.rotate(90)">');
	        		thumbhtml.push('	        			<span class="fa fa-redo-alt"></span>');
	        		thumbhtml.push('	        		</span>');
	        		thumbhtml.push('	        	</button>');
	        		thumbhtml.push('	        </div>');
	        	}else{
	        		if (!self.ThumbPickerOptions.showFileSize){
			        	if (el.id.startsWith('a')){
			        		if (self.ThumbPickerOptions.thumbPickerType=='album' || self.ThumbPickerOptions.thumbPickerType=='all'){
			        			thumbhtml.push("	        <p><div class='checkbox mds_go_t' style='width:", el.titleWidth, "px;'><label><input type='checkbox' id='chk"
			        					,  el.id, "' value='",  el.id, "'>", el.text, "</label></div></p>");
			        		}else{
			        			thumbhtml.push("	        <p><div class='checkbox mds_go_t' style='width:", el.titleWidth, "px;'><label><input type='checkbox' id='chk"
			        					,  el.id, "' value='",  el.id, "' class='hidden d-none'>", el.text, "</label></div></p>");
			        		}
			        	}else{
			        		if (self.ThumbPickerOptions.thumbPickerType=='contentobject' || self.ThumbPickerOptions.thumbPickerType=='all'){
			        			thumbhtml.push("	        <p><div class='checkbox mds_go_t' style='width:", el.titleWidth, "px;'><label><input type='checkbox' id='chk"
			        					,  el.id, "' value='",  el.id, "'>", el.text, "</label></p>");
			        		}else{
			        			thumbhtml.push("	        <p><div class='checkbox mds_go_t' style='width:", el.titleWidth, "px;'><label><input type='checkbox' id='chk"
			        					,  el.id, "' value='",  el.id, "' class='hidden d-none'>", el.text, "</label></div></p>");
			        		}
			        	}
	        		}else{
	        			thumbhtml.push("	        <p class='mds_go_t' style='width:", el.titleWidth, "px;'>", el.text, "</p>");
	        		}
	        	}
        	}
        	if (self.ThumbPickerOptions.showWarningMsg && el.warningMsg.length > 0){
        		thumbhtml.push('	        <p id="p2" class="dcm_msgwarning">'+ el.warningMsg + '</p>');
        	}
        	if (self.ThumbPickerOptions.showFileSize){
        		if (el.id.startsWith('a')){
	        		if (el.sc && (self.ThumbPickerOptions.thumbPickerType=='album' || self.ThumbPickerOptions.thumbPickerType=='all')){
	        			thumbhtml.push("	        <p id='p3' class='dcm_em'><div class='checkbox'><label><input type='checkbox' id='chk",  el.id, "' value='"
	        					,  el.id, "'>", el.savings, "</label></div></p>");
	        		}else{
	        			thumbhtml.push("	        <p id='p3' class='dcm_em'><div class='checkbox'><label><input type='checkbox' id='chk",  el.id, "' value='"
	        					,  el.id, "' class='hidden d-none'>", el.savings, "</label></div></p>");
	        		}
	        	}else{
	        		if (el.sc && (self.ThumbPickerOptions.thumbPickerType=='contentobject' || self.ThumbPickerOptions.thumbPickerType=='all')){
	        			thumbhtml.push("	        <p id='p3' class='dcm_em'><div class='checkbox'><label><input type='checkbox' id='chk",  el.id, "' value='"
	        					,  el.id, "'>", el.savings, "</label></p>");
	        		}else{
	        			thumbhtml.push("	        <p id='p3' class='dcm_em'><div class='checkbox'><label><input type='checkbox' id='chk",  el.id, "' value='"
	        					,  el.id, "' class='hidden d-none'>", el.savings, "</label></div></p>");
	        		}
	        	}
        	}
        	
        	thumbhtml.push("	    </li>");	
        });
        //thumbhtml.push("	  </div>\n");
        self.$target.html(thumbhtml.join(''));
        /*$('.mds_i_c', self.$target).each(function() {
        	$(this).css({
        		width: $('.mds_thmb_img', this).width(),
   			 	height: $('.mds_thmb_img', this).height()
        	});
        });*/
        $('.thmb', self.$target).equalSize(); // Make all thumbnail tags the same width & height
        $('.mds_go_t', self.$target).css('width', '').css('display', '');// Remove the width that was initially set, allowing title to take the full width of thumbnail
	};

	//#endregion

})(jQuery, this);    
