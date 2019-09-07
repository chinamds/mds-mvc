<script type="text/javascript">
    var async = ${not empty param.async and param.async eq true};
    function onClick() {
        var organizationId = 0;
        var jobId = 0;
        var organizationTree =  $.fn.zTree.getZTreeObj($("#organizationTree .ztree").attr("id"));
        var jobTree =  $.fn.zTree.getZTreeObj($("#jobTree .ztree").attr("id"));
        var organizationSelectNodes = organizationTree.getSelectedNodes();
        if(organizationSelectNodes.length) {
            organizationId = organizationSelectNodes[0].id;
        }
        var jobSelectNodes = jobTree.getSelectedNodes();
        if(jobSelectNodes.length) {
            jobId = jobSelectNodes[0].id;
        }

        parent.frames['listFrame'].location.href = "${ctx}/admin/sys/user/" + organizationId + "/" + jobId;

    }
    $(function() {
        $.zTree.initMovableTree({
            zNodes : null,
            urlPrefix : "${ctx}/admin/sys/organization/organization",
            onlyDisplayShow : true,
            async : true,
            asyncLoadAll : false,
            editable : false,
            containerId : "organizationTree",

            autocomplete : {
                enable : true
            },
            setting : {
                callback : {
                    onClick: onClick
                }
            }
        });

        $.zTree.initMovableTree({
            zNodes : null,
            urlPrefix : "${ctx}/admin/sys/organization/job",
//            onlyDisplayShow : true,
            async : true,
            asyncLoadAll : true,
            editable : false,
            containerId : "jobTree",
            autocomplete : {
                enable : true
            },
            setting : {
                callback : {
                    onClick: onClick
                }
            }
        });

        $(".scroll-pane").niceScroll({domfocus:true, hidecursordelay: 2000});

    });
</script>