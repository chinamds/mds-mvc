<%@ tag pageEncoding="UTF-8" description="show global error message" %>
<%@ attribute name="modelAttribute" type="java.lang.String" required="true" description="model target mame" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:hasBindErrors name="${modelAttribute}">
  <c:if test="${errors.globalErrorCount > 0}">
  <div class="alert alert-error">
  <button type="button" class="close" data-dismiss="alert">&times;</button>
  <c:forEach items="${errors.globalErrors}" var="error">
      <spring:message var="message" code="${error.code}" arguments="${error.arguments}" text="${error.defaultMessage}"/>
      <c:if test="${not empty message}">
          ${message}<br/>
      </c:if>
  </c:forEach>
  </div>
  </c:if>
</spring:hasBindErrors>
