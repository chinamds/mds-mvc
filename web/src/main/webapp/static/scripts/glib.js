
//#region jsrender

/*! JsRender v1.0.0-beta: http://github.com/BorisMoore/jsrender and http://jsviews.com/jsviews
informal pre V1.0 commit counter: 53 */
/*
 * Optimized version of jQuery Templates, for rendering to string.
 * Does not require jQuery, or HTML DOM
 * Integrates with JsViews (http://jsviews.com/jsviews)
 *
 * Copyright 2014, Boris Moore
 * Released under the MIT License.
 */

;(function (global, jQuery, undefined) {
	// global is the this object, which is window when running in the usual browser environment.
	"use strict";

	if (jQuery && jQuery.views || global.jsviews) { return; } // JsRender is already loaded

	//========================== Top-level vars ==========================
	//onInit versus init? inherit/base/deriveFrom/extend/basetag

	var versionNumber = "v1.0.0-beta",

		$, jsvStoreName, rTag, rTmplString, indexStr, // nodeJsModule,

//TODO	tmplFnsCache = {},
		delimOpenChar0 = "{", delimOpenChar1 = "{", delimCloseChar0 = "}", delimCloseChar1 = "}", linkChar = "^",

		rPath = /^(!*?)(?:null|true|false|\d[\d.]*|([\w$]+|\.|~([\w$]+)|#(view|([\w$]+))?)([\w$.^]*?)(?:[.[^]([\w$]+)\]?)?)$/g,
		//                                     none   object     helper    view  viewProperty pathTokens      leafToken

		rParams = /(\()(?=\s*\()|(?:([([])\s*)?(?:(\^?)(!*?[#~]?[\w$.^]+)?\s*((\+\+|--)|\+|-|&&|\|\||===|!==|==|!=|<=|>=|[<>%*:?\/]|(=))\s*|(!*?[#~]?[\w$.^]+)([([])?)|(,\s*)|(\(?)\\?(?:(')|("))|(?:\s*(([)\]])(?=\s*\.|\s*\^|\s*$)|[)\]])([([]?))|(\s+)/g,
		//          lftPrn0        lftPrn        bound            path    operator err                                                eq             path2       prn    comma   lftPrn2   apos quot      rtPrn rtPrnDot                        prn2      space
		// (left paren? followed by (path? followed by operator) or (path followed by left paren?)) or comma or apos or quot or right paren or space

		rNewLine = /[ \t]*(\r\n|\n|\r)/g,
		rUnescapeQuotes = /\\(['"])/g,
		rEscapeQuotes = /['"\\]/g, // Escape quotes and \ character
		rBuildHash = /\x08(~)?([^\x08]+)\x08/g,
		rTestElseIf = /^if\s/,
		rFirstElem = /<(\w+)[>\s]/,
		rAttrEncode = /[\x00`><"'&]/g, // Includes > encoding since rConvertMarkers in JsViews does not skip > characters in attribute strings
		rHasHandlers = /^on[A-Z]|^convert(Back)?$/,
		rHtmlEncode = rAttrEncode,
		autoTmplName = 0,
		viewId = 0,
		charEntities = {
			"&": "&amp;",
			"<": "&lt;",
			">": "&gt;",
			"\x00": "&#0;",
			"'": "&#39;",
			'"': "&#34;",
			"`": "&#96;"
		},
		htmlStr = "html",
		tmplAttr = "data-jsv-tmpl",
		$render = {},
		jsvStores = {
			template: {
				compile: compileTmpl
			},
			tag: {
				compile: compileTag
			},
			helper: {},
			converter: {}
		},

		// jsviews object ($.views if jQuery is loaded)
		$views = {
			jsviews: versionNumber,
			render: $render,
			settings: function (settings) {
				$extend($viewsSettings, settings);
				dbgMode($viewsSettings._dbgMode);
				if ($viewsSettings.jsv) {
					$viewsSettings.jsv();
				}
			},
			sub: {
				// subscription, e.g. JsViews integration
				View: View,
				Err: JsViewsError,
				tmplFn: tmplFn,
				cvt: convertArgs,
				parse: parseParams,
				extend: $extend,
				err: error,
				syntaxErr: syntaxError,
				isFn: function (ob) {
					return typeof ob === "function"
				},
				DataMap: DataMap
			},
			_cnvt: convertVal,
			_tag: renderTag,

			_err: function (e) {
				// Place a breakpoint here to intercept template rendering errors
				return $viewsSettings._dbgMode ? ("Error: " + (e.message || e)) + ". " : '';
			}
		};

	function retVal(val) {
		return val;
	}

	function dbgBreak(val) {
		debugger;
		return val;
	}

	function dbgMode(debugMode) {
		$viewsSettings._dbgMode = debugMode;
		indexStr = debugMode ? "Unavailable (nested view): use #getIndex()" : ""; // If in debug mode set #index to a warning when in nested contexts
		$tags("dbg", $helpers.dbg = $converters.dbg = debugMode ? dbgBreak : retVal); // If in debug mode, register {{dbg/}}, {{dbg:...}} and ~dbg() to insert break points for debugging.
	}

	function DataMap(getTarget) {
		return {
			getTgt: getTarget,
			map: function (source) {
				var theMap = this; // Instance of DataMap
				if (theMap.src !== source) {
					if (theMap.src) {
						theMap.unmap();
					}
					if (typeof source === "object") {
						var changing,
						target = getTarget.apply(theMap, arguments);
						theMap.src = source;
						theMap.tgt = target;
					}
				}
			}
		}
	}

	function JsViewsError(message, object) {
		// Error exception type for JsViews/JsRender
		// Override of $.views.sub.Error is possible
		if (object && object.onError) {
			if (object.onError(message) === false) {
				return;
			}
		}
		this.name = ($.link ? "JsViews" : "JsRender") + " Error";
		this.message = message || this.name;
	}

	function $extend(target, source) {
		var name;
		target = target || {};
		for (name in source) {
			target[name] = source[name];
		}
		return target;
	}

	(JsViewsError.prototype = new Error()).constructor = JsViewsError;

	//========================== Top-level functions ==========================

	//===================
	// jsviews.delimiters
	//===================
	function $viewsDelimiters(openChars, closeChars, link) {
		// Set the tag opening and closing delimiters and 'link' character. Default is "{{", "}}" and "^"
		// openChars, closeChars: opening and closing strings, each with two characters

		if (!$viewsSub.rTag || openChars) {
			delimOpenChar0 = openChars ? openChars.charAt(0) : delimOpenChar0; // Escape the characters - since they could be regex special characters
			delimOpenChar1 = openChars ? openChars.charAt(1) : delimOpenChar1;
			delimCloseChar0 = closeChars ? closeChars.charAt(0) : delimCloseChar0;
			delimCloseChar1 = closeChars ? closeChars.charAt(1) : delimCloseChar1;
			linkChar = link || linkChar;
			openChars = "\\" + delimOpenChar0 + "(\\" + linkChar + ")?\\" + delimOpenChar1;  // Default is "{^{"
			closeChars = "\\" + delimCloseChar0 + "\\" + delimCloseChar1;                   // Default is "}}"
			// Build regex with new delimiters
			//          tag    (followed by / space or })   or cvtr+colon or html or code
			rTag = "(?:(?:(\\w+(?=[\\/\\s\\" + delimCloseChar0 + "]))|(?:(\\w+)?(:)|(>)|!--((?:[^-]|-(?!-))*)--|(\\*)))"
				+ "\\s*((?:[^\\" + delimCloseChar0 + "]|\\" + delimCloseChar0 + "(?!\\" + delimCloseChar1 + "))*?)";

			// make rTag available to JsViews (or other components) for parsing binding expressions
			$viewsSub.rTag = rTag + ")";

			rTag = new RegExp(openChars + rTag + "(\\/)?|(?:\\/(\\w+)))" + closeChars, "g");

			// Default:    bind           tag       converter colon html     comment            code      params            slash   closeBlock
			//           /{(\^)?{(?:(?:(\w+(?=[\/\s}]))|(?:(\w+)?(:)|(>)|!--((?:[^-]|-(?!-))*)--|(\*)))\s*((?:[^}]|}(?!}))*?)(\/)?|(?:\/(\w+)))}}/g

			rTmplString = new RegExp("<.*>|([^\\\\]|^)[{}]|" + openChars + ".*" + closeChars);
			// rTmplString looks for html tags or { or } char not preceded by \\, or JsRender tags {{xxx}}. Each of these strings are considered
			// NOT to be jQuery selectors
		}
		return [delimOpenChar0, delimOpenChar1, delimCloseChar0, delimCloseChar1, linkChar];
	}

	//=========
	// View.get
	//=========

	function getView(inner, type) { //view.get(inner, type)
		if (!type) {
			// view.get(type)
			type = inner;
			inner = undefined;
		}

		var views, i, l, found,
			view = this,
			root = !type || type === "root";
		// If type is undefined, returns root view (view under top view).

		if (inner) {
			// Go through views - this one, and all nested ones, depth-first - and return first one with given type.
			found = view.type === type ? view : undefined;
			if (!found) {
				views = view.views;
				if (view._.useKey) {
					for (i in views) {
						if (found = views[i].get(inner, type)) {
							break;
						}
					}
				} else for (i = 0, l = views.length; !found && i < l; i++) {
					found = views[i].get(inner, type);
				}
			}
		} else if (root) {
			// Find root view. (view whose parent is top view)
			while (view.parent.parent) {
				found = view = view.parent;
			}
		} else while (view && !found) {
			// Go through views - this one, and all parent ones - and return first one with given type.
			found = view.type === type ? view : undefined;
			view = view.parent;
		}
		return found;
	}

	function getNestedIndex() {
		var view = this.get("item");
		return view ? view.index : undefined;
	}

	getNestedIndex.depends = function () {
		return [this.get("item"), "index"];
	};

	function getIndex() {
		return this.index;
	}

	getIndex.depends = function () {
		return ["index"];
	};

	//==========
	// View.hlp
	//==========

	function getHelper(helper) {
		// Helper method called as view.hlp(key) from compiled template, for helper functions or template parameters ~foo
		var wrapped,
			view = this,
			ctx = view.linkCtx,
			res = (view.ctx || {})[helper];

		if (res === undefined && ctx && ctx.ctx) {
			res = ctx.ctx[helper];
		}
		if (res === undefined) {
			res = $helpers[helper];
		}

		if (res) {
			if ($isFunction(res) && !res._wrp) {
				wrapped = function () {
					// If it is of type function, and not already wrapped, we will wrap it, so if called with no this pointer it will be called with the
					// view as 'this' context. If the helper ~foo() was in a data-link expression, the view will have a 'temporary' linkCtx property too.
					// Note that helper functions on deeper paths will have specific this pointers, from the preceding path.
					// For example, ~util.foo() will have the ~util object as 'this' pointer
					return res.apply((!this || this === global) ? view : this, arguments);
				};
				wrapped._wrp = 1;
				$extend(wrapped, res); // Attach same expandos (if any) to the wrapped function
			}
		}
		return wrapped || res;
	}

	//==============
	// jsviews._cnvt
	//==============

	function convertVal(converter, view, tagCtx) {
		// self is template object or linkCtx object
		var tag, value, prop,
			boundTagCtx = +tagCtx === tagCtx && tagCtx, // if tagCtx is an integer, then it is the key for the boundTagCtx (compiled function to return the tagCtx)
			linkCtx = view.linkCtx; // For data-link="{cvt:...}"...

		if (boundTagCtx) {
			// This is a bound tag: {^{xx:yyy}}. Call compiled function which returns the tagCtxs for current data
			tagCtx = (boundTagCtx = view.tmpl.bnds[boundTagCtx - 1])(view.data, view, $views);
		}

		value = tagCtx.args[0];
		if (converter || boundTagCtx) {
			tag = linkCtx && linkCtx.tag || {
				_: {
					inline: !linkCtx,
					bnd: boundTagCtx
				},
				tagName: converter + ":",
				flow: true,
				_is: "tag"
			};

			for (prop in tagCtx.props) {
				if (rHasHandlers.test(prop)) {
					tag[prop] = tagCtx.props[prop]; // Copy over the onFoo props from tagCtx.props to tag (overrides values in tagDef).
				}
			}

			if (linkCtx) {
				linkCtx.tag = tag;
				tag.linkCtx = tag.linkCtx || linkCtx;
				tagCtx.ctx = extendCtx(tagCtx.ctx, linkCtx.view.ctx);
			}
			tag.tagCtx = tagCtx;
			tagCtx.view = view;

			tag.ctx = tagCtx.ctx || {};
			delete tagCtx.ctx;
			// Provide this tag on view, for addBindingMarkers on bound tags to add the tag to view._.bnds, associated with the tag id,
			view._.tag = tag;

			value = convertArgs(tag, tag.convert || converter !== "true" && converter)[0]; // If there is a convertBack but no convert, converter will be "true"

			// Call onRender (used by JsViews if present, to add binding annotations around rendered content)
			value = value != undefined ? value : "";
			value = boundTagCtx && view._.onRender
				? view._.onRender(value, view, boundTagCtx)
				: value;
			view._.tag = undefined;
		}
		return value;
	}

	function convertArgs(tag, converter) {
		var tagCtx = tag.tagCtx,
			view = tagCtx.view,
			args = tagCtx.args;

		converter = converter && ("" + converter === converter
			? (view.getRsc("converters", converter) || error("Unknown converter: '" + converter + "'"))
			: converter);

		args = !args.length && !tagCtx.index && tag.autoBind // On the opening tag with no args, if autoBind is true, bind the the current data context
			? [view.data]
			: converter
				? args.slice() // If there is a converter, use a copy of the tagCtx.args array for rendering, and replace the args[0] in
				// the copied array with the converted value. But we don not modify the value of tag.tagCtx.args[0] (the original args array)
				: args; // If no converter, render with the original tagCtx.args

		if (converter) {
			if (converter.depends) {
				tag.depends = $viewsSub.getDeps(tag.depends, tag, converter.depends, converter);
			}
			args[0] = converter.apply(tag, args);
		}
		return args;
	}

	//=============
	// jsviews._tag
	//=============

	function getResource(resourceType, itemName) {
		var res, store,
			view = this;
		while ((res === undefined) && view) {
			store = view.tmpl[resourceType];
			res = store && store[itemName];
			view = view.parent;
		}
		return res || $views[resourceType][itemName];
	}

	function renderTag(tagName, parentView, tmpl, tagCtxs, isRefresh) {
		// Called from within compiled template function, to render a template tag
		// Returns the rendered tag

		var render, tag, tags, attr, parentTag, i, l, itemRet, tagCtx, tagCtxCtx, content, boundTagFn, tagDef,
			callInit, map, thisMap, args, prop, props, converter, initialTmpl,
			ret = "",
			boundTagKey = +tagCtxs === tagCtxs && tagCtxs, // if tagCtxs is an integer, then it is the boundTagKey
			linkCtx = parentView.linkCtx || 0,
			ctx = parentView.ctx,
			parentTmpl = tmpl || parentView.tmpl;

		if (tagName._is === "tag") {
			tag = tagName;
			tagName = tag.tagName;
		}
		tag = tag || linkCtx.tag;

		// Provide tagCtx, linkCtx and ctx access from tag
		if (boundTagKey) {
			// if tagCtxs is an integer, we are data binding
			// Call compiled function which returns the tagCtxs for current data
			tagCtxs = (boundTagFn = parentTmpl.bnds[boundTagKey - 1])(parentView.data, parentView, $views);
		}

		l = tagCtxs.length;
		for (i = 0; i < l; i++) {
			tagCtx = tagCtxs[i];
			props = tagCtx.props;

			// Set the tmpl property to the content of the block tag, unless set as an override property on the tag
			content = tagCtx.tmpl;
			content = tagCtx.content = content && parentTmpl.tmpls[content - 1];
			tmpl = tagCtx.props.tmpl;
			if (!i && (!tmpl || !tag)) {
				tagDef = parentView.getRsc("tags", tagName) || error("Unknown tag: {{" + tagName + "}}");
			}
			tmpl = tmpl || (tag ? tag : tagDef).template || content;
			tmpl = "" + tmpl === tmpl // if a string
				? parentView.getRsc("templates", tmpl) || $templates(tmpl)
				: tmpl;

			$extend(tagCtx, {
				tmpl: tmpl,
				render: renderContent,
				index: i,
				view: parentView,
				ctx: extendCtx(tagCtx.ctx, ctx) // Extend parentView.ctx
			}); // Extend parentView.ctx

			if (!tag) {
				// This will only be hit for initial tagCtx (not for {{else}}) - if the tag instance does not exist yet
				// Instantiate tag if it does not yet exist
				if (tagDef._ctr) {
					// If the tag has not already been instantiated, we will create a new instance.
					// ~tag will access the tag, even within the rendering of the template content of this tag.
					// From child/descendant tags, can access using ~tag.parent, or ~parentTags.tagName
					//	TODO provide error handling owned by the tag - using tag.onError
					//				try {
					tag = new tagDef._ctr();
					callInit = !!tag.init;
					//				}
					//				catch(e) {
					//					tagDef.onError(e);
					//				}
					// Set attr on linkCtx to ensure outputting to the correct target attribute.
					tag.attr = tag.attr || tagDef.attr || undefined;
					// Setting either linkCtx.attr or this.attr in the init() allows per-instance choice of target attrib.
				} else {
					// This is a simple tag declared as a function, or with init set to false. We won't instantiate a specific tag constructor - just a standard instance object.
					tag = {
						// tag instance object if no init constructor
						render: tagDef.render
					};
				}
				tag._ = {
					inline: !linkCtx
				};
				if (linkCtx) {
					// Set attr on linkCtx to ensure outputting to the correct target attribute.
					linkCtx.attr = tag.attr = linkCtx.attr || tag.attr;
					linkCtx.tag = tag;
					tag.linkCtx = linkCtx;
				}
				if (tag._.bnd = boundTagFn || linkCtx.fn) {
					// Bound if {^{tag...}} or data-link="{tag...}"
					tag._.arrVws = {};
				} else if (tag.dataBoundOnly) {
					error("{^{" + tagName + "}} tag must be data-bound");
				}
				tag.tagName = tagName;
				tag.parent = parentTag = ctx && ctx.tag;
				tag._is = "tag";
				tag._def = tagDef;

				for (prop in props = tagCtx.props) {
					if (rHasHandlers.test(prop)) {
						tag[prop] = props[prop]; // Copy over the onFoo or convert or convertBack props from tagCtx.props to tag (overrides values in tagDef).
					}
				}
				//TODO better perf for childTags() - keep child tag.tags array, (and remove child, when disposed)
				// tag.tags = [];
				// Provide this tag on view, for addBindingMarkers on bound tags to add the tag to view._.bnds, associated with the tag id,
			}
			tagCtx.tag = tag;
			if (tag.map && tag.tagCtxs) {
				tagCtx.map = tag.tagCtxs[i].map; // Copy over the compiled map instance from the previous tagCtxs to the refreshed ones
			}
			if (!tag.flow) {
				tagCtxCtx = tagCtx.ctx = tagCtx.ctx || {};

				// tags hash: tag.ctx.tags, merged with parentView.ctx.tags,
				tags = tag.parents = tagCtxCtx.parentTags = ctx && extendCtx(tagCtxCtx.parentTags, ctx.parentTags) || {};
				if (parentTag) {
					tags[parentTag.tagName] = parentTag;
					//TODO better perf for childTags: parentTag.tags.push(tag);
				}
				tagCtxCtx.tag = tag;
			}
		}
		tag.tagCtxs = tagCtxs;
		parentView._.tag = tag;
		tag.rendering = {}; // Provide object for state during render calls to tag and elses. (Used by {{if}} and {{for}}...)
		for (i = 0; i < l; i++) {
			tagCtx = tag.tagCtx = tagCtxs[i];
			props = tagCtx.props;
			args = convertArgs(tag, tag.convert);

			if ((map = props.map || tag).map) {
				if (args.length || props.map) {
					thisMap = tagCtx.map = $extend(tagCtx.map || { unmap: map.unmap }, props); // Compiled map instance
					if (thisMap.src !== args[0]) {
						if (thisMap.src) {
							thisMap.unmap();
						}
						map.map.apply(thisMap, args);
					}
					args = [thisMap.tgt];
				}
			}
			tag.ctx = tagCtx.ctx;

			if (!i && callInit) {
				initialTmpl = tag.template;
				tag.init(tagCtx, linkCtx, tag.ctx);
				callInit = undefined;
				if (tag.template !== initialTmpl) {
					tag._.tmpl = tag.template; // This will override the tag.template and also tagCtx.props.tmpl for all tagCtxs
				}
			}

			itemRet = undefined;
			render = tag.render;
			if (render = tag.render) {
				itemRet = render.apply(tag, args);
			}
			args = args.length ? args : [parentView]; // no arguments - get data context from view.
			itemRet = itemRet !== undefined
				? itemRet // Return result of render function unless it is undefined, in which case return rendered template
				: tagCtx.render(args[0], true) || (isRefresh ? undefined : "");
			// No return value from render, and no template/content tagCtx.render(...), so return undefined
			ret = ret ? ret + (itemRet || "") : itemRet; // If no rendered content, this will be undefined
		}

		delete tag.rendering;

		tag.tagCtx = tag.tagCtxs[0];
		tag.ctx = tag.tagCtx.ctx;

		if (tag._.inline && (attr = tag.attr) && attr !== htmlStr) {
			// inline tag with attr set to "text" will insert HTML-encoded content - as if it was element-based innerText
			ret = attr === "text"
				? $converters.html(ret)
				: "";
		}
		return boundTagKey && parentView._.onRender
			// Call onRender (used by JsViews if present, to add binding annotations around rendered content)
			? parentView._.onRender(ret, parentView, boundTagKey)
			: ret;
	}

	//=================
	// View constructor
	//=================

	function View(context, type, parentView, data, template, key, contentTmpl, onRender) {
		// Constructor for view object in view hierarchy. (Augmented by JsViews if JsViews is loaded)
		var views, parentView_, tag,
			isArray = type === "array",
			self_ = {
				key: 0,
				useKey: isArray ? 0 : 1,
				id: "" + viewId++,
				onRender: onRender,
				bnds: {}
			},
			self = {
				data: data,
				tmpl: template,
				content: contentTmpl,
				views: isArray ? [] : {},
				parent: parentView,
				type: type,
				// If the data is an array, this is an 'array view' with a views array for each child 'item view'
				// If the data is not an array, this is an 'item view' with a views 'map' object for any child nested views
				// ._.useKey is non zero if is not an 'array view' (owning a data array). Uuse this as next key for adding to child views map
				get: getView,
				getIndex: getIndex,
				getRsc: getResource,
				hlp: getHelper,
				_: self_,
				_is: "view"
			};
		if (parentView) {
			views = parentView.views;
			parentView_ = parentView._;
			if (parentView_.useKey) {
				// Parent is an 'item view'. Add this view to its views object
				// self._key = is the key in the parent view map
				views[self_.key = "_" + parentView_.useKey++] = self;
				self.index = indexStr;
				self.getIndex = getNestedIndex;
				tag = parentView_.tag;
				self_.bnd = isArray && (!tag || !!tag._.bnd && tag); // For array views that are data bound for collection change events, set the
				// view._.bnd property to true for top-level link() or data-link="{for}", or to the tag instance for a data-bound tag, e.g. {^{for ...}}
			} else {
				// Parent is an 'array view'. Add this view to its views array
				views.splice(
					// self._.key = self.index - the index in the parent view array
					self_.key = self.index = key,
				0, self);
			}
			// If no context was passed in, use parent context
			// If context was passed in, it should have been merged already with parent context
			self.ctx = context || parentView.ctx;
		} else {
			self.ctx = context;
		}
		return self;
	}

	//=============
	// Registration
	//=============

	function compileChildResources(parentTmpl) {
		var storeName, resources, resourceName, settings, compile;
		for (storeName in jsvStores) {
			settings = jsvStores[storeName];
			if ((compile = settings.compile) && (resources = parentTmpl[storeName + "s"])) {
				for (resourceName in resources) {
					// compile child resource declarations (templates, tags, converters or helpers)
					resources[resourceName] = compile(resourceName, resources[resourceName], parentTmpl, storeName, settings);
				}
			}
		}
	}

	function compileTag(name, tagDef, parentTmpl) {
		var init, tmpl;
		if ($isFunction(tagDef)) {
			// Simple tag declared as function. No presenter instantation.
			tagDef = {
				depends: tagDef.depends,
				render: tagDef
			};
		} else {
			// Tag declared as object, used as the prototype for tag instantiation (control/presenter)
			if (tmpl = tagDef.template) {
				tagDef.template = "" + tmpl === tmpl ? ($templates[tmpl] || $templates(tmpl)) : tmpl;
			}
			if (tagDef.init !== false) {
				// Set int: false on tagDef if you want to provide just a render method, or render and template, but no constuctor or prototype.
				// so equivalent to setting tag to render function, except you can also provide a template.
				init = tagDef._ctr = function (tagCtx) { };
				(init.prototype = tagDef).constructor = init;
			}
		}
		if (parentTmpl) {
			tagDef._parentTmpl = parentTmpl;
		}
		//TODO	tagDef.onError = function(e) {
		//			var error;
		//			if (error = this.prototype.onError) {
		//				error.call(this, e);
		//			} else {
		//				throw e;
		//			}
		//		}
		return tagDef;
	}

	function compileTmpl(name, tmpl, parentTmpl, storeName, storeSettings, options) {
		// tmpl is either a template object, a selector for a template script block, the name of a compiled template, or a template object

		//==== nested functions ====
		function tmplOrMarkupFromStr(value) {
			// If value is of type string - treat as selector, or name of compiled template
			// Return the template object, if already compiled, or the markup string

			if (("" + value === value) || value.nodeType > 0) {
				try {
					elem = value.nodeType > 0
					? value
					: !rTmplString.test(value)
					// If value is a string and does not contain HTML or tag content, then test as selector
						&& jQuery && jQuery(global.document).find(value)[0]; // TODO address case where DOM is not available
					// If selector is valid and returns at least one element, get first element
					// If invalid, jQuery will throw. We will stay with the original string.
				} catch (e) { }

				if (elem) {
					// Generally this is a script element.
					// However we allow it to be any element, so you can for example take the content of a div,
					// use it as a template, and replace it by the same content rendered against data.
					// e.g. for linking the content of a div to a container, and using the initial content as template:
					// $.link("#content", model, {tmpl: "#content"});

					value = elem.getAttribute(tmplAttr);
					name = name || value;
					value = $templates[value];
					if (!value) {
						// Not already compiled and cached, so compile and cache the name
						// Create a name for compiled template if none provided
						name = name || "_" + autoTmplName++;
						elem.setAttribute(tmplAttr, name);
						// Use tmpl as options
						value = $templates[name] = compileTmpl(name, elem.innerHTML, parentTmpl, storeName, storeSettings, options);
					}
					elem = null;
				}
				return value;
			}
			// If value is not a string, return undefined
		}

		var tmplOrMarkup, elem;

		//==== Compile the template ====
		tmpl = tmpl || "";
		tmplOrMarkup = tmplOrMarkupFromStr(tmpl);

		// If options, then this was already compiled from a (script) element template declaration.
		// If not, then if tmpl is a template object, use it for options
		options = options || (tmpl.markup ? tmpl : {});
		options.tmplName = name;
		if (parentTmpl) {
			options._parentTmpl = parentTmpl;
		}
		// If tmpl is not a markup string or a selector string, then it must be a template object
		// In that case, get it from the markup property of the object
		if (!tmplOrMarkup && tmpl.markup && (tmplOrMarkup = tmplOrMarkupFromStr(tmpl.markup))) {
			if (tmplOrMarkup.fn && (tmplOrMarkup.debug !== tmpl.debug || tmplOrMarkup.allowCode !== tmpl.allowCode)) {
				// if the string references a compiled template object, but the debug or allowCode props are different, need to recompile
				tmplOrMarkup = tmplOrMarkup.markup;
			}
		}
		if (tmplOrMarkup !== undefined) {
			if (name && !parentTmpl) {
				$render[name] = function () {
					return tmpl.render.apply(tmpl, arguments);
				};
			}
			if (tmplOrMarkup.fn || tmpl.fn) {
				// tmpl is already compiled, so use it, or if different name is provided, clone it
				if (tmplOrMarkup.fn) {
					if (name && name !== tmplOrMarkup.tmplName) {
						tmpl = extendCtx(options, tmplOrMarkup);
					} else {
						tmpl = tmplOrMarkup;
					}
				}
			} else {
				// tmplOrMarkup is a markup string, not a compiled template
				// Create template object
				tmpl = TmplObject(tmplOrMarkup, options);
				// Compile to AST and then to compiled function
				tmplFn(tmplOrMarkup.replace(rEscapeQuotes, "\\$&"), tmpl);
			}
			compileChildResources(options);
			return tmpl;
		}
	}
	//==== /end of function compile ====

	function TmplObject(markup, options) {
		// Template object constructor
		var htmlTag,
			wrapMap = $viewsSettings.wrapMap || {},
			tmpl = $extend(
				{
					markup: markup,
					tmpls: [],
					links: {}, // Compiled functions for link expressions
					tags: {}, // Compiled functions for bound tag expressions
					bnds: [],
					_is: "template",
					render: renderContent
				},
				options
			);

		if (!options.htmlTag) {
			// Set tmpl.tag to the top-level HTML tag used in the template, if any...
			htmlTag = rFirstElem.exec(markup);
			tmpl.htmlTag = htmlTag ? htmlTag[1].toLowerCase() : "";
		}
		htmlTag = wrapMap[tmpl.htmlTag];
		if (htmlTag && htmlTag !== wrapMap.div) {
			// When using JsViews, we trim templates which are inserted into HTML contexts where text nodes are not rendered (i.e. not 'Phrasing Content').
			// Currently not trimmed for <li> tag. (Not worth adding perf cost)
			tmpl.markup = $.trim(tmpl.markup);
		}

		return tmpl;
	}

	function registerStore(storeName, storeSettings) {

		function theStore(name, item, parentTmpl) {
			// The store is also the function used to add items to the store. e.g. $.templates, or $.views.tags

			// For store of name 'thing', Call as:
			//    $.views.things(items[, parentTmpl]),
			// or $.views.things(name, item[, parentTmpl])

			var onStore, compile, itemName, thisStore;

			if (name && "" + name !== name && !name.nodeType && !name.markup) {
				// Call to $.views.things(items[, parentTmpl]),

				// Adding items to the store
				// If name is a map, then item is parentTmpl. Iterate over map and call store for key.
				for (itemName in name) {
					theStore(itemName, name[itemName], item);
				}
				return $views;
			}
			// Adding a single unnamed item to the store
			if (item === undefined) {
				item = name;
				name = undefined;
			}
			if (name && "" + name !== name) { // name must be a string
				parentTmpl = item;
				item = name;
				name = undefined;
			}
			thisStore = parentTmpl ? parentTmpl[storeNames] = parentTmpl[storeNames] || {} : theStore;
			compile = storeSettings.compile;
			if (onStore = $viewsSub.onBeforeStoreItem) {
				// e.g. provide an external compiler or preprocess the item.
				compile = onStore(thisStore, name, item, compile) || compile;
			}
			if (!name) {
				item = compile(undefined, item);
			} else if (item === null) {
				// If item is null, delete this entry
				delete thisStore[name];
			} else {
				thisStore[name] = compile ? (item = compile(name, item, parentTmpl, storeName, storeSettings)) : item;
			}
			if (compile && item) {
				item._is = storeName; // Only do this for compiled objects (tags, templates...)
			}
			if (onStore = $viewsSub.onStoreItem) {
				// e.g. JsViews integration
				onStore(thisStore, name, item, compile);
			}
			return item;
		}

		var storeNames = storeName + "s";

		$views[storeNames] = theStore;
		jsvStores[storeName] = storeSettings;
	}

	//==============
	// renderContent
	//==============

	function renderContent(data, context, noIteration, parentView, key, onRender) {
		// Render template against data as a tree of subviews (nested rendered template instances), or as a string (top-level template).
		// If the data is the parent view, treat as noIteration, re-render with the same data context.
		var i, l, dataItem, newView, childView, itemResult, swapContent, tagCtx, contentTmpl, tag_, outerOnRender, tmplName, tmpl,
			self = this,
			allowDataLink = !self.attr || self.attr === htmlStr,
			result = "";
		if (!!context === context) {
			noIteration = context; // passing boolean as second param - noIteration
			context = undefined;
		}

		if (key === true) {
			swapContent = true;
			key = 0;
		}
		if (self.tag) {
			// This is a call from renderTag or tagCtx.render(...)
			tagCtx = self;
			self = self.tag;
			tag_ = self._;
			tmplName = self.tagName;
			tmpl = tag_.tmpl || tagCtx.tmpl;
			context = extendCtx(context, self.ctx);
			contentTmpl = tagCtx.content; // The wrapped content - to be added to views, below
			if (tagCtx.props.link === false) {
				// link=false setting on block tag
				// We will override inherited value of link by the explicit setting link=false taken from props
				// The child views of an unlinked view are also unlinked. So setting child back to true will not have any effect.
				context = context || {};
				context.link = false;
			}
			parentView = parentView || tagCtx.view;
			data = arguments.length ? data : parentView;
		} else {
			tmpl = self.jquery && (self[0] || error('Unknown template: "' + self.selector + '"')) // This is a call from $(selector).render
				|| self;
		}
		if (tmpl) {
			if (!parentView && data && data._is === "view") {
				parentView = data; // When passing in a view to render or link (and not passing in a parent view) use the passed in view as parentView
			}
			if (parentView) {
				contentTmpl = contentTmpl || parentView.content; // The wrapped content - to be added as #content property on views, below
				onRender = onRender || parentView._.onRender;
				if (data === parentView) {
					// Inherit the data from the parent view.
					// This may be the contents of an {{if}} block
					data = parentView.data;
				}
				context = extendCtx(context, parentView.ctx);
			}
			if (!parentView || parentView.data === undefined) {
				(context = context || {}).root = data; // Provide ~root as shortcut to top-level data.
			}

			// Set additional context on views created here, (as modified context inherited from the parent, and to be inherited by child views)
			// Note: If no jQuery, $extend does not support chained copies - so limit extend() to two parameters

			if (!tmpl.fn) {
				tmpl = $templates[tmpl] || $templates(tmpl);
			}

			if (tmpl) {
				onRender = (context && context.link) !== false && allowDataLink && onRender;
				// If link===false, do not call onRender, so no data-linking marker nodes
				outerOnRender = onRender;
				if (onRender === true) {
					// Used by view.refresh(). Don't create a new wrapper view.
					outerOnRender = undefined;
					onRender = parentView._.onRender;
				}
				context = tmpl.helpers
					? extendCtx(tmpl.helpers, context)
					: context;
				if ($.isArray(data) && !noIteration) {
					// Create a view for the array, whose child views correspond to each data item. (Note: if key and parentView are passed in
					// along with parent view, treat as insert -e.g. from view.addViews - so parentView is already the view item for array)
					newView = swapContent
						? parentView :
						(key !== undefined && parentView) || View(context, "array", parentView, data, tmpl, key, contentTmpl, onRender);
					for (i = 0, l = data.length; i < l; i++) {
						// Create a view for each data item.
						dataItem = data[i];
						childView = View(context, "item", newView, dataItem, tmpl, (key || 0) + i, contentTmpl, onRender);
						itemResult = tmpl.fn(dataItem, childView, $views);
						result += newView._.onRender ? newView._.onRender(itemResult, childView) : itemResult;
					}
				} else {
					// Create a view for singleton data object. The type of the view will be the tag name, e.g. "if" or "myTag" except for
					// "item", "array" and "data" views. A "data" view is from programatic render(object) against a 'singleton'.
					newView = swapContent ? parentView : View(context, tmplName || "data", parentView, data, tmpl, key, contentTmpl, onRender);
					if (tag_ && !self.flow) {
						newView.tag = self;
					}
					result += tmpl.fn(data, newView, $views);
				}
				return outerOnRender ? outerOnRender(result, newView) : result;
			}
		}
		return "";
	}

	//===========================
	// Build and compile template
	//===========================

	// Generate a reusable function that will serve to render a template against data
	// (Compile AST then build template function)

	function error(message) {
		throw new $viewsSub.Err(message);
	}

	function syntaxError(message) {
		error("Syntax error\n" + message);
	}

	function tmplFn(markup, tmpl, isLinkExpr, convertBack) {
		// Compile markup to AST (abtract syntax tree) then build the template function code from the AST nodes
		// Used for compiling templates, and also by JsViews to build functions for data link expressions

		//==== nested functions ====
		function pushprecedingContent(shift) {
			shift -= loc;
			if (shift) {
				content.push(markup.substr(loc, shift).replace(rNewLine, "\\n"));
			}
		}

		function blockTagCheck(tagName) {
			tagName && syntaxError('Unmatched or missing tag: "{{/' + tagName + '}}" in template:\n' + markup);
		}

		function parseTag(all, bind, tagName, converter, colon, html, comment, codeTag, params, slash, closeBlock, index) {

			//    bind         tag        converter colon html     comment            code      params            slash   closeBlock
			// /{(\^)?{(?:(?:(\w+(?=[\/\s}]))|(?:(\w+)?(:)|(>)|!--((?:[^-]|-(?!-))*)--|(\*)))\s*((?:[^}]|}(?!}))*?)(\/)?|(?:\/(\w+)))}}/g
			// Build abstract syntax tree (AST): [tagName, converter, params, content, hash, bindings, contentMarkup]
			if (html) {
				colon = ":";
				converter = htmlStr;
			}
			slash = slash || isLinkExpr;
			var noError, current0,
				pathBindings = bind && [],
				code = "",
				hash = "",
				passedCtx = "",
				// Block tag if not self-closing and not {{:}} or {{>}} (special case) and not a data-link expression
				block = !slash && !colon && !comment;

			//==== nested helper function ====
			tagName = tagName || (params = params || "#data", colon); // {{:}} is equivalent to {{:#data}}
			pushprecedingContent(index);
			loc = index + all.length; // location marker - parsed up to here
			if (codeTag) {
				if (allowCode) {
					content.push(["*", "\n" + params.replace(rUnescapeQuotes, "$1") + "\n"]);
				}
			} else if (tagName) {
				if (tagName === "else") {
					if (rTestElseIf.test(params)) {
						syntaxError('for "{{else if expr}}" use "{{else expr}}"');
					}
					pathBindings = current[6];
					current[7] = markup.substring(current[7], index); // contentMarkup for block tag
					current = stack.pop();
					content = current[3];
					block = true;
				}
				if (params) {
					// remove newlines from the params string, to avoid compiled code errors for unterminated strings
					params = params.replace(rNewLine, " ");
					code = parseParams(params, pathBindings, tmpl)
						.replace(rBuildHash, function (all, isCtx, keyValue) {
							if (isCtx) {
								passedCtx += keyValue + ",";
							} else {
								hash += keyValue + ",";
							}
							hasHandlers = hasHandlers || rHasHandlers.test(keyValue.split(":")[0]);
							return "";
						});
				}
				hash = hash.slice(0, -1);
				code = code.slice(0, -1);
				noError = hash && (hash.indexOf("noerror:true") + 1) && hash || "";

				newNode = [
						tagName,
						converter || !!convertBack || hasHandlers || "",
						code,
						block && [],
						'\n\tparams:"' + params + '",\n\tprops:{' + hash + "}"
							+ (passedCtx ? ",ctx:{" + passedCtx.slice(0, -1) + "}" : ""),
						noError,
						pathBindings || 0
				];
				content.push(newNode);
				if (block) {
					stack.push(current);
					current = newNode;
					current[7] = loc; // Store current location of open tag, to be able to add contentMarkup when we reach closing tag
				}
			} else if (closeBlock) {
				current0 = current[0];
				blockTagCheck(closeBlock !== current0 && current0 !== "else" && closeBlock);
				current[7] = markup.substring(current[7], index); // contentMarkup for block tag
				current = stack.pop();
			}
			blockTagCheck(!current && closeBlock);
			content = current[3];
		}
		//==== /end of nested functions ====

		var newNode, hasHandlers,
			allowCode = tmpl && tmpl.allowCode,
			astTop = [],
			loc = 0,
			stack = [],
			content = astTop,
			current = [, , , astTop];

		//TODO	result = tmplFnsCache[markup]; // Only cache if template is not named and markup length < ...,
		//and there are no bindings or subtemplates?? Consider standard optimization for data-link="a.b.c"
		//		if (result) {
		//			tmpl.fn = result;
		//		} else {

		//		result = markup;

		blockTagCheck(stack[0] && stack[0][3].pop()[0]);
		// Build the AST (abstract syntax tree) under astTop
		markup.replace(rTag, parseTag);

		pushprecedingContent(markup.length);

		if (loc = astTop[astTop.length - 1]) {
			blockTagCheck("" + loc !== loc && (+loc[7] === loc[7]) && loc[0]);
		}
		//			result = tmplFnsCache[markup] = buildCode(astTop, tmpl);
		//		}
		return buildCode(astTop, isLinkExpr ? markup : tmpl, isLinkExpr);
	}

	function buildCode(ast, tmpl, isLinkExpr) {
		// Build the template function code from the AST nodes, and set as property on the passed-in template object
		// Used for compiling templates, and also by JsViews to build functions for data link expressions
		var i, node, tagName, converter, params, hash, hasTag, hasEncoder, getsVal, hasCnvt, useCnvt, tmplBindings, pathBindings,
			nestedTmpls, tmplName, nestedTmpl, tagAndElses, content, markup, nextIsElse, oldCode, isElse, isGetVal, prm, tagCtxFn,
			tmplBindingKey = 0,
			code = "",
			noError = "",
			tmplOptions = {},
			l = ast.length;

		if ("" + tmpl === tmpl) {
			tmplName = isLinkExpr ? 'data-link="' + tmpl.replace(rNewLine, " ").slice(1, -1) + '"' : tmpl;
			tmpl = 0;
		} else {
			tmplName = tmpl.tmplName || "unnamed";
			if (tmpl.allowCode) {
				tmplOptions.allowCode = true;
			}
			if (tmpl.debug) {
				tmplOptions.debug = true;
			}
			tmplBindings = tmpl.bnds;
			nestedTmpls = tmpl.tmpls;
		}
		for (i = 0; i < l; i++) {
			// AST nodes: [tagName, converter, params, content, hash, noError, pathBindings, contentMarkup, link]
			node = ast[i];

			// Add newline for each callout to t() c() etc. and each markup string
			if ("" + node === node) {
				// a markup string to be inserted
				code += '\nret+="' + node + '";';
			} else {
				// a compiled tag expression to be inserted
				tagName = node[0];
				if (tagName === "*") {
					// Code tag: {{* }}
					code += "" + node[1];
				} else {
					converter = node[1];
					params = node[2];
					content = node[3];
					hash = node[4];
					noError = node[5];
					markup = node[7];

					if (!(isElse = tagName === "else")) {
						tmplBindingKey = 0;
						if (tmplBindings && (pathBindings = node[6])) { // Array of paths, or false if not data-bound
							tmplBindingKey = tmplBindings.push(pathBindings);
						}
					}
					if (isGetVal = tagName === ":") {
						if (converter) {
							tagName = converter === htmlStr ? ">" : converter + tagName;
						}
						if (noError) {
							// If the tag includes noerror=true, we will do a try catch around expressions for named or unnamed parameters
							// passed to the tag, and return the empty string for each expression if it throws during evaluation
							//TODO This does not work for general case - supporting noError on multiple expressions, e.g. tag args and properties.
							//Consider replacing with try<a.b.c(p,q) + a.d, xxx> and return the value of the expression a.b.c(p,q) + a.d, or, if it throws, return xxx||'' (rather than always the empty string)
							prm = "prm" + i;
							noError = "try{var " + prm + "=[" + params + "][0];}catch(e){" + prm + '="";}\n';
							params = prm;
						}
					} else {
						if (content) {
							// Create template object for nested template
							nestedTmpl = TmplObject(markup, tmplOptions);
							nestedTmpl.tmplName = tmplName + "/" + tagName;
							// Compile to AST and then to compiled function
							buildCode(content, nestedTmpl);
							nestedTmpls.push(nestedTmpl);
						}

						if (!isElse) {
							// This is not an else tag.
							tagAndElses = tagName;
							// Switch to a new code string for this bound tag (and its elses, if it has any) - for returning the tagCtxs array
							oldCode = code;
							code = "";
						}
						nextIsElse = ast[i + 1];
						nextIsElse = nextIsElse && nextIsElse[0] === "else";
					}

					hash += ",\n\targs:[" + params + "]}";

					if (isGetVal && (pathBindings || converter && converter !== htmlStr)) {
						// For convertVal we need a compiled function to return the new tagCtx(s)
						tagCtxFn = new Function("data,view,j,u", " // "
									+ tmplName + " " + tmplBindingKey + " " + tagName + "\n" + noError + "return {" + hash + ";");
						tagCtxFn.paths = pathBindings;
						tagCtxFn._ctxs = tagName;
						if (isLinkExpr) {
							return tagCtxFn;
						}
						useCnvt = 1;
					}

					code += (isGetVal
						? "\n" + (pathBindings ? "" : noError) + (isLinkExpr ? "return " : "ret+=") + (useCnvt // Call _cnvt if there is a converter: {{cnvt: ... }} or {^{cnvt: ... }}
							? (useCnvt = 0, hasCnvt = true, 'c("' + converter + '",view,' + (pathBindings
								? ((tmplBindings[tmplBindingKey - 1] = tagCtxFn), tmplBindingKey) // Store the compiled tagCtxFn in tmpl.bnds, and pass the key to convertVal()
								: "{" + hash) + ");")
							: tagName === ">"
								? (hasEncoder = true, "h(" + params + ");")
								: (getsVal = true, "(v=" + params + ")!=" + (isLinkExpr ? "=" : "") + 'u?v:"";') // Strict equality just for data-link="title{:expr}" so expr=null will remove title attribute
						)
						: (hasTag = true, "{view:view,tmpl:" // Add this tagCtx to the compiled code for the tagCtxs to be passed to renderTag()
							+ (content ? nestedTmpls.length : "0") + "," // For block tags, pass in the key (nestedTmpls.length) to the nested content template
							+ hash + ","));

					if (tagAndElses && !nextIsElse) {
						code = "[" + code.slice(0, -1) + "]"; // This is a data-link expression or the last {{else}} of an inline bound tag. We complete the code for returning the tagCtxs array
						if (isLinkExpr || pathBindings) {
							// This is a bound tag (data-link expression or inline bound tag {^{tag ...}}) so we store a compiled tagCtxs function in tmp.bnds
							code = new Function("data,view,j,u", " // " + tmplName + " " + tmplBindingKey + " " + tagAndElses + "\nreturn " + code + ";");
							if (pathBindings) {
								(tmplBindings[tmplBindingKey - 1] = code).paths = pathBindings;
							}
							code._ctxs = tagName;
							if (isLinkExpr) {
								return code; // For a data-link expression we return the compiled tagCtxs function
							}
						}

						// This is the last {{else}} for an inline tag.
						// For a bound tag, pass the tagCtxs fn lookup key to renderTag.
						// For an unbound tag, include the code directly for evaluating tagCtxs array
						code = oldCode + '\nret+=t("' + tagAndElses + '",view,this,' + (tmplBindingKey || code) + ");";
						pathBindings = 0;
						tagAndElses = 0;
					}
				}
			}
		}
		// Include only the var references that are needed in the code
		code = "// " + tmplName
			+ "\nvar j=j||" + (jQuery ? "jQuery." : "js") + "views"
			+ (getsVal ? ",v" : "")                      // gets value
			+ (hasTag ? ",t=j._tag" : "")                // has tag
			+ (hasCnvt ? ",c=j._cnvt" : "")              // converter
			+ (hasEncoder ? ",h=j.converters.html" : "") // html converter
			+ (isLinkExpr ? ";\n" : ',ret="";\n')
			+ ($viewsSettings.tryCatch ? "try{\n" : "")
			+ (tmplOptions.debug ? "debugger;" : "")
			+ code + (isLinkExpr ? "\n" : "\nreturn ret;\n")
			+ ($viewsSettings.tryCatch ? "\n}catch(e){return j._err(e);}" : "");
		try {
			code = new Function("data,view,j,u", code);
		} catch (e) {
			syntaxError("Compiled template code:\n\n" + code, e);
		}
		if (tmpl) {
			tmpl.fn = code;
		}
		return code;
	}

	function parseParams(params, bindings, tmpl) {

		//function pushBindings() { // Consider structured path bindings
		//	if (bindings) {
		//		named ? bindings[named] = bindings.pop(): bindings.push(list = []);
		//	}
		//}

		function parseTokens(all, lftPrn0, lftPrn, bound, path, operator, err, eq, path2, prn, comma, lftPrn2, apos, quot, rtPrn, rtPrnDot, prn2, space, index, full) {
			//rParams = /(\()(?=\s*\()|(?:([([])\s*)?(?:(\^?)(!*?[#~]?[\w$.^]+)?\s*((\+\+|--)|\+|-|&&|\|\||===|!==|==|!=|<=|>=|[<>%*:?\/]|(=))\s*|(!*?[#~]?[\w$.^]+)([([])?)|(,\s*)|(\(?)\\?(?:(')|("))|(?:\s*(([)\]])(?=\s*\.|\s*\^)|[)\]])([([]?))|(\s+)/g,
			//          lftPrn0        lftPrn        bound            path    operator err                                                eq             path2       prn    comma   lftPrn2   apos quot      rtPrn rtPrnDot                        prn2      space
			// (left paren? followed by (path? followed by operator) or (path followed by paren?)) or comma or apos or quot or right paren or space
			var expr;
			operator = operator || "";
			lftPrn = lftPrn || lftPrn0 || lftPrn2;
			path = path || path2;
			prn = prn || prn2 || "";

			function parsePath(allPath, not, object, helper, view, viewProperty, pathTokens, leafToken) {
				// rPath = /^(?:null|true|false|\d[\d.]*|(!*?)([\w$]+|\.|~([\w$]+)|#(view|([\w$]+))?)([\w$.^]*?)(?:[.[^]([\w$]+)\]?)?)$/g,
				//                                        none   object     helper    view  viewProperty pathTokens      leafToken
				if (object) {
					if (bindings) {
						if (named === "linkTo") {
							bindto = bindings._jsvto = bindings._jsvto || [];
							bindto.push(path);
						}
						if (!named || boundName) {
							bindings.push(path.slice(not.length)); // Add path binding for paths on props and args,
							//							list.push(path);
						}
					}
					if (object !== ".") {
						var ret = (helper
								? 'view.hlp("' + helper + '")'
								: view
									? "view"
									: "data")
							+ (leafToken
								? (viewProperty
									? "." + viewProperty
									: helper
										? ""
										: (view ? "" : "." + object)
									) + (pathTokens || "")
								: (leafToken = helper ? "" : view ? viewProperty || "" : object, ""));

						ret = ret + (leafToken ? "." + leafToken : "");

						return not + (ret.slice(0, 9) === "view.data"
							? ret.slice(5) // convert #view.data... to data...
							: ret);
					}
				}
				return allPath;
			}

			if (err && !aposed && !quoted) {
				syntaxError(params);
			} else {
				if (bindings && rtPrnDot && !aposed && !quoted) {
					// This is a binding to a path in which an object is returned by a helper/data function/expression, e.g. foo()^x.y or (a?b:c)^x.y
					// We create a compiled function to get the object instance (which will be called when the dependent data of the subexpression changes, to return the new object, and trigger re-binding of the subsequent path)
					if (!named || boundName || bindto) {
						expr = pathStart[parenDepth];
						if (full.length - 1 > index - expr) { // We need to compile a subexpression
							expr = full.slice(expr, index + 1);
							rtPrnDot = delimOpenChar1 + ":" + expr + delimCloseChar0; // The parameter or function subexpression
							//TODO Optimize along the lines of:
							//var paths = [];
							//rtPrnDot = tmplLinks[rtPrnDot] = tmplLinks[rtPrnDot] || tmplFn(delimOpenChar0 + rtPrnDot + delimCloseChar1, tmpl, true, paths); // Compile the expression (or use cached copy already in tmpl.links)
							//rtPrnDot.paths = rtPrnDot.paths || paths;

							rtPrnDot = tmplLinks[rtPrnDot] = tmplLinks[rtPrnDot] || tmplFn(delimOpenChar0 + rtPrnDot + delimCloseChar1, tmpl, true); // Compile the expression (or use cached copy already in tmpl.links)
							if (!rtPrnDot.paths) {
								parseParams(expr, rtPrnDot.paths = [], tmpl);
							}
							(bindto || bindings).push({ _jsvOb: rtPrnDot }); // Insert special object for in path bindings, to be used for binding the compiled sub expression ()
							//list.push({_jsvOb: rtPrnDot});
						}
					}
				}
				return (aposed
					// within single-quoted string
					? (aposed = !apos, (aposed ? all : '"'))
					: quoted
					// within double-quoted string
						? (quoted = !quot, (quoted ? all : '"'))
						:
					(
						(lftPrn
								? (parenDepth++, pathStart[parenDepth] = index++, lftPrn)
								: "")
						+ (space
							? (parenDepth
								? ""
								//: (pushBindings(), named
								//	: ",")
								: named
									? (named = boundName = bindto = false, "\b")
									: ","
							)
							: eq
					// named param
					// Insert backspace \b (\x08) as separator for named params, used subsequently by rBuildHash
								? (parenDepth && syntaxError(params), named = path, boundName = bound, /*pushBindings(),*/ '\b' + path + ':')
								: path
					// path
									? (path.split("^").join(".").replace(rPath, parsePath)
										+ (prn
											? (fnCall[++parenDepth] = true, path.charAt(0) !== "." && (pathStart[parenDepth] = index), prn)
											: operator)
									)
									: operator
										? operator
										: rtPrn
					// function
											? ((fnCall[parenDepth--] = false, rtPrn)
												+ (prn
													? (fnCall[++parenDepth] = true, prn)
													: "")
											)
											: comma
												? (fnCall[parenDepth] || syntaxError(params), ",") // We don't allow top-level literal arrays or objects
												: lftPrn0
													? ""
													: (aposed = apos, quoted = quot, '"')
					))
				);
			}
		}

		var named, bindto, boundName, // list,
			tmplLinks = tmpl.links,
			fnCall = {},
			pathStart = { 0: -1 },
			parenDepth = 0,
			quoted = false, // boolean for string content in double quotes
			aposed = false; // or in single quotes

		//pushBindings();

		return (params + " ")
			.replace(/\)\^/g, ").") // Treat "...foo()^bar..." as equivalent to "...foo().bar..."
								//since preceding computed observables in the path will always be updated if their dependencies change
			.replace(rParams, parseTokens);
	}

	//==========
	// Utilities
	//==========

	// Merge objects, in particular contexts which inherit from parent contexts
	function extendCtx(context, parentContext) {
		// Return copy of parentContext, unless context is defined and is different, in which case return a new merged context
		// If neither context nor parentContext are defined, return undefined
		return context && context !== parentContext
			? (parentContext
				? $extend($extend({}, parentContext), context)
				: context)
			: parentContext && $extend({}, parentContext);
	}

	// Get character entity for HTML and Attribute encoding
	function getCharEntity(ch) {
		return charEntities[ch] || (charEntities[ch] = "&#" + ch.charCodeAt(0) + ";");
	}

	//========================== Initialize ==========================

	for (jsvStoreName in jsvStores) {
		registerStore(jsvStoreName, jsvStores[jsvStoreName]);
	}

	var $observable,
		$templates = $views.templates,
		$converters = $views.converters,
		$helpers = $views.helpers,
		$tags = $views.tags,
		$viewsSub = $views.sub,
		$isFunction = $viewsSub.isFn,
		$viewsSettings = $views.settings;

	if (jQuery) {
		////////////////////////////////////////////////////////////////////////////////////////////////
		// jQuery is loaded, so make $ the jQuery object
		$ = jQuery;
		$.fn.render = renderContent;
		if ($observable = $.observable) {
			$extend($viewsSub, $observable.sub); // jquery.observable.js was loaded before jsrender.js
			delete $observable.sub;
		}
	} else {
		////////////////////////////////////////////////////////////////////////////////////////////////
		// jQuery is not loaded.

		$ = global.jsviews = {};

		$.isArray = Array && Array.isArray || function (obj) {
			return Object.prototype.toString.call(obj) === "[object Array]";
		};

		//	//========================== Future Node.js support ==========================
		//	if ((nodeJsModule = global.module) && nodeJsModule.exports) {
		//		nodeJsModule.exports = $;
		//	}
	}

	$.render = $render;
	$.views = $views;
	$.templates = $templates = $views.templates;

	$viewsSettings({
		debugMode: dbgMode,
		delimiters: $viewsDelimiters,
		_dbgMode: true,
		tryCatch: true
	});

	//========================== Register tags ==========================

	$tags({
		"else": function () { }, // Does nothing but ensures {{else}} tags are recognized as valid
		"if": {
			render: function (val) {
				// This function is called once for {{if}} and once for each {{else}}.
				// We will use the tag.rendering object for carrying rendering state across the calls.
				// If not done (a previous block has not been rendered), look at expression for this block and render the block if expression is truthy
				// Otherwise return ""
				var self = this,
					ret = (self.rendering.done || !val && (arguments.length || !self.tagCtx.index))
						? ""
						: (self.rendering.done = true, self.selected = self.tagCtx.index,
							// Test is satisfied, so render content on current context. We call tagCtx.render() rather than return undefined
							// (which would also render the tmpl/content on the current context but would iterate if it is an array)
							self.tagCtx.render(self.tagCtx.view, true)); // no arg, so renders against parentView.data
				return ret;
			},
			onUpdate: function (ev, eventArgs, tagCtxs) {
				var tci, prevArg, different;
				for (tci = 0; (prevArg = this.tagCtxs[tci]) && prevArg.args.length; tci++) {
					prevArg = prevArg.args[0];
					different = !prevArg !== !tagCtxs[tci].args[0];
					if ((!this.convert && !!prevArg) || different) {
						return different;
						// If there is no converter, and newArg and prevArg are both truthy, return false to cancel update. (Even if values on later elses are different, we still don't want to update, since rendered output would be unchanged)
						// If newArg and prevArg are different, return true, to update
						// If newArg and prevArg are both falsey, move to the next {{else ...}}
					}
				}
				// Boolean value of all args are unchanged (falsey), so return false to cancel update
				return false;
			},
			flow: true
		},
		"for": {
			render: function (val) {
				// This function is called once for {{for}} and once for each {{else}}.
				// We will use the tag.rendering object for carrying rendering state across the calls.
				var finalElse,
					self = this,
					tagCtx = self.tagCtx,
					result = "",
					done = 0;

				if (!self.rendering.done) {
					if (finalElse = !arguments.length) {
						val = tagCtx.view.data; // For the final else, defaults to current data without iteration.
					}
					if (val !== undefined) {
						result += tagCtx.render(val, finalElse); // Iterates except on final else, if data is an array. (Use {{include}} to compose templates without array iteration)
						done += $.isArray(val) ? val.length : 1;
					}
					if (self.rendering.done = done) {
						self.selected = tagCtx.index;
					}
					// If nothing was rendered we will look at the next {{else}}. Otherwise, we are done.
				}
				return result;
			},
			flow: true,
			autoBind: true
		},
		include: {
			flow: true,
			autoBind: true
		},
		"*": {
			// {{* code... }} - Ignored if template.allowCode is false. Otherwise include code in compiled template
			render: retVal,
			flow: true
		}
	});

	function getTargetProps(source) {
		// this pointer is theMap - which has tagCtx.props too
		// arguments: tagCtx.args.
		var key, prop,
			props = [];

		if (typeof source === "object") {
			for (key in source) {
				prop = source[key];
				if (!prop || !prop.toJSON || prop.toJSON()) {
					if (!$isFunction(prop)) {
						props.push({ key: key, prop: source[key] });
					}
				}
			}
		}
		return props;
	}

	$tags({
		props: $extend($extend({}, $tags["for"]),
			DataMap(getTargetProps)
		)
	});

	$tags.props.autoBind = true;

	//========================== Register converters ==========================

	$converters({
		html: function (text) {
			// HTML encode: Replace < > & and ' and " by corresponding entities.
			return text != undefined ? String(text).replace(rHtmlEncode, getCharEntity) : ""; // null and undefined return ""
		},
		attr: function (text) {
			// Attribute encode: Replace < > & ' and " by corresponding entities.
			return text != undefined ? String(text).replace(rAttrEncode, getCharEntity) : text === null ? text : ""; // null returns null, e.g. to remove attribute. undefined returns ""
		},
		url: function (text) {
			// URL encoding helper.
			return text != undefined ? encodeURI(String(text)) : text === null ? text : ""; // null returns null, e.g. to remove attribute. undefined returns ""
		}
	});

	//========================== Define default delimiters ==========================
	$viewsDelimiters();

})(this, this.jQuery);

//#endregion End jsrender

//#region parseJSON extension

/*!
* http://erraticdev.blomdsot.com/2010/12/converting-dates-in-json-strings-using.html
* jQuery.parseJSON() extension (supports ISO & Asp.net date conversion)
*
* Version 1.0 (13 Jan 2011)
*
* Copyright (c) 2011 Robert Koritnik
* Licensed under the terms of the MIT license
* http://www.opensource.org/licenses/mit-license.php
*/
(function ($) {

	// JSON RegExp
	var rvalidchars = /^[\],:{}\s]*$/;
	var rvalidescape = /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g;
	var rvalidtokens = /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g;
	var rvalidbraces = /(?:^|:|,)(?:\s*\[)+/g;
	var dateISO = /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:[.,]\d+)?Z/i;
	var dateNet = /\/Date\((\d+)(?:-\d+)?\)\//i;

	// replacer RegExp
	var replaceISO = /"(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(?:[.,](\d+))?Z"/i;
	var replaceNet = /"\\\/Date\((\d+)(?:-\d+)?\)\\\/"/i;

	// determine JSON native support
	var nativeJSON = (window.JSON && window.JSON.parse) ? true : false;
	var extendedJSON = nativeJSON && window.JSON.parse('{"x":9}', function (k, v) { return "Y"; }) === "Y";

	var jsonDateConverter = function (key, value) {
		if (typeof (value) === "string") {
			if (dateISO.test(value)) {
				return new Date(value);
			}
			if (dateNet.test(value)) {
				return new Date(parseInt(dateNet.exec(value)[1], 10));
			}
		}
		return value;
	};

	$.extend({
		parseJSON: function (data, convertDates) {
			/// <summary>Takes a well-formed JSON string and returns the resulting JavaScript object.</summary>
			/// <param name="data" type="String">The JSON string to parse.</param>
			/// <param name="convertDates" optional="true" type="Boolean">Set to true when you want ISO/Asp.net dates to be auto-converted to dates.</param>
			if (typeof data !== "string" || !data) {
				return null;
			}

			// Make sure leading/trailing whitespace is removed (IE can't handle it)
			data = $.trim(data);

			// Make sure the incoming data is actual JSON
			// Logic borrowed from http://json.org/json2.js
			if (rvalidchars.test(data
								.replace(rvalidescape, "@")
								.replace(rvalidtokens, "]")
								.replace(rvalidbraces, ""))) {
				// Try to use the native JSON parser
				if (extendedJSON || (nativeJSON && convertDates !== true)) {
					return window.JSON.parse(data, convertDates === true ? jsonDateConverter : undefined);
				}
				else {
					data = convertDates === true ?
												data.replace(replaceISO, "new Date(parseInt('$1',10),parseInt('$2',10)-1,parseInt('$3',10),parseInt('$4',10),parseInt('$5',10),parseInt('$6',10),(function(s){return parseInt(s,10)||0;})('$7'))")
														.replace(replaceNet, "new Date($1)") :
												data;
					return (new Function("return " + data))();
				}
			} else {
				$.error("Invalid JSON: " + data);
			}
		}
	});
})(jQuery);

//#endregion parseJSON extension

//#region Globalize

/*!
 * Globalize
 *
 * http://github.com/jquery/globalize
 *
 * Copyright Software Freedom Conservancy, Inc.
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 */

(function (window, undefined) {

	var Globalize,
		// private variables
		regexHex,
		regexInfinity,
		regexParseFloat,
		regexTrim,
		// private JavaScript utility functions
		arrayIndexOf,
		endsWith,
		extend,
		isArray,
		isFunction,
		isObject,
		startsWith,
		trim,
		truncate,
		zeroPad,
		// private Globalization utility functions
		appendPreOrPostMatch,
		expandFormat,
		formatDate,
		formatNumber,
		getTokenRegExp,
		getEra,
		getEraYear,
		parseExact,
		parseNegativePattern;

	// Global variable (Globalize) or CommonJS module (globalize)
	Globalize = function (cultureSelector) {
		return new Globalize.prototype.init(cultureSelector);
	};

	if (typeof require !== "undefined" &&
		typeof exports !== "undefined" &&
		typeof module !== "undefined") {
		// Assume CommonJS
		module.exports = Globalize;
	} else {
		// Export as global variable
		window.Globalize = Globalize;
	}

	Globalize.cultures = {};

	Globalize.prototype = {
		constructor: Globalize,
		init: function (cultureSelector) {
			this.cultures = Globalize.cultures;
			this.cultureSelector = cultureSelector;

			return this;
		}
	};
	Globalize.prototype.init.prototype = Globalize.prototype;

	// 1. When defining a culture, all fields are required except the ones stated as optional.
	// 2. Each culture should have a ".calendars" object with at least one calendar named "standard"
	//    which serves as the default calendar in use by that culture.
	// 3. Each culture should have a ".calendar" object which is the current calendar being used,
	//    it may be dynamically changed at any time to one of the calendars in ".calendars".
	Globalize.cultures["default"] = {
		// A unique name for the culture in the form <language code>-<country/region code>
		name: "en",
		// the name of the culture in the english language
		englishName: "English",
		// the name of the culture in its own language
		nativeName: "English",
		// whether the culture uses right-to-left text
		isRTL: false,
		// "language" is used for so-called "specific" cultures.
		// For example, the culture "es-CL" means "Spanish, in Chili".
		// It represents the Spanish-speaking culture as it is in Chili,
		// which might have different formatting rules or even translations
		// than Spanish in Spain. A "neutral" culture is one that is not
		// specific to a region. For example, the culture "es" is the generic
		// Spanish culture, which may be a more generalized version of the language
		// that may or may not be what a specific culture expects.
		// For a specific culture like "es-CL", the "language" field refers to the
		// neutral, generic culture information for the language it is using.
		// This is not always a simple matter of the string before the dash.
		// For example, the "zh-Hans" culture is netural (Simplified Chinese).
		// And the "zh-SG" culture is Simplified Chinese in Singapore, whose lanugage
		// field is "zh-CHS", not "zh".
		// This field should be used to navigate from a specific culture to it's
		// more general, neutral culture. If a culture is already as general as it
		// can get, the language may refer to itself.
		language: "en",
		// numberFormat defines general number formatting rules, like the digits in
		// each grouping, the group separator, and how negative numbers are displayed.
		numberFormat: {
			// [negativePattern]
			// Note, numberFormat.pattern has no "positivePattern" unlike percent and currency,
			// but is still defined as an array for consistency with them.
			//   negativePattern: one of "(n)|-n|- n|n-|n -"
			pattern: ["-n"],
			// number of decimal places normally shown
			decimals: 2,
			// string that separates number groups, as in 1,000,000
			",": ",",
			// string that separates a number from the fractional portion, as in 1.99
			".": ".",
			// array of numbers indicating the size of each number group.
			// TODO: more detailed description and example
			groupSizes: [3],
			// symbol used for positive numbers
			"+": "+",
			// symbol used for negative numbers
			"-": "-",
			// symbol used for NaN (Not-A-Number)
			"NaN": "NaN",
			// symbol used for Negative Infinity
			negativeInfinity: "-Infinity",
			// symbol used for Positive Infinity
			positiveInfinity: "Infinity",
			percent: {
				// [negativePattern, positivePattern]
				//   negativePattern: one of "-n %|-n%|-%n|%-n|%n-|n-%|n%-|-% n|n %-|% n-|% -n|n- %"
				//   positivePattern: one of "n %|n%|%n|% n"
				pattern: ["-n %", "n %"],
				// number of decimal places normally shown
				decimals: 2,
				// array of numbers indicating the size of each number group.
				// TODO: more detailed description and example
				groupSizes: [3],
				// string that separates number groups, as in 1,000,000
				",": ",",
				// string that separates a number from the fractional portion, as in 1.99
				".": ".",
				// symbol used to represent a percentage
				symbol: "%"
			},
			currency: {
				// [negativePattern, positivePattern]
				//   negativePattern: one of "($n)|-$n|$-n|$n-|(n$)|-n$|n-$|n$-|-n $|-$ n|n $-|$ n-|$ -n|n- $|($ n)|(n $)"
				//   positivePattern: one of "$n|n$|$ n|n $"
				pattern: ["($n)", "$n"],
				// number of decimal places normally shown
				decimals: 2,
				// array of numbers indicating the size of each number group.
				// TODO: more detailed description and example
				groupSizes: [3],
				// string that separates number groups, as in 1,000,000
				",": ",",
				// string that separates a number from the fractional portion, as in 1.99
				".": ".",
				// symbol used to represent currency
				symbol: "$"
			}
		},
		// calendars defines all the possible calendars used by this culture.
		// There should be at least one defined with name "standard", and is the default
		// calendar used by the culture.
		// A calendar contains information about how dates are formatted, information about
		// the calendar's eras, a standard set of the date formats,
		// translations for day and month names, and if the calendar is not based on the Gregorian
		// calendar, conversion functions to and from the Gregorian calendar.
		calendars: {
			standard: {
				// name that identifies the type of calendar this is
				name: "Gregorian_USEnglish",
				// separator of parts of a date (e.g. "/" in 11/05/1955)
				"/": "/",
				// separator of parts of a time (e.g. ":" in 05:44 PM)
				":": ":",
				// the first day of the week (0 = Sunday, 1 = Monday, etc)
				firstDay: 0,
				days: {
					// full day names
					names: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
					// abbreviated day names
					namesAbbr: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
					// shortest day names
					namesShort: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"]
				},
				months: {
					// full month names (13 months for lunar calendards -- 13th month should be "" if not lunar)
					names: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December", ""],
					// abbreviated month names
					namesAbbr: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", ""]
				},
				// AM and PM designators in one of these forms:
				// The usual view, and the upper and lower case versions
				//   [ standard, lowercase, uppercase ]
				// The culture does not use AM or PM (likely all standard date formats use 24 hour time)
				//   null
				AM: ["AM", "am", "AM"],
				PM: ["PM", "pm", "PM"],
				eras: [
					// eras in reverse chronological order.
					// name: the name of the era in this culture (e.g. A.D., C.E.)
					// start: when the era starts in ticks (gregorian, gmt), null if it is the earliest supported era.
					// offset: offset in years from gregorian calendar
					{
						"name": "A.D.",
						"start": null,
						"offset": 0
					}
				],
				// when a two digit year is given, it will never be parsed as a four digit
				// year greater than this year (in the appropriate era for the culture)
				// Set it as a full year (e.g. 2029) or use an offset format starting from
				// the current year: "+19" would correspond to 2029 if the current year 2010.
				twoDigitYearMax: 2029,
				// set of predefined date and time patterns used by the culture
				// these represent the format someone in this culture would expect
				// to see given the portions of the date that are shown.
				patterns: {
					// short date pattern
					d: "M/d/yyyy",
					// long date pattern
					D: "dddd, MMMM dd, yyyy",
					// short time pattern
					t: "h:mm tt",
					// long time pattern
					T: "h:mm:ss tt",
					// long date, short time pattern
					f: "dddd, MMMM dd, yyyy h:mm tt",
					// long date, long time pattern
					F: "dddd, MMMM dd, yyyy h:mm:ss tt",
					// month/day pattern
					M: "MMMM dd",
					// month/year pattern
					Y: "yyyy MMMM",
					// S is a sortable format that does not vary by culture
					S: "yyyy\u0027-\u0027MM\u0027-\u0027dd\u0027T\u0027HH\u0027:\u0027mm\u0027:\u0027ss"
				}
				// optional fields for each calendar:
				/*
				monthsGenitive:
					Same as months but used when the day preceeds the month.
					Omit if the culture has no genitive distinction in month names.
					For an explaination of genitive months, see http://blogs.msdn.com/michkap/archive/2004/12/25/332259.aspx
				convert:
					Allows for the support of non-gregorian based calendars. This convert object is used to
					to convert a date to and from a gregorian calendar date to handle parsing and formatting.
					The two functions:
						fromGregorian( date )
							Given the date as a parameter, return an array with parts [ year, month, day ]
							corresponding to the non-gregorian based year, month, and day for the calendar.
						toGregorian( year, month, day )
							Given the non-gregorian year, month, and day, return a new Date() object
							set to the corresponding date in the gregorian calendar.
				*/
			}
		},
		// For localized strings
		messages: {}
	};

	Globalize.cultures["default"].calendar = Globalize.cultures["default"].calendars.standard;

	Globalize.cultures.en = Globalize.cultures["default"];

	Globalize.cultureSelector = "en";

	//
	// private variables
	//

	regexHex = /^0x[a-f0-9]+$/i;
	regexInfinity = /^[+\-]?infinity$/i;
	regexParseFloat = /^[+\-]?\d*\.?\d*(e[+\-]?\d+)?$/;
	regexTrim = /^\s+|\s+$/g;

	//
	// private JavaScript utility functions
	//

	arrayIndexOf = function (array, item) {
		if (array.indexOf) {
			return array.indexOf(item);
		}
		for (var i = 0, length = array.length; i < length; i++) {
			if (array[i] === item) {
				return i;
			}
		}
		return -1;
	};

	endsWith = function (value, pattern) {
		return value.substr(value.length - pattern.length) === pattern;
	};

	extend = function () {
		var options, name, src, copy, copyIsArray, clone,
			target = arguments[0] || {},
			i = 1,
			length = arguments.length,
			deep = false;

		// Handle a deep copy situation
		if (typeof target === "boolean") {
			deep = target;
			target = arguments[1] || {};
			// skip the boolean and the target
			i = 2;
		}

		// Handle case when target is a string or something (possible in deep copy)
		if (typeof target !== "object" && !isFunction(target)) {
			target = {};
		}

		for (; i < length; i++) {
			// Only deal with non-null/undefined values
			if ((options = arguments[i]) != null) {
				// Extend the base object
				for (name in options) {
					src = target[name];
					copy = options[name];

					// Prevent never-ending loop
					if (target === copy) {
						continue;
					}

					// Recurse if we're merging plain objects or arrays
					if (deep && copy && (isObject(copy) || (copyIsArray = isArray(copy)))) {
						if (copyIsArray) {
							copyIsArray = false;
							clone = src && isArray(src) ? src : [];

						} else {
							clone = src && isObject(src) ? src : {};
						}

						// Never move original objects, clone them
						target[name] = extend(deep, clone, copy);

						// Don't bring in undefined values
					} else if (copy !== undefined) {
						target[name] = copy;
					}
				}
			}
		}

		// Return the modified object
		return target;
	};

	isArray = Array.isArray || function (obj) {
		return Object.prototype.toString.call(obj) === "[object Array]";
	};

	isFunction = function (obj) {
		return Object.prototype.toString.call(obj) === "[object Function]";
	};

	isObject = function (obj) {
		return Object.prototype.toString.call(obj) === "[object Object]";
	};

	startsWith = function (value, pattern) {
		return value.indexOf(pattern) === 0;
	};

	trim = function (value) {
		return (value + "").replace(regexTrim, "");
	};

	truncate = function (value) {
		if (isNaN(value)) {
			return NaN;
		}
		return Math[value < 0 ? "ceil" : "floor"](value);
	};

	zeroPad = function (str, count, left) {
		var l;
		for (l = str.length; l < count; l += 1) {
			str = (left ? ("0" + str) : (str + "0"));
		}
		return str;
	};

	//
	// private Globalization utility functions
	//

	appendPreOrPostMatch = function (preMatch, strings) {
		// appends pre- and post- token match strings while removing escaped characters.
		// Returns a single quote count which is used to determine if the token occurs
		// in a string literal.
		var quoteCount = 0,
			escaped = false;
		for (var i = 0, il = preMatch.length; i < il; i++) {
			var c = preMatch.charAt(i);
			switch (c) {
				case "\'":
					if (escaped) {
						strings.push("\'");
					}
					else {
						quoteCount++;
					}
					escaped = false;
					break;
				case "\\":
					if (escaped) {
						strings.push("\\");
					}
					escaped = !escaped;
					break;
				default:
					strings.push(c);
					escaped = false;
					break;
			}
		}
		return quoteCount;
	};

	expandFormat = function (cal, format) {
		// expands unspecified or single character date formats into the full pattern.
		format = format || "F";
		var pattern,
			patterns = cal.patterns,
			len = format.length;
		if (len === 1) {
			pattern = patterns[format];
			if (!pattern) {
				throw "Invalid date format string \'" + format + "\'.";
			}
			format = pattern;
		}
		else if (len === 2 && format.charAt(0) === "%") {
			// %X escape format -- intended as a custom format string that is only one character, not a built-in format.
			format = format.charAt(1);
		}
		return format;
	};

	formatDate = function (value, format, culture) {
		var cal = culture.calendar,
			convert = cal.convert,
			ret;

		if (!format || !format.length || format === "i") {
			if (culture && culture.name.length) {
				if (convert) {
					// non-gregorian calendar, so we cannot use built-in toLocaleString()
					ret = formatDate(value, cal.patterns.F, culture);
				}
				else {
					var eraDate = new Date(value.getTime()),
						era = getEra(value, cal.eras);
					eraDate.setFullYear(getEraYear(value, cal, era));
					ret = eraDate.toLocaleString();
				}
			}
			else {
				ret = value.toString();
			}
			return ret;
		}

		var eras = cal.eras,
			sortable = format === "s";
		format = expandFormat(cal, format);

		// Start with an empty string
		ret = [];
		var hour,
			zeros = ["0", "00", "000"],
			foundDay,
			checkedDay,
			dayPartRegExp = /([^d]|^)(d|dd)([^d]|$)/g,
			quoteCount = 0,
			tokenRegExp = getTokenRegExp(),
			converted;

		function padZeros(num, c) {
			var r, s = num + "";
			if (c > 1 && s.length < c) {
				r = (zeros[c - 2] + s);
				return r.substr(r.length - c, c);
			}
			else {
				r = s;
			}
			return r;
		}

		function hasDay() {
			if (foundDay || checkedDay) {
				return foundDay;
			}
			foundDay = dayPartRegExp.test(format);
			checkedDay = true;
			return foundDay;
		}

		function getPart(date, part) {
			if (converted) {
				return converted[part];
			}
			switch (part) {
				case 0:
					return date.getFullYear();
				case 1:
					return date.getMonth();
				case 2:
					return date.getDate();
				default:
					throw "Invalid part value " + part;
			}
		}

		if (!sortable && convert) {
			converted = convert.fromGregorian(value);
		}

		for (; ;) {
			// Save the current index
			var index = tokenRegExp.lastIndex,
				// Look for the next pattern
				ar = tokenRegExp.exec(format);

			// Append the text before the pattern (or the end of the string if not found)
			var preMatch = format.slice(index, ar ? ar.index : format.length);
			quoteCount += appendPreOrPostMatch(preMatch, ret);

			if (!ar) {
				break;
			}

			// do not replace any matches that occur inside a string literal.
			if (quoteCount % 2) {
				ret.push(ar[0]);
				continue;
			}

			var current = ar[0],
				clength = current.length;

			switch (current) {
				case "ddd":
					//Day of the week, as a three-letter abbreviation
				case "dddd":
					// Day of the week, using the full name
					var names = (clength === 3) ? cal.days.namesAbbr : cal.days.names;
					ret.push(names[value.getDay()]);
					break;
				case "d":
					// Day of month, without leading zero for single-digit days
				case "dd":
					// Day of month, with leading zero for single-digit days
					foundDay = true;
					ret.push(
						padZeros(getPart(value, 2), clength)
					);
					break;
				case "MMM":
					// Month, as a three-letter abbreviation
				case "MMMM":
					// Month, using the full name
					var part = getPart(value, 1);
					ret.push(
						(cal.monthsGenitive && hasDay()) ?
						(cal.monthsGenitive[clength === 3 ? "namesAbbr" : "names"][part]) :
						(cal.months[clength === 3 ? "namesAbbr" : "names"][part])
					);
					break;
				case "M":
					// Month, as digits, with no leading zero for single-digit months
				case "MM":
					// Month, as digits, with leading zero for single-digit months
					ret.push(
						padZeros(getPart(value, 1) + 1, clength)
					);
					break;
				case "y":
					// Year, as two digits, but with no leading zero for years less than 10
				case "yy":
					// Year, as two digits, with leading zero for years less than 10
				case "yyyy":
					// Year represented by four full digits
					part = converted ? converted[0] : getEraYear(value, cal, getEra(value, eras), sortable);
					if (clength < 4) {
						part = part % 100;
					}
					ret.push(
						padZeros(part, clength)
					);
					break;
				case "h":
					// Hours with no leading zero for single-digit hours, using 12-hour clock
				case "hh":
					// Hours with leading zero for single-digit hours, using 12-hour clock
					hour = value.getHours() % 12;
					if (hour === 0) hour = 12;
					ret.push(
						padZeros(hour, clength)
					);
					break;
				case "H":
					// Hours with no leading zero for single-digit hours, using 24-hour clock
				case "HH":
					// Hours with leading zero for single-digit hours, using 24-hour clock
					ret.push(
						padZeros(value.getHours(), clength)
					);
					break;
				case "m":
					// Minutes with no leading zero for single-digit minutes
				case "mm":
					// Minutes with leading zero for single-digit minutes
					ret.push(
						padZeros(value.getMinutes(), clength)
					);
					break;
				case "s":
					// Seconds with no leading zero for single-digit seconds
				case "ss":
					// Seconds with leading zero for single-digit seconds
					ret.push(
						padZeros(value.getSeconds(), clength)
					);
					break;
				case "t":
					// One character am/pm indicator ("a" or "p")
				case "tt":
					// Multicharacter am/pm indicator
					part = value.getHours() < 12 ? (cal.AM ? cal.AM[0] : " ") : (cal.PM ? cal.PM[0] : " ");
					ret.push(clength === 1 ? part.charAt(0) : part);
					break;
				case "f":
					// Deciseconds
				case "ff":
					// Centiseconds
				case "fff":
					// Milliseconds
					ret.push(
						padZeros(value.getMilliseconds(), 3).substr(0, clength)
					);
					break;
				case "z":
					// Time zone offset, no leading zero
				case "zz":
					// Time zone offset with leading zero
					hour = value.getTimezoneOffset() / 60;
					ret.push(
						(hour <= 0 ? "+" : "-") + padZeros(Math.floor(Math.abs(hour)), clength)
					);
					break;
				case "zzz":
					// Time zone offset with leading zero
					hour = value.getTimezoneOffset() / 60;
					ret.push(
						(hour <= 0 ? "+" : "-") + padZeros(Math.floor(Math.abs(hour)), 2) +
						// Hard coded ":" separator, rather than using cal.TimeSeparator
						// Repeated here for consistency, plus ":" was already assumed in date parsing.
						":" + padZeros(Math.abs(value.getTimezoneOffset() % 60), 2)
					);
					break;
				case "g":
				case "gg":
					if (cal.eras) {
						ret.push(
							cal.eras[getEra(value, eras)].name
						);
					}
					break;
				case "/":
					ret.push(cal["/"]);
					break;
				default:
					throw "Invalid date format pattern \'" + current + "\'.";
			}
		}
		return ret.join("");
	};

	// formatNumber
	(function () {
		var expandNumber;

		expandNumber = function (number, precision, formatInfo) {
			var groupSizes = formatInfo.groupSizes,
				curSize = groupSizes[0],
				curGroupIndex = 1,
				factor = Math.pow(10, precision),
				rounded = Math.round(number * factor) / factor;

			if (!isFinite(rounded)) {
				rounded = number;
			}
			number = rounded;

			var numberString = number + "",
				right = "",
				split = numberString.split(/e/i),
				exponent = split.length > 1 ? parseInt(split[1], 10) : 0;
			numberString = split[0];
			split = numberString.split(".");
			numberString = split[0];
			right = split.length > 1 ? split[1] : "";

			var l;
			if (exponent > 0) {
				right = zeroPad(right, exponent, false);
				numberString += right.slice(0, exponent);
				right = right.substr(exponent);
			}
			else if (exponent < 0) {
				exponent = -exponent;
				numberString = zeroPad(numberString, exponent + 1, true);
				right = numberString.slice(-exponent, numberString.length) + right;
				numberString = numberString.slice(0, -exponent);
			}

			if (precision > 0) {
				right = formatInfo["."] +
					((right.length > precision) ? right.slice(0, precision) : zeroPad(right, precision));
			}
			else {
				right = "";
			}

			var stringIndex = numberString.length - 1,
				sep = formatInfo[","],
				ret = "";

			while (stringIndex >= 0) {
				if (curSize === 0 || curSize > stringIndex) {
					return numberString.slice(0, stringIndex + 1) + (ret.length ? (sep + ret + right) : right);
				}
				ret = numberString.slice(stringIndex - curSize + 1, stringIndex + 1) + (ret.length ? (sep + ret) : "");

				stringIndex -= curSize;

				if (curGroupIndex < groupSizes.length) {
					curSize = groupSizes[curGroupIndex];
					curGroupIndex++;
				}
			}

			return numberString.slice(0, stringIndex + 1) + sep + ret + right;
		};

		formatNumber = function (value, format, culture) {
			if (!isFinite(value)) {
				if (value === Infinity) {
					return culture.numberFormat.positiveInfinity;
				}
				if (value === -Infinity) {
					return culture.numberFormat.negativeInfinity;
				}
				return culture.numberFormat.NaN;
			}
			if (!format || format === "i") {
				return culture.name.length ? value.toLocaleString() : value.toString();
			}
			format = format || "D";

			var nf = culture.numberFormat,
				number = Math.abs(value),
				precision = -1,
				pattern;
			if (format.length > 1) precision = parseInt(format.slice(1), 10);

			var current = format.charAt(0).toUpperCase(),
				formatInfo;

			switch (current) {
				case "D":
					pattern = "n";
					number = truncate(number);
					if (precision !== -1) {
						number = zeroPad("" + number, precision, true);
					}
					if (value < 0) number = "-" + number;
					break;
				case "N":
					formatInfo = nf;
					/* falls through */
				case "C":
					formatInfo = formatInfo || nf.currency;
					/* falls through */
				case "P":
					formatInfo = formatInfo || nf.percent;
					pattern = value < 0 ? formatInfo.pattern[0] : (formatInfo.pattern[1] || "n");
					if (precision === -1) precision = formatInfo.decimals;
					number = expandNumber(number * (current === "P" ? 100 : 1), precision, formatInfo);
					break;
				default:
					throw "Bad number format specifier: " + current;
			}

			var patternParts = /n|\$|-|%/g,
				ret = "";
			for (; ;) {
				var index = patternParts.lastIndex,
					ar = patternParts.exec(pattern);

				ret += pattern.slice(index, ar ? ar.index : pattern.length);

				if (!ar) {
					break;
				}

				switch (ar[0]) {
					case "n":
						ret += number;
						break;
					case "$":
						ret += nf.currency.symbol;
						break;
					case "-":
						// don't make 0 negative
						if (/[1-9]/.test(number)) {
							ret += nf["-"];
						}
						break;
					case "%":
						ret += nf.percent.symbol;
						break;
				}
			}

			return ret;
		};

	}());

	getTokenRegExp = function () {
		// regular expression for matching date and time tokens in format strings.
		return (/\/|dddd|ddd|dd|d|MMMM|MMM|MM|M|yyyy|yy|y|hh|h|HH|H|mm|m|ss|s|tt|t|fff|ff|f|zzz|zz|z|gg|g/g);
	};

	getEra = function (date, eras) {
		if (!eras) return 0;
		var start, ticks = date.getTime();
		for (var i = 0, l = eras.length; i < l; i++) {
			start = eras[i].start;
			if (start === null || ticks >= start) {
				return i;
			}
		}
		return 0;
	};

	getEraYear = function (date, cal, era, sortable) {
		var year = date.getFullYear();
		if (!sortable && cal.eras) {
			// convert normal gregorian year to era-shifted gregorian
			// year by subtracting the era offset
			year -= cal.eras[era].offset;
		}
		return year;
	};

	// parseExact
	(function () {
		var expandYear,
			getDayIndex,
			getMonthIndex,
			getParseRegExp,
			outOfRange,
			toUpper,
			toUpperArray;

		expandYear = function (cal, year) {
			// expands 2-digit year into 4 digits.
			if (year < 100) {
				var now = new Date(),
					era = getEra(now),
					curr = getEraYear(now, cal, era),
					twoDigitYearMax = cal.twoDigitYearMax;
				twoDigitYearMax = typeof twoDigitYearMax === "string" ? new Date().getFullYear() % 100 + parseInt(twoDigitYearMax, 10) : twoDigitYearMax;
				year += curr - (curr % 100);
				if (year > twoDigitYearMax) {
					year -= 100;
				}
			}
			return year;
		};

		getDayIndex = function (cal, value, abbr) {
			var ret,
				days = cal.days,
				upperDays = cal._upperDays;
			if (!upperDays) {
				cal._upperDays = upperDays = [
					toUpperArray(days.names),
					toUpperArray(days.namesAbbr),
					toUpperArray(days.namesShort)
				];
			}
			value = toUpper(value);
			if (abbr) {
				ret = arrayIndexOf(upperDays[1], value);
				if (ret === -1) {
					ret = arrayIndexOf(upperDays[2], value);
				}
			}
			else {
				ret = arrayIndexOf(upperDays[0], value);
			}
			return ret;
		};

		getMonthIndex = function (cal, value, abbr) {
			var months = cal.months,
				monthsGen = cal.monthsGenitive || cal.months,
				upperMonths = cal._upperMonths,
				upperMonthsGen = cal._upperMonthsGen;
			if (!upperMonths) {
				cal._upperMonths = upperMonths = [
					toUpperArray(months.names),
					toUpperArray(months.namesAbbr)
				];
				cal._upperMonthsGen = upperMonthsGen = [
					toUpperArray(monthsGen.names),
					toUpperArray(monthsGen.namesAbbr)
				];
			}
			value = toUpper(value);
			var i = arrayIndexOf(abbr ? upperMonths[1] : upperMonths[0], value);
			if (i < 0) {
				i = arrayIndexOf(abbr ? upperMonthsGen[1] : upperMonthsGen[0], value);
			}
			return i;
		};

		getParseRegExp = function (cal, format) {
			// converts a format string into a regular expression with groups that
			// can be used to extract date fields from a date string.
			// check for a cached parse regex.
			var re = cal._parseRegExp;
			if (!re) {
				cal._parseRegExp = re = {};
			}
			else {
				var reFormat = re[format];
				if (reFormat) {
					return reFormat;
				}
			}

			// expand single digit formats, then escape regular expression characters.
			var expFormat = expandFormat(cal, format).replace(/([\^\$\.\*\+\?\|\[\]\(\)\{\}])/g, "\\\\$1"),
				regexp = ["^"],
				groups = [],
				index = 0,
				quoteCount = 0,
				tokenRegExp = getTokenRegExp(),
				match;

			// iterate through each date token found.
			while ((match = tokenRegExp.exec(expFormat)) !== null) {
				var preMatch = expFormat.slice(index, match.index);
				index = tokenRegExp.lastIndex;

				// don't replace any matches that occur inside a string literal.
				quoteCount += appendPreOrPostMatch(preMatch, regexp);
				if (quoteCount % 2) {
					regexp.push(match[0]);
					continue;
				}

				// add a regex group for the token.
				var m = match[0],
					len = m.length,
					add;
				switch (m) {
					case "dddd": case "ddd":
					case "MMMM": case "MMM":
					case "gg": case "g":
						add = "(\\D+)";
						break;
					case "tt": case "t":
						add = "(\\D*)";
						break;
					case "yyyy":
					case "fff":
					case "ff":
					case "f":
						add = "(\\d{" + len + "})";
						break;
					case "dd": case "d":
					case "MM": case "M":
					case "yy": case "y":
					case "HH": case "H":
					case "hh": case "h":
					case "mm": case "m":
					case "ss": case "s":
						add = "(\\d\\d?)";
						break;
					case "zzz":
						add = "([+-]?\\d\\d?:\\d{2})";
						break;
					case "zz": case "z":
						add = "([+-]?\\d\\d?)";
						break;
					case "/":
						add = "(\\/)";
						break;
					default:
						throw "Invalid date format pattern \'" + m + "\'.";
				}
				if (add) {
					regexp.push(add);
				}
				groups.push(match[0]);
			}
			appendPreOrPostMatch(expFormat.slice(index), regexp);
			regexp.push("$");

			// allow whitespace to differ when matching formats.
			var regexpStr = regexp.join("").replace(/\s+/g, "\\s+"),
				parseRegExp = { "regExp": regexpStr, "groups": groups };

			// cache the regex for this format.
			return re[format] = parseRegExp;
		};

		outOfRange = function (value, low, high) {
			return value < low || value > high;
		};

		toUpper = function (value) {
			// "he-IL" has non-breaking space in weekday names.
			return value.split("\u00A0").join(" ").toUpperCase();
		};

		toUpperArray = function (arr) {
			var results = [];
			for (var i = 0, l = arr.length; i < l; i++) {
				results[i] = toUpper(arr[i]);
			}
			return results;
		};

		parseExact = function (value, format, culture) {
			// try to parse the date string by matching against the format string
			// while using the specified culture for date field names.
			value = trim(value);
			var cal = culture.calendar,
				// convert date formats into regular expressions with groupings.
				// use the regexp to determine the input format and extract the date fields.
				parseInfo = getParseRegExp(cal, format),
				match = new RegExp(parseInfo.regExp).exec(value);
			if (match === null) {
				return null;
			}
			// found a date format that matches the input.
			var groups = parseInfo.groups,
				era = null, year = null, month = null, date = null, weekDay = null,
				hour = 0, hourOffset, min = 0, sec = 0, msec = 0, tzMinOffset = null,
				pmHour = false;
			// iterate the format groups to extract and set the date fields.
			for (var j = 0, jl = groups.length; j < jl; j++) {
				var matchGroup = match[j + 1];
				if (matchGroup) {
					var current = groups[j],
						clength = current.length,
						matchInt = parseInt(matchGroup, 10);
					switch (current) {
						case "dd": case "d":
							// Day of month.
							date = matchInt;
							// check that date is generally in valid range, also checking overflow below.
							if (outOfRange(date, 1, 31)) return null;
							break;
						case "MMM": case "MMMM":
							month = getMonthIndex(cal, matchGroup, clength === 3);
							if (outOfRange(month, 0, 11)) return null;
							break;
						case "M": case "MM":
							// Month.
							month = matchInt - 1;
							if (outOfRange(month, 0, 11)) return null;
							break;
						case "y": case "yy":
						case "yyyy":
							year = clength < 4 ? expandYear(cal, matchInt) : matchInt;
							if (outOfRange(year, 0, 9999)) return null;
							break;
						case "h": case "hh":
							// Hours (12-hour clock).
							hour = matchInt;
							if (hour === 12) hour = 0;
							if (outOfRange(hour, 0, 11)) return null;
							break;
						case "H": case "HH":
							// Hours (24-hour clock).
							hour = matchInt;
							if (outOfRange(hour, 0, 23)) return null;
							break;
						case "m": case "mm":
							// Minutes.
							min = matchInt;
							if (outOfRange(min, 0, 59)) return null;
							break;
						case "s": case "ss":
							// Seconds.
							sec = matchInt;
							if (outOfRange(sec, 0, 59)) return null;
							break;
						case "tt": case "t":
							// AM/PM designator.
							// see if it is standard, upper, or lower case PM. If not, ensure it is at least one of
							// the AM tokens. If not, fail the parse for this format.
							pmHour = cal.PM && (matchGroup === cal.PM[0] || matchGroup === cal.PM[1] || matchGroup === cal.PM[2]);
							if (
								!pmHour && (
									!cal.AM || (matchGroup !== cal.AM[0] && matchGroup !== cal.AM[1] && matchGroup !== cal.AM[2])
								)
							) return null;
							break;
						case "f":
							// Deciseconds.
						case "ff":
							// Centiseconds.
						case "fff":
							// Milliseconds.
							msec = matchInt * Math.pow(10, 3 - clength);
							if (outOfRange(msec, 0, 999)) return null;
							break;
						case "ddd":
							// Day of week.
						case "dddd":
							// Day of week.
							weekDay = getDayIndex(cal, matchGroup, clength === 3);
							if (outOfRange(weekDay, 0, 6)) return null;
							break;
						case "zzz":
							// Time zone offset in +/- hours:min.
							var offsets = matchGroup.split(/:/);
							if (offsets.length !== 2) return null;
							hourOffset = parseInt(offsets[0], 10);
							if (outOfRange(hourOffset, -12, 13)) return null;
							var minOffset = parseInt(offsets[1], 10);
							if (outOfRange(minOffset, 0, 59)) return null;
							tzMinOffset = (hourOffset * 60) + (startsWith(matchGroup, "-") ? -minOffset : minOffset);
							break;
						case "z": case "zz":
							// Time zone offset in +/- hours.
							hourOffset = matchInt;
							if (outOfRange(hourOffset, -12, 13)) return null;
							tzMinOffset = hourOffset * 60;
							break;
						case "g": case "gg":
							var eraName = matchGroup;
							if (!eraName || !cal.eras) return null;
							eraName = trim(eraName.toLowerCase());
							for (var i = 0, l = cal.eras.length; i < l; i++) {
								if (eraName === cal.eras[i].name.toLowerCase()) {
									era = i;
									break;
								}
							}
							// could not find an era with that name
							if (era === null) return null;
							break;
					}
				}
			}
			var result = new Date(), defaultYear, convert = cal.convert;
			defaultYear = convert ? convert.fromGregorian(result)[0] : result.getFullYear();
			if (year === null) {
				year = defaultYear;
			}
			else if (cal.eras) {
				// year must be shifted to normal gregorian year
				// but not if year was not specified, its already normal gregorian
				// per the main if clause above.
				year += cal.eras[(era || 0)].offset;
			}
			// set default day and month to 1 and January, so if unspecified, these are the defaults
			// instead of the current day/month.
			if (month === null) {
				month = 0;
			}
			if (date === null) {
				date = 1;
			}
			// now have year, month, and date, but in the culture's calendar.
			// convert to gregorian if necessary
			if (convert) {
				result = convert.toGregorian(year, month, date);
				// conversion failed, must be an invalid match
				if (result === null) return null;
			}
			else {
				// have to set year, month and date together to avoid overflow based on current date.
				result.setFullYear(year, month, date);
				// check to see if date overflowed for specified month (only checked 1-31 above).
				if (result.getDate() !== date) return null;
				// invalid day of week.
				if (weekDay !== null && result.getDay() !== weekDay) {
					return null;
				}
			}
			// if pm designator token was found make sure the hours fit the 24-hour clock.
			if (pmHour && hour < 12) {
				hour += 12;
			}
			result.setHours(hour, min, sec, msec);
			if (tzMinOffset !== null) {
				// adjust timezone to utc before applying local offset.
				var adjustedMin = result.getMinutes() - (tzMinOffset + result.getTimezoneOffset());
				// Safari limits hours and minutes to the range of -127 to 127.  We need to use setHours
				// to ensure both these fields will not exceed this range.	adjustedMin will range
				// somewhere between -1440 and 1500, so we only need to split this into hours.
				result.setHours(result.getHours() + parseInt(adjustedMin / 60, 10), adjustedMin % 60);
			}
			return result;
		};
	}());

	parseNegativePattern = function (value, nf, negativePattern) {
		var neg = nf["-"],
			pos = nf["+"],
			ret;
		switch (negativePattern) {
			case "n -":
				neg = " " + neg;
				pos = " " + pos;
				/* falls through */
			case "n-":
				if (endsWith(value, neg)) {
					ret = ["-", value.substr(0, value.length - neg.length)];
				}
				else if (endsWith(value, pos)) {
					ret = ["+", value.substr(0, value.length - pos.length)];
				}
				break;
			case "- n":
				neg += " ";
				pos += " ";
				/* falls through */
			case "-n":
				if (startsWith(value, neg)) {
					ret = ["-", value.substr(neg.length)];
				}
				else if (startsWith(value, pos)) {
					ret = ["+", value.substr(pos.length)];
				}
				break;
			case "(n)":
				if (startsWith(value, "(") && endsWith(value, ")")) {
					ret = ["-", value.substr(1, value.length - 2)];
				}
				break;
		}
		return ret || ["", value];
	};

	//
	// public instance functions
	//

	Globalize.prototype.findClosestCulture = function (cultureSelector) {
		return Globalize.findClosestCulture.call(this, cultureSelector);
	};

	Globalize.prototype.format = function (value, format, cultureSelector) {
		return Globalize.format.call(this, value, format, cultureSelector);
	};

	Globalize.prototype.localize = function (key, cultureSelector) {
		return Globalize.localize.call(this, key, cultureSelector);
	};

	Globalize.prototype.parseInt = function (value, radix, cultureSelector) {
		return Globalize.parseInt.call(this, value, radix, cultureSelector);
	};

	Globalize.prototype.parseFloat = function (value, radix, cultureSelector) {
		return Globalize.parseFloat.call(this, value, radix, cultureSelector);
	};

	Globalize.prototype.culture = function (cultureSelector) {
		return Globalize.culture.call(this, cultureSelector);
	};

	//
	// public singleton functions
	//

	Globalize.addCultureInfo = function (cultureName, baseCultureName, info) {

		var base = {},
			isNew = false;

		if (typeof cultureName !== "string") {
			// cultureName argument is optional string. If not specified, assume info is first
			// and only argument. Specified info deep-extends current culture.
			info = cultureName;
			cultureName = this.culture().name;
			base = this.cultures[cultureName];
		} else if (typeof baseCultureName !== "string") {
			// baseCultureName argument is optional string. If not specified, assume info is second
			// argument. Specified info deep-extends specified culture.
			// If specified culture does not exist, create by deep-extending default
			info = baseCultureName;
			isNew = (this.cultures[cultureName] == null);
			base = this.cultures[cultureName] || this.cultures["default"];
		} else {
			// cultureName and baseCultureName specified. Assume a new culture is being created
			// by deep-extending an specified base culture
			isNew = true;
			base = this.cultures[baseCultureName];
		}

		this.cultures[cultureName] = extend(true, {},
			base,
			info
		);
		// Make the standard calendar the current culture if it's a new culture
		if (isNew) {
			this.cultures[cultureName].calendar = this.cultures[cultureName].calendars.standard;
		}
	};

	Globalize.findClosestCulture = function (name) {
		var match;
		if (!name) {
			return this.findClosestCulture(this.cultureSelector) || this.cultures["default"];
		}
		if (typeof name === "string") {
			name = name.split(",");
		}
		if (isArray(name)) {
			var lang,
				cultures = this.cultures,
				list = name,
				i, l = list.length,
				prioritized = [];
			for (i = 0; i < l; i++) {
				name = trim(list[i]);
				var pri, parts = name.split(";");
				lang = trim(parts[0]);
				if (parts.length === 1) {
					pri = 1;
				}
				else {
					name = trim(parts[1]);
					if (name.indexOf("q=") === 0) {
						name = name.substr(2);
						pri = parseFloat(name);
						pri = isNaN(pri) ? 0 : pri;
					}
					else {
						pri = 1;
					}
				}
				prioritized.push({ lang: lang, pri: pri });
			}
			prioritized.sort(function (a, b) {
				if (a.pri < b.pri) {
					return 1;
				} else if (a.pri > b.pri) {
					return -1;
				}
				return 0;
			});
			// exact match
			for (i = 0; i < l; i++) {
				lang = prioritized[i].lang;
				match = cultures[lang];
				if (match) {
					return match;
				}
			}

			// neutral language match
			for (i = 0; i < l; i++) {
				lang = prioritized[i].lang;
				do {
					var index = lang.lastIndexOf("-");
					if (index === -1) {
						break;
					}
					// strip off the last part. e.g. en-US => en
					lang = lang.substr(0, index);
					match = cultures[lang];
					if (match) {
						return match;
					}
				}
				while (1);
			}

			// last resort: match first culture using that language
			for (i = 0; i < l; i++) {
				lang = prioritized[i].lang;
				for (var cultureKey in cultures) {
					var culture = cultures[cultureKey];
					if (culture.language == lang) {
						return culture;
					}
				}
			}
		}
		else if (typeof name === "object") {
			return name;
		}
		return match || null;
	};

	Globalize.format = function (value, format, cultureSelector) {
		var culture = this.findClosestCulture(cultureSelector);
		if (value instanceof Date) {
			value = formatDate(value, format, culture);
		}
		else if (typeof value === "number") {
			value = formatNumber(value, format, culture);
		}
		return value;
	};

	Globalize.localize = function (key, cultureSelector) {
		return this.findClosestCulture(cultureSelector).messages[key] ||
			this.cultures["default"].messages[key];
	};

	Globalize.parseDate = function (value, formats, culture) {
		culture = this.findClosestCulture(culture);

		var date, prop, patterns;
		if (formats) {
			if (typeof formats === "string") {
				formats = [formats];
			}
			if (formats.length) {
				for (var i = 0, l = formats.length; i < l; i++) {
					var format = formats[i];
					if (format) {
						date = parseExact(value, format, culture);
						if (date) {
							break;
						}
					}
				}
			}
		} else {
			patterns = culture.calendar.patterns;
			for (prop in patterns) {
				date = parseExact(value, patterns[prop], culture);
				if (date) {
					break;
				}
			}
		}

		return date || null;
	};

	Globalize.parseInt = function (value, radix, cultureSelector) {
		return truncate(Globalize.parseFloat(value, radix, cultureSelector));
	};

	Globalize.parseFloat = function (value, radix, cultureSelector) {
		// radix argument is optional
		if (typeof radix !== "number") {
			cultureSelector = radix;
			radix = 10;
		}

		var culture = this.findClosestCulture(cultureSelector);
		var ret = NaN,
			nf = culture.numberFormat;

		if (value.indexOf(culture.numberFormat.currency.symbol) > -1) {
			// remove currency symbol
			value = value.replace(culture.numberFormat.currency.symbol, "");
			// replace decimal seperator
			value = value.replace(culture.numberFormat.currency["."], culture.numberFormat["."]);
		}

		//Remove percentage character from number string before parsing
		if (value.indexOf(culture.numberFormat.percent.symbol) > -1) {
			value = value.replace(culture.numberFormat.percent.symbol, "");
		}

		// remove spaces: leading, trailing and between - and number. Used for negative currency pt-BR
		value = value.replace(/ /g, "");

		// allow infinity or hexidecimal
		if (regexInfinity.test(value)) {
			ret = parseFloat(value);
		}
		else if (!radix && regexHex.test(value)) {
			ret = parseInt(value, 16);
		}
		else {

			// determine sign and number
			var signInfo = parseNegativePattern(value, nf, nf.pattern[0]),
				sign = signInfo[0],
				num = signInfo[1];

			// #44 - try parsing as "(n)"
			if (sign === "" && nf.pattern[0] !== "(n)") {
				signInfo = parseNegativePattern(value, nf, "(n)");
				sign = signInfo[0];
				num = signInfo[1];
			}

			// try parsing as "-n"
			if (sign === "" && nf.pattern[0] !== "-n") {
				signInfo = parseNegativePattern(value, nf, "-n");
				sign = signInfo[0];
				num = signInfo[1];
			}

			sign = sign || "+";

			// determine exponent and number
			var exponent,
				intAndFraction,
				exponentPos = num.indexOf("e");
			if (exponentPos < 0) exponentPos = num.indexOf("E");
			if (exponentPos < 0) {
				intAndFraction = num;
				exponent = null;
			}
			else {
				intAndFraction = num.substr(0, exponentPos);
				exponent = num.substr(exponentPos + 1);
			}
			// determine decimal position
			var integer,
				fraction,
				decSep = nf["."],
				decimalPos = intAndFraction.indexOf(decSep);
			if (decimalPos < 0) {
				integer = intAndFraction;
				fraction = null;
			}
			else {
				integer = intAndFraction.substr(0, decimalPos);
				fraction = intAndFraction.substr(decimalPos + decSep.length);
			}
			// handle groups (e.g. 1,000,000)
			var groupSep = nf[","];
			integer = integer.split(groupSep).join("");
			var altGroupSep = groupSep.replace(/\u00A0/g, " ");
			if (groupSep !== altGroupSep) {
				integer = integer.split(altGroupSep).join("");
			}
			// build a natively parsable number string
			var p = sign + integer;
			if (fraction !== null) {
				p += "." + fraction;
			}
			if (exponent !== null) {
				// exponent itself may have a number patternd
				var expSignInfo = parseNegativePattern(exponent, nf, "-n");
				p += "e" + (expSignInfo[0] || "+") + expSignInfo[1];
			}
			if (regexParseFloat.test(p)) {
				ret = parseFloat(p);
			}
		}
		return ret;
	};

	Globalize.culture = function (cultureSelector) {
		// setter
		if (typeof cultureSelector !== "undefined") {
			this.cultureSelector = cultureSelector;
		}
		// getter
		return this.findClosestCulture(cultureSelector) || this.cultures["default"];
	};

}(this));

//#endregion End Globalize

//#region Jeditable
/*
 * Jeditable - jQuery in place edit plugin
 *
 * Copyright (c) 2006-2009 Mika Tuupola, Dylan Verheul
 *
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Project home:
 *   http://www.appelsiini.net/projects/jeditable
 *
 * Based on editable by Dylan Verheul <dylan_at_dyve.net>:
 *    http://www.dyve.net/jquery/?editable
 *
 * Modified by Roger: Added 'oncomplete' event. Search for "[MDS]" to see lines that have been modified.
 *
 */

/**
	* Version 1.7.2-dev
	*
	* ** means there is basic unit tests for this parameter.
	*
	* @name  Jeditable
	* @type  jQuery
	* @param String  target             (POST) URL or function to send edited content to **
	* @param Hash    options            additional options
	* @param String  options[method]    method to use to send edited content (POST or PUT) **
	* @param Function options[callback] Function to run after submitting edited content **
	* @param String  options[name]      POST parameter name of edited content
	* @param String  options[id]        POST parameter name of edited div id
	* @param Hash    options[submitdata] Extra parameters to send when submitting edited content.
	* @param String  options[type]      text, textarea or select (or any 3rd party input type) **
	* @param Integer options[rows]      number of rows if using textarea **
	* @param Integer options[cols]      number of columns if using textarea **
	* @param Mixed   options[height]    'auto', 'none' or height in pixels **
	* @param Mixed   options[width]     'auto', 'none' or width in pixels **
	* @param Mixed   options[widthBuffer] Number of pixels to subtract from auto-calculated width, applies only when width=auto ** [MDS] Added to allow for padding in form elements
	* @param String  options[loadurl]   URL to fetch input content before editing **
	* @param String  options[loadtype]  Request type for load url. Should be GET or POST.
	* @param String  options[loadtext]  Text to display while loading external content.
	* @param Mixed   options[loaddata]  Extra parameters to pass when fetching content before editing.
	* @param Mixed   options[data]      Or content given as paramameter. String or function.**
	* @param String  options[indicator] indicator html to show when saving
	* @param String  options[tooltip]   optional tooltip text via title attribute **
	* @param String  options[event]     jQuery event such as 'click' of 'dblclick' **
	* @param String  options[submit]    submit button value, empty means no button **
	* @param String  options[cancel]    cancel button value, empty means no button **
	* @param String  options[cssclass]  CSS class to apply to input form. 'inherit' to copy from parent. **
	* @param String  options[style]     Style to apply to input form 'inherit' to copy from parent. **
	* @param String  options[select]    true or false, when true text is highlighted ??
	* @param String  options[placeholder] Placeholder text or html to insert when element is empty. **
	* @param String  options[onblur]    'cancel', 'submit', 'ignore' or function ??
	* @param String  options[oncomplete]  [MDS]: Specifies a function to be called after the web service is returned. Can be used to process the returned data.
	* @param String  options[oneditbegin]  [MDS]: Specifies a function to be called when the user initiates an editing action, *before* the DOM is manipulated. Was called onedit in original version
	* @param String  options[oneditend]  [MDS]: Specifies a function to be called when the user initiates an editing action, *after* the DOM is manipulated
	* @param String  options[submitontab]  [MDS]: When true, the form is submitted when the user clicks tab.
	* @param String  options[submitonenter]  [MDS]: When true, the form is submitted when the user clicks enter.
	*
	* @param Function options[onsubmit] function(settings, original) { ... } called before submit
	* @param Function options[onreset]  function(settings, original) { ... } called before reset
	* @param Function options[onerror]  function(settings, original, xhr) { ... } called on error
	*
	* @param Hash    options[ajaxoptions]  jQuery Ajax options. See docs.jquery.com.
	*
	*/

(function ($) {

	$.fn.editable = function (target, options) {

		if ('disable' == target) {
			$(this).data('disabled.editable', true);
			return;
		}
		if ('enable' == target) {
			$(this).data('disabled.editable', false);
			return;
		}
		if ('destroy' == target) {
			$(this)
								.unbind($(this).data('event.editable'))
								.removeData('disabled.editable')
								.removeData('event.editable');
			return;
		}

		var settings = $.extend({}, $.fn.editable.defaults, { target: target }, options);

		/* setup some functions */
		var plugin = $.editable.types[settings.type].plugin || function () { };
		var submit = $.editable.types[settings.type].submit || function () { };
		var buttons = $.editable.types[settings.type].buttons
										|| $.editable.types['defaults'].buttons;
		var content = $.editable.types[settings.type].content
										|| $.editable.types['defaults'].content;
		var element = $.editable.types[settings.type].element
										|| $.editable.types['defaults'].element;
		var reset = $.editable.types[settings.type].reset
										|| $.editable.types['defaults'].reset;
		var callback = settings.callback || function () { };
		var oneditbegin = settings.oneditbegin || settings.onedit || function () { }; // [MDS] If caller specified onedit, use that (preserves backward compatibility)
		var oneditend = settings.oneditend || function () { };
		var onsubmit = settings.onsubmit || function () { };
		var onreset = settings.onreset || function () { };
		var onerror = settings.onerror || reset;
		var oncomplete = settings.oncomplete || function (s) { return s; }; // [MDS]

		/* Show tooltip. */
		if (settings.tooltip) {
			$(this).attr('title', settings.tooltip);
		}

		settings.autowidth = 'auto' == settings.width;
		settings.autoheight = 'auto' == settings.height;

		return this.each(function () {

			/* Save this to self because this changes when scope changes. */
			var self = this;

			/* Inlined block elements lose their width and height after first edit. */
			/* Save them for later use as workaround. */
			var savedwidth = $(self).width();
			var savedheight = $(self).height();

			/* Save so it can be later used by $.editable('destroy') */
			$(this).data('event.editable', settings.event);

			/* If element is empty add something clickable (if requested) */
			if (!$.trim($(this).html())) {
				$(this).html(settings.placeholder);
			}

			$(this).bind(settings.event, function (e) {

				/* Abort if element is disabled. */
				if (true === $(this).data('disabled.editable')) {
					return;
				}

				/* Prevent throwing an exeption if edit field is clicked again. */
				if (self.editing) {
					return;
				}

				/* Abort if oneditbegin hook returns false. */
				if (false === oneditbegin.apply(this, [settings, self, e])) { //[MDS] Passed event object as 3rd parm
					return;
				}

				/* Prevent default action and bubbling. */
				e.preventDefault();
				e.stopPropagation();

				/* Remove tooltip. */
				if (settings.tooltip) {
					$(self).removeAttr('title');
				}

				/* Figure out how wide and tall we are, saved width and height. */
				/* Workaround for http://dev.jquery.com/ticket/2190 */
				if (0 == $(self).width()) {
					settings.width = savedwidth;
					settings.height = savedheight;
				} else {
					if (settings.width != 'none') {
						settings.width = settings.autowidth ? $(self).width() - settings.widthBuffer : settings.width; //[MDS] Subtracted widthBuffer to allow for padding in form elements
					}
					if (settings.height != 'none') {
						settings.height = settings.autoheight ? $(self).outerHeight() : settings.height; //[MDS] Use outerHeight() instead of height() to capture padding
					}
				}

				/* Remove placeholder text, replace is here because of IE. */
				if ($(this).html().toLowerCase().replace(/(;|"|\/)/g, '') ==
										settings.placeholder.toLowerCase().replace(/(;|"|\/)/g, '')) {
					$(this).html('');
				}

				self.editing = true;
				self.revert = $(self).html();
				$(self).html('');

				/* Create the form object. */
				var form = $('<form />');

				/* Apply css or style or both. */
				if (settings.cssclass) {
					if ('inherit' == settings.cssclass) {
						form.attr('class', $(self).attr('class'));
					} else {
						form.attr('class', settings.cssclass);
					}
				}

				if (settings.style) {
					if ('inherit' == settings.style) {
						form.attr('style', $(self).attr('style'));
						/* IE needs the second line or display wont be inherited. */
						form.css('display', $(self).css('display'));
					} else {
						form.attr('style', settings.style);
					}
				}

				/* Add main input element to form and store it in input. */
				var input = element.apply(form, [settings, self]);

				/* Set input content via POST, GET, given data or existing value. */
				var input_content;

				if (settings.loadurl) {
					var t = setTimeout(function () {
						input.disabled = true;
						content.apply(form, [settings.loadtext, settings, self]);
					}, 100);

					var loaddata = {};
					loaddata[settings.id] = self.id;
					if ($.isFunction(settings.loaddata)) {
						$.extend(loaddata, settings.loaddata.apply(self, [self.revert, settings]));
					} else {
						$.extend(loaddata, settings.loaddata);
					}
					$.ajax({
						type: settings.loadtype,
						url: settings.loadurl,
						data: loaddata,
						async: false,
						success: function (result) {
							window.clearTimeout(t);
							input_content = result;
							input.disabled = false;
						}
					});
				} else if (settings.data) {
					input_content = settings.data;
					if ($.isFunction(settings.data)) {
						input_content = settings.data.apply(self, [self.revert, settings]);
					}
				} else {
					input_content = self.revert;
				}
				content.apply(form, [input_content, settings, self]);

				input.attr('name', settings.name);

				/* Add buttons to the form. */
				buttons.apply(form, [settings, self]);

				/* Add created form to self. */
				$(self).append(form);

				/* Attach 3rd party plugin if requested. */
				plugin.apply(form, [settings, self]);

				/* Focus to first visible form element. */
				$(':input:visible:enabled:first', form).focus();

				/* Highlight input contents when requested. */
				if (settings.select) {
					input.select();
				}

				/* discard changes if pressing esc */
				input.keydown(function (e) {
					if (e.keyCode == 27) { // escape
						e.preventDefault();
						reset.apply(form, [settings, self]);
					}
					else if (((e.keyCode == 9 && settings.submitontab) || (e.keyCode == 13 && settings.submitonenter))
						&& ($(this).val().length == 0)) { // [MDS] User clicked tab or enter; submit form
						form.submit();
					}
				});

				/* Discard, submit or nothing with changes when clicking outside. */
				/* Do nothing is usable when navigating with tab. */
				var t;
				if ('cancel' == settings.onblur) {
					input.blur(function (e) {
						/* Prevent canceling if submit was clicked. */
						t = setTimeout(function () {
							reset.apply(form, [settings, self]);
						}, 100); // [MDS] Reduce from 500 ms to 100 ms
					});
				} else if ('submit' == settings.onblur) {
					input.blur(function (e) {
						/* Prevent double submit if submit was clicked. */
						t = setTimeout(function () {
							form.submit();
						}, 200);
					});
				} else if ($.isFunction(settings.onblur)) {
					input.blur(function (e) {
						settings.onblur.apply(self, [input.val(), settings]);
					});
				} else {
					input.blur(function (e) {
						/* TODO: maybe something here */
					});
				}

				form.submit(function (e) {
					if (t) {
						clearTimeout(t);
					}

					/* Do no submit. */
					e.preventDefault();

					/* Call before submit hook. */
					/* If it returns false abort submitting. */
					if (false !== onsubmit.apply(form, [settings, self])) {
						/* Custom inputs call before submit hook. */
						/* If it returns false abort submitting. */
						if (false !== submit.apply(form, [settings, self])) {

							/* Check if given target is function */
							if ($.isFunction(settings.target)) {
								var str = settings.target.apply(self, [input.val(), settings]);
								$(self).html(str);
								self.editing = false;
								callback.apply(self, [self.innerHTML, settings]);
								/* TODO: this is not dry */
								if (!$.trim($(self).html())) {
									$(self).html(settings.placeholder);
								}
							} else {
								/* Add edited content and id of edited element to POST. */
								var submitdata = {};
								submitdata[settings.name] = input.val();
								submitdata[settings.id] = self.id;
								/* Add extra data to be POST:ed. */
								if ($.isFunction(settings.submitdata)) {
									$.extend(submitdata, settings.submitdata.apply(self, [self.revert, settings]));
								} else {
									$.extend(submitdata, settings.submitdata);
								}

								/* Quick and dirty PUT support. */
								if ('PUT' == settings.method) {
									submitdata['_method'] = 'put';
								}

								/* Show the saving indicator. */
								$(self).html(settings.indicator);

								/* Defaults for ajaxoptions. */
								var ajaxoptions = {
									type: 'POST',
									data: submitdata,
									dataType: 'html',
									url: settings.target,
									success: function (result, status) {
										result = oncomplete.apply(self, [result]); // [MDS] Added call to oncomplete event to get updated text
										if (ajaxoptions.dataType == 'html') {
											$(self).html(result);
										}
										self.editing = false;
										callback.apply(self, [result, settings]);
										if (!$.trim($(self).html())) {
											$(self).html(settings.placeholder);
										}
									},
									error: function (xhr, status, error) {
										onerror.apply(form, [settings, self, xhr]);
									}
								};

								/* Override with what is given in settings.ajaxoptions. */
								$.extend(ajaxoptions, settings.ajaxoptions);
								$.ajax(ajaxoptions);

							}
						}
					}

					/* Show tooltip again. */
					$(self).attr('title', settings.tooltip);

					return false;
				});

				oneditend.apply(this, [settings, self]);
			});

			/* Privileged methods */
			this.reset = function (form) {
				/* Prevent calling reset twice when blurring. */
				if (this.editing) {
					/* Before reset hook, if it returns false abort reseting. */
					if (false !== onreset.apply(form, [settings, self])) {
						$(self).html(self.revert);
						self.editing = false;
						if (!$.trim($(self).html())) {
							$(self).html(settings.placeholder);
						}
						/* Show tooltip again. */
						if (settings.tooltip) {
							$(self).attr('title', settings.tooltip);
						}
					}
				}
			};
		});

	};


	$.editable = {
		types: {
			defaults: {
				element: function (settings, original) {
					var input = $('<input type="hidden"></input>');
					$(this).append(input);
					return (input);
				},
				content: function (string, settings, original) {
					string = string.replace(/&amp;/g, '&'); // [MDS]: Replace encoded ampersand with regular one
					$(':input:first', this).val(string);
				},
				reset: function (settings, original) {
					original.reset(this);
				},
				buttons: function (settings, original) {
					var form = this;
					if (settings.submit) {
						/* If given html string use that. */
						if (settings.submit.match(/>$/)) {
							var submit = $(settings.submit).click(function () {
								if (submit.attr("type") != "submit") {
									form.submit();
								}
							});
							/* Otherwise use button with given string as text. */
						} else {
							var submit = $('<button type="submit" />');
							submit.html(settings.submit);
						}
						$(this).append(submit);
					}
					if (settings.cancel) {
						/* If given html string use that. */
						if (settings.cancel.match(/>$/)) {
							var cancel = $(settings.cancel);
							/* otherwise use button with given string as text */
						} else {
							var cancel = $('<button type="cancel" />');
							cancel.html(settings.cancel);
						}
						$(this).append(cancel);

						$(cancel).click(function (event) {
							if ($.isFunction($.editable.types[settings.type].reset)) {
								var reset = $.editable.types[settings.type].reset;
							} else {
								var reset = $.editable.types['defaults'].reset;
							}
							reset.apply(form, [settings, original]);
							return false;
						});
					}
				}
			},
			text: {
				element: function (settings, original) {
					var input = $('<input />');
					if (settings.width != 'none') { input.css('width', settings.width); } // [MDS] Change attr to css for better standards compliance
					if (settings.height != 'none') { input.css('height', settings.height); } // [MDS] Change attr to css for better standards compliance
					/* https://bugzilla.mozilla.org/show_bug.cgi?id=236791 */
					//input[0].setAttribute('autocomplete','off');
					input.attr('autocomplete', 'off');
					$(this).append(input);
					return (input);
				}
			},
			textarea: {
				element: function (settings, original) {
					var textarea = $('<textarea />');
					if (settings.rows) {
						textarea.attr('rows', settings.rows);
					} else if (settings.height != "none") {
						textarea.height(settings.height);
					}
					if (settings.cols) {
						textarea.attr('cols', settings.cols);
					} else if (settings.width != "none") {
						textarea.width(settings.width);
					}
					$(this).append(textarea);
					return (textarea);
				}
			},
			select: {
				element: function (settings, original) {
					var select = $('<select />');
					$(this).append(select);
					return (select);
				},
				content: function (data, settings, original) {
					/* If it is string assume it is json. */
					if (String == data.constructor) {
						eval('var json = ' + data);
					} else {
						/* Otherwise assume it is a hash already. */
						var json = data;
					}
					for (var key in json) {
						if (!json.hasOwnProperty(key)) {
							continue;
						}
						if ('selected' == key) {
							continue;
						}
						var option = $('<option />').val(key).append(json[key]);
						$('select', this).append(option);
					}
					/* Loop option again to set selected. IE needed this... */
					$('select', this).children().each(function () {
						if ($(this).val() == json['selected'] ||
														$(this).text() == $.trim(original.revert)) {
							$(this).attr('selected', 'selected');
						}
					});
					/* Submit on change if no submit button defined. */
					if (!settings.submit) {
						var form = this;
						$('select', this).change(function () {
							form.submit();
						});
					}
				}
			}
		},

		/* Add new input type */
		addInputType: function (name, input) {
			$.editable.types[name] = input;
		}
	};

	/* Publicly accessible defaults. */
	$.fn.editable.defaults = {
		name: 'value',
		id: 'id',
		type: 'text',
		width: 'auto',
		widthBuffer: 0,
		height: 'auto',
		event: 'click.editable',
		onblur: 'cancel',
		submitontab: true,
		submitonenter: true,
		loadtype: 'GET',
		loadtext: 'Loading...',
		placeholder: 'Click to edit',
		loaddata: {},
		submitdata: {},
		ajaxoptions: {}
	};

})(jQuery);

//#endregion End Jeditable

//#region cookie

/*!
 * jQuery Cookie Plugin v1.3.1
 * https://github.com/carhartl/jquery-cookie
 *
 * Copyright 2013 Klaus Hartl
 * Released under the MIT license
 */
(function (factory) {
	if (typeof define === 'function' && define.amd && define.amd.jQuery) {
		// AMD. Register as anonymous module.
		define(['jquery'], factory);
	} else {
		// Browser globals.
		factory(jQuery);
	}
}(function ($) {

	var pluses = /\+/g;

	function raw(s) {
		return s;
	}

	function decoded(s) {
		return decodeURIComponent(s.replace(pluses, ' '));
	}

	function converted(s) {
		if (s.indexOf('"') === 0) {
			// This is a quoted cookie as according to RFC2068, unescape
			s = s.slice(1, -1).replace(/\\"/g, '"').replace(/\\\\/g, '\\');
		}
		try {
			return config.json ? JSON.parse(s) : s;
		} catch (er) { }
	}

	var config = $.cookie = function (key, value, options) {

		// write
		if (value !== undefined) {
			options = $.extend({}, config.defaults, options);

			if (typeof options.expires === 'number') {
				var days = options.expires, t = options.expires = new Date();
				t.setDate(t.getDate() + days);
			}

			value = config.json ? JSON.stringify(value) : String(value);

			return (document.cookie = [
				encodeURIComponent(key), '=', config.raw ? value : encodeURIComponent(value),
				options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
				options.path ? '; path=' + options.path : '',
				options.domain ? '; domain=' + options.domain : '',
				options.secure ? '; secure' : ''
			].join(''));
		}

		// read
		var decode = config.raw ? raw : decoded;
		var cookies = document.cookie.split('; ');
		var result = key ? undefined : {};
		for (var i = 0, l = cookies.length; i < l; i++) {
			var parts = cookies[i].split('=');
			var name = decode(parts.shift());
			var cookie = decode(parts.join('='));

			if (key && key === name) {
				result = converted(cookie);
				break;
			}

			if (!key) {
				result[name] = converted(cookie);
			}
		}

		return result;
	};

	config.defaults = {};

	$.removeCookie = function (key, options) {
		if ($.cookie(key) !== undefined) {
			$.cookie(key, '', $.extend(options, { expires: -1 }));
			return true;
		}
		return false;
	};

}));

//#endregion End cookie


//#region Paging

/**
 * @license jQuery paging plugin v1.1.0 02/05/2013
 * http://www.xarg.org/2011/09/jquery-pagination-revised/
 *
 * Copyright (c) 2011, Robert Eisele (robert@xarg.org)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 **/

(function ($, window, undefined) {


	$["fn"]["paging"] = function (number, opts) {

		var self = this,
		Paging = {

			"setOptions": function (opts) {

				function parseFormat(format) {

					var gndx = 0, group = 0, num = 1, res = {
						fstack: [], // format stack
						asterisk: 0, // asterisk?
						inactive: 0, // fill empty pages with inactives up to w?
						blockwide: 5, // width of number block
						current: 3, // position of current element in number block
						rights: 0, // num of rights
						lefts: 0 // num of lefts
					}, tok, pattern = /[*<>pq\[\]().-]|[nc]+!?/g;

					var known = {
						"[": "first",
						"]": "last",
						"<": "prev",
						">": "next",
						"q": "left",
						"p": "right",
						"-": "fill",
						".": "leap"
					}, count = {};

					while ((tok = pattern["exec"](format))) {

						tok = String(tok);

						if (undefined === known[tok]) {

							if ("(" === tok) {
								group = ++gndx;
							} else if (")" === tok) {
								group = 0;
							} else if (num) {

								if ("*" === tok) {
									res.asterisk = 1;
									res.inactive = 0;
								} else {
									// number block is the only thing left here
									res.asterisk = 0;
									res.inactive = "!" === tok.charAt(tok.length - 1);
									res.blockwide = tok["length"] - res.inactive;
									if (!(res.current = 1 + tok.indexOf("c"))) {
										res.current = (1 + res.blockwide) >> 1;
									}
								}

								res.fstack[res.fstack.length] = ({
									ftype: "block",	// type
									fgroup: 0,		// group
									fpos: 0		// pos
								});
								num = 0;
							}

						} else {

							res.fstack[res.fstack.length] = ({
								ftype: known[tok], // type
								fgroup: group,      // group
								fpos: undefined === count[tok] ? count[tok] = 1 : ++count[tok] // pos
							});

							if ("q" === tok)
								++res.lefts;
							else if ("p" === tok)
								++res.rights;
						}
					}
					return res;
				}

				this.opts = $.extend(this.opts || {
					"lapping": 0,	// number of elements overlap
					"perpage": 10,	// number of elements per page
					"page": 1,	// current page
					"refresh": {
						"interval": 10,
						"url": null
					},	// refresh callback information

					"format": "",	// visual format string

					"onLock": null, // empty callback. set it if you want to lock the entire pagination

					"onFormat": function (type) {	// callback for every format element

						/** EXAMPLE **

		switch (type) {

			case 'block':

				if (!this.active)
					return '<span class="disabled">' + this.value + '</span>';
				else if (this.value != this.page)
					return '<em><a href="#' + this.value + '">' + this.value + '</a></em>';
				return '<span class="current">' + this.value + '</span>';

			case 'right':
			case 'left':

				if (!this.active) {
					return "";
				}
				return '<a href="#' + this.value + '">' + this.value + '</a>';

			case 'next':

				if (this.active) {
					return '<a href="#' + this.value + '" class="next">Next &raquo;</a>';
				}
				return '<span class="disabled">Next &raquo;</span>';

			case 'prev':

				if (this.active) {
					return '<a href="#' + this.value + '" class="prev">&laquo; Previous</a>';
				}
				return '<span class="disabled">&laquo; Previous</span>';

			case 'first':

				if (this.active) {
					return '<a href="#' + this.value + '" class="first">|&lt;</a>';
				}
				return '<span class="disabled">|&lt;</span>';

			case 'last':

				if (this.active) {
					return '<a href="#' + this.value + '" class="prev">&gt;|</a>';
				}
				return '<span class="disabled">&gt;|</span>';

			case 'fill':
				if (this.active) {
					return "...";
				}
		}
		return ""; // return nothing for missing branches

		**/
					},
					"onSelect": function (page) {	// callback for page selection

						/** EXAMPLE SLICE **

var data = this.slice;

content.slice(prev[0], prev[1]).css('display', 'none');
content.slice(data[0], data[1]).css('display', 'block');

prev = data;

**/


						/** EXAMPLE AJAX **

$.ajax({
	"url": '/data.php?start=' + this.slice[0] + '&end=' + this.slice[1] + '&page=' + page,
	"success": function(data) {
		// content replace
	}
});

 **/

						// Return code indicates if the link of the clicked format element should be followed (otherwise only the click-event is used)
						return true;
					},
					"onRefresh": function (json) {// callback for new data of refresh api

						/** EXAMPLE **
		if (json.number) {
			Paging.setNumber(json.number);
		}

		if (json.options) {
			Paging.setOptions(json.options);
		}

		Paging.setPage(); // Call with empty params to reload the paginator
		**/
					}
				}, opts || {});

				this.opts["lapping"] |= 0;
				this.opts["perpage"] |= 0;
				if (this.opts["page"] !== null) this.opts["page"] |= 0;

				// If the number of elements per page is less then 1, set it to default
				if (this.opts["perpage"] < 1) {
					this.opts["perpage"] = 10;
				}

				if (this.interval) window.clearInterval(this.interval);

				if (this.opts["refresh"]["url"]) {

					this.interval = window.setInterval(function (o) {

						$["ajax"]({
							"url": o.opts["refresh"]["url"],
							"success": function (data) {

								if (typeof (data) === "string") {

									try {
										data = $["parseJSON"](data);
									} catch (o) {
										return;
									}
								}
								o.opts["onRefresh"](data);
							}
						});

					}, 1000 * this.opts["refresh"]["interval"], this);
				}

				this.format = parseFormat(this.opts["format"]);
				return this;
			},

			"setNumber": function (number) {
				this.number = (undefined === number || number < 0) ? -1 : number;
				return this;
			},

			"setPage": function (page) {

				if (undefined === page) {

					if (page = this.opts["page"], null === page) {
						return this;
					}

				} else if (this.opts["page"] == page) {
					return this;
				}

				this.opts["page"] = (page |= 0);

				if (null !== this.opts["onLock"]) {
					this.opts["onLock"].call(null, page);
					return this;
				}

				var number = this.number;
				var opts = this.opts;

				var rStart, rStop;

				var pages, buffer;

				var groups = 1, format = this.format;

				var data, tmp, node, lapping;

				var count = format.fstack["length"], i = count;


				// If the lapping is greater than perpage, reduce it to perpage - 1 to avoid endless loops
				if (opts["perpage"] <= opts["lapping"]) {
					opts["lapping"] = opts["perpage"] - 1;
				}

				lapping = number <= opts["lapping"] ? 0 : opts["lapping"] | 0;


				// If the number is negative, the value doesn"t matter, we loop endlessly with a constant width
				if (number < 0) {

					number = -1;
					pages = -1;

					rStart = Math.max(1, page - format.current + 1 - lapping);
					rStop = rStart + format.blockwide;

				} else {

					// Calculate the number of pages
					pages = 1 + Math.ceil((number - opts["perpage"]) / (opts["perpage"] - lapping));

					// If current page is negative, start at the end and
					// Set the current page into a valid range, includes 0, which is set to 1
					page = Math.max(1, Math.min(page < 0 ? 1 + pages + page : page, pages));

					// Do we need to print all numbers?
					if (format.asterisk) {
						rStart = 1;
						rStop = 1 + pages;

						// Disable :first and :last for asterisk mode as we see all buttons
						format.current = page;
						format.blockwide = pages;

					} else {

						// If no, start at the best position and stop at max width or at num of pages
						rStart = Math.max(1, Math.min(page - format.current, pages - format.blockwide) + 1);
						rStop = format.inactive ? rStart + format.blockwide : Math.min(rStart + format.blockwide, 1 + pages);
					}
				}

				while (i--) {

					tmp = 0; // default everything is visible
					node = format.fstack[i];

					switch (node.ftype) {

						case "left":
							tmp = (node.fpos < rStart);
							break;
						case "right":
							tmp = (rStop <= pages - format.rights + node.fpos);
							break;

						case "first":
							tmp = (format.current < page);
							break;
						case "last":
							tmp = (format.blockwide < format.current + pages - page);
							break;

						case "prev":
							tmp = (1 < page);
							break;
						case "next":
							tmp = (page < pages);
							break;
					}
					groups |= tmp << node.fgroup; // group visible?
				}

				data = {
					"number": number,	// number of elements
					"lapping": lapping,	// overlapping
					"pages": pages,	// number of pages
					"perpage": opts["perpage"], // number of elements per page
					"page": page,		// current page
					"slice": [			// two element array with bounds of the current page selection
					(tmp = page * (opts["perpage"] - lapping) + lapping) - opts["perpage"], // Lower bound
					Math.min(tmp, number) // Upper bound
					]
				};

				buffer = "";

				function buffer_append(opts, data, type) {

					type = String(opts["onFormat"].call(data, type));

					if (data["value"])
						buffer += type.replace(/<a/i, '<a data-page="' + data["value"] + '"');
					else
						buffer += type
				}

				while (++i < count) {

					node = format.fstack[i];

					tmp = (groups >> node.fgroup & 1);

					switch (node.ftype) {
						case "block":
							for (; rStart < rStop; ++rStart) {

								data["value"] = rStart;
								data["pos"] = 1 + format.blockwide - rStop + rStart;

								data["active"] = rStart <= pages || number < 0;     // true if infinity series and rStart <= pages
								data["first"] = 1 === rStart;                      // check if it is the first page
								data["last"] = rStart == pages && 0 < number;     // false if infinity series or rStart != pages

								buffer_append(opts, data, node.ftype);
							}
							continue;

						case "left":
							data["value"] = node.fpos;
							data["active"] = node.fpos < rStart; // Don't take group-visibility into account!
							break;

						case "right":
							data["value"] = pages - format.rights + node.fpos;
							data["active"] = rStop <= data["value"]; // Don't take group-visibility into account!
							break;

						case "first":
							data["value"] = 1;
							data["active"] = tmp && 1 < page;
							break;

						case "prev":
							data["value"] = Math.max(1, page - 1);
							data["active"] = tmp && 1 < page;
							break;

						case "last":
							if ((data["active"] = (number < 0))) {
								data["value"] = 1 + page;
							} else {
								data["value"] = pages;
								data["active"] = tmp && page < pages;
							}
							break;

						case "next":
							if ((data["active"] = (number < 0))) {
								data["value"] = 1 + page;
							} else {
								data["value"] = Math.min(1 + page, pages);
								data["active"] = tmp && page < pages;
							}
							break;

						case "leap":
						case "fill":
							data["pos"] = node.fpos;
							data["active"] = tmp; // tmp is true by default and changes only for group behaviour
							buffer_append(opts, data, node.ftype);
							continue;
					}

					data["pos"] = node.fpos;
					data["last"] = /* void */
					data["first"] = undefined;

					buffer_append(opts, data, node.ftype);
				}

				if (self.length) {

					$("a", self["html"](buffer)).click(function (ev) {
						ev["preventDefault"]();

						var obj = this;

						do {

							if ('a' === obj["nodeName"].toLowerCase()) {
								break;
							}

						} while ((obj = obj["parentNode"]));

						Paging["setPage"]($(obj).data("page"));

						if (Paging.locate) {
							window.location = obj["href"];
						}
					});

					this.locate = opts["onSelect"].call({
						"number": number,
						"lapping": lapping,
						"pages": pages,
						"slice": data["slice"]
					}, page);
				}
				return this;
			}
		};

		return Paging
		["setNumber"](number)
		["setOptions"](opts)
		["setPage"]();
	}

}(jQuery, this));

//#endregion End Paging

//#region Splitter

/*
 * jQuery.splitter.js - two-pane splitter window plugin
 *
 * version 1.6 (2010/01/03)
 * version 1.61 (2012/05/09) -- Fixes by Roger Martin
 *  * Added check in window resize event handler to run only when the target is the window. This fixes a breaking
 *    change introduced in jQuery 1.6.
 *  * Added support for IE 9+
 * version 1.62 (2012/05/16) -- Fixes by Roger Martin
 *  * Included bottom padding of body and html elements when calculating height. This elimates vertical scroll bar and thus a need for overflow:none on the body element
 * version 1.63 (2012/08/12) -- Fixes by Roger Martin
 *  * Changed curCSS to css (curCSS was removed in jQuery 1.8)
 * version 1.64 (2013/01/08) -- Fixes by Roger Martin
 *  * sizeLeft and sizeRight was being ignored when cookie option was used
 * version 1.65 (2013/01/09) -- Fixes by Roger Martin
 *  * Fixed issue where scrollbars were still appearing in IE.
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 */

/**
 * The splitter() plugin implements a two-pane resizable splitter window.
 * The selected elements in the jQuery object are converted to a splitter;
 * each selected element should have two child elements, used for the panes
 * of the splitter. The plugin adds a third child element for the splitbar.
 *
 * For more details see: http://www.methvin.com/splitter/
 *
 *
 * @example $('#MySplitter').splitter();
 * @desc Create a vertical splitter with default settings
 *
 * @example $('#MySplitter').splitter({type: 'h', accessKey: 'M'});
 * @desc Create a horizontal splitter resizable via Alt+Shift+M
 *
 * @name splitter
 * @type jQuery
 * @param Object options Options for the splitter (not required)
 * @cat Plugins/Splitter
 * @return jQuery
 * @author Dave Methvin (dave.methvin@gmail.com)
 */
; (function ($) {

	var splitterCounter = 0;

	$.fn.splitter = function (args) {
		args = args || {};
		return this.each(function () {
			if ($(this).is(".splitter"))	// already a splitter
				return;
			var zombie;		// left-behind splitbar for outline resizes
			function setBarState(state) {
				bar.removeClass(opts.barStateClasses).addClass(state);
			}
			function startSplitMouse(evt) {
				if (evt.which != 1)
					return;		// left button only
				bar.removeClass(opts.barHoverClass);
				if (opts.outline) {
					zombie = zombie || bar.clone(false).insertAfter(A);
					bar.removeClass(opts.barDockedClass);
				}
				setBarState(opts.barActiveClass)
				// Safari selects A/B text on a move; iframes capture mouse events so hide them
				panes.css("-webkit-user-select", "none").find("iframe").addClass(opts.iframeClass);
				A._posSplit = A[0][opts.pxSplit] - evt[opts.eventPos];
				$(document)
			 .bind("mousemove" + opts.eventNamespace, doSplitMouse)
			 .bind("mouseup" + opts.eventNamespace, endSplitMouse);
			}
			function doSplitMouse(evt) {
				var pos = A._posSplit + evt[opts.eventPos],
			 range = Math.max(0, Math.min(pos, splitter._DA - bar._DA)),
			 limit = Math.max(A._min, splitter._DA - B._max,
					 Math.min(pos, A._max, splitter._DA - bar._DA - B._min));
				if (opts.outline) {
					// Let docking splitbar be dragged to the dock position, even if min width applies
					if ((opts.dockPane == A && pos < Math.max(A._min, bar._DA)) ||
					(opts.dockPane == B && pos > Math.min(pos, A._max, splitter._DA - bar._DA - B._min))) {
						bar.addClass(opts.barDockedClass).css(opts.origin, range);
					}
					else {
						bar.removeClass(opts.barDockedClass).css(opts.origin, limit);
					}
					bar._DA = bar[0][opts.pxSplit];
				} else
					resplit(pos);
				setBarState(pos == limit ? opts.barActiveClass : opts.barLimitClass);
			}
			function endSplitMouse(evt) {
				setBarState(opts.barNormalClass);
				bar.addClass(opts.barHoverClass);
				var pos = A._posSplit + evt[opts.eventPos];
				if (opts.outline) {
					zombie.remove(); zombie = null;
					resplit(pos);
				}
				panes.css("-webkit-user-select", "text").find("iframe").removeClass(opts.iframeClass);
				$(document)
			 .unbind("mousemove" + opts.eventNamespace + " mouseup" + opts.eventNamespace);
			}
			function resplit(pos) {
				bar._DA = bar[0][opts.pxSplit];		// bar size may change during dock
				// Constrain new splitbar position to fit pane size and docking limits
				if ((opts.dockPane == A && pos < Math.max(A._min, bar._DA)) ||
				(opts.dockPane == B && pos > Math.min(pos, A._max, splitter._DA - bar._DA - B._min))) {
					bar.addClass(opts.barDockedClass);
					bar._DA = bar[0][opts.pxSplit];
					pos = opts.dockPane == A ? 0 : splitter._DA - bar._DA;
					if (bar._pos == null)
						bar._pos = A[0][opts.pxSplit];
				}
				else {
					bar.removeClass(opts.barDockedClass);
					bar._DA = bar[0][opts.pxSplit];
					bar._pos = null;
					pos = Math.max(A._min, splitter._DA - B._max,
					 Math.min(pos, A._max, splitter._DA - bar._DA - B._min));
				}
				// Resize/position the two panes
				bar.css(opts.origin, pos).css(opts.fixed, splitter._DF);
				A.css(opts.origin, 0).css(opts.split, pos).css(opts.fixed, splitter._DF);
				B.css(opts.origin, pos + bar._DA)
			 .css(opts.split, splitter._DA - bar._DA - pos).css(opts.fixed, splitter._DF);
				// IE fires resize for us; all others pay cash
				if (!browser_resize_auto_fired()) {
					for (i = 0; i <= splitterCounter; i++) {
						panes.trigger("resize" + eventNamespaceBase + i);
					}
				}
			}
			function dimSum(jq, dims) {
				// Opera returns -1 for missing min/max width, turn into 0
				var sum = 0;
				for (var i = 1; i < arguments.length; i++)
					sum += Math.max(parseInt(jq.css(arguments[i]), 10) || 0, 0);
				return sum;
			}
			function browser_resize_auto_fired() {
				// Returns true when the browser natively fires the resize event attached to the panes elements
				return ($.browser.msie && (parseInt($.browser.version) < 9));
			}

			// Determine settings based on incoming opts, element classes, and defaults
			var vh = (args.splitHorizontal ? 'h' : args.splitVertical ? 'v' : args.type) || 'v';
			var eventNamespaceBase = ".splitter";
			var opts = $.extend({
				// Defaults here allow easy use with ThemeRoller
				splitterClass: "splitter mds-ui-widget mds-ui-widget-content",
				paneClass: "splitter-pane",
				barClass: "splitter-bar",
				barNormalClass: "mds-ui-state-default",			// splitbar normal
				barHoverClass: "mds-ui-state-hover",			// splitbar mouse hover
				barActiveClass: "mds-ui-state-highlight",		// splitbar being moved
				barLimitClass: "mds-ui-state-error",			// splitbar at limit
				iframeClass: "splitter-iframe-hide",		// hide iframes during split
				eventNamespace: eventNamespaceBase + (++splitterCounter),
				pxPerKey: 8,			// splitter px moved per keypress
				tabIndex: 0,			// tab order indicator
				accessKey: ''			// accessKey for splitbar
			}, {
				// user can override
				v: {					// Vertical splitters:
					keyLeft: 39, keyRight: 37, cursor: "e-resize",
					barStateClass: "splitter-bar-vertical",
					barDockedClass: "splitter-bar-vertical-docked"
				},
				h: {					// Horizontal splitters:
					keyTop: 40, keyBottom: 38, cursor: "n-resize",
					barStateClass: "splitter-bar-horizontal",
					barDockedClass: "splitter-bar-horizontal-docked"
				}
			}[vh], args, {
				// user cannot override
				v: {					// Vertical splitters:
					type: 'v', eventPos: "pageX", origin: "left",
					split: "width", pxSplit: "offsetWidth", side1: "Left", side2: "Right",
					fixed: "height", pxFixed: "offsetHeight", side3: "Top", side4: "Bottom"
				},
				h: {					// Horizontal splitters:
					type: 'h', eventPos: "pageY", origin: "top",
					split: "height", pxSplit: "offsetHeight", side1: "Top", side2: "Bottom",
					fixed: "width", pxFixed: "offsetWidth", side3: "Left", side4: "Right"
				}
			}[vh]);
			opts.barStateClasses = [opts.barNormalClass, opts.barHoverClass, opts.barActiveClass, opts.barLimitClass].join(' ');

			// Create jQuery object closures for splitter and both panes
			var splitter = $(this).css({ position: "relative" }).addClass(opts.splitterClass);
			var panes = $(">*", splitter[0]).addClass(opts.paneClass).css({
				position: "absolute", 			// positioned inside splitter container
				"z-index": "1",					// splitbar is positioned above
				"-moz-outline-style": "none"	// don't show dotted outline
			});
			var A = $(panes[0]), B = $(panes[1]);	// A = left/top, B = right/bottom
			opts.dockPane = opts.dock && (/right|bottom/.test(opts.dock) ? B : A);

			// Focuser element, provides keyboard support; title is shown by Opera accessKeys
			var focuser = $('<a href="javascript:void(0)"></a>')
		 .attr({ accessKey: opts.accessKey, tabIndex: opts.tabIndex, title: opts.splitbarClass })
		 .bind(($.browser.opera ? "click" : "focus") + opts.eventNamespace,
			 function () { this.focus(); bar.addClass(opts.barActiveClass) })
		 .bind("keydown" + opts.eventNamespace, function (e) {
			 var key = e.which || e.keyCode;
			 var dir = key == opts["key" + opts.side1] ? 1 : key == opts["key" + opts.side2] ? -1 : 0;
			 if (dir)
				 resplit(A[0][opts.pxSplit] + dir * opts.pxPerKey, false);
		 })
		 .bind("blur" + opts.eventNamespace,
			 function () { bar.removeClass(opts.barActiveClass) });

			// Splitbar element
			var bar = $('<div></div>')
		 .insertAfter(A).addClass(opts.barClass).addClass(opts.barStateClass)
		 .append(focuser).attr({ unselectable: "on" })
		 .css({
			 position: "absolute", "user-select": "none", "-webkit-user-select": "none",
			 "-khtml-user-select": "none", "-moz-user-select": "none", "z-index": "100"
		 })
		 .bind("mousedown" + opts.eventNamespace, startSplitMouse)
		 .bind("mouseover" + opts.eventNamespace, function () {
			 $(this).addClass(opts.barHoverClass);
		 })
		 .bind("mouseout" + opts.eventNamespace, function () {
			 $(this).removeClass(opts.barHoverClass);
		 });
			// Use our cursor unless the style specifies a non-default cursor
			if (/^(auto|default|)$/.test(bar.css("cursor")))
				bar.css("cursor", opts.cursor);

			// Cache several dimensions for speed, rather than re-querying constantly
			// These are saved on the A/B/bar/splitter jQuery vars, which are themselves cached
			// DA=dimension adjustable direction, PBF=padding/border fixed, PBA=padding/border adjustable
			bar._DA = bar[0][opts.pxSplit];
			splitter._PBF = dimSum(splitter, "border" + opts.side3 + "Width", "border" + opts.side4 + "Width");
			splitter._PBA = dimSum(splitter, "border" + opts.side1 + "Width", "border" + opts.side2 + "Width");
			A._pane = opts.side1;
			B._pane = opts.side2;
			$.each([A, B], function () {
				this._splitter_style = this.style;
				this._min = opts["min" + this._pane] || dimSum(this, "min-" + opts.split);
				this._max = opts["max" + this._pane] || dimSum(this, "max-" + opts.split) || 9999;
				this._init = opts["size" + this._pane] === true ?
			 parseInt($.css(this[0], opts.split), 10) : opts["size" + this._pane]; //[RDM] Changed curCSS to css (curCSS was removed in jQuery 1.8)
			});

			// Determine initial position, get from cookie if specified
			var initPos = A._init;
			if (!isNaN(B._init))	// recalc initial B size as an offset from the top or left side
				initPos = splitter[0][opts.pxSplit] - splitter._PBA - B._init - bar._DA;
			if (opts.cookie) {
				if (!$.cookie)
					alert('jQuery.splitter(): jQuery cookie plugin required');
				var cookieVal = parseInt($.cookie(opts.cookie), 10);
				if (!isNaN(cookieVal))
					initPos = cookieVal; //[RDM] Overwrite initPos only when we found a cookie (instead of always)
				$(window).bind("unload" + opts.eventNamespace, function () {
					var state = String(bar.css(opts.origin));	// current location of splitbar
					$.cookie(opts.cookie, state, {
						expires: opts.cookieExpires || 365,
						path: opts.cookiePath || document.location.pathname
					});
				});
			}
			if (isNaN(initPos))	// King Solomon's algorithm
				initPos = Math.round((splitter[0][opts.pxSplit] - splitter._PBA - bar._DA) / 2);

			// Resize event propagation and splitter sizing
			if (opts.anchorToWindow)
				opts.resizeTo = window;
			if (opts.resizeTo) {
				splitter._hadjust = dimSum(splitter, "borderTopWidth", "borderBottomWidth", "marginBottom", "paddingBottom");
				splitter._hadjust += dimSum($('body'), 'paddingBottom'); // Added by Roger
				splitter._hadjust += dimSum($('html'), 'paddingBottom'); // Added by Roger
				splitter._hadjust += 1; // [RDM] Need a fudge factor of one extra pixel to prevent scrollbars in IE & Chrome
				splitter._hmin = Math.max(dimSum(splitter, "minHeight"), 20);
				$(window).bind("resize" + opts.eventNamespace, function (e) {
					if (e.target == window) {
						var top = splitter.offset().top;
						var eh = $(opts.resizeTo).height();
						splitter.css("height", Math.max(eh - top - splitter._hadjust - 0, splitter._hmin) + "px");
						if (!browser_resize_auto_fired()) splitter.trigger("resize" + opts.eventNamespace);
					}
				}).trigger("resize" + opts.eventNamespace);
			}
			else if (opts.resizeToWidth && !browser_resize_auto_fired()) {
				$(window).bind("resize" + opts.eventNamespace, function (e) {
					if (e.target == window) {
						splitter.trigger("resize" + opts.eventNamespace);
					}
				});
			}

			// Docking support
			if (opts.dock) {
				splitter
			 .bind("toggleDock" + opts.eventNamespace, function () {
				 var pw = opts.dockPane[0][opts.pxSplit];
				 splitter.trigger(pw ? "dock" + opts.eventNamespace : "undock" + opts.eventNamespace);
			 })
			 .bind("dock" + opts.eventNamespace, function () {
				 var pw = A[0][opts.pxSplit];
				 if (!pw) return;
				 bar._pos = pw;
				 var x = {};
				 x[opts.origin] = opts.dockPane == A ? 0 :
					 splitter[0][opts.pxSplit] - splitter._PBA - bar[0][opts.pxSplit];
				 bar.animate(x, opts.dockSpeed || 1, opts.dockEasing, function () {
					 bar.addClass(opts.barDockedClass);
					 resplit(x[opts.origin]);
				 });
			 })
			 .bind("undock" + opts.eventNamespace, function () {
				 var pw = opts.dockPane[0][opts.pxSplit];
				 if (pw) return;
				 var x = {}; x[opts.origin] = bar._pos + "px";
				 bar.removeClass(opts.barDockedClass)
					 .animate(x, opts.undockSpeed || opts.dockSpeed || 1, opts.undockEasing || opts.dockEasing, function () {
						 resplit(bar._pos);
						 bar._pos = null;
					 });
			 });
				if (opts.dockKey)
					$('<a title="' + opts.splitbarClass + ' toggle dock" href="javascript:void(0)"></a>')
				 .attr({ accessKey: opts.dockKey, tabIndex: -1 }).appendTo(bar)
				 .bind($.browser.opera ? "click" : "focus", function () {
					 splitter.trigger("toggleDock" + opts.eventNamespace); this.blur();
				 });
				bar.bind("dblclick", function () { splitter.trigger("toggleDock" + opts.eventNamespace); });
			}


			// Resize event handler; triggered immediately to set initial position
			splitter
		 .bind("destroy" + opts.eventNamespace, function () {
			 $([window, document]).unbind(opts.eventNamespace);
			 bar.unbind().remove();
			 panes.removeClass(opts.paneClass);
			 splitter
				 .removeClass(opts.splitterClass)
				 .add(panes)
					 .unbind(opts.eventNamespace)
					 .attr("style", function (el) {
						 return this._splitter_style || "";	//TODO: save style
					 });
			 splitter = bar = focuser = panes = A = B = opts = args = null;
		 })
		 .bind("resize" + opts.eventNamespace, function (e, size) {
			 // Custom events bubble in jQuery 1.3; avoid recursion
			 if (e.target != this) return;
			 // Determine new width/height of splitter container
			 splitter._DF = splitter[0][opts.pxFixed] - splitter._PBF;
			 splitter._DA = splitter[0][opts.pxSplit] - splitter._PBA;
			 // Bail if splitter isn't visible or content isn't there yet
			 if (splitter._DF <= 0 || splitter._DA <= 0) return;
			 // Re-divvy the adjustable dimension; maintain size of the preferred pane
			 resplit(!isNaN(size) ? size : (!(opts.sizeRight || opts.sizeBottom) ? A[0][opts.pxSplit] :
				 splitter._DA - B[0][opts.pxSplit] - bar._DA));
			 setBarState(opts.barNormalClass);
		 })
		 .trigger("resize" + opts.eventNamespace, [initPos]);
		});
	};

})(jQuery);

//#endregion End Splitter

//#region autoSuggest

/*
* AutoSuggest
* Copyright 2009-2010 Drew Wilson
* www.drewwilson.com
* http://code.drewwilson.com/entry/autosuggest-jquery-plugin
*
* Version 1.4   -   Updated: Mar. 23, 2010
*
* This Plug-In will auto-complete or auto-suggest completed search queries
* for you as you type. You can add multiple selections and remove them on
* the fly. It supports keybord navigation (UP + DOWN + RETURN), as well
* as multiple AutoSuggest fields on the same page.
*
* Inspied by the Autocomplete plugin by: Jšrn Zaefferer
* and the Facelist plugin by: Ian Tearle (iantearle.com)
*
* This AutoSuggest jQuery plug-in is dual licensed under the MIT and GPL licenses:
*   http://www.opensource.org/licenses/mit-license.php
*   http://www.gnu.org/licenses/gpl.html
*/

(function ($) {
	$.fn.autoSuggest = function (data, options) {
		var defaults = {
			asHtmlID: false,
			startText: "Enter Name Here",
			emptyText: "No Results Found",
			preFill: {},
			limitText: "No More Selections Are Allowed",
			selectedItemProp: "value", //name of object property
			selectedValuesProp: "value", //name of object property
			searchObjProps: "value", //comma separated list of object property names
			queryParam: "q",
			retrieveLimit: false, //number for 'limit' param on ajax request
			extraParams: "",
			matchCase: false,
			minChars: 1,
			keyDelay: 400,
			resultsHighlight: true,
			neverSubmit: false,
			selectionLimit: false,
			showResultList: true,
			start: function () { },
			selectionClick: function (elem) { },
			selectionAdded: function (elem) { },
			selectionRemoved: function (elem) { elem.remove(); },
			formatList: false, //callback function
			beforeRetrieve: function (string) { return string; },
			retrieveComplete: function (data) { return data; },
			resultClick: function (data) { },
			resultsComplete: function () { }
		};
		var opts = $.extend(defaults, options);

		var d_type = "object";
		var d_count = 0;
		if (typeof data == "string") {
			d_type = "string";
			var req_string = data;
		} else {
			var org_data = data;
			for (k in data) if (data.hasOwnProperty(k)) d_count++;
		}
		if ((d_type == "object" && d_count > 0) || d_type == "string") {
			return this.each(function (x) {
				if (!opts.asHtmlID) {
					x = x + "" + Math.floor(Math.random() * 100); //this ensures there will be unique IDs on the page if autoSuggest() is called multiple times
					var x_id = "as-input-" + x;
				} else {
					x = opts.asHtmlID;
					var x_id = x;
				}
				opts.start.call(this);
				var input = $(this);
				input.attr("autocomplete", "off").addClass("as-input").attr("id", x_id).val(opts.startText);
				var input_focus = false;

				// Setup basic elements and render them to the DOM
				input.wrap('<ul class="as-selections" id="as-selections-' + x + '"></ul>').wrap('<li class="as-original" id="as-original-' + x + '"></li>');
				var selections_holder = $("#as-selections-" + x);
				var org_li = $("#as-original-" + x);
				var results_holder = $('<div class="as-results" id="as-results-' + x + '"></div>').hide();
				var results_ul = $('<ul class="as-list"></ul>');
				var values_input = $('<input type="hidden" class="as-values" name="as_values_' + x + '" id="as-values-' + x + '" />');
				var prefill_value = "";
				if (typeof opts.preFill == "string") {
					var vals = opts.preFill.split(",");
					for (var i = 0; i < vals.length; i++) {
						var v_data = {};
						v_data[opts.selectedValuesProp] = vals[i];
						if (vals[i] != "") {
							add_selected_item(v_data, "000" + i);
						}
					}
					prefill_value = opts.preFill;
				} else {
					prefill_value = "";
					var prefill_count = 0;
					for (k in opts.preFill) if (opts.preFill.hasOwnProperty(k)) prefill_count++;
					if (prefill_count > 0) {
						for (var i = 0; i < prefill_count; i++) {
							var new_v = opts.preFill[i][opts.selectedValuesProp];
							if (new_v == undefined) { new_v = ""; }
							prefill_value = prefill_value + new_v + ",";
							if (new_v != "") {
								add_selected_item(opts.preFill[i], "000" + i);
							}
						}
					}
				}
				if (prefill_value != "") {
					input.val("");
					var lastChar = prefill_value.substring(prefill_value.length - 1);
					if (lastChar != ",") { prefill_value = prefill_value + ","; }
					values_input.val("," + prefill_value);
					$("li.as-selection-item", selections_holder).addClass("blur").removeClass("selected");
				}
				input.after(values_input);
				selections_holder.click(function () {
					input_focus = true;
					input.focus();
				}).mousedown(function () { input_focus = false; }).after(results_holder);

				var timeout = null;
				var prev = "";
				var totalSelections = 0;
				var tab_press = false;

				// Handle input field events
				input.focus(function () {
					if ($(this).val() == opts.startText && values_input.val() == "") {
						$(this).val("");
					} else if (input_focus) {
						$("li.as-selection-item", selections_holder).removeClass("blur");
						if ($(this).val() != "") {
							results_ul.css("width", selections_holder.outerWidth());
							results_holder.show();
						}
					}
					input_focus = true;
					return true;
				}).blur(function () {
					if ($(this).val() == "" && values_input.val() == "" && prefill_value == "") {
						$(this).val(opts.startText);
					} else if (input_focus) {
						$("li.as-selection-item", selections_holder).addClass("blur").removeClass("selected");
						results_holder.hide();
					}
				}).keydown(function (e) {
					// track last key pressed
					lastKeyPressCode = e.keyCode;
					first_focus = false;
					switch (e.keyCode) {
						case 38: // up
							e.preventDefault();
							moveSelection("up");
							break;
						case 40: // down
							e.preventDefault();
							moveSelection("down");
							break;
						case 8:  // delete
							if (input.val() == "") {
								var last = values_input.val().split(",");
								last = last[last.length - 2];
								selections_holder.children().not(org_li.prev()).removeClass("selected");
								if (org_li.prev().hasClass("selected")) {
									values_input.val(values_input.val().replace("," + last + ",", ","));
									opts.selectionRemoved.call(this, org_li.prev());
								} else {
									opts.selectionClick.call(this, org_li.prev());
									org_li.prev().addClass("selected");
								}
							}
							if (input.val().length == 1) {
								results_holder.hide();
								prev = "";
							}
							if ($(":visible", results_holder).length > 0) {
								if (timeout) { clearTimeout(timeout); }
								timeout = setTimeout(function () { keyChange(); }, opts.keyDelay);
							}
							break;
						case 9: case 188: case 13:  // tab or comma or enter [MDS] Added case 13 because we want enter behavior same as tab & comma
							var active = $("li.active:first", results_holder);
							if (active.length > 0) {
								// An item in the drop down is selected. Use that.
								tab_press = false;
								active.click().removeClass("active"); //[MDS] Added removeClass("active") so that subsequent 'enter' presses can submit data when used in Jeditable
								results_holder.hide();
								if (opts.neverSubmit || active.length > 0) {
									e.preventDefault();
								}
							} else {
								// If text has been entered, use that.
								tab_press = true;
								var i_input = input.val().replace(/(,)/g, "");
								if (i_input != "" && values_input.val().search("," + i_input + ",") < 0 && i_input.length >= opts.minChars) {
									e.preventDefault();
									var n_data = {};
									n_data[opts.selectedItemProp] = i_input;
									n_data[opts.selectedValuesProp] = i_input;
									var lis = $("li", selections_holder).length;
									add_selected_item(n_data, "00" + (lis + 1));
									input.val("");
								}
							}
							break;
						case 27: // [MDS] Added case for escape to clear input
							results_holder.hide();
							input.val("");
							break;
						default:
							if (opts.showResultList) {
								if (opts.selectionLimit && $("li.as-selection-item", selections_holder).length >= opts.selectionLimit) {
									results_ul.html('<li class="as-message">' + opts.limitText + '</li>');
									results_holder.show();
								} else {
									if (timeout) { clearTimeout(timeout); }
									timeout = setTimeout(function () { keyChange(); }, opts.keyDelay);
								}
							}
							break;
					}
				});

				function keyChange() {
					// ignore if the following keys are pressed: [del] [shift] [capslock]
					if (lastKeyPressCode == 46 || (lastKeyPressCode > 8 && lastKeyPressCode < 32)) { return results_holder.hide(); }
					var string = input.val().replace(/[\\]+|[\/]+/g, "");
					if (string == prev) return;
					prev = string;
					if (string.length >= opts.minChars) {
						selections_holder.addClass("loading");
						if (d_type == "string") {
							var limit = "";
							if (opts.retrieveLimit) {
								limit = "&limit=" + encodeURIComponent(opts.retrieveLimit);
							}
							if (opts.beforeRetrieve) {
								string = opts.beforeRetrieve.call(this, string);
							}
							$.getJSON(req_string + "?" + opts.queryParam + "=" + encodeURIComponent(string) + limit + opts.extraParams, function (data) {
								d_count = 0;
								var new_data = opts.retrieveComplete.call(this, data);
								for (k in new_data) if (new_data.hasOwnProperty(k)) d_count++;
								processData(new_data, string);
							});
						} else {
							if (opts.beforeRetrieve) {
								string = opts.beforeRetrieve.call(this, string);
							}
							processData(org_data, string);
						}
					} else {
						selections_holder.removeClass("loading");
						results_holder.hide();
					}
				}
				var num_count = 0;
				function processData(data, query) {
					if (!opts.matchCase) { query = query.toLowerCase(); }
					var matchCount = 0;
					results_holder.html(results_ul.html("")).hide();
					for (var i = 0; i < d_count; i++) {
						var num = i;
						num_count++;
						var forward = false;
						if (opts.searchObjProps == "value") {
							var str = data[num].value;
						} else {
							var str = "";
							var names = opts.searchObjProps.split(",");
							for (var y = 0; y < names.length; y++) {
								var name = $.trim(names[y]);
								str = str + data[num][name] + " ";
							}
						}
						if (str) {
							if (!opts.matchCase) { str = str.toLowerCase(); }
							if (str.search(query) != -1 && values_input.val().search("," + data[num][opts.selectedValuesProp] + ",") == -1) {
								forward = true;
							}
						}
						if (forward) {
							var formatted = $('<li class="as-result-item" id="as-result-item-' + num + '"></li>').click(function () {
								var raw_data = $(this).data("data");
								var number = raw_data.num;
								if ($("#as-selection-" + number, selections_holder).length <= 0 && !tab_press) {
									var data = raw_data.attributes;
									input.val("").focus();
									prev = "";
									add_selected_item(data, number);
									opts.resultClick.call(this, raw_data);
									results_holder.hide();
								}
								tab_press = false;
							}).mousedown(function () { input_focus = false; }).mouseover(function () {
								$("li", results_ul).removeClass("active");
								$(this).addClass("active");
							}).data("data", { attributes: data[num], num: num_count });
							var this_data = $.extend({}, data[num]);
							if (!opts.matchCase) {
								var regx = new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + query + ")(?![^<>]*>)(?![^&;]+;)", "gi");
							} else {
								var regx = new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + query + ")(?![^<>]*>)(?![^&;]+;)", "g");
							}

							if (opts.resultsHighlight) {
								this_data[opts.selectedItemProp] = this_data[opts.selectedItemProp].replace(regx, "<em>$1</em>");
							}
							if (!opts.formatList) {
								formatted = formatted.html(this_data[opts.selectedItemProp]);
							} else {
								formatted = opts.formatList.call(this, this_data, formatted);
							}
							results_ul.append(formatted);
							delete this_data;
							matchCount++;
							if (opts.retrieveLimit && opts.retrieveLimit == matchCount) { break; }
						}
					}
					selections_holder.removeClass("loading");
					if (matchCount <= 0) {
						results_ul.html('<li class="as-message">' + opts.emptyText + '</li>');
					}
					results_ul.css("width", selections_holder.outerWidth());
					results_holder.show();
					opts.resultsComplete.call(this);
				}

				function add_selected_item(data, num) {
					values_input.val(values_input.val() + data[opts.selectedValuesProp] + ",");
					var item = $('<li class="as-selection-item" id="as-selection-' + num + '"></li>').click(function () {
						opts.selectionClick.call(this, $(this));
						selections_holder.children().removeClass("selected");
						$(this).addClass("selected");
					}).mousedown(function () { input_focus = false; });
					var close = $('<a class="as-close">&times;</a>').click(function () {
						values_input.val(values_input.val().replace("," + data[opts.selectedValuesProp] + ",", ","));
						opts.selectionRemoved.call(this, item);
						input_focus = true;
						input.focus();
						return false;
					});
					org_li.before(item.html(data[opts.selectedItemProp]).prepend(close));
					opts.selectionAdded.call(this, org_li.prev());
				}

				function moveSelection(direction) {
					if ($(":visible", results_holder).length > 0) {
						var lis = $("li", results_holder);
						if (direction == "down") {
							var start = lis.eq(0);
						} else {
							var start = lis.filter(":last");
						}
						var active = $("li.active:first", results_holder);
						if (active.length > 0) {
							if (direction == "down") {
								start = active.next();
							} else {
								start = active.prev();
							}
						}
						lis.removeClass("active");
						start.addClass("active");
					}
				}

			});
		}
	}
})(jQuery);

//#endregion End autoSuggest

//#region menubar 2013-03-11 https://github.com/rdogmartin/jquery-ui/blob/menubar/ui/jquery.ui.menubar.js
// This is a branch from https://github.com/jquery/jquery-ui/blob/menubar/ui/jquery.ui.menubar.js with these changes:

// * Replaced show() with slideDown(200) in _open function
// * Added open delay to prevent inadvertently opening menu when mouse is quickly passing over menu button

/*
 * jQuery UI Menubar @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Menubar
 *
 * Depends:
 *	jquery.ui.core.js
 *	jquery.ui.widget.js
 *	jquery.ui.position.js
 *	jquery.ui.menu.js
 */
(function ($) {

	// TODO when mixing clicking menus and keyboard navigation, focus handling is broken
	// there has to be just one item that has tabindex
	$.widget("ui.menubar", {
		version: "@VERSION",
		options: {
			autoExpand: false,
			buttons: false,
			items: "li",
			menuElement: "ul",
			menuIcon: false,
			position: {
				my: "left top",
				at: "left bottom"
			}
		},
		_create: function () {
			var that = this;
			this.menuItems = this.element.children(this.options.items);
			this.items = this.menuItems.children("button, a");

			this.menuItems
				.addClass("ui-menubar-item")
				.attr("role", "presentation");
			// let only the first item receive focus
			this.items.slice(1).attr("tabIndex", -1);

			this.element
				.addClass("ui-menubar ui-widget-header ui-helper-clearfix")
				.attr("role", "menubar");
			this._focusable(this.items);
			this._hoverable(this.items);
			this.items.siblings(this.options.menuElement)
				.menu({
					position: {
						within: this.options.position.within
					},
					select: function (event, ui) {
						ui.item.parents("ul.ui-menu:last").hide();
						that._close();
						// TODO what is this targetting? there's probably a better way to access it
						$(event.target).prev().focus();
						that._trigger("select", event, ui);
					},
					menus: that.options.menuElement
				})
				.hide()
				.attr({
					"aria-hidden": "true",
					"aria-expanded": "false"
				})
				// TODO use _on
				.bind("keydown.menubar", function (event) {
					var menu = $(this);
					if (menu.is(":hidden")) {
						return;
					}
					switch (event.keyCode) {
						case $.ui.keyCode.LEFT:
							that.previous(event);
							event.preventDefault();
							break;
						case $.ui.keyCode.RIGHT:
							that.next(event);
							event.preventDefault();
							break;
					}
				});
			this.items.each(function () {
				var input = $(this),
					// TODO menu var is only used on two places, doesn't quite justify the .each
					menu = input.next(that.options.menuElement);

				// might be a non-menu button
				if (menu.length) {
					// TODO use _on
					input.bind("click.menubar focus.menubar mouseenter.menubar", function (event) {
						// ignore triggered focus event
						if (event.type === "focus" && !event.originalEvent) {
							return;
						}
						event.preventDefault();
						// TODO can we simplify or extractthis check? especially the last two expressions
						// there's a similar active[0] == menu[0] check in _open
						if (event.type === "click" && menu.is(":visible") && that.active && that.active[0] === menu[0]) {
							that._close();
							return;
						}
						if ((that.open && event.type === "mouseenter") || event.type === "click" || that.options.autoExpand) {
							if (that.options.autoExpand) {
								clearTimeout(that.closeTimer);
							}

							if (that.options.autoExpand) {
								// Expand after a slight delay, which we'll cancel if the mouse leaves the element
								// before the delay is up. This prevents inadvertently opening the menu when the mouse
								// is just passing through the area.
								that.openTimer = window.setTimeout(function () {
									that._open(event, menu);
								}, 200);
							} else {
								that._open(event, menu);
							}
						}
					})
					// TODO use _on
					.bind("keydown", function (event) {
						switch (event.keyCode) {
							case $.ui.keyCode.SPACE:
							case $.ui.keyCode.UP:
							case $.ui.keyCode.DOWN:
								that._open(event, $(this).next());
								event.preventDefault();
								break;
							case $.ui.keyCode.LEFT:
								that.previous(event);
								event.preventDefault();
								break;
							case $.ui.keyCode.RIGHT:
								that.next(event);
								event.preventDefault();
								break;
						}
					})
					.attr("aria-haspopup", "true");

					// TODO review if these options (menuIcon and buttons) are a good choice, maybe they can be merged
					if (that.options.menuIcon) {
						input.addClass("ui-state-default").append("<span class='ui-button-icon-secondary ui-icon ui-icon-triangle-1-s'></span>");
						input.removeClass("ui-button-text-only").addClass("ui-button-text-icon-secondary");
					}
				} else {
					// TODO use _on
					input.bind("click.menubar mouseenter.menubar", function (event) {
						if ((that.open && event.type === "mouseenter") || event.type === "click") {
							that._close();
						}
					});
				}

				input
					.addClass("ui-button ui-widget ui-button-text-only ui-menubar-link")
					.attr("role", "menuitem")
					.wrapInner("<span class='ui-button-text'></span>");

				if (that.options.buttons) {
					input.removeClass("ui-menubar-link").addClass("ui-state-default");
				}
			});
			that._on({
				keydown: function (event) {
					if (event.keyCode === $.ui.keyCode.ESCAPE && that.active && that.active.menu("collapse", event) !== true) {
						var active = that.active;
						that.active.blur();
						that._close(event);
						active.prev().focus();
					}
				},
				focusin: function (event) {
					clearTimeout(that.closeTimer);
				},
				focusout: function (event) {
					clearTimeout(that.openTimer);
					that.closeTimer = setTimeout(function () {
						that._close(event);
					}, 150);
				},
				"mouseleave .ui-menubar-item": function (event) {
					if (that.options.autoExpand) {
						clearTimeout(that.openTimer);
						that.closeTimer = setTimeout(function () {
							that._close(event);
						}, 150);
					}
				},
				"mouseenter .ui-menubar-item": function (event) {
					clearTimeout(that.closeTimer);
				}
			});

			// Keep track of open submenus
			this.openSubmenus = 0;
		},

		_destroy: function () {
			this.menuItems
				.removeClass("ui-menubar-item")
				.removeAttr("role");

			this.element
				.removeClass("ui-menubar ui-widget-header ui-helper-clearfix")
				.removeAttr("role")
				.unbind(".menubar");

			this.items
				.unbind(".menubar")
				.removeClass("ui-button ui-widget ui-button-text-only ui-menubar-link ui-state-default")
				.removeAttr("role")
				.removeAttr("aria-haspopup")
				// TODO unwrap?
				.children("span.ui-button-text").each(function (i, e) {
					var item = $(this);
					item.parent().html(item.html());
				})
				.end()
				.children(".ui-icon").remove();

			this.element.find(":ui-menu")
				.menu("destroy")
				.show()
				.removeAttr("aria-hidden")
				.removeAttr("aria-expanded")
				.removeAttr("tabindex")
				.unbind(".menubar");
		},

		_close: function () {
			if (!this.active || !this.active.length) {
				return;
			}
			this.active
				.menu("collapseAll")
				.hide()
				.attr({
					"aria-hidden": "true",
					"aria-expanded": "false"
				});
			this.active
				.prev()
				.removeClass("ui-state-active")
				.removeAttr("tabIndex");
			this.active = null;
			this.open = false;
			this.openSubmenus = 0;
		},

		_open: function (event, menu) {
			// on a single-button menubar, ignore reopening the same menu
			if (this.active && this.active[0] === menu[0]) {
				return;
			}
			// TODO refactor, almost the same as _close above, but don't remove tabIndex
			if (this.active) {
				this.active
					.menu("collapseAll")
					.hide()
					.attr({
						"aria-hidden": "true",
						"aria-expanded": "false"
					});
				this.active
					.prev()
					.removeClass("ui-state-active");
			}
			// set tabIndex -1 to have the button skipped on shift-tab when menu is open (it gets focus)
			var button = menu.prev().addClass("ui-state-active").attr("tabIndex", -1);
			this.active = menu
				.slideDown(200) // Replace show() with slideDown()
				.position($.extend({
					of: button
				}, this.options.position))
				.removeAttr("aria-hidden")
				.attr("aria-expanded", "true")
				.menu("focus", event, menu.children(".ui-menu-item").first())
				// TODO need a comment here why both events are triggered
				.focus()
				.focusin();
			this.open = true;
		},

		next: function (event) {
			if (this.open && this.active.data("menu").active.has(".ui-menu").length) {
				// Track number of open submenus and prevent moving to next menubar item
				this.openSubmenus++;
				return;
			}
			this.openSubmenus = 0;
			this._move("next", "first", event);
		},

		previous: function (event) {
			if (this.open && this.openSubmenus) {
				// Track number of open submenus and prevent moving to previous menubar item
				this.openSubmenus--;
				return;
			}
			this.openSubmenus = 0;
			this._move("prev", "last", event);
		},

		_move: function (direction, filter, event) {
			var next,
				wrapItem;
			if (this.open) {
				next = this.active.closest(".ui-menubar-item")[direction + "All"](this.options.items).first().children(".ui-menu").eq(0);
				wrapItem = this.menuItems[filter]().children(".ui-menu").eq(0);
			} else {
				if (event) {
					next = $(event.target).closest(".ui-menubar-item")[direction + "All"](this.options.items).children(".ui-menubar-link").eq(0);
					wrapItem = this.menuItems[filter]().children(".ui-menubar-link").eq(0);
				} else {
					next = wrapItem = this.menuItems.children("a").eq(0);
				}
			}

			if (next.length) {
				if (this.open) {
					this._open(event, next);
				} else {
					next.removeAttr("tabIndex")[0].focus();
				}
			} else {
				if (this.open) {
					this._open(event, wrapItem);
				} else {
					wrapItem.removeAttr("tabIndex")[0].focus();
				}
			}
		}
	});

}(jQuery));

//#endregion End menubar

//#region RateIt
/*
		RateIt
		version 1.0.9
		10/31/2012
		http://rateit.codeplex.com
		Twitter: @gjunge

*/
(function ($) {
	$.fn.rateit = function (p1, p2) {
		//quick way out.
		var options = {}; var mode = 'init';
		var capitaliseFirstLetter = function (string) {
			return string.charAt(0).toUpperCase() + string.substr(1);
		};

		if (this.length == 0) return this;


		var tp1 = $.type(p1);
		if (tp1 == 'object' || p1 === undefined || p1 == null) {
			options = $.extend({}, $.fn.rateit.defaults, p1); //wants to init new rateit plugin(s).
		}
		else if (tp1 == 'string' && p2 === undefined) {
			return this.data('rateit' + capitaliseFirstLetter(p1)); //wants to get a value.
		}
		else if (tp1 == 'string') {
			mode = 'setvalue'
		}

		return this.each(function () {
			var item = $(this);

			//shorten all the item.data('rateit-XXX'), will save space in closure compiler, will be like item.data('XXX') will become x('XXX')
			var itemdata = function (key, value) {
				arguments[0] = 'rateit' + capitaliseFirstLetter(key);
				return item.data.apply(item, arguments); ////Fix for WI: 523
			};

			//add the rate it class.
			if (!item.hasClass('rateit')) item.addClass('rateit');

			var ltr = item.css('direction') != 'rtl';

			// set value mode
			if (mode == 'setvalue') {
				if (!itemdata('init')) throw 'Can\'t set value before init';


				//if readonly now and it wasn't readonly, remove the eventhandlers.
				if (p1 == 'readonly' && !itemdata('readonly')) {
					item.find('.rateit-range').unbind();
					itemdata('wired', false);
				}
				if (p1 == 'value' && p2 == null) p2 = itemdata('min'); //when we receive a null value, reset the score to its min value.

				if (itemdata('backingfld')) {
					//if we have a backing field, check which fields we should update. 
					//In case of input[type=range], although we did read its attributes even in browsers that don't support it (using fld.attr())
					//we only update it in browser that support it (&& fld[0].min only works in supporting browsers), not only does it save us from checking if it is range input type, it also is unnecessary.
					var fld = $(itemdata('backingfld'));
					if (p1 == 'value') fld.val(p2);
					if (p1 == 'min' && fld[0].min) fld[0].min = p2;
					if (p1 == 'max' && fld[0].max) fld[0].max = p2;
					if (p1 == 'step' && fld[0].step) fld[0].step = p2;
				}

				itemdata(p1, p2);
			}

			//init rateit plugin
			if (!itemdata('init')) {

				//get our values, either from the data-* html5 attribute or from the options.
				itemdata('min', itemdata('min') || options.min);
				itemdata('max', itemdata('max') || options.max);
				itemdata('step', itemdata('step') || options.step);
				itemdata('readonly', itemdata('readonly') !== undefined ? itemdata('readonly') : options.readonly);
				itemdata('resetable', itemdata('resetable') !== undefined ? itemdata('resetable') : options.resetable);
				itemdata('backingfld', itemdata('backingfld') || options.backingfld);
				itemdata('starwidth', itemdata('starwidth') || options.starwidth);
				itemdata('starheight', itemdata('starheight') || options.starheight);
				itemdata('value', itemdata('value') || options.value || options.min);
				itemdata('ispreset', itemdata('ispreset') !== undefined ? itemdata('ispreset') : options.ispreset);
				//are we LTR or RTL?

				if (itemdata('backingfld')) {
					//if we have a backing field, hide it, and get its value, and override defaults if range.
					var fld = $(itemdata('backingfld'));
					itemdata('value', fld.hide().val());

					if (fld.attr('disabled') || fld.attr('readonly'))
						itemdata('readonly', true); //http://rateit.codeplex.com/discussions/362055 , if a backing field is disabled or readonly at instantiation, make rateit readonly.


					if (fld[0].nodeName == 'INPUT') {
						if (fld[0].type == 'range' || fld[0].type == 'text') { //in browsers not support the range type, it defaults to text

							itemdata('min', parseInt(fld.attr('min')) || itemdata('min')); //if we would have done fld[0].min it wouldn't have worked in browsers not supporting the range type.
							itemdata('max', parseInt(fld.attr('max')) || itemdata('max'));
							itemdata('step', parseInt(fld.attr('step')) || itemdata('step'));
						}
					}
					if (fld[0].nodeName == 'SELECT' && fld[0].options.length > 1) {
						itemdata('min', Number(fld[0].options[0].value));
						itemdata('max', Number(fld[0].options[fld[0].length - 1].value));
						itemdata('step', Number(fld[0].options[1].value) - Number(fld[0].options[0].value));
					}
				}

				//Create the necessary tags.
				item.append('<div class="rateit-reset"></div><div class="rateit-range"><div class="rateit-selected" style="height:' + itemdata('starheight') + 'px"></div><div class="rateit-hover" style="height:' + itemdata('starheight') + 'px"></div></div>');

				//if we are in RTL mode, we have to change the float of the "reset button"
				if (!ltr) {
					item.find('.rateit-reset').css('float', 'right');
					item.find('.rateit-selected').addClass('rateit-selected-rtl');
					item.find('.rateit-hover').addClass('rateit-hover-rtl');
				}

				itemdata('init', true);
			}


			//set the range element to fit all the stars.
			var range = item.find('.rateit-range');
			range.width(itemdata('starwidth') * (itemdata('max') - itemdata('min'))).height(itemdata('starheight'));

			//add/remove the preset class
			var presetclass = 'rateit-preset' + ((ltr) ? '' : '-rtl');
			if (itemdata('ispreset'))
				item.find('.rateit-selected').addClass(presetclass);
			else
				item.find('.rateit-selected').removeClass(presetclass);

			//set the value if we have it.
			if (itemdata('value') != null) {
				var score = (itemdata('value') - itemdata('min')) * itemdata('starwidth');
				item.find('.rateit-selected').width(score);
			}

			var resetbtn = item.find('.rateit-reset');
			if (resetbtn.data('wired') !== true) {
				resetbtn.click(function () {
					itemdata('value', itemdata('min'));
					range.find('.rateit-hover').hide().width(0);
					range.find('.rateit-selected').width(0).show();
					if (itemdata('backingfld')) $(itemdata('backingfld')).val(itemdata('min'));
					item.trigger('reset');
				}).data('wired', true);

			}


			var calcRawScore = function (element, event) {
				var pageX = (event.changedTouches) ? event.changedTouches[0].pageX : event.pageX;

				var offsetx = pageX - $(element).offset().left;
				if (!ltr) offsetx = range.width() - offsetx;
				if (offsetx > range.width()) offsetx = range.width();
				if (offsetx < 0) offsetx = 0;

				return score = Math.ceil(offsetx / itemdata('starwidth') * (1 / itemdata('step')));
			};


			//

			if (!itemdata('readonly')) {
				//if we are not read only, add all the events

				//if we have a reset button, set the event handler.
				if (!itemdata('resetable'))
					resetbtn.hide();

				//when the mouse goes over the range div, we set the "hover" stars.
				if (!itemdata('wired')) {
					range.bind('touchmove touchend', touchHandler); //bind touch events
					range.mousemove(function (e) {
						var score = calcRawScore(this, e);
						var w = score * itemdata('starwidth') * itemdata('step');
						var h = range.find('.rateit-hover');
						if (h.data('width') != w) {
							range.find('.rateit-selected').hide();
							h.width(w).show().data('width', w);
							var data = [(score * itemdata('step')) + itemdata('min')];
							item.trigger('hover', data).trigger('over', data);
						}
					});
					//when the mouse leaves the range, we have to hide the hover stars, and show the current value.
					range.mouseleave(function (e) {
						range.find('.rateit-hover').hide().width(0).data('width', '');
						item.trigger('hover', [null]).trigger('over', [null]);
						range.find('.rateit-selected').show();
					});
					//when we click on the range, we have to set the value, hide the hover.
					range.mouseup(function (e) {
						var score = calcRawScore(this, e);

						var newvalue = (score * itemdata('step')) + itemdata('min');
						itemdata('value', newvalue);
						if (itemdata('backingfld')) {
							$(itemdata('backingfld')).val(newvalue);
						}
						if (itemdata('ispreset')) { //if it was a preset value, unset that.
							range.find('.rateit-selected').removeClass(presetclass);
							itemdata('ispreset', false);
						}
						range.find('.rateit-hover').hide();
						range.find('.rateit-selected').width(score * itemdata('starwidth') * itemdata('step')).show();
						item.trigger('hover', [null]).trigger('over', [null]).trigger('rated', [newvalue]);
					});

					itemdata('wired', true);
				}
				if (itemdata('resetable')) {
					resetbtn.show();
				}
			}
			else {
				resetbtn.hide();
			}
		});
	};

	//touch converter http://ross.posterous.com/2008/08/19/iphone-touch-events-in-javascript/
	function touchHandler(event) {

		var touches = event.originalEvent.changedTouches,
						first = touches[0],
						type = "";
		switch (event.type) {
			case "touchmove": type = "mousemove"; break;
			case "touchend": type = "mouseup"; break;
			default: return;
		}

		var simulatedEvent = document.createEvent("MouseEvent");
		simulatedEvent.initMouseEvent(type, true, true, window, 1,
													first.screenX, first.screenY,
													first.clientX, first.clientY, false,
													false, false, false, 0/*left*/, null);

		first.target.dispatchEvent(simulatedEvent);
		event.preventDefault();
	};

	//some default values.
	$.fn.rateit.defaults = { min: 0, max: 5, step: 0.5, starwidth: 16, starheight: 16, readonly: false, resetable: true, ispreset: false };

	//invoke it on all div.rateit elements. This could be removed if not wanted.
	//$(function () { $('div.rateit').rateit(); });

})(jQuery);

//#endregion End RateIt

//#region supersized

/*
	supersized.3.2.7.js
	Supersized - Fullscreen Slideshow jQuery Plugin
	Version : 3.2.7
	Site	: www.buildinternet.com/project/supersized
	
	Author	: Sam Dunn
	Company : One Mighty Roar (www.onemightyroar.com)
	License : MIT License / GPL License
	
*/

(function ($) {

	$.supersized = function (options) {

		/* Variables
	----------------------------*/
		var base = this;

		base.init = function () {
			// Combine options and vars
			$.supersized.vars = $.extend($.supersized.vars, $.supersized.themeVars);
			$.supersized.vars.options = $.extend({}, $.supersized.defaultOptions, $.supersized.themeOptions, options);
			base.options = $.supersized.vars.options;

			base._build();
		};


		/* Build Elements
----------------------------*/
		base._build = function () {
			// Add in slide markers
			var thisSlide = 0,
				slideSet = '',
		markers = '',
		markerContent,
		thumbMarkers = '',
		thumbImage;


			// Hide current page contents and add Supersized Elements
			$('body').children(':visible').hide().addClass('supersized_hidden');
			$('body').append($($.supersized.vars.options.html_template), '<div id="supersized-loader"></div><ul id="supersized"></ul>');

			var el = '#supersized';
			// Access to jQuery and DOM versions of element
			base.$el = $(el);
			base.el = el;
			vars = $.supersized.vars;
			// Add a reverse reference to the DOM object
			base.$el.data("supersized", base);
			api = base.$el.data('supersized');


			while (thisSlide <= base.options.slides.length - 1) {
				//Determine slide link content
				switch (base.options.slide_links) {
					case 'num':
						markerContent = thisSlide;
						break;
					case 'name':
						markerContent = base.options.slides[thisSlide].title;
						break;
					case 'blank':
						markerContent = '';
						break;
				}

				slideSet = slideSet + '<li class="slide-' + thisSlide + '"></li>';

				if (thisSlide == base.options.start_slide - 1) {
					// Slide links
					if (base.options.slide_links) markers = markers + '<li class="slide-link-' + thisSlide + ' current-slide"><a>' + markerContent + '</a></li>';
					// Slide Thumbnail Links
					if (base.options.thumb_links) {
						base.options.slides[thisSlide].thumb ? thumbImage = base.options.slides[thisSlide].thumb : thumbImage = base.options.slides[thisSlide].image;
						thumbMarkers = thumbMarkers + '<li class="thumb' + thisSlide + ' current-thumb"><img src="' + thumbImage + '"/></li>';
					};
				} else {
					// Slide links
					if (base.options.slide_links) markers = markers + '<li class="slide-link-' + thisSlide + '" ><a>' + markerContent + '</a></li>';
					// Slide Thumbnail Links
					if (base.options.thumb_links) {
						base.options.slides[thisSlide].thumb ? thumbImage = base.options.slides[thisSlide].thumb : thumbImage = base.options.slides[thisSlide].image;
						thumbMarkers = thumbMarkers + '<li class="thumb' + thisSlide + '"><img src="' + thumbImage + '"/></li>';
					};
				}
				thisSlide++;
			}

			if (base.options.slide_links) $(vars.slide_list).html(markers);
			if (base.options.thumb_links && vars.thumb_tray.length) {
				$(vars.thumb_tray).append('<ul id="' + vars.thumb_list.replace('#', '') + '">' + thumbMarkers + '</ul>');
			}

			$(base.el).append(slideSet);

			// Add in thumbnails
			if (base.options.thumbnail_navigation) {
				// Load previous thumbnail
				vars.current_slide - 1 < 0 ? prevThumb = base.options.slides.length - 1 : prevThumb = vars.current_slide - 1;
				$(vars.prev_thumb).show().html($("<img/>").attr("src", base.options.slides[prevThumb].image));

				// Load next thumbnail
				vars.current_slide == base.options.slides.length - 1 ? nextThumb = 0 : nextThumb = vars.current_slide + 1;
				$(vars.next_thumb).show().html($("<img/>").attr("src", base.options.slides[nextThumb].image));
			}

			base._start(); // Get things started
		};


		/* Initialize
----------------------------*/
		base._start = function () {

			// Determine if starting slide random
			if (base.options.start_slide) {
				vars.current_slide = base.options.start_slide - 1;
			} else {
				vars.current_slide = Math.floor(Math.random() * base.options.slides.length);	// Generate random slide number
			}

			// If links should open in new window
			var linkTarget = base.options.new_window ? ' target="_blank"' : '';

			// Set slideshow quality (Supported only in FF and IE, no Webkit)
			if (base.options.performance == 3) {
				base.$el.addClass('speed'); 		// Faster transitions
			} else if ((base.options.performance == 1) || (base.options.performance == 2)) {
				base.$el.addClass('quality');	// Higher image quality
			}

			// Shuffle slide order if needed		
			if (base.options.random) {
				arr = base.options.slides;
				for (var j, x, i = arr.length; i; j = parseInt(Math.random() * i), x = arr[--i], arr[i] = arr[j], arr[j] = x);	// Fisher-Yates shuffle algorithm (jsfromhell.com/array/shuffle)
				base.options.slides = arr;
			}

			/*-----Load initial set of images-----*/

			if (base.options.slides.length > 1) {
				if (base.options.slides.length > 2) {
					// Set previous image
					vars.current_slide - 1 < 0 ? loadPrev = base.options.slides.length - 1 : loadPrev = vars.current_slide - 1;	// If slide is 1, load last slide as previous
					var imageLink = (base.options.slides[loadPrev].url) ? "href='" + base.options.slides[loadPrev].url + "'" : "";

					var imgPrev = $('<img src="' + base.options.slides[loadPrev].image + '"/>');
					var slidePrev = base.el + ' li:eq(' + loadPrev + ')';
					imgPrev.appendTo(slidePrev).wrap('<a ' + imageLink + linkTarget + '></a>').parent().parent().addClass('image-loading prevslide');

					imgPrev.load(function () {
						$(this).data('origWidth', $(this).width()).data('origHeight', $(this).height());
						base.resizeNow();	// Resize background image
					});	// End Load
				}
			} else {
				// Slideshow turned off if there is only one slide
				//base.options.slideshow = 0; //[RDM] Commented out because this disables buttons when there is only one slide
			}

			// Set current image
			imageLink = (api.getField('url')) ? "href='" + api.getField('url') + "'" : "";
			var img = $('<img src="' + api.getField('image') + '"/>');

			var slideCurrent = base.el + ' li:eq(' + vars.current_slide + ')';
			img.appendTo(slideCurrent).wrap('<a ' + imageLink + linkTarget + '></a>').parent().parent().addClass('image-loading activeslide').css('visibility', 'visible');

			img.load(function () {
				base._origDim($(this));
				base.resizeNow();	// Resize background image
				base.launch();
				if (typeof theme != 'undefined' && typeof theme._init == "function") theme._init();	// Load Theme
			});

			if (base.options.slides.length > 1) {
				// Set next image
				vars.current_slide == base.options.slides.length - 1 ? loadNext = 0 : loadNext = vars.current_slide + 1;	// If slide is last, load first slide as next
				imageLink = (base.options.slides[loadNext].url) ? "href='" + base.options.slides[loadNext].url + "'" : "";

				var imgNext = $('<img src="' + base.options.slides[loadNext].image + '"/>');
				var slideNext = base.el + ' li:eq(' + loadNext + ')';
				imgNext.appendTo(slideNext).wrap('<a ' + imageLink + linkTarget + '></a>').parent().parent().addClass('image-loading');

				imgNext.load(function () {
					$(this).data('origWidth', $(this).width()).data('origHeight', $(this).height());
					base.resizeNow();	// Resize background image
				});	// End Load
			}
			/*-----End load initial images-----*/

			//  Hide elements to be faded in
			base.$el.css('visibility', 'hidden');
			$('.load-item').hide();

		};


		/* Launch Supersized
		----------------------------*/
		base.launch = function () {

			//base.$el.css('visibility', 'visible');
			$('#supersized-loader').remove();		//Hide loading animation

			// Call theme function for before slide transition
			if (typeof theme != 'undefined' && typeof theme.beforeAnimation == "function") theme.beforeAnimation('next');
			$('.load-item').show();

			// Keyboard Navigation
			if (base.options.keyboard_nav) {
				$(document.documentElement).on('keyup.supersized', function (event) {

					if (vars.in_animation) return false;		// Abort if currently animating
					if ($(document.activeElement).is("input, textarea")) return false; // Abort if active element is an input or a textarea.

					// Left Arrow or Down Arrow
					if ((event.keyCode == 37) || (event.keyCode == 40)) {
						clearInterval(vars.slideshow_interval);	// Stop slideshow, prevent buildup
						base.prevSlide();

						// Right Arrow or Up Arrow
					} else if ((event.keyCode == 39) || (event.keyCode == 38)) {
						clearInterval(vars.slideshow_interval);	// Stop slideshow, prevent buildup
						base.nextSlide();

						// Spacebar	
					} else if (event.keyCode == 32 && !vars.hover_pause) {
						clearInterval(vars.slideshow_interval);	// Stop slideshow, prevent buildup
						base.playToggle();
					}

				});
			}

			// Pause when hover on image
			if (base.options.slideshow && base.options.pause_hover) {
				$(base.el).hover(function () {
					if (vars.in_animation) return false;		// Abort if currently animating
					vars.hover_pause = true;	// Mark slideshow paused from hover
					if (!vars.is_paused) {
						vars.hover_pause = 'resume';	// It needs to resume afterwards
						base.playToggle();
					}
				}, function () {
					if (vars.hover_pause == 'resume') {
						base.playToggle();
						vars.hover_pause = false;
					}
				});
			}

			if (base.options.slide_links) {
				// Slide marker clicked
				$(vars.slide_list + '> li').click(function () {

					index = $(vars.slide_list + '> li').index(this);
					targetSlide = index + 1;

					base.goTo(targetSlide);
					return false;

				});
			}

			// Thumb marker clicked
			if (base.options.thumb_links) {
				$(vars.thumb_list + '> li').click(function () {

					index = $(vars.thumb_list + '> li').index(this);
					targetSlide = index + 1;

					api.goTo(targetSlide);
					return false;

				});
			}

			// Start slideshow if enabled
			if (base.options.slideshow && base.options.slides.length > 1) {

				// Start slideshow if autoplay enabled
				if (base.options.autoplay && base.options.slides.length > 1) {
					vars.slideshow_interval = setInterval(base.nextSlide, base.options.slide_interval);	// Initiate slide interval
				} else {
					vars.is_paused = true;	// Mark as paused
				}

				//Prevent navigation items from being dragged					
				$('.load-item img').bind("contextmenu mousedown", function () {
					return false;
				});

			}

			// Adjust image when browser is resized
			$(window).resize(function () {
				base.resizeNow();
			});

		};


		/* Resize Images
----------------------------*/
		base.resizeNow = function () {

			return base.$el.each(function () {
				//  Resize each image seperately
				$('img', base.el).each(function () {

					thisSlide = $(this);
					var ratio = (thisSlide.data('origHeight') / thisSlide.data('origWidth')).toFixed(2);	// Define image ratio

					// Gather browser size
					var browserwidth = base.$el.width(),
						browserheight = base.$el.height(),
						offset;

					/*-----Resize Image-----*/
					if (base.options.fit_always) {	// Fit always is enabled
						if ((browserheight / browserwidth) > ratio) {
							resizeWidth();
						} else {
							resizeHeight();
						}
					} else {	// Normal Resize
						if ((browserheight <= base.options.min_height) && (browserwidth <= base.options.min_width)) {	// If window smaller than minimum width and height

							if ((browserheight / browserwidth) > ratio) {
								base.options.fit_landscape && ratio < 1 ? resizeWidth(true) : resizeHeight(true);	// If landscapes are set to fit
							} else {
								base.options.fit_portrait && ratio >= 1 ? resizeHeight(true) : resizeWidth(true);		// If portraits are set to fit
							}

						} else if (browserwidth <= base.options.min_width) {		// If window only smaller than minimum width

							if ((browserheight / browserwidth) > ratio) {
								base.options.fit_landscape && ratio < 1 ? resizeWidth(true) : resizeHeight();	// If landscapes are set to fit
							} else {
								base.options.fit_portrait && ratio >= 1 ? resizeHeight() : resizeWidth(true);		// If portraits are set to fit
							}

						} else if (browserheight <= base.options.min_height) {	// If window only smaller than minimum height

							if ((browserheight / browserwidth) > ratio) {
								base.options.fit_landscape && ratio < 1 ? resizeWidth() : resizeHeight(true);	// If landscapes are set to fit
							} else {
								base.options.fit_portrait && ratio >= 1 ? resizeHeight(true) : resizeWidth();		// If portraits are set to fit
							}

						} else {	// If larger than minimums

							if ((browserheight / browserwidth) > ratio) {
								base.options.fit_landscape && ratio < 1 ? resizeWidth() : resizeHeight();	// If landscapes are set to fit
							} else {
								base.options.fit_portrait && ratio >= 1 ? resizeHeight() : resizeWidth();		// If portraits are set to fit
							}

						}
					}
					/*-----End Image Resize-----*/


					/*-----Resize Functions-----*/

					function resizeWidth(minimum) {
						if (minimum) {	// If minimum height needs to be considered
							if (thisSlide.width() < browserwidth || thisSlide.width() < base.options.min_width) {
								if (thisSlide.width() * ratio >= base.options.min_height) {
									thisSlide.width(base.options.min_width);
									thisSlide.height(thisSlide.width() * ratio);
								} else {
									resizeHeight();
								}
							}
						} else {
							if (base.options.min_height >= browserheight && !base.options.fit_landscape) {	// If minimum height needs to be considered
								if (browserwidth * ratio >= base.options.min_height || (browserwidth * ratio >= base.options.min_height && ratio <= 1)) {	// If resizing would push below minimum height or image is a landscape
									thisSlide.width(browserwidth);
									thisSlide.height(browserwidth * ratio);
								} else if (ratio > 1) {		// Else the image is portrait
									thisSlide.height(base.options.min_height);
									thisSlide.width(thisSlide.height() / ratio);
								} else if (thisSlide.width() < browserwidth) {
									thisSlide.width(browserwidth);
									thisSlide.height(thisSlide.width() * ratio);
								}
							} else {	// Otherwise, resize as normal
								thisSlide.width(browserwidth);
								thisSlide.height(browserwidth * ratio);
							}
						}
					};

					function resizeHeight(minimum) {
						if (minimum) {	// If minimum height needs to be considered
							if (thisSlide.height() < browserheight) {
								if (thisSlide.height() / ratio >= base.options.min_width) {
									thisSlide.height(base.options.min_height);
									thisSlide.width(thisSlide.height() / ratio);
								} else {
									resizeWidth(true);
								}
							}
						} else {	// Otherwise, resized as normal
							if (base.options.min_width >= browserwidth) {	// If minimum width needs to be considered
								if (browserheight / ratio >= base.options.min_width || ratio > 1) {	// If resizing would push below minimum width or image is a portrait
									thisSlide.height(browserheight);
									thisSlide.width(browserheight / ratio);
								} else if (ratio <= 1) {		// Else the image is landscape
									thisSlide.width(base.options.min_width);
									thisSlide.height(thisSlide.width() * ratio);
								}
							} else {	// Otherwise, resize as normal
								thisSlide.height(browserheight);
								thisSlide.width(browserheight / ratio);
							}
						}
					};

					/*-----End Resize Functions-----*/

					if (thisSlide.parents('li').hasClass('image-loading')) {
						$('.image-loading').removeClass('image-loading');
					}

					// Horizontally Center
					if (base.options.horizontal_center) {
						$(this).css('left', (browserwidth - $(this).width()) / 2);
					}

					// Vertically Center
					if (base.options.vertical_center) {
						$(this).css('top', (browserheight - $(this).height()) / 2);
					}

				});

				// Basic image drag and right click protection
				if (base.options.image_protect) {

					$('img', base.el).bind("contextmenu mousedown", function () {
						return false;
					});

				}

				return false;

			});

		};


		/* Next Slide
----------------------------*/
		base.nextSlide = function () {
			if (base.options.slideshow && !vars.is_paused && base.options.auto_exit && (vars.current_slide == base.options.slides.length - 1)) {
				// We're on the last slide of a running slideshow where auto_exit is enabled, so exit.
				base.destroy();
				return false;
			}

			var old_slide_number = vars.current_slide;
			// Get the slide number of new slide
			if (vars.current_slide < base.options.slides.length - 1) {
				vars.current_slide++;
			} else if (base.options.loop) {
				vars.current_slide = 0;
			}

			if (old_slide_number == vars.current_slide) {
				vars.in_animation = false;
				return false;
			}

			if (vars.in_animation || !api.options.slideshow) return false;		// Abort if currently animating
			else vars.in_animation = true;		// Otherwise set animation marker

			clearInterval(vars.slideshow_interval);	// Stop slideshow

			var slides = base.options.slides,					// Pull in slides array
			liveslide = base.$el.find('.activeslide');		// Find active slide
			$('.prevslide').removeClass('prevslide');
			liveslide.removeClass('activeslide').addClass('prevslide');	// Remove active class & update previous slide


			var nextslide = $(base.el + ' li:eq(' + vars.current_slide + ')'),
				prevslide = base.$el.find('.prevslide');

			// If hybrid mode is on drop quality for transition
			if (base.options.performance == 1) base.$el.removeClass('quality').addClass('speed');


			/*-----Load Image-----*/

			loadSlide = false;

			vars.current_slide == base.options.slides.length - 1 ? loadSlide = 0 : loadSlide = vars.current_slide + 1;	// Determine next slide

			var targetList = base.el + ' li:eq(' + loadSlide + ')';
			if (!$(targetList).html()) {

				// If links should open in new window
				var linkTarget = base.options.new_window ? ' target="_blank"' : '';

				imageLink = (base.options.slides[loadSlide].url) ? "href='" + base.options.slides[loadSlide].url + "'" : "";	// If link exists, build it
				var img = $('<img src="' + base.options.slides[loadSlide].image + '"/>');

				img.appendTo(targetList).wrap('<a ' + imageLink + linkTarget + '></a>').parent().parent().addClass('image-loading').css('visibility', 'hidden');

				img.load(function () {
					base._origDim($(this));
					base.resizeNow();
				});	// End Load
			};

			// Update thumbnails (if enabled)
			if (base.options.thumbnail_navigation == 1) {

				// Load previous thumbnail
				vars.current_slide - 1 < 0 ? prevThumb = base.options.slides.length - 1 : prevThumb = vars.current_slide - 1;
				$(vars.prev_thumb).html($("<img/>").attr("src", base.options.slides[prevThumb].image));

				// Load next thumbnail
				nextThumb = loadSlide;
				$(vars.next_thumb).html($("<img/>").attr("src", base.options.slides[nextThumb].image));

			}



			/*-----End Load Image-----*/


			// Call theme function for before slide transition
			if (typeof theme != 'undefined' && typeof theme.beforeAnimation == "function") theme.beforeAnimation('next');

			//Update slide markers
			if (base.options.slide_links) {
				$('.current-slide').removeClass('current-slide');
				$(vars.slide_list + '> li').eq(vars.current_slide).addClass('current-slide');
			}

			nextslide.css('visibility', 'hidden').addClass('activeslide');	// Update active slide

			switch (base.options.transition) {
				case 0: case 'none':	// No transition
					nextslide.css('visibility', 'visible'); vars.in_animation = false; base.afterAnimation();
					break;
				case 1: case 'fade':	// Fade
					nextslide.css({ opacity: 0, 'visibility': 'visible' }).animate({ opacity: 1, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					break;
				case 2: case 'slideTop':	// Slide Top
					nextslide.css({ top: -base.$el.height(), 'visibility': 'visible' }).animate({ top: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					break;
				case 3: case 'slideRight':	// Slide Right
					nextslide.css({ left: base.$el.width(), 'visibility': 'visible' }).animate({ left: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					break;
				case 4: case 'slideBottom': // Slide Bottom
					nextslide.css({ top: base.$el.height(), 'visibility': 'visible' }).animate({ top: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					break;
				case 5: case 'slideLeft':  // Slide Left
					nextslide.css({ left: -base.$el.width(), 'visibility': 'visible' }).animate({ left: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					break;
				case 6: case 'carouselRight':	// Carousel Right
					nextslide.css({ left: base.$el.width(), 'visibility': 'visible' }).animate({ left: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					liveslide.animate({ left: -base.$el.width(), avoidTransforms: false }, base.options.transition_speed);
					break;
				case 7: case 'carouselLeft':   // Carousel Left
					nextslide.css({ left: -base.$el.width(), 'visibility': 'visible' }).animate({ left: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					liveslide.animate({ left: base.$el.width(), avoidTransforms: false }, base.options.transition_speed);
					break;
			}
			return false;
		};


		/* Previous Slide
		----------------------------*/
		base.prevSlide = function () {

			if (vars.in_animation || !api.options.slideshow) return false;		// Abort if currently animating
			else vars.in_animation = true;		// Otherwise set animation marker

			var old_slide_number = vars.current_slide;
			// Get current slide number
			if (vars.current_slide > 0) {
				vars.current_slide--;
			} else if (base.options.loop) {
				vars.current_slide = base.options.slides.length - 1;
			}

			if (old_slide_number == vars.current_slide) {
				vars.in_animation = false;
				return false;
			}

			clearInterval(vars.slideshow_interval);	// Stop slideshow

			var slides = base.options.slides,					// Pull in slides array
				liveslide = base.$el.find('.activeslide');		// Find active slide
			$('.prevslide').removeClass('prevslide');
			liveslide.removeClass('activeslide').addClass('prevslide');		// Remove active class & update previous slide

			var nextslide = $(base.el + ' li:eq(' + vars.current_slide + ')'),
				prevslide = base.$el.find('.prevslide');

			// If hybrid mode is on drop quality for transition
			if (base.options.performance == 1) base.$el.removeClass('quality').addClass('speed');


			/*-----Load Image-----*/

			loadSlide = vars.current_slide;

			var targetList = base.el + ' li:eq(' + loadSlide + ')';
			if (!$(targetList).html()) {
				// If links should open in new window
				var linkTarget = base.options.new_window ? ' target="_blank"' : '';
				imageLink = (base.options.slides[loadSlide].url) ? "href='" + base.options.slides[loadSlide].url + "'" : "";	// If link exists, build it
				var img = $('<img src="' + base.options.slides[loadSlide].image + '"/>');

				img.appendTo(targetList).wrap('<a ' + imageLink + linkTarget + '></a>').parent().parent().addClass('image-loading').css('visibility', 'hidden');

				img.load(function () {
					base._origDim($(this));
					base.resizeNow();
				});	// End Load
			};

			// Update thumbnails (if enabled)
			if (base.options.thumbnail_navigation == 1) {

				// Load previous thumbnail
				//prevThumb = loadSlide;
				loadSlide == 0 ? prevThumb = base.options.slides.length - 1 : prevThumb = loadSlide - 1;
				$(vars.prev_thumb).html($("<img/>").attr("src", base.options.slides[prevThumb].image));

				// Load next thumbnail
				vars.current_slide == base.options.slides.length - 1 ? nextThumb = 0 : nextThumb = vars.current_slide + 1;
				$(vars.next_thumb).html($("<img/>").attr("src", base.options.slides[nextThumb].image));
			}

			/*-----End Load Image-----*/


			// Call theme function for before slide transition
			if (typeof theme != 'undefined' && typeof theme.beforeAnimation == "function") theme.beforeAnimation('prev');

			//Update slide markers
			if (base.options.slide_links) {
				$('.current-slide').removeClass('current-slide');
				$(vars.slide_list + '> li').eq(vars.current_slide).addClass('current-slide');
			}

			nextslide.css('visibility', 'hidden').addClass('activeslide');	// Update active slide

			switch (base.options.transition) {
				case 0: case 'none':	// No transition
					nextslide.css('visibility', 'visible'); vars.in_animation = false; base.afterAnimation();
					break;
				case 1: case 'fade':	// Fade
					nextslide.css({ opacity: 0, 'visibility': 'visible' }).animate({ opacity: 1, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					break;
				case 2: case 'slideTop':	// Slide Top (reverse)
					nextslide.css({ top: base.$el.height(), 'visibility': 'visible' }).animate({ top: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					break;
				case 3: case 'slideRight':	// Slide Right (reverse)
					nextslide.css({ left: -base.$el.width(), 'visibility': 'visible' }).animate({ left: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					break;
				case 4: case 'slideBottom': // Slide Bottom (reverse)
					nextslide.css({ top: -base.$el.height(), 'visibility': 'visible' }).animate({ top: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					break;
				case 5: case 'slideLeft':  // Slide Left (reverse)
					nextslide.css({ left: base.$el.width(), 'visibility': 'visible' }).animate({ left: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					break;
				case 6: case 'carouselRight':	// Carousel Right (reverse)
					nextslide.css({ left: -base.$el.width(), 'visibility': 'visible' }).animate({ left: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					liveslide.css({ left: 0 }).animate({ left: base.$el.width(), avoidTransforms: false }, base.options.transition_speed);
					break;
				case 7: case 'carouselLeft':   // Carousel Left (reverse)
					nextslide.css({ left: base.$el.width(), 'visibility': 'visible' }).animate({ left: 0, avoidTransforms: false }, base.options.transition_speed, function () { base.afterAnimation(); });
					liveslide.css({ left: 0 }).animate({ left: -base.$el.width(), avoidTransforms: false }, base.options.transition_speed);
					break;
			}
			return false;
		};


		/* Play/Pause Toggle
		----------------------------*/
		base.playToggle = function () {

			if (vars.in_animation || !api.options.slideshow) return false;		// Abort if currently animating

			if (vars.is_paused) {

				vars.is_paused = false;

				// Call theme function for play
				if (typeof theme != 'undefined' && typeof theme.playToggle == "function") theme.playToggle('play');

				// Resume slideshow
				vars.slideshow_interval = setInterval(base.nextSlide, base.options.slide_interval);

			} else {

				vars.is_paused = true;

				// Call theme function for pause
				if (typeof theme != 'undefined' && typeof theme.playToggle == "function") theme.playToggle('pause');

				// Stop slideshow
				clearInterval(vars.slideshow_interval);

			}

			return false;

		};

		/* Tear down this instance of supersized
		----------------------------*/
		base.destroy = function () {
			if (vars.in_animation || !api.options.slideshow) return;		// Abort if currently animating

			// Start slideshow if paused. Without this, the slideshow is paused and the play/pause button has the wrong icon
			// when the user clicks the 'start slideshow' button a second time.
			if (vars.is_paused)
				api.playToggle();

			clearInterval(vars.slideshow_interval);

			// Unbind events (requires jQuery 1.7+)
			$(document.documentElement).off('.supersized');
			$('.ssControlsContainer *').off('click');

			var currentSlideId = vars.options.slides[vars.current_slide].id;

			vars = null;
			api = null;

			// Remove slideshow DOM elements and restore the page.
			$('#supersized-loader,#supersized,.ssControlsContainer').remove();
			$('body .supersized_hidden').show().removeClass('supersized_hidden');

			// Trigger on_destroy event
			base.options.on_destroy.apply(null, [currentSlideId]);
		};

		/* Go to specific slide
	----------------------------*/
		base.goTo = function (targetSlide) {
			if (vars.in_animation || !api.options.slideshow) return false;		// Abort if currently animating

			var totalSlides = base.options.slides.length;

			// If target outside range
			if (targetSlide < 0) {
				targetSlide = totalSlides;
			} else if (targetSlide > totalSlides) {
				targetSlide = 1;
			}
			targetSlide = totalSlides - targetSlide + 1;

			clearInterval(vars.slideshow_interval);	// Stop slideshow, prevent buildup

			// Call theme function for goTo trigger
			if (typeof theme != 'undefined' && typeof theme.goTo == "function") theme.goTo();

			if (vars.current_slide == totalSlides - targetSlide) {
				if (!(vars.is_paused)) {
					vars.slideshow_interval = setInterval(base.nextSlide, base.options.slide_interval);
				}
				return false;
			}

			// If ahead of current position
			if (totalSlides - targetSlide > vars.current_slide) {

				// Adjust for new next slide
				vars.current_slide = totalSlides - targetSlide - 1;
				vars.update_images = 'next';
				base._placeSlide(vars.update_images);

				//Otherwise it's before current position
			} else if (totalSlides - targetSlide < vars.current_slide) {

				// Adjust for new prev slide
				vars.current_slide = totalSlides - targetSlide + 1;
				vars.update_images = 'prev';
				base._placeSlide(vars.update_images);

			}

			// set active markers
			if (base.options.slide_links) {
				$(vars.slide_list + '> .current-slide').removeClass('current-slide');
				$(vars.slide_list + '> li').eq((totalSlides - targetSlide)).addClass('current-slide');
			}

			if (base.options.thumb_links) {
				$(vars.thumb_list + '> .current-thumb').removeClass('current-thumb');
				$(vars.thumb_list + '> li').eq((totalSlides - targetSlide)).addClass('current-thumb');
			}

		};


		/* Place Slide
----------------------------*/
		base._placeSlide = function (place) {

			// If links should open in new window
			var linkTarget = base.options.new_window ? ' target="_blank"' : '';

			loadSlide = false;

			if (place == 'next') {

				vars.current_slide == base.options.slides.length - 1 ? loadSlide = 0 : loadSlide = vars.current_slide + 1;	// Determine next slide

				var targetList = base.el + ' li:eq(' + loadSlide + ')';

				if (!$(targetList).html()) {
					// If links should open in new window
					var linkTarget = base.options.new_window ? ' target="_blank"' : '';

					imageLink = (base.options.slides[loadSlide].url) ? "href='" + base.options.slides[loadSlide].url + "'" : "";	// If link exists, build it
					var img = $('<img src="' + base.options.slides[loadSlide].image + '"/>');

					img.appendTo(targetList).wrap('<a ' + imageLink + linkTarget + '></a>').parent().parent().addClass('image-loading').css('visibility', 'hidden');

					img.load(function () {
						base._origDim($(this));
						base.resizeNow();
					});	// End Load
				};

				base.nextSlide();

			} else if (place == 'prev') {

				vars.current_slide - 1 < 0 ? loadSlide = base.options.slides.length - 1 : loadSlide = vars.current_slide - 1;	// Determine next slide

				var targetList = base.el + ' li:eq(' + loadSlide + ')';

				if (!$(targetList).html()) {
					// If links should open in new window
					var linkTarget = base.options.new_window ? ' target="_blank"' : '';

					imageLink = (base.options.slides[loadSlide].url) ? "href='" + base.options.slides[loadSlide].url + "'" : "";	// If link exists, build it
					var img = $('<img src="' + base.options.slides[loadSlide].image + '"/>');

					img.appendTo(targetList).wrap('<a ' + imageLink + linkTarget + '></a>').parent().parent().addClass('image-loading').css('visibility', 'hidden');

					img.load(function () {
						base._origDim($(this));
						base.resizeNow();
					});	// End Load
				};
				base.prevSlide();
			}

		};


		/* Get Original Dimensions
		----------------------------*/
		base._origDim = function (targetSlide) {
			targetSlide.data('origWidth', targetSlide.width()).data('origHeight', targetSlide.height());
		};


		/* After Slide Animation
		----------------------------*/
		base.afterAnimation = function () {

			// If hybrid mode is on swap back to higher image quality
			if (base.options.performance == 1) {
				base.$el.removeClass('speed').addClass('quality');
			}

			// Update previous slide
			if (vars.update_images) {
				vars.current_slide - 1 < 0 ? setPrev = base.options.slides.length - 1 : setPrev = vars.current_slide - 1;
				vars.update_images = false;
				$('.prevslide').removeClass('prevslide');
				$(base.el + ' li:eq(' + setPrev + ')').addClass('prevslide');
			}

			vars.in_animation = false;

			// Resume slideshow
			if (!vars.is_paused && base.options.slideshow) {
				vars.slideshow_interval = setInterval(base.nextSlide, base.options.slide_interval);
				if (!base.options.loop && !base.options.auto_exit && vars.current_slide == base.options.slides.length - 1) base.playToggle();
			}

			// Call theme function for after slide transition
			if (typeof theme != 'undefined' && typeof theme.afterAnimation == "function") theme.afterAnimation();

			return false;

		};

		base.getField = function (field) {
			return base.options.slides[vars.current_slide][field];
		};

		// Make it go!
		base.init();
	};


	/* Global Variables
	----------------------------*/
	$.supersized.vars = {

		// Elements							
		thumb_tray: '#thumb-tray',	// Thumbnail tray
		thumb_list: '#thumb-list',	// Thumbnail list
		slide_list: '#slide-list',	// Slide link list

		// Internal variables
		current_slide: 0,			// Current slide number
		in_animation: false,		// Prevents animations from stacking
		is_paused: false,		// Tracks paused on/off
		hover_pause: false,		// If slideshow is paused from hover
		slideshow_interval: false,		// Stores slideshow timer					
		update_images: false,		// Trigger to update images after slide jump
		options: {}			// Stores assembled options list

	};


	/* Default Options
	----------------------------*/
	$.supersized.defaultOptions = {

		// Functionality
		slideshow: 1,			// Slideshow on/off
		autoplay: 1,			// Slideshow starts playing automatically
		auto_exit: 0,      // Exit the slideshow when the last slide is finished
		start_slide: 1,			// Start slide (0 is random)
		loop: 1,			// Enables moving between the last and first slide.
		random: 0,			// Randomize slide order (Ignores start slide)
		slide_interval: 5000,		// Length between transitions
		transition: 1, 			// 0-None, 1-Fade, 2-Slide Top, 3-Slide Right, 4-Slide Bottom, 5-Slide Left, 6-Carousel Right, 7-Carousel Left
		transition_speed: 750,		// Speed of transition
		new_window: 1,			// Image links open in new window/tab
		pause_hover: 0,			// Pause slideshow on hover
		keyboard_nav: 1,			// Keyboard navigation on/off
		performance: 1,			// 0-Normal, 1-Hybrid speed/quality, 2-Optimizes image quality, 3-Optimizes transition speed //  (Only works for Firefox/IE, not Webkit)
		image_protect: 1,			// Disables image dragging and right click with Javascript

		// Size & Position
		fit_always: 0,			// Image will never exceed browser width or height (Ignores min. dimensions)
		fit_landscape: 0,			// Landscape images will not exceed browser width
		fit_portrait: 1,			// Portrait images will not exceed browser height  			   
		min_width: 0,			// Min width allowed (in pixels)
		min_height: 0,			// Min height allowed (in pixels)
		horizontal_center: 1,			// Horizontally center background
		vertical_center: 1,			// Vertically center background


		// Components							
		slide_links: 1,			// Individual links for each slide (Options: false, 'num', 'name', 'blank')
		thumb_links: 1,			// Individual thumb links for each slide
		thumbnail_navigation: 0,			// Thumbnail navigation
		on_destroy: function () { } // Empty implementation for on_destroy event, may be overridden by user

	};

	$.fn.supersized = function (options) {
		return this.each(function () {
			(new $.supersized(options));
		});
	};

})(jQuery);

/*
	supersized.shutter.js
	Supersized - Fullscreen Slideshow jQuery Plugin
	Version : 3.2.7
	Theme 	: Shutter 1.1
	
	Site	: www.buildinternet.com/project/supersized
	Author	: Sam Dunn
	Company : One Mighty Roar (www.onemightyroar.com)
	License : MIT License / GPL License

*/

(function ($) {

	theme = {


		/* Initial Placement
		----------------------------*/
		_init: function () {

			// Configure Slide Links
			if (api.options.slide_links) {
				// Note: This code is repeated in the resize event, so if you change it here do it there, too.
				var maxSlideListWidth = $(vars.slide_list).parent().width() - 400; // Constrain the slide bullets area width so they don't cover buttons
				$(vars.slide_list).css('margin-left', -$(vars.slide_list).width() / 2).css('max-width', maxSlideListWidth);
			}

			// Start progressbar if autoplay enabled
			if (api.options.autoplay) {
				if (api.options.progress_bar) theme.progressBar(); else $(vars.progress_bar).parent().hide();
			} else {
				if ($(vars.play_button).attr('src')) $(vars.play_button).attr("src", api.options.image_path + "play.png");	// If pause play button is image, swap src
				if (api.options.progress_bar)
					$(vars.progress_bar).stop().css({ left: -$(window).width() });	//  Place progress bar
				else
					$(vars.progress_bar).parent().hide();
			}


			/* Thumbnail Tray
			----------------------------*/
			// Hide tray off screen
			$(vars.thumb_tray).css({ bottom: -($(vars.thumb_tray).outerHeight() + 5) });

			// Thumbnail Tray Toggle
			$(vars.tray_button).toggle(function () {
				$(vars.thumb_tray).stop().animate({ bottom: 0, avoidTransforms: true }, 300);
				if ($(vars.tray_arrow).attr('src')) $(vars.tray_arrow).attr("src", api.options.image_path + "button-tray-down.png");
				return false;
			}, function () {
				$(vars.thumb_tray).stop().animate({ bottom: -($(vars.thumb_tray).outerHeight() + 5), avoidTransforms: true }, 300);
				if ($(vars.tray_arrow).attr('src')) $(vars.tray_arrow).attr("src", api.options.image_path + "button-tray-up.png");
				return false;
			});

			// Make thumb tray proper size
			$(vars.thumb_list).width($('> li', vars.thumb_list).length * $('> li', vars.thumb_list).outerWidth(true));	//Adjust to true width of thumb markers

			// Display total slides
			if ($(vars.slide_total).length) {
				$(vars.slide_total).html(api.options.slides.length);
			}


			/* Thumbnail Tray Navigation
			----------------------------*/
			if (api.options.thumb_links) {
				//Hide thumb arrows if not needed
				if ($(vars.thumb_list).width() <= $(vars.thumb_tray).width()) {
					$(vars.thumb_back + ',' + vars.thumb_forward).fadeOut(0);
				}

				// Thumb Intervals
				vars.thumb_interval = Math.floor($(vars.thumb_tray).width() / $('> li', vars.thumb_list).outerWidth(true)) * $('> li', vars.thumb_list).outerWidth(true);
				vars.thumb_page = 0;

				// Cycle thumbs forward
				$(vars.thumb_forward).click(function () {
					if (vars.thumb_page - vars.thumb_interval <= -$(vars.thumb_list).width()) {
						vars.thumb_page = 0;
						$(vars.thumb_list).stop().animate({ 'left': vars.thumb_page }, { duration: 500, easing: 'easeOutExpo' });
					} else {
						vars.thumb_page = vars.thumb_page - vars.thumb_interval;
						$(vars.thumb_list).stop().animate({ 'left': vars.thumb_page }, { duration: 500, easing: 'easeOutExpo' });
					}
				});

				// Cycle thumbs backwards
				$(vars.thumb_back).click(function () {
					if (vars.thumb_page + vars.thumb_interval > 0) {
						vars.thumb_page = Math.floor($(vars.thumb_list).width() / vars.thumb_interval) * -vars.thumb_interval;
						if ($(vars.thumb_list).width() <= -vars.thumb_page) vars.thumb_page = vars.thumb_page + vars.thumb_interval;
						$(vars.thumb_list).stop().animate({ 'left': vars.thumb_page }, { duration: 500, easing: 'easeOutExpo' });
					} else {
						vars.thumb_page = vars.thumb_page + vars.thumb_interval;
						$(vars.thumb_list).stop().animate({ 'left': vars.thumb_page }, { duration: 500, easing: 'easeOutExpo' });
					}
				});

			}


			/* Navigation Items
			----------------------------*/
			$(vars.next_slide).click(function () {
				api.nextSlide();
			});

			$(vars.prev_slide).click(function () {
				api.prevSlide();
			});

			// Full Opacity on Hover
			if (jQuery.support.opacity) {
				$(vars.prev_slide + ',' + vars.next_slide).mouseover(function () {
					$(this).stop().animate({ opacity: 1 }, 100);
				}).mouseout(function () {
					$(this).stop().animate({ opacity: 0.6 }, 100);
				});
			}

			if (api.options.thumbnail_navigation) {
				// Next thumbnail clicked
				$(vars.next_thumb).click(function () {
					api.nextSlide();
				});
				// Previous thumbnail clicked
				$(vars.prev_thumb).click(function () {
					api.prevSlide();
				});
			}

			$(vars.play_button).click(function () {
				api.playToggle();
			});


			/* Thumbnail Mouse Scrub
			----------------------------*/
			if (api.options.mouse_scrub) {
				$(vars.thumb_tray).mousemove(function (e) {
					var containerWidth = $(vars.thumb_tray).width(),
						listWidth = $(vars.thumb_list).width();
					if (listWidth > containerWidth) {
						var mousePos = 1,
							diff = e.pageX - mousePos;
						if (diff > 10 || diff < -10) {
							mousePos = e.pageX;
							newX = (containerWidth - listWidth) * (e.pageX / containerWidth);
							diff = parseInt(Math.abs(parseInt($(vars.thumb_list).css('left')) - newX)).toFixed(0);
							$(vars.thumb_list).stop().animate({ 'left': newX }, { duration: diff * 3, easing: 'easeOutExpo' });
						}
					}
				});
			}


			/* Window Resize
			----------------------------*/
			$(window).resize(function () {

				// Delay progress bar on resize
				if (api.options.progress_bar && !vars.in_animation) {
					if (vars.slideshow_interval) clearInterval(vars.slideshow_interval);
					if (api.options.slides.length - 1 > 0) clearInterval(vars.slideshow_interval);

					$(vars.progress_bar).stop().css({ left: -$(window).width() });

					if (!vars.progressDelay && api.options.slideshow) {
						// Delay slideshow from resuming so Chrome can refocus images
						vars.progressDelay = setTimeout(function () {
							if (!vars.is_paused) {
								theme.progressBar();
								vars.slideshow_interval = setInterval(api.nextSlide, api.options.slide_interval);
							}
							vars.progressDelay = false;
						}, 1000);
					}
				}

				// Thumb Links
				if (api.options.thumb_links && vars.thumb_tray.length) {
					// Update Thumb Interval & Page
					vars.thumb_page = 0;
					vars.thumb_interval = Math.floor($(vars.thumb_tray).width() / $('> li', vars.thumb_list).outerWidth(true)) * $('> li', vars.thumb_list).outerWidth(true);

					// Adjust thumbnail markers
					if ($(vars.thumb_list).width() > $(vars.thumb_tray).width()) {
						$(vars.thumb_back + ',' + vars.thumb_forward).fadeIn('fast');
						$(vars.thumb_list).stop().animate({ 'left': 0 }, 200);
					} else {
						$(vars.thumb_back + ',' + vars.thumb_forward).fadeOut('fast');
					}

				}

				// Configure Slide Links
				if (api.options.slide_links) {
					// Note: This code is repeated in the _init function, so if you change it here do it there, too.
					maxSlideListWidth = $(vars.slide_list).parent().width() - 400; // Constrain the slide bullets area width so they don't cover buttons
					$(vars.slide_list).css('margin-left', -$(vars.slide_list).width() / 2).css('max-width', maxSlideListWidth);
					console.log(maxSlideListWidth);
				}
			});


		},


		/* Go To Slide
		----------------------------*/
		goTo: function () {
			if (api.options.progress_bar && !vars.is_paused) {
				$(vars.progress_bar).stop().css({ left: -$(window).width() });
				theme.progressBar();
			}
		},

		/* Play & Pause Toggle
		----------------------------*/
		playToggle: function (state) {

			if (state == 'play') {
				// If image, swap to pause
				if ($(vars.play_button).attr('src')) $(vars.play_button).attr("src", api.options.image_path + "pause.png");
				if (api.options.progress_bar && !vars.is_paused) theme.progressBar();
			} else if (state == 'pause') {
				// If image, swap to play
				if ($(vars.play_button).attr('src')) $(vars.play_button).attr("src", api.options.image_path + "play.png");
				if (api.options.progress_bar && vars.is_paused) $(vars.progress_bar).stop().css({ left: -$(window).width() });
			}

		},


		/* Before Slide Transition
		----------------------------*/
		beforeAnimation: function (direction) {
			if (api.options.progress_bar && !vars.is_paused) $(vars.progress_bar).stop().css({ left: -$(window).width() });

			/* Update Fields
			----------------------------*/
			// Update slide caption
			if ($(vars.slide_caption).length) {
				(api.getField('title')) ? $(vars.slide_caption).html(api.getField('title')) : $(vars.slide_caption).html('');
			}
			// Update slide number
			if (vars.slide_current.length) {
				$(vars.slide_current).html(vars.current_slide + 1);
			}


			// Highlight current thumbnail and adjust row position
			if (api.options.thumb_links) {

				$('.current-thumb').removeClass('current-thumb');
				$('li', vars.thumb_list).eq(vars.current_slide).addClass('current-thumb');

				// If thumb out of view
				if ($(vars.thumb_list).width() > $(vars.thumb_tray).width()) {
					// If next slide direction
					if (direction == 'next') {
						if (vars.current_slide == 0) {
							vars.thumb_page = 0;
							$(vars.thumb_list).stop().animate({ 'left': vars.thumb_page }, { duration: 500, easing: 'easeOutExpo' });
						} else if ($('.current-thumb').offset().left - $(vars.thumb_tray).offset().left >= vars.thumb_interval) {
							vars.thumb_page = vars.thumb_page - vars.thumb_interval;
							$(vars.thumb_list).stop().animate({ 'left': vars.thumb_page }, { duration: 500, easing: 'easeOutExpo' });
						}
						// If previous slide direction
					} else if (direction == 'prev') {
						if (vars.current_slide == api.options.slides.length - 1) {
							vars.thumb_page = Math.floor($(vars.thumb_list).width() / vars.thumb_interval) * -vars.thumb_interval;
							if ($(vars.thumb_list).width() <= -vars.thumb_page) vars.thumb_page = vars.thumb_page + vars.thumb_interval;
							$(vars.thumb_list).stop().animate({ 'left': vars.thumb_page }, { duration: 500, easing: 'easeOutExpo' });
						} else if ($('.current-thumb').offset().left - $(vars.thumb_tray).offset().left < 0) {
							if (vars.thumb_page + vars.thumb_interval > 0) return false;
							vars.thumb_page = vars.thumb_page + vars.thumb_interval;
							$(vars.thumb_list).stop().animate({ 'left': vars.thumb_page }, { duration: 500, easing: 'easeOutExpo' });
						}
					}
				}


			}

		},


		/* After Slide Transition
		----------------------------*/
		afterAnimation: function () {
			if (api.options.progress_bar && !vars.is_paused) theme.progressBar();	//  Start progress bar
		},


		/* Progress Bar
		----------------------------*/
		progressBar: function () {
			$(vars.progress_bar).stop().css({ left: -$(window).width() }).animate({ left: 0 }, api.options.slide_interval);
		}


	};


	/* Theme Specific Variables
	----------------------------*/
	$.supersized.themeVars = {

		// Internal Variables
		progress_delay: false,				// Delay after resize before resuming slideshow
		thumb_page: false,				// Thumbnail page
		thumb_interval: false,				// Thumbnail interval

		// General Elements							
		play_button: '#pauseplay',		// Play/Pause button
		next_slide: '#nextslide',		// Next slide button
		prev_slide: '#prevslide',		// Prev slide button
		next_thumb: '#nextthumb',		// Next slide thumb button
		prev_thumb: '#prevthumb',		// Prev slide thumb button

		slide_caption: '#slidecaption',	// Slide caption
		slide_current: '.slidenumber',		// Current slide number
		slide_total: '.totalslides',		// Total Slides
		slide_list: '#slide-list',		// Slide jump list							

		thumb_tray: '#thumb-tray',		// Thumbnail tray
		thumb_list: '#thumb-list',		// Thumbnail list
		thumb_forward: '#thumb-forward',	// Cycles forward through thumbnail list
		thumb_back: '#thumb-back',		// Cycles backwards through thumbnail list
		tray_arrow: '#tray-arrow',		// Thumbnail tray button arrow
		tray_button: '#tray-button',		// Thumbnail tray button

		progress_bar: '#progress-bar'		// Progress bar

	};

	/* Theme Specific Options
	----------------------------*/
	$.supersized.themeOptions = {

		progress_bar: 1,		// Timer for each slide											
		image_path: 'img/',				// Default image path
		mouse_scrub: 0,		// Thumbnails move with mouse
		// html_template contains the HTML for the slideshow controls
		html_template: '\
<div class="ssControlsContainer"> \
		<!--Thumbnail Navigation--> \
		<div id="prevthumb"></div> \
		<div id="nextthumb"></div> \
\
		<!--Arrow Navigation--> \
		<a id="prevslide" class="load-item"></a> \
		<a id="nextslide" class="load-item"></a> \
\
		<div id="thumb-tray" class="load-item"> \
			<div id="thumb-back"></div> \
			<div id="thumb-forward"></div> \
		</div> \
\
		<!--Time Bar--> \
		<div id="progress-back" class="load-item"> \
			<div id="progress-bar"></div> \
		</div> \
\
		<!--Control Bar--> \
		<div id="controls-wrapper" class="load-item"> \
			<div id="controls"> \
\
				<a id="play-button"> \
					<img id="pauseplay" src="img/pause.png" /></a> \
\
				<a id="stop-button"> \
					<img src="img/stop.png" /></a> \
\
				<!--Slide counter--> \
				<div id="slidecounter"> \
					<span class="slidenumber"></span>/ <span class="totalslides"></span> \
				</div> \
\
				<!--Slide captions displayed here--> \
				<div id="slidecaption"></div> \
\
				<!--Thumb Tray button--> \
				<a id="tray-button"> \
					<img id="tray-arrow" src="img/button-tray-up.png" /></a> \
\
				<!--Navigation--> \
				<ul id="slide-list"></ul> \
\
			</div> \
		</div> \
</div>'

	};


})(jQuery);

//#endregion End supersized

//#region MultiSelect

/*
 * jQuery MultiSelect UI Widget 1.13
 * Copyright (c) 2012 Eric Hynds
 *
 * http://www.erichynds.com/jquery/jquery-ui-multiselect-widget/
 *
 * Depends:
 *   - jQuery 1.4.2+
 *   - jQuery UI 1.8 widget factory
 *
 * Optional:
 *   - jQuery UI effects
 *   - jQuery UI position utility
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * CHANGE LOG
 * 2014-05-30 - HTML-encode quotes. See [Roger]
 *
*/
(function ($, undefined) {

	var multiselectID = 0;

	$.widget("ech.multiselect", {

		// default options
		options: {
			header: true,
			height: 175,
			minWidth: 225,
			classes: '',
			checkAllText: 'Check all',
			uncheckAllText: 'Uncheck all',
			noneSelectedText: 'Select options',
			selectedText: '# selected',
			selectedList: 0,
			show: null,
			hide: null,
			autoOpen: false,
			multiple: true,
			position: {}
		},

		_create: function () {
			var el = this.element.hide(),
				o = this.options;

			this.speed = $.fx.speeds._default; // default speed for effects
			this._isOpen = false; // assume no

			var
				button = (this.button = $('<button type="button"><span class="ui-icon ui-icon-triangle-2-n-s"></span></button>'))
					.addClass('ui-multiselect ui-widget ui-state-default ui-corner-all')
					.addClass(o.classes)
					.attr({ 'title': el.attr('title'), 'aria-haspopup': true, 'tabIndex': el.attr('tabIndex') })
					.insertAfter(el),

				buttonlabel = (this.buttonlabel = $('<span />'))
					.html(o.noneSelectedText)
					.appendTo(button),

				menu = (this.menu = $('<div />'))
					.addClass('ui-multiselect-menu ui-widget ui-widget-content ui-corner-all')
					.addClass(o.classes)
					.appendTo(document.body),

				header = (this.header = $('<div />'))
					.addClass('ui-widget-header ui-corner-all ui-multiselect-header ui-helper-clearfix')
					.appendTo(menu),

				headerLinkContainer = (this.headerLinkContainer = $('<ul />'))
					.addClass('ui-helper-reset')
					.html(function () {
						if (o.header === true) {
							return '<li><a class="ui-multiselect-all" href="#"><span class="ui-icon ui-icon-check"></span><span>' + o.checkAllText + '</span></a></li><li><a class="ui-multiselect-none" href="#"><span class="ui-icon ui-icon-closethick"></span><span>' + o.uncheckAllText + '</span></a></li>';
						} else if (typeof o.header === "string") {
							return '<li>' + o.header + '</li>';
						} else {
							return '';
						}
					})
					.append('<li class="ui-multiselect-close"><a href="#" class="ui-multiselect-close"><span class="ui-icon ui-icon-circle-close"></span></a></li>')
					.appendTo(header),

				checkboxContainer = (this.checkboxContainer = $('<ul />'))
					.addClass('ui-multiselect-checkboxes ui-helper-reset')
					.appendTo(menu);

			// perform event bindings
			this._bindEvents();

			// build menu
			this.refresh(true);

			// some addl. logic for single selects
			if (!o.multiple) {
				menu.addClass('ui-multiselect-single');
			}
		},

		_init: function () {
			if (this.options.header === false) {
				this.header.hide();
			}
			if (!this.options.multiple) {
				this.headerLinkContainer.find('.ui-multiselect-all, .ui-multiselect-none').hide();
			}
			if (this.options.autoOpen) {
				this.open();
			}
			if (this.element.is(':disabled')) {
				this.disable();
			}
		},

		refresh: function (init) {
			var el = this.element,
				o = this.options,
				menu = this.menu,
				checkboxContainer = this.checkboxContainer,
				optgroups = [],
				html = "",
				id = el.attr('id') || multiselectID++; // unique ID for the label & option tags

			// build items
			el.find('option').each(function (i) {
				var $this = $(this),
					parent = this.parentNode,
					title = this.innerHTML,
					description = this.title,
					value = this.value,
					inputID = 'ui-multiselect-' + (this.id || id + '-option-' + i),
					isDisabled = this.disabled,
					isSelected = this.selected,
					labelClasses = ['ui-corner-all'],
					liClasses = (isDisabled ? 'ui-multiselect-disabled ' : ' ') + this.className,
					optLabel;

				// is this an optgroup?
				if (parent.tagName === 'OPTGROUP') {
					optLabel = parent.getAttribute('label');

					// has this optgroup been added already?
					if ($.inArray(optLabel, optgroups) === -1) {
						html += '<li class="ui-multiselect-optgroup-label ' + parent.className + '"><a href="#">' + optLabel + '</a></li>';
						optgroups.push(optLabel);
					}
				}

				if (isDisabled) {
					labelClasses.push('ui-state-disabled');
				}

				// browsers automatically select the first option
				// by default with single selects
				if (isSelected && !o.multiple) {
					labelClasses.push('ui-state-active');
				}

				html += '<li class="' + liClasses + '">';

				// create the label
				html += '<label for="' + inputID + '" title="' + description + '" class="' + labelClasses.join(' ') + '">';
				html += '<input id="' + inputID + '" name="multiselect_' + id + '" type="' + (o.multiple ? "checkbox" : "radio") + '" value="' + value.replace(/\"/g, '&quot;') + '" title="' + title.replace(/\"/g, '&quot;') + '"'; // [Roger] Added replace function to HTML encode quotes

				// pre-selected?
				if (isSelected) {
					html += ' checked="checked"';
					html += ' aria-selected="true"';
				}

				// disabled?
				if (isDisabled) {
					html += ' disabled="disabled"';
					html += ' aria-disabled="true"';
				}

				// add the title and close everything off
				html += ' /><span>' + title + '</span></label></li>';
			});

			// insert into the DOM
			checkboxContainer.html(html);

			// cache some moar useful elements
			this.labels = menu.find('label');
			this.inputs = this.labels.children('input');

			// set widths
			this._setButtonWidth();
			this._setMenuWidth();

			// remember default value
			this.button[0].defaultValue = this.update();

			// broadcast refresh event; useful for widgets
			if (!init) {
				this._trigger('refresh');
			}
		},

		// updates the button text. call refresh() to rebuild
		update: function () {
			var o = this.options,
				$inputs = this.inputs,
				$checked = $inputs.filter(':checked'),
				numChecked = $checked.length,
				value;

			if (numChecked === 0) {
				value = o.noneSelectedText;
			} else {
				if ($.isFunction(o.selectedText)) {
					value = o.selectedText.call(this, numChecked, $inputs.length, $checked.get());
				} else if (/\d/.test(o.selectedList) && o.selectedList > 0 && numChecked <= o.selectedList) {
					value = $checked.map(function () { return $(this).next().html(); }).get().join(', ');
				} else {
					value = o.selectedText.replace('#', numChecked).replace('#', $inputs.length);
				}
			}

			this.buttonlabel.html(value);
			return value;
		},

		// binds events
		_bindEvents: function () {
			var self = this, button = this.button;

			function clickHandler() {
				self[self._isOpen ? 'close' : 'open']();
				return false;
			}

			// webkit doesn't like it when you click on the span :(
			button
				.find('span')
				.bind('click.multiselect', clickHandler);

			// button events
			button.bind({
				click: clickHandler,
				keypress: function (e) {
					switch (e.which) {
						case 27: // esc
						case 38: // up
						case 37: // left
							self.close();
							break;
						case 39: // right
						case 40: // down
							self.open();
							break;
					}
				},
				mouseenter: function () {
					if (!button.hasClass('ui-state-disabled')) {
						$(this).addClass('ui-state-hover');
					}
				},
				mouseleave: function () {
					$(this).removeClass('ui-state-hover');
				},
				focus: function () {
					if (!button.hasClass('ui-state-disabled')) {
						$(this).addClass('ui-state-focus');
					}
				},
				blur: function () {
					$(this).removeClass('ui-state-focus');
				}
			});

			// header links
			this.header
				.delegate('a', 'click.multiselect', function (e) {
					// close link
					if ($(this).hasClass('ui-multiselect-close')) {
						self.close();

						// check all / uncheck all
					} else {
						self[$(this).hasClass('ui-multiselect-all') ? 'checkAll' : 'uncheckAll']();
					}

					e.preventDefault();
				});

			// optgroup label toggle support
			this.menu
				.delegate('li.ui-multiselect-optgroup-label a', 'click.multiselect', function (e) {
					e.preventDefault();

					var $this = $(this),
						$inputs = $this.parent().nextUntil('li.ui-multiselect-optgroup-label').find('input:visible:not(:disabled)'),
						nodes = $inputs.get(),
						label = $this.parent().text();

					// trigger event and bail if the return is false
					if (self._trigger('beforeoptgrouptoggle', e, { inputs: nodes, label: label }) === false) {
						return;
					}

					// toggle inputs
					self._toggleChecked(
						$inputs.filter(':checked').length !== $inputs.length,
						$inputs
					);

					self._trigger('optgrouptoggle', e, {
						inputs: nodes,
						label: label,
						checked: nodes[0].checked
					});
				})
				.delegate('label', 'mouseenter.multiselect', function () {
					if (!$(this).hasClass('ui-state-disabled')) {
						self.labels.removeClass('ui-state-hover');
						$(this).addClass('ui-state-hover').find('input').focus();
					}
				})
				.delegate('label', 'keydown.multiselect', function (e) {
					e.preventDefault();

					switch (e.which) {
						case 9: // tab
						case 27: // esc
							self.close();
							break;
						case 38: // up
						case 40: // down
						case 37: // left
						case 39: // right
							self._traverse(e.which, this);
							break;
						case 13: // enter
							$(this).find('input')[0].click();
							break;
					}
				})
				.delegate('input[type="checkbox"], input[type="radio"]', 'click.multiselect', function (e) {
					var $this = $(this),
						val = this.value,
						checked = this.checked,
						tags = self.element.find('option');

					// bail if this input is disabled or the event is cancelled
					if (this.disabled || self._trigger('click', e, { value: val, text: this.title, checked: checked }) === false) {
						e.preventDefault();
						return;
					}

					// make sure the input has focus. otherwise, the esc key
					// won't close the menu after clicking an item.
					$this.focus();

					// toggle aria state
					$this.attr('aria-selected', checked);

					// change state on the original option tags
					tags.each(function () {
						if (this.value === val) {
							this.selected = checked;
						} else if (!self.options.multiple) {
							this.selected = false;
						}
					});

					// some additional single select-specific logic
					if (!self.options.multiple) {
						self.labels.removeClass('ui-state-active');
						$this.closest('label').toggleClass('ui-state-active', checked);

						// close menu
						self.close();
					}

					// fire change on the select box
					self.element.trigger("change");

					// setTimeout is to fix multiselect issue #14 and #47. caused by jQuery issue #3827
					// http://bugs.jquery.com/ticket/3827
					setTimeout($.proxy(self.update, self), 10);
				});

			// close each widget when clicking on any other element/anywhere else on the page
			$(document).bind('mousedown.multiselect', function (e) {
				if (self._isOpen && !$.contains(self.menu[0], e.target) && !$.contains(self.button[0], e.target) && e.target !== self.button[0]) {
					self.close();
				}
			});

			// deal with form resets.  the problem here is that buttons aren't
			// restored to their defaultValue prop on form reset, and the reset
			// handler fires before the form is actually reset.  delaying it a bit
			// gives the form inputs time to clear.
			$(this.element[0].form).bind('reset.multiselect', function () {
				setTimeout($.proxy(self.refresh, self), 10);
			});
		},

		// set button width
		_setButtonWidth: function () {
			var width = this.element.outerWidth(),
				o = this.options;

			if (/\d/.test(o.minWidth) && width < o.minWidth) {
				width = o.minWidth;
			}

			// set widths
			this.button.width(width);
		},

		// set menu width
		_setMenuWidth: function () {
			var m = this.menu,
				width = this.button.outerWidth() -
					parseInt(m.css('padding-left'), 10) -
					parseInt(m.css('padding-right'), 10) -
					parseInt(m.css('border-right-width'), 10) -
					parseInt(m.css('border-left-width'), 10);

			m.width(width || this.button.outerWidth());
		},

		// move up or down within the menu
		_traverse: function (which, start) {
			var $start = $(start),
				moveToLast = which === 38 || which === 37,

				// select the first li that isn't an optgroup label / disabled
				$next = $start.parent()[moveToLast ? 'prevAll' : 'nextAll']('li:not(.ui-multiselect-disabled, .ui-multiselect-optgroup-label)')[moveToLast ? 'last' : 'first']();

			// if at the first/last element
			if (!$next.length) {
				var $container = this.menu.find('ul').last();

				// move to the first/last
				this.menu.find('label')[moveToLast ? 'last' : 'first']().trigger('mouseover');

				// set scroll position
				$container.scrollTop(moveToLast ? $container.height() : 0);

			} else {
				$next.find('label').trigger('mouseover');
			}
		},

		// This is an internal function to toggle the checked property and
		// other related attributes of a checkbox.
		//
		// The context of this function should be a checkbox; do not proxy it.
		_toggleState: function (prop, flag) {
			return function () {
				if (!this.disabled) {
					this[prop] = flag;
				}

				if (flag) {
					this.setAttribute('aria-selected', true);
				} else {
					this.removeAttribute('aria-selected');
				}
			};
		},

		_toggleChecked: function (flag, group) {
			var $inputs = (group && group.length) ? group : this.inputs,
				self = this;

			// toggle state on inputs
			$inputs.each(this._toggleState('checked', flag));

			// give the first input focus
			$inputs.eq(0).focus();

			// update button text
			this.update();

			// gather an array of the values that actually changed
			var values = $inputs.map(function () {
				return this.value;
			}).get();

			// toggle state on original option tags
			this.element
				.find('option')
				.each(function () {
					if (!this.disabled && $.inArray(this.value, values) > -1) {
						self._toggleState('selected', flag).call(this);
					}
				});

			// trigger the change event on the select
			if ($inputs.length) {
				this.element.trigger("change");
			}
		},

		_toggleDisabled: function (flag) {
			this.button
				.attr({ 'disabled': flag, 'aria-disabled': flag })[flag ? 'addClass' : 'removeClass']('ui-state-disabled');

			var inputs = this.menu.find('input');
			var key = "ech-multiselect-disabled";

			if (flag) {
				// remember which elements this widget disabled (not pre-disabled)
				// elements, so that they can be restored if the widget is re-enabled.
				inputs = inputs.filter(':enabled')
					.data(key, true)
			} else {
				inputs = inputs.filter(function () {
					return $.data(this, key) === true;
				}).removeData(key);
			}

			inputs
				.attr({ 'disabled': flag, 'arial-disabled': flag })
				.parent()[flag ? 'addClass' : 'removeClass']('ui-state-disabled');

			this.element
				.attr({ 'disabled': flag, 'aria-disabled': flag });
		},

		// open the menu
		open: function (e) {
			var self = this,
				button = this.button,
				menu = this.menu,
				speed = this.speed,
				o = this.options,
				args = [];

			// bail if the multiselectopen event returns false, this widget is disabled, or is already open
			if (this._trigger('beforeopen') === false || button.hasClass('ui-state-disabled') || this._isOpen) {
				return;
			}

			var $container = menu.find('ul').last(),
				effect = o.show,
				pos = button.offset();

			// figure out opening effects/speeds
			if ($.isArray(o.show)) {
				effect = o.show[0];
				speed = o.show[1] || self.speed;
			}

			// if there's an effect, assume jQuery UI is in use
			// build the arguments to pass to show()
			if (effect) {
				args = [effect, speed];
			}

			// set the scroll of the checkbox container
			$container.scrollTop(0).height(o.height);

			// position and show menu
			if ($.ui.position && !$.isEmptyObject(o.position)) {
				o.position.of = o.position.of || button;

				menu
					.show()
					.position(o.position)
					.hide();

				// if position utility is not available...
			} else {
				menu.css({
					top: pos.top + button.outerHeight(),
					left: pos.left
				});
			}

			// show the menu, maybe with a speed/effect combo
			$.fn.show.apply(menu, args);

			// select the first option
			// triggering both mouseover and mouseover because 1.4.2+ has a bug where triggering mouseover
			// will actually trigger mouseenter.  the mouseenter trigger is there for when it's eventually fixed
			this.labels.eq(0).trigger('mouseover').trigger('mouseenter').find('input').trigger('focus');

			button.addClass('ui-state-active');
			this._isOpen = true;
			this._trigger('open');
		},

		// close the menu
		close: function () {
			if (this._trigger('beforeclose') === false) {
				return;
			}

			var o = this.options,
					effect = o.hide,
					speed = this.speed,
					args = [];

			// figure out opening effects/speeds
			if ($.isArray(o.hide)) {
				effect = o.hide[0];
				speed = o.hide[1] || this.speed;
			}

			if (effect) {
				args = [effect, speed];
			}

			$.fn.hide.apply(this.menu, args);
			this.button.removeClass('ui-state-active').trigger('blur').trigger('mouseleave');
			this._isOpen = false;
			this._trigger('close');
		},

		enable: function () {
			this._toggleDisabled(false);
		},

		disable: function () {
			this._toggleDisabled(true);
		},

		checkAll: function (e) {
			this._toggleChecked(true);
			this._trigger('checkAll');
		},

		uncheckAll: function () {
			this._toggleChecked(false);
			this._trigger('uncheckAll');
		},

		getChecked: function () {
			return this.menu.find('input').filter(':checked');
		},

		destroy: function () {
			// remove classes + data
			$.Widget.prototype.destroy.call(this);

			this.button.remove();
			this.menu.remove();
			this.element.show();

			return this;
		},

		isOpen: function () {
			return this._isOpen;
		},

		widget: function () {
			return this.menu;
		},

		getButton: function () {
			return this.button;
		},

		// react to option changes after initialization
		_setOption: function (key, value) {
			var menu = this.menu;

			switch (key) {
				case 'header':
					menu.find('div.ui-multiselect-header')[value ? 'show' : 'hide']();
					break;
				case 'checkAllText':
					menu.find('a.ui-multiselect-all span').eq(-1).text(value);
					break;
				case 'uncheckAllText':
					menu.find('a.ui-multiselect-none span').eq(-1).text(value);
					break;
				case 'height':
					menu.find('ul').last().height(parseInt(value, 10));
					break;
				case 'minWidth':
					this.options[key] = parseInt(value, 10);
					this._setButtonWidth();
					this._setMenuWidth();
					break;
				case 'selectedText':
				case 'selectedList':
				case 'noneSelectedText':
					this.options[key] = value; // these all needs to update immediately for the update() call
					this.update();
					break;
				case 'classes':
					menu.add(this.button).removeClass(this.options.classes).addClass(value);
					break;
				case 'multiple':
					menu.toggleClass('ui-multiselect-single', !value);
					this.options.multiple = value;
					this.element[0].multiple = value;
					this.refresh();
			}

			$.Widget.prototype._setOption.apply(this, arguments);
		}
	});

})(jQuery);

//#endregion End MultiSelect

//#region JQCloud

/*!
 * jQCloud Plugin for jQuery
 *
 * Version 1.0.4
 *
 * Copyright 2011, Luca Ongaro
 * Licensed under the MIT license.
 *
 * Date: 2013-05-09 18:54:22 +0200
*/

(function ($) {
	"use strict";
	$.fn.jQCloud = function (word_array, options) {
		// Reference to the container element
		var $this = this;
		// Namespace word ids to avoid collisions between multiple clouds
		var cloud_namespace = $this.attr('id') || Math.floor((Math.random() * 1000000)).toString(36);

		// Default options value
		var default_options = {
			width: $this.width(),
			height: $this.height(),
			center: {
				x: ((options && options.width) ? options.width : $this.width()) / 2.0,
				y: ((options && options.height) ? options.height : $this.height()) / 2.0
			},
			delayedMode: word_array.length > 50,
			shape: false, // It defaults to elliptic shape
			encodeURI: true,
			removeOverflowing: true
		};

		options = $.extend(default_options, options || {});

		// Add the "jqcloud" class to the container for easy CSS styling, set container width/height
		$this.addClass("jqcloud").width(options.width).height(options.height);

		// Container's CSS position cannot be 'static'
		if ($this.css("position") === "static") {
			$this.css("position", "relative");
		}

		var drawWordCloud = function () {
			// Helper function to test if an element overlaps others
			var hitTest = function (elem, other_elems) {
				// Pairwise overlap detection
				var overlapping = function (a, b) {
					if (Math.abs(2.0 * a.offsetLeft + a.offsetWidth - 2.0 * b.offsetLeft - b.offsetWidth) < a.offsetWidth + b.offsetWidth) {
						if (Math.abs(2.0 * a.offsetTop + a.offsetHeight - 2.0 * b.offsetTop - b.offsetHeight) < a.offsetHeight + b.offsetHeight) {
							return true;
						}
					}
					return false;
				};
				var i = 0;
				// Check elements for overlap one by one, stop and return false as soon as an overlap is found
				for (i = 0; i < other_elems.length; i++) {
					if (overlapping(elem, other_elems[i])) {
						return true;
					}
				}
				return false;
			};

			// Make sure every weight is a number before sorting
			for (var i = 0; i < word_array.length; i++) {
				word_array[i].weight = parseFloat(word_array[i].weight, 10);
			}

			// Sort word_array from the word with the highest weight to the one with the lowest
			word_array.sort(function (a, b) { if (a.weight < b.weight) { return 1; } else if (a.weight > b.weight) { return -1; } else { return 0; } });

			var step = (options.shape === "rectangular") ? 18.0 : 2.0,
					already_placed_words = [],
					aspect_ratio = options.width / options.height;

			// Function to draw a word, by moving it in spiral until it finds a suitable empty place. This will be iterated on each word.
			var drawOneWord = function (index, word) {
				// Define the ID attribute of the span that will wrap the word, and the associated jQuery selector string
				var word_id = cloud_namespace + "_word_" + index,
						word_selector = "#" + word_id,
						angle = 6.28 * Math.random(),
						radius = 0.0,

						// Only used if option.shape == 'rectangular'
						steps_in_direction = 0.0,
						quarter_turns = 0.0,

						weight = 5,
						custom_class = "",
						inner_html = "",
						word_span;

				// Extend word html options with defaults
				word.html = $.extend(word.html, { id: word_id });

				// If custom class was specified, put them into a variable and remove it from html attrs, to avoid overwriting classes set by jQCloud
				if (word.html && word.html["class"]) {
					custom_class = word.html["class"];
					delete word.html["class"];
				}

				// Check if min(weight) > max(weight) otherwise use default
				if (word_array[0].weight > word_array[word_array.length - 1].weight) {
					// Linearly map the original weight to a discrete scale from 1 to 10
					weight = Math.round((word.weight - word_array[word_array.length - 1].weight) /
															(word_array[0].weight - word_array[word_array.length - 1].weight) * 9.0) + 1;
				}
				word_span = $('<span>').attr(word.html).addClass('w' + weight + " " + custom_class);

				// Append link if word.url attribute was set
				if (word.link) {
					// If link is a string, then use it as the link href
					if (typeof word.link === "string") {
						word.link = { href: word.link };
					}

					// Extend link html options with defaults
					if (options.encodeURI) {
						word.link = $.extend(word.link, { href: encodeURI(word.link.href).replace(/'/g, "%27") });
					}

					inner_html = $('<a>').attr(word.link).text(word.text);
				} else {
					inner_html = word.text;
				}
				word_span.append(inner_html);

				// Bind handlers to words
				if (!!word.handlers) {
					for (var prop in word.handlers) {
						if (word.handlers.hasOwnProperty(prop) && typeof word.handlers[prop] === 'function') {
							$(word_span).bind(prop, word.handlers[prop]);
						}
					}
				}

				$this.append(word_span);

				var width = word_span.width(),
						height = word_span.height(),
						left = options.center.x - width / 2.0,
						top = options.center.y - height / 2.0;

				// Save a reference to the style property, for better performance
				var word_style = word_span[0].style;
				word_style.position = "absolute";
				word_style.left = left + "px";
				word_style.top = top + "px";

				while (hitTest(word_span[0], already_placed_words)) {
					// option shape is 'rectangular' so move the word in a rectangular spiral
					if (options.shape === "rectangular") {
						steps_in_direction++;
						if (steps_in_direction * step > (1 + Math.floor(quarter_turns / 2.0)) * step * ((quarter_turns % 4 % 2) === 0 ? 1 : aspect_ratio)) {
							steps_in_direction = 0.0;
							quarter_turns++;
						}
						switch (quarter_turns % 4) {
							case 1:
								left += step * aspect_ratio + Math.random() * 2.0;
								break;
							case 2:
								top -= step + Math.random() * 2.0;
								break;
							case 3:
								left -= step * aspect_ratio + Math.random() * 2.0;
								break;
							case 0:
								top += step + Math.random() * 2.0;
								break;
						}
					} else { // Default settings: elliptic spiral shape
						radius += step;
						angle += (index % 2 === 0 ? 1 : -1) * step;

						left = options.center.x - (width / 2.0) + (radius * Math.cos(angle)) * aspect_ratio;
						top = options.center.y + radius * Math.sin(angle) - (height / 2.0);
					}
					word_style.left = left + "px";
					word_style.top = top + "px";
				}

				// Don't render word if part of it would be outside the container
				if (options.removeOverflowing && (left < 0 || top < 0 || (left + width) > options.width || (top + height) > options.height)) {
					word_span.remove()
					return;
				}


				already_placed_words.push(word_span[0]);

				// Invoke callback if existing
				if ($.isFunction(word.afterWordRender)) {
					word.afterWordRender.call(word_span);
				}
			};

			var drawOneWordDelayed = function (index) {
				index = index || 0;
				if (!$this.is(':visible')) { // if not visible then do not attempt to draw
					setTimeout(function () { drawOneWordDelayed(index); }, 10);
					return;
				}
				if (index < word_array.length) {
					drawOneWord(index, word_array[index]);
					setTimeout(function () { drawOneWordDelayed(index + 1); }, 10);
				} else {
					if ($.isFunction(options.afterCloudRender)) {
						options.afterCloudRender.call($this);
					}
				}
			};

			// Iterate drawOneWord on every word. The way the iteration is done depends on the drawing mode (delayedMode is true or false)
			if (options.delayedMode) {
				drawOneWordDelayed();
			}
			else {
				$.each(word_array, drawOneWord);
				if ($.isFunction(options.afterCloudRender)) {
					options.afterCloudRender.call($this);
				}
			}
		};

		// Delay execution so that the browser can render the page before the computatively intensive word cloud drawing
		setTimeout(function () { drawWordCloud(); }, 10);
		return $this;
	};
})(jQuery);

//#endregion End JQCloud

//#region jQuery UI Touch Punch

 /*!
	* jQuery UI Touch Punch 0.2.3
	* http://touchpunch.furf.com
	*
	* Copyright 2011–2014, Dave Furfero
	* Dual licensed under the MIT or GPL Version 2 licenses.
	*
	* Depends:
	*  jquery.ui.widget.js
	*  jquery.ui.mouse.js
	*/
(function ($) {

    // Detect touch support
    $.support.touch = 'ontouchend' in document;

    // Ignore browsers without touch support
    if (!$.support.touch) {
        return;
    }

    var mouseProto = $.ui.mouse.prototype,
      _mouseInit = mouseProto._mouseInit,
      _mouseDestroy = mouseProto._mouseDestroy,
      touchHandled;

    /**
   * Simulate a mouse event based on a corresponding touch event
   * @param {Object} event A touch event
   * @param {String} simulatedType The corresponding mouse event
   */
    function simulateMouseEvent(event, simulatedType) {

        // Ignore multi-touch events
        if (event.originalEvent.touches.length > 1) {
            return;
        }

        event.preventDefault();

        var touch = event.originalEvent.changedTouches[0],
        simulatedEvent = document.createEvent('MouseEvents');

        // Initialize the simulated mouse event using the touch event's coordinates
        simulatedEvent.initMouseEvent(
      simulatedType,    // type
      true,             // bubbles                    
      true,             // cancelable                 
      window,           // view                       
      1,                // detail                     
      touch.screenX,    // screenX                    
      touch.screenY,    // screenY                    
      touch.clientX,    // clientX                    
      touch.clientY,    // clientY                    
      false,            // ctrlKey                    
      false,            // altKey                     
      false,            // shiftKey                   
      false,            // metaKey                    
      0,                // button                     
      null              // relatedTarget              
    );

        // Dispatch the simulated event to the target element
        event.target.dispatchEvent(simulatedEvent);
    }

    /**
   * Handle the jQuery UI widget's touchstart events
   * @param {Object} event The widget element's touchstart event
   */
    mouseProto._touchStart = function (event) {

        var self = this;

        // Ignore the event if another widget is already being handled
        if (touchHandled || !self._mouseCapture(event.originalEvent.changedTouches[0])) {
            return;
        }

        // Set the flag to prevent other widgets from inheriting the touch event
        touchHandled = true;

        // Track movement to determine if interaction was a click
        self._touchMoved = false;

        // Simulate the mouseover event
        simulateMouseEvent(event, 'mouseover');

        // Simulate the mousemove event
        simulateMouseEvent(event, 'mousemove');

        // Simulate the mousedown event
        simulateMouseEvent(event, 'mousedown');
    };

    /**
   * Handle the jQuery UI widget's touchmove events
   * @param {Object} event The document's touchmove event
   */
    mouseProto._touchMove = function (event) {

        // Ignore event if not handled
        if (!touchHandled) {
            return;
        }

        // Interaction was not a click
        this._touchMoved = true;

        // Simulate the mousemove event
        simulateMouseEvent(event, 'mousemove');
    };

    /**
   * Handle the jQuery UI widget's touchend events
   * @param {Object} event The document's touchend event
   */
    mouseProto._touchEnd = function (event) {

        // Ignore event if not handled
        if (!touchHandled) {
            return;
        }

        // Simulate the mouseup event
        simulateMouseEvent(event, 'mouseup');

        // Simulate the mouseout event
        simulateMouseEvent(event, 'mouseout');

        // If the touch interaction did not move, it should trigger a click
        if (!this._touchMoved) {

            // Simulate the click event
            simulateMouseEvent(event, 'click');
        }

        // Unset the flag to allow other widgets to inherit the touch event
        touchHandled = false;
    };

    /**
   * A duck punch of the $.ui.mouse _mouseInit method to support touch events.
   * This method extends the widget with bound touch event handlers that
   * translate touch events to mouse events and pass them to the widget's
   * original mouse event handling methods.
   */
    mouseProto._mouseInit = function () {

        var self = this;

        // Delegate the touch handlers to the widget's element
        self.element.bind({
            touchstart: $.proxy(self, '_touchStart'),
            touchmove: $.proxy(self, '_touchMove'),
            touchend: $.proxy(self, '_touchEnd')
        });

        // Call the original $.ui.mouse init method
        _mouseInit.call(self);
    };

    /**
   * Remove the touch event handlers
   */
    mouseProto._mouseDestroy = function () {

        var self = this;

        // Delegate the touch handlers to the widget's element
        self.element.unbind({
            touchstart: $.proxy(self, '_touchStart'),
            touchmove: $.proxy(self, '_touchMove'),
            touchend: $.proxy(self, '_touchEnd')
        });

        // Call the original $.ui.mouse destroy method
        _mouseDestroy.call(self);
    };

})(jQuery);

//#endregion End jQuery UI Touch Punch

//#region plug-in name goes here

//#endregion End custom plug-in


//#endregion End javascript libraries and jQuery plug-ins