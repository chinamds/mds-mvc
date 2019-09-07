<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="zoneList.title"/></title>
    <meta name="menu" content="ZoneMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="zoneList.heading"/></h2>

<form method="get" action="${ctx}/zones" id="searchForm" class="form-inline">
<div id="search" class="text-right">
    <span class="col-sm-9">
        <input type="text" size="20" name="q" id="query" value="${param.q}"
               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
    </span>
    <button id="button.search" class="btn btn-default btn-sm" type="submit">
        <i class="fa fa-search"></i> <fmt:message key="button.search"/>
    </button>
</div>
</form>

<p><fmt:message key="zoneList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/zoneform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="zoneList" class="table table-condensed table-striped table-hover" requestURI="" id="zoneList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="zoneform" media="html"
        paramId="id" paramProperty="id" titleKey="zone.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="zone.id"/>
    <display:column property="BAlpha" sortable="true" titleKey="zone.BAlpha"/>
    <display:column sortProperty="DDERefresh" sortable="true" titleKey="zone.DDERefresh">
        <input type="checkbox" disabled="disabled" <c:if test="${zoneList.DDERefresh}">checked="checked"</c:if>/>
    </display:column>
    <display:column property="aspect" sortable="true" titleKey="zone.aspect"/>
    <display:column property="audioDevice" sortable="true" titleKey="zone.audioDevice"/>
    <display:column property="audioSource" sortable="true" titleKey="zone.audioSource"/>
    <display:column property="audioSourceString" sortable="true" titleKey="zone.audioSourceString"/>
    <display:column property="audioStandard" sortable="true" titleKey="zone.audioStandard"/>
    <display:column property="channelId" sortable="true" titleKey="zone.channelId"/>
    <display:column sortProperty="chkZone" sortable="true" titleKey="zone.chkZone">
        <input type="checkbox" disabled="disabled" <c:if test="${zoneList.chkZone}">checked="checked"</c:if>/>
    </display:column>
    <display:column property="frequency" sortable="true" titleKey="zone.frequency"/>
    <display:column property="speed" sortable="true" titleKey="zone.speed"/>
    <display:column property="volume" sortable="true" titleKey="zone.volume"/>
    <display:column property="webCharset" sortable="true" titleKey="zone.webCharset"/>
    <display:column property="webZoom" sortable="true" titleKey="zone.webZoom"/>
    <display:column property="zoneBGColor" sortable="true" titleKey="zone.zoneBGColor"/>
    <display:column property="zoneBGFile" sortable="true" titleKey="zone.zoneBGFile"/>
    <display:column sortProperty="zoneChkMpeg2" sortable="true" titleKey="zone.zoneChkMpeg2">
        <input type="checkbox" disabled="disabled" <c:if test="${zoneList.zoneChkMpeg2}">checked="checked"</c:if>/>
    </display:column>
    <display:column property="zoneDelay" sortable="true" titleKey="zone.zoneDelay"/>
    <display:column property="zoneDirection" sortable="true" titleKey="zone.zoneDirection"/>
    <display:column property="zoneDuration" sortable="true" titleKey="zone.zoneDuration"/>
    <display:column property="zoneEffectType" sortable="true" titleKey="zone.zoneEffectType"/>
    <display:column property="zoneFile" sortable="true" titleKey="zone.zoneFile"/>
    <display:column property="zoneIndex" sortable="true" titleKey="zone.zoneIndex"/>
    <display:column property="zoneMotion" sortable="true" titleKey="zone.zoneMotion"/>
    <display:column sortProperty="zoneMute" sortable="true" titleKey="zone.zoneMute">
        <input type="checkbox" disabled="disabled" <c:if test="${zoneList.zoneMute}">checked="checked"</c:if>/>
    </display:column>
    <display:column property="zoneOffineFile" sortable="true" titleKey="zone.zoneOffineFile"/>
    <display:column property="zoneOrientation" sortable="true" titleKey="zone.zoneOrientation"/>
    <display:column property="zonePort" sortable="true" titleKey="zone.zonePort"/>
    <display:column sortProperty="zoneRatio" sortable="true" titleKey="zone.zoneRatio">
        <input type="checkbox" disabled="disabled" <c:if test="${zoneList.zoneRatio}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="zoneSelectBgPic" sortable="true" titleKey="zone.zoneSelectBgPic">
        <input type="checkbox" disabled="disabled" <c:if test="${zoneList.zoneSelectBgPic}">checked="checked"</c:if>/>
    </display:column>
    <display:column property="zoneTVChannel" sortable="true" titleKey="zone.zoneTVChannel"/>
    <display:column property="zoneTVCountry" sortable="true" titleKey="zone.zoneTVCountry"/>
    <display:column property="zoneTVInput" sortable="true" titleKey="zone.zoneTVInput"/>
    <display:column property="zoneTVInputType" sortable="true" titleKey="zone.zoneTVInputType"/>
    <display:column property="zoneTVSource" sortable="true" titleKey="zone.zoneTVSource"/>
    <display:column property="zoneTVSourceString" sortable="true" titleKey="zone.zoneTVSourceString"/>
    <display:column property="zoneTVStandard" sortable="true" titleKey="zone.zoneTVStandard"/>
    <display:column property="zoneTVTuningSpace" sortable="true" titleKey="zone.zoneTVTuningSpace"/>
    <display:column property="zoneType" sortable="true" titleKey="zone.zoneType"/>
    <display:column property="zoom" sortable="true" titleKey="zone.zoom"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="zoneList.zone"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="zoneList.zones"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="zoneList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="zoneList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="zoneList.title"/>.pdf</display:setProperty>
</display:table>
