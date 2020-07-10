<%@ taglib prefix="secure" uri="/WEB-INF/tlds/security.tld" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib uri="http://www.springmodules.org/tags/commons-validator" prefix="v" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page"%>
<%@ taglib uri="http://www.appfuse.org/tags/spring" prefix="appfuse" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sys" tagdir="/WEB-INF/tags/sys" %>
<%@ taglib prefix="pretty" tagdir="/WEB-INF/tags/pretty" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/cm" %>
<%@ taglib prefix="comm" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="ts" tagdir="/WEB-INF/tags/task" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="datePattern"><fmt:message key="date.format"/></c:set>
<c:if test = "${empty pageContext.request.locale.country}">
	<c:set var="languageTag" value="${pageContext.request.locale.language}"/>
</c:if>
<c:if test = "${not empty pageContext.request.locale.country}">
	<c:set var="languageTag" value="${pageContext.request.locale.language}_${pageContext.request.locale.country}"/>
</c:if>