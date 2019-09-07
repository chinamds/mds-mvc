<script type="text/javascript">
    $(function () {
    	initDailyListTable("table", '${ctx}/services/api/dailyLists/approval/table', {q: $("#query").val()});
    	 
		$("#buttonSearch").click(function(){
			initDailyListTable("table", '${ctx}/services/api/dailyLists/approval/table', {q: $("#query").val()});
		});
				
		$("#approve, #reject").click(function(){
			var a = $(this);
			var approvalAction = 'Approve';
			if(!a.is(".btn-primary")) {
				approvalAction = 'Reject';
			}
			//alert('getSelections: ' + JSON.stringify($("#table").bootstrapTable('getSelections')));
			var seletions = $("#table").bootstrapTable('getAllSelections');
			var ids = [];
			for(x in seletions){
				ids.push({
					id: seletions[x].id,
					approvalAction: approvalAction,
					approvalOpinion: $('#approvalOpinion' + seletions[x].id).val()
				});
			}
			if (ids.length>0){
				top.$.mdsForm.confirm(approvalAction == 'Approve' ? '<fmt:message key="table.message.approveconfirm" />' : '<fmt:message key="table.message.rejectconfirm" />', 
						'<fmt:message key="dailyListList.dailyList" />', {ok: function(){
					$.ajax({
		                url: '${ctx}/services/api/dailyLists/approve',
		                type: "POST",
						data: JSON.stringify(ids),
						contentType: "application/json; charset=utf-8",
						dataType: "json",
		                success: function () {
		                	alert('<fmt:message key="album.deleted"/>');
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
    });
	
	function actionFormatter(value) {
        return [
            '<a class="update" href="${ctx}/cm/dailyListform?method=Approve&id=' + value + '" title="<fmt:message key="button.Approve.tip"/>"><i class="glyphicon glyphicon-edit"></i></a>'
            //'<a class="remove" href="javascript:" title="<fmt:message key="button.delete.tip" />"><i class="glyphicon glyphicon-remove-circle"></i></a>',
        ].join('');
    } 
    
    function thumbnailFormatter(value) {
    	var title = '<div class="dcm_ns">'; 
    	title += value;
    	title += '</div>';

   		return title;
    }
    
    function flowchartFormatter(value) {
    	var title = '<div class="flowchart">'; 
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
   		var title = '<a href="${ctx}/cm/dailyListform?id=' + row.id + '" class="btn btn-link no-padding">' + value + '</a>';
		
   		return title;
    }
    
    function textInputFormatter(value, row) {
   		var title = '<textarea name="approvalOpinion' + row.id + '" id="approvalOpinion' + row.id + '" class="form-control" title="<fmt:message key="dailyList.approvalOpinion"/>" rows="2"></textarea>';
		
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
    
    function initDailyListTable(tableId, url, searchformData, options, columns) {
		var defaults = {
				 url: url,   
				 queryParams: function(params) {
					if (searchformData != undefined && searchformData != null){
						return $.extend({}, params, searchformData);    
					}else{
						return params;
					}
				},
				//toolbar: "#toolbar",  
				sidePagination: "server",  
				pageNumber: 1,    
				pageSize: 10,   
				pageList: [
					10, 20, 50, 100, 200 
				],
				pagination: true,  
				showRefresh: false, 
				showColumns: false, 
				searchOnEnterKey: true,  
				search:false,
				onLoadSuccess: function(data) {
					$(".flowchart").flowChart({
						"line-color"    : "red",
						"line-length"   : 5,
						"element-color" : "red",
						"symbols"       : {
							"start"     : {
								"element-color" : "blue",
								"fill"          : "red"
							}
						},
					});
				}  
			};
		if (columns != undefined && columns != null){
			defaults.columns = columns;
		}
		var settings = defaults;
		if (options != undefined && options != null){
			settings = $.extend({}, defaults, options);
		}
		
		$("#" + tableId).bootstrapTable(settings);
	};
</script>
