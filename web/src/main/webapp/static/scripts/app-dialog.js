;$.extend($.fn, {
    formOption : function(showOpt) {
        var opt = $.data(this[0], "formOpt");

        var settings;
        if (opt) {
            settings = $.extend({}, opt, showOpt);
        } else {
            settings = showOpt;
        }

        $.data(this[0], "formOpt", settings);
    },
    getFormOpt : function() {
        if (this[0] != undefined) {
            var opt = $.data(this[0], "formOpt");
            if (opt != undefined && opt != null)
                return opt;
        }
        return {};
    }
})

/*PNotify.prototype.options.styling = "bootstrap3";*/
PNotify.defaults.styling = 'bootstrap4'; // Bootstrap version 3
PNotify.defaults.icons = 'fontawesome5'; // glyphicons
$.mdsNotify =
{
    showSuccess : function (title, text, context)
    {
        var opt =
        {
            title : title,
            text : text,
            type : 'success',
            hide : true,
            delay : 3000
        };
        if (context != undefined && context != null)
        {
            $.mdsNotify.show_context (context, false, opt);
            return;
        }
        new PNotify.alert(opt);
    },
    showNotice : function (title, text, context)
    {
        var opt =
        {
            title : title,
            text : text,
            type : 'notice',
            hide : true,
            delay : 1500
        };
        if (context != undefined && context != null)
        {
            $.mdsNotify.show_context (context, false, opt);
            return;
        }
        new PNotify.alert(opt);
    },
    showError : function (title, text, context)
    {
        var opt =
        {
            title : title,
            text : text,
            type : 'error',
            hide : true,
            delay : 1500
        };
        if (context != undefined && context != null)
        {
            $.mdsNotify.show_context (context, false, opt);
            return;
        }
        new PNotify.alert(opt);
    },
    showLogin : function (context)
    {
        var opt =
        {
            title : "信息",
            text : "您可能已经离线，请重新刷新网页后再试！",
            type : "info",
            confirm :
            {
                confirm : true,
                buttons : [
                    {
                        text : '刷新网页',
                        click : function (notice)
                        {
                            top.location.reload ();
                        }
                    }
                ]
            },
            buttons :
            {
                closer : false,
                sticker : false
            }
        };
        if (context != undefined && context != null)
        {
            $.mdsNotify.show_context (context, false, opt);
            return;
        }
        new PNotify.alert(opt);
    },
    showConfirm : function (title, text, buttons, context)
    {
        var opt =
        {
            title : title,
            text : text,
            type : "notice",
            confirm :
            {
                confirm : true,
                buttons : buttons
            },
            buttons :
            {
                closer : false,
                sticker : false
            }
        };
        
        if (context != undefined && context != null)
        {
            $.mdsNotify.show_context (context, false, opt);
            return;
        }
        
        new PNotify.alert(opt);
        
    },
    show_context : function (context, modal, opt)
    {
        var opts = $.extend (
        {
            "push" : "top",
            "dir1" : "down",
            "dir2" : "left",
            "context" : context,
            "modal" : modal,
            "overlay_close" : true,
            "addclass" : "stack-modal"
        }, opt)
        new PNotify.alert(opts);
    }
}

//#region mdsShowMsg utility function
$.mdsShowResult = function (result, title, options) {
	if (result){
		var defaults = {
			 title : title ? title : result.title,
		     text : result.message,
		     type : result.status==200 ? 'success' : 'error',
		     hide : true,
			 delay: 4000, 
		};
		var settings = $.extend({}, defaults, options);
		new PNotify.alert(settings);
	}else{
		var opt = {
	            title : "system error",
	            text : "Please contact your administrator!",
	            type : "error"
        };
        new PNotify.alert(opt);
	}
};

$.mdsShowMsg = function (title, message, options) {
     
	var defaults = {
		 title : title,
	     text : text,
	     hide : true,
		 delay: 4000, // The # of milliseconds to wait until a message auto-closes. Use 0 to never auto-close.
	};

	var settings = $.extend({}, defaults, options);

	new PNotify.alert(settings);
};

