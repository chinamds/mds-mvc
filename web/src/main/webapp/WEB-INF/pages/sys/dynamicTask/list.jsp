<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="myMessageList.title"/></title>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/dynamicTask/list.js"%>
</c:set>

<div data-table="table" class="panel">

    <ul class="nav nav-tabs">
        <li ${empty param['search.start_eq'] ? 'class="active"' : ''}>
            <a href="${ctx}/admin/maintain/dynamicTask">
                <i class="fa fa-table"></i>
                所有任务列表
            </a>
        </li>
        <li ${param['search.start_eq'] eq 'true' ? 'class="active"' : ''}>
            <a href="${ctx}/admin/maintain/dynamicTask?search.start_eq=true">
                <i class="fa fa-table"></i>
                已启动任务列表
            </a>
        </li>
        <li ${param['search.start_eq'] eq 'false' ? 'class="active"' : ''}>
            <a href="${ctx}/admin/maintain/dynamicTask?search.start_eq=false">
                <i class="fa fa-table"></i>
                未启动任务列表
            </a>
        </li>
    </ul>

    <es:showMessage/>

    <div class="row-fluid tool ui-toolbar">
        <div class="span4">
            <div class="btn-group">
                <secure:hasPermission name="maintain:dynamicTask:create">
                <a class="btn btn-create">
                    <i class="icon-file-alt"></i>
                    新增
                </a>
                </secure:hasPermission>
                <secure:hasPermission name="maintain:dynamicTask:update">
                <a id="update" class="btn btn-update">
                    <i class="fa fa-edit"></i>
                    修改
                </a>
                </secure:hasPermission>
                <secure:hasPermission name="maintain:dynamicTask:delete">
                <a class="btn btn-custom btn-delete">
                    <i class="fa fa-trash"></i>
                    删除
                </a>
                </secure:hasPermission>

                <secure:hasPermission name="maintain:dynamicTask:update">
                    <a class="btn btn-custom btn-start">
                        <i class="fa fa-trash"></i>
                        启动任务
                    </a>
                </secure:hasPermission>

                <secure:hasPermission name="maintain:dynamicTask:update">
                    <a class="btn btn-custom btn-stop">
                        <i class="fa fa-trash"></i>
                        停止任务
                    </a>
                </secure:hasPermission>
            </div>
        </div>
        <div class="span8">
        </div>
    </div>
    <%@include file="listTable.jsp"%>
</div>