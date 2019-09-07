<%@ include file="/common/taglibs.jsp"%>

<%@ page import="com.mds.core.ResourceId" %>
<%@ page import="com.mds.util.Utils" %>

<head>
    <title><fmt:message key="task.synch.Page_Title"/></title>
    <meta name="menu" content="AddContentMenu"/>
    <meta name="container" content="galleryView"/>
    <meta name="mdsClientId" content="${galleryView.mdsClientId}"/>
    <meta name="heading" content="<fmt:message key='task.synch.heading'/>"/>
</head>

<c:set var="group" value="grp_layout" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/contentobjects/synchronize.js" %>
</c:set>

<div class="col-sm-12 mds_content">
  <%@ include file="/common/messages.jsp" %>
	
	<form id="synchForm">
		<input type="hidden" id="gid"  name="gid" value="<c:out value="${galleryId}"/>"/>
		<input type="hidden" id="albumId"  name="albumId" value="<c:out value="${albumId}"/>"/>
		<input type="hidden" id="moid"  name="moid" value="<c:out value="${param.moid}"/>"/>
		<fmt:message key="task.synch.Header_Text" var="taskHeader"/>
		<fmt:message key="task.synch.Body_Text" var="taskBody"/>
		<fmt:message key="task.synch.Ok_Button_Text" var="task_Ok_Button_Text"/>
		<fmt:message key="task.synch.Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
		
		<ts:taskheader taskHeader="${taskHeader }" taskBody="" task_Ok_Button_Text="${task_Ok_Button_Text}" />
		  <div class="mds_addleftpadding1">
		    <p class="mds_h3">
		      <fmt:message key="task.synch.Album_Title_Prefix_Text" />&nbsp;${albumTitle}
		    </p>
		    <hr />
		    <p class="mds_textcol">
		      <label ID="lblInstructions" ><fmt:message key="task.synch.Body_Text" /></label>
		    </p>
		    <div class="mds_addleftpadding6">
		      <p>
<%-- 		      	<div class="custom-control custom-checkbox">
				  <input type="checkbox" class="custom-control-input" id="chkIncludeChildAlbums">
				  <label class="custom-control-label" for="chkIncludeChildAlbums"><fmt:message key="task.synch.IncludeChildAlbums_Text" /></label>
				  <label ID="lblIncludeChildAlbums" ></label>
				</div> --%>
		        <input id="chkIncludeChildAlbums" type="checkbox" /><label for="chkIncludeChildAlbums">&nbsp;<fmt:message key="task.synch.IncludeChildAlbums_Text" />
		        </label>
		        <label ID="lblIncludeChildAlbums" ></label>
		      </p>
		      <p>
		      	<%-- <div class="form-group checkbox">
               <label>
                   <input type="checkbox" id="chkOverwriteThumbnails"><fmt:message key="task.synch.OverwriteThumbnails_Text" />
               </label>
              </div>  --%>
		        <input id="chkOverwriteThumbnails" type="checkbox" /><label for="chkOverwriteThumbnails">&nbsp;<fmt:message key="task.synch.OverwriteThumbnails_Text" />
		        </label>
		        <label ID="lblOverwriteThumbnails" ></label>
		      </p>
		      <p>
		        <input id="chkOverwriteCompressed" type="checkbox" /><label for="chkOverwriteCompressed">&nbsp;<fmt:message key="task.synch.OverwriteCompressed_Text" />
		        </label>
		        <label ID="lblOverwriteCompressed" ></label>
		      </p>
		    </div>
		    <div id="${galleryView.mdsClientId}_mds_sync_sts" class="mds_sync_sts mds_single_tab">
		      <div class="mds_single_tab_hdr">
		        <span class="mds_single_tab_hdr_txt"><fmt:message key="task.synch.Status_Text" /></span>
		      </div>
		      <div class="mds_single_tab_bdy mds_dropshadow3">
		        <div class="mds_sync_pb_ctr">
		          <div class="mds_sync_pb">
		          </div>
		        </div>
		        <p class="mds_sync_sts_cursts">
		          <span class="mds_sync_sts_cursts_hdr">
		            <fmt:message key="task.synch.Progress_Status_Text" />
		          </span><span class="mds_sync_sts_cursts_msg"></span>&nbsp;<a href="#" class="mds_sync_sts_cursts_abort_ctr" title='<fmt:message key="task.synch.Close_Button_Tooltip" />'><fmt:message key="task.synch.Cancel_Button_Text" /></a>
		          	<img src="${fns:getSkinnedUrl(pageContext.request, '/images/wait-squares.gif')}" class="mds_invisible mds_sync_sts_spinner" alt="" />
		        </p>
		        <p class="mds_sync_sts_rate">
		          <span class="mds_sync_sts_rate_hdr">
		            <fmt:message key="task.synch.Progress_SynchRate_Text" />
		          </span><span class="mds_sync_sts_rate_msg"></span>
		        </p>
		        <p class="mds_sync_sts_curfile">
		          <span class="mds_sync_sts_curfile_hdr">
		            <fmt:message key="task.synch.Progress_Processing_Text" />
		          </span><span class="mds_sync_sts_curfile_msg"></span>
		        </p>
		      </div>
		    </div>
		  </div>
    	<ts:taskfooter task_Ok_Button_Text="${task_Ok_Button_Text}" />
	</form>
</div>

<cm:syncCompleteJsRenderTemplate />
