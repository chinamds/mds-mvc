<script>
    $(function() {
        /*var validationEngine = $("form").validationEngine({
            promptPosition : "topRight",
            autoPositionUpdate:true,
            scroll:false
        });*/
    	$.extend(true, $.fn.datetimepicker.defaults, {
    	    icons: {
    	      time: 'far fa-clock',
    	      date: 'far fa-calendar',
    	      up: 'fas fa-arrow-up',
    	      down: 'fas fa-arrow-down',
    	      previous: 'fas fa-chevron-left',
    	      next: 'fas fa-chevron-right',
    	      today: 'fas fa-calendar-check',
    	      clear: 'far fa-trash-alt',
    	      close: 'far fa-times-circle'
    	    }
    	});

    	$('#backgroundColor').colorpicker({
    		          extensions: [
    		            {
    		              name: 'swatches',
    		              options: {
    		                colors: {
    		                  'tetrad1': '#000',
    		                  'tetrad2': '#000',
    		                  'tetrad3': '#000',
    		                  'tetrad4': '#000'
    		                },
    		                namesAsValues: false
    		              }
    		            }
    		          ]
    		        })
			    	.on('colorpickerChange colorpickerCreate', function (e) {
			            var colors = e.color.generate('tetrad');
			
			            colors.forEach(function (color, i) {
			              var colorStr = color.string(),
			                  swatch = e.colorpicker.picker
			                      .find('.colorpicker-swatch[data-name="tetrad' + (i + 1) + '"]');
			
			              swatch
			                  .attr('data-value', colorStr)
			                  .attr('title', colorStr)
			                  .find('> i')
			                  .css('background-color', colorStr);
			            });
			          });
    	$('#backgroundColor').on('colorpickerChange', function(event) {
            $('.jumbotron').css('background-color', event.color.toString());
        });
        /*$("#backgroundColor").change(function() {
            $(this).attr("style", $(this).find("option:selected").attr("style"));
        })*/
        
        $('.date:not(.custom)').each(function() {
             var $date = $(this);

             if($date.attr("initialized") == "true") {
                 return;
             }

             var dateformat = $(this).find("[data-format]").data("format");
             $date.datetimepicker({
             	format: dateformat,
             	buttons: {
                    showToday: true,
                    showClear: true,
                    showClose: true
                },
                icons: {
          	      time: 'far fa-clock',
          	      date: 'far fa-calendar',
          	      up: 'fas fa-arrow-up',
          	      down: 'fas fa-arrow-down',
          	      previous: 'fas fa-chevron-left',
          	      next: 'fas fa-chevron-right',
          	      today: 'fas fa-calendar-check',
          	      clear: 'far fa-trash-alt',
          	      close: 'far fa-times-circle'
          	    }
             });
             $date.find(":input").click(function() {$date.find(".icon-calendar,.icon-time,.icon-date").click();});
             $date.attr("initialized", true);
         });
        
        $(".all-day").change(function() {
            if($(this).is(":checked")) {
                $("[name=startTime],[name=endTime]").val("").attr("disabled", true);//.removeClass("validate[required]");
                //$("[name=startTime]").validationEngine("hide");
                //$("[name=endTime]").validationEngine("hide");
            } else {
                $("[name=startTime],[name=endTime]").removeAttr("disabled");//.addClass("validate[required]");
            }
        });
    })
    
</script>
