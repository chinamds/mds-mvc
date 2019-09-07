<%@ include file="/common/taglibs.jsp"%>
<div id="navmenu">
<div class="navbar navbar-expand-sm navbar-default navbar-light fixed-top navbar-fixed-top bg-light py-0" role="navigation">
	<a class="navbar-brand" href="<c:url value='/'/>"><fmt:message key="webapp.name"/></a>
    <button type="button" class="navbar-toggler navbar-toggle" aria-expanded="false" aria-controls="navbar" aria-label="Toggle navigation" data-toggle="collapse" data-target="#navbar">
        <span class="icon-bar navbar-toggler-icon"></span>
    </button>
     
   <%@ include file="/common/menu.jsp" %>
   <%-- <%@ include file="/common/userinfo.jsp" %> --%>
</div>
</div>

