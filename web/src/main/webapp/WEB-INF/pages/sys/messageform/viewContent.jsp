<%@ include file="/common/taglibs.jsp"%>
${fns:unescapeHtml(m.getContent().content)}
<div style="padding: 20px 0">
    <c:if test="${m.messageFolder ne MessageFolder.junk and m.messageFolder ne MessageFolder.deleted and  m.messageFolder ne MessageFolder.drafts}">
        <a class="btn" href="${ctx}/sys/messageform/${m.id}/reply"><fmt:message key="myMessage.messageoperate.reply"/></a>
        <a class="btn" href="${ctx}/sys/messageform/${m.id}/forward"><fmt:message key="myMessage.messageoperate.forward"/></a>
    </c:if>
    
    <c:if test="${m.messageFolder ne MessageFolder.drafts}">
    	<a class="btn btn-view-archive" data-href="${ctx}/sys/messageform/batch/archive?ids=${m.id}"><fmt:message key="myMessage.messageoperate.archive"/></a>
    	<a class="btn btn-view-delete" data-href="${ctx}/sys/messageform/batch/delete?ids=${m.id}"><fmt:message key="myMessage.messageoperate.delete"/></a>
    	<a class="btn btn-view-move" data-href="${ctx}/sys/messageform/batch/move?ids=${m.id}"><fmt:message key="myMessage.messageoperate.move"/></a>
    </c:if>
    <c:if test="${m.messageFolder eq MessageFolder.drafts}">
    	<a class="btn btn-view-discard" data-href="${ctx}/sys/messageform/draft/${m.id}/discard"><fmt:message key="myMessage.messageoperate.discard"/></a>
    	<a class="btn btn-view-send" data-href="${ctx}/sys/messageform/batch/archive?ids=${m.id}"><fmt:message key="myMessage.messageoperate.send"/></a>
    </c:if>
</div>

