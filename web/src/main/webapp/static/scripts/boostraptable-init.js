$.mdsTable = {
	/**
	 * Initialize boostrapTable 
	 * @param table
	 */
	initTable: function (tableId, url, searchformData, options, columns) {
		var defaults = {
				 url: url,   
				 queryParams: function(params) {
					if (searchformData != undefined && searchformData != null){
						return $.extend({}, params, searchformData);    
					}else{
						return params;
					}
				},
				//toolbar: "#toolbar",  
				sidePagination: "server",  
				pageNumber: 1,    
				pageSize: 10,   
				pageList: [
					10, 20, 50, 100, 200 
				],
				pagination: true,  
				showRefresh: false, 
				showColumns: false, 
				searchOnEnterKey: true,  
				search:false    
			};
		if (columns != undefined && columns != null){
			defaults.columns = columns;
		}
		var settings = defaults;
		if (options != undefined && options != null){
			settings = $.extend({}, defaults, options);
		}
		
		$("#" + tableId).bootstrapTable(settings);
	},
}

$.mdsTreeTable = {
	/**
	 * Initialize boostrapTable tree grid 
	 * @param table
	 */
	initTable: function (tableId, url, searchformData, options, columns) {
		var defaults = {
				 url: url,   
				 queryParams: function(params) {
					if (searchformData != undefined && searchformData != null){
						return $.extend({}, params, searchformData);    
					}else{
						return params;
					}
				},
				//toolbar: "#toolbar",  
				sidePagination: "server",  
				pageNumber: 1,    
				pageSize: 10,   
				pageList: [
					10, 20, 50, 100, 200 
				],
				pagination: true,  
				showRefresh: false, 
				showColumns: false, 
				searchOnEnterKey: true,  
				search:false,
				idField: 'id',
				treeShowField: 'code',
				parentIdField: 'pid',
				onLoadSuccess: function(data) {
					console.log('load');
					// jquery.treegrid.js
					$("#" + tableId).treegrid({
					// initialState: 'collapsed',
					treeColumn: 2,
					// expanderExpandedClass: 'fa fa-minus',
					// expanderCollapsedClass: 'fa fa-plus',
					onChange: function() {
					  $("#" + tableId).bootstrapTable('resetWidth');
					}
				});
			  }
			};
		if (columns != undefined && columns != null){
			defaults.columns = columns;
		}
		
		var settings = defaults;
		if (options != undefined && options != null){
			settings = $.extend({}, defaults, options);
		}
		
		$("#" + tableId).bootstrapTable(settings);
	},
}

