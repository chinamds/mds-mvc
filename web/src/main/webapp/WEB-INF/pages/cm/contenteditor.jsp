<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="contenteditor.title"/></title>
    <meta name="menu" content="ContentEditor"/>
    <meta name="heading" content="<fmt:message key='contenteditor.heading'/>"/>

<!--StringBuilder html = new StringBuilder();
string jqueryPath = System.IO.Path.Combine(appPath, "jquery-1.7.2.js"); 
html.AppendFormat("<script type=\"text/javascript\" src=\"{0}\"; ></script>", jqueryPath);-->
<script><!--
    function myFunction() {
        return false;
    }
//--></script>
</head>

<c:set var="group" value="grp_contenteditor" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/cm/contenteditor.js"%>
</c:set>

<body id=home>
<form action="javascript:myFunction(); return false;">
    <!-- <div class="container"> javascript:void(0) will prevent scroll -->
    <nav class="navbar navbar-expand-sm navbar-default navbar-light fixed-top navbar-fixed-top bg-light py-0" role="navigation">
	    <div class="navbar-header" style="padding-top: 5px;">
	       <button type="button" class="navbar-toggle" data-bs-toggle="collapse" data-bs-target="#navbarImageMap">
	           <span class="icon-bar"></span>
	           <span class="icon-bar"></span>
	           <span class="icon-bar"></span>
	       </button>
	       <a class="navbar-brand visible-xs-block visible-sm-block" href="<c:url value='/'/>"><fmt:message key="webapp.name"/></a>
	   </div>
       <div class="navbar-collapse collapse" id="navbarImageMap" style="padding-top: 5px;"> 
           <ul class="nav navbar-nav">
               <li style="text-align:left;" class="dropdown"><a href="#" class="dropdown-toggle" data-bs-toggle="dropdown">
               	<img src="${ctx}/static/img/zfile.png"/><b class="caret"></b></a>
                   <ul id="filestuff" class="dropdown-menu">
                       <li><a href="#">Load Image</a></li>
                       <li class="divider"></li>                  
                       <li><a href="#">Sample #1: Load Map from Custom Data</a></li>
                       <li><a href="#">Sample #2: Load map from JSON Data</a></li>
                       <li class="divider"></li>
                       <li><a href="#">Show Image Map Html</a></li>
                       <li><a href="#">Show Objects Custom Data</a></li>
                       <li><a href="#">Show Objects JSON Data</a></li>
                       <li class="divider"></li>
                       <li><a href="#">Save JSON Local Storage</a></li> 
                       <li><a href="#">Load JSON Local Storage</a></li>
                   </ul>
               </li>
               <li id="x"><a id="btnCircle" href="javascript:void(0)" onclick="return false;"><img id="circle" src="${ctx}/static/img/zcircle.png" style="width:24px;height:24px;" alt="" /></a></li>
               <li><a id="btnEllipse" href="javascript:void(0)" onclick="return false;"><img src="${ctx}/static/img/zellipse.png" style="width:24px;height:24px;" alt="" /></a></li>
               <li><a id="btnSquare" href="javascript:void(0)" onclick="return false;"><img src="${ctx}/static/img/zsquare.png" style="width:24px;height:24px;" alt="" /></a></li>
               <li><a id="btnPolygon" href="javascript:void(0)" onclick="return false;"><img src="${ctx}/static/img/zpolygon.png" style="width:24px;height:24px;" alt="" /></a></li>
               <li><a id="btnVideo" href="javascript:void(0)" onclick="return false;"><img src="${ctx}/static/img/zvideo.png" style="width:24px;height:24px;" alt="" /></a></li>
               <li><a id="btnWebcam" href="javascript:void(0)" onclick="return false;"><img src="${ctx}/static/img/zwebcam.png" style="width:24px;height:24px;" alt="" /></a></li>
               <li><a id="btnCamera" href="javascript:void(0)" onclick="return false;"><img src="${ctx}/static/img/zcamera.png" style="width:24px;height:24px;" alt="" /></a></li>
               
               <li id="closepolygon"><a id="btnPolygonClose" href="javascript:void(0)" onclick="return false;" data-bs-toggle="tooltip" data-placement="bottom" title="Closes Open polygon"><img src="${ctx}/static/img/zpolygonclose.png" style="width:24px;height:24px;" alt="" /></a></li>
               
               <li style="text-align:left;" class="dropdown"><a href="#" id="ddtext" class="dropdown-toggle" data-bs-toggle="dropdown">
                   <img src="${ctx}/static/img/zfont.png" alt="" /><b class="caret"></b></a>
                   <ul class="dropdown-menu">
                       <li id="liText">
                           <div style="margin:8px 8px 8px 8px; font-size:.8em;width:420px;" class="container-fluid">
                           		<div class="form-inline">
	                           <!-- <span style="white-space:nowrap;"> -->
	                           		<div class="form-group">
	                           		<label for="fs"><fmt:message key="contenteditor.font"/></label>
		                            <select id="fs" style="font-size:.9em;width:110px;" class="form-control input-sm">        
		                                   <option value="Arial,Arial,Helvetica,sans-serif">Arial,Arial,Helvetica,sans-serif</option>
		                                   <option value="Arial Black,Arial Black,Gadget,sans-serif">Arial Black,Arial Black,Gadget,sans-serif</option>
		                                   <option value="Comic Sans MS,Comic Sans MS,cursive">Comic Sans MS,Comic Sans MS,cursive</option>
		                                   <option value="Courier New,Courier New,Courier,monospace">Courier New,Courier New,Courier,monospace</option>
		                                   <option value="Georgia,Georgia,serif">Georgia,Georgia,serif</option>
		                                   <option value="Impact,Charcoal,sans-serif">Impact,Charcoal,sans-serif</option>
		                                   <option value="Lucida Console,Monaco,monospace">Lucida Console,Monaco,monospace</option>
		                                   <option value="Lucida Sans Unicode,Lucida Grande,sans-serif">Lucida Sans Unicode,Lucida Grande,sans-serif</option>
		                                   <option value="Palatino Linotype,Book Antiqua,Palatino,serif">Palatino Linotype,Book Antiqua,Palatino,serif</option>
		                                   <option value="Tahoma,Geneva,sans-serif">Tahoma,Geneva,sans-serif</option>
		                                   <option value="Times New Roman,Times,serif">Times New Roman,Times,serif</option>
		                                   <option value="Trebuchet MS,Helvetica,sans-serif">Trebuchet MS,Helvetica,sans-serif</option>
		                                   <option value="Verdana,Geneva,sans-serif">Verdana,Geneva,sans-serif</option>
		                           </select>
		                           </div>
		                           
		                           <div class="form-group">
		                           <label for="size"><fmt:message key="contenteditor.size"/></label>
		                           <select id="size" style="width:60px;" class="form-control input-sm">
		                                   <option value=7>7</option>
		                                   <option value=8>8</option>
		                                   <option value=9>9</option>
		                                   <option value=10>10</option>
		                                   <option value=11>11</option>
		                                   <option value=12>12</option>
		                                   <option value=14>14</option>
		                                   <option value=16>16</option>
		                                   <option value=18>18</option>
		                                   <option value=20>20</option>
		                                   <option value=30>30</option>
		                               </select>
		                               </div>
	                           <!-- </span><br/> -->
	                           </div>
 	                           <textarea id="text2add" class="form-control changeMe" rows="2" wrap="hard">Enter Text</textarea><br />
                           </div>
                           <div style="z-index:999999;padding-right:12px;text-align:right;"><button id="btnAddText" type="button" class="btn btn-danger">Add Text</button></div>
                       </li>
                   </ul>
               </li>

