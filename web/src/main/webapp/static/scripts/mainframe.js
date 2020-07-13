//Extended jQuery function
//Initialize home page - main frame
$.mainframe = {
	options: null,
	defaultOptions: { 
		appRoot: '', 
		i18n: {
			myprofile:'My Profile', 
			mymessage:'My message',  
			viewallnotifications:'View all notifications',
			mynotifications:'My notifications',
			addevent:'Add Event',
			ok:'OK',
			suretodelete:'Are you sure to delete this event?',
            discardchanges:'Are you sure to discard the changes?',
			viewevent:'View Event',
		},
	},
	initOptions: function (options) {
		$.mainframe.options = $.extend(true, $.mainframe.defaultOptions, options);

        return this;
    },
    /**Initialize home page layout, menu, tab*/
	initialize: function (options) {
		this.initOptions(options);
		
        $.menus.initMenu();
        $.layouts.initLayout();
        $.tabs.initTab();

        $.mainframe.initCommonBtn();

        var message = $.mainframe.initMessage();
        var notification = $.mainframe.initNotification();
        var fiveMinute = 5 * 60 * 1000;
        var pollingUrl = window.Mds.AppRoot + "/sys/polling";
        var longPolling = function(url, callback) {
            $.ajax({
                url: url,
                async: true,
                cache: false,
                global: false,
                timeout: 30 * 1000,
                dataType : "json",
                success: function (data, status, request) {
                    callback(data);
                    data = null;
                    status = null;
                    request = null;
                    setTimeout(
                        function () {
                            longPolling(url, callback);
                        },
                        10
                    );
                },
                error: function (xmlHR, textStatus, errorThrown) {
                    xmlHR = null;
                    textStatus = null;
                    errorThrown = null;

                    setTimeout(
                        function () {
                            longPolling(url, callback);
                        },
                        30 * 1000
                    );
                }
            });
        };
        longPolling(pollingUrl, function(data) {
            if(data) {
                if(data.unreadMessageCount) {
                    message.update(data.unreadMessageCount);
                }
                if(data.notifications) {
                    notification.update(data.notifications);
                }
            }
        });

    },
    initCommonBtn : function() {
        $(".btn-view-info,.btn-change-password").click(function() {
            var a = $(this);
            var url = "";
            if(a.is(".btn-view-info")) {
                url = window.Mds.AppRoot +"/sys/users/loginUser/viewInfo";
            } else if(a.is(".btn-change-password")) {
                url = window.Mds.AppRoot + "/sys/users/loginUser/changePassword";
            }
            setTimeout(function() {
                $.tabs.activeTab($.tabs.nextCustomTabIndex(), $.mainframe.options.i18n.myprofile, url, true)
            }, 0);
        });
        $(".btn-view-message,.btn-message").click(function() {
            var url = window.Mds.AppRoot + "/sys/myMessages";
            setTimeout(function() {
                $.tabs.activeTab($.tabs.nextCustomTabIndex(), $.mainframe.options.i18n.mymessage, url, true)
            }, 0);
        });
        $(".btn-view-notice").click(function() {
            var url = window.Mds.AppRoot + "/sys/notifications/list?read=false";
            setTimeout(function() {
                $.tabs.activeTab($.tabs.nextCustomTabIndex(), this.options.i18n.mynotifications, url, true)
            }, 0);
        });
        $(".btn-view-worklist,.btn-view-work").click(function() {
            var $that = $(this);
            var url = window.Mds.AppRoot + "/office/personal/worklist";
            setTimeout(function() {
                if($that.is(".btn-view-work")) {
                    url = $that.data("url");
                }
                $.tabs.activeTab($.tabs.nextCustomTabIndex(), this.options.i18n.mymessage, url, true)
            }, 0);

            return false;
        });


    },
    initMessage : function() {
        var messageBtn = $(".btn-message");
        var icon = messageBtn.find(".icon-message");
        var messageBtnInterval = null;

        var activeUnreadIcon = function(count) {
            clearInterval(messageBtnInterval);
            if(count > 0) {
                var label = messageBtn.find(".icon-count");
                if(!label.length) {
                    label = $("<i class='label label-danger label-important badge badge-danger icon-count'></i>");
                    messageBtn.append(label);
                }
                label.text(count);
                messageBtn.addClass("unread");
                messageBtnInterval = setInterval(function() {icon.toggleClass("far fa-envelope").toggleClass("fa fa-envelope");}, 650);
            }
        };

        messageBtn.click(function() {
            clearInterval(messageBtnInterval);
            //$($.find("#treemenu a:contains(" + $.mainframe.options.i18n.mymessage + ")")).dblclick();
            $($.find("#treemenu a[href='" + window.Mds.AppRoot + "/sys/myMessages']")).dblclick();
            messageBtn.removeClass("unread");
            messageBtn.find(".icon-count").remove();
            icon.removeClass("fa fa-envelope").addClass("far fa-envelope");

        });

        activeUnreadIcon(messageBtn.data("unread"));

        return {
            update : function(unReadMessageCount) {
                activeUnreadIcon(unReadMessageCount);
            }
        };
    },
    initNotification : function() {
        var notificationBtn = $(".btn-notification");
        var notificationList = $(".notification-list");
        /*var menu = $(".notification-list-pop .menu");
        var menuList = menu.find(".list");
        var detail = $(".notification-list-pop .detail");
        var detailList = detail.find(".list");
        var loading = $(".notification-list-pop .loading");
        var noComment = $(".notification-list-pop .no-comment");*/
        var markReadUrl = window.Mds.AppRoot + "/sys/notifications/markRead?id=";

        var contentTemplate = '<li class="view-content {unread}"><span>{title}</span><span class="float-right">{date}</span></li>';
        var detailContentTemplate = '<div id="notificaiton-{id}" class="notification-detail" style="display: none"><div class="title"><span>{title}</span><span class="float-right">{date}</span></div><div class="content">{content}</div></div>';
        var moreContent = '<li class="view-all-notification"><span>&gt;&gt;'+ $.mainframe.options.i18n.viewallnotifications + '</span></li>';
        
        var initPopover = function(dataList, hasUnread){
	        notificationBtn.popover({
	    		html:true,
	    		placement:'bottom',
	    		content: function() {
		            return notificationList.html();
		        },
	    		template: '<div class="popover notification-list-pop" role="tooltip"><div class="arrow"></div><h3 class="popover-header"></h3><div class="popover-body"></div></div>'
	    	}).on('shown.bs.popover', function () {
	    		if(dataList && dataList.length) {
	    			initMenu(dataList, hasUnread);
	    		}
	    		
	    		var menu = $(".notification-list-pop .menu");
	    		if(menu.find(".view-content").length) {
	                showMenuPop();
	            } else {
	                showNoCommentPop();
	            }
	    		
	    		var notificationListPop = $(".notification-list-pop");
	    		notificationListPop.find(".close-notification-list").click(function() {
	                hideNotification();
	            });
	    		
	    		var windowClickHideNotification = function (event) {
	                var target = $(event.target);
	                if (!target.closest(".btn-notification").length && !target.closest(".notification-list-pop").length) {
	                    hideNotification();
	                }
	            };
	
	            $("body")
	                .on("click", windowClickHideNotification)
	                .find("iframe").contents().find("body").each(function() {
	                    $(this).on("click", windowClickHideNotification);
	                });
	    	});
        }
                
        var viewAllNotification = function() {
            $($.find("#treemenu a[href='" + window.Mds.AppRoot + "/sys/notifications']")).dblclick();
            hideNotification();
            return false;
        };
        var hideNotification = function() {
        	var notificationListPop = $(".notification-list-pop");
        	notificationListPop.find(".content > div").hide();
            //notificationList.removeClass("in");
            notificationBtn.popover('hide');
            $("body")
                .off("click")
                .find("iframe").contents().find("body").each(function() {
                    $(this).off("click");
                });
        };

        var activeDetailBtn = function() {
        	var menu = $(".notification-list-pop .menu");
            var detail = $(".notification-list-pop .detail");
            var notificationDetails = detail.find(".notification-detail");
            var current = notificationDetails.not(":hidden");
            var currentIndex = notificationDetails.index(current);

            var pre = detail.find(".pre");
            var next = detail.find(".next");
            pre.removeClass("none");
            next.removeClass("none");


            if(currentIndex == 0) {
                pre.addClass("none");
            }

            var currentMenu = $(menu.find(".view-content").get(currentIndex));
            if(currentMenu.hasClass("unread")) {
                currentMenu.removeClass("unread");
                var id = current.attr("id").replace("notificaiton-", "");
                $.ajax({
                    url: markReadUrl + id,
                    global: false,
                    error: function (xmlHR, textStatus, errorThrown) {
                        //ignore
                    }
                });
            }

            if(currentIndex == notificationDetails.length - 1) {
                next.addClass("none");
            }

        };

        var showNoComment = function() {
            notificationList.find(".content > div").hide();
            var noComment = $(".notification-list .no-comment");
            noComment.show();
        };
        var showMenu = function() {
            notificationList.find(".content > div").hide();
            var menu = $(".notification-list .menu");
            menu.show();
        };
        
        var showNoCommentPop = function() {
        	var notificationListPop = $(".notification-list-pop");
        	notificationListPop.find(".content > div").hide();
            var noCommentPop = $(".notification-list-pop .no-comment");
            noCommentPop.show();
        };
        var showMenuPop = function() {
        	var notificationListPop = $(".notification-list-pop");
        	notificationListPop.find(".content > div").hide();
        	var menuPop = $(".notification-list-pop .menu");
        	menuPop.show();
        };

        var initDetail = function(dataList) {
        	
            var detail = $(".notification-list-pop .detail");
            var detailList = detail.find(".list");
            var menu = $(".notification-list-pop .menu");
            
            var content = "";
            $(dataList).each(function(index, data) {
                content = content + detailContentTemplate.replace("{id}", data.id).replace("{title}", data.title).replace("{date}", data.date).replace("{content}", data.content);
            });
            detailList.html(content);
            detail.find(".notification-detail:first").show();
            detail.find(".back-notification-list").click(function() {
                slide(detail, menu, "left");
            });
            detail.find(".pre").click(function() {
                var current = detail.find(".notification-detail").not(":hidden");
                var pre = current.prev(".notification-detail");
                if (pre.length) {
                    slide(current, pre, "left");
                }
            });
            detail.find(".next").click(function() {
                var current = detail.find(".notification-detail").not(":hidden");
                var next = current.next(".notification-detail");
                if (next.length) {
                    slide(current, next, "right");
                }
            });
            slide(menu, detail, "right");

            return false;
        };


        var initMenu = function(dataList, hasUnread) {
        	
        	var menu = $(".notification-list-pop .menu");
            var menuList = menu.find(".list");

            var content = "";
            $(dataList).each(function (index, data) {
                content = content + contentTemplate.replace("{unread}", data.read ? "" : "unread").replace("{title}", data.title).replace("{date}", data.date);
            });
            content = content + moreContent;
            menuList.html(content);

            menu.find(".view-content").click(function() {
                initDetail(dataList);
            });
            menu.find(".view-all-notification").click(function() {
                viewAllNotification();
            });

            /*if(hasUnread) {
                showNotification();
            }*/

            return false;
        };
        var slide = function(from, to, direction) {
            from.css({
                position: 'relative',
                width:"100%"
            });
            from.stop(true).hide("slide", {direction : direction == "left" ? "right" : "left"}, function() {
                from.css({
                    position : "",
                    width : "",
                    left : ""
                });
            });
            to.css({
                position: 'absolute',
                top: to.is(".notification-detail") ? to.closest(".detail").find(".title").outerHeight() + "px" : "0px",
                left: "0px",
                width: "100%",
                display : "none"
            });
            to.stop(true).show("slide", {direction : direction}, function() {
                to.css({
                    position : "",
                    left : "",
                    top : "",
                    width : ""
                });
                if(to.is(".notification-detail") || to.is(".detail")) {
                    activeDetailBtn();
                }
            });
        };

        var showNotification = function() {
        	notificationBtn.popover('show');
            //notificationList.addClass("in");
        };
        
        var menuTemplate = $(".notification-list .menu");
		if(menuTemplate.find(".view-content").length) {
            showMenu();
        } else {
            showNoComment();
        }
		
        initPopover();
        hideNotification();

        return {
            update : function(dataList) {

                if(!dataList.length) {
                	showNoCommentPop();
                    return;
                }

                var hasUnread = false;
                for(var i = 0, l = dataList.length; i < l; i++) {
                    var data = dataList[i];
                    if(!data.read) {
                        hasUnread = true;
                    }
                    data.title = data.title.replace("{ctx}", window.Mds.AppRoot);
                    data.content = data.content.replace("{ctx}", window.Mds.AppRoot);
                }

                initPopover(dataList, hasUnread);
                if(hasUnread) {
                    showNotification();
                }
            }
        };
    },
    contentLoaded:function(n,t){var l="complete",s="readystatechange",u=!1,h=u,c=!0,i=n.document,a=i.documentElement,e=i.addEventListener?"addEventListener":"attachEvent",v=i.addEventListener?"removeEventListener":"detachEvent",f=i.addEventListener?"":"on",r=function(e){(e.type!=s||i.readyState==l)&&((e.type=="load"?n:i)[v](f+e.type,r,u),!h&&(h=!0)&&t.call(n,null))},o=function(){try{a.doScroll("left")}catch(n){setTimeout(o,50);return}r("poll")};if(i.readyState==l)t.call(n,"lazy");else{if(i.createEventObject&&a.doScroll){try{c=!n.frameElement}catch(y){}c&&o()}i[e](f+"DOMContentLoaded",r,u),i[e](f+s,r,u),n[e](f+"load",r,u)}},
    /**
     * loading url within tab async
     */
    loadingToCenterIframe: function (panel, url, loadingMessage, forceRefresh) {
        panel.data("url", url);

        var panelId = panel.prop("id");
        var iframeId = "iframe-" + panelId;
        var iframe = $("#" + iframeId);

        if (!iframe.length || forceRefresh) {
        	if (!isMicrosoftEdge()){
        		$.mdsDialog.waiting(loadingMessage);
        	}
        	
            if(!iframe.length) {
                iframe = $("iframe[tabs=true]:last").clone(true);
                iframe.prop("id", iframeId);
                $("iframe[tabs=true]:last").after(iframe);
            };
            if (!isMicrosoftEdge()){
	            iframe.prop("src", url).one("load DOMContentLoaded readystatechange", function () {
	                $.mainframe.activeIframe(panelId, iframe);
	                
	                //console.log('waitingOver');
	                $.mdsDialog.waitingOver();
	            });
            }else{
            	iframe.prop("src", url);
                setTimeout(function() {
                	$.mainframe.activeIframe(panelId, iframe);                        
                }, 200);
            }           
            /*iframe.onreadystatechange =  function(){
				if(iframe[0].readyState == "complete" || iframe[0].readyState == "loaded"){
					$.mainframe.activeIframe(panelId, iframe);                        
				}
				$.mainframe.activeIframe(panelId, iframe);                        
            };*/
        } else {
            $.mainframe.activeIframe(panelId, iframe);
        }

    },
    activeIframe: function (panelId, iframe) {
        if (!iframe) {
            iframe = $("#iframe-" + panelId);
        }
        var layout = $.layouts.layout;
        if (layout.panes.center.prop("id") == iframe.prop("id")) {
            return;
        }
        layout.panes.center.hide();
        layout.panes.center = iframe;
        layout.panes.center.show();
        layout.resizeAll();
        //$.tabs.initTabScrollHideOrShowMoveBtn(panelId);
    },
    
    initCalendar : function() {

        var date = new Date();
        var d = date.getDate();
        var m = date.getMonth();
        var y = date.getFullYear();

        var calendar = $('#calendar').fullCalendar({
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'month,agendaWeek,agendaDay'
            },
            events: window.Mds.AppRoot + "/sys/myCalendars/load",
            eventDrop: function(event, delta) {
                moveCalendar(event);
            },
            eventClick: function(event, delta) {
                viewCalendar(event);
            },
            loading: function(bool) {
                if (bool) $('#loading').show();
                else $('#loading').hide();
            },
            editable: true,
            selectable: true,
            selectHelper: true,
            select: function(start, end, allDay) {
                openNewCalendarForm(start, end);
                calendar.fullCalendar('unselect');
            }
        });

        if (window.Mds.isWidthLessThan(991)){
        	$('button.fc-prev-button').before('<button type="button" class="fc-button fc-button-add fc-state-default fc-corner-left fc-corner-right"><i class="fa fa-plus icon-white"></i></button>');
        }else{
        	$('button.fc-prev-button').before('<button type="button" class="fc-button fc-button-add fc-state-default fc-corner-left fc-corner-right"><i class="fa fa-plus icon-white"></i>' + $.mainframe.options.i18n.addevent + '</button>');
        }

        $(".fc-button-add").click(function() {
            openNewCalendarForm();
        });

        function openNewCalendarForm(start, end) {
            var url = window.Mds.AppRoot + "/sys/myCalendars/new";
            if(start) {
                //start = $.fullCalendar.moment.format(start, "yyyy-MM-dd HH:mm:ss");
                //end = $.fullCalendar.formatDate(end, "yyyy-MM-dd HH:mm:ss");
            	start = $.fullCalendar.formatDate(start, "YYYY-MM-DD HH:mm:ss");
            	end = $.fullCalendar.formatDate(end, "YYYY-MM-DD HH:mm:ss");
                url = url + "?start=" + start + "&end=" + end;
            }
            
            var okTitle = '"' + $.mainframe.options.i18n.ok + '"';           
            jsPanel.create({
                position:    "center",
                panelSize: "auto 480",
                //contentFetch: url,
                contentFetch: {
                    resource: url,
                    done: (panel, response) => {
                        panel.content.innerHTML = response;
                        var form =$(panel.content).find("#editForm");
                        form.find("#cancel").click(function(){
                            if (confirm($.mainframe.options.i18n.discardchanges)){
                                panel.close();
                            }
                        });
                        form.find("#save").click(function() {
                            var form =$(panel.content).find("#editForm");
                            if(!validateMyCalendar(form.get(0))) {
                                form.find(".form-group").addClass('error');
                                
                                return;
                            }
                            
                            var url = window.Mds.AppRoot + "/sys/myCalendars/new";
                            $.post(url, form.serialize(), function(result) {
                                if(result && result.status == 200){
                                    panel.close();
                                    calendar.fullCalendar("refetchEvents");
                                }
                            }, 'json');
                        });
                    }
                },
                headerTitle: $.mainframe.options.i18n.addevent,
                theme:       "rebeccapurple",
                /*callback:    function (panel) {
                    var form =$(panel.content).find("#editForm");
                    form.find("#cancel").click(function(){
                        panel.close();
                    });
                    form.find("#save").click(function() {
                        var form =$(panel.content).find("#editForm");
                        if(!validateMyCalendar(form.get(0))) {
                            form.find(".form-group").addClass('error');
                            
                            return;
                        }
                        
                        var url = window.Mds.AppRoot + "/sys/myCalendars/new";
                        $.post(url, form.serialize(), function(result) {
                            if(result && result.status == 200){
                                calendar.fullCalendar("refetchEvents");
                            }
                        }, 'json');
                    });
                },*/
                onbeforeclose: function(panel, status, closedByUser) {
                    if (closedByUser)
                        return confirm($.mainframe.options.i18n.discardchanges);
    
                    return true;                
                    /*var form =$(panel.content).find("#editForm");
                    if(!validateMyCalendar(form.get(0))) {
                        form.find(".form-group").addClass('error');
                        
                        return false;
                    }
                    
                    return true;*/
                }/*,
                onclosed: function(panel, closedByUser) {
                    //form.attr("action", window.Mds.AppRoot + "/sys/myCalendars/new").submit();
                    var form =$(panel.content).find("#editForm");
                    var url = window.Mds.AppRoot + "/sys/myCalendars/new";
                    $.post(url, form.serialize(), function(result) {
                        if(result && result.status == 200){
                            calendar.fullCalendar("refetchEvents");
                        }
                    }, 'json');
                }*/
            });
                        
            /*$.mdsDialog.modalDialog($.mainframe.options.i18n.addevent, "iframe:" + url, { //
                draggable: true,
                height:480,
                width:640,
                maxHeight:600,
                maxWidth:800,
                iframeScrolling:'no',
                ok : function(modal) {
                	var iframe1 = $(".bootstrap-dialog-iframe");
                    var form =iframe1.contents().find("#editForm");
                    if(!validateMyCalendar(form.get(0))) {
                    	form.find(".form-group").addClass('error');
                    	
                        return false;
                    }

                    //form.attr("action", window.Mds.AppRoot + "/sys/myCalendars/new").submit();
                    var url = window.Mds.AppRoot + "/sys/myCalendars/new";
                    $.post(url, form.serialize(), function(result) {
                    	if(result && result.status == 200){
                    		calendar.fullCalendar("refetchEvents");
                    	}
                    }, 'json');

                    return true;
                }
            });*/
        }

        function moveCalendar(event) {
            var url = window.Mds.AppRoot + "/sys/myCalendars/move";
            var id = event.id;
            var start = $.fullCalendar.formatDate(event.start, "YYYY-MM-dd HH:mm:ss");
            var end = $.fullCalendar.formatDate(event.end, "YYYY-MM-dd HH:mm:ss");
            url = url + "?id=" + id;
            url = url + "&start=" + start + "&end=" + end;

            $.post(url, function(result) {
            	if(result && result.status == 200){
            		calendar.fullCalendar("refetchEvents");
            	}
            }, 'json');
        }
        
        function deleteCalendar(eventId) {
        	var deleted = false;
        	$.mdsDialog.confirm($.mainframe.options.i18n.suretodelete, $.mainframe.options.i18n.suretodelete, {
        		draggable: true,
                ok : function() {
                	var url = window.Mds.AppRoot + "/sys/myCalendars/delete?id=" + eventId;
                    $.post(url, function(result) {
                    	if(result && result.status == 200){
                    		deleted = true;
                    	}
                    }, 'json');
                    
                    return true;
                },
        	});
        	
        	return deleted;
        }

        function viewCalendar(event) {
            var url = window.Mds.AppRoot + "/sys/myCalendars/view/" + event.id;
            jsPanel.create({
                position:    "center",
                panelSize: "auto 480",
                ContentSize: "480 480",
                //contentFetch: url,
                contentFetch: {
                    resource: url,
                    done: (panel, response) => {
                        panel.content.innerHTML = response;
                        var form =$(panel.content).find("form");
                        form.find("#cancel").click(function(){
                            panel.close();
                        });
                        form.find("#delete").click(function() {
                            var deleted = false;
                            if (confirm($.mainframe.options.i18n.suretodelete)){
                                var url = window.Mds.AppRoot + "/sys/myCalendars/delete?id=" + event.id;
                                $.ajax({
                                    type: "POST",
                                    async: false,
                                    url: url,
                                    dataType : "json",
                                    success: function (result) {
                                        if(result && result.status == 200){
                                            deleted = true;
                                            panel.close();
                                        }
                                    },
                                    error: function (response) {
                                        alert(response.responseText);
                                        deleted=false;
                                    }
                                });
                            }
                            
                            if (deleted){
                                calendar.fullCalendar("refetchEvents");
                            }
                        });
                    }
                },
                headerTitle: $.mainframe.options.i18n.viewevent,
                theme:       "rebeccapurple"
            });
            
            /*$.mdsDialog.modalDialog($.mainframe.options.i18n.viewevent, "iframe:" + url, {
                draggable: true,
                height:480,
                width:640,
                maxHeight:600,
                maxWidth:800,
                iframeScrolling:'no',
                ok : function(modal) {               	
                	var deleted = false;
                	if (confirm($.mainframe.options.i18n.suretodelete)){
                		var url = window.Mds.AppRoot + "/sys/myCalendars/delete?id=" + event.id;
                		$.ajax({
                			type: "POST",
                			async: false,
                			url: url,
                			dataType : "json",
                			success: function (result) {
                				if(result && result.status == 200){
                            		deleted = true;
                            	}
                			},
                			error: function (response) {
                				alert(response.responseText);
                				deleted=false;
                			}
                		});
                	}
                	
                	if (deleted){
                    	calendar.fullCalendar("refetchEvents");
                    }
                	
               		return deleted;
                },
                buttons:[ {
                    icon : 'fa fa-trash',
                    label : 'Delete',
                    cssClass : 'btn-default',
                	}, {
                    cssClass : 'btn-primary',
                }]
            });*/
        }
        
        $("body").on("click", ".btn-delete-calendar", function() {
            var $this = $(this);
            $.mdsDialog.confirm($.mainframe.options.i18n.suretodelete, $.mainframe.options.i18n.suretodelete, {
                draggable: true,
                ok : function() {
                    var url = window.Mds.AppRoot + "/sys/myCalendars/delete?id=" + $this.data("id");
                    $.post(url, function(result) {
                    	if(result && result.status == 200){
	                        calendar.fullCalendar("refetchEvents");
                    	}
                    }, 'json');
                }
            });

        });
    }
    ,
    removeContextPath : function(url) {
        if(url.indexOf(window.Mds.AppRoot) == 0) {
            return url.substr(window.Mds.AppRoot.length);
        }
        return url;
    }
    ,
    /**
     * Asynchronous form or a href
     * @param $form
     * @param containerId
     */
    asyncLoad : function($tag, containerId) {
        if($tag.is("form")) {
            $tag.submit(function() {
                if($tag.prop("method").toLowerCase() == 'post') {
                    $.post($tag.prop("action"), $tag.serialize(), function(data) {
                        $("#" + containerId).replaceWith(data);
                    });
                } else {
                    $.get($tag.prop("action"), $tag.serialize(), function(data) {
                        $("#" + containerId).replaceWith(data);
                    });
                }
                return false;
            });
        } else if($tag.is("a")) {
            $tag.click(function() {
                $.get($tag.prop("href"), function(data) {
                    $("#" + containerId).replaceWith(data);
                });
                return false;
            });
        } else {
            $.mdsDialog.alert("The html tag does not support asynchronous loading, supported tags have form, a");
        }

    },
    /**
     * Readonly form
     * @param form
     */
    readonlyForm : function(form, removeButton) {
        var inputs = $(form).find(":input");
        inputs.not(":submit,:button").prop("readonly", true);
        if(removeButton) {
            inputs.remove(":button,:submit");
        }
    }
    ,

    /**
                   * 将$("N").val() ----> [1,2,3]
     */
    joinVar : function(elem, separator) {
        if(!separator) {
            separator = ",";
        }
        var array = new Array();
        $(elem).each(function() {
            array.push($(this).val());
        });

        return array.join(separator);
    },

    /**
     *   Asynchronous loading table sub-content (parent-child table)
     * @param toggleEle
     * @param tableEle
     * @param asyncLoadURL
     */
    toggleLoadTable : function(tableEle, asyncLoadURL) {
        var openIcon = "icon-plus-sign";
        var closeIcon = "icon-minus-sign";
        $(tableEle).find("tr .toggle-child").click(function() {
            var $a = $(this);
            //只显示当前的 其余的都隐藏
            $a.closest("table")
                .find(".toggle-child." + closeIcon).not($a).removeClass(closeIcon).addClass(openIcon)
                .end().end()
                .find(".child-data").not($a.closest("tr").next("tr")).hide();

            //如果是ie7
            if($(this).closest("html").hasClass("ie7")) {
                var $aClone = $(this).clone(true);
                if($aClone.hasClass(closeIcon)) {
                    $aClone.addClass(openIcon).removeClass(closeIcon);
                } else {
                    $aClone.addClass(closeIcon).removeClass(openIcon);
                }
                $(this).after($aClone);
                $(this).remove();
                $a = $aClone;
            } else {
                $a.toggleClass(openIcon);
                $a.toggleClass(closeIcon);
            }

            var $currentTr = $a.closest("tr");
            var $dataTr = $currentTr.next("tr");
            if(!$dataTr.hasClass("child-data")) {
                $.mdsDialog.waiting();
                $dataTr = $("<tr class='child-data' style='display: none;'></tr>");
                var $dataTd = $("<td colspan='" + $currentTr.find("td").size() + "'></td>");
                $dataTr.append($dataTd);
                $currentTr.after($dataTr);
                $dataTd.load(asyncLoadURL.replace("{parentId}", $a.data("id")),function() {
                    $.mdsDialog.waitingOver();
                });
            }
            $dataTr.toggle();

            return false;
        });

    },
};

