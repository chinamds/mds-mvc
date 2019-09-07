<script type="text/javascript">
    $(function () {

        <c:choose>
            <c:when test="${op eq 'delete'}">
                //no need validate when delete, and readonly form
                $.app.readonlyForm($("#editForm"), false);
                $.sys.organization.removeOrganizationBtn();
            </c:when>
            <c:when test="${op eq 'view'}">
                $.app.readonlyForm($("#editForm"), true);
                $.sys.organization.removeOrganizationBtn();
            </c:when>
            <c:otherwise>
                $.sys.user.initValidator($("#editForm"));
                <es:showFieldError commandName="m"/>
            </c:otherwise>
        </c:choose>

        $.sys.organization.initSelectForm("organizationId", "jobId");

    });
</script>