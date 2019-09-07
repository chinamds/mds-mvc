<script>
    $(document).ready(function() {

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
            events: "${ctx}/sys/myCalendars/load",
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

        $('span.fc-button-prev').before('<span class="fc-button fc-button-add fc-state-default fc-corner-left fc-corner-right">新增</span>');

        $(".fc-button-add").click(function() {
            openNewCalendarForm();
        });

        function openNewCalendarForm(start, end) {
            var url = "${ctx}/sys/myCalendars/new";
            if(start) {
            	start = $.fullCalendar.formatDate(start, "YYYY-MM-DD HH:mm:ss");
            	end = $.fullCalendar.formatDate(end, "YYYY-MM-DD HH:mm:ss");
                url = url + "?start=" + start + "&end=" + end;
            }
            top.$.jBox.open("iframe:" + url, '<fmt:message key="myCalendar.addevent"/>', 370, 430
            		, {buttons:{"<fmt:message key="button.ok" />":"ok", "<fmt:message key="button.close" />":true}, submit:function(v, h, f){
            			if (v=="ok"){
            				var form = h.find("#editForm");
                            if(!form.validationEngine('validate')) {
                                return false;
                            }
                            var url = "${ctx}/sys/myCalendars/new";
                            $.post(url, form.serialize(), function() {
                                calendar.fullCalendar("refetchEvents");
                            });

                            return true;
            			}
            		}
            	});
        }

        function moveCalendar(event) {
            var url = "${ctx}/sys/myCalendars/move";
            var id = event.id;
            var start = $.fullCalendar.formatDate(event.start, "yyyy-MM-dd HH:mm:ss");
            var end = $.fullCalendar.formatDate(event.end, "yyyy-MM-dd HH:mm:ss");
            url = url + "?id=" + id;
            url = url + "&start=" + start + "&end=" + end;

            $.post(url, function() {
                calendar.fullCalendar("refetchEvents");
            });
        }

        function viewCalendar(event) {
            var url = "${ctx}/sys/myCalendars/view/" + event.id;
            $.app.modalDialog('<fmt:message key="myCalendar.viewevent"/>', url, {
                width:370,
                height:250,
                noTitle : false,
                okBtn : false,
                closeBtn : false
            });
        }
        $("body").on("click", ".btn-delete-calendar", function() {
            var $this = $(this);
            $.app.confirm({
                title : '<fmt:message key="myCalendar.deleteevent"/>',
                message : '<fmt:message key="myCalendar.suretodelete"/>',
                ok : function() {
                    var url = "${ctx}/sys/myCalendars/delete?id=" + $this.data("id");
                    $.post(url, function() {
                        calendar.fullCalendar("refetchEvents");
                        $.app.closeModalDialog();
                    });
                }
            });

        });
    });

</script>
