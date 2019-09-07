<script type="text/javascript">
	(function ($) {
		jQuery(document).ready(function () {
			//$("#${galleryView.mdsClientId} .thmb").equalSize(); // Make all thumbnail tags the same width & height
			//$('#${galleryView.mdsClientId} .mds_go_t').css('width', '').css('display', ''); // Remove the width that was initially set, allowing title to take the full width of thumbnail
						  
			var aid = $('#albumId').val();
			var thumbOptions = {
				clientId: '${galleryView.mdsClientId}',
				allowMultiSelect: true,
				thumbPickerType: 'all',
				thumbShowType: 'contentobject',
				checkedContentItemIdsHiddenFieldClientId: 'hdnCheckedContentObjectIds',
				requiredSecurityPermissions: '1',
				thumbPickerUrl: '${ctx}/services/api/albumrests/gettreepicker',
				albumIdsToSelect: [aid],
				controlPlugin:'radio'
			};
			
			$('#${galleryView.mdsClientId}_ThumbView').mdsThumbPicker(null, thumbOptions);
			
			// Clear all radio buttons when one is clicked and re-select the one that was clicked. This is a workaround
	        // for a bug (see KB 316495 at microsoft.com).
	        $(".${galleryView.mdsClientId}_caption input[type=radio]").click(function () {
	          $(".${galleryView.mdsClientId}_caption input[type=radio]").prop("checked", false);
	          $(this).prop("checked", true);
	        });
			  
			$(".mds_btnOkTop, .mds_btnOkBottom").click(function(e) {
				var chkIds = [];
				$(".${galleryView.mdsClientId}_caption input[type=radio]").each(function() {
					if ($(this).prop("checked")){
						chkIds.push($(this).val());
					}
				});
				if (chkIds.length > 0){
					$("#hdnCheckedContentObjectIds").val(chkIds);
				}
											 	
				//$("#assignThumbnailForm").attr("action", "${ctx}/cm/contentobjects/transferobject").submit();
				$("#assignThumbnail").click();
							
				//e.preventDefault();
			});
			
			$(".mds_btnCancelTop, .mds_btnCancelBottom").click(function(e) {
				$("#cancel").click();
			});
		});
	})(jQuery);
</script>