
//#region equalHeights, equalSize

/**
* equalHeights: Make all elements same height according to tallest one in the collection
* equalSize: Make all elements same width & height according to widest and tallest one in the collection
*/
(function ($) {
	jQuery.fn.equalHeights = function (hBuffer) {
		hBuffer = hBuffer || 0;
		return this.height(hBuffer + Math.max.apply(null,
			this.map(function () {
				return jQuery(this).height();
			}).get()
		));
	};

	jQuery.fn.equalWidths = function (wBuffer) {
		wBuffer = wBuffer || 0;
		if ($.support.cssFloat || jQuery(".mds_i_c", this).length == 0) {
			return this.width(wBuffer + Math.max.apply(null,
				this.map(function () {
					return jQuery(this).width();
				}).get()
			));
		}
		else {
			// Hack for IE7, which makes floated divs that do not have a width assigned 100% wide.
			// We'll grab the width of the child div tag having class 'mds_i_c' and use that as the
			// basis for our width calculation. (Height calculation remains the same)
			return this.width(wBuffer + 10 + Math.max.apply(null,
				this.map(function () {
					return jQuery(".mds_i_c", this).width();
				}).get()
			));
		}
	};

	jQuery.fn.equalSize = function (wBuffer, hBuffer) {
		wBuffer = wBuffer || 0;
		hBuffer = hBuffer || 0;
		if ($.support.cssFloat || jQuery(".mds_i_c", this).length == 0) {
			return this.width(wBuffer + Math.max.apply(null,
				this.map(function () {
					return jQuery(this).width();
				}).get()
			)).height(hBuffer + Math.max.apply(null,
				this.map(function () {
					return jQuery(this).height();
				}).get()
			));
		}
		else {
			// Hack for IE7, which makes floated divs that do not have a width assigned 100% wide.
			// We'll grab the width of the child div tag having class 'mds_i_c' and use that as the
			// basis for our width calculation. (Height calculation remains the same)
			return this.height(hBuffer + Math.max.apply(null,
				this.map(function () {
					return jQuery(this).height();
				}).get()
			)).width(wBuffer + 10 + Math.max.apply(null,
				this.map(function () {
					return jQuery(".mds_i_c", this).width();
				}).get()
			));
		}
	};
})(jQuery);

//#endregion
