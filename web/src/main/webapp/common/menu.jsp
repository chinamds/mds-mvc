<%@ include file="/common/taglibs.jsp"%>

 <!-- display menu for mobile device, handle by MenuRepositoryFilter 10/12/2017 -->
<!-- display menu for desktop device -->
<div class="collapse navbar-collapse" id="navbar">
<ul class="nav navbar-nav mr-auto mt-2 mt-lg-0">
    <c:if test="${empty pageContext.request.remoteUser}">
        <c:if test="${not empty fns:getFilingNo()}">
	        <li class="nav-item">
	            <a class="nav-link" href="http://beian.miit.gov.cn"  target="_blank">${fns:getFilingNo()}</a>
	        </li>
        </c:if>
        <li class="nav-item active">
            <a class="nav-link" href="<c:url value="/login"/>"><fmt:message key="login.title"/></a>
        </li>
    </c:if>
	<c:if test="${userMenuRepository != null}">
	    <c:forEach items="${userMenuRepository.topMenus}" var="menu">
	    	<comm:navbarMenu menu="${menu}" />
	    </c:forEach>
    </c:if>
</ul>
<form class="navbar-form navbar-right form-inline shadow-sm d-flex ms-auto" id="userinfo">
	<div id="user-navbar" class="navbar navbar-default navbar-light my-0 py-0" role="navigation">
		<ul class="nav navbar-nav btn-group">
        <c:if test="${not empty pageContext.request.remoteUser}">
          <li class="nav-item btn">
		   <a class="nav-link btn-view-info" data-placement="bottom" title="<fmt:message key="menu.personaltitle"/>">
			   ${pageContext.request.remoteUser}<fmt:message key="menu.welcome"/>
		   </a>
		  </li>
		  <li class="nav-item btn" data-placement="bottom" title="<fmt:message key="menu.mynotifications"/>">
		   <a class="nav-link btn-notification" role="button" data-bs-toggle="popover">
			   <i class="icon-large fa fa-envelope fa fa-volume-up"></i>
		   </a>
		  </li>
		  <div class="notification-list popover fade bottom d-none" role="tooltip">
	             <div class="content">
				   <div class="loading">
					   <div class="popover-title title popover-header">
						   <a class="btn btn-link no-padding view-all-notification"><i class="fa fa-table"></i> <fmt:message key="menu.viewallnotifications"/></a>
						   <span class="float-end"><a class="btn btn-link no-padding close-notification-list"><i class="fa fa-times"></i></a></span>
					   </div>
					   <div class="popover-content list popover-body">
						   <img src="${ctx}/static/images/loading.gif" width="20px">&nbsp;&nbsp;&nbsp; <fmt:message key="menu.loading"/>
					   </div>
				   </div>
				   <div class="no-comment">
					   <div class="popover-title title popover-header">
						   <a class="btn btn-link no-padding view-all-notification"><i class="fa fa-table"></i> <fmt:message key="menu.viewallnotifications"/></a>
						   <span class="float-end"><a  class="btn btn-link no-padding close-notification-list"><i class="fa fa-times"></i></a></span>
					   </div>
					   <div class="popover-content list popover-body">
						   <i class="icon-comment fa fa-comment"></i>&nbsp;&nbsp;&nbsp;<fmt:message key="menu.nonewnotifications"/>
					   </div>
				   </div>
				   <div class="detail">
					   <div class="popover-title title popover-header">
						   <a class="btn btn-link no-padding back-notification-list"><i class="fa fa-reply"></i> <fmt:message key="menu.backtonotificationlist"/></a>
						   <span class="float-end"><a class="pre">&lt; <fmt:message key="menu.prev"/></a> | <a class="next"><fmt:message key="menu.next"/> &gt;</a></span>
					   </div>
					   <div class="popover-content list popover-body">
					   </div>
				   </div>
			
				   <div class="menu">
					   <div class="popover-title title popover-header">
						   <a class="btn btn-link no-padding view-all-notification"><i class="fa fa-table"></i> <fmt:message key="menu.viewallnotifications"/></a>
						   <span class="float-end"><a  class="btn btn-link no-padding close-notification-list"><i class="fa fa-times"></i></a></span>
					   </div>
					   <div class="popover-content list popover-body">
					   </div>
				   </div>
				   <div class="clearfix"></div>
			    </div>
		  </div>
		  <li class="nav-item btn" data-placement="bottom" title="<fmt:message key="menu.mymessage"/>">
		   <a class="nav-link btn-message">
			   <i class="icon-large far fa-envelope icon-message"></i>
		   </a>
		  </li>
		  <li class="nav-item btn dropdown">
		   <a class="nav-link dropdown-toggle" data-bs-toggle="dropdown" data-hover="dropdown" aria-haspopup="true" aria-expanded="false">
			   <i class="icon-large fas fa-cogs"></i>
		   </a>
		   <ul class="dropdown-menu dropdown-menu-right">
			   <li>
				   <a class="dropdown-item btn-view-info">
					   <i class="far fa-user"></i>
					   <fmt:message key="userProfile.title"/>
				   </a>
			   </li>
			   <li>
				   <a class="dropdown-item btn-change-password">
					   <i class="fas fa-key"></i>
					   <fmt:message key="updatePassword.title"/>
				   </a>
			   </li>
		  </ul>
		  </li>
		  <li class="nav-item btn">
		   <a class="nav-link" href="${ctx}/logout" data-placement="bottom" title="<fmt:message key="user.logout"/>"><i class="icon-large fas fa-sign-out-alt"></i></a>
		  </li>
		  </c:if>
		  <li class="nav-item btn dropdown">
			   <a class="nav-link dropdown-toggle" data-bs-toggle="dropdown" data-hover="dropdown" aria-haspopup="true" aria-expanded="false">
				   <i class="icon-large fas fa-globe"></i>
		    		${fns:getLanguageName(pageContext.request)}
			   </a>
			   <ul class="dropdown-menu dropdown-menu-right">
				    <c:forEach items="${fns:getCultures()}" var="culture">
					    <li>
					       <a class="dropdown-item" href="#" onclick="location='<c:url value='/?locale=${culture.cultureCode}'/>'">${culture.cultureName}</a>
					    </li>
				    </c:forEach>
			   </ul>
		  </li>
		</ul>
    </div>
</form>
</div>

