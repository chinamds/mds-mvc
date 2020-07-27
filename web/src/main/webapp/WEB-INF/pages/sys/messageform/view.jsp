<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.aiotplayer.sys.model.MessageFolder" %>

<head>
	<title><fmt:message key="myMessageList.title"/></title>
</head>

<c:set var="group" value="grp_personal" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/myMessages/view.js"%>
</c:set>

<div class="col">
    <ul class="nav nav-tabs">
         <li role="presentation" class="nav-item active">
             <a class="nav-link" href="${ctx}/sys/myMessages/${m.id}?BackURL=<comm:BackURL/>">
                 <i class="fa fa-eye-open"></i>
                 <fmt:message key="myMessage.messageoperate.viewmessage"/>
             </a>
         </li>
        <li class="nav-item" role="presentation">
            <a class="nav-link" href="<comm:BackURL/>" class="btn btn-link">
                <i class="fa fa-reply"></i>
                <fmt:message key="button.done"/>
            </a>
        </li>
    </ul>

    <sys:showMessage/>

    <div class="panel-heading">
    	<%-- <h5><fmt:message key="myMessage.sendto">
        <fmt:param>
        <c:choose>
            <c:when test="${user.id eq m.sender.id}">
                <fmt:message key="myMessage.i"/>
            </c:when>
            <c:otherwise>
            	${m.sender.username}
            </c:otherwise>
        </c:choose>
        </fmt:param>
         <fmt:param>
        	${m.getToRecipients()}
        </fmt:param>
        </fmt:message>&nbsp;&nbsp;&nbsp;
        <span class="label label-info"><pretty:prettyTime date="${m.sendDate}"/></span></h5> --%>
        <h5><span class="bold span2"><strong><fmt:message key="myMessage.from"/></strong></span>&nbsp;&nbsp;
       	${m.sender.username}</h5>
       	<h5><span class="bold span2"><strong><fmt:message key="myMessage.date"/></strong></span>&nbsp;&nbsp;
       	<spring:eval expression="m.sendDate"/>&nbsp;&nbsp;<span class="label label-info"><pretty:prettyTime date="${m.sendDate}"/></span></h5>
       	<h5><span class="bold span2"><strong><fmt:message key="myMessage.to"/></strong></span>&nbsp;&nbsp;
       	${m.getToRecipients()}</h5>
        <h5><span class="bold span2"><strong><fmt:message key="myMessage.title"/></strong></span>&nbsp;&nbsp;
        ${m.title}</h5>
    </div>
    <br/>

    <div class="accordion mymessage">
        <c:forEach items="${messages}" var="message">
            <c:if test="${message.sendDate.time < m.sendDate.time}">
            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle bold no-underline" data-toggle="collapse" href="#collapse${message.id}">
                        ${message.title}
                        <span class="muted" style="float: right;padding-right: 20px;">
                            ${m.user.username}
                            &nbsp;&nbsp;&nbsp;
                            <spring:eval expression="message.sendDate"/>
                        </span>
                    </a>

                </div>
                <div id="collapse${message.id}" class="accordion-body collapse">
                    <div class="accordion-inner"></div>
                </div>
            </div>
            </c:if>
        </c:forEach>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a class="accordion-toggle bold no-underline" data-toggle="collapse" href="#collapse${m.id}">
                    ${m.title}
                    <span class="muted" style="float: right;padding-right: 20px;">
                        ${m.user.username}
                        &nbsp;&nbsp;&nbsp;
                        <spring:eval expression="m.sendDate"/>
                    </span>
                </a>

            </div>
            <div id="collapse${m.id}" class="accordion-body collapse in" data-loaded="true">
                <div class="accordion-inner">
                    <%@include file="viewContent.jsp"%>
                </div>
            </div>
        </div>

        <c:forEach items="${messages}" var="message">
            <c:if test="${message.sendDate.time > m.sendDate.time}">
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle bold no-underline" data-toggle="collapse" href="#collapse${message.id}">
                                ${message.title}
                                <span class="muted" style="float: right;padding-right: 20px;">
                                    ${m.user.username}
                                    &nbsp;&nbsp;&nbsp;
                                    <spring:eval expression="message.sendDate"/>
                                </span>
                        </a>

                    </div>
                    <div id="collapse${message.id}" class="accordion-body collapse">
                        <div class="accordion-inner"></div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>
</div>

