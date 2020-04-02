<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.sys.model.MessageOperate" %>

<head>
	<title><fmt:message key="myMessageList.title"/></title>
</head>

<c:set var="group" value="grp_editor" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/myMessages/sendForm.js"%>
</c:set>

<div class="col">
<ul class="nav nav-tabs">
    <c:if test="${op eq MessageOperate.drafts}">
        <li class="nav-item active" role="presentation">
            <a class="nav-link" href="${ctx}/sys/myMessages/draft/${m.id}/send?BackURL=<comm:BackURL/>">
                <i class="${op.icon}"></i>
                <fmt:message key="${op.info}"/>
            </a>
        </li>
    </c:if>
    <c:if test="${op eq MessageOperate.newmessage}">
        <li class="nav-item active" role="presentation">
            <a class="nav-link" href="${ctx}/sys/myMessages/send?BackURL=<comm:BackURL/>">
                <i class="${op.icon}"></i>
                <fmt:message key="${op.info}"/>
            </a>
        </li>
    </c:if>
    <c:if test="${op eq MessageOperate.reply}">
        <li class="nav-item active" role="presentation">
            <a class="nav-link" href="${ctx}/sys/myMessages/${original.id}/reply?BackURL=<comm:BackURL/>">
                <i class="${op.icon}"></i>
                <fmt:message key="${op.info}"/>
            </a>
        </li>
    </c:if>
    <c:if test="${op eq MessageOperate.forward}">
        <li class="nav-item active" role="presentation">
            <a class="nav-link" href="${ctx}/sys/myMessages/${original.id}/forward?BackURL=<comm:BackURL/>">
                <i class="${op.icon}"></i>
                    <fmt:message key="${op.info}"/>
            </a>
        </li>
    </c:if>
    <li class="nav-item" role="presentation">
        <a class="nav-link" href="<comm:BackURL/>" class="btn btn-link">
            <i class="fa fa-reply"></i>
            <fmt:message key="button.done"/>
        </a>
    </li>
</ul>

<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>   
<form:form id="myMessageForm" method="post" modelAttribute="m" cssClass="form-horizontal" onsubmit="return validateMyMessage(this)">
 <comm:BackURL hiddenInput="true"/>

 <sys:showGlobalError modelAttribute="m"/>

 <form:hidden path="id"/>
 <form:hidden path="content.id"/>
 <%-- <form:hidden path="parentId"/>
 <form:hidden path="parentIds"/> --%>
 <%-- <form:hidden path="myMessageRecipients"/> --%>
 <form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>

	<c:if test="${op eq MessageOperate.reply}">
	    <div class="form-group">
	    	<h5><span class="bold span2"><strong><fmt:message key="myMessage.from"/></strong></span>&nbsp;&nbsp;
        	${original.sender.username}</h5>
        	<h5><span class="bold span2"><strong><fmt:message key="myMessage.date"/></strong></span>&nbsp;&nbsp;
        	<spring:eval expression="original.sendDate"/>&nbsp;&nbsp;<span class="label label-info"><pretty:prettyTime date="${original.sendDate}"/></span></h5>
        	<h5><span class="bold span2"><strong><fmt:message key="myMessage.to"/></strong></span>&nbsp;&nbsp;
        	${original.getToRecipients()}</h5>
	        <%-- <span class="strong span2"><pretty:prettyTime date="${original.sendDate}"/></span>
	        <fmt:message key="myMessage.sendto">
	        <fmt:param>
	        <c:choose>
	            <c:when test="${user.id eq original.sender.id}">
	                <fmt:message key="myMessage.i"/>
	            </c:when>
	            <c:otherwise>
	            	${original.sender.username}
	            </c:otherwise>
	        </c:choose>
	        </fmt:param>
	         <fmt:param>
	        	${original.getToRecipients()}
	        </fmt:param>
	        </fmt:message><br/>
	     <br/> --%>
	     <div class="accordion message">
	         <div class="accordion-group">
	             <div class="accordion-heading">
	                 <a class="accordion-toggle bold no-underline" data-toggle="collapse" href="#collapse${original.id}">
	                         ${original.title}
	                  <span class="muted" style="float: right;padding-right: 20px;">
	                      ${original.user.username}
	                      &nbsp;&nbsp;&nbsp;
	                      <spring:eval expression="original.sendDate"/>
	                  </span>
	                 </a>
	             </div>
	             <div id="collapse${original.id}" class="accordion-body collapse in">
	                 <div class="accordion-inner">
	                         ${fns:unescapeHtml(original.content.content)}
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
	<br/>
	</c:if>

    <c:if test="${op eq MessageOperate.newmessage or op eq MessageOperate.forward or op eq MessageOperate.drafts or op eq MessageOperate.reply}">
	    <%-- <spring:bind path="m.myMessageRecipients">
	    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
		</spring:bind>       --%>
		<div class="form-group">  
		   	<appfuse:label key="myMessage.to" styleClass="col-sm-2 control-label"/>
		      <div id="search-recipient" class="col-sm-10">
		      	<%-- <form:hidden path="myMessageRecipients"/> --%>
		      	<select class="form-control" id="recipients" name="recipients" multiple="multiple">
		    	<c:forEach items="${m.myMessageRecipients}" var="recipient">
		  	<option selected="selected" value="${recipient.recipientId}">${recipient.recipientName}</option>
		  </c:forEach>
		</select>
	        	<%-- <c:set var="recipient" value="${not empty recipient ? recipient : param.recipient}"/>
	             <select class="form-control" id="recipientId_msg" name="recipient" multiple="multiple" />
	             <form:hidden path="myMessageRecipients"/> --%>
	      	<%-- <form:errors path="myMessageRecipients" cssClass="help-block"/> --%>
	      </div>
	  </div>
    </c:if>
    
 	<spring:bind path="m.title">
 	<div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
	</spring:bind>        
	 	<appfuse:label key="myMessage.title" styleClass="col-sm-2 control-label"/>
	     <div class="col-sm-10">
	     	<fmt:message key="myMessage.title.tip" var="titletip"/>
	     	<form:input cssClass="form-control" path="title" id="title" placeholder='${titletip}' />
	     	<form:errors path="title" cssClass="help-block"/>
	     </div>
	 </div>
 
	 <spring:bind path="m.content.content">
	 <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
	</spring:bind>        
	 	<appfuse:label key="myMessage.content" styleClass="col-sm-2 control-label"/>
	     <%-- <div class="col-sm-10">
	     	<form:textarea id="content" path="content.content" cssClass="form-control" data-promptPosition="topRight:-200"/>
	     	<form:errors path="content.content" cssClass="help-block"/>
	     </div> --%>
	     <div class="col-sm-10">
	   <!-- <textarea rows="20" id="testcontent" class="form-control"></textarea> -->
	    <input type="hidden" id="textcontent" name="textcontent" value="${m.content.content}"/>
	   <textarea name="text" class="summernote" id="contents" title="Contents">${m.content.content}</textarea>
	  </div>
	 </div>
 
    <div class="form-group">
    	<div class="col-sm-10 col-sm-offset-2">
        <button type="submit" class="btn btn-primary" id="send" name="send">
            <i class="fa fa-paper-plane"></i>
                <fmt:message key="myMessage.button.send"/>
        </button>
        <button type="submit" class="btn btn-default btn-save-draft" id="draft" name="draft">
            <i class="fa fa-save"></i>
            <fmt:message key="myMessage.button.saveasdrafts"/>
        </button>
        </div>
    </div>
</form:form>

</div>
    
<v:javascript formName="myMessage" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>
