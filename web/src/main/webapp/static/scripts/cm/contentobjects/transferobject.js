<script type="text/javascript">
	(function ($) {
		jQuery(document).ready(function () {
			$("#${galleryView.mdsClientId} .thmb").equalSize(); // Make all thumbnail tags the same width & height
			$('#${galleryView.mdsClientId} .mds_go_t').css('width', '').css('display', ''); // Remove the width that was initially set, allowing title to take the full width of thumbnail
			
			$("#${galleryView.mdsClientId} .chkCheckUncheckAll").on('click', function () {
				var checkAll = !$(this).data("isChecked"); // true when we want to check all; otherwise false
				
				$(this).data("isChecked", checkAll);
						
				$("#${galleryView.mdsClientId} .thmb input[type=checkbox]").prop("checked", checkAll);
							
				return false;
			});
			  
			if ($('#${galleryView.mdsClientId}_ThumbView').length > 0){
				var thumbOptions = {
					allowMultiSelect: true,
					checkedContentItemIdsHiddenFieldClientId: 'hdnCheckedContentObjectIds',
					requiredSecurityPermissions: '1',
					thumbPickerUrl: '${ctx}/services/api/albumrests/gettreepicker',
					albumIdsToSelect: [$('#albumId').val()]
				};
				
				$('#${galleryView.mdsClientId}_ThumbView').mdsThumbPicker(null, thumbOptions);
			}
			  
			$(".mds_btnOkTop, .mds_btnOkBottom").click(function(e) {
				var chkIds = [];
				$("#${galleryView.mdsClientId} .thmb input:checked").each(function() {
					chkIds.push($(this).val());
				});
				if (chkIds.length > 0){
					$("#hdnCheckedContentObjectIds").val(chkIds);
				}
							 	
				//$("#transferObjectForm").attr("action", "${ctx}/cm/contentobjects/transferobject").submit();
				$("#transfer").click();
							
				//e.preventDefault();
			});
			$(".mds_btnCancelTop, .mds_btnCancelBottom").click(function(e) {
				$("#cancel").click();
			});
		});
	})(jQuery);
</script>