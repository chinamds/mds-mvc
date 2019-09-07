fabric.NamedPattern = fabric.util.createClass(fabric.Pattern, {

    type: 'named-pattern',

    initialize: function (element, options) {
        this.callSuper('initialize', element, options);
        options && this.set('name', options.name);
    },

    toObject: function () {
        return fabric.util.object.extend(this.callSuper('toObject'), { name: this.name });
    }
});

fabric.NamedPattern.fromObject = function (object, callback) {
    fabric.util.loadImage(object.src, function (img) {
        callback && callback(new fabric.NamedPattern(img, object));
    });
};

jQuery.fn.filter = function () {
    $(this).keydown(function (e) {
        char = String.fromCharCode(e.which);
        //alert(e.keyCode);
        var n = e.keyCode;
        if (n == 190) { //period for decimal 
            return;
        }
        if (e.shiftKey || e.ctrlKey || e.altKey) { // if shift, ctrl or alt keys held down 
            e.preventDefault();         // Prevent character input 
        } else {
            //var n = e.keyCode;
            if (!((n == 8)              // backspace 
        || (n == 48)                // 0 pad 
        || (n == 46)                // delete 
        || (n >= 35 && n <= 40)     // arrow keys/home/end 
        || (n >= 48 && n <= 57)     // numbers on keyboard 
        || (n >= 96 && n <= 105))   // number on keypad 
        || (char == "0")            // can't have strokeWidth = 0
        ) {
                e.preventDefault();     // Prevent character input 
            }
        }
    });
}

