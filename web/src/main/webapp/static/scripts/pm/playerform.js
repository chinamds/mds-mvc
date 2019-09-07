<script type="text/javascript">
    $(function () {
        
    	 $('.date:not(.custom)').each(function() {
             var $date = $(this);

             if($date.attr("initialized") == "true") {
                 return;
             }

             var dateformat = $(this).find("[data-format]").data("format");
             $date.datetimepicker({
             	format: dateformat
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
     		placeholder: "<fmt:message key='player.gallery.tip'/>",
     		allowClear: true,
     		disabled: $("#method").val()=='Add'? false : true
 		};

		$("#gallery").select2(options);
		
		$("#gallery").on("change", function (e) {
		});
    });
</script>
