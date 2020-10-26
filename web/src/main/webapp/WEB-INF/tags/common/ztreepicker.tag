<%@ tag language="java" pageEncoding="UTF-8"%>
<%-- <%@ include file="/common/tagcommlibs.jsp"%> --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>
<%@ tag import="com.mds.aiotplayer.util.StringUtils" %>

<%@ attribute name="id" type="java.lang.String" required="true" description="Id"%>
<%@ attribute name="keyName" type="java.lang.String" required="true" description="hidden field（ID）"%>
<%@ attribute name="keyValue" type="java.lang.String" required="true" description="hidden field value（ID）"%>
<%@ attribute name="fieldName" type="java.lang.String" required="true" description="input field（Name）"%>
<%@ attribute name="fieldValue" type="java.lang.String" required="true" description="input file name（Name）"%>
<%@ attribute name="url" type="java.lang.String" required="true" description="data URL"%>
<%@ attribute name="checked" type="java.lang.Boolean" required="false" description="show zTree checkbox" %>
<%@ attribute name="extId" type="java.lang.String" required="false" description="except node(key)" %>
<%@ attribute name="allowSelectRoot" type="java.lang.Boolean" required="false" description="allow select root node"%>
<%@ attribute name="allowSelectParent" type="java.lang.Boolean" required="false" description="allow select parent node"%>
<%@ attribute name="allowClear" type="java.lang.Boolean" required="false" description="allow clear"%>
<%@ attribute name="allowInput" type="java.lang.Boolean" required="false" description="allow input"%>
<%@ attribute name="cssClass" type="java.lang.String" required="false" description="css class"%>
<%@ attribute name="cssStyle" type="java.lang.String" required="false" description="css style"%>
<%@ attribute name="smallBtn" type="java.lang.Boolean" required="false" description="small button"%>
<%@ attribute name="hideBtn" type="java.lang.Boolean" required="false" description="Hide select button"%>
<%@ attribute name="disabled" type="java.lang.String" required="false" description="disable zTree Picker widget"%>
<%@ attribute name="dataMsgRequired" type="java.lang.String" required="false" description=""%>
<%@ attribute name="maxlength" type="java.lang.String" required="false" description="max length for text input"%>
<%@ attribute name="selectChanged" type="java.lang.String" required="false" description="event for select changed"%>

<c:if test="${empty allowSelectRoot}"><c:set var="allowSelectRoot" value="false"/></c:if>
        
<div class="input-group dropdown">
 	<%-- <input id="${id}Id" name="${keyName}" class="${cssClass}" type="hidden" value="${keyValue}"/> --%>
	<input id="${id}Name" name="${fieldName}" ${allowInput?'':'readonly="readonly"'} type="text" value="${fns:unescapeHtml(fieldValue)}" data-msg-required="${dataMsgRequired}"
		class="${cssClass}" style="${cssStyle}" maxlength="${not empty maxlength?maxlength:50}"/>
	<span class="input-group-append">
		<a id="${id}Button" type="button" href="javascript:" class="btn btn-outline-secondary dropdown-toggle ${disabled} ${hideBtn ? 'hidden d-none' : ''}" data-toggle="dropdown"></a>
	</span>
</div>  
 
<%
	String scripts = "<script>\n" +
			"(function ($) {\n" +
			"	$(document).ready(function () {\n" +
			"		var zTreeId = $.zTree.initSelectTree({\n" +
			"			zNodes : [],\n" +
			"			nodeType : 'default',\n" +
			"			fullName: true,\n" +
			"			loadUrl : '{0}',\n" +
			"			async : true,\n" +
			"			asyncLoadAll : true,\n" +
			"			onlyDisplayShow: false,\n" +
			"			lazy : true,\n" +
			"			selectChanged: {3},\n" +
			"			select : {\n" +
			"				btn : $('#{1}Button, #{1}Name'),\n" +
			"				id : '{1}Id',\n" +
			"				name : '{1}Name',\n" +
			"				btnId : '{1}Button',\n" +
			"				includeRoot: {2}\n" + 
			"			},\n" +
			"			autocomplete : {\n" +
			"				enable : false\n" +
			"			}\n" +
			"		});\n" +
			"	});\n" +
			"})(jQuery);\n" +
			"</script>\n";
 
	String includeRoot="false";
	if (allowSelectRoot != null && allowSelectRoot == true){
		includeRoot="true";
	}
	String selectEvent="null";
	if (StringUtils.isNotBlank(selectChanged)){
		selectEvent = selectChanged;
	}
	scripts = StringUtils.format(scripts, request.getContextPath() + url, id, includeRoot, selectEvent); 
	if (request.getAttribute("scripts") == null){
		request.setAttribute("scripts", scripts);
	}else{
		request.setAttribute("scripts", (request.getAttribute("scripts") + "\n" + scripts));
	}
	//request.setAttribute("scripts", request.getAttribute("scripts") == null ? scripts : (request.getAttribute("scripts") + "\n" + scripts));
				
	//out.write(scripts);
%>