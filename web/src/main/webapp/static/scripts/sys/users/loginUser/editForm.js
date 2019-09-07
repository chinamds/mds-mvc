<script type="text/javascript">
    $(function () {
        $("#editForm").find(":checkbox,:radio").filter(":not(:checked)").parent("label").andSelf().remove();
        $("#username,#dateAdded,#enabled,#accountExpired,#accountLocked,#credentialsExpired").attr("disabled", true);
        <c:choose>
        <c:when test="${op eq 'userProfile.viewprofile'}">
        readonlyForm($("#editForm"), true);
        </c:when>
        <c:when test="${op eq 'userProfile.editprofile'}">
        //$.sys.user.initValidator($("#editForm"));
        //<es:showFieldError commandName="m"/>
        </c:when>
        </c:choose>

    });
</script>
