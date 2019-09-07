<script type="text/javascript">
	(function ($) {
		jQuery(document).ready(function () {
			//$("#${galleryView.mdsClientId} .thmb").equalSize(); // Make all thumbnail tags the same width & height
			//$('#${galleryView.mdsClientId} .mds_go_t').css('width', '').css('display', ''); // Remove the width that was initially set, allowing title to take the full width of thumbnail
						  
			var aid = $('#albumId').val();
			var thumbOptions = {
				allowMultiSelect: true,
				thumbPickerType: 'all',
				checkedContentItemIdsHiddenFieldClientId: 'hdnCheckedContentObjectIds',
				requiredSecurityPermissions: '1',
				thumbPickerUrl: '${ctx}/services/api/albumrests/gettreepicker',
				albumIdsToSelect: [aid],
				controlPlugin:'textarea'
			};
			
			$('#${galleryView.mdsClientId}_ThumbView').mdsThumbPicker(null, thumbOptions);
			  
			$(".mds_btnOkTop, .mds_btnOkBottom").click(function(e) {
				var chkIds = [];
				var titles = [];
				$("#${galleryView.mdsClientId} .thmb input:hidden").each(function() {
					chkIds.push($(this).val());
					titles.push($(this).prev().val());
				});
				if (chkIds.length > 0){
					$("#hdnCheckedContentObjectIds").val(chkIds);
					$("#hdnTitles").val(titles);
				}
											 	
				//$("#editCaptionForm").attr("action", "${ctx}/cm/contentobjects/transferobject").submit();
				$("#editCaptions").click();
							
				//e.preventDefault();
			});
			
			$(".mds_btnCancelTop, .mds_btnCancelBottom").click(function(e) {
				$("#cancel").click();
			});
		});
	})(jQuery);
</script>