<script type="text/javascript">
    $(function () {
    	$.mdsTable.initTable("table", '${ctx}/services/api/contentLists/table', {q: $("#query").val()});
    	 
		$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/contentLists/table', {q: $("#query").val()});
		});
				
		$("#delete").click(function(){
			var seletions = $("#table").bootstrapTable('getAllSelections');
			var ids="";
			for(x in seletions){
				if (ids.length>0){
					ids+=",";
					ids+=seletions[x].id;
				}else{
					ids += seletions[x].id;
				}
			}
			if (ids.length>0){
				top.$.mdsForm.confirm('<fmt:message key="table.message.deleteconfirm" />','<fmt:message key="contentListList.contentList" />', {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/contentLists/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="contentList.deleted"/>');
		                	$("#table").bootstrapTable('refresh');
		                },
		                error: function (response) {
		                	$.mdsForm.alert(response.responseText, '<fmt:message key="delete.deletefailed" />');
		                }
		            })
				}, buttonsFocus:1});
			}else{
				alert('<fmt:message key="table.message.norecordselected" />');
			}
		});
		
		$("#btnExport").click(function(){
			top.$.mdsForm.confirm('<fmt:message key="message.export.confirm" />','<fmt:message key="exportform.title" />', {ok: function(){
				$.fileDownload("${ctx}/cm/contentLists/export")
	                .done(function () { $.mdsForm.alert('<fmt:message key="message.export.successed" />', '<fmt:message key="exportform.title"/>'); })
	                .fail(function () { $.mdsForm.alert('<fmt:message key="message.export.failed" />', '<fmt:message key="exportform.title"/>'); });
				//$("#searchForm").attr("action","${ctx}/cm/areas/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){	
			$.mdsForm.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/cm/contentLists/import",
			     isReadOnly: false,
			     isImportForm: true,
			     template: $("#importBox").html(),
			     formId: "#importForm",
			     postType: "multipart",
			     waitingMsg:'<fmt:message key="importform.importing"/>',
			     onPostSuccess: function(data) {
			    	 top.$.mdsForm.confirm(data.message,'<fmt:message key="importform.title" />',
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
	
	function actionFormatter(value) {
        return [
            '<a class="update" href="${ctx}/cm/contentListform?method=Edit&id=' + value + '" title="<fmt:message key="button.edit.tip"/>"><i class="glyphicon glyphicon-edit"></i></a>'
            //'<a class="remove" href="javascript:" title='<fmt:message key="button.delete.tip" />'><i class="glyphicon glyphicon-remove-circle"></i></a>',
        ].join('');
    } 
    
    function thumbnailFormatter(value) {
    	var title = '<div class="mds_ns">'; 
    	title += value;
    	title += '</div>';

   		return title;
    }
    
    function dateFormatter(value) {
        return new Date(parseInt(value.substr(6))).format("yyyy-MM-dd"); // hh:mm:ss
    }
    
    function dateTimeFormatter(value) {
        return new Date(parseInt(value.substr(6))).format("yyyy-MM-dd hh:mm"); // hh:mm:ss
    }
	
	function timeFormatter(value) {
        return new Date(parseInt(value.substr(6))).format("hh:mm"); // hh:mm:ss
    }
    
    function nameFormatter(value, row) {
   		var title = '<a href="${ctx}/cm/contentListform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }
    
    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	//var msgParam = '<fmt:message key="contentListList.contentList"/>';
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="contentListList.contentList"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                    /*url: '${ctx}/contentListform?delete=&id=' + row.id,
                    type: 'post',*/
                	url: '${ctx}/services/api/contentLists/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="contentList.deleted"/>');
                    	$("#table").bootstrapTable('refresh');
                    },
                    error: function (response) {
                    	$.mdsForm.alert(response.responseText, '<fmt:message key="delete.deletefailed" />');
                    }
                })
            }
        }
    };
    
    var contentPreview;
    var renderPreview = function(moid) {
    	$.ajax({
 			type: "get",
 			async: true,
 			url: "${ctx}/services/api/contentLists/contentpreview?moid=" + moid,//
 			contentType : 'application/json',
            dataType : "json",
 			success: function (data) {
 				// Compile the HTML and javascript templates
 				$.templates({
 					contentpreview_a_tmpl_html : data.Settings.ContentTmplName,
 					contentpreview_a_tmpl_script : data.Settings.HeaderTmplName
 				});

 				data.Settings.ClientId='mds_dl';
 				data.Settings.HeaderTmplName = 'contentpreview_a_tmpl_html';
				data.Settings.HeaderClientId = 'mds_dl_mediaHtml';
				data.Settings.ThumbnailTmplName = 'contentpreview_a_tmpl_html';
				data.Settings.ThumbnailClientId = 'mds_dl_mediaHtml';
				data.Settings.ContentTmplName = 'contentpreview_a_tmpl_html';
				data.Settings.ContentClientId = 'mds_dl_mediaHtml';
				data.Settings.LeftPaneTmplName = 'contentpreview_a_tmpl_html';
				data.Settings.LeftPaneClientId = 'mds_dl_mediaHtml';
				data.Settings.RightPaneTmplName = 'contentpreview_a_tmpl_html';
				data.Settings.RightPaneClientId = 'mds_dl_mediaHtml';
				
				window.mds_dl = {};
				window.mds_dl.p = function(){return $('#mds_dl');};
				window.mds_dl.mdsData = data;

 				// Pass the script template to the renderer and then execute. It is expected the script contains code for
 				// rendering the HTML template.
 				var script = $.render["contentpreview_a_tmpl_script"](window.mds_dl.mdsData);
 				if (console) console.log(script); // Send to console (useful for debugging)
 				(new Function(script))(); // Execute the script
 				if (!contentPreview){
 					contentPreview = new OnoffCanvas('#onoffcanvas-dialog').on('show.onoffcanvas', (event)=>{
 					   console.log(event.type); // show.onoffcanvas
 					 }).on('hide.onoffcanvas',(event)=>{
 					   //console.log(event.target); // <div class="onoffcanvas ...
 						$('#mds_dl').find('.mds_mvContentView').remove();
 					 });
 				}
 				contentPreview.show();
 				//$('#onoffcanvas-dialog').addClass('is-open');
 			},
 			error: function (response) {
 				$.mdsForm.alert(response.responseText);
 			}
   		});    	
	};
</script>
