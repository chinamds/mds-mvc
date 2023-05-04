<%@ include file="/common/taglibs.jsp"%>

<%@ page import="com.mds.aiotplayer.core.MessageType" %>
<%@ page import="com.mds.aiotplayer.core.ResourceId" %>
<%@ page import="com.mds.aiotplayer.util.Utils" %>

<head>
    <title><fmt:message key="task.addObjects.title"/></title>
    <meta name="menu" content="AddContentMenu"/>
    <meta name="container" content="galleryView"/>
    <meta name="mdsClientId" content="${galleryView.mdsClientId}"/>
    <meta name="mdsShowAlbumMenu" content="${galleryView.showAlbumMenu}"/>
    <meta name="heading" content="<fmt:message key='task.addObjects.heading'/>"/>
</head>

<c:set var="group" value="grp_layout" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/contentobjects/addobject.js"%>
</c:set>

<%-- <div class="col-sm-3">
    <h2><fmt:message key="task.addObjects.Header_Text"/></h2>
    <fmt:message key="task.addObjects.Body_Text"/>
</div> --%>

<div class="col-sm-12 mds_addObjTabContainer">
	<%@ include file="/common/messages.jsp" %>
	<input type="hidden" id="galleryId" value="<c:out value="${param.galleryId}"/>"/>
	<input type="hidden" id="albumId" value="<c:out value="${album.id}"/>"/>
	<input type="hidden" id="fileFilters" name="fileFilters" value="${fileFilters}"/>
	<fmt:message key="task.addObjects.Header_Text" var="taskHeader"/>
	<fmt:message key="task.addObjects.Body_Text" var="taskBody"/>
	<fmt:message key="task.addObjects.OK_Btn_Text" var="task_Ok_Button_Text"/>
	<ts:taskheader taskHeader="${taskHeader }" taskBody="${ taskBody}" task_Ok_Button_Text="${task_Ok_Button_Text}" />
	<div class="table-responsive">
		<p class="mds_ao_hdr">
			<span id="lblAddFileHdr"><fmt:message key="task.addObjects.Local_Content_Tab_Hdr"/></span>
		</p>
	
		<div id="uploader" class="mds_addbottommargin2 mds_addtoppadding4">
			<p style="width: 100%; height: 150px; text-align: center; padding-top: 100px;">Loading...&nbsp;<img src="${fns:getSkinnedUrl(pageContext.request, '/images/wait-squares.gif')}" alt="" /></p>
		</div>
	</div>
	<div>
        <legend id="mds_optionsHdr" class="accordion-heading mds_optionsHdr mds_collapsed ui-corner-top" title="<fmt:message key="Site.Options_Tooltip"/>">
        	<p class="mds_ao_options_hdr">
				<a data-bs-toggle="collapse" href="#mds_optionsDtl"><fmt:message key="site.Options_Hdr"/></a>
			</p>
        </legend>
       	<div id="mds_optionsDtl" class="accordion-body collapse mds_optionsDtl ui-corner-bottom">
            <div class="form-group checkbox">
	            <label>
                   <input type="checkbox" id="chkDiscardOriginal">&nbsp;<fmt:message key="task.addObjects.Discard_Original_File_Option_Text"/>
               </label>
           </div>	                
              <div class="form-group checkbox">
               <label>
                   <input type="checkbox" id="chkDoNotExtractZipFile">&nbsp;<fmt:message key="task.addObjects.Do_Not_Extract_Zip_File_Option_Text"/>
               </label>
              </div>               
        </div>
    </div>
    
    <%-- <div class="form-group">
        <button type="submit" class="btn btn-primary mds_btnOkTop" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="task.addObjects.OK_Btn_Text"/>
        </button>

        <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
        </button>
    </div> --%>
    <ts:taskfooter task_Ok_Button_Text="${task_Ok_Button_Text}" />
</div>