$.table = {

	/**
	 * 初始化表格：全选/反选 排序
	 * @param table
	 */
	initTable: function (table) {
		if(!table || !table.length || table.attr("initialized") == "true") {
			return;
		}

		table.attr("initialized", "true");

		$.table.initSort(table);
		$.table.initSearchForm(table);
		if(table.is(".move-table")) {
			$.movable.initMoveableTable(table);
		}

		//初始化table里的a标签
		$.table.initTableBtn(table);
		//初始化删除和修改按钮
		$.table.initDeleteSelected(table);
		$.table.initUpdateSelected(table);
		$.table.initCreate(table);

		//初始化checkbox
		$.table.initCheckbox(table);
		//初始化 按钮的状态
		$.table.changeBtnState(table);


	},
	initCheckbox: function(table) {
		var activeClass = "active";
		//初始化表格中checkbox 点击单元格选中
		table.find("td.check").each(function () {
			var checkbox = $(this).find(":checkbox,:radio");
			checkbox.off("click").on("click", function (event) {
				var checked = checkbox.is(":checked");
				if(!checked) {
					checkbox.closest("tr").removeClass(activeClass);
				} else {
					checkbox.closest("tr").addClass(activeClass);
				}
				$.table.changeBtnState(table);
				event.stopPropagation();
			});
			$(this).closest("tr").off("click").on("click", function (event) {
				var checked = checkbox.is(":checked");
				if(checked) {
					checkbox.closest("tr").removeClass(activeClass);
				} else {
					checkbox.closest("tr").addClass(activeClass);
				}
				checkbox.prop("checked", !checked);
				$.table.changeBtnState(table);
			});
		});
		//Initializing select all/invert selection button
		table.find(".check-all").off("click").on("click", function () {
			var checkAll = $(this);
			if(checkAll.text() == window.Mds.getString("button_selectall")) {
				checkAll.text(window.Mds.getString("button_cancel"));
				table.find("td.check :checkbox").prop("checked", true).closest("tr").addClass(activeClass);
			} else {
				checkAll.text(window.Mds.getString("button_selectall"));
				table.find("td.check :checkbox").prop("checked", false).closest("tr").removeClass(activeClass);
			}
			$.table.changeBtnState(table);
		});
		table.find(".reverse-all").off("click").on("click", function () {
			table.find("td.check :checkbox").each(function () {
				var checkbox = $(this);
				var checked = checkbox.is(":checked");
				if(checked) {
					checkbox.closest("tr").removeClass(activeClass);
				} else {
					checkbox.closest("tr").addClass(activeClass);
				}
				checkbox.prop("checked", !checked);
				$.table.changeBtnState(table);
			});
		});
	},
	changeBtnState : function(table) {
		var hasChecked = table.find("td.check :checkbox:checked").length;
		var btns = table.closest(".panel").find(".tool .btn").not(".no-disabled");
		if(hasChecked) {
			btns.removeClass("disabled");
			btns.each(function() {
				var btn = $(this);
				var href = btn.data("btn-state-href");
				if(href) {
					btn.attr("href", href);
				}
			});
		} else {
			btns.addClass("disabled");
			btns.each(function() {
				var btn = $(this);
				var href = btn.attr("href");
				if(href) {
					btn.data("btn-state-href", href);
					btn.removeAttr("href");
				}
			});
		}
	},
	/**
	 * 初始化对应的查询表单
	 * @param table
	 */
	initSearchForm : function(table) {
		var id = $(table).attr("id");
		var searchForm = table.closest("[data-table='" + id + "']").find(".search-form");

		if(!searchForm.length) {
			return;
		}

		searchForm.find(".btn").addClass("no-disabled");

		searchForm.find(".btn-clear-search").click(function() {

			if (table.data("async") == true) {
				var resetBtn = searchForm.find("input[type='reset']");
				if(!resetBtn.length) {
					searchForm.append("<input type='reset' style='display:none'>");
					resetBtn = searchForm.find("input[type='reset']");
				}
				resetBtn.click();
			}
			turnSearch(table, searchForm, true);
		});

		var turnSearch = function(table, searchForm, isSearchAll) {
			var url = $.table.tableURL(table);
			url = $.table.removeSearchParam(url, searchForm);
			url = $.table.removePageParam(url, searchForm);
			if(!isSearchAll) {
				if(url.indexOf("?") == -1) {
					url = url + "?";
				} else {
					url = url + "&";
				}
				url = url + searchForm.serialize();
			}
			$.table.reloadTable(table, url, null);
		}

		searchForm.off("submit").on("submit", function() {
			turnSearch(table, searchForm, false);
			return false;
		});

		if(searchForm.is("[data-change-search=true]")) {
			searchForm.find(":input:not(:button,:submit,:reset)").off("change").on("change", function(e) {
				// avoid double search issue, when you click search button after change any input
				searchForm.off("submit").on("submit", function() {
					return false;
				});
				turnSearch(table, searchForm, false);
			});
		}

		searchForm.find(".btn-search-all").off("click").on("click", function() {
			turnSearch(table, searchForm, true);
			return false;
		});


	},
	/**
	 * 初始化sort
	 * @param table
	 */
	initSort: function (table) {
		if (!table.length) {
			return;
		}

		//初始化排序
		var prefix = $.table.getPrefix(table);

		var sortURL = $.table.tableURL(table);

		var sortBtnTemplate = '<div class="sort"><a class="{sort-icon}" href="#" title="排序"></a></div>';
		table.find("[sort]").each(function () {
			var th = $(this);
			var sortPropertyName = prefix + "sort." + th.attr("sort");
			var sortBtnStr = null;
			var matchResult = sortURL.match(new RegExp(sortPropertyName + "=(asc|desc)", "gi"));
			var order = null;
			if (matchResult) {
				order = RegExp.$1;
				if (order == 'asc') {
					sortBtnStr = sortBtnTemplate.replace("{sort-icon}", "sort-hover icon-arrow-up");
				} else if (order == 'desc') {
					sortBtnStr = sortBtnTemplate.replace("{sort-icon}", "sort-hover icon-arrow-down");
				}
			}
			if (sortBtnStr == null) {
				sortBtnStr = sortBtnTemplate.replace("{sort-icon}", "icon-arrow-down");
			}
			th.wrapInner("<div class='sort-title'></div>").append($(sortBtnStr));

			//当前排序
			th.prop("order", order);//设置当前的排序 方便可移动表格

			th.addClass("sort-th").click(function () {
				sortURL = $.table.tableURL(table);
				//清空上次排序
				sortURL = $.table.removeSortParam(sortURL);

				if (!order) { //asc
					order = "asc";
				} else if (order == "asc") { //desc
					order = "desc";
				} else if (order == "desc") { //none
					order = "asc";
				}

				if (order) {
					sortURL = sortURL + (sortURL.indexOf("?") == -1 ? "?" : "&");
					sortURL = sortURL + sortPropertyName + "=" + order;
				}

				$.table.reloadTable(table, sortURL, null);
			});

		});
	},
	/**
	 * paging
	 * @param pageSize
	 * @param pn
	 * @param child table
	 */
	turnPage: function (pageSize, pn, child) {
		var table = $(child).closest(".table-pagination").prev("table");
		if(!table.length) {
			table = $(child).closest("table");
		}

		var pageURL = $.table.tableURL(table);

		//清空上次分页
		pageURL = $.table.removePageParam(pageURL);


		pageURL = pageURL + (pageURL.indexOf("?") == -1 ? "?" : "&");

		var prefix = $.table.getPrefix(table);
		pageURL = pageURL + prefix + "page.pn=" + pn;

		if (pageSize) {
			pageURL = pageURL + "&" + prefix + "page.size=" + pageSize;
		}

		$.table.reloadTable(table, pageURL, null);
	},
	/**
	 * redirect
	 * @param table
	 * @param url
	 * @param backURL
	 */
	reloadTable: function (table, url, backURL) {

		if(!url) {
			url = $.table.tableURL(table);
		}

		if (!backURL) {
			backURL = url;
		}
		//modalDialog时 把当前url保存下来方便翻页和排序
		table.closest(".ui-dialog").data("url", backURL);

		if (table.data("async") == true) {
			$.mdsForm.waiting();

			var tableId = table.attr("id");
			var containerId = table.data("async-container");
			var headers = {};

			if(!containerId) {//只替换表格时使用
				headers.table = true;
			} else {
				headers.container = true;
			}

			$.ajax({
				url: url,
				async:true,
				headers: headers
			}).done(function (data) {
					if (containerId) {//装载到容器
						$("#" + containerId).replaceWith(data);
					} else {
						var pagination = table.next(".table-pagination");
						if(pagination.length) {
							pagination.remove();
						}
						table.replaceWith(data);
					}

					table = $("#" + tableId);
					table.data("url", backURL);
					$.table.initTable(table);

					var callback = table.data("async-callback");
					if(callback && window[callback]) {
						window[callback](table);
					}

					$.mdsForm.waitingOver();
				});
		} else {
			window.location.href = url;
		}
	}
	,
	/**
	 * Retrieve table's url
	 * @param table
	 * @return {*}
	 */
	tableURL : function(table) {
		var $dialog = table.closest(".ui-dialog");

		var url = table.data("url");
		if(!url && $dialog.length) {
			//modalDialog
			url = $dialog.data("url");
		}
		if (!url) {
			url = window.location.href;
		}
		//如果URL中包含锚点（#） 删除
		if(url.indexOf("#") > 0) {
			url = url.substring(0, url.indexOf("#"));
		}

		return url;
	},
	/**
	 *
	 * @param table
	 */
	encodeTableURL : function(table) {
		return encodeURIComponent($.table.tableURL(table));
	}
	,
	/**
	 * 获取传递参数时的前缀
	 * @param table
	 */
	getPrefix : function(table) {
		var prefix = table.data("prefix");
		if (!prefix) {
			prefix = "";
		} else {
			prefix = prefix + "_";
		}
		return prefix;
	}
	,
	removePageParam : function(pageURL) {
		pageURL = pageURL.replace(/\&\w*page.pn=\d+/gi, '');
		pageURL = pageURL.replace(/\?\w*page.pn=\d+\&/gi, '?');
		pageURL = pageURL.replace(/\?\w*page.pn=\d+/gi, '');
		pageURL = pageURL.replace(/\&\w*page.size=\d+/gi, '');
		pageURL = pageURL.replace(/\?\w*page.size=\d+\&/gi, '?');
		pageURL = pageURL.replace(/\?\w*page.size=\d+/gi, '');
		return pageURL;
	}
	,
	removeSortParam : function(sortURL) {
		sortURL = sortURL.replace(/\&\w*sort.*=((asc)|(desc))/gi, '');
		sortURL = sortURL.replace(/\?\w*sort.*=((asc)|(desc))\&/gi, '?');
		sortURL = sortURL.replace(/\?\w*sort.*=((asc)|(desc))/gi, '');
		return sortURL;
	},
	removeSearchParam : function(url, form) {
		$.each(form.serializeArray(), function() {
			var name = this.name;
			url = url.replace(new RegExp(name + "=.*?\&","g"), '');
			url = url.replace(new RegExp("[\&\?]" + name + "=.*$","g"), '');
		});
		return url;
	}
	,
	//格式化url前缀，默认清除url ? 后边的
	formatUrlPrefix : function(urlPrefix, $table) {

		if(!urlPrefix) {
			urlPrefix = $table.data("prefix-url");
		}

		if(!urlPrefix && $table && $table.length) {
			urlPrefix = decodeURIComponent($.table.tableURL($table));
		}

		if(!urlPrefix) {
			urlPrefix = currentURL;
		}

		if(urlPrefix.indexOf("?") >= 0) {
			return urlPrefix.substr(0, urlPrefix.indexOf("?"));
		}
		return urlPrefix;
	},

	initDeleteSelected : function($table, urlPrefix) {
		if(!$table || !$table.length) {
			return;
		}

		var $btn = $table.closest("[data-table='" + $table.attr("id") + "']").find(".btn-delete:not(.btn-custom)");
		urlPrefix = $.table.formatUrlPrefix(urlPrefix, $table);
		$btn.off("click").on("click", function() {
			var checkbox = $.table.getAllSelectedCheckbox($table);
			if(!checkbox.length)  return;

			$.mdsForm.confirm({
				message: window.Mds.getString("table_message_deleteconfirm"),
				ok : function() {
					window.location.href =
						urlPrefix + "/batch/delete?" + checkbox.serialize() + "&BackURL=" + $.table.encodeTableURL($table);
				}
			});
		});
	}
	,
	initUpdateSelected : function($table, urlPrefix) {
		if(!$table || !$table.length) {
			return;
		}
		var $btn = $table.closest("[data-table='" + $table.attr("id") + "']").find(".btn-update:not(.btn-custom)");
		urlPrefix = $.table.formatUrlPrefix(urlPrefix, $table);
		$btn.off("click").on("click", function() {
			var checkbox = $.table.getFirstSelectedCheckbox($table);
			if(!checkbox.length)  return;
			var id = checkbox.val();
			window.location.href = urlPrefix + "/" + id + "/update?BackURL=" + $.table.encodeTableURL($table);
		});
	},
	initCreate : function($table, urlPrefix) {
		if(!$table || !$table.length) {
			return;
		}
		var $btn = $table.closest("[data-table='" + $table.attr("id") + "']").find(".btn-create");

		$btn.addClass("no-disabled");

		$btn.off("click").on("click", function() {
			var url =  $.table.formatUrlPrefix(urlPrefix, $table) + "/create";
			if($btn.attr("href")) {
				url = $btn.attr("href");
			}
			window.location.href = url + (url.indexOf("?") == -1 ? "?" : "&") + "BackURL=" + $.table.encodeTableURL($table);
			return false;
		});
	},
	initTableBtn : function($table, urlPrefix) {
		if(!$table || !$table.length) {
			return;
		}
		$table.closest("[data-table=" + $table.attr("id") + "]").find(".btn").not(".btn-custom,.btn-create,.btn-update,.btn-delete").each(function() {
			var $btn = $(this);
			var url = $btn.attr("href");
			if(!url || url.indexOf("#") == 0 || url.indexOf("javascript:") == 0) {//没有url就不处理了
				return;
			}
			$btn.off("click").on("click", function() {
				window.location.href = url + (url.indexOf("?") == -1 ? "?" : "&") + "BackURL=" + $.table.encodeTableURL($table);
				return false;
			});
		});

		urlPrefix = $.table.formatUrlPrefix(urlPrefix, $table);
		//double click to edit
		if($table.hasClass("table-dblclick-edit")) {
			$table.children("tbody").children("tr").off("dblclick").on("dblclick", function() {
				var id = $(this).find(":checkbox[name=ids]").val();
				window.location.href = urlPrefix + "/" + id + "/update?BackURL=" + $.table.encodeTableURL($table);
			});
		}

	},
	getFirstSelectedCheckbox :function($table) {
		var checkbox = $("#table :checkbox:checked:first");
		if(!checkbox.length) {

			//表示不选中 不可以用，此时没必要弹窗
			if($(this).hasClass(".no-disable") == false) {
				return checkbox;
			}

			$.mdsForm.alert({
				message : window.Mds.getString("table_message_norecordselected")
			});
		}
		return checkbox;
	},
	getAllSelectedCheckbox :function($table) {
		var checkbox = $table.find(":checkbox:checked");
		if(!checkbox.length) {

			//表示不选中 不可以用，此时没必要弹窗
			if($(this).hasClass(".no-disable") == false) {
				return checkbox;
			}

			$.mdsForm.alert({
				message : window.Mds.getString("table_message_norecordselected")
			});
		}
		return checkbox;
	}
}

