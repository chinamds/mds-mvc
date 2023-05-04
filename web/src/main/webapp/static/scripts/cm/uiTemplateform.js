<script type="text/javascript">	
    $(function () {
        var options = {
            theme: "bootstrap4",
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
            placeholder: "<fmt:message key='uiTemplate.gallery.tip'/>",
            allowClear: true
        };

        $("#gallery").select2(options);
                      		
   		$("input[type='text']:visible:enabled:first", document.forms['uiTemplateForm']).focus();
    });
        
    //change change flag
    var changedFlag = function (flag) {
        $("form").data("changed", flag);
    }
</script>