$.layouts = {
    layout: null,
    /**Initialize Layout*/
    initLayout: function () {
        function resizePanel(panelName, panelElement, panelState, panelOptions, layoutName) {
            var tabul = $(".tabs-fix-top");
            if (panelName == 'north') {
                var top = 0;
                if($("html").hasClass("ie")) {
                    top = panelElement.height() - 35;

                } else {
                    top = panelElement.height() - 32;
                }
                if (panelState.isClosed) {
                    top = -56;
                }
                tabul.css("top", top);
            }

            if(panelName == "center") {
                //tabul.find(".ul-wrapper").andSelf().width(panelState.layoutWidth);
            	tabul.width(panelState.layoutWidth);
                //$.tabs.initTabScrollHideOrShowMoveBtn();
            }
        }

        this.layout = $('.index-panel').layout({
            west__size:  210
            ,   south__size: 30
            ,	west__spacing_closed:		20
            ,	west__togglerLength_closed:	100
            ,	west__togglerContent_closed:"M<BR>e<BR>n<BR>u"
            ,	togglerTip_closed:	"Open"
            ,	togglerTip_open:	"Close"
            ,	sliderTip:			"Slide Open"
            ,   resizerTip:         "Resize"
            ,   onhide: resizePanel
            ,   onshow: resizePanel
            ,   onopen: resizePanel
            ,   onclose: resizePanel
            ,   onresize: resizePanel
            ,	center__maskContents:true // IMPORTANT - enable iframe masking
            ,   north : {
                togglerLength_open : 0
                ,  resizable : false
                ,  size: 90
            },
            south: {
                resizable:false
            }
        });
    }
}