<!--                        <li><a id="btnDelete" href="javascript:void(0)" onclick="return false;" data-bs-toggle="tooltip" data-placement="bottom" title="Deletes Only Selected Object"><img src="${ctx}/static/img/zdelete.png" style="width:24px;height:24px;" alt="" /></a></li>
                        <li><a id="btnEraser" href="javascript:void(0)" onclick="return false;" data-bs-toggle="tooltip" data-placement="bottom" title="Deletes All Object Except for Background Image"><img src="${ctx}/static/img/zfist.png" style="width:24px;height:24px;" alt="" /></a></li>
-->                        
               <li style="text-align:left;" class="dropdown"><a href="#" id="qqqq" class="dropdown-toggle" data-bs-toggle="dropdown">
                   <img src="${ctx}/static/img/zsettings.png" alt="" /><b class="caret"></b></a>
                   <ul id="btnSelect" class="dropdown-menu">
                       <li><a href="javascript:void(0)"><img id="btnCopy" src="${ctx}/static/img/zcopy.png" style="width:24px;height:24px;padding-right:4px;" alt="" />Copy Object (CTRL C)</a></li>
                       <li><a href="javascript:void(0)"><img id="btnPaste" src="${ctx}/static/img/zpaste.png" style="width:24px;height:24px;padding-right:4px;" alt="" />Paste Object (CTRL V)</a></li>                  
                       <li class="divider"></li>
                       <li><a href="javascript:void(0)"><img id="zbtnDelete" src="${ctx}/static/img/zdelete.png" style="width:24px;height:24px;padding-right:4px;" alt="" />Delete Object</a></li>
                       <li><a href="javascript:void(0)"><img id="zbtnErase" src="${ctx}/static/img/zfist.png" style="width:24px;height:24px;padding-right:4px;" alt="" />Erase All Objects</a></li>
                       <li class="divider"></li>
                       <li><a href="javascript:void(0)">Select All Objects</a></li>
                       <li><a href="javascript:void(0)">Lock All Objects</a></li>
                       <li class="divider"></li>
                       <li><a href="javascript:void(0)">Bring Forward</a></li>
                       <li><a href="javascript:void(0)">Bring To Front</a></li>
                       <li><a href="javascript:void(0)">Send Backwards</a></li>
                       <li><a href="javascript:void(0)">Send To Back</a></li>
                       <li class="divider"></li>
                   </ul>
               </li>
                               
               <!--<li><a id="btnVideo" href="#" onclick="return false;" data-bs-toggle="tooltip" data-placement="bottom" title="Animates Selected Object"><img src="${ctx}/static/img/ztv.png" style="width:24px;height:24px;" alt="" /></a></li> -->
               <li style="text-align:left;" class="dropdown"><a href="javascript:void(0)" id="ddproperties" class="dropdown-toggle" data-bs-toggle="dropdown">
                   <img src="${ctx}/static/img/zoptions.png" alt="" /><b class="caret"></b></a>
                   <ul class="dropdown-menu">
                       <li id="zspecial">
                       	   <div class="panel panel-default">
							  <div class="panel-heading">Object Properties</div>
							  	<div class="panel-body" style="margin: 12px 0px 12px 12px;">
                       	   
                           <!-- <div style="margin: 12px 0px 12px 12px; width:190px; vertical-align: top;">
	                           <span style="font-family: Arial, Helvetica, sans-serif; font-size: 1.2em; font-weight: bold; font-style: normal; font-variant: normal; text-align: center; white-space: nowrap; margin: 0px auto 0px auto; padding-bottom: 8px;">&nbsp;&nbsp;Object Properties&nbsp;&nbsp;</span> -->
	                           <div style="white-space: nowrap;" class="form-inline">
	                           	<span class="label label-danger">mapKey&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;
	                           	<input value="home1" style="width:90px;" type="text" autocomplete="on" class="form-control input-sm search-query" id="txtMapKey">
	                           </div>
	                           
	                           <div style="white-space: nowrap;" class="form-inline">
		                           <span class="label label-danger">mapValue</span>&nbsp;
		                           <span class="entertext"><input style="width:88px;" type="text" autocomplete="on" class="form-control input-sm search-query span2" placeholder="roof" id="txtMapValue"></span>
	                           </div>
	                           
	                           <div style="white-space: nowrap;" class="form-inline">
	                           		<span class="label label-success">Area Link</span>&nbsp;
	                           		<span class="entertext"><input style="width:90px;" type="text" autocomplete="on" class="form-control input-sm search-query span2" placeholder="Link" id="hrefBox" value="#"></span>
	                           </div>
	                           
	                           <div style="white-space: nowrap;" class="form-inline">
		                           <span class="label label-info">Area Alt&nbsp;&nbsp;&nbsp;</span>&nbsp;
		                           <span class="entertext"><input style="width:88px;" type="text" autocomplete="on" class="form-control input-sm search-query span2" placeholder="roof" id="txtAltValue"></span>
	                           </div>
	
	                           <div style="white-space: nowrap;" class="form-inline">
		                           <span class="label label-warning">Stroke Color&nbsp;&nbsp;&nbsp;</span>&nbsp;
		                           <span class="entertext"><input style="width:64px;" type="text" autocomplete="off" class="form-control input-sm search-query span2" placeholder="#000000" id="txtStrokeColor"></span>
	                           </div>
	
	                           <div style="white-space: nowrap;" class="form-inline">
		                           <span class="span3 label label-warning">Stroke Width&nbsp;&nbsp;</span>&nbsp;
		                           <span class="span9 entertext"><input style="width:64px;" type="text" autocomplete="off" class="form-control input-sm search-query span2" placeholder="3" id="txtStrokeWidth"></span>
	                           </div>
	                           
	                            <div class="btn-group">
                                   <label class="checkbox">
                                       <input type="checkbox" value="option1" id="cb_selectable"> selectable
                                   </label>
                                   <label class="checkbox">
                                       <input type="checkbox" value="option2" id="cb_hasControls"> hasControls
                                   </label>
                                   <label class="checkbox">
                                       <input type="checkbox" value="option1" id="cb_lockMovementX"> lockMovementX
                                   </label>
                                   <label class="checkbox">
                                       <input type="checkbox" value="option2" id="cb_lockMovementY"> lockMovementY
                                   </label>
                                   <label class="checkbox">
                                       <input type="checkbox" value="option3" id="cb_lockScaling"> lockScaling
                                   </label>
                                   <label class="checkbox">
                                       <input type="checkbox" value="option3" id="cb_lockRotation"> lockRotation
                                   </label>
                                   <label class="checkbox">
                                       <input type="checkbox" value="option3" id="cb_hasRotatingPoint"> hasRotatingPoint
                                   </label>
                                   <label class="checkbox">
                                       <input type="checkbox" value="option1" id="cb_transparentCorners"> transparentCorners
                                   </label>
                                   <label class="checkbox">
                                       <input type="checkbox" value="option2" id="cb_hasBorders"> hasBorders
                                   </label>
                                   <label class="checkbox">
                                       <input type="checkbox" value="option3" id="cb_perPixelTargetFind"> perPixelTargetFind
                                   </label>
                               </div>
                           </div>
                           </div>

                           <div style="padding-right:12px;text-align:right;"><button id="btnUpdate"class="btn btn-dangerz" data-bind="click:reset">Reset</button></div>
                       </li>
                   </ul>
               </li>

               <li style="text-align:left;" class="dropdown"><a href="#" id="A1" class="dropdown-toggle" data-bs-toggle="dropdown">
                   <img src="${ctx}/static/img/ztarget.png" alt="" /><b class="caret"></b></a>
                   <ul id="btnAnimate" class="dropdown-menu">
                       <li><a href="#"><img id="Img1" src="${ctx}/static/img/zupdate.png" style="padding-right:4px;" alt="" />Spin Selected Object</a></li>
                       <li><a href="#"><img id="Img2" src="${ctx}/static/img/zspiral.png" style="padding-right:4px;" alt="" />Cycle Patterns for MapValue</a></li>
                   </ul>
               </li>

               <li style="text-align:left;" class="dropdown"><a href="#" id="ddSlider" class="dropdown-toggle" data-bs-toggle="dropdown">
                   <img src="${ctx}/static/img/ztransparent.png" alt="" /><b class="caret"></b></a>
                   <ul class="dropdown-menu">
                       <li id="liSlider" style="margin:0px 12px 0px 12px;">
                           <input type="text" class="span2" value="7" id="sl1" data-slider-tooltip="show">
                       </li>
                   </ul>
               </li> 

               <li style="background-color:White;">
               <div style="width:90px;display:inline-block;margin-left:0px;margin-top:10px;margin-bottom:0px; height: 20px;" >
                   <input class="minicolors" style="width:60px;" type="text" id="color" name="color" size="7" autocomplete="on" value="#005294" /></div>

               <!-- <li class="divider-vertical"></li>-->
      
               <li style="text-align:left;" class="dropdown"><a href="#" id="A2" class="dropdown-toggle" data-bs-toggle="dropdown">
                   <img src="${ctx}/static/img/zzoom.png" alt="" /><b class="caret"></b></a>
                   <ul id="btnZoom" class="dropdown-menu">
                       <li><a href="#"><img id="btnZoomIn" src="${ctx}/static/img/zzoom_in.png" style="padding-right:4px;" alt="" />Zoom In (CTRL Plus)</a></li>
                       <li><a href="#"><img id="btnZoomOut" src="${ctx}/static/img/zzoom_out.png" style="padding-right:4px;" alt="" />Zoom Out (CTRL Minus)</a></li>
                       <li><a href="#"><img id="btnResetZoom" src="${ctx}/static/img/zzoom_121.png" style="padding-right:4px;" alt="" />Zoom 1:1 (CTRL Zero)</a></li>
                   </ul>
               </li>

               <li style="text-align:left;margin-right:-12px;margin-top:-6px;" class="dropdown"><a href="#" id="ddAreasTop" class="dropdown-toggle" data-bs-toggle="dropdown">
                   <span style="white-space:nowrap;text-align:left;white-space:nowrap;">Map Values<img src="${ctx}/static/img/zshape.png" style="width:24px;height:18px;" alt="" /><b class="caret"></b></span></a>
                   <div style="text-align:center;"><input id="txtAreaSelected" style="margin-top:-10px;font-size:.9em;height:16px;width:80px;border-style:none;" readonly="true" /></div>
                   <ul id="ddAreas" class="dropdown-menu">
                   </ul>
               </li>
               <li><a id="ddrefresh" href="javascript:void(0)" onclick="return false;" data-bs-toggle="tooltip" data-placement="bottom" title="Deletes Only Selected Object">
               	<img src="${ctx}/static/img/zrefresh.png" style="margin-left:2px;width:24px;height:24px;" alt="" />
               </a></li>
               <!--onmouseover="this.style.cursor='hand'"-->                                                                         
           </ul>
           <ul class="nav navbar-nav navbar-right dd" style="padding-top: 5px;">
               <li style="text-align:left;" class="dd">
               		<a href="#" class="dd-toggle" data-bs-toggle="dd"><span class="dd-text" style="white-space:nowrap;"><img src="${ctx}/static/img/zpatterns.png" />Patterns<b class="caret"></b></span></a>
	                <ul id="ddPatterns" class="dd-menu">
	                </ul>
               </li>
           </ul>   
       </div>
               
    </nav><!-- /.navbar navbar-fixed-top -->
    
	<div id="canvas-cover" style="margin-top: 80px;"><canvas id="editor" width="100%" height="700"></canvas></div>
    <div class="ImageHolder"><img id="mapImage" src="${ctx}/static/uploads/image1.png" alt="MapImage" /></div>

	<div class="clear5px"></div>
	<br /><br />   

