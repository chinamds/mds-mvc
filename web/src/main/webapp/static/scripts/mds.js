/* TO MODIFY: Make changes to this file and test locally under the Debug compilation configuration. When 
finished, run this text through a javascript minifier and copy the output to lib.min.js. 
There is an online minifier at http://www.refresh-sf.com/yui/. */

// Contains javascript required for read-only browsing of a gallery
//#region MDSPlus System javascript
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

	window.Mds.i18n = {};
	window.Mds.AppRoot = ""; // Set to Utils.AppRoot in GalleryPage.AddGlobalStartupScript (e.g. '/dev/gallery')
	window.Mds.CompanyResourcesRoot = ""; // Set to Utils.GalleryResourcesPath in GalleryPage.AddGlobalStartupScript (e.g. '/dev/gallery/ds')
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
	/*Array.prototype.mdsRemove = function (from, to) {
		var rest = this.slice((to || from) + 1 || this.length);
		this.length = from < 0 ? this.length + from : from;
		return this.push.apply(this, rest);
		
		return this.slice(0, from).concat(this.slice(to+1,this.length));
	};*/
	
	//An extension to date that converts date to a stringi n the specified format,
	//month(M), days (d), hours (h), minutes (m), seconds (s), quarter (q) can be 1-2 placeholders,
	//year (y) can be 1-4 placeholders, milliseconds (s) can only be 1 placeholders (1-3 digits)
	//Example:
	// (new Date()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
	// (new Date()).format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
	//author: meizz
	Date.prototype.format = function(fmt){  
	  var o = { 
	    "M+" : this.getMonth()+1,                 //Month
	    "d+" : this.getDate(),                    //Day
	    "h+" : this.getHours(),                   //Hour 
	    "m+" : this.getMinutes(),                 //Minute
	    "s+" : this.getSeconds(),                 //Second
	    "q+" : Math.floor((this.getMonth()+3)/3), //season
	    "S"  : this.getMilliseconds()             //Millisecond
	  };
	  
	  if(/(y+)/.test(fmt)) 
		  fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	  
	  for(var k in o) {
	    if(new RegExp("("+ k +")").test(fmt))
	    	fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
	  }
	  
	  return fmt; 
	}

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
	
	window.Mds.isImage = function(filename) {
        return /gif|jpe?g|png|bmp$/i.test(filename);
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
	
	window.Mds.getString = function (msgKey) {
		if (Mds.isNullOrEmpty(window.Mds.i18n)){
			/**Initialize resources for i18n*/
			var url = Mds.AppRoot + "/i18n/localizedResources/jsResource";
	         $.ajax({
	             url: url,
	             async: false,
	             cache: false,
	             dataType : "json",
	             success: function (data) {
	            	 window.Mds.i18n = data;
	             },
	             error: function (re) {
	             }
	         });
		}
		
		return window.Mds.i18n[msgKey];
	};

	window.Mds.Init = function () {
		//Mds.initDatetimePicker();
		// Gets reference to current URL with hash tag removed
		window.Mds.href = window.location.href.replace(/#\d+/, '');
	};

	$(document).ready(Mds.Init);

	window.Mds.ReloadPage = function () {
		window.location = Mds.RemoveQSParm(window.location.href, 'msg');
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
		
	window.Mds.initAutocomplete = function(config) {
        var defaultConfig = {
            minLength : 1,
            enterSearch : false,
            focus: function( event, ui ) {
                $(config.input).val( ui.item.label );
                return false;
            },
            renderItem : function( ul, item ) {
                return $( "<li>" )
                    .data( "ui-autocomplete-item", item )
                    .append( "<a>" + item.label + "</a>" )
                    .appendTo( ul );
            }
        };

        config = $.extend(true, defaultConfig, config);

        $(config.input)
            .on( "keydown", function( event ) {
                //enter to search
                if(config.enterSearch && event.keyCode === $.ui.keyCode.ENTER) {
                    config.select(event, {item:{value:$(this).val()}});
                }
            })
            .autocomplete({
                source : config.source,
                minLength : config.minLength,
                focus : config.focus,
                select : config.select
            }).data( "ui-autocomplete" )._renderItem = config.renderItem;
    };


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
	
	//#region initDatetimePicker
	window.Mds.initDatetimePicker = function() {
        //Initialize datetime picker
        $('.date:not(.custom)').each(function() {
            var $date = $(this);

            if($date.attr("initialized") == "true") {
                return;
            }

            /*var pickDate = $(this).find("[data-format]").data("format").toLowerCase().indexOf("yyyy-mm-dd") != -1;
            var pickTime = $(this).find("[data-format]").data("format").toLowerCase().indexOf("hh:mm:ss") != -1;*/
            var dateformat = $(this).find("[data-format]").data("format");
            $date.datetimepicker({
            	format: dateformat
            });/*.on('dp.change', function(ev) {
                    if(pickTime == false) {
                        $(this).data("datetimepicker").hide();
                    }
                });*/
            $date.find(":input").click(function() {$date.find(".fa-calendar,.fa-clock,.fa-calendar-alt").click();});
            $date.attr("initialized", true);
        });
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