$.menus = {
    /**Initialize menu*/
    initMenu: function () {
        var menus = $("#treemenu");
        menus.metisMenu();

        menus.find("a").each(function () {
            var a = $(this);
            var title = a.text();
            var href = a.attr("href");
            //a.attr("href", "#");//virtuecai update 开发模式, 便于调试
            if (href == "#" || href == '') {
                return;
            }

            var active = function(a, forceRefresh) {
                /*menus.find("a").closest("li >.li-wrapper").removeClass("active");
                a.closest("li > .li-wrapper").addClass("active");*/
            	a.addClass("active"); 
                var oldPanelIndex = a.data("panelIndex");
                var activeMenuCallback = function(panelIndex) {
                    /*alert(a.data("panelIndex"));
                    if(!a.data("panelIndex") || a.data("panelIndex") == '') {
                        a.data("panelIndex", panelIndex);
                        a.attr("id", "treemenu-" + panelIndex);
                    }*/
                    a.data("panelIndex", panelIndex);
                    a.attr("id", "treemenu-" + panelIndex);
                }
                $.tabs.activeTab(oldPanelIndex, title, href, forceRefresh, activeMenuCallback);

                return false;
            }

            a.closest("li")
                .click(function () {
                    active(a, false);
                    return false;
                }).dblclick(function() {
                    active(a, true);//Double click to refresh force
                    return false;
                });
        });
    }
}