$.movable = {
	/**
	 * urlPrefix：指定移动URL的前缀，
	 * 如/sample，生成的URL格式为/sample/{fromId}/{toId}/{direction:方向(up|down)}
	 * @param table
	 * @param urlPrefix
	 */
	initMoveableTable : function(table) {
		if(!table.length) {
			return;
		}
		var urlPrefix = table.data("move-url-prefix");
		if(!urlPrefix) {
			$.mdsForm.alert({message : "请添加移动地址URL，如&lt;table move-url-prefix='/sample'&gt;<br/>自动生成：/sample/{fromId}/{toId}/{direction:方向(up|down)}"});
		}
		var fixHelper = function (e, tr) {
			var $originals = tr.children();
			var $helper = tr.clone();
			$helper.children().each(function (index) {
				// Set helper cell sizes to match the original sizes
				$(this).width($originals.eq(index).width())
			});
			return $helper;
		};

		//事表格可拖拽排序
		table.find("tbody")
			.sortable({
				helper: fixHelper,
				opacity: 0.5,
				cursor: "move",
				placeholder: "sortable-placeholder",
				update: function (even, ui) {
					even.stopPropagation();
					prepareMove(ui.item.find(".moveable").closest("td"));
				}
			});

		//弹出移动框
		table.find("a.pop-movable[rel=popover]")
			.mouseenter(function (e) {
				var a = $(this);
				a.popover("show");
				var idInput = a.closest("tr").find(".id");
				idInput.focus();
				a.next(".popover").find(".popover-up-btn,.popover-down-btn").click(function() {
					var fromId = $(this).closest("tr").prop("id");
					var toId = idInput.val();

					if(!/\d+/.test(toId)) {
						$.mdsForm.alert({message : "请输入数字!"});
						return;
					}

					var fromTD = $(this).closest("td");

					if($(this).hasClass("popover-up-btn")) {
						move(fromTD, fromId, toId, "up");
					} else {
						move(fromTD, fromId, toId, "down");
					}
				});
				a.parent().mouseleave(function() {
					a.popover("hide");
				});
			});

		table.find(".up-btn,.down-btn").click(function() {
			var fromTR = $(this).closest("tr");
			if($(this).hasClass("up-btn")) {
				fromTR.prev("tr").before(fromTR);
			} else {
				fromTR.next("tr").after(fromTR);
			}
			prepareMove($(this).closest("td"));
		});

		/**
		 *
		 * @param fromTD
		 */
		function prepareMove(fromTD) {
			var fromTR = fromTD.closest("tr");
			var fromId = fromTR.prop("id");
			var nextTR = fromTR.next("tr");
			if(nextTR.length) {
				move(fromTD, fromId, nextTR.prop("id"), "down");
			} else {
				var preTR = fromTR.prev("tr");
				move(fromTD, fromId, preTR.prop("id"), "up");
			}

		}
		function move(fromTD, fromId, toId, direction) {
			if(!(fromId && toId)) {
				return;
			}
			var order = $.movable.tdOrder(fromTD);
			if (!order) {
				$.mdsForm.alert({message: "请首先排序要移动的字段！"});
				return;
			}
			//如果升序排列 需要反转direction
			if(order == "desc") {
				if(direction == "up") {
					direction = "down";
				} else {
					direction = "up";
				}
			}
			$.mdsForm.waiting("正在移动");
			var url = urlPrefix + "/" + fromId + "/" + toId + "/" + direction;
			$.getJSON(url, function(data) {
				$.mdsForm.waitingOver();
				if(data.success) {
					$.table.reloadTable(fromTD.closest("table"));
				} else {
					$.mdsForm.alert({message : data.message});
				}

			});
		}
	}
	,
	initMovableReweight : function($btn, url) {
		$btn.click(function () {
			$.mdsForm.confirm({
				message: "确定优化权重吗？<br/><strong>注意：</strong>优化权重执行效率比较低，请在本系统使用人员较少时执行（如下班时间）",
				ok: function () {
					$.mdsForm.waiting("优化权重执行中。。");
					$.getJSON(url, function(data) {
						$.mdsForm.waitingOver();
						if(!data.success) {
							$.mdsForm.alert({message : data.message});
						} else {
							location.reload();
						}
					});
				}
			});
		});
	},

	tdOrder : function(td) {
		var tdIndex = td.closest("tr").children("td").index(td);
		return td.closest("table").find("thead > tr > th").eq(tdIndex).prop("order");
	}
};

