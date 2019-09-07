<script type="text/javascript">
    $(function () {
        var form = $("#myMessageForm");
       /* var editor = KindEditor.create("textarea[name='content.content']", {
            themeType: 'simple',
            uploadJson: '${ctx}/kindeditor/upload',
            fileManagerJson: '${ctx}/kindeditor/filemanager',
            allowFileManager: true,
            afterBlur: function(){this.sync();}
        });*/
        var options = {
        		theme: "bootstrap",
        		ajax: {
        		    url: '${ctx}/services/api/users/select2',
        		    dataType: 'json',
        		    data: function (params) {
        		      var query = {
        		        q: params.term
        		      }

        		      // Query parameters will be ?q=[term]
        		      return query;
        		    }
			        /*processResults: function (data) {
			            // Tranforms the top-level key of the response object from 'items' to 'results'
			            return {
			              results: data.items
			            };
			       }*/
        		},
        		placeholder: "<fmt:message key='myMessage.to.tip'/>",
        		allowClear: true
    		};

   		$("#recipients").select2(options);
        $('.summernote').summernote({
            height: 200
        }).on('summernote.change', function() {
        	$('#textcontent').val($('.summernote').val());
        });
        /*var text = $('#textcontent').val();
        $('.summernote').val($('#textcontent').val());
        var test2 = $('.summernote').val();*/
        /*$('#testcontent').wysihtml5({
        	  toolbar: {
        		    "font-styles": true, //Font styling, e.g. h1, h2, etc. Default true
        		    "emphasis": true, //Italics, bold, etc. Default true
        		    "lists": true, //(Un)ordered lists, e.g. Bullets, Numbers. Default true
        		    "html": false, //Button which allows you to edit the generated HTML. Default false
        		    "link": true, //Button to insert a link. Default true
        		    "image": true, //Button to insert an image. Default true,
        		    "color": true, //Button to change color of font  
        		    "blockquote": true, //Blockquote  
        		    "size": 'none' //default: none, other options are xs, sm, lg
        		  }
        		});*/

        /*var $username = $("#receiverId_msg");
        if($username[0]){
            $.app.initAutocomplete({
                input : $username,
                source : "${ctx}/sys/user/ajax/autocomplete",
                select : function(event, ui) {
                    $username.val(ui.item.label);
                    return false;
                }
            });
        }*/

        /*$(window).on('beforeunload',function() {
            if($username.val() || $("#title").val() || editor.html()) {
                return "确定离开当前编辑页面吗？";
            }
        });

        form.submit(function() {
            $(window).unbind("beforeunload");
        });*/
        
        /*$('form').on('submit', function (e) {
            //e.preventDefault();
            alert($('.summernote').summernote('code'));
            $('#content').val($('.summernote').val());
          });*/

        /*$(".btn-save-draft").click(function() {
            $(window).unbind("beforeunload");
            //form.validationEngine("detach");
            if(!validateMyMessage(form.get(0))) {
            	form.find(".form-group").addClass('error');
            	
                return false;
            }
            form.attr("action", "${ctx}/sys/meessageform/draft/save").submit();
        });*/
        
        $("input[type='text']:visible:enabled:first", document.forms['myMessageForm']).focus();
    });
</script>