$.navmenus = {
    /** Initialize header menu */
    initMenu: function () {
        var navmenus = $("#navmenu");
        navmenus.find("a").each(function () {
            var a = $(this);
            var title = a.text();
            var href = a.attr("href");
            if (href == "#" || href == '') {
                return;
            }

            var active = function(a, forceRefresh) {
            	var oldPanelIndex = a.data("panelIndex");
                var activeMenuCallback = function(panelIndex) {
                    if(!a.data("panelIndex") || a.data("panelIndex") == '') {
                        a.data("panelIndex", panelIndex);
                        a.attr("id", "treemenu-" + panelIndex);
                    }
                }
                $.tabs.activeTab(oldPanelIndex, title, href, forceRefresh, activeMenuCallback);
                a.closest("li").closest("ul").closest("li").removeClass("open"); //.addClass("menu-header-icon");

                return false;
            }
            
            a.closest("li")
            .click(function () {
                active(a, false);
                return false;
            }).dblclick(function() {
                active(a, true);//Double click to refresh force
                return false;
            });
        });
    },

	NavMenuInTab: function (a, title, href) {
		/*var oldPanelIndex = $(a).data("panelIndex");
        var activeMenuCallback = function(panelIndex) {
            if(!$(a).data("panelIndex")) {
            	$(a).data("panelIndex", panelIndex);
            	$(a).attr("id", "treemenu-" + panelIndex);
            }
        }
        $.tabs.activeTab(oldPanelIndex, title, href, false, activeMenuCallback);*/
		//$($.find("#treemenu a:contains(" + title + ")")).dblclick();
		$($.find("#treemenu a[href='" + href + "']")).dblclick();
        //$.tabs.activeMenu($.find("#treemenu a:contains(" + title + ")").data("panelIndex"));
        //return false;
	}
}

