<%@ include file="/common/taglibs.jsp"%>

<div class="table-responsive">
	<table id="table">
        <thead>
        <tr>
            <th data-field="state" data-checkbox="true"></th>
            <%-- <th data-field="id"
                data-align="center"
                data-formatter="actionFormatter"
                data-events="actionEvents"><fmt:message key="table.operation"/></th> --%>
            <th data-field="title" data-formatter="titleFormatter"><fmt:message key="myMessage.title"/></th>
            <c:if test="${state ne MessageFolder.outbox}">
	        <th data-field="senderName"><fmt:message key="myMessage.from"/></th>
	        </c:if>
	        <c:if test="${state ne MessageFolder.inbox}">
	        <th data-field="toRecipients"><fmt:message key="myMessage.to"/></th>
	        </c:if>
	        
	        <th data-field="sendDate" data-formatter="dateFormatter"><fmt:message key="myMessage.date"/></th>
        </tr>
        </thead>
    </table>
</div>