$.btn = {
	initChangeStatus : function(urlPrefix, tableId, config) {
		$(config.btns.join(",")).each(function(i) {
			$(this).off("click").on("click", function() {
				var $table = $("#" + tableId);
				var checkbox = $.table.getAllSelectedCheckbox($table);
				if(checkbox.size() == 0) {
					return;
				}
				var title = config.titles[i];
				var message = config.messages[i];
				var status = config.status[i];
				var url = urlPrefix + "/" + status + "?" + checkbox.serialize();
				$.mdsForm.confirm({
					title : title,
					message : message,
					ok : function() {
						window.location.href = url;
					}
				});
			});
		});
	},
	/**
	 * Initialize change show hidden btn
	 */
	initChangeShowStatus : function(urlPrefix, tableId) {
		$.btn.initChangeStatus(urlPrefix, tableId, {
			btns : [".status-show", ".status-hide"],
			titles : ['显示数据', '隐藏数据'],
			messages : ['确认显示数据吗？', '确认隐藏数据吗？'],
			status : ['true', 'false']
		});
	}
};

$.array = {
	remove : function(array, data) {
		if(array.length == 0) {
			return;
		}
		for(var i = array.length - 1; i >= 0; i--) {
			if(array[i] == data) {
				array.splice(i, 1);
			}
		}
	},
	contains : function(array, data) {
		if(array.length == 0) {
			return false;
		}
		for(var i = array.length - 1; i >= 0; i--) {
			if(array[i] == data) {
				return true;
			}
		}
		return false;
	},
	indexOf : function(array, data) {
		if(array.length == 0) {
			return -1;
		}
		for(var i = array.length - 1; i >= 0; i--) {
			if(array[i] == data) {
				return i;
			}
		}
		return -1;
	},
	clear : function(array) {
		if(array.length == 0) {
			return;
		}
		array.splice(0, array.length);
	},
	trim : function(array) {
		for(var i = array.length - 1; i >= 0; i--) {
			if(array[i] == "" || array[i] == null) {
				array.splice(i, 1);
			}
		}
		return array;
	}

};

//#endregion