$.tabs = {
    tabs: null,
    tabDatas:null,
    tabsLiContent:null,
    tabsLiAContent:null,
    tabsPostProcessors:null,
    maxTabIndex : 1,
    /*starting index for tab create by user*/
    customTabStartIndex : 9999999999,
    /**Initialize tab */
    initTab: function () {   	        
        var tabDatas = [
            { panelId: 'tabs-0', tooltip: 'Welcome!',  title: 'Welcome!', content: '', active: true, disabled: false, url:'/welcome' }
         ];
        $.tabs.tabDatas = tabDatas;
        
         var tabsLiContent = tabDatas.map(function(tab) {
             return '<li role="presentation" title="' + tab.tooltip + '" data-trigger="hover" class="nav-item"></li>';
         });
         $.tabs.tabsLiContent = tabsLiContent;
         
         var tabsLiAContent = tabDatas.map(function(tab) {
             return '<div class="d-table" style="height:35px;"><div class="d-table-row"><a class="nav-link d-table-cell py-0 align-middle" href="#tabs-0" role="tab" data-toggle="tab" aria-controls="tabs-0" aria-selected="false">' + tab.title  //style="display:inline-block; width: 14px; height: 14px"
             	+ '</a><div class="d-table-cell menu"><div class="d-table" style="height:35px;"><span class="d-table-row" style="width: 14px; height: 14px" role="presentation"></span><span class="fa fa-sync d-table-row" role="presentation" title="Refresh"></span></div></div></div></div>';
         });
         $.tabs.tabsLiAContent = tabsLiAContent;
         
         var tabsPostProcessors = tabDatas.map(function(tab) {
             return function($li, $a) {
	           ($li, $a).click(function() {
	                 console.log("anchor click! tab.tooltip: ", tab.tooltip);
	                 setTimeout(function() {
		                 $.tabs.activeTabById(tab.panelId);
	                     $.tabs.activeMenu(tab.panelId);
	                     $.mainframe.activeIframe(tab.panelId);
	                 }, 0);
	           });
             };
        });
        $.tabs.tabsPostProcessors = tabsPostProcessors;
        
        var tabs = $(".tabs-bar").scrollingTabs({
            tabs: $.tabs.tabDatas, // required,
            propPaneId: 'panelId', // optional - pass in default value for demo purposes
            propTitle: 'title', // optional - pass in default value for demo purposes
            propActive: 'active', // optional - pass in default value for demo purposes
            propDisabled: 'disabled', // optional - pass in default value for demo purposes
            propContent: 'content', // optional - pass in default value for demo purposes
            scrollToTabEdge: false, // optional - pass in default value for demo purposes
            disableScrollArrowsOnFullyScrolled: true,
            cssClassLeftArrow: 'fa fa-chevron-left',
            cssClassRightArrow: 'fa fa-chevron-right',
            enableSwiping: true,
            tabsLiContent: $.tabs.tabsLiContent,
            tabsLiAContent: $.tabs.tabsLiAContent,
            tabsPostProcessors: $.tabs.tabsPostProcessors,
            //widthMultiplier: 0.7,
            bootstrapVersion: 4
            /*tabClickHandler: function () {
              console.log("click!! ", Date.now());
            }*/
        }).on('ready.scrtabs', function() {
        	$.tabs.tabDatas.forEach(function(tab) {
        		var newPanel = $("#" + tab.panelId);
                newPanel.data("index", tab.panelId.replace("tabs-", ""));
                if (tab.url != ''){
                	newPanel.data("url", window.Mds.AppRoot + tab.url);
                }
            });
        });
        $.tabs.tabs = tabs;
        
        tabs.delegate("span.fa-times", "click", function () {
            var panelId = $(this).closest("li").find('a[role="tab"]').attr("aria-controls");
            //$(this).closest("li").remove();
            setTimeout(function() {
                $.tabs.removeTab(panelId);
            }, 0);
        });
        tabs.delegate("span.fa-sync", "click", function () {
            //var panelId = $(this).closest("li").attr("aria-controls");
        	var panelId = $(this).closest("li").find('a[role="tab"]').attr("aria-controls");
            setTimeout(function() {
                $.tabs.activeTab(panelId, null, null, true);
            }, 0);
        });

        tabs.bind("keyup", function (event) {
            if (event.altKey && event.keyCode === $.ui.keyCode.BACKSPACE) {
                var panelId = tabs.find(".nav-item.active").find('a[role="tab"]').attr("aria-controls");
                setTimeout(function() {
                    $.tabs.removeTab(panelId);
                }, 0);
            }
        });

        //$.tabs.initTabScroll();
        $.tabs.initTabContextMenu();
    },
    activeMenu : function(tabPanelId) {
        $('#treemenu').metisMenu('dispose');
    	var currentMenu = $("#treemenu-" + tabPanelId.replace("tabs-", ""));
    	if(currentMenu.length) {
	    	if ( currentMenu.hasClass("active") && currentMenu.closest("li").parents("li").hasClass("mm-active")){
	    		$("#treemenu .nav-link.active").removeClass("active");
	    		currentMenu.addClass("active");
                $('#treemenu').metisMenu();
	    		
	    		return;
	    	}
    	}
    	
        $("#treemenu .nav-link.active").removeClass("active");
        $("#treemenu").find("ul").siblings("a").attr("aria-expanded", false);
        $("#treemenu").find("ul").removeClass("mm-show in");
        $("#treemenu .nav-item.mm-active").removeClass("mm-active");

        if(currentMenu.length) {
            //expand parent menu
            currentMenu.closest("li").parents("li").addClass("mm-active");
        	currentMenu.parents("ul.submenu").addClass("mm-show in");
            currentMenu.parents("ul.submenu").attr("style", "");
            currentMenu.parents("ul").siblings("a").attr("aria-expanded", true);
            currentMenu.addClass("active");
        }else{
            $("#treemenu").find("li:first").addClass("mm-active");
        	$("#treemenu").find("ul.submenu:first").addClass("mm-show in");
            $("#treemenu").find("ul.submenu:first").attr("style", "");
        	$("#treemenu").find("ul:first").siblings("a").attr("aria-expanded", true);
        	/*$("#treemenu .nav-item.active:first").addClass("active");
            $("#treemenu .nav-link.active:first").addClass("active"); */
        }
        $('#treemenu').metisMenu();
    },
    removeTab : function(panelId) {
        var tabs = $.tabs.tabs;
        var panel = $("#" + panelId);
        var iframe = $("#iframe-" + panelId);

        var currentMenu = $("#treemenu-" + panelId.replace("tabs-", ""));
        if(currentMenu.length) {
            currentMenu.attr("id", "");
            currentMenu.attr("panelIndex", "");
            currentMenu.closest("li").removeClass("active");
        }

        var tabIndex=-1;
        $.tabs.tabDatas.some(function (tab, index, array) {
	    	if (tab.panelId == panelId) {
	    		tabIndex = index; // exit loop
	    		return true;
	        }
        });
        $.tabs.tabDatas.splice(tabIndex, 1);
        $.tabs.tabsLiContent.splice(tabIndex, 1);
        $.tabs.tabsLiAContent.splice(tabIndex, 1);
        $.tabs.tabsPostProcessors.splice(tabIndex, 1);
        var nextTab = tabIndex;
        if ($.tabs.tabDatas.length <= tabIndex){
        	nextTab = tabIndex - 1;
        }
        $.tabs.tabDatas[nextTab].active=true;
        
        tabs.scrollingTabs('refresh');
        tabs.find(".menu").removeClass("d-table-cell").addClass("d-none");
        tabs.find(".nav-item.active").find(".menu").removeClass("d-none").addClass("d-table-cell");
        $.mainframe.activeIframe($.tabs.tabDatas[nextTab].panelId);
        $.tabs.activeMenu($.tabs.tabDatas[nextTab].panelId);

        var iframeDom = iframe[0];
        iframeDom.src = "";
        iframeDom.contentWindow.document.write('');
        iframeDom.contentWindow.close();
        iframe.remove();
        var isIE = !-[1,];
        if (isIE) {
            CollectGarbage();
        }

    },
    /**
     * Create new tab
     * @param title
     * @param panelIndex
     */
    /**
     * Create new tab
     * @param title
     * @param panelIndex
     */
    createTab : function(title, panelIndex) {
        var tabs = $.tabs.tabs;
        
        var newPanelIndex = panelIndex || $.tabs.maxTabIndex++ || 1;
        var newPanelId = "tabs-" + newPanelIndex;
        
        var newTab = {
        	panelId: newPanelId,
	        tooltip: title,
	        title: title,
	        content: '',
	        active: true,
	        disabled: false,
	        url:''
	    };
	
	    console.log("append new tab ", newTab.title);
	
	    var tabDatas = $.tabs.tabDatas;
	    // deactivate currently active tab
	    tabDatas.some(function (tab) {
	    	if (tab.active) {
	          tab.active = false;
	          return true; // exit loop
	        }
        });

	    tabDatas.push(newTab);
	    $.tabs.tabsLiContent.push('<li role="presentation" title="' + newTab.tooltip + '" data-trigger="hover" class="nav-item"></li>');
	    $.tabs.tabsLiAContent.push('<div class="d-table" style="height:35px;"><div class="d-table-row"><a class="nav-link d-table-cell py-0 align-middle" href="#'+ newTab.panelId +'"  role="tab" data-toggle="tab" aria-controls="'+ newTab.panelId +'" aria-selected="false">' + newTab.title  //style="display:inline-block; width: 14px; height: 14px"
          	+ '</a><div class="d-table-cell menu"><div class="d-table" style="height:35px;"><span class="d-table-row fa fa-times" role="presentation" title="Close"></span><span class="fa fa-sync d-table-row" role="presentation" title="Refresh"></span></div></div></div></div>');
      
	    $.tabs.tabsPostProcessors.push(function($li, $a) {
	    	  ($li, $a).click(function() {
	              console.log("anchor click! tab.tooltip: ", newTab.tooltip);
	              setTimeout(function() {
	                  $.tabs.activeTabById(newTab.panelId);
	                  $.tabs.activeMenu(newTab.panelId);
	                  $.mainframe.activeIframe(newTab.panelId);
	              }, 0);
	          });
        });
	
	    tabs.scrollingTabs('refresh', {
			forceActiveTab: true // make our new tab active
	    });


        var newPanel = $("#" + newPanelId);
        newPanel.data("index", newPanelIndex);

        return newPanel;
    },
    /**
     * Activate a tab with the specified index, create new tab if not exists
     * @param panelIdOrIndex
     * @param title
     * @param url
     * @param forceRefresh
     * @return {*}
     */
    activeTab: function (panelIdOrIndex, title, url, forceRefresh, callback) {
        var tabs = $.tabs.tabs;
        var panelId = "tabs-" + ("" + panelIdOrIndex).replace("tabs-", "");
        
        var currentTabPanel = $("#" + panelId);
        if (!currentTabPanel.length) {
            currentTabPanel = $.tabs.createTab(title, panelIdOrIndex);
        }else{
        	var tabDatas = $.tabs.tabDatas;
            // deactivate orgin active tab
            tabDatas.some(function (tab) {
              if (tab.active) {
                tab.active = false;
                return true;
              }
            });
            // activate currently active tab
            tabDatas.some(function (tab) {
              if (tab.panelId == panelId) {
                  tab.active = true;
                  return true;
               }
            });
        }

        if(callback) { 
            callback(currentTabPanel.data("index"));
        }

        if(!url) {
            url = currentTabPanel.data("url");
        }

        setTimeout(function() {
            $.mainframe.loadingToCenterIframe(currentTabPanel, url, null, forceRefresh);
            tabs.scrollingTabs('refresh', {
                forceActiveTab: true //make our new tab active
            });
            tabs.find(".menu").removeClass("d-table-cell").addClass("d-none");
            tabs.find(".nav-item.active").find(".menu").removeClass("d-none").addClass("d-table-cell");
            $.tabs.activeMenu("tabs-" + currentTabPanel.data("index"));
        }, 0);
        
        return currentTabPanel.data("index");
    },
    
    activeTabById: function (panelId) {
    	var tabDatas = $.tabs.tabDatas;
        // deactivate orgin active tab
        tabDatas.some(function (tab) {
          if (tab.active) {
            tab.active = false;
            return true;
          }
        });
        
        var tabIndex=-1;
        // activate currently active tab
        tabDatas.some(function (tab, index, array) {
          if (tab.panelId == panelId) {
        	  tabIndex = index; // exit loop
              tab.active = true;
              return true;
           }
        });
        /*$.tabs.tabsLiAContent.forEach(function(aContent, index, array){
        	array[index] = aContent.replace("d-table-cell menu", "d-none menu");
        });
        $.tabs.tabsLiAContent[tabIndex] = $.tabs.tabsLiAContent[tabIndex].replace("d-none menu", "d-table-cell menu");*/

        $.tabs.tabs.scrollingTabs('refresh', {
            forceActiveTab: true // make our new tab active
        });
        $.tabs.tabs.find(".menu").removeClass("d-table-cell").addClass("d-none");
        $.tabs.tabs.find(".nav-item.active").find(".menu").removeClass("d-none").addClass("d-table-cell");
    },

    initTabScrollHideOrShowMoveBtn : function(panelId) {
        var $ulWrapper = $(".tabs-bar .ul-wrapper");
        var $lastLI = $ulWrapper.find("ul li:last");
        var $firstLI = $ulWrapper.find("ul li:first");

        var ulWapperOffsetLeft = $ulWrapper.offset().left;
        var ulWrapperLeftPos = ulWapperOffsetLeft + $ulWrapper.width();

        var hideOrShowBtn = function() {
            var lastLIOffsetLeft = $lastLI.offset().left;
            var lastLILeftPos = lastLIOffsetLeft + $lastLI.width();
            var firstLIOffsetLeft = $firstLI.offset().left;

            var $leftBtn = $(".tabs-bar .icon-chevron-left");
            var $rightBtn = $(".tabs-bar .icon-chevron-right");

            if (ulWapperOffsetLeft == firstLIOffsetLeft) {
                $leftBtn.hide();
            } else {
                $leftBtn.show();
            }
            if (ulWrapperLeftPos >= lastLILeftPos) {
                $rightBtn.hide();
            } else {
                $rightBtn.show();
            }
        };

        if(panelId) {

            var $li = $(".tabs-bar").find("li[aria-labelledby='" + $("#" + panelId).attr("aria-labelledby") + "']");

            var liOffsetLeft = $li.offset().left;
            var liLeftPos = liOffsetLeft + $li.width();

            var isLast = $li.attr("aria-controls") == $lastLI.attr("aria-controls");

            //if the current tab not hidden, not need scroll 
            if((ulWapperOffsetLeft <= liOffsetLeft) && (liLeftPos <= ulWrapperLeftPos) && !isLast) {
                return;
            }

            var leftPos = 0;
            //right
            if(ulWrapperLeftPos < liLeftPos || isLast) {
                leftPos = $ulWrapper.scrollLeft() + (liLeftPos - ulWrapperLeftPos) + (isLast ? 10 :55);
            } else {
                //left
                leftPos = "-=" + (ulWapperOffsetLeft - liOffsetLeft + 55);
            }

            $ulWrapper.animate({scrollLeft: leftPos}, 600, function () {
                hideOrShowBtn();
            });
        } else {
            hideOrShowBtn();
        }


    },
    
    initTabScroll: function () {
        var move = function (step) {
            return function () {
                var $ulWrapper = $(".tabs-bar .ul-wrapper");
                var $lastLI = $ulWrapper.find("ul li:last");

                var leftPos = $ulWrapper.scrollLeft() + step;

                var ulWrapperLeftPos = $ulWrapper.offset().left + $ulWrapper.width();
                var lastLILeftPos = $lastLI.offset().left + $lastLI.width();
                var maxLeftPos = lastLILeftPos - ulWrapperLeftPos;

                //right move
                if (step > 0) {
                    if (maxLeftPos <= step + step / 2) {
                        leftPos = $ulWrapper.scrollLeft() + maxLeftPos;
                    }
                    if (maxLeftPos <= 0) {
                        return;
                    }
                }

                //left move
                if (step < 0) {
                    if (leftPos < -step) {
                        leftPos = 0;
                    }
                }

                if (leftPos < 0) {
                    leftPos = 0;
                }
                $ulWrapper.animate({scrollLeft: leftPos}, 600, function () {
                    $.tabs.initTabScrollHideOrShowMoveBtn();
                });
            };
        };

        $(".tabs-bar .icon-chevron-left").click(function () {
            setTimeout(function() {move(-200)()}, 0);
        });
        $(".tabs-bar .icon-chevron-right").click(function () {
            setTimeout(function() {move(200)()}, 0);
        });

    },
    
    /**
     * Initialize context menu(right click menu)
     */
    initTabContextMenu : function() {
        //Initialize the right click menu
        var tabsMenu = $("#tabs-menu");
        //call this method to disable the right click menu in system
        $(document).bind('contextmenu', function (e) {
            var target = $(e.target);
            var clickTab = target.closest(".nav-tabs").length && target.is(".nav-link");

            if (clickTab && target.attr("href") == '#tabs-0') {
                return true;
            }
            if (clickTab) {
                showMenu(target.attr("id"), e.pageX - 5, e.pageY - 5);
                tabsMenu.mouseleave(function () {
                    hideMenu();
                });
                return false;
            }
            return true;
        });

        function hideMenu() {
            tabsMenu.hide();
            tabsMenu.data("tabId", "");
        }

        function showMenu(tabId, x, y) {
            tabsMenu.data("tabId", tabId);
            tabsMenu.css("left", x).css("top", y);
            tabsMenu.show();
        }

        function closeTab(tabId) {
            $("#" + tabId).parent().find(".fa-times").click();
        }
        tabsMenu.find(".close-current").click(function (e) {
            var currentTabId = tabsMenu.data("tabId");
            closeTab(currentTabId);
            hideMenu();
        });

        tabsMenu.find(".close-others").click(function (e) {
            var currentTabId = tabsMenu.data("tabId");
            var tabs = $.tabs.tabs.find(".ul-wrapper > ul > li > a");
            tabs.each(function() {
                var tabId = this.id;
                if(tabId != currentTabId) {
                    closeTab(tabId);
                }
            });
            hideMenu();
        });
        tabsMenu.find(".close-all").click(function (e) {
            var currentTabId = tabsMenu.data("tabId");
            var tabs = $.tabs.tabs.find(".ul-wrapper > ul > li > a");
            tabs.each(function() {
                var tabId = this.id;
                closeTab(tabId);
            });
            hideMenu();
        });

        tabsMenu.find(".close-left-all").click(function (e) {
            var currentTabId = tabsMenu.data("tabId");
            var tabs = $.tabs.tabs.find(".ul-wrapper > ul > li > a");
            var currentTabIndex = tabs.index($("#" + currentTabId));
            tabs.each(function(index) {
                if(index < currentTabIndex) {
                    var tabId = this.id;
                    closeTab(tabId);
                }
            });
            hideMenu();
        });
        tabsMenu.find(".close-right-all").click(function (e) {
            var currentTabId = tabsMenu.data("tabId");
            var tabs = $.tabs.tabs.find(".ul-wrapper > ul > li > a");
            var currentTabIndex = tabs.index($("#" + currentTabId));
            tabs.each(function(index) {
                if(index > currentTabIndex) {
                    var tabId = this.id;
                    closeTab(tabId);
                }
            });
            hideMenu();
        });
    },

    /**
     * Get index for next custom panel
     */
    nextCustomTabIndex : function() {
        var tabs = $.tabs.tabs;
        var maxIndex = $.tabs.customTabStartIndex;
        $.tabs.tabDatas.forEach(function(tab) {
            var index = parseInt(tab.panelId.replace("tabs-", ""));
            if(maxIndex < index) {
                maxIndex = index;
            }
        });

        return maxIndex + 1;

    }
};

