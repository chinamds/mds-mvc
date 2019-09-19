
// Contains javascript required for read-only browsing of a gallery
//#region DCM System javascript
; (function ($, window, document, undefined) {
	//#region Gallery-wide functions

	window.Mds = {};
	window.Mds.Constants = {};
	window.Mds.Constants.ViewSize_Thumbnail = 1;
	window.Mds.Constants.ViewSize_Optimized = 2;
	window.Mds.Constants.ViewSize_Original = 3;
	window.Mds.Constants.ViewSize_External = 4;
	window.Mds.Constants.MimeType_Other = 1;
	window.Mds.Constants.MimeType_Image = 2;
	window.Mds.Constants.MimeType_Video = 3;
	window.Mds.Constants.MimeType_Audio = 4;

	// These map to the enum GalleryObjectType:
	window.Mds.Constants.ItemType_Album = 3;
	window.Mds.Constants.ItemType_Image = 4;
	window.Mds.Constants.ItemType_Audio = 5;
	window.Mds.Constants.ItemType_Video = 6;
	window.Mds.Constants.ItemType_Generic = 7;
	window.Mds.Constants.ItemType_External = 8;

	window.Mds.Constants.VirtualType_NotVirtual = 1; // VirtualAlbumType.NotVirtual
	window.Mds.Constants.IntMinValue = -2147483648;
	window.Mds.Constants.IntMaxValue = 2147483647;

	window.Mds.AppRoot = ""; // Set to Utils.AppRoot in GalleryPage.AddGlobalStartupScript (e.g. '/dev/gallery')
	window.Mds.GalleryResourcesRoot = ""; // Set to Utils.GalleryResourcesPath in GalleryPage.AddGlobalStartupScript (e.g. '/dev/gallery/ds')
	window.Mds.msAjaxComponentId = ""; // Holds a reference to the ID of a Microsoft Ajax Library component that implements IDisposable. Used to hold the Silverlight component.
	window.Mds.href = ""; // Holds location.href with hash tag removed

	// Replace apostrophes and quotation marks with their ASCII equivalents
	window.Mds.escape = function (value) {
		return value.replace(/\'/g, '&#39;').replace(/\"/g, '&quot;');
	};

	// HTML encode a string. Note that this function will strip out extra whitespace, such as new lines and tabs.
	window.Mds.htmlEncode = function (value) {
		return $('<div/>').text(value).html();
	};

	// HTML decode a string.
	window.Mds.htmlDecode = function (value) {
		return $('<div/>').html(value).text();
	};

	// Define a remove method for easily removing items from an array. From http://ejohn.org/blog/javascript-array-remove/
	// Examples of usage:
	// array.remove(1); Remove the second item from the array
	// array.remove(-2); Remove the second-to-last item from the array
	// array.remove(1, 2); Remove the second and third items from the array
	// array.remove(-2, -1); Remove the last and second-to-last items from the array
	Array.prototype.mdsRemove = function (from, to) {
		var rest = this.slice((to || from) + 1 || this.length);
		this.length = from < 0 ? this.length + from : from;
		return this.push.apply(this, rest);
	};

	// Define equivalent of C#'s String.Format method. From http://stackoverflow.com/questions/610406/javascript-equivalent-to-printf-string-format
	// Ex: Given "{0} is dead, but {1} is alive! {0} {2}".format("ASP", "ASP.NET"), result is
	// "ASP is dead, but ASP.NET is alive! ASP {2}"
	String.prototype.format = function () {
		var args = arguments;
		return this.replace(/{(\d+)}/g, function (match, number) {
			return typeof args[number] != 'undefined'
				? args[number]
				: match;
		});
	};

	if (!String.prototype.trim) {
		// Add trim() function for browsers that don't implement it (IE 1-8).
		String.prototype.trim = function () {
			return this.replace(/^\s+|\s+$/g, '');
		};
	}

	window.Mds.hasFormValidation = function () {
		return (typeof document.createElement('input').checkValidity == 'function');
	};

	window.Mds.isTouchScreen = function () {
		return !!('ontouchstart' in window) || !!navigator.msMaxTouchPoints;
	};

	window.Mds.isWidthLessThan = function (w) {
		// Returns true for screens less than w px wide. See http://stackoverflow.com/questions/6850164
		return window.matchContent && window.matchContent('(max-device-width: ' + w + 'px)').matches || screen.width <= w;
	};

	window.Mds.isNullOrEmpty = function (obj) {
		if ((!obj && obj !== false) || !(obj.length > 0)) {
			return true;
		}
		return false;
	};

	window.Mds.deepCopy = function (o) {
		var copy = o, k;

		if (o && typeof o === 'object') {
			copy = Object.prototype.toString.call(o) === '[object Array]' ? [] : {};
			for (k in o) {
				copy[k] = window.Mds.deepCopy(o[k]);
			}
		}

		return copy;
	};

	window.Mds.convertAlbumToContentItem = function (a) {
		return { Id: a.Id, IsAlbum: true, MimeType: 0, ItemType: 3, NumAlbums: a.NumAlbums, NumMediaItems: a.NumMediaItems, Caption: a.Caption, Title: a.Title };
	};

	window.Mds.convertMediaItemToContentItem = function (m) {
		return { Id: m.Id, IsAlbum: false, MimeType: m.MimeType, ItemType: m.ItemType, NumAlbums: 0, NumMediaItems: 0, Caption: '', Title: m.Title };
	};

	window.Mds.getItemTypeDesc = function (itemType) {
		switch (itemType) {
			case window.Mds.Constants.ItemType_Album:
				return "Album";
			case window.Mds.Constants.ItemType_Image:
				return "Image";
			case window.Mds.Constants.ItemType_Audio:
				return "Audio";
			case window.Mds.Constants.ItemType_Video:
				return "Video";
			case window.Mds.Constants.ItemType_Generic:
				return "Generic";
			case window.Mds.Constants.ItemType_External:
				return "External";
			default:
				return "Unknown";
		}
	};

	window.Mds.findContentItem = function (data, id, got) {
		if (data.Album != null && data.Album.ContentItems != null)
			return $.grep(data.Album.ContentItems, function (gi) { return gi.Id === id && gi.ItemType === got; })[0];
		else
			return null;
	};

	window.Mds.findMediaItem = function (data, id, got) {
		if (data.Album != null && data.Album.MediaItems != null)
			return $.grep(data.Album.MediaItems, function (mi) { return mi.Id === id && mi.ItemType === got; })[0];
		else
			return null;
	};

	window.Mds.findMetaItem = function (metaItems, mTypeId) {
		return $.grep(metaItems, function (mi) { return mi.MTypeId === mTypeId; })[0] || null;
	};

	window.Mds.findPlayerItem = function (playerItems, uniqueName) {
	    return $.grep(playerItems, function (mi) { return mi.UniqueName == uniqueName; })[0] || null;
	};

	window.Mds.findAlbum = function (data, id, got) {
		if (data.Album != null) {
			if (data.Album.Id === id && got === window.Mds.Constants.ItemType_Album)
				return data.Album;
			else if (data.Album.ContentItems != null)
				return $.grep(data.Album.ContentItems, function (gi) { return gi.Id === id && gi.ItemType === got; })[0];
			else
				return null;
		} else
			return null;
	};

	window.Mds.getView = function (mediaItem, viewSize) {
		// Get the requested view for the specified media item. If the requested view is for the optimized version and
		// it does not exist, the original is returned; otherwise returns null when the requested size does not exist.
		// viewSize must be one of the constants specified at the beginning of this file 
		// (e.g. window.Mds.Constants.ViewSize_Thumbnail, etc.)
		var orig = null;
		for (var i = 0; i < mediaItem.Views.length; i++) {
			if (mediaItem.Views[i].ViewSize == viewSize) {
				return mediaItem.Views[i];
			}
			else if (mediaItem.Views[i].ViewSize == Mds.Constants.ViewSize_Original)
				orig = mediaItem.Views[i];
		}
		return orig;
	};

	window.Mds.Init = function () {	
		$(".mds_ns input:submit, .mds_ns button").button();

		// Set up jsRender converters
		$.views.converters({
			getItemTypeDesc: function (itemType) {
				return window.Mds.getItemTypeDesc(itemType);
			},
			stripHtml: function (text) {
				return Mds.escape(text.replace(/(<[^<>]*>)/g, ""));
			}
		});

		$.views.helpers({
			htmlEscape: function (value) {
				return window.Mds.escape(value);
			},

			format: function (value, format, culture) {
				// Formats and parses strings, dates and numbers in over 350 cultures. See https://github.com/jquery/globalize
				return Globalize.format(value, format, culture);
			},

			parseDate: function (value, formats, culture) {
				// Parses a string representing a date into a JavaScript Date object. See https://github.com/jquery/globalize
				return Globalize.parseDate(value, formats, culture);
			},

			parseInt: function (value, radix, culture) {
				// Parses a string representing a whole number in the given radix (10 by default). See https://github.com/jquery/globalize
				return Globalize.parseInt(value, radix, culture);
			},

			parseFloat: function (value, radix, culture) {
				// Parses a string representing a floating point number in the given radix (10 by default). See https://github.com/jquery/globalize
				return Globalize.parseFloat(value, radix, culture);
			},

			findMetaItem: function (metaItems, mTypeId) {
				// Find the meta item for the specified type, returning an object set to default values if not found.
				return window.Mds.findMetaItem(metaItems, mTypeId) || { Id: 0, GTypeId: 0, IsEditable: false, ContentId: 0, MTypeId: mTypeId, Value: '' };
			},

			getAlbumUrl: function (albumId, preserveTags) {
				// Gets URL to album. Ex: http://localhost/cm/galleryview?tag=desert, http://localhost/cm/galleryview?aid=44
				// When preserveTags=true, the qs parms tag, people, and search are included if present; otherwise they are stripped
				return Mds.GetAlbumUrl(albumId, preserveTags);
			},

			getContentItemUrl: function (contentItem, preserveTags) {
				// Generate URL to the page containing the specified gallery item. Ex: http://localhost/cm/galleryview?aid=44, http://localhost/cm/galleryview?tag=desert&moid=23
				// When preserveTags=true, the qs parms title, tag, people, search, latest & filter are included if present; otherwise they are stripped
				var qs = { aid: contentItem.IsAlbum ? contentItem.Id : null, moid: contentItem.IsAlbum ? null : contentItem.Id };

				if (!preserveTags) {
					// Generally we want to strip tags for albums and preserve them for MOs. This allows users to browse MOs
					// within the context of their tag/people/search criteria.
					qs.title = null;
					qs.tag = null;
					qs.people = null;
					qs.search = null;
					qs.latest = null;
					qs.filter = null;
					qs.rating = null;
					qs.top = null;
				}

				return Mds.GetUrl(window.Mds.href, qs);
			},

			getContentUrl: function (mediaId, preserveTags) {
				// Generate URL to the page containing the specified media item. Ex: http://localhost/cm/galleryview?tag=desert&moid=23
				// When preserveTags=true, the qs parms tag, people, and search are included if present; otherwise they are stripped
				var qs = { aid: null, moid: mediaId };

				if (!preserveTags) {
					// Generally we want to strip tags for albums and preserve them for MOs. This allows users to browse MOs
					// within the context of their tag/people/search criteria.
					qs.title = null;
					qs.tag = null;
					qs.people = null;
					qs.search = null;
					qs.latest = null;
					qs.filter = null;
					qs.rating = null;
					qs.top = null;
				}

				return Mds.GetUrl(document.location.href, qs);
			},

			getDownloadUrl: function (albumId) {
				// Gets URL to page where album objects can be downloaded. Ex: http://localhost/cm/galleryview?g=task_downloadobjects&aid=45
				//return Mds.GetUrl(window.location.href, { g: 'task_downloadobjects', moid: null, aid: albumId });
				return Mds.GetUrl(window.Mds.AppRoot + "/cm/galleryview", {  g: 'cm_downloadobjects', moid: null, aid: albumId });
			},

			getAddUrl: function (galleryData) {
				// Gets URL to add objects page for current album. Ex: http://localhost/cm/galleryview?g=task_addobjects&aid=45
				//return Mds.GetUrl(window.location.href, { g: 'task_addobjects', aid: galleryData.Album.Id });
				return Mds.GetUrl(window.Mds.AppRoot + "/cm/galleryview", { g: 'cm_addobjects', aid: galleryData.Album.Id });
			}
		});

		// Gets reference to current URL with hash tag removed
		window.Mds.href = window.location.href.replace(/#\d+/, '');
	};

	$(document).ready(Mds.Init);

	window.Mds.ReloadPage = function () {
		window.location = Mds.RemoveQSParm(window.location.href, 'msg');
	};

	window.Mds.GetAlbumUrl = function (albumId, preserveTags) {
		var qs = { aid: null, moid: null };

		if (!preserveTags) {
			qs.title = null;
			qs.tag = null;
			qs.people = null;
			qs.search = null;
			qs.latest = null;
			qs.filter = null;
			qs.rating = null;
			qs.top = null;
		}

		if (albumId > Mds.Constants.IntMinValue)
			qs.aid = albumId;

		return Mds.GetUrl(document.location.href, qs);
	};

	window.Mds.GetUrl = function (url, parmValuePairs) {
		// We never want to include the ss and msg parms, so set them to null if the caller didn't already do it.
		if (typeof parmValuePairs.ss === 'undefined')
			parmValuePairs.ss = null; // auto-start slide show

		if (typeof parmValuePairs.msg === 'undefined')
			parmValuePairs.msg = null; // msg ID

		$.each(parmValuePairs, function (p, v) { url = Mds.AddQSParm(Mds.RemoveQSParm(url, p), p, v); });
		return url;
	};

	window.Mds.IsQSParmPresent = function (param) {
		var qs = Mds.GetQS[param];
		return ((qs != null) && (qs.length > 0));
	};

	window.Mds.GetQS = function () {
		var result = {}, queryString = location.search.substring(1), re = /([^&=]+)=([^&]*)/g, m;

		while (m = re.exec(queryString)) {
			result[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
		}

		return result;
	};

	window.Mds.GetQSParm = function (param) {
		return window.Mds.GetQS()[param];
	};

	window.Mds.AddQSParm = function (url, param, value) {
		if (!param || !value) return url;

		param = encodeURIComponent(param);
		value = encodeURIComponent(value);

		var urlparts = url.split('?');
		if (urlparts.length < 2)
			return url + '?' + param + "=" + value;

		var kvp = urlparts[1].split(/[&;]/g);
		for (var i = kvp.length - 1; i >= 0; i--) {
			var x = kvp[i].split('=');

			if (x[0] == param) {
				x[1] = value;
				kvp[i] = x.join('=');
				break;
			}
		}

		if (i < 0) {
			kvp[kvp.length] = [param, value].join('=');
		}
		return urlparts[0] + '?' + kvp.join('&');
	};

	window.Mds.RemoveQSParm = function (url, param) {
		var urlparts = url.split('?');
		if (urlparts.length < 2)
			return url;

		var prefix = encodeURIComponent(param) + '=';
		var pars = urlparts[1].split(/[&;]/g);
		for (var i = pars.length - 1; i >= 0; i--)
			if (pars[i].lastIndexOf(prefix, 0) !== -1)
				pars.splice(i, 1);

		if (pars.length > 0)
			return urlparts[0] + '?' + pars.join('&');
		else
			return urlparts[0];
	};

	window.Mds.DisposeAjaxComponent = function (id) {
		if (typeof Sys === 'undefined' || typeof Sys.Application === 'undefined')
			return;

		if (id && id.length > 0) {
			var obj = Sys.Application.findComponent(id);
			if (obj) obj.dispose();
		}
	};

	//#endregion End Gallery-wide functions

	//#region AJAX functions

	window.Mds.DataService = new function () {

		logOff = function (callback) {
			$.post(window.Mds.AppRoot + '/services/api/task/logoff',
				function (data) {
					callback(data);
				});
		};

		deleteContentObject = function (mediaObjectId, completeCallback, successCallback, errorCallback) {
			$.ajax(({
				type: "DELETE",
				url: window.Mds.AppRoot + '/services/api/contentitems/' + mediaObjectId,
				complete: completeCallback,
				success: successCallback,
				error: errorCallback
			}));
		};

		saveAlbum = function (album, completeCallback, successCallback, errorCallback) {
			var a = Mds.deepCopy(album);
			a.MediaItems = null;
			a.ContentItems = null;
			a.MetaItems = null;
			a.Permissions = null;

			$.ajax(({
				type: "POST",
				url: url = window.Mds.AppRoot + '/services/api/albumrests/post',
				data: JSON.stringify(a),
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				complete: completeCallback,
				success: successCallback,
				error: errorCallback
			}));
		};

		saveMeta = function (contentItemMeta, completeCallback, successCallback, errorCallback) {
			$.ajax(({
				type: "PUT",
				url: window.Mds.AppRoot + '/services/api/contentitemmeta',
				data: JSON.stringify(contentItemMeta),
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				complete: completeCallback,
				success: successCallback,
				error: errorCallback
			}));
		};

		return {
			logOff: logOff,
			deleteContentObject: deleteContentObject,
			saveAlbum: saveAlbum,
			saveMeta: saveMeta
		};

	}();

	//#endregion

	//#region mdsTagCloud plug-in

	$.fn.mdsTagCloud = function (data, options) {
		var self = this;
		var settings = $.extend({}, $.fn.mdsTagCloud.defaults, options);

		var getTagDataAndRender = function () {
			$.ajax({
				type: "GET",
				url: options.tagCloudUrl,
				contentType: "application/json; charset=utf-8",
				complete: function () {
					self.removeClass('mds_wait');
				},
				success: function (tags) {
					var tc = new MdsTagCloud(self, tags, settings);
					tc.render();
				},
				error: function (response) {
					$.mdsShowMsg("Action Aborted", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
				}
			});
		};

		if (data == null) {
			getTagDataAndRender();
		} else {
			var mdsTc = new MdsTagCloud(this, data, settings);
			mdsTc.render();
		}

		return this;
	};

	$.fn.mdsTagCloud.defaults = {
		clientId: '',
		tagCloudType: 'tag', // 'tag' or 'people'
		tagCloudUrl: '', // The URL for retrieving tag data. Ignored when tag data is passed via data parameter
		shape: 'rectangular' // Shape of cloud. Set to false to get the elliptic shape (the default shape of JQCloud)
	};

	window.MdsTagCloud = function (target, data, options) {
		this.$target = target; // A jQuery object to receive the tag cloud.
		this.TagCloudOptions = options;
		this.Data = data;
	};

	MdsTagCloud.prototype.render = function () {
		var self = this;
		var parms = { title: null, tag: null, people: null, search: null, latest: null, filter: null, rating: null, top: null, aid: null, moid: null };
		var pageUrl = window.location.href;

		$.each(this.Data, function (i, el) {
			el.text = el.value;
			el.weight = el.count;
			parms[self.TagCloudOptions.tagCloudType] = el.value;
			el.link = Mds.GetUrl(pageUrl, parms);
		});

		self.$target.jQCloud(self.Data, {
			encodeURI: false,
			shape: self.TagCloudOptions.shape
		});
	};

	//#endregion

	//#region mdsTreeView plug-in

	$.fn.mdsTreeView = function (data, options) {
		var self = this;
		var settings = $.extend({}, $.fn.mdsTreeView.defaults, options);

		var getTreeDataAndRender = function () {
			$.ajax({
				type: "GET",
				url: options.treeDataUrl,
				contentType: "application/json; charset=utf-8",
				complete: function () {
					self.removeClass('mds_wait');
				},
				success: function (tagTreeJson) {
					var tv = new MdsTreeView(self, $.parseJSON(tagTreeJson), settings);
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
			var mdsTv = new MdsTreeView(this, data, settings);
			mdsTv.render();
		}

		return this;
	};

	$.fn.mdsTreeView.defaults = {
		clientId: '', // The ID of the HTML element containing the entire gallery. Used to scroll node into view in left pane. Omit if left pane scrolling not needed
		allowMultiSelect: false, // Indicates whether more than one node can be selected at a time
		disabledCascadeUp:false,
		disabledCascadeDown:false,
		albumIdsToSelect: null, // An array of the album IDs of any nodes to be selected during rendering
		checkedAlbumIdsHiddenFieldClientId: '', // The client ID of the hidden input field that stores a comma-separated list of the album IDs of currently checked nodes
		theme: 'mds', // Used to generate the CSS class name that is applied to the HTML DOM element that contains the treeview. Ex: "mds" is rendered as CSS class "jstree-mds"
		requiredSecurityPermissions: 1, //ViewAlbumOrContentObject
		navigateUrl: '', // The URL to the current page without query string parms. Used during lazy load ajax call. Example: "/dev/cm/galleryview"
		enableCheckboxPlugin: false, // Indicates whether a checkbox is to be rendered for each node
		treeDataUrl: '' // The URL for retrieving tree data. Ignored when tree data is passed via data parameter
	};

	window.MdsTreeView = function (target, data, options) {
		this.$target = target; // A jQuery object to receive the rendered treeview.
		this.TreeViewOptions = options;
		this.Data = data;
	};

	MdsTreeView.prototype.render = function () {
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
			var cascade='';
			if (this.TreeViewOptions.disabledCascadeUp && this.TreeViewOptions.disabledCascadeDown){
				cascade='undetermined';
			}else if (this.TreeViewOptions.disabledCascadeUp){
				cascade='down+undetermined';
			}else if (this.TreeViewOptions.disabledCascadeDown){
				cascade='up+undetermined';
			}
			jstreeOptions.plugins = ['checkbox'];
			jstreeOptions.checkbox = {
				keep_selected_style: false,
				cascade:cascade,
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

	MdsTreeView.prototype._storeSelectedNodesInHiddenFormField = function (data) {
		// Grab the data-id values from the top selected nodes, concatenate them and store them in a hidden
		// form field. This can later be retrieved by server side code to determine what was selected.
		if (this.TreeViewOptions.checkedAlbumIdsHiddenFieldClientId == null || this.TreeViewOptions.checkedAlbumIdsHiddenFieldClientId.length == 0)
			return;

		var topSelectedNodes = data.instance.get_top_selected(true);
		var albumIds = $.map(topSelectedNodes, function (val, i) {
			return val.li_attr['data-id'];
		}).join();

		$('#' + this.TreeViewOptions.checkedAlbumIdsHiddenFieldClientId).val(albumIds);
	};

	MdsTreeView.prototype._updateNodeDataWithAlbumIdsToSelect = function () {
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
		$.each(this.TreeViewOptions.albumIdsToSelect, function (idx, id) {
			var node = findMatch(self.Data, id);

			if (node != null) {
				node.state.selected = true;
			}
		});
	};

	MdsTreeView.prototype.onChangeState = function (e, data) {
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
		}
	};

	MdsTreeView.prototype.onDeselectNode = function (e, data) {
		// Don't let user deselect the only selected node when allowMultiSelect=false
		if (!this.TreeViewOptions.allowMultiSelect && data.instance.get_selected().length == 0) {
			data.instance.select_node(data.node);
		}
	};

	MdsTreeView.prototype.onLoaded = function (e, data) {
		this._storeSelectedNodesInHiddenFormField(data);

		// Scroll the left pane if necessary so that the selected node is visible
		if (this.TreeViewOptions.clientId.length < 1)
			return;

		var selectedIds = data.instance.get_selected();
		if (selectedIds != null && selectedIds.length == 1) {
			var nodeOffsetTop = $('#' + selectedIds[0]).position().top;
			var leftPaneHeight = $('#' + this.TreeViewOptions.clientId + '_lpHtml').height();
			if (nodeOffsetTop > leftPaneHeight) {
				$('#' + this.TreeViewOptions.clientId + '_lpHtml').animate({ scrollTop: nodeOffsetTop }, 200, "linear");
			}
		}
	};

	//#endregion mdsTreeView plug-in
		
	//#region mdsContent plug-in

	$.fn.mdsContent = function (tmplName, data) {
		var mdsContent = new MdsContent(tmplName, this, data);
		mdsContent.initialize();

		return this;
	};

	// Define object for handling the viewing of a single content object. Uses the Revealing Prototype Pattern:
	// http://weblogs.asp.net/dwahlin/archive/2011/08/01/techniques-strategies-and-patterns-for-structuring-javascript-code-the-prototype-pattern.aspx
	window.MdsContent = function (tmplName, target, data) {
		this.TemplateName = tmplName; // The name of a compiled jsRender template
		this.$target = target; // A jQuery object to receive the rendered HTML from the template.
		this.IdPrefix = data.Settings.ClientId;
		this.Data = data;
		this.Toolbar = null;
		this.Timer = null;

		this.inCallback = false;
	};

	MdsContent.prototype.initialize = function () {
		if (!this.Data.MediaItem) {
			$.mdsShowMsg("Cannot Render Content Object", "<p>Cannot render the content object template. Navigate to a content object and then return to this page.</p><p>You'll know you got it right when you see 'moid' In the URL's query string.</p><p>ERROR: this.Data.MediaItem is null.</p>", { msgType: 'error', autoCloseDelay: 0 });
			return;
		}

		this.jsRenderSetup();
		this.overwriteContentObject();
		this.attachEvents();
		this.configureMediaItem();
		this.render();
		this.preloadImages();
	};

	MdsContent.prototype.jsRenderSetup = function () {
		// Create a few helper functions that can be used in the jsRender template.
		var self = this;
		$.views.helpers({
			prevUrl: function () {
				// Generate the URL to the previous media item.
				var prvMi = self.getPreviousContentObject();
				return prvMi ? self.getPermalink(prvMi.Id) : Mds.GetAlbumUrl(self.Data.Album.Id, true);
			},
			nextUrl: function () {
				// Generate the URL to the next media item.
				var nxtMi = self.getNextContentObject();
				return nxtMi ? self.getPermalink(nxtMi.Id) : Mds.GetAlbumUrl(self.Data.Album.Id, true);
			},
			getEmbedCode: function () {
				//var url = Mds.GetUrl(self.Data.App.AppUrl + '/' + self.Data.App.GalleryResourcesPath + '/galleryview' + location.search, { aid: null, moid: self.Data.MediaItem.Id });
				var url = Mds.GetUrl(self.Data.App.AppUrl + '/cm/galleryview' + location.search, { aid: null, moid: self.Data.MediaItem.Id });

				return "<iframe allowtransparency='true' frameborder='0' sandbox='allow-same-origin allow-forms allow-scripts' scrolling='auto' src='"
					+ url + "' style='width:100%;height:100%'></iframe>";
			},
		});
	};

	MdsContent.prototype.configureMediaItem = function () {
		this.setSize(Mds.Constants.ViewSize_Optimized);
	};

	MdsContent.prototype.render = function () {
		this.dataBind();
		this.bindToolbar();
		this.runContentObjectScript();

		if (this.Data.Settings.SlideShowIsRunning)
			this.startSlideshow();

		if (history.replaceState) history.replaceState(null, "", this.getPermalink(this.Data.MediaItem.Id));
	};

	MdsContent.prototype.setSize = function (viewSize) {
		var defaultViewIndex = 0;

		for (var i = 0; i < this.Data.MediaItem.Views.length; i++) {
			if (this.Data.MediaItem.Views[i].ViewSize == viewSize) {
				this.Data.MediaItem.ViewIndex = i; // Get index corresponding to requested size
				return;
			} else if (this.Data.MediaItem.Views[i].ViewSize == Mds.Constants.ViewSize_Original
				|| this.Data.MediaItem.Views[i].ViewSize == Mds.Constants.ViewSize_External)
				defaultViewIndex = i;
		}

		// If we get here, we couldn't find a match for the requested size, so default to showing original or external
		this.Data.MediaItem.ViewIndex = defaultViewIndex;
	};

	MdsContent.prototype.startSlideshow = function () {
		// Returns true when successfully started; otherwise false
		var self = this;
		if (this.Data.Settings.SlideShowType == 'FullScreen') {
			this.removeCursorNavigationHandler();

			var ss = new window.MdsFullScreenSlideShow(this.Data,
			{
				on_exit: function (currentId) {
					self.Data.Settings.SlideShowIsRunning = false;
					self.addCursorNavigationHandler(self);
					
					if (self.Data.MediaItem.Id != currentId) {
						// User ended on a different image than they started. Show that image.
						self.showContentObject(currentId);
					}
				}
			});

			this.Data.Settings.SlideShowIsRunning = ss.startSlideShow();
		}
		else if (this.Data.Settings.SlideShowType == 'Inline') {
			if (this.Timer && this.Timer.isRunning)
				return true;

			this.Data.Settings.SlideShowIsRunning = true;
			if (this.getNextContentObject() != null) {
				this.Timer = new MdsTimer(this.showNextContentObject, this.Data.Settings.SlideShowIntervalMs, this);
				this.Timer.start();
			} else {
				this.Data.Settings.SlideShowIsRunning = false;
				$.mdsShowMsg(this.Data.Resource.MoNoSsHdr, this.Data.Resource.MoNoSsBdy, { msgType: 'info' });
			}
		}

		return this.Data.Settings.SlideShowIsRunning;
	};

	MdsContent.prototype.stopSlideshow = function () {
		if (this.Timer) this.Timer.stop();
		this.Data.Settings.SlideShowIsRunning = false;
	};

	MdsContent.prototype.dataBind = function () {
		Mds.DisposeAjaxComponent(window.Mds.msAjaxComponentId); // Dispose Silverlight component (if necessary)
		this.$target.html($.render[this.TemplateName](this.Data)); // Render HTML template and add to page
		this.animateContentObject(); // Execute transition effect
		this.attachContentEvents();
		this.makeCaptionEditable();
	};

	MdsContent.prototype.makeCaptionEditable = function () {
		if (!this.Data.Album.Permissions.EditContentObject)
			return;

		var self = this;

		$(".mds_mediaObjectTitle", this.$target)
			.addClass('mds_editableContent')
			.hover(function () {
				$(this).removeClass('mds_editableContent').addClass('mds_editableContentHover');
			}, function () {
				$(this).removeClass('mds_editableContentHover').addClass('mds_editableContent');
			})
			.editable(window.Mds.AppRoot + '/services/api/contentitems',
				{
					type: 'textarea',
					rows: 4,
					tooltip: this.Data.Resource.ContentCaptionEditTt,
					event: 'click',
					submit: this.Data.Resource.ContentCaptionEditSave,
					cancel: this.Data.Resource.ContentCaptionEditCancel,
					cssclass: 'mds_editableContentForm',
					indicator: '<img class="mds_wait" src="' + this.Data.App.SkinPath + '/images/wait-squares.gif" />',
					style: "inherit",
					onblur: '', // Setting to empty value prevents auto-cancel when blurring away from input (specify submit to submit on blur)
					oneditbegin: function (s, el, e) { return self.onEditBegin($(el), e); },
					onreset: function (frm, obj) { self.setCaptionHover($(obj), true); },
					onsubmit: function (settings, el) {
						// Get the media item from the instance and update the value with the data the user entered.
						self.Data.MediaItem.Title = $('textarea, input, select', $(el)).val();

						// Assign the media item to the AJAX options. This is how the data gets to the server.
						settings.ajaxoptions.data = JSON.stringify(self.Data.MediaItem);
					},
					oncomplete: function (xmldata) {
						var m = self.Data.MediaItem; // Get reference to media item
						var actionResult = $.parseJSON(xmldata); // Hydrate ActionResult instance returned from server
						m.Title = actionResult.ActionTarget.Title; // Update title (which may have been changed by the server (e.g. HTML removed))

						self.$target.trigger('mediaUpdate.' + self.Data.Settings.ClientId, [self.Data.ActiveContentItems]);

						return m.Title;
					},
					callback: function (value, settings) {
						self.Data.MediaItem.Title = value;
						self.setCaptionHover($(this), true);
					},
					ajaxoptions: {
						type: 'PUT',
						contentType: 'application/json;charset=utf-8'
					}
				});
	};

	MdsContent.prototype.onEditBegin = function (el, e) {
		this.setCaptionHover(el, false);

		// Return false when a hyperlink is clicked. Calling code will cancel the edit and allow the navigation to the link.
		return (e.target.tagName != 'A');
	};

	MdsContent.prototype.setCaptionHover = function (el, enable) {
		if (enable) {
			el.hover(function () {
				el.removeClass('mds_editableContent').addClass('mds_editableContentHover');
			}, function () {
				el.removeClass('mds_editableContentHover').addClass('mds_editableContent');
			});
		} else {
			el.removeClass('mds_editableContentHover').addClass('mds_editableContent').unbind('mouseenter').unbind('mouseleave');
		}
	};

	MdsContent.prototype.animateContentObject = function () {
		var self = this;

		var hideContentObject = function (moEl) {
			// If it is an image and a transition is specified, then hide the content object container so that it
			// can later be shown with the transition effect. Returns true when object is hidden; otherwise returns false.
			var isImage = self.Data.MediaItem.ItemType == Mds.Constants.ItemType_Image;
			var hasTransition = self.Data.Settings.TransitionType != 'none';

			if (isImage && hasTransition) {
				// Explicitly set the height of the parent element so that the page doesn't reflow when the content object is hidden.
				// Line commented out 2012-06-05 because it added a vertical scrollbar and no longer seemed required
				//moEl.parent().height(moEl.parent().height());
				moEl.hide();
				return true;
			} else {
				return false;
			}
		};

		var anEl = $(".mds_moContainer", this.$target);

		if (hideContentObject(anEl)) {
			// Valid:  'none', 'fade', 'blind', 'bounce', 'clip', 'drop', 'explode', 'fold', 'highlight', 'puff', 'pulsate', 'scale', 'shake', 'size', 'slide', 'transfer'.
			switch (this.Data.Settings.TransitionType) {
				case 'none':
					anEl.show();
					break;
				case 'fade':
					anEl.fadeIn(this.Data.Settings.TransitionDurationMs);
					break;
				default:
					var options = {};
					// Some effects have required parameters
					if (this.Data.Settings.TransitionType === "scale") options = { percent: 100 };

					anEl.toggle(this.Data.Settings.TransitionType, options, this.Data.Settings.TransitionDurationMs);
					break;
			}
		}
	};

	MdsContent.prototype.showPreviousContentObject = function (e) {
		this.Data.MediaItem = this.getPreviousContentObject();
		if (this.Data.MediaItem) {
			if (e) e.preventDefault(); // Prevent the event from bubbling (prevents hyperlink navigation on next/previous buttons)
			this.Data.ActiveContentItems = [window.Mds.convertMediaItemToContentItem(this.Data.MediaItem)];
			$('#' + this.IdPrefix + '_moid').val(this.Data.MediaItem.Id);
			this.setSize(Mds.Constants.ViewSize_Optimized);
			this.render(); // Re-bind the template
			this.$target.trigger('previous.' + this.Data.Settings.ClientId, [this.Data.ActiveContentItems]);
		} else this.redirectToAlbum();
	};

	MdsContent.prototype.showNextContentObject = function (e) {
		this.Data.MediaItem = this.getNextContentObject();
		if (this.Data.MediaItem) {
			if (e) e.preventDefault(); // Prevent the event from bubbling (prevents hyperlink navigation on next/previous buttons)
			this.Data.ActiveContentItems = [window.Mds.convertMediaItemToContentItem(this.Data.MediaItem)];
			$('#' + this.IdPrefix + '_moid').val(this.Data.MediaItem.Id);
			this.setSize(Mds.Constants.ViewSize_Optimized);
			this.render(); // Re-bind the template
			this.$target.trigger('next.' + this.Data.Settings.ClientId, [this.Data.ActiveContentItems]);
		} else this.redirectToAlbum();
	};

	MdsContent.prototype.showContentObject = function (id) {
		this.Data.MediaItem = $.grep(this.Data.Album.MediaItems, function (mi) { return mi.Id === id; })[0];
		if (this.Data.MediaItem) {
			this.Data.ActiveContentItems = [window.Mds.convertMediaItemToContentItem(this.Data.MediaItem)];
			$('#' + this.IdPrefix + '_moid').val(this.Data.MediaItem.Id);
			this.setSize(Mds.Constants.ViewSize_Optimized);
			this.render(); // Re-bind the template
			this.$target.trigger('mediaUpdate.' + this.Data.Settings.ClientId, [this.Data.ActiveContentItems]);
		} else this.redirectToAlbum();
	};

	MdsContent.prototype.getPreviousContentObject = function () {
		return this.Data.Album.MediaItems[$.inArray(this.Data.MediaItem, this.Data.Album.MediaItems) - 1];
	};

	MdsContent.prototype.getNextContentObject = function () {
		if (this.Data.Settings.SlideShowIsRunning) {
			// Return the next *image* content object
			var mo = this.Data.MediaItem;
			do {
				mo = this.Data.Album.MediaItems[$.inArray(mo, this.Data.Album.MediaItems) + 1];
			} while (mo && mo.MimeType != Mds.Constants.MimeType_Image);
			return mo;
		} else {
			// Return the next content object
			return this.Data.Album.MediaItems[$.inArray(this.Data.MediaItem, this.Data.Album.MediaItems) + 1];
		}
	};

	MdsContent.prototype.redirectToAlbum = function () {
		window.location = Mds.GetAlbumUrl(this.Data.Album.Id);
	};

	MdsContent.prototype.runContentObjectScript = function () {
		if (this.Data.MediaItem.Views[this.Data.MediaItem.ViewIndex].ScriptOutput.length > 0) {
			(new Function((this.Data.MediaItem.Views[this.Data.MediaItem.ViewIndex].ScriptOutput)))();
		}
	};

	MdsContent.prototype.getPermalink = function (id) {
		return Mds.GetUrl(document.location.href, { moid: id });
	};

	MdsContent.prototype.bindToolbar = function () {
		this.Toolbar = this.buildContentToolbar(this);
	};

	MdsContent.prototype.overwriteContentObject = function () {
		// Overwrite the this.Data.MediaItem object that was parsed from JSON with the equivalent object from the collection. We do this so that
		// we can later use $.inArray to find the current item in the array.
		for (var i = 0; i < this.Data.Album.MediaItems.length; i++) {
			if (this.Data.Album.MediaItems[i].Id == this.Data.MediaItem.Id) {
				this.Data.MediaItem = this.Data.Album.MediaItems[i];
				return;
			}
		}
	};

	MdsContent.prototype.attachEvents = function () {
		// This runs once when initialized, so don't wire up any events on items *inside* the template, since
		// they'll be erased when the user navigates between content objects. (Do that in attachContentEvents())
		var self = this;

		// Attach a handler for when a metaitem is updated.
		$('#' + this.Data.Settings.ContentClientId).on('metaUpdate.' + this.Data.Settings.ClientId, function (e, gim) { self.onMetaUpdate(e, gim); });

		this.addCursorNavigationHandler(self);
	};

	MdsContent.prototype.attachContentEvents = function () {
		// This runs each time the template is rendered, so here we wire up events to any elements inside the rendered HTML.
		var self = this;

		// Attach handlers for next/previous clicks.
		$(".mds_mvPrevBtn", this.$target).on("click", function (e) { self.showPreviousContentObject(e); });
		$(".mds_mvNextBtn", this.$target).on("click", function (e) { self.showNextContentObject(e); });
	};

	MdsContent.prototype.addCursorNavigationHandler = function (mdsContentInstance) {
		if (this.Data.Settings.ShowContentObjectNavigation) {
			$(document.documentElement).on('keydown.' + this.Data.Settings.ClientId, function (e) {
				if ((e.target.tagName == 'INPUT') || (e.target.tagName == 'TEXTAREA')) return; // Ignore when focus is in editable box

				if (e.keyCode == 37) mdsContentInstance.showPreviousContentObject(e); // 37 = left arrow
				if (e.keyCode == 39) mdsContentInstance.showNextContentObject(e); // 39 = right arrow
			});
		}
	};

	MdsContent.prototype.removeCursorNavigationHandler = function () {
		$(document.documentElement).off('keydown.' + this.Data.Settings.ClientId);
	};

	MdsContent.prototype.onMetaUpdate = function (e, gim) {
		// Event handler for when a meta item has been updated. e is the jQuery event object; gim is the ContentItemMeta instance.
		if (gim.MetaItem.MTypeId == 29) { // 29 = MetadataItemName.Title
			this.render(); // At some point we may want to move this outside the 'if' if the media template uses other metadata values
		} else if (gim.MetaItem.MTypeId == 112) { // 112 = MetadataItemName.HtmlSource
			this.render();
		}
	};

	MdsContent.prototype.preloadImages = function () {
		// Create an array of all optimized or original image URLs
		var urls = $.map(this.Data.Album.MediaItems, function (mo) {
			for (var i = 0; i < mo.Views.length; i++) {
				if ((mo.Views[i].ViewType == Mds.Constants.MimeType_Image) && (mo.Views[i].ViewSize == Mds.Constants.ViewSize_Optimized))
					return mo.Views[i].Url;
			}
		});

		// Create an image tag & set the source
		$(urls).each(function () { $('<img>').attr('src', this); });
	};

	MdsContent.prototype.buildContentToolbar = function () {
		var self = this;

		if (!this.Data.Settings.ShowContentObjectToolbar) {
			$(".mds_mvToolbar", this.$target).hide();

			return null;
		}

		var tb = new MdsContentToolbar();
		tb.EmbedButton = $(".mds_mvTbEmbed", this.$target);
		tb.SlideshowButton = $(".mds_mvTbSlideshow", this.$target);
		tb.MoveButton = $(".mds_mvTbMove", this.$target);
		tb.CopyButton = $(".mds_mvTbCopy", this.$target);
		tb.RotateButton = $(".mds_mvTbRotate", this.$target);
		tb.DeleteButton = $(".mds_mvTbDelete", this.$target);
		tb.ApproveButton = $(".mds_mvTbApprove", this.$target);

		if (this.Data.Settings.ShowUrlsButton) {
			var dgShare = $(".mds_mo_share_dlg", this.$target);

			dgShare.dialog({
				appendTo: '#' + self.Data.Settings.ClientId,
				autoOpen: false,
				draggable: false,
				resizable: false,
				closeOnEscape: true,
				dialogClass: 'mds_mo_share_dlg_container',
				width: 420,
				minHeight: 0,
				show: 'fade',
				hide: 'fade',
				position: { my: "left top", at: "left bottom", of: $(".mds_mvTbEmbed", this.$target) },
				open: function (e, ui) {
					$(this).parent().focus();
					$(document).on("click", function (e1) {
						if ($(e1.target).parents('.mds_mo_share_dlg_container').length == 0) {
							dgShare.dialog('close');
							$(this).unbind(e1);
						}
					});
				}
			});

			$('input.mds_mo_share_dlg_ipt,textarea.mds_mo_share_dlg_ipt').click(function (e) {
				$(this).select(); // Auto select text for easy copy and paste
			});

			// Update download link when user selects new size in dropdown
			$(".mds_mo_share_dlg_ipt_select", dgShare).on('change', function (e) {
				var sel = this; // The select HTML element containing the sizes (thmb, opt, orig).

				var getContentUrl = function (dt) {
					var url = self.Data.App.AppUrl + '/' + self.Data.App.GalleryResourcesPath +
						"/services/api/contentitems/getmedia?moid=" + self.Data.MediaItem.Id +
						"&dt=" + dt +
						"&g=" + self.Data.Settings.GalleryId + "&sa=1";

					return url;
				};

				$('.mds_mo_share_dwnld', dgShare).attr('href', getContentUrl(sel.value));

				if (self.Data.MediaItem.ItemType == window.Mds.Constants.ItemType_External) {
					$('.mds_mo_share_dwnld', dgShare).click(function (e1) {
						if (parseInt(sel.value) != window.Mds.Constants.ViewSize_Thumbnail) {
							e1.preventDefault();
							e1.stopPropagation();
							$.mdsShowMsg('Download not available', 'External content objects cannot be downloaded. However, the source HTML can be accessed in the right pane.', { msgType: 'info', autoCloseDelay: 0 });
						}
					});
				}
			}).change();

			tb.EmbedButton
				.button({
					text: false,
					icons: { primary: "mds-ui-icon mds-ui-icon-embed" }
				})
				.click(function (e) {
					if (dgShare.dialog('isOpen') === true)
						dgShare.dialog('close');
					else {
						dgShare.dialog('open');
					}
					return false;
				});
		} else {
			tb.EmbedButton.hide();
		}

		if (this.Data.Settings.ShowSlideShowButton) {
			var createInlineSlideShowBtn = function () {
				var playOptions = { text: false, label: self.Data.Resource.MoTbSsStart, icons: { primary: "mds-ui-icon mds-ui-icon-ssplay" } };
				var pauseOptions = { text: false, label: self.Data.Resource.MoTbSsStop, icons: { primary: "mds-ui-icon mds-ui-icon-sspause" } };

				tb.SlideshowButton
					.prop('checked', self.Data.Settings.SlideShowIsRunning)
					.button(self.Data.Settings.SlideShowIsRunning ? pauseOptions : playOptions)
					.click(function (e) {
						if ($(this).prop('checked')) {
							if (self.startSlideshow()) {
								$(this).removeClass('mds_mvTbSlideshow_pause').addClass('mds_mvTbSlideshow_play')
									.button("option", pauseOptions);
							}
						} else {
							self.stopSlideshow();
							$(this).removeClass('mds_mvTbSlideshow_play').addClass('mds_mvTbSlideshow_pause')
								.button("option", playOptions);
						}
					});
			};

			var createFullScreenSlideShowBtn = function () {
				tb.SlideshowButton
					.button({
						text: false,
						icons: { primary: "mds-ui-icon mds-ui-icon-ssplay" }
					})
					.click(function (e) {
						self.startSlideshow();
						return false;
					});
			};

			if (this.Data.Settings.SlideShowType == 'Inline')
				createInlineSlideShowBtn();
			else if (this.Data.Settings.SlideShowType == 'FullScreen')
				createFullScreenSlideShowBtn();
			else
				$.mdsShowMsg("Error", 'Unrecognized SlideShowType value: ' + this.Data.Settings.SlideShowType, { msgType: 'error', autoCloseDelay: 0 });
		} else {
			tb.SlideshowButton.add(tb.SlideshowButton.next()).hide();
		}

		if (this.Data.Settings.ShowTransferContentObjectButton && this.Data.Album.Permissions.DeleteContentObject && this.Data.User.CanAddContentToAtLeastOneAlbum) {
			tb.MoveButton.button({
				text: false,
				icons: {
					primary: "mds-ui-icon mds-ui-icon-move"
				}
			})
				.click(function (e) {
					window.location = Mds.GetUrl(window.location.href, { g: 'cm_transferobject', moid: self.Data.MediaItem.Id, tt: 'move', skipstep1: 'true' });
					e.preventDefault();
				});
		} else {
			tb.MoveButton.hide();
		}

		if (this.Data.Settings.ShowCopyContentObjectButton && this.Data.User.CanAddContentToAtLeastOneAlbum) {
			tb.CopyButton.button({
				text: false,
				icons: {
					primary: "mds-ui-icon mds-ui-icon-copy"
				}
			})
				.click(function (e) {
					window.location = Mds.GetUrl(window.location.href, { g: 'cm_transferobject', moid: self.Data.MediaItem.Id, tt: 'copy', skipstep1: 'true' });
					e.preventDefault();
				});
		} else {
			tb.CopyButton.hide();
		}

		if (this.Data.Settings.ShowRotateContentObjectButton && this.Data.Album.Permissions.EditContentObject && !this.Data.Settings.IsReadOnlyGallery) {
			tb.RotateButton.button({
				text: false,
				icons: {
					primary: "mds-ui-icon mds-ui-icon-rotate"
				}
			})
				.click(function (e) {
					window.location = Mds.GetUrl(window.location.href, { g: 'cm_rotateimage', moid: self.Data.MediaItem.Id });
					e.preventDefault();
				});
		} else {
			tb.RotateButton.hide();
		}

		if (false) {
		    tb.ApproveButton.button({
		        text: false,
		        icons: {
		            primary: "mds-ui-icon mds-ui-icon-approve"
		        }
		    })
				.click(function (e) {
				    window.location = Mds.GetUrl(window.location.href, { g: 'task_rotateimage', moid: self.Data.MediaItem.Id });
				    e.preventDefault();
				});
		} else {
		    tb.ApproveButton.hide();
		}

		if (this.Data.Settings.ShowDeleteContentObjectButton && this.Data.Album.Permissions.DeleteContentObject) {
			tb.DeleteButton.button({
				text: false,
				icons: {
					primary: "mds-ui-icon mds-ui-icon-delete"
				}
			})
				.click(function (e) {
					var removeContentObject = function (idx) {
						// Remove the content object at the specified index from the client data and show the next content object
						// (or previous if there aren't any subsequent items).
						self.Data.Album.MediaItems.mdsRemove(idx);

						if (idx >= self.Data.Album.MediaItems.length)
							idx = self.Data.Album.MediaItems.length - 1; // Deleted item was the last one; set index to 2nd to last one

						if (idx >= 0) {
							$.each(self.Data.Album.MediaItems, function (indx, mo) {
								mo.Index = indx + 1; // Re-assign the index values of each content object
							});

							self.Data.Album.NumContentItems--;
							self.Data.Album.NumMediaItems--;
							self.Data.MediaItem = self.Data.Album.MediaItems[idx - 1];
							self.showNextContentObject();
						} else self.Data.MediaItem = null; // No more items in album; set to null. Calling code will detect and then redirect
					};

					if (confirm(self.Data.Resource.ContentDeleteConfirm)) {
						{
							var id = self.Data.MediaItem.Id;
							removeContentObject($.inArray(self.Data.MediaItem, self.Data.Album.MediaItems));

							window.Mds.DataService.deleteContentObject(id, null, function () {
								if (self.Data.MediaItem == null) self.redirectToAlbum();
							}, function () {
								$.mdsShowMsg("Cannot Delete Content Object", "An error occurred on the server. Check the gallery's event log for details.", { msgType: 'error', autoCloseDelay: 0 });
							});
						}
					}
					e.preventDefault();
				});
		} else {
			tb.DeleteButton.hide();
		}

		return tb;
	};

	window.MdsContentToolbar = function () {
		this.EmbedButton = null;
		this.SlideshowButton = null;
		this.MoveButton = null;
		this.CopyButton = null;
		this.RotateButton = null;
		this.DeleteButton = null;
		this.ApproveButton = null;
	};

	//#endregion End Content view functions

	//#region mdsThumbnails plug-in

	$.fn.mdsThumbnails = function (tmplName, data) {
		var self = this;
		var $target = this;

		var initialize = function () {

			var attachEvents = function () {
				// Attach a handler for when a metaitem is updated.
				$('#' + data.Settings.ThumbnailClientId).on('metaUpdate.' + data.Settings.ClientId, function (e, gim) {
					// Meta data has been updated. Let's re-render the thumbnail view so that is always shows the
					// latest data (e.g. the title/caption may have changed).
					// e is the jQuery event object; gim is the ContentItemMeta instance identifying what was updated.
					var selItemsOld = $(".ui-selected", this); // Grab a reference to which thumbnails are selected
					renderThmbView(); // Render the template (which will wipe out the current selection)

					// Re-select thumbnails that were previously selected
					$(".thmb", self).filter(function () {
						return selItemsOld.is("li[data-id=" + $(this).data("id") + "][data-it=" + $(this).data("it") + "]");
					}).addClass("ui-selected");

					// Fire the stop event function to simulate the user selecting the thumbnails.
					var s = $(".thmb", self).parent().selectable("option", "stop");
					if (typeof (s) == 'function')
						s();
				});
			};

			var renderThmbView = function () {
				var albumHtml = $.render[tmplName](data);

				if (!renderPager(albumHtml)) {
					self.html(albumHtml); // Render HTML template and add to page
					configThmbs(); // Assign width & height of thumbnails, make selectable
				}

				configHeader();
			};

			var configHeaderAlbumPrivacy = function () {
				$(".mds_abm_sum_pvt_trigger", $target).click(function (e) {

					var self2 = this;

					var toggleAlbumPrivacy = function () {
						data.Album.IsPrivate = !data.Album.IsPrivate;

						$(self2)
							.find('img').attr("src", data.Album.IsPrivate ? data.App.SkinPath + '/images/lock-active-s.png' : data.App.SkinPath + '/images/lock-s.png')
							.attr('title', data.Album.IsPrivate ? data.Resource.AbmIsPvtTt : data.Resource.AbmNotPvtTt);
					};

					if (!data.Settings.AllowAnonBrowsing) {
						$.mdsShowMsg(data.Resource.AbmAnonDisabledTitle, data.Resource.AbmAnonDisabledMsg);
						return false;
					}

					$target.addClass('mds_wait'); // Show wait animated gif
					toggleAlbumPrivacy();

					Mds.DataService.saveAlbum(data.Album, null, function () {
						var msg = (data.Album.IsPrivate ? data.Resource.AbmIsPvtTt : data.Resource.AbmNotPvtTt);
						$.mdsShowMsg(data.Resource.AbmPvtChngd, msg);
						$target.removeClass('mds_wait');
					},
						function (response) {
							toggleAlbumPrivacy(); // Revert back
							$.mdsShowMsg("Cannot Edit Album", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
							$target.removeClass('mds_wait');
						});

					return false;
				});
			};

			var configHeaderSort = function () {
				// Handle the sorting functionality.
				var changeAlbumSortAndRebind = function () {
					// A user with edit album permission is requesting to sort the album. Send the request to the server,
					// which sorts the album and persists it to the DB. When complete, make a GET request to get the newly sorted items,
					// then assign the results to our local data object and rebind.
					$target.addClass('mds_wait'); // Show wait animated gif

					$.ajax({
						type: "POST",
						url: url = window.Mds.AppRoot + '/services/api/albumrests/' + data.Album.Id + '/sortalbum?sortByMetaNameId=' + data.Album.SortById + '&sortAscending=' + data.Album.SortUp,
						success: function () {
							// The items have been resorted on the server and persisted to the DB. Now get them.
							$.ajax({
								type: "GET",
								url: window.Mds.AppRoot + '/services/api/albumrests/' + data.Album.Id + '/contentitems',
								success: function (contentItems) {
									data.Album.ContentItems = contentItems;
									renderThmbView();
									$target.removeClass('mds_wait');
								},
								error: function (response) {
									$.mdsShowMsg("Action Aborted", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
									$target.removeClass('mds_wait');
								}
							});
						},
						error: function (response) {
							$.mdsShowMsg("Action Aborted", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
							$target.removeClass('mds_wait');
						}
					});
				};

				var getAlbumSortAndRebind = function () {
					// A user without edit album permission is requesting to sort the album, or the album is a virtual one.
					// Send the request to the server, which sorts the album and returns the results. Assign the results 
					// to our local data object and rebind.
					var getSortRequestData = function () {
						var contentItemAction = {};

						contentItemAction.Album = Mds.deepCopy(data.Album);
						contentItemAction.SortByMetaNameId = data.Album.SortById;
						contentItemAction.SortAscending = data.Album.SortUp;

						contentItemAction.Album.MediaItems = null; // Save bandwidth by getting rid of unnecessary data
						contentItemAction.Album.MetaItems = null; // Save bandwidth by getting rid of unnecessary data

						if (contentItemAction.Album.Id > Mds.Constants.IntMinValue) {
							// Save bandwidth by not passing gallery items to server. The server will load them from the album ID.
							contentItemAction.Album.ContentItems = null;
						}

						return contentItemAction;
					};

					$target.addClass('mds_wait'); // Show wait animated gif

					$.ajax(({
						type: "POST",
						url: window.Mds.AppRoot + '/services/api/albumrests/getsortedalbum',
						data: JSON.stringify(getSortRequestData()),
						contentType: "application/json; charset=utf-8",
						dataType: "json",
						success: function (contentItems) {
							data.Album.ContentItems = contentItems;
							renderThmbView();
							$target.removeClass('mds_wait');
						},
						error: function (response) {
							$.mdsShowMsg("Action Aborted", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
							$target.removeClass('mds_wait');
						}
					}));
				};

				var sortAlbum = function () {
					if (data.Album.Permissions.EditAlbum && data.Album.VirtualType == Mds.Constants.VirtualType_NotVirtual)
						changeAlbumSortAndRebind();
					else
						getAlbumSortAndRebind();
				};

				var getSortIndicatorHtml = function () {
					if (data.Album.SortById == window.Mds.Constants.IntMinValue) {
						return '<span class="ui-icon ui-icon-circle-check" />';
					} else {
						return (data.Album.SortUp ? '<span class="ui-icon ui-icon ui-icon-circle-arrow-n" />' : '<span class="ui-icon ui-icon ui-icon-circle-arrow-s" />');
					}
				};

				$(".mds_abm_sum_rs", $target)
					.button({ text: false, icons: { primary: "ui-icon-arrowthick-2-n-s" } })
					.click(function () {
						data.Album.SortUp = !data.Album.SortUp;
						sortAlbum();
						return false;
					})
					.next()
					.button({ text: false, icons: { primary: "ui-icon-triangle-1-s" } })
					.click(function () {
						var menu = $(this).parent().next().show().position({ my: "right top", at: "right bottom", of: this });
						$(document).one("click", function () {
							menu.hide();
						});
						return false;
					})
					.parent().buttonset().next().hide().menu() // menu() is applied to the <ul> containing the sort options
					.children().click(function () {
						var sortById = $('a', $(this)).data('id');
						if (sortById != null) {
							data.Album.SortUp = true; // When a new sort field is selected, reset the sort direction to ASC
							data.Album.SortById = sortById; // Get the sort ID (e.g. <li><a href='#' data-id='29'>Title</a></li>)
							sortAlbum();
						}
						return false;
					})
					.children('a[data-id=' + data.Album.SortById + ']').parent().prepend(getSortIndicatorHtml()); // Add the sort icon to the current sort field
			};

			var configHeaderShare = function () {
				var dgShare = $(".mds_abm_sum_share_dlg", $target);

				dgShare.dialog({
					appendTo: '#' + data.Settings.ClientId,
					autoOpen: false,
					draggable: false,
					resizable: false,
					closeOnEscape: true,
					dialogClass: 'mds_abm_sum_share_dlg_container',
					width: 420,
					minHeight: 0,
					show: 'fade',
					hide: 'fade',
					position: { my: "right top", at: "right bottom", of: $(".mds_abm_sum_sa_trigger", $target) },
					open: function (e, ui) {
						$(this).parent().focus();
						$(document).on("click", function (e1) {
							if ($(e1.target).parents('.mds_abm_sum_share_dlg_container').length == 0) {
								dgShare.dialog('close');
								$(this).unbind(e1);
							}
						});
					}
				});

				$(".mds_abm_sum_sa_trigger", $target).click(function (e) {
					if (dgShare.dialog('isOpen') === true)
						dgShare.dialog('close');
					else {
						dgShare.dialog('option', 'hide', null).dialog('close').dialog('option', 'hide', 'fade'); // Kill, then restore fade for quicker closing
						dgShare.dialog('open');
					}
					return false;
				});

				$('input.mds_abm_sum_share_dlg_ipt').click(function (e) {
					$(this).select(); // Auto select text for easy copy and paste
				});
			};

			var configHeaderSlideShow = function () {
				$(".mds_abm_sum_ss_trigger", $target).click(function (e) {
					// Send the user to the media view page of the first image in this album, including
					// the 'ss=1' query string parm that will trigger an auto slide show when the page loads.
					var findFirstImage = function () {
						if (data.Album != null && data.Album.ContentItems != null)
							return $.grep(data.Album.ContentItems, function (gi) { return gi.ItemType === window.Mds.Constants.ItemType_Image; })[0];
						else
							return null;
					};

					var img = findFirstImage();

					if (img != null) {
						var qs = { aid: null, moid: null, ss: 1 };
						qs.moid = img.Id;

						window.location = Mds.GetUrl(document.location.href, qs);
					} else
						$.mdsShowMsg(data.Resource.MoNoSsHdr, data.Resource.MoNoSsBdy, { msgType: 'info' });

					return false;
				});
			};

			var configHeaderAlbumOwner = function () {
				var dg = $(".mds_abm_sum_ownr_dlg", $target);

				dg.dialog({
					appendTo: '#' + data.Settings.ClientId,
					autoOpen: false,
					draggable: false,
					resizable: false,
					closeOnEscape: true,
					dialogClass: 'mds_abm_sum_ownr_dlg_container',
					width: 420,
					minHeight: 0,
					show: 'fade',
					hide: 'fade',
					position: { my: "left top", at: "left bottom", of: $(".mds_abm_sum_ownr_trigger", $target) },
					open: function (e, ui) {
						$('.mds_abm_sum_ownr_dlg_o > span', $(this)).mdsTooltip({
							title: data.Resource.AbmOwnr,
							content: data.Resource.AbmOwnrTtDtl
						});

						var ts = this;
						setTimeout(function () { $(ts).parent().find('.mds_abm_sum_ownr_dlg_ipt').focus(); }, 500); // Delay needed or else focus stays on tooltip icon for some reason

						$(document).on("click", function (e1) {
							if ($(e1.target).parents('.mds_abm_sum_ownr_dlg_container').length == 0) {
								dg.dialog('close');
								$(this).unbind(e1);
							}
						});
					},
					buttons: {
						"Save": function () {
							var oldAbmOwnr = data.Album.Owner;
							data.Album.Owner = $(this).find('.mds_abm_sum_ownr_dlg_ipt').val();

							if (oldAbmOwnr != data.Album.Owner) {
								$target.addClass('mds_wait');
								Mds.DataService.saveAlbum(data.Album, null, function () {
									if (Mds.isNullOrEmpty(data.Album.Owner)) {
										$.mdsShowMsg(data.Resource.AbmOwnrChngd, data.Resource.AbmOwnrClrd.format(oldAbmOwnr));
									} else {
										$.mdsShowMsg(data.Resource.AbmOwnrChngd, data.Resource.AbmOwnrChngdDtl.format(data.Album.Owner));
									}
									$target.removeClass('mds_wait');
								},
									function (response) {
										data.Album.Owner = oldAbmOwnr; // Error, so revert
										$.mdsShowMsg("Cannot Edit Album", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
										$target.removeClass('mds_wait');
									});
							}

							$(this).dialog("close");
						},
						Cancel: function () {
							$(this).dialog("close");
						}
					}
				});

				$(".mds_abm_sum_ownr_trigger", $target).click(function (e) {
					if (dg.dialog('isOpen') === true)
						dg.dialog('close');
					else {
						dg.dialog('option', 'hide', null).dialog('close').dialog('option', 'hide', 'fade'); // Kill, then restore fade for quicker closing
						dg.dialog('open');
					}
					return false;
				});
			};

			var configHeader = function () {
				// When user clicks header area, make the album the current item, unselect any selected thumbnails, and trigger event to be handled in meta plug-in
				$(".mds_abm_sum", $target).click(function () {
					$(".thmb", self).removeClass('ui-selected');
					data.ActiveContentItems = [window.Mds.convertAlbumToContentItem(data.Album)];
					$target.trigger('select.' + data.Settings.ClientId, [data.ActiveContentItems]);
				});

				configHeaderAlbumPrivacy();
				configHeaderSort();
				configHeaderShare();
				configHeaderSlideShow();
				configHeaderAlbumOwner();
			};

			var pagerRequired = function () {
				return (data.Settings.PageSize > 0 && data.Album.ContentItems.length > data.Settings.PageSize);
			};

			var renderPager = function (albumHtml) {
				if (!pagerRequired())
					return false;

				var $albumHtml = $(albumHtml);
				var $albumHtmlThmbs = $albumHtml.find('.thmb').clone();
				var $albumHtmlWithoutThmbs = $albumHtml.find('.thmb').remove().end();
				var pager;

				var pagerOptions = {
					format: '[< c >]',
					page: null, // we await hashchange() event
					lapping: 0,
					perpage: data.Settings.PageSize,
					onSelect: function (page) {
						// Retrieve the HTML for the desired slice and replace existing thumbnails with them.
						var visibleIndices = this.slice; // Contains start and end indices for visible elements
						$(".thmb", self).remove();

						// Get array of thumbnail elements for the desired page, then add to page DOM, and configure
						var html = $albumHtmlThmbs.slice(visibleIndices[0], visibleIndices[1]);
						$('.mds_abm_thmbs').append(html.hide().fadeIn());
						configThmbs();

						return true;
					},
					onFormat: function (type) {
						switch (type) {
							case 'block':
								// n and c
								return '<span class="mds_pagerText">' + data.Resource.AbmPgrStatus.format(this.value, this.pages) + '</span>';
							case 'next':
								// >
								if (this.active)
									return '<a href="#' + this.value + '" title="' + data.Resource.AbmPgrNextTt + '">›</a>';
								else
									return '<span class="mds_disabled">›</span>';
							case 'prev':
								// <
								if (this.active)
									return '<a href="#' + this.value + '" title="' + data.Resource.AbmPgrPrevTt + '">‹</a>';
								else
									return '<span class="mds_disabled">‹</span>';
							case 'first':
								// [
								if (this.active)
									return '<a href="#' + this.value + '" title="' + data.Resource.AbmPgrFirstTt + '" class="mds_first-child">«</a>';
								else
									return '<span class="mds_disabled mds_first-child">«</span>';
							case 'last':
								// ]
								if (this.active)
									return '<a href="#' + this.value + '" title="' + data.Resource.AbmPgrLastTt + '" class="mds_last-child">»</a>';
								else
									return '<span class="mds_disabled mds_last-child">»</span>';
						}
					}
				};

				// Render album template except for thumbnails (we'll add the thumbs in onSelect)
				self.html($albumHtmlWithoutThmbs);

				var pgr = $();
				var pagerHtml = '<div class="mds_pager"></div>';
				if (data.Settings.PagerLocation == 'Top' || data.Settings.PagerLocation == 'TopAndBottom') {
					pgr = $(pagerHtml).prependTo(self);
				}

				if (data.Settings.PagerLocation == 'Bottom' || data.Settings.PagerLocation == 'TopAndBottom') {
					pgr = pgr.add($(pagerHtml).appendTo(self));
				}

				pager = pgr.paging(data.Album.ContentItems.length, pagerOptions);

				$(window).on('hashchange', (function () {
					if (window.location.hash)
						pager.setPage(window.location.hash.substr(1));
					else
						pager.setPage(1); // Default to 1st page
				}));

				$(window).trigger('hashchange');

				return true;
			};

			var hndleDom = "<div class='hndl'></div>"; // The drag handle for rearranging thumbnails

			var thmbSelected = function (e, ui) {
				// Get a reference to the selected gallery items, then trigger event to be handled in meta plug-in
				var selItems = $(".ui-selected", self).map(function () {
					var $this = $(this);
					var id = $this.data("id");
					var itemType = $this.data("it");
					// Get the gallery item that matches the thumbnail that was selected
					return $.map(data.Album.ContentItems, function (obj) {
						return (obj.Id === id && obj.ItemType === itemType ? obj : null);
					})[0];
				}).get();

				if (data.Album.Permissions.EditAlbum && data.Album.SortById == window.Mds.Constants.IntMinValue) {
					if (window.Mds.isTouchScreen && selItems.length == 1) {
						$(".ui-selected", self).prepend(hndleDom);
					}
				}

				data.ActiveContentItems = selItems.length > 0 ? selItems : [window.Mds.convertAlbumToContentItem(data.Album)];

				$target.trigger('select.' + data.Settings.ClientId, [data.ActiveContentItems]);
			};

			var thmbUnselected = function (e, ui) {
				$('.hndl', ui.unselected).remove();
			};

			var configThmbs = function () {
				var thmbs = $(".thmb", self);
				thmbs.equalSize(); // Make all thumbnail tags the same width & height

				// Make thumbnails selectable, but not for touchscreens when the left & right panes are hidden (user can't scroll otherwise)
				// Left/right panes not shown when < 750px wide (see gallery.css & media.ascx)
				var isSinglePaneTouchScreen = window.Mds.isTouchScreen() && window.Mds.isWidthLessThan(750);

				if (!isSinglePaneTouchScreen) {
					thmbs.parent().selectable({
						filter: 'li',
						cancel: "a,.hndl",
						stop: thmbSelected,
						unselected: thmbUnselected
					});
				}

				// Make thumbnails sortable
				if (data.Album.Permissions.EditAlbum && data.Album.SortById == window.Mds.Constants.IntMinValue) {

					var onManualSort = function (e, ui) {
						ui.item.addClass('mds_wait');

						// Get the items in their current sequence and pass that to the server's sort method.
						var gItems = ui.item.parent().children().map(function () {
							var $this = $(this);
							var id = $this.data("id");
							var itemType = $this.data("it");

							return { Id: id, ItemType: itemType };
						}).get();

						$.ajax(({
							type: "POST",
							url: window.Mds.AppRoot + '/services/api/albumrests/sortcontentobjects',
							data: JSON.stringify(gItems),
							contentType: "application/json; charset=utf-8",
							dataType: "json",
							success: function () {},
							error: function (response) {
								$.mdsShowMsg("Cannot save", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
							},
							complete: function () {
								ui.item.removeClass('mds_wait');
							},
						}));
					};

					thmbs
						.css({ '-ms-touch-action': 'none', 'touch-action': 'none' }) // Required to allow dragging on touchscreens (-ms-touch-action applies to IE10 only)
						.parent()
						.sortable({
							start: function (e, ui) {
								ui.placeholder.width(ui.helper.width());
								ui.placeholder.height(ui.helper.height());
							},
							stop: onManualSort,
							scroll: true,
							containment: "document",
							cursor: "move",
							handle: ".hndl"
						});

					// Show/hide the drag bar when a user hovers over the thumbnail, but only for non-touchscreens.
					// We use stop/unselected events to handle show/hide for touchscreens.
					if (!window.Mds.isTouchScreen()) {
						thmbs.hover(
							function () { $(this).prepend(hndleDom); },
							function () { $('.hndl', this).remove(); }
						);
					}
				}
			};

			var jsRenderSetup = function () {
				// Set up converters that can strip all HTML from some text and truncate. There is a related stripHtml converter in
				// window.Mds.Init(). Note that the released version of jsRender may support chained converters, which would allow 
				// us to create one for stripping and one for truncating, then chain them instead of using stripHtmlAndTruncate.
				// See https://github.com/BorisMoore/jsrender/issues/127
				$.views.converters({
					stripHtmlAndTruncate: function (text) {
						var t = text.replace(/(<[^<>]*>)/g, "");
						var m = data.Settings.MaxThmbTitleDisplayLength;
						return (t.length > m ? Mds.escape(t.substr(0, m)) + '...' : Mds.escape(t));
					}
				});
			};

			if (!data.Album.ContentItems) {
				$.mdsShowMsg("Cannot Render Album", "<p>Cannot render the album thumbnails. Navigate to an album and then return to this page.</p><p>You'll know you got it right when you see 'aid' In the URL's query string.</p><p>ERROR: data.Album.ContentItems is null.</p>", { msgType: 'error', autoCloseDelay: 0 });
				return;
			}

			jsRenderSetup(); // Prepare jsRender for rendering
			attachEvents();
			renderThmbView();
		};
		initialize();

		return this;
	};

	//#endregion mdsThumbnails jQuery plug-in

	//#region mdsMeta plug-in

	$.fn.mdsMeta = function (data, options) {
		var self = this;
		return this.each(function () {
			if (!$.data(this, 'plugin_mdsMeta')) {
				var mdsMeta = MdsMeta();
				mdsMeta.init(self, data, options);
				$.data(this, 'plugin_mdsMeta', mdsMeta);
			}
		});
	};

	$.fn.mdsMeta.defaults = {
		tmplName: ''
	};

	// Define object to handle metadata. Uses Revealing Module Pattern from here:
	// http://weblogs.asp.net/dwahlin/archive/2011/09/05/creating-multiple-javascript-objects-when-using-the-revealing-module-pattern.aspx
	window.MdsMeta = function () {
		var $target,
				data,
				settings,
				init = function (target, mdsData, options) {
					$target = target;
					data = mdsData;
					settings = $.extend({}, $.fn.mdsMeta.defaults, options);

					bindData();

					// Bind to next, previous, and mediaUpdated events from the MdsContent plug-in so that we can refresh the metadata.
					$('#' + data.Settings.ContentClientId).on('next.' + data.Settings.ClientId + ' previous.' + data.Settings.ClientId + ' mediaUpdate.' + data.Settings.ClientId, showMeta);

					// Bind to the select event from the mdsThumbnails plug-in so we can refresh the metadata.
					$('#' + data.Settings.ThumbnailClientId).on('select.' + data.Settings.ClientId, showMeta);
				},
				bindData = function () {
					// Render the right pane template to the page DOM
					$target.removeClass('mds_wait').html($.render[settings.tmplName](data));

					// Add separator row between the top and bottom sections of the metadata table
					$('.mds_m1Row:last').after('<tr class="mds_mSep"><td colspan="2"></td></tr>');

					var hasExistingMetaItems = data.ActiveMetaItems.length > 0;
					var hasExistingApprovalItems = data.ActiveApprovalItems.length > 0;

					var hasEditPermission = function () {
						// If any of the selected items are a content object, user must have EditContentObject perm.
						// If any of the selected items are an album, user must have EditAlbum perm.
						var hasAlbum, hasMediaItem;
						$.each(data.ActiveContentItems, function () {
							if (this.IsAlbum)
								hasAlbum = true;
							else
								hasMediaItem = true;
						});
						
						var canEdit = ((!hasAlbum || data.Album.Permissions.EditAlbum) && (!hasMediaItem || data.Album.Permissions.EditContentObject));
						var isVirtualAlbum = data.Album.VirtualType != Mds.Constants.VirtualType_NotVirtual;
						var isChildSelected = (data.ActiveContentItems.length >= 0 && data.ActiveContentItems[0].Id != data.Album.Id);
						
						if (!canEdit && isVirtualAlbum && isChildSelected && data.User.IsAuthenticated) {
							// Logged-on user is looking at a virtual album and has limited permissions. User may have edit permission to the 
							// particular items that are selected, so make a callback to see.
							$.ajax(({
								type: "POST",
								async: false,
								url: window.Mds.AppRoot + '/services/api/contentitemmeta/canuseredit',
								data: JSON.stringify(data.ActiveContentItems),
								contentType: "application/json; charset=utf-8",
								dataType: "json",
								success: function (result) {
									canEdit = result;
								},
								error: function (response) {
									$.mdsShowMsg("Cannot Retrieve Data", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
								}
							}));
						}

						return canEdit;
					};

					if (hasExistingMetaItems && hasEditPermission())
						makeMetaEditable();

					convertTagsToLinks();
					configureRating();
					//configurePlayers();
					//configureApproval();
				},
				convertTagsToLinks = function () {
					// Look for the comma-separated tags (they have CSS class names mds_mtag & mds_mpeople) and convert them to hyperlinks.
					$.each(['tag', 'people'], function (i, tagType) {
						// Find the tag or people tag, but not if it's been previously made editable (we won't do anything in those cases)
						var tagContainer = $('.mds_meta tr td.mds_m' + tagType + ':not(.mds_editableContent)', $target);

						// Build HTML links to represent the tags.
						var html = $.map(tagContainer.text().split(','), function (item) {
							var tag = item.trim();
							if (tag.length > 0) {
								var parms = { title: null, tag: null, people: null, search: null, latest: null, filter: null, rating: null, top: null, aid: null, moid: null };
								parms[tagType] = tag.replace(/\s+\(\d+\)$/gi, ''); // Strip off the trailing count (e.g ' (3)') if present

								return '<a href="' + Mds.GetUrl(window.location.href, parms) + '" class="mds_mtaglink">' + tag + '</a>';
							}
							return null;
						});

						tagContainer.text('').html(html); // Replace text with HTML links
					});
				},
				configureRating = function () {
					// Get rating element. Gets a match ONLY when the admin has configured the rating meta item as editable.
					var editableRatingEls = $('.mds_meta tr[data-iseditable=true] td.mds_v .mds_rating', $target);

					if (editableRatingEls.length > 0 && (data.User.IsAuthenticated || data.Settings.AllowAnonymousRating)) {
						// Configure an editable rating
						editableRatingEls.rateit({
							min: 0,
							max: 5,
							resetable: false
						}).bind('rated', function (e, v) {
							$target.addClass('mds_wait');

							var metaTypeId = getMetaItem($(e.target).closest('.mds_m2Row').data('id')).MTypeId;
							var contentItemMeta = { ContentItems: data.ActiveContentItems, MetaItem: { MTypeId: metaTypeId, Value: v } };

							Mds.DataService.saveMeta(contentItemMeta,
								function () { $target.removeClass('mds_wait'); },
								function (o) {
								},
								function (response) { $.mdsShowMsg("Cannot Save Changes", response.responseText, { msgType: 'error', autoCloseDelay: 0 }); }
							);
						});
					} else {
						// Configure a read-only rating
						$('.mds_rating', $target).rateit({
							min: 0,
							max: 5,
							resetable: false,
							readonly: true
						});
					}
				},
                configurePlayers = function () {
                    // Get Player element. Gets a match ONLY when the admin has configured the player meta item 
                    var editablePlayerEls = $('.mds_meta tr[data-iseditable=true] td.mds_mPlayer', $target); 
                    if (editablePlayerEls.length > 0) {
                        editablePlayerEls.each(function () {
                            var selectplayers = Mds.escape($(this).text()).split(';');
                            var playerhtml = "<select class='mds_mPlayer_ipt' multiple='multiple'><option>(all)</option>";
                            $.each(data.PlayerItems, function (i, el) {
                                playerhtml += "<option value='";
                                playerhtml += el.UniqueName;
                                playerhtml += "'"
                                if (selectplayers.indexOf(el.UniqueName) != -1) {
                                    playerhtml += " selected = 'selected'";
                                }
                                playerhtml += " >"
                                playerhtml += el.PlayerName;
                                playerhtml += "</option>";
                            });
                            playerhtml += "</select>";
                            $(this).html(playerhtml);
                            //$(this).html("<select class='mds_mPlayer_ipt' multiple='multiple'><option>(all)</option><option>Player1</option><option>Player2</option><option>Player3</option></select>");
                            var ipt = $('.mds_mPlayer_ipt', $(this));
                            ipt.dropdownchecklist({
                                icon: {}, width: 350, firstItemChecksAll: true, maxDropHeight: 400, emptyText: data.Resource.LpSelectPlayer,
                                onComplete: function (selector) {
                                    $target.addClass('mds_wait');
                                    var values = "";
                                    for (i = 0; i < selector.options.length; i++) {
                                        if (selector.options[i].selected && (selector.options[i].value != "")) {
                                            if (values != "") values += ";";
                                            values += selector.options[i].value;
                                        }
                                    }
                                    var metaTypeId = getMetaItem($(selector).closest('.mds_mRowDtl').data('id')).MTypeId;
                                    var contentItemMeta = { ContentItems: data.ActiveContentItems, MetaItem: { MTypeId: metaTypeId, Value: values } };

                                    Mds.DataService.saveMeta(contentItemMeta,
                                        function () { $target.removeClass('mds_wait'); },
                                        function (gim) {
                                            if (data.MediaItem != null) {
                                                // When showing a single content object, update the data object's meta value so it is available during next/previous browsing
                                                var mi = Mds.findMetaItem(data.MediaItem.MetaItems, gim.MetaItem.MTypeId);
                                                if (mi != null) mi.Value = gim.MetaItem.Value;
                                            }
                                        },
                                        function (response) { $.mdsShowMsg("Cannot Save Changes", response.responseText, { msgType: 'error', autoCloseDelay: 0 }) });
                                }
                            });
                        });
                    }
                    else {
                        var getPlayerItem = function (playerItems, uniqueName) {
                            return $.grep(playerItems, function (mi) { return mi.UniqueName === uniqueName; })[0] || null;
                        };
                        $('.mds_mPlayer', $target).each(function () {
                            var selectplayers = Mds.escape($(this).text()).split(';');
                            var values = "";
                            $.each(selectplayers, function (i, el) {
                                var playerItem = getPlayerItem(data.PlayerItems, el);
                                if (playerItem != null) {
                                    if (values != "") values += "; ";
                                    values += playerItem.PlayerName;
                                }
                            });
                            $(this).text(values);
                        });
                    }
                },
                configureApproval = function () {
                    // Get Approval element. Gets a match ONLY when the admin has configured the approval meta item 
                    var editableApprovalEls = $('.mds_meta tr[data-iseditable=true] td.mds_mApproval', $target);
                    if (editableApprovalEls.length > 0) {
                        editableApprovalEls.each(function () {
                            var approval = Mds.escape($(this).text());
                            $(this).html("<input type='radio' id='mds_mApproved' name='mds_mApproval_ipt' value='1'" +
                                (approval == '1' ? " checked='true' />" : " />") + "<label for='mds_mApproved'>" + data.Resource.ContentApproved +
                                "</label><input type='radio' id='mds_mRejected' name='mds_mApproval_ipt' value='2'" +
                                (approval == '2' ? " checked='true' />" : " />") + "<label for='mds_mRejected'>" + data.Resource.ContentRejected +
                                "</label><input type='radio' id='mds_mNoAction' name='mds_mApproval_ipt' value='0'" +
                                (approval == '0' ? " checked='true' />" : " />") + "<label for='mds_mNoAction'>" + data.Resource.ContentNoAction + "</label>");
                            $(":radio[name=mds_mApproval_ipt]").click(function (e) {
                                var v = this.value;
                                $target.addClass('mds_wait');

                                var metaTypeId = getMetaItem($(e.target).closest('.mds_mRowDtl').data('id')).MTypeId;
                                var contentItemMeta = { ContentItems: data.ActiveContentItems, MetaItem: { MTypeId: metaTypeId, Value: v } };

                                Mds.DataService.saveMeta(contentItemMeta,
                                    function () { $target.removeClass('mds_wait'); },
                                    function (gim) {
                                        if (data.MediaItem != null) {
                                            // When showing a single content object, update the data object's meta value so it is available during next/previous browsing
                                            var Values = gim.MetaItem.Value.split(";");
                                            var mi = Mds.findMetaItem(data.MediaItem.MetaItems, gim.MetaItem.MTypeId);
                                            if (mi != null && Values.length > 0) mi.Value = Values[0]; 
                                            var miApproval = Mds.findMetaItem(data.MediaItem.MetaItems, 47);
                                            if (miApproval != null && Values.length > 1) {
                                                miApproval.Value = Values[1];
                                                $('.mds_mApprovalUser', $target).text(miApproval.Value);
                                            }
                                            var miDate = Mds.findMetaItem(data.MediaItem.MetaItems, 48);
                                            if (miDate != null && Values.length > 2) {
                                                miDate.Value = Values[2];
                                                $('.mds_mApprovalDate', $target).text(miDate.Value);
                                            }
                                        }
                                    },
                                    function (response) { $.mdsShowMsg("Cannot Save Changes", response.responseText, { msgType: 'error', autoCloseDelay: 0 }) });
                            });
                        });
                    }
                },
				makeMetaEditable = function () {
					// Set up the default options for the Jeditable plug-in
					var editableOptions = {
						type: 'text',
						rows: 1,
						width: 'auto',
						widthBuffer: 6,
						tooltip: data.Resource.ContentCaptionEditTt,
						event: 'click', //dblclick
						cssclass: 'mds_editableContentForm',
						indicator: '<img class="mds_wait_img" src="' + data.App.SkinPath + '/images/wait-squares.gif" />',
						style: "display:block",
						placeholder: data.Resource.MetaEditPlaceholder,
						submitonenter: true,
						onblur: 'submit',
						//onblur: '', // Use this instead of previous line to prevent auto-cancel when blurring away from input
						data: function (curValue, obj) { return onGetData(curValue, obj, $(this)); },
						oneditbegin: function (s, el, e) { return onEditBegin($(el), e); },
						oneditend: function () { onEditEnd($(this)); },
						onreset: function (frm, obj) { onReset($(obj)); },
						onsubmit: onsubmit,
						oncomplete: function (xmldata) { return onComplete($(this), xmldata); },
						//onerror: function (s, el, r) { }, // Don't use because the item doesn't reset itself properly
						callback: function (value, s) { setHover($(this), true); },
						ajaxoptions: {
							type: 'PUT',
							contentType: 'application/json;charset=utf-8'
						}
					};

					// Copy the default options and change those necessary for the caption metadata item.
					var editableOptionsCaption = Mds.deepCopy(editableOptions);
					editableOptionsCaption.type = 'textarea';
					editableOptionsCaption.rows = '4';
					editableOptionsCaption.submitonenter = false;

					// Make another copy for use in metadata that use the autoSuggest plug-in (e.g. tags)
					var editableOptionsTag = Mds.deepCopy(editableOptions);
					editableOptionsTag.onblur = ''; // Do nothing when blurring away from input tag

					// Get a reference to all metadata items and set the hover functionality
					var els = $('.mds_meta tr[data-iseditable=true] td.mds_v', $target).addClass('mds_editableContent');

					// Get reference to the subset of elements that are the caption and tag
					var elsCaption = els.filter(".mds_mCaption");
					var elsTag = els.filter(".mds_mtag,.mds_mpeople");
					var elsRating = els.filter(".mds_mrating");
					var elsApproval = els.filter(".mds_mApproval");
					var elsPlayer = els.filter(".mds_mPlayer");
					var elsRemaining = els.not(elsCaption).not(elsTag).not(elsRating).not(elsApproval).not(elsPlayer); // All elements that aren't the caption, tag-based item, or rating 

					// Make metadata editable, using the appropriate set of options
					elsCaption.editable(window.Mds.AppRoot + '/services/api/contentitemmeta', editableOptionsCaption);
					//elsTag.editable(window.Mds.AppRoot + '/services/api/meta', editableOptionsTag);
					elsRemaining.editable(window.Mds.AppRoot + '/services/api/contentitemmeta', editableOptions); // Matches all elements not in elsCaption and elsTag

					// Set up the hover functionality
					setHover(elsCaption, true);
					setHover(elsRemaining, true);

					var getTagValue = function (e) {
						// Get the tag value, stripping off trailing count if present. Ex: If tag="Animal (3)", change it to "Animal".
						// The parameter 'e' is expected to be a jQuery reference to the li element containing the tag.
						return e.contents().filter(function () {
							return this.nodeType == 3;
						}).text().trim().replace(/\s+\(\d+\)$/gi, '');
					};

					elsTag.each(function () {
						$(this).html("<input class='mds_mTag_ipt' value='" + Mds.escape($(this).text()) + "' placeholder='" + data.Resource.MetaEditPlaceholder + "' />");

						var initComplete = false;
						var ipt = $('input', $(this));
						var tagType = ipt.closest('.mds_mtag').length > 0 ? 'tags' : 'people';

						ipt.autoSuggest(window.Mds.AppRoot + '/services/api/meta/' + tagType, {
							extraParams: '&galleryId=' + data.Settings.GalleryId,
							preFill: ipt.val(),
							startText: data.Resource.MetaEditPlaceholder,
							selectionClick: function (e) {
								// User clicked the tag. Navigate to a page showing all objects with that tag.
								var tagValue = e.parents('.mds_mtag').length == 0 ? null : getTagValue(e);
								var peopleValue = e.parents('.mds_mpeople').length == 0 ? null : getTagValue(e);
								window.location = Mds.GetUrl(window.location.href, { title: null, tag: tagValue, people: peopleValue, search: null, latest: null, filter: null, rating: null, top: null, aid: null, moid: null });
							},
							selectionAdded: function (e) {
								if (initComplete) {
									e.addClass('mds_wait_spinner');
									var newTag = e.contents().filter(function () {
										return this.nodeType == 3;
									}).text().trim();

									var metaTypeId = getMetaItem(e.closest('.mds_mRowDtl').data('id')).MTypeId;
									var contentItemMeta = { ContentItems: data.ActiveContentItems, MetaItem: { MTypeId: metaTypeId, Value: newTag } };

									Mds.DataService.saveMeta(contentItemMeta,
										function () { e.removeClass('mds_wait_spinner'); },
										function (gim) {
											if (data.MediaItem != null) {
												// When showing a single content object, update the data object's meta value so it is available during next/previous browsing
												var mi = Mds.findMetaItem(data.MediaItem.MetaItems, gim.MetaItem.MTypeId);
												if (mi != null) mi.Value += ', ' + gim.MetaItem.Value;
											}

											if (data.ActiveContentItems.length > 1) {
												// Append the count to the end of the tag value showing how many of the selected items have that tag (e.g. "Vacation (12)")
												var textNode = e.contents().filter(function () { return this.nodeType == 3; })[0];
												textNode.textContent = textNode.textContent + ' (' + data.ActiveContentItems.length + ')';
											}

										},
										function (response) { $.mdsShowMsg("Cannot Save Changes", response.responseText, { msgType: 'error', autoCloseDelay: 0 }); }
									);
								}
							},
							selectionRemoved: function (e) {
								e.animate({ opacity: .2 }, "slow", function () { e.addClass('mds_wait_spinner'); });

								// Get the tag value, stripping off trailing count if present. Ex: If tag="Animal (3)", change it to "Animal".
								var newTag = getTagValue(e);

								var metaTypeId = getMetaItem(e.closest('.mds_mRowDtl').data('id')).MTypeId;

								$.ajax(({
									type: "DELETE",
									url: window.Mds.AppRoot + '/services/api/contentitemmeta',
									data: JSON.stringify({ ContentItems: data.ActiveContentItems, MetaItem: { MTypeId: metaTypeId, Value: newTag } }),
									contentType: "application/json; charset=utf-8",
									complete: function () {
									},
									success: function (o) {
										e.remove();

										if (data.MediaItem != null) {
											// When showing a single content object, update the data object's meta value so it is available during next/previous browsing
											var mi = Mds.findMetaItem(data.MediaItem.MetaItems, metaTypeId);

											if (mi != null) {
												// Remove the tag from the comma separated list of tags
												mi.Value = $.grep(mi.Value.split(/\s*,\s*/), function (tag, i) {
													return tag != newTag;
												}).join(', ');
											}
										}
									},
									error: function (response) {
										$.mdsShowMsg("Cannot Save Changes", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
									}
								}));
							}
						});
						initComplete = true;
					});
				},
				getMetaItem = function (id) {
					// Find the meta item with the specified ID.
					return $.grep(data.ActiveMetaItems, function (mi) { return mi.Id === id; })[0];
				},
				onsubmit = function (settingsObj, el) {
					// Get the meta item from the instance and update the value with the data the user entered.
					var $el = $(el);
					var md = getMetaItem($el.parent().data('id'));

					// Get the user-entered value and assign the data to be sent to the server. It will later by assigned to
					// our local data entities in the onComplete event.
					var mValue = $('textarea:last, input:last, select:last', $el).val().trim();

					settingsObj.ajaxoptions.data = JSON.stringify({ ContentItems: data.ActiveContentItems, MetaItem: { MTypeId: md.MTypeId, Value: mValue } }); // '{"Id":1,"TypeId":1,"Desc":"My desc","Value":"My value"}';
				},
				showMeta = function (e, gItems) {
					// gItems is an array of ContentItem objects. It should be the same reference as data.ActiveContentItems
					$target.addClass('mds_wait'); // Show wait animated gif

					// Are we showing the meta for the current media item?
					var showMetaForMediaItem = gItems.length == 1 && gItems[0].ItemType != Mds.Constants.ItemType_Album && data.MediaItem && data.MediaItem.Id == gItems[0].Id;

					if (showMetaForMediaItem && data.MediaItem.MetaItems) {
						// We already have the meta items on the client, so grab them, bind and return (no need to get them from server).
						data.ActiveMetaItems = data.MediaItem.MetaItems;
						bindData();
						return;
					}

					if (gItems.length == 1) {
						// A single gallery item is selected.
						var i = gItems[0];

						var gt = (i.ItemType == Mds.Constants.ItemType_Album ? 'albumrests' : 'contentitems');
						var url = window.Mds.AppRoot + '/services/api/' + gt + '/' + i.Id + '/meta';

						$.ajax({
							url: url,
							dataType: 'json',
							success: function (metaItems) {
								data.ActiveMetaItems = metaItems;
								if (data.MediaItem != null)
									data.MediaItem.MetaItems = metaItems;
								bindData();
							},
							statusCode: {
								404: function () {
									// When we get a 404, just bind to the current album's metadata. We'll get here when the user is viewing a 
									// virtual album and clicks in the album area. Since the ID is int.MinValue, the server will send a 404.
									data.ActiveMetaItems = data.Album.MetaItems;
									bindData();
								}
							}
						});
					} else if (gItems.length < 1) {
						// No gallery items have been passed. It is not expected that we'll get here, but just in case, clear out the active
						// meta items and re-bind.
						data.ActiveMetaItems = [];
						bindData();
					} else {
						// More than one gallery item has been passed. Send the items to the server so we can get a merged list of meta.
						$.ajax(({
							type: "POST",
							url: window.Mds.AppRoot + '/services/api/contentitemmeta/contentitems',
							data: JSON.stringify(gItems),
							contentType: "application/json; charset=utf-8",
							dataType: "json",
							success: function (metaItems) {
								data.ActiveMetaItems = metaItems;
								if (data.MediaItem != null)
									data.MediaItem.MetaItems = metaItems;
								bindData();
							},
							error: function (response) {
								$.mdsShowMsg("Cannot Retrieve Data", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
							}
						}));
					}
				},
				onEditBegin = function (el, e) {
					//var pr = $(el).prev();
					// Save current padding on Desc td cell and then give it a top padding of .7em. We'll restore it when the edit is complete.
					// Without this step the desc is too high in relation to the input to its right.
					//pr.data('padding-top', pr.css('padding-top')).css('padding-top', '0.7em');
					setHover(el, false);

					// Return false when a hyperlink is clicked. Calling code will cancel the edit and allow the navigation to the link.
					return (e.target.tagName != 'A');
				},
				onGetData = function (curValue, obj, $el) {
					// Get the metadata value we want to use to populate the form element when editing. We get the metadata value from the 
					// object rather than the HTML DOM value since the DOM element might be HTML encoded; although we do use it as a last
					// resort if we can't find the data object (this should not typically happen, though).
					var md = getMetaItem($el.parent().data('id'));
					if (md && md.Value)
						return md.Value;
					else
						return curValue;
				},
				onEditEnd = function (el) {
				},
				onReset = function (el) {
					reset(el);
				},
				onComplete = function (el, xmldata) {
					// Reset control and return the new value. The calling function will assign the value to the original HTML element.
					var syncGalleryDataOnMetaUpdate = function () {
						// Update related properties on albums, content objects, and their metadata when meta items are changed.
						// 29 = MetadataItemName.Title; 41 = Caption; 112 = HtmlSource
						for (var i = 0; i < gim.ContentItems.length; i++) {
							// Update ContentItem, MediaItem, Album, and MetaItems
							var gNew = gim.ContentItems[i];
							var metaItems;

							// Update gallery item if present in our data
							var gCurrent = Mds.findContentItem(data, gNew.Id, gNew.ItemType);
							if (gCurrent != null) {
								switch (gim.MetaItem.MTypeId) {
									case 29:
										gCurrent.Title = gim.MetaItem.Value;
										break;
									case 41:
										gCurrent.Caption = gim.MetaItem.Value;
										break;
								}
							}

							// Update content object if present in our data
							var mCurrent = Mds.findMediaItem(data, gNew.Id, gNew.ItemType);
							if (mCurrent != null) {
								switch (gim.MetaItem.MTypeId) {
									case 29:
										mCurrent.Title = gim.MetaItem.Value;
										break;
									case 112:
										var view = Mds.getView(mCurrent, window.Mds.Constants.ViewSize_External);
										if (view)
											view.HtmlOutput = gim.MetaItem.Value;
										break;
								}
								metaItems = mCurrent.MetaItems;
							}

							// Update album if present in our data
							var aCurrent = Mds.findAlbum(data, gNew.Id, gNew.ItemType);
							if (aCurrent != null) {
								switch (gim.MetaItem.MTypeId) {
									case 29:
										aCurrent.Title = gim.MetaItem.Value;
										break;
									case 41:
										aCurrent.Caption = gim.MetaItem.Value;
										break;
								}
								metaItems = aCurrent.MetaItems;
							}

							// Update meta item if present in our data
							if (metaItems != null) {
								var mi = Mds.findMetaItem(metaItems, gim.MetaItem.MTypeId);
								if (mi != null) mi.Value = gim.MetaItem.Value;
							}
						}
					};

					reset(el);

					var m = getMetaItem($(el).parent().data('id')); // Get reference to metadata item
					var gim = $.parseJSON(xmldata); // Hydrate ContentItemMeta instance returned from server

					if (gim.ActionResult && gim.ActionResult.Status == "Error") {
						$.mdsShowMsg(gim.ActionResult.Title, gim.ActionResult.Message, { msgType: 'error', autoCloseDelay: 0 });
						return m.Value;
					}

					m.Value = gim.MetaItem.Value; // Update value (which may have been changed by the server (e.g. HTML removed))

					syncGalleryDataOnMetaUpdate();

					$('#' + data.Settings.ContentClientId).trigger('metaUpdate.' + data.Settings.ClientId, [gim]);
					$('#' + data.Settings.ThumbnailClientId).trigger('metaUpdate.' + data.Settings.ClientId, [gim]);

					// For the HTML content metadata item, we need to encode the return value so that the HTML is visible in the browser.
					if (m.MTypeId == 112) // MetadataItemName.HtmlSource (used only in external content objects)
						return window.Mds.htmlEncode(m.Value);
					else
						return m.Value;
				},
				reset = function (el) {
					setHover(el, true);
				},
				setHover = function (els, enable) {
					$.each(els, function (i, el) {
						var $el = $(el);
						if (enable) {
							$el.hover(function () {
								$el.removeClass('mds_editableContent').addClass('mds_editableContentHover');
							}, function () {
								$el.removeClass('mds_editableContentHover').addClass('mds_editableContent');
							});
						} else {
							$el.removeClass('mds_editableContentHover').addClass('mds_editableContent').unbind('mouseenter').unbind('mouseleave');
						}
					});
				};

		return {
			init: init
		};
	};

	//#endregion

	//#region mdsHeader plug-in

	$.fn.mdsHeader = function (tmplName, data) {
		var self = this;
		var $target = this;

		var initialize = function () {
			var renderHeader = function () {
				self.html($.render[tmplName](data)); // Render HTML template and add to page
			};

			var configLogin = function () {
				var dgLogin;
				if (data.User.IsAuthenticated) {
					$('.mds_logoffLink', $target).click(function (e) {
						e.preventDefault();
						e.stopPropagation();
						Mds.DataService.logOff(function () { Mds.ReloadPage(); });
					});
				}
				else {
					dgLogin = $('#' + data.Settings.ClientId + '_loginDlg');
					dgLogin.dgLoginWidth = 420;

					dgLogin.dialog({
						appendTo: '#' + data.Settings.ClientId,
						autoOpen: false,
						draggable: false,
						resizable: false,
						closeOnEscape: true,
						dialogClass: 'mds_loginDlgContainer',
						width: dgLogin.dgLoginWidth,
						minHeight: 0,
						show: 'fade',
						hide: 'fade',
						open: function () {
							setTimeout(function () { $('.mds_login_textbox:first', dgLogin).focus(); }, 50); // Delay needed for IE
						}
					});

					var disableCreateUserValidation = function (disabled) {
						// When true, the required attribute of form elements in the create user control are disabled. This allows the user to log in 
						// on the create user page.
						$('.mds_createuser input[data-required=true]').prop('required', !disabled);
					};

					$('.mds_login_trigger', $target).click(function (e) {
						if (dgLogin.dialog('isOpen') === true) {
							dgLogin.dialog('close');
							disableCreateUserValidation(false); // Restore required attribute
						}
						else {
							dgLogin.dialog('option', 'hide', null).dialog('close').dialog('option', 'hide', 'fade'); // Kill, then restore fade for quicker closing
							dgLogin.dialog('option', 'position', [$(window).width() - dgLogin.dgLoginWidth - 20, e.pageY + 30]);
							dgLogin.dialog('open');

							disableCreateUserValidation(true);
						}
						return false;
					});

					$('.mds_login_textbox', dgLogin).on('keydown', function (e) {
						if (e.keyCode == 13) { // Enter
							$('.mds_login_button', dgLogin).click();
							return false;
						} else {
							return true;
						}
					});

					$('.mds_login_button', dgLogin).button();

					// Close dialog when user clicks outside the login window
					$('body').bind('click', function (e) {
						if (dgLogin.dialog('isOpen') === true && !$(e.target).is('.ui-dialog, a') && !$(e.target).closest('.ui-dialog').length) {
							dgLogin.dialog('close');
							disableCreateUserValidation(false); // Restore required attribute
						}
					});
				}
			};

			var configSearch = function () {
				var dgSearch;
				if (data.Settings.ShowSearch) {
					dgSearch = $('#' + data.Settings.ClientId + '_searchDlg');
					dgSearch.dgSearchWidth = 420;

					dgSearch.dialog({
						appendTo: '#' + data.Settings.ClientId,
						autoOpen: false,
						draggable: false,
						resizable: false,
						closeOnEscape: true,
						dialogClass: 'mds_searchDlgContainer',
						width: dgSearch.dgSearchWidth,
						minHeight: 0,
						show: 'fade',
						hide: 'fade',
						open: function (t, d) {
							setTimeout(function () { $('.mds_searchbox', dgSearch).focus(); }, 50); // Delay needed for IE
						}
					});

					$('.mds_search_trigger', $target).click(function (e) {
						if (dgSearch.dialog('isOpen') === true)
							dgSearch.dialog('close');
						else {
							dgSearch.dialog('option', 'hide', null).dialog('close').dialog('option', 'hide', 'fade'); // Kill, then restore fade for quicker closing
							dgSearch.dialog('option', 'position', [$(window).width() - dgSearch.dgSearchWidth - 20, e.pageY + 30]);
							dgSearch.dialog('open');
						}
						return false;
					});

					// Start search when search button is clicked
					$('.mds_searchbutton', dgSearch).on('click', function (e) {
						var prepSearchTerms = function (st) {
							// Replace any spaces outside of quotes with +
							var result = '';
							var inQuote;
							$.each(st.split(''), function (idx, v) {
								if (v == '\"' || v == '\'')
									inQuote = !inQuote;

								result += (!inQuote && v == ' ' ? '+' : v);
							});
							return result;
						};

						e.preventDefault(); e.stopPropagation();

						var minSearchLen = 3;
						var searchTerm = $('.mds_searchbox', dgSearch).val();
						var sStatus = $('[name=' + data.Settings.ClientId + '_searchStatus]:checked').val();
						if (searchTerm.length >= minSearchLen || sStatus != 'All') {
						    var sType = $('[name=' + data.Settings.ClientId + '_searchType]:checked').val();

						    var parms = { title: null, tag: null, people: null, search: null, latest: null, filter: null, rating: null, top: null, aid: null, moid: null, approval: null };
						    if (searchTerm != '')
							    parms[sType] = prepSearchTerms(searchTerm);
							parms['approval'] = sStatus;
							window.location = Mds.GetUrl(window.location.href, parms);
						}
						else {
							var $msgEl = $('.mds_search_msg', dgSearch);
							$msgEl.css('visibility', 'visible');
							$('.mds_searchbox', dgSearch).one('keydown', function () { $msgEl.css('visibility', 'hidden'); }).focus();
						}
					}).button();

					$('.mds_searchbox, .mds_search_type_container input', dgSearch).on('keydown', function (e) {
						if (e.keyCode == 13) { // Enter
							$('.mds_searchbutton', dgSearch).click();
							return false;
						} else
							return true;
					});

					// Close dialog when user clicks outside the search window
					$('body').bind('click', function (e) {
						if (dgSearch.dialog('isOpen') === true && !$(e.target).is('.ui-dialog, a') && !$(e.target).closest('.ui-dialog').length) {
							dgSearch.dialog('close');
						}
					});
				}
			};

			var configsmartmenus = function () {
			    $('#main-menu').smartmenus({
			        mainMenuSubOffsetX: -1,
			        mainMenuSubOffsetY: 4,
			        subMenusSubOffsetX: 6,
			        subMenusSubOffsetY: -6
			    });
			};

			renderHeader();
			configLogin();
			configSearch();
			configsmartmenus();
		};

		initialize();

		$(document.documentElement).trigger('mdsHeaderLoaded.' + data.Settings.ClientId);

		return this;
	};

	//#endregion

	//#region mdsTooltip plug-in

	$.fn.mdsTooltip = function (options) {
		var self = this;
		return this.each(function () {
			if (!$.data(this, 'plugin_mdsTooltip')) {
				var tt = MdsTooltip();
				tt.init(self, options);
				$.data(this, 'plugin_mdsTooltip', tt);
			}
		});
	};

	$.fn.mdsTooltip.defaults = {
		title: '',
		content: ''
	};

	// Define object to handle tooltip. Uses Revealing Module Pattern from here:
	// http://weblogs.asp.net/dwahlin/archive/2011/09/05/creating-multiple-javascript-objects-when-using-the-revealing-module-pattern.aspx
	window.MdsTooltip = function () {
		var $target,
				settings,
				$dgTrigger,
				$dgTooltip,
				init = function (target, options) {
					$target = target;
					settings = $.extend({}, $.fn.mdsTooltip.defaults, options);

					initVars();
					configureDialog();
					configureTooltip();
				},

				initVars = function () {
					$dgTrigger = $("<button class='mds_tt_tgr'></button>");
					$dgTooltip = $("<div class='mds_tt_dlg'><div class='mds_tt_dlg_title'>{0}</div><div class='mds_tt_dlg_bdy'>{1}</div></div>".format(settings.title, settings.content));
				},

				configureDialog = function () {

					// Configure the tooltip dialog
					$dgTooltip.dialog({
						appendTo: $('.mds_ns').first(),
						autoOpen: false,
						draggable: false,
						resizable: false,
						closeOnEscape: true,
						dialogClass: 'mds_tt_dlg_container',
						width: 420,
						minHeight: 0,
						show: 'fade',
						hide: 'fade',
						position: { my: "left top", at: "left bottom", of: $dgTrigger },
						open: function (e, ui) {
							$(document).on("click", function (e1) {
								if ($(e1.target).parents('.mds_tt_dlg_container').length == 0) {
									$dgTooltip.dialog('close');
									$(this).unbind(e1);
								}
							});
						}
					});
				},

				configureTooltip = function () {
					$dgTrigger.insertAfter($target)
						.button({
							text: false,
							icons: { primary: "mds-ui-icon mds-ui-icon-help" }
						})
						.click(function (e) {
							if ($dgTooltip.dialog('isOpen') === true)
								$dgTooltip.dialog('close');
							else {
								$dgTooltip.dialog('open');
							}
							return false;
						});
				};

		return {
			init: init
		};
	};

	//#endregion

	//#region FullScreenSlideShow class

	window.MdsFullScreenSlideShow = function (data, options) {
		var defaults = {
			on_exit: function () { }
		};

		this.data = data;
		this.settings = $.extend({}, defaults, options);
	};

	MdsFullScreenSlideShow.prototype.startSlideShow = function () {
		var self = this;
		var items = this.data.Album.MediaItems || this.data.Album.ContentItems;

		var urls = $.map(items, function (mo) {
			if (mo.ItemType == Mds.Constants.ItemType_Image)
				return { id: mo.Id, thumb: Mds.getView(mo, Mds.Constants.ViewSize_Thumbnail).Url, title: mo.Title, image: Mds.getView(mo, Mds.Constants.ViewSize_Optimized).Url };
			else
				return null;
		});

		if (urls.length == 0) {
			$.mdsShowMsg(this.data.Resource.MoNoSsHdr, this.data.Resource.MoNoSsBdy, { msgType: 'info' });
			return false;
		};

		var ssTmpl = '\
<div class="ssControlsContainer"> \
		<!--Thumbnail Navigation--> \
		<div id="prevthumb"></div> \
		<div id="nextthumb"></div> \
\
		<!--Arrow Navigation--> \
		<a id="prevslide" class="load-item"></a> \
		<a id="nextslide" class="load-item"></a> \
\
		<div id="thumb-tray" class="load-item"> \
			<div id="thumb-back"></div> \
			<div id="thumb-forward"></div> \
		</div> \
\
		<!--Time Bar--> \
		<div id="progress-back" class="load-item"> \
			<div id="progress-bar"></div> \
		</div> \
\
		<!--Control Bar--> \
		<div id="controls-wrapper" class="load-item"> \
			<div id="controls"> \
\
				<a id="play-button"> \
					<img id="pauseplay" src="{0}/pause.png" /></a> \
\
				<a id="stop-button"> \
					<img src="{0}/stop.png" /></a> \
\
				<!--Slide counter--> \
				<div id="slidecounter"> \
					<span class="slidenumber"></span> / <span class="totalslides"></span> \
				</div> \
\
				<!--Slide captions displayed here--> \
				<div id="slidecaption"></div> \
\
				<!--Thumb Tray button--> \
				<a id="tray-button"> \
					<img id="tray-arrow" src="{0}/button-tray-up.png" /></a> \
\
				<!--Navigation--> \
				<ul id="slide-list"></ul> \
\
			</div> \
		</div> \
</div> \
				'.format(this.data.App.SkinPath + '/images/supersized');

		var getTransition = function (transitionType) {
			switch (transitionType) {
				case 'fade': return 1;
				case 'slide': return 3;
				default: return 0;
			}
		};

		var getStartSlide = function () {
			// Get the current content object and find the index of the matching one in the urls var.
			var startSlide = 1;

			if (self.data.MediaItem == null)
				return startSlide;

			$.each(urls, function (idx, ssItem) {
				if (self.data.MediaItem.Id == ssItem.id) {
					startSlide = idx + 1;
					return false; // false breaks out of $.each
				}
				return true;
			});

			return startSlide;
		};

		// Fire up the full screen slide show.
		$.supersized({

			// Functionality
			image_path: this.data.App.SkinPath + '/images/supersized/',
			slideshow: 1,			// Slideshow on/off
			autoplay: 1,			// Slideshow starts playing automatically
			auto_exit: 1,      // Exit the slideshow when the last slide is finished
			start_slide: getStartSlide(),			// Start slide (0 is random)
			loop: 0,			// Enables moving between the last and first slide.
			random: 0,			// Randomize slide order (Ignores start slide)
			slide_interval: this.data.Settings.SlideShowIntervalMs,		// Length between transitions
			transition: getTransition(this.data.Settings.TransitionType), 			// 0-None, 1-Fade, 2-Slide Top, 3-Slide Right, 4-Slide Bottom, 5-Slide Left, 6-Carousel Right, 7-Carousel Left
			transition_speed: 500,		// Speed of transition
			new_window: 1,			// Image links open in new window/tab
			pause_hover: 0,			// Pause slideshow on hover
			keyboard_nav: 1,			// Keyboard navigation on/off
			performance: 1,			// 0-Normal, 1-Hybrid speed/quality, 2-Optimizes image quality, 3-Optimizes transition speed // (Only works for Firefox/IE, not Webkit)
			image_protect: 0,			// Disables image dragging and right click with Javascript

			// Size & Position						   
			min_width: 0,			// Min width allowed (in pixels)
			min_height: 0,			// Min height allowed (in pixels)
			vertical_center: 1,			// Vertically center background
			horizontal_center: 1,			// Horizontally center background
			fit_always: 1,			// Image will never exceed browser width or height (Ignores min. dimensions)
			fit_portrait: 1,			// Portrait images will not exceed browser height
			fit_landscape: 1,			// Landscape images will not exceed browser width

			// Components							
			slide_links: 'blank',	// Individual links for each slide (Options: false, 'num', 'name', 'blank')
			thumb_links: 1,			// Individual thumb links for each slide
			thumbnail_navigation: 0,			// Thumbnail navigation
			slides: urls,

			// Theme Options			   
			progress_bar: 0,			// Timer for each slide							
			mouse_scrub: 0,
			html_template: ssTmpl, // The HTML for the controls
			on_destroy: function (currentId) {
				self.settings.on_exit.apply(null, [currentId]);
			}
		});

		// Exit slideshow when stop button is clicked.
		$('#stop-button').on('click', function () {
			api.destroy();
		});

		return true;
	};

	//#endregion

	//#region Timer class
	window.MdsTimer = function (callback, milliseconds, context) {
		this.isRunning = false;
		this.milliseconds = milliseconds;
		this.callback = callback;
		this.context = context;
		if (!this.context) this.context = this;
		this.handle = null;
	};

	MdsTimer.prototype.start = function () {
		var timer = this;
		var context = this.context;
		var invokeCallback = function () {
			timer.callback.apply(context);
		};
		this.handle = setInterval(invokeCallback, this.milliseconds);
		this.isRunning = true;
	};

	MdsTimer.prototype.stop = function () {
		clearInterval(this.handle);
		this.isRunning = false;
	};
	//#endregion End Timer class

	//#region mdsShowMsg utility function

	$.mdsShowMsg = function (title, message, options) {

		var defaults = {
			msgType: 'success', // Any property of the MessageStyle enumeration: success, info, warning, error
			autoCloseDelay: 4000, // The # of milliseconds to wait until a message auto-closes. Use 0 to never auto-close.
			width: 500 // The width of the dialog window.
		};

		var settings = $.extend({}, defaults, options);

		/*$('.mds_msg').remove(); // Remove any previous message that may be visible

		var $dgHtml = $('<div>');

		var cssClass = "mds_msg";
		if (message) {
			$dgHtml.append(message);
			cssClass += " mds_msgHasContent";
		} else {
			cssClass += " mds_msgNoContent";
		}

		cssClass += " mds_msg_" + settings.msgType;

		$dgHtml.dialog({
			appendTo: $('.mds_ns').first(),
			position: { my: 'top', at: 'top' },
			title: title,
			width: settings.width,
			height: 'auto',
			resizable: false,
			dialogClass: cssClass,
			show: 'fade',
			hide: 'fade'
		});

		if (settings.autoCloseDelay > 0) {
			// Auto-close for success messages
			setTimeout(function () {
				if ($dgHtml.is(":ui-dialog")) {
					$dgHtml.dialog('destroy');
				}
			}, settings.autoCloseDelay);
		}*/
		var opt =
        {
            title : title,
            text : message,
            textTrusted:true,
            width: settings.width + "px",
            type : settings.msgType=='warning' ? "notice" : settings.msgType,
            //hide : settings.autoCloseDelay > 0 ? true : false,
            hide : true,
            delay : settings.autoCloseDelay > 0 ? settings.autoCloseDelay : window.Mds.Constants.IntMaxValue,
            buttons: { closer: true }
        };
		/*if (!(settings.autoCloseDelay > 0)){
			opt = $.extend({}, opt, {buttons: {closer: true}});
		}*/

        new PNotify.alert(opt);
	};

	//#endregion

	//#region ServerTask

	window.Mds.ServerTask = function (options) {
		/// <summary>
		///   An object for executing long running tasks on the server and periodically checking its status. The following
		///   assumptions are made: (1) The URL used to invoke the task invokes it on a background thread and returns quickly.
		///   (2) A callback function userDefinedProgressCallback is specified that monitors the returned progress data. This
		///   function is responsible for detecting when the task is complete and subsequently calling resetTask, which cancels the
		///   polling mechanism.
		/// </summary>
		/// <param name="options">The options that contain configuration data.</param>
		var createTaskId = function () {
			/// <summary>Internal function to generate a unique task ID.</summary>
			/// <returns type="String">Returns a pseudo-GUID.</returns>
			return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
				var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
				return v.toString(16);
			});
		};

		var defaults = {
			taskId: createTaskId(), // The current task ID
			timerId: 0, // The timer ID used to identify the polling mechanism
			taskBeginData: null, // An object to include when starting the task. It is serialized as JSON and sent with the taskBeginUrl.
			taskBeginUrl: null, // The URL to use to initiate the long running task. It is expected that the task will be started on a background thread and that this URL returns quickly.
			taskProgressUrl: null, // The URL to invoke to read status
			taskAbortUrl: null, // The URL to invoke to abort the operation 
			interval: 1000, // The current interval for progress refresh (in ms)
			userDefinedProgressCallback: null, // The user-defined callback that refreshes the UI 
			taskAbortedCallback: null // Get the user-defined callback that runs after aborting the call 
		};

		var taskSettings = $.extend({}, defaults, options);
		var self = this;

		var abortTask = function () {
			/// <summary>
			///   Send a signal to the server to stop the action. When the polling mechanism detects that the server task has
			///   been cancelled, it will resets the task (which cancels the timer).
			/// </summary>
			if (taskSettings.taskAbortUrl != null && taskSettings.taskAbortUrl != '') {
				$.ajax({
					url: taskSettings.taskAbortUrl,
					async: false,
					cache: false,
					headers: { 'X-ServerTask-TaskId': taskSettings.taskId }
				});
			}
		};

		var internalProgressCallback = function () {
			/// <summary>An internal function for invoking the progress URL on the server.</summary>
			$.ajax({
				url: taskSettings.taskProgressUrl,
				cache: false,
				headers: { 'X-ServerTask-TaskId': taskSettings.taskId },
				success: function (status) {
					// Set the timer to call this method again after the specified interval.
					taskSettings.timerId = window.setTimeout(internalProgressCallback, taskSettings.interval);

					if (taskSettings.userDefinedProgressCallback != null)
						taskSettings.userDefinedProgressCallback(status, self);
				}
			});
		};

		var startTask = function () {
			/// <summary>Invoke the long tunning task and begin the periodic polling to check its status.</summary>
			_xhr = $.ajax({
				url: taskSettings.taskBeginUrl,
				type: "POST",
				data: JSON.stringify(taskSettings.taskBeginData),
				contentType: "application/json; charset=utf-8",
				cache: false,
				headers: { 'X-ServerTask-TaskId': taskSettings.taskId },
				complete: function () {
					if (_xhr.status != 0) return;
					if (taskSettings.taskAbortedCallback != null)
						taskSettings.taskAbortedCallback(self);
					end();
				},
				success: function (data) {
					// Start the progress callback (if any)
					if (taskSettings.userDefinedProgressCallback != null && taskSettings.taskProgressUrl != null) {
						taskSettings.timerId = window.setTimeout(internalProgressCallback, taskSettings.interval);
					}
				},
				error: function (response) {
					$.mdsShowMsg("Error starting task", response.responseText, { msgType: 'error', autoCloseDelay: 0 });
				}
			});
		};

		var resetTask = function () {
			/// <summary>
			///   Clears the existing timer function and resets the internal state of the object. Note that
			///   this function does not send an abort signal to the server.
			/// </summary>
			taskSettings.taskId = 0;
			window.clearTimeout(taskSettings.timerId);
		};

		this.startTask = startTask;
		this.abortTask = abortTask;
		this.resetTask = resetTask;
		this.taskSettings = taskSettings;
	};

	//#endregion

})(jQuery, window, document);

//#endregion