<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="dictList.title"/></title>
    <meta name="menu" content="DictMenu"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/dict.js"%>
</c:set>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<div class="col">
<h2><fmt:message key="dictList.heading"/></h2>

<form method="get" action="${ctx}/sys/dicts" id="searchForm" class="form-inline">
	<div id="search" class="text-right">
		<div class="input-group">
		    <input type="text" size="20" name="q" id="query" value="${param.q}"
		               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
		    <span class="input-group-btn">
			  <button id="button.search" class="btn btn-default btn-sm" type="submit">
			  	<i class="fa fa-search"></i> <fmt:message key="button.search"/>
			  </button>
			  <button type="button" id="searchadv" class="btn btn-success btn-sm" data-tooltip="popover">
			    <span class="caret"></span>
			  </button>
			</span>
		</div>
	</div>
</form>

<p><fmt:message key="dictList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/sys/dictform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <button class="btn btn-danger" id="delete" name="delete">
        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
    <button id="btnImport" class="btn btn-success" type="button" title="<fmt:message key="button.import"/>">
        <i class="fa fa-file-import"></i> <span class="hidden-xs"><fmt:message key="button.import"/></span></button>
    <button id="btnExport" class="btn btn-info" type="button" title="<fmt:message key="button.export"/>">
        <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<div id="importBox" class="hidden d-none">
	<form id="importForm" method="post" enctype="multipart/form-data"
		class="form-horizontal"><br/>
		<div class="form-group">
			<label class="control-label col-sm-3"><fmt:message key="import.file"/></label>
			<div class="col-sm-9">
				<input name="importFile" id="uploadFile" type="file" class="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel">
				<p class="help-block"><fmt:message key="import.information" /></p><br/><br/>
			</div>
	        <div class="form-group">
            	<div class="col-sm-9 col-sm-offset-3">
					<a href="${ctx}/sys/menus/import/template"><fmt:message key="button.downloadtemplate"/></a>
				</div>
			</div>
		</div>
	</form>
</div>

<div id="advanceSearchBox" class="hidden d-none">
	<form id="advanceSearchForm" modelAttribute="dictParam" method="post" action="${ctx}/sys/dicts/advancesearch" class="form-horizontal">
		  <div class="form-group">
			<label for="category" class="col-sm-4 control-label small"><fmt:message key="dict.category"/></label>
			<div class="col-sm-8">
				<select id="category" name="category" class="form-control input-sm">
	                <c:forEach items="${categories}" var="cat">
	                	<option value="${cat}" ${cat eq dictParam.category ? 'selected' : ''}>${cat}</option>
	                </c:forEach>
		        </select>
	        </div>
	      </div>
		  <div class="form-group">
			<label for="word" class="col-sm-4 control-label small"><fmt:message key="dict.word"/></label>
			<div class="col-sm-8">
				<input type="text" id="word" name="word" maxlength="50" value="${dictParam.word}" class="form-control input-sm"/>
			</div>
		  </div>
		  <div class="form-group">
		  	  <div class="col-sm-8 col-sm-offset-4">
			  	<input id="btnSubmit" class="btn btn-primary btn-sm" type="submit" value="<fmt:message key="button.searchadv"/>"/>
			  </div>
		 </div>
	</form>
</div>

<div class="table-responsive">
	<table id="table">
        <thead>
        <tr>
            <th data-field="state" data-checkbox="true"></th>
            <th data-field="word" data-formatter="wordFormatter"><fmt:message key="dict.word"/></th>
	        <th data-field="category"><fmt:message key="dict.category"/></th>
	        <th data-field="value"><fmt:message key="dict.value"/></th>          
	        <th data-field="sort"><fmt:message key="dict.sort"/></th>
	        <th data-field="description"><fmt:message key="dict.description"/></th>
        </tr>
        </thead>
    </table>
</div>
</div>