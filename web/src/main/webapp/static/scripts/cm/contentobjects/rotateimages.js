<script type="text/javascript">
	(function ($) {	
		jQuery(document).ready(function () {
			 $("#${galleryView.mdsClientId} .thmbRotate").equalSize(); // Make all thumbnail tags the same width & height
			 
			configureEventHandlers();
			  
			$(".mds_btnOkTop, .mds_btnOkBottom").click(function(e) {
				var ids = [];
				var newSides = [];
				$("#${galleryView.mdsClientId} .thmbRotate").each(function () {
		            var $hdnInput = $(this).find('input.hdnSelectedSide');
		            newSides.push($hdnInput.val());
		            ids.push($(this).data('id'));
				});
				if (ids.length > 0){
					$("#hdnCheckedContentObjectIds").val(ids);
					$("hdnNewSides").val(newSides);
				}
											 	
				//$("#rotateImageForm").attr("action", "${ctx}/cm/contentobjects/transferobject").submit();
				$("#rotateImages").click();
							
				//e.preventDefault();
			});
			
			$(".mds_btnCancelTop, .mds_btnCancelBottom").click(function(e) {
				$("#cancel").click();
			});
		});
		
		var configureEventHandlers = function () {
	        // When side of image is clicked, save choice in hidden field and update background image of old and new side markers
	        $('a', $("#${galleryView.mdsClientId} .thmbRotate")).click(function () {
	          // Assign selected side to hidden input
	          var $parentThmb = $(this).closest('.thmbRotate');
	          var $hdnInput = $parentThmb.find('input.hdnSelectedSide');
	          var oldSide = $hdnInput.val();
	          var newSide = $(this).data('side');

	          $hdnInput.val(newSide);

	          // Reset previously selected side to default image
	          $parentThmb.find('a[data-side=' + oldSide + ']').css('background', 'transparent url(${fns:getSkinnedUrl(pageContext.request, "/images/rotate/' + oldSide + '.gif")}) no-repeat 0 0');

	          // Mark newly selected side
	          $(this).css('background', '#ccc url(${fns:getSkinnedUrl(pageContext.request, "/images/rotate/' + newSide + '1.gif")}) no-repeat 0 0');
	        });
	      };
	})(jQuery);
</script>