<script type="text/javascript">
	(function ($) {
		var aid = $('#moid').val();
		var thumbOptions = {
			clientId: '${galleryView.mdsClientId}',
			allowMultiSelect: true,
			thumbPickerType: 'all',
			thumbShowType: 'contentobjectid',
			checkedContentItemIdsHiddenFieldClientId: 'hdnCheckedContentObjectIds',
			requiredSecurityPermissions: '1',
			thumbPickerUrl: '${ctx}/services/api/albumrests/gettreepicker',
			albumIdsToSelect: [aid],
			controlPlugin:'rotate'
		};
		
		$('#${galleryView.mdsClientId}_ThumbView').mdsThumbPicker(null, thumbOptions);
		
		jQuery(document).ready(function () {
			//$("#${galleryView.mdsClientId} .thmb").equalSize(); // Make all thumbnail tags the same width & height
			//$('#${galleryView.mdsClientId} .mds_go_t').css('width', '').css('display', ''); // Remove the width that was initially set, allowing title to take the full width of thumbnail
						  			
			/*$(".mds_thmb_img").cropper({
				  viewMode: 3,
			        dragMode: 'move',
			        autoCropArea: 1,
			        restore: false,
			        modal: false,
			        guides: false,
			        highlight: false,
			        cropBoxMovable: false,
			        cropBoxResizable: false,
			        toggleDragModeOnDblclick: false,
				  crop: function(event) {
				    console.log(event.detail.x);
				    console.log(event.detail.y);
				    console.log(event.detail.width);
				    console.log(event.detail.height);
				    console.log(event.detail.rotate);
				    console.log(event.detail.scaleX);
				    console.log(event.detail.scaleY);
				  }
			});*/
			
			var btns = $(".mds_hor, .mds_vert");
			
			$(".mds_hor, .mds_vert").click(function(e) {
				// Get the Cropper.js instance after initialized
				//var cropper = $("#${galleryView.mdsClientId} .thmb img").data('cropper');
				$("#${galleryView.mdsClientId} .thmb img").rotate(90);
			});
			  
			$(".mds_btnOkTop, .mds_btnOkBottom").click(function(e) {
				var btns = $(".mds_hor, .mds_vert");
				var chkIds = [];
				$("#${galleryView.mdsClientId} .thmb input[name='thmb'][checked]").each(function() {
					chkIds.push($(this).val());
				});
				if (chkIds.length > 0){
					$("#hdnCheckedContentObjectIds").val(chkIds);
				}
											 	
				//$("#rotateImageForm").attr("action", "${ctx}/cm/contentobjects/transferobject").submit();
				//$("#rotateImage").click();
							
				//e.preventDefault();
			});
			
			$(".mds_btnCancelTop, .mds_btnCancelBottom").click(function(e) {
				$("#cancel").click();
			});
		});
	})(jQuery);
</script>