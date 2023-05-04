<%@ include file="/common/taglibs.jsp"%>
 <form id="searchForm" class="form-inline float-end">
	<div id="search" class="input-group">
		 <input type="text" size="20" name="q" id="query" value="${param.q}"
				   placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
		<span class="input-group-append">
			<button id="buttonSearch" class="btn btn-default btn-sm">
				<i class="fa fa-search"></i> <fmt:message key="button.search"/>
			</button>
		</span>
	</div>
</form>

