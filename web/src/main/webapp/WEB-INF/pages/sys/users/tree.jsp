<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="userProfile.title"/></title>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/users/tree.js"%>
</c:set>

<style>
    .scroll-pane {
        width: 384px;
        height: 100%;
        overflow: auto;
        float: left;
    }
    #organizationTree {
        margin-right: 30px;
    }
</style>

<div id="organizationTree" class="scroll-pane"></div>

<div id="jobTree" class="scroll-pane"></div>