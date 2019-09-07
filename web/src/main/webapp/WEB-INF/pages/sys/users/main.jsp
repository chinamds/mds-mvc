<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="userProfile.title"/></title>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/users/main.js"%>
</c:set>

<style type="text/css">
    .ui-layout-north {
        display:	none;
        overflow:	hidden;
    }

    .ui-layout-toggler{
        background-color: #0099ff;
        opacity: 1;

    }
    .ui-layout-resizer {
        opacity: 1;
        filter:none;
    }
    .ui-layout-toggler .content {
        color: #fff;
        line-height: 18px;
    }
</style>
<div class="ui-layout-north tree" style="overflow: hidden">
    <iframe id="treeFrame" name="treeFrame" width="100%" height="100%"
            style="overflow: hidden"
            frameborder="0" scrolling="no"
            src="about:blank" longdesc="${ctx}/admin/sys/users/tree"></iframe>
</div>
<div class="ui-layout-center tree">
    <iframe id="listFrame" name="listFrame" width="100%" height="100%"  frameborder="0" scrolling="auto"
             src="${ctx}/admin/sys/users/0/0"></iframe>
</div>