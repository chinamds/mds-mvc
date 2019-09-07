<script type="text/javascript">
	/*if (typeof window.Mds === "undefined" || !window.Mds){
		window.Mds = {};
	}
	window.Mds.AppRoot = "${ctx}";
	if (!(typeof(PNotify) === "undefined") && PNotify){
		PNotify.prototype.options.styling = 'bootstrap3'; // Bootstrap version 3
		PNotify.prototype.options.icons = 'bootstrap3'; // glyphicons .clientMessage
	}*/
  
    $(document).ready(function () {
    	<c:if test="${not empty galleryView.clientMessage}">
    	$.mdsShowMsg("${galleryView.clientMessage.title}", "${galleryView.clientMessage.message}", {msgType: "${galleryView.clientMessage.msgType}", autoCloseDelay: "${galleryView.clientMessage.autoCloseDelay}"});
    	</c:if>
    });
</script>