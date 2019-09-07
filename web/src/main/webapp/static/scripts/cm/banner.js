<script type="text/javascript">
	$(document).ready(function() {
		//table sort
		//tableSort({callBack : page});
		
		$("#btnExport").click(function(){
			top.$.mdsForm.confirm("<fmt:message key="bannerList.export.confirm" />","<fmt:message key="exportform.title" />", {ok: function(){
				$("#searchForm").attr("action","${ctx}/cm/banners/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){
			$.mdsForm.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/cm/banners/import",
			     isReadOnly: false,
			     isImportForm: true,
			     template: $("#importBox").html(),
			     formId: "#importForm",
			     postType: "multipart",
			     waitingMsg:'<fmt:message key="importform.importing"/>',
			     onPostSuccess: function(data) {
			    	 top.$.mdsForm.confirm(data.message,"<fmt:message key="importform.title" />",
											{
												buttonsFocus:1,
												ok: function() {
													window.location.reload();
												}
											});
			     }
			});
		});
	});
</script>