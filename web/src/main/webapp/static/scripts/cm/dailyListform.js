<script type="text/javascript">
    $(function () {
    	//$.mdsTable.initTable("table", '${ctx}/services/api/dailyLists/table', {q: $("#query").val()});
    	/*if ($("#method").val()){
    		$.mdsTable.initTable("table2", '${ctx}/services/api/dailyLists/daylistitem/'+ $("#id").val() ? $("#id").val() : 0, null, {pagination: false, striped: false});
    	}*/
    	if (!$('#jsonItems').val() || $('#jsonItems').val()==''){
	    	$.ajax({
	 			type: "get",
	 			async: true,
	 			url: "${ctx}/services/api/dailyLists/daylistitem/" + ($("#id").val() ? $("#id").val() : 0),//
	 			contentType : 'application/json',
	            dataType : "json",
	 			success: function (data) {
	 				initAppendGrid(data);
	 			},
	 			error: function (response) {
	 				$.mdsForm.alert(response.responseText);
	 			}
	 		});
    	}else{
    		//var json1 = $('#jsonItems').val();
    		//var json2 = '${jsonItems}';
    		initAppendGrid($.parseJSON($('#jsonItems').val()));
    	}
    	
    	function initAppendGrid(data) {
    		$('#tblAppendGrid').appendGrid('init', {
				initRows: 1,
	            columns: [
	                { name: 'content', display: '<fmt:message key="dailyListZone.content"/>', type: 'text', ctrlAttr: { maxlength: 200 }, ctrlCss: { width: '160px'} },
	                { name: 'thumbnailHtml', display: '<fmt:message key="dailyListZone.fileName"/>', type: 'custom', ctrlAttr: { maxlength: 200, readonly:'readonly' }, ctrlCss: { width: '160px'},
	               customBuilder: function (parent, idPrefix, name, uniqueIndex) {
	                    // Prepare the control ID/name by using idPrefix, column name and uniqueIndex
	                    var ctrlId = idPrefix + '_' + name + '_' + uniqueIndex;
	                    // Create a span as a container
	                    var ctrl = document.createElement('div');
	                    //$(ctrl).addClass("mds_ns").appendTo(parent);
	                    //ctrl = document.createElement('ui');
	                  
	                    // Set the ID and name to container and append it to parent control which is a table cell
	                    $(ctrl).attr({ id: ctrlId, name: ctrlId }).addClass("mds_ns").appendTo(parent);

	                    // Finally, return the container control	 		              	
	                    return ctrl;
	                },
	                customGetter: function (idPrefix, name, uniqueIndex) {
	                    // Return the formatted duration
	                    return '';
	                },
	                customSetter: function (idPrefix, name, uniqueIndex, value) {
	                    // Prepare the control ID/name by using idPrefix, column name and uniqueIndex
	                    var ctrlId = idPrefix + '_' + name + '_' + uniqueIndex;
	                    // Set the value to different spinners
	                    $('#' + ctrlId).html(value);

	                }
	                },
	                { name: 'contentTypeDisplay', display: '<fmt:message key="dailyListZone.contentType"/>', type: 'text', ctrlAttr: { maxlength: 50, readonly:'readonly' }, ctrlCss: { width: '100px'} },
	                { name: 'duration', display: '<fmt:message key="dailyListZone.duration"/>', type: 'text', ctrlAttr: { maxlength: 20 }, ctrlCss: { width: '50px', 'text-align': 'right' }, value: 0 },
	                { name: 'mute', display: '<fmt:message key="dailyListZone.mute"/>', type: 'checkbox' },
	                { name: 'aspectRatio', display: '<fmt:message key="dailyListZone.aspectRatio"/>', type: 'checkbox' },
	                { name: 'timeFrom', display: '<fmt:message key="dailyListZone.timeFrom"/>', type: 'time', ctrlCss: { width: '100px'} },
	                { name: 'timeTo', display: '<fmt:message key="dailyListZone.timeTo"/>', type: 'time', ctrlCss: { width: '100px'} },
	                { name: 'contentObjectId', type: 'hidden', value: 0 },
	                { name: 'dailyListItemId', type: 'hidden', value: 0 },
	                { name: 'id', type: 'hidden', value: 0 },
	                { name: 'contentType', type: 'hidden', value: 0 },
	                { name: 'fileName', type: 'hidden', value: '' }
	            ],
	            initData: data.rows,
	            hideRowNumColumn: false,
	            rowButtonsInFront: true,
	            hideButtons: {
	            	insert: true,
	            	append: true
	           },
	           customFooterButtons: [
	                { uiButton: { icon: 'ui-icon-folder-open', label: '<fmt:message key="button.import"/>' }, btnAttr: { title: '<fmt:message key="button.import"/>' }, click: function (evt) {
	                	var test = $('#independentSpaceForDailyList').val();
	                	if (!$('#independentSpaceForDailyList').val() || $('#independentSpaceForDailyList').val()=='' 
	                		|| $('#independentSpaceForDailyList').val().toLowerCase()=='true'){
		                	var galleries = $('#gallery').select2('data');
		                	if (!galleries || galleries.length == 0){
		                		$.mdsForm.alert('<fmt:message key="dailyList.gallery.required" />', '<fmt:message key="dailyList.gallery" />');
		                		return;
		                	}
		                	//$('#rootwizard [href="#tab2"]').attr("data-content-url", "${ctx}/cm/albumtreepickers?gid=" + galleries[0].id);
		                	frames['treepickerFrame'].location.href = "${ctx}/cm/albumtreepickers?gid=" + galleries[0].id;
		                	//openAlbumTreePicker(galleries[0].id);
		                	//$('#tab2').attr("data-content-url", "${ctx}/cm/albumtreepickers?gid=" + galleries[0].id);
	                	}else{
	                		//openAlbumTreePicker();
	                		//$('#tab2').attr("data-content-url", "${ctx}/cm/albumtreepickers");
	                		//$('#rootwizard [href="#tab2"]').attr("data-content-url", "${ctx}/cm/albumtreepickers");
	                		frames['treepickerFrame'].location.href = "${ctx}/cm/albumtreepickers";
	                	}
	                	$('#rootwizard').smartWizard("next");
	                	/*$.mdsForm.waiting();
	                	setTimeout(function () {
	                		var instance = $('#albumTreeView').jstree(true);
	                    instance.deselect_all();
	                    instance.select_node('tv_${selectedAlbumIds[0]}');
	                   $.mdsForm.waitingOver();
	       			}, 1000);*/
	                	}, atTheFront: true }
	           ]
			});
    	}
    	       
    	
    	
    	// Toolbar extra buttons
        var btnFinish = $('<button></button>').text('<fmt:message key="button.wizard.finish" />')
                                         .addClass('btn btn-info btn-finish')
                                         .on('click', function(){
                                        	 var chkIds = [];
                                        	 $("#albumThumbView .thmb input:checked", $('#treepickerFrame').contents().find(".albumtreepicker")).each(function() {
                                        		 chkIds.push($(this).val());
                                        	 });
                                        	 if (chkIds.length > 0){
                                        		 $.ajax({
                                     	 			type: "get",
                                     	 			async: true,
                                     	 			url: "${ctx}/services/api/dailyLists/gendaylistitems",//
                                     	 			data: {
                                						// Query string parms to be added to the AJAX request
                                						ids: chkIds.join(',')
                                					},
                                     	 			contentType : 'application/json',
                                     	            dataType : "json",
                                     	 			success: function (data) {
			                                        	 $('#tblAppendGrid').appendGrid('appendRow', data);
	                                        		 },
	                                 	 			error: function (response) {
	                                 	 				$.mdsForm.alert(response.responseText);
	                                 	 			}
                                        		 });
                                        	 }
                                        	 $('#rootwizard').smartWizard("prev"); });
        var btnCancel = $('<button></button>').text('<fmt:message key="button.wizard.cancel" />')
                                         .addClass('btn btn-danger btn-cancel')
                                         .on('click', function(){ $('#rootwizard').smartWizard("prev"); });
        var btnSelectAll = $('<button></button>').text('<fmt:message key="button.selectall" />')
								        .addClass('btn btn-primary btn-selectallthumb')
								        .prepend('<i class="glyphicon glyphicon-unchecked"></i> ')
								        .on('click', function(){ 
								          var checkAll = !$(this).hasClass("active"); // true when we want to check all; otherwise false
								          if (checkAll){
								        	  $(this).addClass("active");
								        	  $(this).find(".glyphicon").removeClass("glyphicon-unchecked").addClass("glyphicon-check");
								          }else{
								        	  $(this).removeClass("active");
								        	  $(this).find(".glyphicon").removeClass("glyphicon-check").addClass("glyphicon-unchecked");
								          }
								          								          
								          $("#albumThumbView .thmb input[type=checkbox]", $('#treepickerFrame').contents().find(".albumtreepicker")).prop("checked", checkAll);

								          return false; });

        // Smart Wizard
        $('#rootwizard').smartWizard({
                selected: 0,
                theme: 'arrows',
                transitionEffect:'fade',
                toolbarSettings: {toolbarPosition: 'bottom',
                				  toolbarButtonPosition: 'end',
                				  showNextButton: false, // show/hide a Next button
                		          showPreviousButton: false, // show/hide a Previous button
                                  toolbarExtraButtons: [btnSelectAll, btnFinish, btnCancel]
                                },
                lang: { // Language variables for button
                                    next: '<fmt:message key="button.wizard.next" />',
                                    previous: '<fmt:message key="button.wizard.prev" />'
                                }
             });
                        
        $("#rootwizard").on("showStep", function(e, anchorObject, stepNumber, stepDirection) {
            // Enable finish button only on last step
        	$('#currentStep').val(stepNumber);
            if(stepNumber == 1){
            	$('.btn-selectallthumb').removeClass('hide');
                $('.btn-finish').removeClass('hide');
                $('.btn-cancel').removeClass('hide');
               /* var instance = $('#albumTreeView').jstree(true);
                instance.deselect_all();
                instance.select_node('tv_1');
                $('#albumTreeView').trigger('ready', instance.get_node('tv_1'));*/
                //$('#albumTreeView').trigger('ready');
                //$('#albumTreeView').jstree("select_node", "1");
                /*$('#albumThumbView .thmb').equalSize();
                $('#albumTreeView').css({
    			    width: $('#albumName').outerWidth() + $('#albumButton').outerWidth()
    			});*/
                //$(".sw-btn-prev").addClass('hide');
                //$(".sw-btn-next").addClass('hide');
            }else{
            	//$('.btn-cancel').removeClass('disabled');
            	$('.btn-selectallthumb').addClass('hide');
                //$(".sw-btn-prev").addClass('hide');
                //$(".sw-btn-next").addClass('hide');
                $('.btn-finish').addClass('hide');
                $('.btn-cancel').addClass('hide');
                //$('.btn-finish').addClass('disabled');
            }
        });
        $('#tab2').on('show', function() {
            //console.log('#foo is now visible');
        	/*$('#albumThumbView .thmb').equalSize();
            $('#albumTreeView').css({
			    width: $('#albumName').outerWidth() + $('#albumButton').outerWidth()
			});*/
        });

        
        $("#rootwizard").on("leaveStep", function(e, anchorObject, stepNumber, stepDirection) {
            //var elmForm = $("#form-step-" + stepNumber);
            // stepDirection === 'forward' :- this condition allows to do the form validation
            // only on forward navigation, that makes easy navigation on backwards still do the validation when going next
            if(stepDirection === 'forward'){
            	if(stepNumber == 0){
            	}
            	if(stepNumber == 1){     
                }
            }
            
            return true;
        });
        
    	 $('.date:not(.custom)').each(function() {
             var $date = $(this);

             if($date.attr("initialized") == "true") {
                 return;
             }

             var dateformat = $(this).find("[data-format]").data("format");
             $date.datetimepicker({
             	format: dateformat,
             	buttons: {
                    showToday: true,
                    showClear: true,
                    showClose: true
                },
                icons: {
                    time: 'fa fa-clock-o',
                    date: 'fa fa-calendar',
                    up: 'fa fa-arrow-up',
                    down: 'fa fa-arrow-down',
                    previous: 'fa fa-chevron-left',
                    next: 'fa fa-chevron-right',
                    today: 'far fa-calendar-check',
                    clear: 'far fa-trash-alt',
                    close: 'fa fa-times'
                }
             });
             $date.find(":input").click(function() {$date.find(".icon-calendar,.icon-time,.icon-date").click();});
             $date.attr("initialized", true);
         });
    	 
    	 
    	 var options = {
     		theme: "bootstrap",
     		ajax: {
     		    url: '${ctx}/services/api/galleries/select2',
     		    dataType: 'json',
     		    data: function (params) {
     		      var query = {
     		        q: params.term
     		      }

     		      // Query parameters will be ?q=[term]
     		      return query;
     		    }
     		},
     		placeholder: "<fmt:message key='dailyList.gallery.tip'/>",
     		allowClear: true,
     		disabled: $("#method").val()=='Add'? false : true
 		};

		$("#gallery").select2(options);
		
		$("#gallery").on("change", function (e) {
		});
		
		//if ($('#currentStep').val() == 0){
        if ($('#tab1').css('display') == 'block'){
        	$('.btn-selectallthumb').addClass('hide');
            $('.btn-finish').addClass('hide');
            $('.btn-cancel').addClass('hide');
            //$('#rootwizard').smartWizard("prev");
        }else{
        	if (!$('#independentSpaceForDailyList').val() || $('#independentSpaceForDailyList').val()=='' 
        		|| $('#independentSpaceForDailyList').val().toLowerCase()=='true'){
            	var galleries = $('#gallery').select2('data');
            	if (!galleries || galleries.length == 0){
            		$.mdsForm.alert('<fmt:message key="dailyList.gallery.required" />', '<fmt:message key="dailyList.gallery" />');
            		return;
            	}
            	frames['treepickerFrame'].location.href = "${ctx}/cm/albumtreepickers?gid=" + galleries[0].id;
        	}else{
        		frames['treepickerFrame'].location.href = "${ctx}/cm/albumtreepickers";
        	}
        }
    		
    	 
		$("#buttonSearch").click(function(){
			$.mdsTable.initTable("table", '${ctx}/services/api/dailyLists/table', {q: $("#query").val()});
		});
				
		$("#delete").click(function(){
			//alert('getSelections: ' + JSON.stringify($("#table").bootstrapTable('getSelections')));
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
				top.$.mdsForm.confirm("<fmt:message key="table.message.deleteconfirm" />","<fmt:message key="albumList.album" />", {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/dailylists/' + ids,
		                type: 'DELETE',
		                success: function () {
		                	alert('<fmt:message key="album.deleted"/>');
		                	$("#table").bootstrapTable('refresh');
		                },
		                error: function (response) {
		                	$.mdsForm.alert(response.responseText, "<fmt:message key="delete.deletefailed" />");
		                }
		            })
				}, buttonsFocus:1});
			}else{
				alert("<fmt:message key="table.message.norecordselected" />");
			}
		});
		
		$("#btnExport").click(function(){
			top.$.mdsForm.confirm("<fmt:message key="message.export.confirm" />","<fmt:message key="exportform.title" />", {ok: function(){
				$.fileDownload("${ctx}/cm/dailylists/export")
	                .done(function () { $.mdsForm.alert("<fmt:message key="message.export.successed" />", "<fmt:message key="exportform.title"/>"); })
	                .fail(function () { $.mdsForm.alert("<fmt:message key="message.export.failed" />", "<fmt:message key="exportform.title"/>"); });
				//$("#searchForm").attr("action","${ctx}/cm/areas/export").submit();
			}, buttonsFocus:1});
		});
		
		$("#btnImport").click(function(){	
			$.mdsForm.showFormDialog({
			     title: '<fmt:message key="importform.title" />',
			     postUrl: "${ctx}/cm/dailylists/import",
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
		
		function openAlbumTreePicker(gId, aId) {
            var url = "${ctx}/cm/albumtreepickers";
            var queryAtp;
            if (gId){
            	queryAtp = 'gid=' + gId;
            }
            if (aId){
            	if (queryAtp)
            		queryAtp += '&aid=' + aId;
            	else
            		queryAtp = 'aid=' + aId;
            }
            if (queryAtp){
            	url += "?";
            	url += queryAtp;
            }
            
            //var okTitle = '<fmt:message key="albumtreepicker.title"/>';
            $.mdsDialog.modalDialog('<fmt:message key="albumtreepicker.title"/>', "iframe:" + url, {  
                draggable: true,
                height:480,
                width:640,
                maxHeight:600,
                maxWidth:800,
                //iframeScrolling:'no',
                buttons:[{
                    icon : 'fa fa-unchecked',
                    label : '<fmt:message key="button.selectall" />',
                    cssClass : 'btn-info btn-selectallthumb',
                    action : function(dialogItself) {
                    	var checkAll = !$(this).hasClass("active"); // true when we want to check all; otherwise false
				          if (checkAll){
				        	  $(this).addClass("active");
				        	  $(this).find(".glyphicon").removeClass("glyphicon-unchecked").addClass("glyphicon-check");
				          }else{
				        	  $(this).removeClass("active");
				        	  $(this).find(".glyphicon").removeClass("glyphicon-check").addClass("glyphicon-unchecked");
				          }
				          
				          var iframe1 = $(".bootstrap-dialog-iframe", dialogItself.getModalBody());
		                  var albumtreepicker =iframe1.contents().find(".albumtreepicker");
				          $("#albumThumbView .thmb input[type=checkbox]", albumtreepicker).prop("checked", checkAll);
                    }}, 
                    {
                    icon : 'fa fa-ok',
                    label : 'OK',
                    cssClass : 'btn-primary',
                    action : function(dialogItself) {
                    	var chkIds = [];
                    	var iframe1 = $(".bootstrap-dialog-iframe", dialogItself.getModalBody());
		                var albumtreepicker =iframe1.contents().find(".albumtreepicker");
	                   	$("#albumThumbView .thmb input:checked", albumtreepicker).each(function() {
	                   		chkIds.push($(this).val());
	                   	});
	                   	if (chkIds.length > 0){
	                   		$.ajax({
                	 			type: "get",
                	 			async: true,
                	 			url: "${ctx}/services/api/dailyLists/gendaylistitems",//
                	 			data: {
           						// Query string parms to be added to the AJAX request
           						ids: chkIds.join(',')
           					},
                	 			contentType : 'application/json',
                	            dataType : "json",
                	 			success: function (data) {
                               	 $('#tblAppendGrid').appendGrid('appendRow', data);
                       		 },
                	 			error: function (response) {
                	 				$.mdsForm.alert(response.responseText);
                	 			}
	                   		});
	                   	}
                    	dialogItself.close();
                    	$.mdsForm.cancelModelDialog();
                    }}, 
                    {
                    icon : 'fa fa-remove',
                    label : 'Cancel',
                    action : function(dialogItself) {
                    	dialogItself.close();
                    	$.mdsForm.cancelModelDialog();
                    }
                } ],
                ok : function(modal) {
                    return true;
                }
            });
        }
    });
   
    var contentPreview;
    var renderPreview = function(moid) {
    	$.ajax({
 			type: "get",
 			async: true,
 			url: "${ctx}/services/api/dailyLists/contentpreview?moid=" + moid,//
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
	
	function actionFormatter(value) {
        return [
            '<a class="update" href="${ctx}/cm/dailyListform?method=Edit&id=' + value + '" title="<fmt:message key="button.edit.tip"/>"><i class="fa fa-edit"></i></a>'
            //'<a class="remove" href="javascript:" title="<fmt:message key="button.delete.tip" />"><i class="fa fa-remove-circle"></i></a>',
        ].join('');
    } 
    
    function templateFormatter(value) {
   		var title = '<input type="checkbox" disabled="disabled"' +  (value ? ' checked="checked"' : '') + '/>';
		
   		return title;
    }
    
    function nameFormatter(value, row) {
   		var title = '<a href="${ctx}/cm/dailyListform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }
    
    // update and delete events
    window.actionEvents = {
        'click .remove': function (e, value, row) {
        	//var msgParam = '<fmt:message key="albumList.album"/>';
        	var msgDelConfirm = '<fmt:message key="delete.confirm"><fmt:param><fmt:message key="albumList.album"/></fmt:param></fmt:message>';
            if (confirmMessage(msgDelConfirm)) {
                $.ajax({
                    /*url: '${ctx}/dailyListform?delete=&id=' + row.id,
                    type: 'post',*/
                	url: '${ctx}/services/api/dailylists/' + row.id,
	                type: 'DELETE',
                    success: function () {
                    	alert('<fmt:message key="album.deleted"/>');
                    	$("#table").bootstrapTable('refresh');
                    },
                    error: function (response) {
                    	$.mdsForm.alert(response.responseText, "<fmt:message key="delete.deletefailed" />");
                    }
                })
            }
        }
    };   
</script>
