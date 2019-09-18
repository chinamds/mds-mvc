<%@ include file="/common/taglibs.jsp"%>
<p style="text-align: center;margin:0">
    <c:if test="${not empty fns:getFilingNo()}">
        <a href="http://beian.miit.gov.cn"  target="_blank">${fns:getFilingNo()}</a> 
    </c:if> 
    <fmt:message key="webapp.fullname"/>&nbsp;<fmt:message key="webapp.version"/> &copy; <fmt:message key="copyright.year"/>&nbsp;<a href="<fmt:message key="webapp.url"/>"><fmt:message key="webapp.company"/></a>
</p>