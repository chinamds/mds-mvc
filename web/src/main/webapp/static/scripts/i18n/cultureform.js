<script type="text/javascript">
    $(function () {
        $(document).ready(function() {
            //$("input[type='text']:visible:enabled:first", document.forms['cultureForm']).focus();
            $("#cultureCode").focus();
        });
        
        var options = {
                theme: 'bootstrap',
                ajax: {
                    url: '${ctx}/services/api/cultures/available/select2',
                    dataType: 'json',
                    data: function (params) {
                      var query = {
                        q: params.term
                      }

                      // Query parameters will be ?q=[term]
                      return query;
                    }
                },
                placeholder: "<fmt:message key='culture.cultureCode.tip'/>",
                allowClear: true
            };

        $("#cultureCode").select2(options);
    });
</script>
