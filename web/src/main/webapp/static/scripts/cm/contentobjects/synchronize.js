<script type="text/javascript">
	(function ($) {
	  var serverTask;

	  $(document).ready(function () {
	    bindEventHandlers();
	    //configTooltips();
	  });

	  var bindEventHandlers = function() {
	    $(".mds_btnOkTop, .mds_btnOkBottom").click(function() {
		    beginSync();
		    return false;
		  });
		};

    var beginSync = function() {
      var updateUi = function(status) {
        var ctr = $("#${galleryView.mdsClientId}_mds_sync_sts");

			  ctr.show();
			  $(".mds_sync_sts_cursts_msg", ctr).text(status.StatusForUI);
			  $(".mds_sync_sts_rate_msg", ctr).text(status.SyncRate);
			  $(".mds_sync_sts_curfile_msg", ctr).text(status.CurrentFile);
			  $(".mds_sync_sts_spinner", ctr).show();
			  $(".mds_sync_pb", ctr).width(status.PercentComplete + '%');

			  var showFinalMsg = function() {
			    if (status.Status == "Complete") {
			      var tmplData = $("#tmplSyncSkippedFiles").render(status);
			      if (status.SkippedFiles.length == 0) {
			        $.mdsShowMsg('Synchronization Complete', tmplData, { autoCloseDelay: 0 });
			      } else { // Some files were skipped, so make the dialog a bit wider.
			        $.mdsShowMsg('Synchronization Complete', tmplData, { autoCloseDelay: 0, width: 700 });
			      }
			    } else if (status.Status == "Error") {
			      $.mdsShowMsg('Synchronization error', 'An error occurred while synchronizing the gallery. Additional details may be found in the gallery\'s event log.', { msgType: 'error', autoCloseDelay: 0 });
			    } else if (status.Status == "AnotherSynchronizationInProgress") {
			      $.mdsShowMsg('Cannot start synchronization', 'Another synchronization is in progress. To forcefully cancel it, restart the application pool.', { msgType: 'info', autoCloseDelay: 0 });
			    }
			  };

			  showFinalMsg();
			};

			var userDefinedProgressCallback = function(status, serverTaskInstance) {
			  // We've received a status report from the server. Update the screen. If the status indicates the action is complete,
			  // cancel the server polling.
			  updateUi(status);

			  if (status.Status == "Complete" || status.Status == "Error" || status.Status == "AnotherSynchronizationInProgress" || status.Status == "Aborted") {
				   serverTaskInstance.resetTask();
				   $(".mds_sync_sts_cursts_abort_ctr, .mds_sync_sts_spinner", $("#${galleryView.mdsClientId}_mds_sync_sts")).hide();
		      }
			};

		  var configAbortFunctionality = function() {
		    $(".mds_sync_sts_cursts_abort_ctr", $("#${galleryView.mdsClientId}_mds_sync_sts")).one("click", function(e) {
			    // Send signal to server to cancel when user clicks cancel button.
			    if (serverTask != null)
			      serverTask.abortTask();

			    $(this)
						.text('<fmt:message key="task.synch.Progress_SynchCanceling"/>')
						.prop('disabled', true);

				}).text('<fmt:message key="task.synch.Cancel_Button_Text"/>')
					.prop('disabled', false).show();
			  
					$(".mds_btnCancelTop, .mds_btnCancelBottom").click(function() {
						if (serverTask != null)
							serverTask.abortTask();
					});
			};

		  var albumId = $("#albumId").val();
		  var galleryId = $("#gid").val();;

		  var serverTaskOptions = {
		    userDefinedProgressCallback: userDefinedProgressCallback,
		    taskAbortedCallback: null,
		    taskBeginData: {
		      AlbumIdToSynchronize: albumId,
		      IsRecursive: $('#chkIncludeChildAlbums').prop('checked'),
		      RebuildThumbnails: $('#chkOverwriteThumbnails').prop('checked'),
		      RebuildOptimized: $('#chkOverwriteCompressed').prop('checked'),
		      SyncInitiator: 1 // SyncInitiator.LoggedOnGalleryUser
		    },
		    taskBeginUrl: window.Mds.AppRoot + '/services/api/task/startsync',
		    taskProgressUrl: window.Mds.AppRoot + '/services/api/task/' + galleryId + '/statussync',
		    taskAbortUrl: window.Mds.AppRoot + '/services/api/task/' + galleryId + '/abortsync',
		  };

		  serverTask = new window.Mds.ServerTask(serverTaskOptions);
		  serverTask.startTask();

		  updateUi({ Status: 'Starting', StatusForUI: '<fmt:message key="task.synch.Progress_SynchStarting"/>', SyncRate: '', CurrentFile: '', PercentComplete: 0 });
	      configAbortFunctionality();
	  };

	  /*var configTooltips = function() {
		  $('#lblIncludeChildAlbums').mdsTooltip({
		      title: '<fmt:message key="task.synch.IncludeChildAlbums_Hlp_Hdr"/>',
		      content: '<fmt:message key="task.synch.IncludeChildAlbums_Hlp_Bdy"/>'
		    });
	
		    $('#lblOverwriteThumbnails').mdsTooltip({
		      title: '<fmt:message key="task.synch.OverwriteThumbnails_Hlp_Hdr"/>',
		      content: '<fmt:message key="task.synch.OverwriteThumbnails_Hlp_Bdy"/>'
		    });
	
		    $('#lblOverwriteCompressed').mdsTooltip({
		      title: '<fmt:message key="task.synch.OverwriteCompressed_Hlp_Hdr"/>',
		      content: '<fmt:message key="task.synch.OverwriteCompressed_Hlp_Bdy"/>'
		    });
	
		    $('#lblInstructions').mdsTooltip({
		      title: '<fmt:message key="task.synch.Options_Hlp_Hdr"/>',
		      content: '<fmt:message key="task.synch.Options_Hlp_Bdy"/>'
		    });
	  };*/
	  
    })(jQuery);
 </script>
