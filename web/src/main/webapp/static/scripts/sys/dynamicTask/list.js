<script type="text/javascript">
    $(function() {
        $(".btn-delete").click(function() {
            var checkbox = $.table.getAllSelectedCheckbox($("#table"));
            if(!checkbox.length) {
                return;
            }

            $.app.confirm({
                title : "确认删除",
                message: "<div class='form-inline'>是否强制终止正在运行的任务：<label class='checkbox inline'><input type='radio' name='forceTermination' checked='true' value='true'>是</label>&nbsp;&nbsp;<label class='checkbox inline'><input type='radio' name='forceTermination' value='false'>否</label></div>",
                ok : function() {
                    var forceTermination = $("[name=forceTermination]:checked").val();
                    location.href = ctx + '/admin/maintain/dynamicTask/batch/delete?' + checkbox.serialize() + "&forceTermination=" + forceTermination;
                }
            });
        });

        $(".btn-start").click(function() {
            var checkbox = $.table.getAllSelectedCheckbox($("#table"));
            if(!checkbox.length) {
                return;
            }

            $.app.confirm({
                title : "启动任务",
                message: "确认启动选中任务吗？",
                ok : function() {
                    var forceTermination = $("[name=forceTermination]:checked").val();
                    location.href = ctx + '/admin/maintain/dynamicTask/start?' + checkbox.serialize();
                }
            });
        });

        $(".btn-stop").click(function() {
            var checkbox = $.table.getAllSelectedCheckbox($("#table"));
            if(!checkbox.length) {
                return;
            }

            $.app.confirm({
                title : "停止任务",
                message: "<div class='form-inline'>是否强制终止正在运行的任务：<label class='checkbox inline'><input type='radio' name='forceTermination' checked='true' value='true'>是</label>&nbsp;&nbsp;<label class='checkbox inline'><input type='radio' name='forceTermination' value='false'>否</label></div>",
                ok : function() {
                    var forceTermination = $("[name=forceTermination]:checked").val();
                    location.href = ctx + '/admin/maintain/dynamicTask/stop?' + checkbox.serialize() + "&forceTermination=" + forceTermination;
                }
            });
        });

    });
</script>