<%-- <script type="text/underscoreTemplate" id="map_template">
&lt;map name="mapid" id="mapid"&gt;
<% for(var i=0; i<areas.length; i++) { var a=areas[i]; %>&lt;area shape="<%= a.shape %>" <%= "data-"+mapKey %>="<%= a.mapValue %>" coords="<%= a.coords %>" href="<%= a.link %>" alt="<%= a.alt %>" /&gt;
<% } %>&lt;/map&gt;
</script>

<script type="text/underscoreTemplate" id="map_data">
[<% for(var i=0; i<areas.length; i++) { var a=areas[i]; %>
{
mapKey:?<%= mapKey %>",
mapValue:?<%= a.mapValue %>",
type:?<%= a.shape %>",
link:?<%= a.link %>",
alt:?<%= a.alt %>",   
perPixelTargetFind: <%= a.perPixelTargetFind %>,
selectable: <%= a.selectable %>,
hasControls: <%= a.hasControls %>,
lockMovementX: <%= a.lockMovementX %>,
lockMovementY: <%= a.lockMovementY %>,
lockScaling: <%= a.lockScaling %>,
lockRotation: <%= a.lockRotation %>,
hasRotatingPoint: <%= a.hasRotatingPoint %>,
hasBorders: <%= a.hasBorders %>,
overlayFill: null,
stroke: '#000000',
strokeWidth: 1,
transparentCorners: true,
borderColor: "black",
cornerColor: "black",
cornerSize: 12,
transparentCorners: true,
pattern:?<%= a.pattern %>",
<% if ( (a.pattern) != "" ) { %>fill:?#00ff00",<% } else { 
%>fill:?<%= a.fill %>",<% } %> opacity:?%= a.opacity %>,
top:?%= a.top %>, left:?%= a.left %>, scaleX:?%= a.scaleX %>,
scaleY:?%= a.scaleY %>,
<% if ( (a.shape) == "circle" ) { %>radius:?%= a.radius %>,<% } 
%><% if ( (a.shape) == "ellipse" ) { %>width:?%= a.width %>,
height:?%= a.height %>,<% } 
%><% if ( (a.shape) == "rect" ) { %>width:?%= a.width %>,,
height:?%= a.height %>,<% } 
%><% if ( (a.shape) == "polygon" ) { %>points:�[<% for(var j=0; j<a.coords.length-1; j = j+2) {  
var checker = j % 6; %> <% if ( (checker) == 0 ) { 
%>{x:?%= (a.coords[j] - a.left)/a.scaleX %>,y: <%= (a.coords[j+1] - a.top)/a.scaleY %>}, <% } 
else { %>{x:?%= (a.coords[j] - a.left)/a.scaleX %>, y: <%= (a.coords[j+1] - a.top)/a.scaleY %>}, <% }
 } %>]<% } %>},<% } %>
]
</script> --%>

	<div id="btnRotatePaterns"></div>
	<div id="btnShowHtmlMap"></div>
	<div id="btnSampleMapCustom"></div>
	<div id="btnSampleMapJSON"></div>
	<div id="btnShowCustomData"></div>
	<div id="btnShowJSONData"></div>
	<div id="btnSaveLocalStorage"></div>
	<div id="btnLoadLocalStorage"></div>
	
	<div id="junk" style="display:none;"></div>
	<div id="btnCreateAreasDropDown"></div>
	<div id="btnSave2JSON"></div>
	<div id="btnXJSON"></div>
	<div id="rawcode" style="visibility:hidden;"></div>
	<video height="360" width="500" id="webcam" style="display: none"></video>
	<div id="camera"></div>
	
	<input style="visibility:hidden;" type="file" id="files" name="files[]" multiple="" />
	
	<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3 id="myModalLabel">My Header</h3>
		</div>
		  <div class="modal-body">
		    <div class="control-group">
		      <textarea style="resize:none;width:95%;" class="input xlarge" rows="16" id="textareaID" name="textareaID" placeholder="Type your comment here..."></textarea>
		    </div>
		  </div>
		<div class="modal-footer">
		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
		</div>
	</div>
</form>
</body>