//#endregion

$.mdsHttp =
{
    getUrlParam : function (name)
    {
        var reg = new RegExp ("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr (1).match (reg);
        if (r != null)
            return unescape (r[2]);
        return null;
    },
    /**
     * 
     * @param opt
     *            opt.url,postdata, success, failure, error
     */
    obj : function (opt)
    {
        var formData;

        if (opt.postType) {
            switch (opt.postType) {
            case "multipart":
                formData = new FormData($(opt.formId)[0]);
                break;
            case "form":
                formData =  $.param(opt.ajaxData);
                break;
            case "json":
                formData = JSON.stringify(opt.ajaxData);
                break;
            default:
                return;
            }
        } else {
            formData = opt.ajaxData;
        }
        
        var sopt =
        {
            type : opt.type == undefined ? "post" : opt.type,
            async : false,
            url : opt.url,
            data : formData,
            dataType : "json",
            success : function (json)
            {
                if (json.stat == 1)
                {
                    if (opt.success != undefined)
                        opt.success (json.data);
                }
                else
                {
                    if (opt.failure != undefined)
                        opt.failure (json);
                    else if (json.code != undefined)
                    {
                        $.mdsHttp.show_code_err (json);
                    }
                    else
                    {
                        console.log ("success not equal true   [url: " + opt.url + "]");
                        if (json.errorMessages != null && json.errorMessages.length > 0)
                        {
                            $.mdsNotify.showNotice ("error", json.errorMessages[0]);
                        }
                    }
                    
                }
                
            },
            error : function (XMLHttpRequest, textStatus, errorThrown)
            {
                var info = "XMLHttpRequest:" + JSON.stringify (XMLHttpRequest) + " ;textStatus:" + textStatus
                        + "; errorThrown:" + JSON.stringify (errorThrown) + ";   【" + opt.url + "】";
                console.log (info);
                if (opt.error != undefined)
                    opt.error (XMLHttpRequest, textStatus, errorThrown);
                else
                {
                    $.mdsNotify.showError ("request error", "Error occurred with system request, please contact your administrator.");
                }
            }
        };
        
        if (opt.postType) {
            switch (opt.postType) {
            case "multipart":
                sopt.async = false;
                sopt.cache = false;
                sopt.contentType = false;
                sopt.processData = false;
                break;
            case "form":
                sopt.contentType = 'application/x-www-form-urlencoded';
                break;
            case "json":
                sopt.contentType = 'application/json';
                sopt.dataType = "json";
                break;
            default:
                return;
            }
        }
        $.ajax (sopt);
    },
    list : function (opt)
    {
        var formData;

        if (opt.postType) {
            switch (opt.postType) {
            case "multipart":
                formData = new FormData($(opt.formId)[0]);
                break;
            case "form":
                formData =  $.param(opt.ajaxData);
                break;
            case "json":
                formData = JSON.stringify(opt.ajaxData);
                break;
            default:
                return;
            }
        } else {
            formData = opt.ajaxData;
        }
        
        var sopt =
        {
            type : opt.type == undefined ? "get" : opt.type,
            async : false,
            url : opt.url,
            data : opt.ajaxData,
            dataType : "json",
            success : function (json)
            {
                if (json.stat == 1)
                {
                    if (opt.success != undefined)
                        opt.success (json.list, json.pageInfo);
                }
                else
                {
                    if (opt.failure != undefined)
                        opt.failure (json);
                    else if (json.code != undefined)
                    {
                        $.mdsHttp.show_code_err (json);
                    }
                    else
                    {
                        console.log ("success不等于true   【url: " + opt.url + "】");
                        if (json.errorMessages != null && json.errorMessages.length > 0)
                        {
                            $.mdsNotify.showNotice ("错误", json.errorMessages[0]);
                        }
                    }
                }
                
            },
            error : function (XMLHttpRequest, textStatus, errorThrown)
            {
                var info = "XMLHttpRequest:" + JSON.stringify (XMLHttpRequest) + " ;textStatus:" + textStatus
                        + "; errorThrown:" + JSON.stringify (errorThrown) + ";   【" + opt.url + "】";
                console.log (info);
                if (opt.error != undefined)
                    opt.error (XMLHttpRequest, textStatus, errorThrown);
                else
                {
                    $.mdsNotify.showError ("请求错误", "系统发生请求错误，请联系管理员解决。");
                }
            }
        };
        
        if (opt.postType) {
            switch (opt.postType) {
            case "multipart":
                sopt.async = false;
                sopt.cache = false;
                sopt.contentType = false;
                sopt.processData = false;
                break;
            case "form":
                sopt.contentType = 'application/x-www-form-urlencoded';
                break;
            case "json":
                sopt.contentType = 'application/json';
                sopt.dataType = "json";
                break;
            default:
                return;
            }
        }
        
        $.ajax (sopt);
    },
    show_code_err : function (json)
    {
        if (json.code == 1403)
        {
            $.mdsNotify.showLogin ();
            return;
        }
        else if (json.errorMessages != undefined && json.errorMessages != null && json.errorMessages.length > 0)
        {
            $.mdsNotify.showNotice ("Error", json.errorMessages[0]);
        }
        
    }
}

$.mdsDialog = {
    showConfirm : function(showOpt) {
        var opt = {
            title : showOpt.title,
            message : showOpt.message,
            draggable : true,
            closeByBackdrop : false,
            type : BootstrapDialog.TYPE_WARNING,
            buttons : [
                    {
                        id : "btn-confirm",
                        icon : 'fa fa-saved',
                        label : '确定',
                        cssClass : 'btn-warning',
                        action : function(dialogItself) {
                            if (showOpt.url != undefined) {
                                $.mdsHttp.obj({
                                            type : showOpt.ajaxType == undefined ? 'post' : showOpt.ajaxType,
                                            url : showOpt.url,
                                            ajaxData : showOpt.ajaxData,
                                            success : function(json) {
                                                if (showOpt.autoClose) {
                                                    dialogItself.close();
                                                    $.mdsNotify.showSuccess(
                                                                    showOpt.successTitle,
                                                                    showOpt.successMessage);
                                                } else {
                                                    dialogItself.setTitle(showOpt.successTitle);
                                                    dialogItself.setMessage(showOpt.successMessage);
                                                    dialogItself.setType(BootstrapDialog.TYPE_SUCCESS);
                                                    dialogItself.setButtons([ {
                                                                icon : 'fa fa-saved',
                                                                label : '确定',
                                                                cssClass : 'btn-success',
                                                                action : function(d) {
                                                                    d.close();
                                                                }
                                                            } ]);
                                                }

                                                if (showOpt.onSuccess)
                                                    showOpt.onSuccess(json);
                                            }
                                        })
                            } else {
                                if (showOpt.onConfirm(dialogItself)) {
                                    dialogItself.close();
                                }
                            }

                        }
                    }, {
                        icon : 'fa fa-ban-circle',
                        label : '取消',
                        action : function(dialogItself) {
                            dialogItself.close();
                        }
                    } ]
        };

        var dialog = BootstrapDialog.show(opt);
        return dialog;
    },
    /**
     * title,isReadOnly,postUrl,data,dataSource,fields,properties,dependencies ,
     * formNodeCallback 收集表单信息回调
     */
    showFormDialog : function(showOpt) {
        var dialog;
        var buttons;
        var showD = function(showOpt, data) {
        	if (showOpt.isImportForm != undefined && showOpt.isImportForm != null && showOpt.isImportForm==true){
        		buttons = [{
                    icon : 'fa fa-import',
                    label : 'Import',
                    cssClass : 'btn-primary',
                    action : function(dialogItself) {
                        $.mdsDialog.postForm(showOpt, dialogItself);
                    }
                },{
                    icon : 'fa fa-ban-circle',
                    label : 'Cancel',
                    action : function(dialogItself) {
                        dialogItself.close();
                    }
                }];
        	}else{
	            if (showOpt.isReadOnly != undefined && showOpt.isReadOnly != null) {
	                if (showOpt.isReadOnly) {
	                    // readonly
	                } else {
	                    // editable
	                    buttons = [{
	                        icon : 'fa fa-saved',
	                        label : '修改',
	                        cssClass : 'btn-primary',
	                        action : function(dialogItself) {
	                            $.mdsDialog.postForm(showOpt, dialogItself);
	                        }
	                    }, {
	                        icon : 'fa fa-ban-circle',
	                        label : '取消',
	                        action : function(dialogItself) {
	                            dialogItself.close();
	                        }
	                    }];
	                }
	            } else {
	                // add mode
	                buttons = [{
	                    icon : 'fa fa-saved',
	                    label : '保存',
	                    cssClass : 'btn-primary',
	                    action : function(dialogItself) {
	                        $.mdsDialog.postForm(showOpt, dialogItself);
	                    }
	                }, {
	                    icon : 'fa fa-ban-circle',
	                    label : '取消',
	                    action : function(dialogItself) {
	                        dialogItself.close();
	                    }
	                }];
	            }
        	}

            dialog = $.mdsDialog.showDialog(showOpt, buttons, data);
        }

        // load data at first if has data source
        if (showOpt.dataSource != undefined && showOpt.dataSource != null) {
            $.ajax({
                type : "get",
                async : false,
                url : showOpt.dataSource,
                contentType : 'application/json',
                dataType : "json",
                success : function(json) {
                    if (json.stat == 1) {
                        showD(showOpt, json.data);
                    } else {
                        if (json.code != undefined) {
                            $.mdsDialog.show_code_err(dialog, json);
                        } else {
                            $.mdsDialog.show_sys_err(dialog);
                            return;
                        }
                    }
                },
                error : function(XMLHttpRequest, textStatus, errorThrown) {
                    var info = "XMLHttpRequest:"
                            + JSON.stringify(XMLHttpRequest) + " ;textStatus:"
                            + textStatus + "; errorThrown:"
                            + JSON.stringify(errorThrown) + "; ["
                            + showOpt.postUrl + "]";
                    console.log("System error[url: " + showOpt.postUrl + "]" + info);
                    $.mdsDialog.show_sys_err(dialog, XMLHttpRequest);
                }
            });
        } else {
            showD(showOpt);
        }
    },

    showDialog : function(showOpt, buttons, data) {
    	if (buttons != undefined && buttons != null){
    		if (showOpt.buttons != undefined && showOpt.buttons != null){
    			showOpt.buttons = $.extend(showOpt.buttons, buttons);
    		}else{
    			showOpt.buttons = buttons;
    		}
    	}
    	
        showOpt.data = $.extend({}, showOpt.data, data);

        if (showOpt.buttons == undefined || showOpt.buttons == null) {
            showOpt.buttons = [{
                icon : 'fa fa-ok',
                label : 'OK',
                cssClass : 'btn-primary',
                action : function(dialogItself) {
                    dialogItself.close();
                }
            }];
        }

        var cont = $('<div>加载数据中，请稍后...</div>');
        var opt = $.extend({
                title : showOpt.title,
                message : function(dialog) {
                    return cont;
                },
                onshown : function(dialogRef) {
                    var contTmp = $('<div></div>');
                    var response = '';
                    if (showOpt.templateUrl) {
                        $.ajax({
                            url : showOpt.templateUrl,
                            async : false,
                            success : function(res) {
                                try {
                                    var t = eval("(" + res + ")");
                                    if (t != null && t.stat == 0) {
                                        $.mdsDialog.show_code_err(null, t);
                                        if (t.code == 1403) {
                                            cont.html("<div>您可能已经离线，请重新刷新网页后再试！</div>");
                                        }
                                        
                                        dialog.enableButtons(false);
                                        
                                        return;

                                    } else {
                                        response = res;
                                    }
                                } catch (e) {
                                    response = res;
                                }
                            }
                        });
                    } else if (showOpt.template) {
                        response = "<div>" + showOpt.template + "</div>";
                    } else {
                        response = "<div></div>";
                    }

                    if (showOpt.data != undefined && showOpt.data != null && showOpt.data.length > 0) {
                        // 只要有数据，就用 artTemplate
                        // 渲染
                        if (showOpt.templateOption != undefined) {
                            if (showOpt.templateOption.helpers) {
                                for ( var i in showOpt.templateOption.helpers) {
                                    var help = showOpt.templateOption.helpers[i];
                                    if (help) {
                                        template.helper(help.name, help.action);
                                    }
                                }
                            }
                        }

                        var tempRes = template.compile(response);
                        var dt = tempRes(showOpt.data);
                        cont.html(dt);

                        if (showOpt.isReadOnly != undefined
                                && showOpt.isReadOnly != null
                                && !showOpt.isReadOnly) {
                            // 编辑模式可以再用js2form填充form数据
                            var rootNode = cont.find(showOpt.formId)[0];
                            if (rootNode && rootNode != null)
                                js2form(rootNode, showOpt.data);
                            else
                                js2form(cont[0], showOpt.data);
                        }
                    } else {
                        cont.html(response);
                    }

                    // form 設置
                    var formOpt = $(showOpt.formId).getFormOpt();
                    if (showOpt.isReadOnly != undefined && showOpt.isReadOnly != null) {
                        if (showOpt.isReadOnly) {
                            if (formOpt.onReadonlyMode != undefined)
                                formOpt.onReadonlyMode(showOpt.data);
                        } else {
                            if (formOpt.onModifyMode != undefined)
                                formOpt.onModifyMode(showOpt.data);
                        }
                    } else {
                        if (formOpt.onCreateMode != undefined)
                            formOpt.onCreateMode(showOpt.data);
                    }

                    if (formOpt.buttons != undefined) {
                        dialogRef.setButtons(formOpt.buttons);
                    }
                },
                draggable : true,
                closeByBackdrop : false,
                closeByKeyboard : true,
                buttons : showOpt.buttons
            }, showOpt.dialogOption);

        var dialog = BootstrapDialog.show(opt);
        return dialog;
    },
    
    postForm : function(showOpt, dialog) {
        dialog.setClosable(false);
        dialog.enableButtons(false);

        var formOpt = $(showOpt.formId).getFormOpt();

        //var postform = $(showOpt.formId);
        var postform = dialog.getModalBody().find(showOpt.formId);
        if (!$.mdsDialog.isNullOrEmpty(formOpt.preValidDataHandler)){
	        if(!formOpt.preValidDataHandler(postform.get(0))) {
	        	form.find(".form-group").addClass('error');
	        	dialog.setClosable(true);
                dialog.enableButtons(true);
	        	
	            return;
	        }
        }
        /*var validator = postform.validate();

        if (formOpt.preValidDataHandler != undefined) {
            if (formOpt.preValidDataHandler(dialog, validator) != true) {
                dialog.setClosable(true);
                dialog.enableButtons(true);
                return;
            }
        }

        // validator.form();
        if (!postform.valid()) {
            postform.focus();
            $.mdsDialog.show_stack_err_context(dialog.getModalContent(), false, {
                title : "提示",
                text : "该页面还有" + validator.numberOfInvalids() + "个字段包含错误！",
                type : "notice",
                delay : 1500,
                hide : true
            });
            dialog.setClosable(true);
            dialog.enableButtons(true);
            return;
        }*/

        if (formOpt.preSloveDataHandler != undefined) {
            if (formOpt.preSloveDataHandler(dialog) != true) {
                dialog.setClosable(true);
                dialog.enableButtons(true);
                return;
            }
        }

        var formData;
        if (showOpt.postType) {
            switch (showOpt.postType) {
            case "multipart":
                formData = new FormData(postform[0]);
                break;
            case "form":
                formData = $.param(form2js(postform[0], '.', true,
                        formOpt.formNodeCallback));
                break;
            case "json":
                formData = JSON.stringify(form2js(postform[0], '.', true,
                        formOpt.formNodeCallback));
                break;
            default:
                return;
            }
        } else {
            formData = form2js(postform[0], '.', true, formOpt.formNodeCallback);
        }

        if (formOpt.prePostDataHandler != undefined) {
            if (formOpt.prePostDataHandler(dialog, formData) != true) {
                dialog.setClosable(true);
                dialog.enableButtons(true);
                return;
            }
        }

        if (showOpt.isDebug != undefined && showOpt.isDebug) {
            var postdata = JSON.stringify(formData);
            alert(postdata);
            dialog.setClosable(true);
            dialog.enableButtons(true);
            return;
        }

        $.mdsDialog.waiting(showOpt.waitingMsg);
        var sendOpt = {
            type : "post",
            async : false,
            url : showOpt.postUrl,
            data : formData,
            dataType : "json",
            success : function(json) {
            	$.mdsDialog.waitingOver();
                if (json && json.status == 200) {
                    dialog.close();
                    if (showOpt.onPostSuccess != undefined){
                        showOpt.onPostSuccess(json)
                    }else{
                    	new PNotify({
                            title : showOpt.title,
                            text : showOpt.text,
                            type : 'success',
                            animation : "fade",
                            shadow : true,
                            hide : true,
                            delay : 2000,
                            mobile : {
                                swipe_dismiss : true,
                                styling : true
                            }
                        });
                    }
                } else {
                    if (json && !$.mdsDialog.isNullOrEmpty(json.status)) {
                        $.mdsDialog.show_code_err(dialog, json);
                    } else {
                        $.mdsDialog.show_sys_err(dialog);
                    }
                }

                dialog.setClosable(true);
                dialog.enableButtons(true);
            },
            error : function(XMLHttpRequest, textStatus, errorThrown) {
            	$.mdsDialog.waitingOver();
            	
                var info = "XMLHttpRequest:" + JSON.stringify(XMLHttpRequest)
                        + " ;textStatus:" + textStatus + "; errorThrown:"
                        + JSON.stringify(errorThrown) + "; 【" + showOpt.postUrl
                        + "】";
                console.log("系统错误 【url: " + showOpt.postUrl + "】" + info);
                $.mdsDialog.show_sys_err(dialog, XMLHttpRequest);
                dialog.setClosable(true);
                dialog.enableButtons(true);
            }
        };

        if (showOpt.postType) {
            switch (showOpt.postType) {
            case "multipart":
                sendOpt.async = false;
                sendOpt.cache = false;
                sendOpt.contentType = false;
                sendOpt.processData = false;
                break;
            case "form":
                sendOpt.contentType = 'application/x-www-form-urlencoded';
                break;
            case "json":
                sendOpt.contentType = 'application/json';
                break;
            default:
                return;
            }
        }

        $.ajax(sendOpt);

    },
    
    _bootstrapDialog:null,

    waiting : function(message, sizeSmall) {
        if(!message) {
            message = "Loading...";
        }
        
        $.LoadingOverlay("show", {
            image       : "",
            text        : message,
            fontawesome : "fa fa-cog fa-spin"
        });

        /*message = '<img src="' + window.Mds.AppRoot + '/static/images/loading.gif" '+ (sizeSmall ? "width='20px'" : "") +'/> ' + message;
        if(!sizeSmall) {
            message = "<h4>"+message+"</h4>";
        }
   	
        $.mdsDialog._bootstrapDialog = new BootstrapDialog({
        	size: sizeSmall ? BootstrapDialog.SIZE_SMALL : BootstrapDialog.SIZE_NORMAL,
        	title: null,
            message: message,
            closable: false,
            closeByBackdrop: false,
            closeByKeyboard: false,
            cssClass: 'bootstrap-dialog-loading',
            onshow: function(dialogItself) {
                var $modal_dialog = dialogItself.getModalDialog();//.find('.modal-dialog');

                dialogItself.getModal().css('display', 'block');
                $modal_dialog.css({'margin-top': Math.max(0, ($(window).height() - $modal_dialog.height()) / 2) });
            },
        });
        $.mdsDialog._bootstrapDialog.realize();
        $.mdsDialog._bootstrapDialog.getModalHeader().hide();
        $.mdsDialog._bootstrapDialog.getModalBody().css('textAlign', 'center');
        $.mdsDialog._bootstrapDialog.open();*/
    },
    
    waitingOver: function () {
    	$.LoadingOverlay("hide");
    	/*console.log('try to close _bootstrapDialog');
    	if ($.mdsDialog._bootstrapDialog){
    		console.log('close _bootstrapDialog');
    		setTimeout(function() {
    			$.mdsDialog._bootstrapDialog.setClosable(true);
        		$.mdsDialog._bootstrapDialog.setCloseByBackdrop(true);
    			$.mdsDialog._bootstrapDialog.close();
            }, 200);
    	}*/
    }
    ,
    /**
     * 当前显示的模态窗口队列
     */
    _modalDialogQueue:null,
    /**
     * 模态窗口
     * @title 标题
     * @param url
     * @param settings
     */
    modalDialog : function(title, url, settings) {

        $.mdsDialog.waiting();
        var defaultSettings = {
            title : title,
            message : url,
            closeText : "Close",
            closeOnEscape:false,
            modal:true,
            noTitle : false,
            _close : function(modal) {
            	modal.close();
                if($.mdsDialog._modalDialogQueue && $.mdsDialog._modalDialogQueue.length > 0) {
                    $.mdsDialog._modalDialogQueue.pop();
                }
            },
            /*onshow: function(dialogItself) {
                var $modal_dialog = dialogItself.getModalDialog();//.find('.modal-dialog');

                dialogItself.getModal().css('display', 'block');
                if (settings.height){
                	$modal_dialog.css('height',  settings.height + 'px');
                }
                if (settings.width){
                	$modal_dialog.css('width',  settings.width + 'px');
                }
            },*/
            buttons:[ {
                icon : 'fa fa-save',
                label : 'OK',
                cssClass : 'btn-primary',
                action : function(dialogItself) {
                	if(settings.ok) {
                        if(settings.ok(dialogItself)) {
                            settings._close(dialogItself);
                        }
                    } else {
                        settings._close(dialogItself);
                    }
                    if(settings.callback) {
                        settings.callback();
                    }
                }
            	}, {
                icon : 'fa fa-ban',
                label : 'Cancel',
                action : function(dialogItself) {
                	settings._close(dialogItself);
                }
            } ]
        };
        
        if(!settings) {
            settings = {};
        }
        
        settings = $.extend(true, {}, defaultSettings, settings);
        if(!settings.ok) {
            //delete settings.buttons[0]; //'OK'
            $.array.remove(settings.buttons, settings.buttons[0]);
        }
        
        /*var css='';
        if (settings.height){
        	css += ('height:' + settings.height + 'px;');
        }
        if (settings.width){
        	css += ('width:' + settings.width + 'px;');
        }*/
        
        $.mdsDialog.waitingOver();
        
        var dialog = new BootstrapDialog(settings);
        if(!$.mdsDialog._modalDialogQueue) {
            $.mdsDialog._modalDialogQueue = new Array();
        }
        $.mdsDialog._modalDialogQueue.push(dialog);
        dialog.realize();
        
        //dialog.setCssClass(css);
        
        //$.table.initTable(div.find(".table"));
        dialog.open();

        /*if (url.indexOf('iframe:') == 0){
        	url = url.substring(url.indexOf(":") + 1, url.length);
        	if (url.indexOf('#') == -1) {
                url = url + (url.indexOf('?') == -1 ? '?___t=' : '&___t=') + Math.random();
            } else {
                var arr = url.split('#');
                url = arr[0] + (arr[0].indexOf('?') == -1 ? '?___t=' : '&___t=') + Math.random() + '#' + arr[1];
            }
        	var boxHtml = '<iframe name="jbox-iframe" id="jbox-iframe" width="100%" height="100%" marginheight="0" marginwidth="0" frameborder="0" src="' + url + '"></iframe>';
        	$.mdsDialog.waitingOver();
            var div = $('<div style="width:520px; height:480px;"></div>').append(boxHtml);
            var opt = $.extend(
                    {
                       message : div,
                    }, settings);
            var dialog = new BootstrapDialog(opt);
            if(!$.mdsDialog._modalDialogQueue) {
                $.mdsDialog._modalDialogQueue = new Array();
            }
            $.mdsDialog._modalDialogQueue.push(dialog);
            $.table.initTable(div.find(".table"));
            dialog.open();
        } else{
	        $.ajax({
	            url: url,
	            headers: { table:true }
	        }).done(function (data) {
               	$.mdsDialog.waitingOver();

                var div = $("<div></div>").append(data);
                var opt = $.extend(
                        {
                            message : div,
                        }, settings);
                var dialog = new BootstrapDialog(opt);
                if(!$.mdsDialog._modalDialogQueue) {
                    $.mdsDialog._modalDialogQueue = new Array();
                }
                $.mdsDialog._modalDialogQueue.push(dialog);
                $.table.initTable(div.find(".table"));
                dialog.open();
            });
        }*/
    }
    ,
    /**
     * cancel model dialog
     */
    cancelModelDialog : function() {
        if($.mdsDialog._modalDialogQueue && $.mdsDialog._modalDialogQueue.length > 0) {
            //$.mdsDialog._modalDialogQueue.pop().dialog("close");
        	$.mdsDialog._modalDialogQueue.pop().close();
        }
    }
    ,
    alert : function(content, title, options) {
        if(!options) {
            options = {};
        }
        if ($.mdsDialog.isNullOrEmpty(title)){
        	title = "Warn";
        }
        if ($.mdsDialog.isNullOrEmpty(content)){
        	content = "Illegal operation.";
        }
        	
        var defaults = {
            title : title,
            message : content,
            draggable: true, // <-- Default value is false
            btnOKLabel : "Close",
        };
        options = $.extend({}, defaults, options);
        BootstrapDialog.alert(options);
    }
    ,
    /**
     * confirm dialog
     * @param options
     */
    confirm : function(content, title, options) {
        var defaults = {
            title : title,
            message : content,
            closable: true, // <-- Default value is false
            draggable: true, // <-- Default value is false
            btnCancelLabel : 'Cancel',
            btnOKLabel : 'OK',
            cancel : $.noop,
            ok : $.noop,
            btnOKClass: 'btn-warning', // <-- If you didn't specify it, dialog type will be used,
            callback: function(result) {
                // result will be true if button was click, while it will be false if users close the dialog directly.
                if(result) {
                	options.ok();
                }else {
                	options.cancel();
                }
            }
        };
        
        if(!options) {
            options = {};
        }
        options = $.extend({}, defaults, options);
        
        BootstrapDialog.confirm(options);
    },
    isNullOrEmpty : function (obj) {
		if ((!obj && obj !== false) || !(obj.length > 0)) {
			return true;
		}
		return false;
	},
    show_sys_err : function(dialog, XMLHttpRequest) {
        var opt = {
            title : "System error",
            text : "Please contact the administrator.",
            type : "error"
        };
        if (dialog == undefined || dialog == null) {
            new PNotify(opt);
        } else {
            $.mdsDialog.show_stack_err_context(dialog.getModalContent(), false, opt);
        }

    },
    show_code_err : function(dialog, json) {
        var opt;
        if (json.code != undefined && json.code == 1403) {
            $.mdsNotify.showLogin();
            return;
        } else if (json.message != undefined
                && json.message != null && json.message.length > 0) {
            opt = {
                title : "Error",
                text : json.message,
                type : "notice",
                delay : 1500,
                hide : true
            };
        }

        if ($.mdsDialog.isNullOrEmpty(dialog)) {
            new PNotify(opt);
        } else {
            $.mdsDialog.show_stack_err_context(dialog.getModalContent(), false, opt)
        }

    },
    show_stack_err_context : function(context, modal, opt) {
        var opts = !opt ? {} : opt;
        opts.stack = modal ? {
            "push" : "top",
            "dir1" : "down",
            "dir2" : "left",
            "context" : context,
            "modal" : true,
            "overlay_close" : true
        } : {
            "push" : "top",
            "dir1" : "down",
            "dir2" : "left",
            "context" : context
        };
        opts.addclass = "stack-modal";
        new PNotify(opts);
    }
}