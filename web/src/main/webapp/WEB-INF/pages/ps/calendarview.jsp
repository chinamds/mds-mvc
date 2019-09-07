<%@ include file="/common/taglibs.jsp"%>
   
<head>
   <title>Calendar</title>
  <meta name="menu" content="OperationMenu"/>
</head>

<div class="page-header">
	<div class="pull-right form-inline">
		<div class="btn-group">
			<button class="btn btn-primary" data-calendar-nav="prev"><< Prev</button>
			<button class="btn" data-calendar-nav="today">Today</button>
			<button class="btn btn-primary" data-calendar-nav="next">Next >></button>
		</div>
		<div class="btn-group">
			<button class="btn btn-warning" data-calendar-view="year">Year</button>
			<button class="btn btn-warning active" data-calendar-view="month">Month</button>
			<button class="btn btn-warning" data-calendar-view="week">Week</button>
			<button class="btn btn-warning" data-calendar-view="day">Day</button>
		</div>
	</div>

	<h3></h3>
	<small>To see example with events navigate to march 2013</small>
</div>
	
<div class="row">
	<div class="span9">
		<div id="calendar"></div>
	</div>
	<div class="span3">
		<div class="row-fluid">
			<select id="first_day" class="span12">
				<option value="" selected="selected">First day of week language-dependant</option>
				<option value="2">First day of week is Sunday</option>
				<option value="1">First day of week is Monday</option>
			</select>
			<select id="language" class="span12">
				<option value="">Select Language (default: en-US)</option>
				<option value="bg-BG">Bulgarian</option>
				<option value="nl-NL">Dutch</option>
				<option value="fr-FR">French</option>
				<option value="de-DE">German</option>
				<option value="el-GR">Greek</option>
				<option value="hu-HU">Hungarian</option>
				<option value="id-ID">Bahasa Indonesia</option>
				<option value="it-IT">Italian</option>
				<option value="pl-PL">Polish</option>
				<option value="pt-BR">Portuguese (Brazil)</option>
				<option value="ro-RO">Romania</option>
				<option value="es-CO">Spanish (Colombia)</option>
				<option value="es-MX">Spanish (Mexico)</option>
				<option value="es-ES">Spanish (Spain)</option>
				<option value="es-CL">Spanish (Chile)</option>
				<option value="es-DO">Spanish (República Dominicana)</option>
				<option value="ru-RU">Russian</option>
				<option value="sk-SR">Slovak</option>
				<option value="sv-SE">Swedish</option>
				<option value="zh-CN">简体中文</option>
				<option value="zh-TW">繁體中文</option>
				<option value="ko-KR">한국어</option>
				<option value="th-TH">Thai (Thailand)</option>
			</select>
			<label class="checkbox">
				<input type="checkbox" value="#events-modal" id="events-in-modal"> Open events in modal window
			</label>
			<label class="checkbox">
				<input type="checkbox" id="format-12-hours"> 12 Hour format
			</label>
			<label class="checkbox">
				<input type="checkbox" id="show_wb" checked> Show week box
			</label>
			<label class="checkbox">
				<input type="checkbox" id="show_wbn" checked> Show week box number
			</label>
		</div>

		<h4>Events</h4>
		<small>This list is populated with events dynamically</small>
		<ul id="eventlist" class="nav nav-list"></ul>
	</div>
</div>

<c:set var="group" value="grp_calendar" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/3rdparty/calendar/js/app.js"%>
</c:set>