$.parentchild = {
    /**
     * 初始化父子操作中的子表单
     * options
     *     {
                form : 表单【默认$("childForm")】,
                tableId : "表格Id"【默认"childTable"】,
                excludeInputSelector : "[name='_show']"【排除的selector 默认无】,
                trId : "修改的哪行数据的tr id， 如果没有表示是新增的",
                validationEngine : null 验证引擎,
                modalSettings:{//模态窗口设置
                    width:800,
                    height:500,
                    buttons:{}
                },
                updateUrl : "${ctx}/showcase/parentchild/parent/child/{id}/update" 修改时url模板 {id} 表示修改时的id,
                deleteUrl : "${ctx}/showcase/parentchild/parent/child/{id}/delete  删除时url模板 {id} 表示删除时的id,
            }
     * @param options
     * @return {boolean}
     */
    initChildForm : function(options) {
        var defaults = {
            form : $("#childForm"),
            tableId : "childTable",
            excludeInputSelector : "",
            trId : "",
            validationEngine : null
        };

        if(!options) {
            options = {};
        }
        options = $.extend({}, defaults, options);

        //如果有trId则用trId中的数据更新当前表单
        if(options.trId) {
            var $tr = $("#" + options.trId);
            if($tr.length && $tr.find(":input").length) {
                //因为是按顺序保存的 所以按照顺序获取  第一个是checkbox 跳过
                var index = 1;
                $(":input", options.form).not(options.excludeInputSelector).each(function() {
                    var $input = $(this);
                    var $trInput = $tr.find(":input").eq(index++);
                    if(!$trInput.length) {
                        return;
                    }
                    var $trInputClone = $trInput.clone(true).show();
                    //saveModalFormToTable 为了防止重名问题，添加了tr id前缀，修改时去掉
                    $trInputClone.prop("name", $trInputClone.prop("name").replace(options.trId, ""));
                    $trInputClone.prop("id", $trInputClone.prop("id").replace(options.trId, ""));

                    //克隆后 select的选择丢失了 TODO 提交给jquery bug?
                    if($trInput.is("select")) {
                        $trInput.find("option").each(function(i) {
                            $trInputClone.find("option").eq(i).prop("selected", $(this).prop("selected"));
                        });
                    }
                    if($trInput.is(":radio,:checkbox")) {
                        $trInputClone.prop("checked", $trInput.prop("checked"));
                    }

                    $trInputClone.replaceAll($input);
                });
            }
        }

        //格式化子表单的 input label
        $(":input,label", options.form).each(function() {
            var prefix = "child_";
            if($(this).is(":input")) {
                var id = $(this).prop("id");
                if(id && id.indexOf(prefix) != 0) {
                    $(this).prop("id", prefix + id);
                }
            } else {
                var _for = $(this).prop("for");
                if(_for && _for.indexOf(prefix) != 0) {
                    $(this).prop("for", prefix + _for);
                }
            }
        });

        options.form.submit(function() {
            if(options.validationEngine && !options.validationEngine.validationEngine("validate")) {
                return false;
            }
            return $.parentchild.saveModalFormToTable(options);
        });
    }
    ,
    //保存打开的模态窗口到打开者的表格中
    /**
     * options
     *     {
                form : 表单【默认$("childForm")】,
                tableId : "表格Id"【默认"childTable"】,
                excludeInputSelector : "[name='_show']"【排除的selector 默认无】,
                updateCallback : 【修改时的回调  默认 updateChild】,
                deleteCallback : 【删除时的回调默认 deleteChild】,
                trId : "修改的哪行数据的tr id， 如果没有表示是新增的"
            }
     * @param options
     * @return {boolean}
     */
    saveModalFormToTable :function(options) {
        var $childTable =  $("#" + options.tableId);
        var $childTbody = $childTable.children("tbody");

        if(!options.trId || options.alwaysNew) {
            var counter = $childTbody.data("counter");
            if(!counter) {
                counter = 0;
            }
            options.trId = "new_" + counter++;
            $childTbody.data("counter", counter);
        }
        var $lastTr = $("#" + options.trId, $childTbody);

        var $tr = $("<tr></tr>");
        $tr.prop("id", options.trId);
        if(!$lastTr.length || options.alwaysNew) {
            $childTbody.append($tr);
        } else {
            $lastTr.replaceWith($tr);
        }

        var $td = $("<td></td>");

        //checkbox
        $tr.append($td.clone(true).addClass("check").append("<input type='checkbox'>"));

        var $inputs = $(":input", options.form).not(":button,:submit,:reset", options.form);
        if(options.excludeInputSelector) {
            $inputs = $inputs.not(options.excludeInputSelector);
        }
        $inputs = $inputs.filter(function() {
            return $inputs.filter("[name='" + $(this).prop("name") + "']").index($(this)) == 0;
        });
        $inputs.each(function() {
            var $input = $("[name='" + $(this).prop("name") + "']", options.form);

            var val = $input.val();
            //使用文本在父页显示，而不是值
            //如果是单选按钮/复选框 （在写的过程中，必须在输入框后跟着一个label）
            if($input.is(":radio,:checkbox")) {
                val = "";
                $input.filter(":checked").each(function() {
                    if(val != "") {
                        val = val + ",";
                    }
                    val = val + $("label[for='" + $(this).prop("id") + "']").text();
                });
            }
            //下拉列表
            if($input.is("select")) {
                val = "";
                $input.find("option:selected").each(function() {
                    if(val != "") {
                        val = val + ",";
                    }
                    val = val + $(this).text();
                });
            }

            //因为有多个孩子 防止重名造成数据丢失
            $input.each(function() {
                if($(this).is("[id]")) {
                    $(this).prop("id", options.trId + $(this).prop("id"));
                }
                $(this).prop("name", options.trId + $(this).prop("name"));
            });
            $tr.append($td.clone(true).append(val).append($input.hide()));

        });

        $.table.initCheckbox($childTable);

        $.mainframe.cancelModelDialog();
        return false;
    }
    ,
    /**
     * 更新子
     * @param $a 当前按钮
     * @param updateUrl  更新地址
     */
    updateChild : function($tr, updateUrl, modalSettings) {
        if(updateUrl.indexOf("?") > 0) {
            updateUrl = updateUrl + "&";
        } else {
            updateUrl = updateUrl + "?";
        }
        updateUrl = updateUrl + "trId={trId}";

        //表示已经在数据库中了
        if($tr.is("[id^='old']")) {
            updateUrl = updateUrl.replace("{id}", $tr.prop("id").replace("old_", ""));
        } else {
            //表示刚刚新增的还没有保存到数据库
            updateUrl = updateUrl.replace("{id}", 0);
        }
        updateUrl = updateUrl.replace("{trId}", $tr.prop("id"));
        $.mdsDialog.modalDialog("修改", updateUrl, modalSettings);
    }
    ,
    /**
     * 以当前行复制一份
     * @param $a 当前按钮
     * @param updateUrl  更新地址
     */
    copyChild : function($tr, updateUrl, modalSettings) {
        if(updateUrl.indexOf("?") > 0) {
            updateUrl = updateUrl + "&";
        } else {
            updateUrl = updateUrl + "?";
        }
        updateUrl = updateUrl + "trId={trId}";
        updateUrl = updateUrl + "&copy=true";

        //表示已经在数据库中了
        if($tr.is("[id^='old']")) {
            updateUrl = updateUrl.replace("{id}", $tr.prop("id").replace("old_", ""));
        } else {
            //表示刚刚新增的还没有保存到数据库
            updateUrl = updateUrl.replace("{id}", 0);
        }
        updateUrl = updateUrl.replace("{trId}", $tr.prop("id"));
        $.mdsDialog.modalDialog("复制", updateUrl, modalSettings);
    }
    ,
    /**
     * 删除子
     * @param $a 当前按钮
     * @param deleteUrl 删除地址
     */
    deleteChild : function($a, deleteUrl) {
        $.mdsDialog.confirm({
            message : "确认删除吗？",
            ok : function() {
                var $tr = $a.closest("tr");
                //如果数据库中存在
                if($tr.prop("id").indexOf("old_") == 0) {
                    deleteUrl = deleteUrl.replace("{id}", $tr.prop("id").replace("old_", ""));
                    $.post(deleteUrl, function() {
                        $tr.remove();
                    });
                } else {
                    $tr.remove();
                }

            }
        });
    }
    ,
    /**
     * 初始化父子表单中的父表单
     * {
     *     form: $form 父表单,
     *     tableId : tableId 子表格id,
     *     prefixParamName : "" 子表单 参数前缀,
     *     modalSettings:{} 打开的模态窗口设置
     *     createUrl : "${ctx}/showcase/parentchild/parent/child/create",
     *     updateUrl : "${ctx}/showcase/parentchild/parent/child/{id}/update" 修改时url模板 {id} 表示修改时的id,
     *     deleteUrl : "${ctx}/showcase/parentchild/parent/child/{id}/delete  删除时url模板 {id} 表示删除时的id,
     * }
     */
    initParentForm : function(options) {


        var $childTable = $("#" + options.tableId);
        $.table.initCheckbox($childTable);
        //绑定在切换页面时的事件 防止误前进/后退 造成数据丢失
        $(window).on('beforeunload',function(){
            if($childTable.find(":input").length) {
                return "确定离开当前编辑页面吗？";
            }
        });
        $(".btn-create-child").click(function() {
            $.mdsDialog.modalDialog("新增", options.createUrl, options.modalSettings);
        });
        $(".btn-update-child").click(function() {
            var $trs = $childTable.find("tbody tr").has(".check :checkbox:checked:first");
            if(!$trs.length) {
                $.mdsDialog.alert("请先选择要修改的数据！");
                return;
            }
            $.parentchild.updateChild($trs, options.updateUrl, options.modalSettings);
        });

        $(".btn-copy-child").click(function() {
            var $trs = $childTable.find("tbody tr").has(".check :checkbox:checked:first");
            if(!$trs.length) {
                $.mdsDialog.alert("请先选择要复制的数据！");
                return;
            }
            $.parentchild.copyChild($trs, options.updateUrl, options.modalSettings);
        });


        $(".btn-delete-child").click(function() {
            var $trs = $childTable.find("tbody tr").has(".check :checkbox:checked");
            if(!$trs.length) {
                $.mdsDialog.alert("请先选择要删除的数据！");
                return;
            }
            $.mdsDialog.confirm({
                message: "确定删除选择的数据吗？",
                ok : function() {
                    var ids = new Array();
                    $trs.each(function() {
                        var id = $(this).prop("id");
                        if(id.indexOf("old_") == 0) {
                            id = id.replace("old_", "");
                            ids.push({name : "ids", value : id});
                        }
                    });

                    $.post(options.batchDeleteUrl, ids, function() {
                        $trs.remove();
                        $.table.changeBtnState($childTable);
                    });

                }
            });
        });

        options.form.submit(function() {
            //如果是提交 不需要执行beforeunload
            $(window).unbind("beforeunload");
            $childTable.find("tbody tr").each(function(index) {
                var tr = $(this);
                tr.find(".check > :checkbox").attr("checked", false);
                tr.find(":input").each(function() {
                    if($(this).prop("name").indexOf(options.prefixParamName) != 0) {
                        $(this).prop("name", options.prefixParamName + "[" + index + "]." + $(this).prop("name").replace(tr.prop("id"), ""));
                    }
                });
            });
        });
    }

}


