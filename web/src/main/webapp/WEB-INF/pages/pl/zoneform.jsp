<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="zoneDetail.title"/></title>
    <meta name="menu" content="ZoneMenu"/>
    <meta name="heading" content="<fmt:message key='zoneDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="zoneList.zone"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="zoneDetail.heading"/></h2>
    <fmt:message key="zoneDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="zone" method="post" action="zoneform" cssClass="well"
           id="zoneForm" onsubmit="return validateZone(this)">
<form:hidden path="id"/>
    <spring:bind path="zone.BAlpha">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.BAlpha" styleClass="control-label"/>
        <form:input cssClass="form-control" path="BAlpha" id="BAlpha"  maxlength="255"/>
        <form:errors path="BAlpha" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.DDERefresh">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.DDERefresh" styleClass="control-label"/>
        <form:checkbox path="DDERefresh" id="DDERefresh" cssClass="checkbox"/>
        <form:errors path="DDERefresh" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.aspect">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.aspect" styleClass="control-label"/>
        <form:input cssClass="form-control" path="aspect" id="aspect"  maxlength="255"/>
        <form:errors path="aspect" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.audioDevice">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.audioDevice" styleClass="control-label"/>
        <form:input cssClass="form-control" path="audioDevice" id="audioDevice"  maxlength="100"/>
        <form:errors path="audioDevice" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.audioSource">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.audioSource" styleClass="control-label"/>
        <form:input cssClass="form-control" path="audioSource" id="audioSource"  maxlength="255"/>
        <form:errors path="audioSource" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.audioSourceString">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.audioSourceString" styleClass="control-label"/>
        <form:input cssClass="form-control" path="audioSourceString" id="audioSourceString"  maxlength="100"/>
        <form:errors path="audioSourceString" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.audioStandard">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.audioStandard" styleClass="control-label"/>
        <form:input cssClass="form-control" path="audioStandard" id="audioStandard"  maxlength="255"/>
        <form:errors path="audioStandard" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.channelId">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.channelId" styleClass="control-label"/>
        <form:input cssClass="form-control" path="channelId" id="channelId"  maxlength="255"/>
        <form:errors path="channelId" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.chkZone">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.chkZone" styleClass="control-label"/>
        <form:checkbox path="chkZone" id="chkZone" cssClass="checkbox"/>
        <form:errors path="chkZone" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.frequency">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.frequency" styleClass="control-label"/>
        <form:input cssClass="form-control" path="frequency" id="frequency"  maxlength="255"/>
        <form:errors path="frequency" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="product" items="productList" itemLabel="label" itemValue="value"/>
    <spring:bind path="zone.speed">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.speed" styleClass="control-label"/>
        <form:input cssClass="form-control" path="speed" id="speed"  maxlength="255"/>
        <form:errors path="speed" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.volume">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.volume" styleClass="control-label"/>
        <form:input cssClass="form-control" path="volume" id="volume"  maxlength="255"/>
        <form:errors path="volume" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.webCharset">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.webCharset" styleClass="control-label"/>
        <form:input cssClass="form-control" path="webCharset" id="webCharset"  maxlength="20"/>
        <form:errors path="webCharset" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.webZoom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.webZoom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="webZoom" id="webZoom"  maxlength="255"/>
        <form:errors path="webZoom" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneBGColor">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneBGColor" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneBGColor" id="zoneBGColor"  maxlength="255"/>
        <form:errors path="zoneBGColor" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneBGFile">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneBGFile" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneBGFile" id="zoneBGFile"  maxlength="1024"/>
        <form:errors path="zoneBGFile" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneChkMpeg2">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneChkMpeg2" styleClass="control-label"/>
        <form:checkbox path="zoneChkMpeg2" id="zoneChkMpeg2" cssClass="checkbox"/>
        <form:errors path="zoneChkMpeg2" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneDelay">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneDelay" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneDelay" id="zoneDelay"  maxlength="255"/>
        <form:errors path="zoneDelay" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneDirection">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneDirection" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneDirection" id="zoneDirection"  maxlength="255"/>
        <form:errors path="zoneDirection" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneDuration">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneDuration" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneDuration" id="zoneDuration"  maxlength="255"/>
        <form:errors path="zoneDuration" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneEffectType">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneEffectType" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneEffectType" id="zoneEffectType"  maxlength="255"/>
        <form:errors path="zoneEffectType" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneFile">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneFile" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneFile" id="zoneFile"  maxlength="1024"/>
        <form:errors path="zoneFile" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneIndex">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneIndex" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneIndex" id="zoneIndex"  maxlength="255"/>
        <form:errors path="zoneIndex" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneMotion">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneMotion" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneMotion" id="zoneMotion"  maxlength="255"/>
        <form:errors path="zoneMotion" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneMute">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneMute" styleClass="control-label"/>
        <form:checkbox path="zoneMute" id="zoneMute" cssClass="checkbox"/>
        <form:errors path="zoneMute" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneOffineFile">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneOffineFile" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneOffineFile" id="zoneOffineFile"  maxlength="1024"/>
        <form:errors path="zoneOffineFile" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneOrientation">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneOrientation" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneOrientation" id="zoneOrientation"  maxlength="255"/>
        <form:errors path="zoneOrientation" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zonePort">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zonePort" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zonePort" id="zonePort"  maxlength="255"/>
        <form:errors path="zonePort" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneRatio">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneRatio" styleClass="control-label"/>
        <form:checkbox path="zoneRatio" id="zoneRatio" cssClass="checkbox"/>
        <form:errors path="zoneRatio" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneSelectBgPic">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneSelectBgPic" styleClass="control-label"/>
        <form:checkbox path="zoneSelectBgPic" id="zoneSelectBgPic" cssClass="checkbox"/>
        <form:errors path="zoneSelectBgPic" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneTVChannel">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneTVChannel" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneTVChannel" id="zoneTVChannel"  maxlength="20"/>
        <form:errors path="zoneTVChannel" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneTVCountry">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneTVCountry" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneTVCountry" id="zoneTVCountry"  maxlength="255"/>
        <form:errors path="zoneTVCountry" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneTVInput">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneTVInput" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneTVInput" id="zoneTVInput"  maxlength="255"/>
        <form:errors path="zoneTVInput" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneTVInputType">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneTVInputType" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneTVInputType" id="zoneTVInputType"  maxlength="255"/>
        <form:errors path="zoneTVInputType" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneTVSource">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneTVSource" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneTVSource" id="zoneTVSource"  maxlength="255"/>
        <form:errors path="zoneTVSource" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneTVSourceString">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneTVSourceString" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneTVSourceString" id="zoneTVSourceString"  maxlength="100"/>
        <form:errors path="zoneTVSourceString" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneTVStandard">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneTVStandard" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneTVStandard" id="zoneTVStandard"  maxlength="255"/>
        <form:errors path="zoneTVStandard" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneTVTuningSpace">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneTVTuningSpace" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneTVTuningSpace" id="zoneTVTuningSpace"  maxlength="255"/>
        <form:errors path="zoneTVTuningSpace" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoneType">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoneType" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneType" id="zoneType"  maxlength="255"/>
        <form:errors path="zoneType" cssClass="help-block"/>
    </div>
    <spring:bind path="zone.zoom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="zone.zoom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoom" id="zoom"  maxlength="255"/>
        <form:errors path="zoom" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty zone.id}">
            <button type="submit" class="btn btn-danger" id="delete" name="delete" onclick="bCancel=true;return confirmMessage(msgDelConfirm)">
                <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/>
            </button>
        </c:if>

        <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
</form:form>
</div>

<v:javascript formName="zone" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['zoneForm']).focus();
    });
</script>
