<script type="text/javascript">
    if ($.cookie("username") != null && $.cookie("username") != "") {
        $("#username").val($.cookie("username"));
        $("#password").focus();
    } else {
        $("#username").focus();
    }
    
    if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0 || $('.modal-dialog').length > 0){
    	top.location = "${ctx}";
    }
    $("#mobileDevice").val(window.Mds.isWidthLessThan(991));
    
    $(".jcaptcha-btn").click(function() {
        var img = $(".jcaptcha-img");
        var imageSrc = img.attr("src");
        if(imageSrc.indexOf("?") > 0) {
            imageSrc = imageSrc.substr(0, imageSrc.indexOf("?"));
        }
        imageSrc = imageSrc + "?" + new Date().getTime();
        img.attr("src", imageSrc);
    });
    
    function saveUsername(theForm) {
        $.cookie("username",theForm.username.value, { expires: 30, path: "<c:url value="/"/>"});
    }
    
    function validateForm(form) {                                                               
        var valid = validateRequired(form);
        if (valid == false) {
            $(".form-group").addClass('error');
        }
        <c:if test="${jcaptchaEnabled}">
        $.ajax({
			type: "GET",
			async: false,
			url: "${ctx}/jcaptcha-validate?fieldId=jcaptchaCode&fieldValue=" + $("#jcaptchaCode").val(),
			dataType : "json",
			success: function (data) {
				//alert(data);
				if (!data || data.length < 2 || data[1] == 0){
					alert("<fmt:message key="login.verificationcode.error"/>");
					valid=false;
				}
			},
			error: function (response) {
				alert(response.responseText);
				valid=false;
			}
		});
        if (valid == false) {
            $("#jcaptchaCode").focus();
        }
        </c:if>       
        return valid;
    }

    function passwordHint() {
        if ($("#username").val().length == 0) {
            alert("<fmt:message key="errors.required"><fmt:param><fmt:message key="label.username"/></fmt:param></fmt:message>");
            $("#username").focus();
        } else {
            //location.href="<c:url value="/sys/passwordHint"/>?username=" + $("#username").val();
        	$.ajax({
     			type: "post",
     			async: true,
     			url: "${ctx}/sys/passwordHint/send?username="  + $("#username").val(),
     			dataType : "json",
     			success: function (data) {
   					$.mdsShowResult(data, "<fmt:message key="login.passwordRetrieval"/>");
     			},
     			error: function (response) {
     				alert(response.responseText);
     			}
     		});
        }
    }
    
    function requestRecoveryToken() {
        if ($("#username").val().length == 0) {
            alert("<fmt:message key="errors.required"><fmt:param><fmt:message key="label.username"/></fmt:param></fmt:message>");
            $("#username").focus();
        } else {
            //location.href="<c:url value="/sys/requestRecoveryToken"/>?username=" + $("#username").val();
        	/*var url = "${ctx}/sys/requestRecoveryToken/send?username="  + $("#username").val();
        	$.post(url, function(result) {
        		$.mdsShowResult(result, "<fmt:message key="login.passwordRetrieval"/>");
            }, 'json');*/
        	$.ajax({
     			type: "post",
     			async: true,
     			url: "${ctx}/sys/requestRecoveryToken/send?username="  + $("#username").val(),
     			dataType : "json",
     			success: function (data) {
   					$.mdsShowResult(data, "<fmt:message key="login.passwordRetrieval"/>");
     			},
     			error: function (response) {
     				alert(response.responseText);
     			}
     		});
        }
    }    
    
    function required () { 
        this.aa = new Array("username", "<fmt:message key="errors.required"><fmt:param><fmt:message key="label.username"/></fmt:param></fmt:message>", new Function ("varName", " return this[varName];"));
        this.ab = new Array("password", "<fmt:message key="errors.required"><fmt:param><fmt:message key="label.password"/></fmt:param></fmt:message>", new Function ("varName", " return this[varName];"));
        <c:if test="${jcaptchaEnabled}">        
        this.ac = new Array("jcaptchaCode", "<fmt:message key="errors.required"><fmt:param><fmt:message key="label.verificationcode"/></fmt:param></fmt:message>", new Function ("varName", " return this[varName];"));
        </c:if> 
    } 
</script>