/*
 * Project: Twitter Bootstrap Hover Dropdown
 * Author: Cameron Spear
 * Contributors: Mattia Larentis
 *
 * Dependencies?: Twitter Bootstrap's Dropdown plugin
 *
 * A simple plugin to enable twitter bootstrap dropdowns to active on hover and provide a nice user experience.
 *
 * No license, do what you want. I'd love credit or a shoutout, though.
 *
 * http://cameronspear.com/blog/twitter-bootstrap-dropdown-on-hover-plugin/
 */
;(function($, window, undefined) {
    // outside the scope of the jQuery plugin to
    // keep track of all dropdowns
    var $allDropdowns = $();

    // if instantlyCloseOthers is true, then it will instantly
    // shut other nav items when a new one is hovered over
    $.fn.dropdownHover = function(options) {

        // the element we really care about
        // is the dropdown-toggle's parent
        $allDropdowns = $allDropdowns.add(this.parent());

        return this.each(function() {
            var $this = $(this).parent(),
                defaults = {
                    delay: 100,
                    instantlyCloseOthers: true
                },
                data = {
                    delay: $(this).data('delay'),
                    instantlyCloseOthers: $(this).data('close-others')
                },
                settings = $.extend(true, {}, defaults, options, data),
                timeout;

            $this.hover(function() {
                if(settings.instantlyCloseOthers === true)
                    $allDropdowns.removeClass('open');

                window.clearTimeout(timeout);
                $(this).addClass('open');
            }, function() {
                timeout = window.setTimeout(function() {
                    $this.removeClass('open');
                }, settings.delay);
            });
        });
    };

    // apply dropdownHover to all elements with the data-hover="dropdown" attribute
    $(document).ready(function() {
        $('[data-hover="dropdown"]').dropdownHover();
    });
})(jQuery, this);


$(function () {
    //global disable ajax cache
    $.ajaxSetup({ cache: false });

    //$.layout = top.$.layout;
    //$.mainframe = top.$.mainframe;
    $.tabs = top.$.tabs;
    $.menus = top.$.menus;

    $("[data-toggle='tooltip']").each(function() {

        $(this).tooltip({delay:300});
    });

    /*$(document).ajaxError(function(event, request, settings) {

        $.mdsDialog.waitingOver();

        if(request.status == 0) {// Not handling when interrupt
            return;
        }

        top.$.mdsDialog.alert(request.responseText.replace(/(<refresh>.*<\/refresh>)/g, ""), "Network failure/system failure");
    });*/
});

