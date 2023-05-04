<script type="text/javascript">

	(function ($) {
		var fileProcessedCount = 0;
		var isError = false; // Set to true if any of the uploads failed (or a file is skipped)
		var isAsync = false; // Set to true if the uploads are being processed on the server asyncronously (will be true for ZIP files)
	  var plUploadStarted = false;
			
		jQuery(document).ready(function () {
			configureEventHandlers();
			configureAddFile();
			//configTooltips();

		});
	
		var configureEventHandlers = function() {
			$(".mds_btnOkTop, .mds_btnOkBottom").click(function(e) {
				$(".mds_spinner").show();
				$(".mds_spinner_msg").text('<fmt:message key="task.addObjects.Uploading_Text"/>').show();

				uploadFiles(e);
				e.preventDefault();
			});
		
			$('.mds_optionsHdr').click(function () {
				$(this).toggleClass("mds_expanded mds_collapsed");
				$('.mds_optionsDtl').slideToggle('fast');
			});
		};

		var uploadFiles = function() {
			// Begin the upload of files to the server.
			plUploadStarted = true;
			var discardOriginal = $("#chkDiscardOriginal").prop("checked");
			var uploader = $("#uploader").bsupload('getUploader');

			if (discardOriginal) {
				uploader.settings.resize = {
					width : '${gallerySettings.maxOptimizedLength}', 
					height : '${gallerySettings.maxOptimizedLength}',
					quality : '${gallerySettings.optimizedImageJpegQuality}'
				};
			}

			// Files in queue upload them first
			if (uploader.files.length > 0) {
				uploader.start();
			} else {
				$(".mds_spinner, .mds_spinner_msg").hide();
				$.mdsShowMsg('Upload cancelled', '<fmt:message key="task.addObjects.Local_Content_Tab_Dtl"/>', { msgType: 'warning' });
			}
		};

        var configureAddFile = function() {
			$('#pnlOptions').hide();
			$('#pnlOptionsHdr').click(function () {
				$('#pnlOptions').slideToggle('fast');
			});

			$("#uploader").bsupload({
			     runtimes: 'html5,silverlight,flash,html4',
				 url: '${ctx}/cm/contentobjects/pluploadUpload?${_csrf.parameterName}=${_csrf.token}', //?aid=' + $("#albumId").val(),
				 flash_swf_url: '${ctx}/static/3rdparty/plupload/Moxie.swf',
			     silverlight_xap_url: '${ctx}/static/3rdparty/plupload/Moxie.xap',
			     filters: $.parseJSON('${fileFilters}'), //$('#fileFilters').val()
				 unique_names: true,
				 max_file_size : '${gallerySettings.maxUploadSize} KB',
			     chunk_size: '2mb',
			     buttons: {
                    browse: true,
                    start: false,
                    stop: false
                 },
			     views: {
    				list: true,
    				thumbs: true,
    				active: 'thumbs'
			     },
			     multipart: true,
                 multiple_queues: true,
                 // Enable ability to drag'n'drop files onto the widget (currently only HTML5 supports that)
                 dragdrop: true,
                
                 sortable: false
			}).on("beforeupload",function(event, args) {
                console.info("before upload");
            }).on("complete",function(event, args) {
                console.info("complete");
                onComplete(event, args);
            }).on("error",function(event, args) {
                console.info("error");
                onError(event, args);
            }).on("uploaded",function(event, args) {
                console.info("uploaded");
                onFileUpload(event, args);
            });
		};
		
		var redirectToAlbum = function(msgId) {
			purgeCache();
			var albumUrl = '${redirectToAlbum}'; //
			if (msgId && msgId > 0)
				window.location = albumUrl + '&msg=' + msgId;
			else 
				window.location = albumUrl;
		};
	
		var onComplete = function(event, args) {
			// Fail-safe: Normally, we redirect after the web service finishes processing the file (onFileProcessed),
			// but just in case it doesn't return or the file counter gets messed up, set a timer to redirect to 
			// the album a few seconds after the last file upload is complete, thus guaranteeing the page will never
			// get stuck with a "Processing. Please wait..." message.
			// NOTE: args.up.state shows a STOPPED value for normal completions as well as when upload.ashx returns an error (like when validation fails),
			// Since we should catch most validation issues on the client before the upload starts (eg. max file size, disabled file extensions),
			// we'll set the timeout whenever we get here. If we discover the timeout occurs in normal workflows, we may need to revisit this.
			if (args.up.state == plupload.STOPPED) {
				window.setTimeout(redirectToAlbum, 20000);
			}
		};
	
		var onError = function(up, args) {
			// args.error.code can be any of these values:
		  //STOPPED:1,STARTED:2,QUEUED:1,UPLOADING:2,FAILED:4,DONE:5,GENERIC_ERROR:-100,HTTP_ERROR:-200,IO_ERROR:-300,SECURITY_ERROR:-400,INIT_ERROR:-500,FILE_SIZE_ERROR:-600,FILE_EXTENSION_ERROR:-601,IMAGE_FORMAT_ERROR:-700,IMAGE_MEMORY_ERROR:-701,IMAGE_DIMENSIONS_ERROR:-702
			isError = true;
			$(".mds_spinner, .mds_spinner_msg").hide();

			$.mdsShowMsg("Cannot Upload File", getErrMsg(args), { msgType: 'error', autoCloseDelay: 0 });

			if (plUploadStarted)
			  onFileComplete();
		};
		
		var onFileUpload = function(event, args) {
			// File has been transferred to the server; now call web service to copy file to destination album and create content object record.
			var getData = function() {
				var settings = { };
			
				settings.FileName = args.file.name;
				settings.FileNameOnServer = args.file.target_name;
				settings.AlbumId = $("#albumId").val();
				settings.DiscardOriginalFile = $("#chkDiscardOriginal").prop("checked");
				settings.ExtractZipFile = !$("#chkDoNotExtractZipFile").prop("checked");

				return settings;
			};
			
			// Call web service to move uploaded file to destination and add to gallery.
			$.ajax(({
				type: "POST",
				url: '${ctx}/services/api/contentitems/createfromfile',
				data: JSON.stringify(getData()),
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success: function(response) { onFileProcessed(args.file, response); },
				error: function(response) { isError = true;onFileComplete(); }
			}));
		};

		var onFileProcessed = function(file, actionResults) {
			// Invoked after the web service has successfully processed an uploaded file. actionResults is an array 
			// of ActionResult objects.
			for (var i = 0; i < actionResults.length; i++) {
				if (actionResults[i].Status == "Error") {
					isError = true;
				}
				else if (actionResults[i].Status == "Async") {
					isAsync = true;
				}
			}
		
			fileProcessedCount++;
			onFileComplete();
		};

		var onFileComplete = function(event, args) {
			// Invoked when a plUpload error occurs, file has either failed to upload/be processed in some way or has successfully been uploaded and processed
			var uploader = $('#uploader').bsupload('getUploader');
			if (fileProcessedCount + uploader.total.failed >= uploader.files.length) {
			  if (isAsync)
					redirectToAlbum(<%=MessageType.ObjectsBeingProcessedAsyncronously.value()%>);
				else if (isError)
				  redirectToAlbum(<%=MessageType.ObjectsSkippedDuringUpload.value()%>);
			  else
				  redirectToAlbum();
			}
		};

		var purgeCache = function() {
			$.ajax({
				url: '${ctx}/services/api/task/${album.id}/purgecache',
				cache: false,
			});
		};
		
		var getErrMsg = function(args) {
			if (args.error.code == plupload.FILE_SIZE_ERROR) {
				return 'File size must be less than ' + args.up.settings.max_file_size + '. The file \'' + args.error.file.name + '\' is ' + Globalize.format(args.error.file.size / 1024 / 1024, 'n1') + ' MB.';
			}
			
			if (args.error.code == plupload.FILE_EXTENSION_ERROR) {
				return 'The file \'' + args.error.file.name + '\' has an extension not currently allowed by the gallery configuration. If you are an administrator, you can enable this extension in the site administration.';
			}

			var msg = '<p>' + args.error.message + ' Code ' + args.error.code + '.</p>';

			if (args.error.file != null) {
				msg += '<p>File: ' + args.error.file.name + '.</p>';
			}

			if (args.error.response != null) {
				// Unfortunately, the response is invalid json and can't be parsed into an object, so we just show the raw text
				msg += '<p>HTTP response data: ' + args.error.response + '</p>';
			}

			return msg;
		};
	  
	})(jQuery);	
</script>