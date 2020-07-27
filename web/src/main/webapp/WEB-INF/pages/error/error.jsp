<%@ page language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="java.io.PrintWriter" %>

<!DOCTYPE html>

<html>
<head>
    <title><fmt:message key="errorPage.title"/></title>
</head>
<body id="error">
    <div class="container">
        <h1><fmt:message key="errorPage.heading"/></h1>
        <%@ include file="/common/messages.jsp" %>

        <p><fmt:message key="errorPage.message"/></p>
        <!--
	    <%
	    Throwable ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
	    if(ex == null) out.println("No stack trace available<br/>");
	    else {
	                for(Throwable t = ex ; t!=null; t = t.getCause())
	                {
	                    out.println(t.getMessage());
	                    out.println("=============================================");
	                    t.printStackTrace(new PrintWriter(out));
	                    out.println("\n\n\n");
	                }
	        }
        %>
      -->
    </div>
</body>
</html>
