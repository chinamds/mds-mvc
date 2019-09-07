<script type="text/javascript">

    var selectedArea = '';
    var mapFile = '${ctx}/static/scripts/contenteditor/data/data1.js';
    var activeFigure;
    var activeNodes;

    var currentColor = "#FFFFFF";  
    var currentOpacity = 0.7;  
    var mapKey = "home1";
    var gText = "";

    var canvas
    var shapes = [];
    var mode = "";
    var currentPoly;
    var lastPoints;
    var lastPos;
    var src = "${ctx}/static/uploads/image1.png";
    var imgMap = null;
    var copiedObjects = new Array();
    var canvasScale = 1.000000000000000;
    var SCALE_FACTOR = 1.200000000000000;
    var gLeft = 0;
    var gTop = 0;

    var servervalue = 0;
    var reducedSize = false;
    var resizeTime = 100;     // total duration of the resize effect, 0 is instant
    var resizeDelay = 100;    // time to wait before checking the window size again

    var imageid = "image1.png";

    function handleFileSelect(evt) {
        var _path = "";
        var files = evt.target.files; // FileList object
        // Loop through the FileList and render image files as thumbnails.
        for (var i = 0, f; f = files[i]; i++) {
            if (!f.type.match('image.*')) {
                continue;
            }
            else {
                _path = f.name;
            }
            var reader = new FileReader();
            reader.onload = (function (theFile) {
                return function (e) {
                    imageid = _path;
                    $('#mapImage').attr('src', e.target.result);
                    var w = $("#mapImage").width();
                    var h = $("#mapImage").height();
                    var tempImg = new Image();
                    tempImg.src = reader.result;
                    tempImg.onload = function(){
                        window.location = "${ctx}/cm/contenteditor?imageid=" +imageid + "&w=" + tempImg.width + "&h=" + tempImg.height;
                    };
                };
            })(f);

            // for "imageid" read in image file as a data URL
            reader.readAsDataURL(f);
        }
    }


    $(document).ready(function () {  
            
        ////$("#gdRows tr:has(td)").hover(function() {    
        //$("#imgshape").hover(function() { 
        //$(this).css("cursor", "pointer");      
        //});

        /* BEGIN - CREATE SCROLLING DROPDOWN LIST OF PATTERNS */
        var s = '${ctx}/static/scripts/contenteditor/data/colors.js';
        var items = [];
        $('#ddPatterns').empty();
        $.getJSON(s, function (d) {
            // pattern can be sub-divided into "styles" of patterns
            // your data file might reak up your patterns into "styles" 
            // below we have selcted the style "Category_3tab" of patterns
            $.each(d["Category_3tab"], function (i, item) {
                items.push('<li><a href="#"><img src="${ctx}/static/scripts/contenteditor/images/' + item.image + '" />' + item.title + '</a></li>');
            }); // close each()
            $('#ddPatterns').append(items.join(''));
        });
        /* END - CREATE SCROLLING DROPDOWN LIST OF PATTERNS */
          
        $("#files").on("change", handleFileSelect);
  
        $('input:text').bind("keydown", function(e) {
            var n = $("input:text").length;
            if (e.which == 13) 
            { //Enter key
            	e.preventDefault(); //Skip default behavior of the enter key
            }
        });

        //return 'rgb(' + (Math.floor(Math.random() * 256)) + ',' + (Math.floor(Math.random() * 256)) + ',' + (Math.floor(Math.random() * 256)) + ')';

        function rgbToHex(r, g, b) {
            if (r > 255 || g > 255 || b > 255)
                throw "Invalid color component";
            return ((r << 16) | (g << 8) | b).toString(16);
        }

        function HexToRGB(Hex) {
            var Long = parseInt(Hex.replace(/^#/, ""), 16);
            return {
                R: (Long >>> 16) & 0xff,
                G: (Long >>> 8) & 0xff,
                B: Long & 0xff
            };
        }

        function randomHex() {
            var r = (Math.floor(Math.random() * 256));
            var g = (Math.floor(Math.random() * 256));
            var b = (Math.floor(Math.random() * 256));
            return "#" + ((r << 16) | (g << 8) | b).toString(16);
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        // ===== START EDIT CONTROLS TOOLBAR =====================================================
        // REMOVE EDIT CONTROLS BELOW TO USE AN IMAGEMAP WHERE YOU ARE JUST PAINTING MAP SECTIONS
        //////////////////////////////////////////////////////////////////////////////////////////


        // We are NOT going to use sub-classing!
        //fabric.NamedImage = fabric.util.createClass(fabric.Image, {
        //        type: 'named-image',
        //        initialize: function(element, options) {
        //        this.callSuper('initialize', element, options);
        //        options && this.set('name', options.name);
        //    },

        //    toObject: function() {
        //        return fabric.util.object.extend(this.callSuper('toObject'), { name: this.name });
        //    }
        //});

        //fabric.NamedImage.fromObject = function(object, callback) {
        //    fabric.util.loadImage(object.src, function(img) {
        //        callback && callback(new fabric.NamedImage(img, object));
        //    });
        //};

        $("#filestuff a").click(function (e) {
            e.preventDefault(); //prevent synchronous loading
            var trial = $(this).text();

            //////////////////////////////////////////////////////////////////
            ////////////// LOAD NEW IMAGE ////////////////////////////////////
            //////////////////////////////////////////////////////////////////
            if (trial == "Load Image") {
                if (window.File && window.FileReader && window.FileList && window.Blob) {
                    // Great success! All the File APIs are supported.
                } else {
                    alert('Opening files are NOT supported in this browser.');
                    return;
                }
                $("#files").click();
            }

            //////////////////////////////////////////////////////////////////
            ////////////// SHOW HTML FOR IMAGE MAP IN DIALOG /////////////////
            //////////////////////////////////////////////////////////////////
            if (trial == "Show Image Map Html") {
                $('#btnZoom img[id="btnResetZoom"]').click();
                $("#btnShowHtmlMap").click();
            }

            //////////////////////////////////////////////////////////////////
            ///////// SAMPLES LOADING FROM CUSTOM & JSON DATA FILES //////////
            //////////////////////////////////////////////////////////////////
            if (trial == "Sample #1: Load Map from Custom Data") {
                $('#btnZoom img[id="btnResetZoom"]').click();
                $("#btnSampleMapCustom").click();
            }
            if (trial == "Sample #2: Load map from JSON Data") {
                $('#btnZoom img[id="btnResetZoom"]').click();
                $("#btnSampleMapJSON").click();
            }
            
            //////////////////////////////////////////////////////////////////
            //////////////// SHOW DATA IN DIALOG /////////////////////////////
            //////////////////////////////////////////////////////////////////
            if (trial == "Show Objects Custom Data") {
                $('#btnZoom img[id="btnResetZoom"]').click();
                $("#btnShowCustomData").click();
            }
            if (trial == "Show Objects JSON Data") {
                $('#btnZoom img[id="btnResetZoom"]').click();
                $("#btnShowJSONData").click();
            }

            //enclose in single quotes
            //var jsonObj = '{"TeamList" : [{"teamid" : "1","teamname" : "Barcelona"}]}';
            //var obj = $.parseJSON(jsonObj);
            //var obj= eval('"(" + jsonObj + ")"');
          
            //////////////////////////////////////////////////////////////////
            //////////////// SERIALIZE JSON TO LOCAL STORAGE /////////////////
            //////////////////////////////////////////////////////////////////
            if (trial == "Save JSON Local Storage") {
                if (typeof(localStorage) == 'undefined' ) {
                    alert('Your browser does not support HTML5 localStorage. Try upgrading.');
                    return;
                } 
                $('#btnZoom img[id="btnResetZoom"]').click();
                $("#btnSaveLocalStorage").click();
            }
            if (trial == "Load JSON Local Storage") {
                if (typeof(localStorage) == 'undefined' ) {
                    alert('Your browser does not support HTML5 localStorage. Try upgrading.');
                    return;
                } 
                $("#btnLoadLocalStorage").click();
            }

            //////////////////////////////////////////////////////////////////
            ///////// YOU CAN ADD THIS FOR LOADING IN WEB BROWSER ////////////
            //////////////////////////////////////////////////////////////////
            if (trial == "Save JSON File") {
                try{
                    alert("This method will only work with simple objects!\r\nApp will NOT work correctly after you select this!");
                    window.external.SaveJSONData(JSON.stringify(canvas));
                }
                catch(e){
                    alert('This only works when page is loaded in WebBrowser control.');
                }
                finally{
                }
            }                    
            if (trial == "Load JSON File") {
                try{
                    alert("This method will only work with simple objects!\r\nIt will lock up this app if you have applied a pattern!\r\nSo go ahead and try it!");
                    window.external.LoadJSONData();
                }
                catch(e){
                    alert('This only works when page is loaded in WebBrowser control.');
                }
                finally{
                }
            }

        });

        $('#closepolygon').hide();

    });

    //$(window).load(function () {
    window.onload = function() {
        initContentEditor();
        currentColor = document.getElementById("color").value;

        $('#btnZoom a').on('click', function (e) {
            e.preventDefault(); 
            var trial = $(this).text();
            if (trial == "Zoom In (CTRL Plus)") {
                zoomIn();
            }
            if (trial == "Zoom Out (CTRL Minus)") {
                zoomOut();
            }
            if (trial == "Zoom 1:1 (CTRL Zero)") {
                resetZoom();
            }
        });

        // button Zoom In
        $("#btnZoomIn").on('click', function (e) {
            zoomIn();
        });
        // button Zoom Out
        $("#btnZoomOut").on('click', function (e) {
            zoomOut();
        });
        // button Reset Zoom
        $("#btnResetZoom").on('click', function (e) {
            resetZoom();
        });

        // Zoom In
        function zoomIn() {
            // limiting the canvas zoom scale 
            //1.200000000000000
            if (canvasScale < 4.900000000000000) {
                canvasScale = canvasScale * SCALE_FACTOR;
                
                canvas.setHeight(canvas.getHeight() * SCALE_FACTOR);
                canvas.setWidth(canvas.getWidth() * SCALE_FACTOR);
                canvas.setZoom(canvasScale);
                
                return;
            }
        }

        // Zoom Out
        function zoomOut() {
            // limiting the zoom out scale 
            if (canvasScale > .200000000000000) {  

                canvasScale = canvasScale / SCALE_FACTOR;
                
                canvas.setHeight(canvas.getHeight() * (1.000000000000000 / SCALE_FACTOR));
                canvas.setWidth(canvas.getWidth() * (1.000000000000000 / SCALE_FACTOR));
                canvas.setZoom(canvasScale);
                
                return;
            }
        }

        // Reset Zoom
        function resetZoom() {

        	canvasScale = 1.00;
            canvas.setHeight(canvas.getHeight() * (1 / canvasScale));
            canvas.setWidth(canvas.getWidth() * (1 / canvasScale));
            canvas.setZoom(1 / canvasScale);
            
            return;

            var objects = canvas.getObjects();
            /*if (canvas.backgroundImage != null){
            	objects.push(canvas.backgroundImage);
            }*/
            for (var i in objects) {
                var scaleX = objects[i].scaleX;
                var scaleY = objects[i].scaleY;
                var left = objects[i].left;
                var top = objects[i].top;

                var tempScaleX = scaleX * (1 / canvasScale);
                var tempScaleY = scaleY * (1 / canvasScale);
                var tempLeft = left * (1 / canvasScale);
                var tempTop = top * (1 / canvasScale);

                objects[i].scaleX = tempScaleX;
                objects[i].scaleY = tempScaleY;
                objects[i].left = tempLeft;
                objects[i].top = tempTop;

                objects[i].setCoords();
            }

            canvas.renderAll();
            canvasScale = 1.00;
            canvas.calcOffset();
        }

    };

    function LodWebBrowserControlData(data) {
        //alert(data);
        canvas.loadFromJSON(data);
    }

    $.fx.speeds._default = 1000;


    function LodWebBrowserControlData(data) {
        //alert(data);
        canvas.loadFromJSON(data);
    }

    function initContentEditor() {
        ////////////////////////// GET IMAGE TO LOAD FROM PREVIOUS PAGE PASSED AS URL PARAMETER ////////////////////////////
        var allVars = $.getUrlVars();
        imageid = $.getUrlVar('imageid');
        if (_.isUndefined(imageid) || _.isNull(imageid) || $.isEmptyObject(imageid)) {
            imageid = "image1.png" 
        }
        var zw = $.getUrlVar('w');
        if (_.isUndefined(zw) || _.isNull(zw) || $.isEmptyObject(zw)) {
            zw = 993;
        }
        var zh = $.getUrlVar('h');
        if (_.isUndefined(zh) || _.isNull(zh) || $.isEmptyObject(zh)) {
            zh = 400;
        }

        var backgroundImage = "${ctx}/static/uploads/" + imageid;
        mapFile = "${ctx}/static/scripts/contenteditor/data/data1.js";
        $('#mapImage').attr('src', backgroundImage);

        $('#editor').attr('width', zw);
        $('#editor').attr('height', zh);

        ////////////////////////// END GET IMAGE TO LOAD FROM PREVIOUS PAGE PASSED AS URL PARAMETER ////////////////////////

        fabric.isTouchSupported = window.Mds.isTouchScreen();
        global = this;
        global.canvas = new fabric.Canvas('editor', { selection: false, 
        											  allowTouchScrolling: true}); 
        canvas = global.canvas;

        fabric.Object.NUM_FRACTION_DIGITS = 10;

        canvas.setBackgroundImage(backgroundImage, canvas.renderAll.bind(canvas));
        $('#mapImage').css('visibility', 'hidden');

        $('#ddPatterns li').on('click', function (e) {
            e.preventDefault;
            this.blur();

            if(selectedArea.length <1) {
                alert("You must first select an area to apply pattern to!");
                return;
            }
            var imgPath = $(this).find('img').attr("src");
            SetMapSectionPattern(selectedArea, imgPath);    
        });

        $('#ddAreas a').on('click', function (e) {
            e.preventDefault;
            this.blur();
            selectedArea = $(this).text();
            $('#txtAreaSelected').val(selectedArea);    //for input
            //$('#txtAreaSelected').text(selectedArea); //for label
        });
         
        $('.minicolors').each( function() {
            $(this).minicolors({
                control: $(this).attr('data-control') || 'wheel',
                defaultValue: $(this).attr('data-default-value') || '',
                inline: $(this).hasClass('inline'),
                letterCase: $(this).hasClass('uppercase') ? 'uppercase' : 'lowercase',
                opacity: $(this).hasClass('opacity'),
                position: $(this).attr('data-position') || 'default',
                styles: $(this).attr('data-style') || '',
                swatchPosition: $(this).attr('data-swatch-position') || 'left',
                textfield: !$(this).hasClass('no-textfield'),
                theme: $(this).attr('data-theme') || 'default',
                hide: function() {
                    var myObj = canvas.getActiveObject();
                    if (!_.isUndefined(myObj) && !_.isNull(myObj)) {
                        myObj.fill = currentColor;
                        canvas.renderAll();
                        canvas.calcOffset()
                    }
                },
                open: function (hex, rgba) {
                },
                close: function (hex, rgba) {
                    currentColor = document.getElementById("color").value;
                    var myObj = canvas.getActiveObject();
                    if (!_.isUndefined(myObj) && !_.isNull(myObj)) {
                        myObj.fill = currentColor;
                        canvas.renderAll();
                        canvas.calcOffset()
                    }
                },
                change: function(hex, opacity) {
                    currentColor = document.getElementById("color").value;
                    var myObj = canvas.getActiveObject();
                    if (!_.isUndefined(myObj) && !_.isNull(myObj)) {
                        myObj.fill = currentColor;
                        canvas.renderAll();
                        canvas.calcOffset()
                    }
                }
            });
                
        });

        $('#btnChangeColor').click(function () {
            activeFigure = canvas.getActiveObject();
            if (!_.isUndefined(activeFigure) && !_.isNull(activeFigure)) {
                activeFigure.fill = currentColor;
                var obj = {
                    fill: activeFigure.fill,
                };
                canvas.renderAll();
                canvas.calcOffset()
                clearNodes();
            }
        });
        
        $('#btnCircle').click(function (e) {
            e.preventDefault;
            this.blur();
            figureType = "circle";
            deselect();      
        });
        
        $('#btnEllipse').click(function (e) {
            e.preventDefault;
            this.blur();
            figureType = "ellipse";
            deselect();
        });

        $('#btnSquare').click(function (e) {
            e.preventDefault;
            this.blur();
            figureType = "square";
            deselect(); 
        });
        
        $('#btnPolygon').click(function (e) {
            e.preventDefault;
            this.blur();
            figureType = "polygon";
            deselect();
        });

        $('#btnPolygonClose').click(function (e) {
            e.preventDefault;
            this.blur();
            figureType = "polygon";
            currentPoly.selectable = true;
            currentPoly.fill = currentColor;
            currentPoly.opacity = currentOpacity;
            activeFigure = currentPoly;
            currentPoly = null;
            canvas.setActiveObject(activeFigure);
            $('#closepolygon').hide();
            figureType = "";
        });     
        
        $("#btnWebcam").on('click', function (e) {
            e.preventDefault;
            this.blur();
/*            var webcamEl = document.getElementById('webcam');
            var webcam = new fabric.Image(webcamEl, {
              left: 539,
              top: 328,
              angle: 94.5,
              originX: 'center',
              originY: 'center'
            });

            // adding webcam video element
            getUserMedia({video: true}, function getWebcamAllowed(localMediaStream) {
              var video = document.getElementById('webcam');
              video.src = window.URL.createObjectURL(localMediaStream);

              canvas.add(webcam);
              webcam.moveTo(0); // move webcam element to back of zIndex stack
              webcam.getElement().play();
            }, function getWebcamNotAllowed(e) {
              // block will be hit if user selects "no" for browser "allow webcam access" prompt
            });
            
          // 	making navigator.getUserMedia cross-browser compatible
           function getUserMedia() {
              var userMediaFunc = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;
              if (userMediaFunc) userMediaFunc.apply(navigator, arguments);
            }*/
            
            Webcam.set({
    			width: 320,
    			height: 240,
    			dest_width: 640,
    			dest_height: 480,
    			image_format: 'jpeg',
    			jpeg_quality: 90,
    			autoplay:false
    		});
            Webcam.attach('#camera');
            /*Webcam.video.width=500;
            Webcam.video.height=360;*/
    		var webcam = new fabric.Image(Webcam.video, {
                left: 539,
                top: 328,
                width:640,
                height:480,
                angle: 94.5,
                originX: 'center',
                originY: 'center'
              });
    		
    		canvas.add(webcam);
            webcam.moveTo(0); // move webcam element to back of zIndex stack
            webcam.getElement().play();

            fabric.util.requestAnimFrame(function render() {
              canvas.renderAll();
              fabric.util.requestAnimFrame(render);
            });         
        });
        
        //btnCamera
        $("#btnCamera").on('click', function (e) {
            e.preventDefault;
            this.blur();
            Webcam.set({
    			width: 320,
    			height: 240,
    			dest_width: 640,
    			dest_height: 480,
    			image_format: 'jpeg',
    			jpeg_quality: 90
    		});
    		Webcam.attach('#camera');
    		
    		// take snapshot and get image data
			Webcam.snap( function(data_uri) {
				// display results in page
				fabric.Image.fromURL(data_uri, function(oImg) {
					  canvas.add(oImg);
				});
			});
        }); //btnCamera

        // inner <li id="zspecial">
        $('#zspecial').on('click', function (e) {
            e.preventDefault;
            this.blur();
            //var zzz = $(this).text();
            e.stopPropagation();
        });

        $('#ddrefresh').on('click', function (e) {
            $('#btnCreateAreasDropDown').click();
            e.stopPropagation();
        });


        // <a href="#" id="ddproperties"
        $('#ddproperties').on('click', function (e) {
            e.preventDefault;
            this.blur();
            activeFigure = canvas.getActiveObject();
            if (activeFigure){
                $('#txtMapKey').val(mapKey); //this is NOT a proprty of the activeFigure, one value for whole map!!!
                $('#txtMapValue').val(activeFigure.mapValue);
                $('#hrefBox').val(activeFigure.link);
                $('#txtAltValue').val(activeFigure.alt);
                $('#txtStrokeColor').val(activeFigure.stroke);
                $('#txtStrokeWidth').val(activeFigure.strokeWidth);

                $('input[id=cb_selectable]').attr('checked', activeFigure.selectable);
                $('input[id=cb_hasControls]').attr('checked', activeFigure.hasControls);
                $('input[id=cb_lockMovementX]').attr('checked', activeFigure.lockMovementX);
                $('input[id=cb_lockMovementY]').attr('checked', activeFigure.lockMovementY);
                $('input[id=cb_lockScaling]').attr('checked', activeFigure.lockScaling);
                $('input[id=cb_lockRotation]').attr('checked', activeFigure.lockRotation);
                $('input[id=cb_hasRotatingPoint]').attr('checked', activeFigure.hasRotatingPoint);
                $('input[id=cb_transparentCorners]').attr('checked', activeFigure.transparentCorners);
                $('input[id=cb_hasBorders]').attr('checked', activeFigure.hasBorders);
                $('input[id=cb_perPixelTargetFind]').attr('checked', activeFigure.perPixelTargetFind);
            }
            
        });

        // button in <li> dropdown
        $("#btnUpdate").on('click', function (e) {
            e.preventDefault;
            this.blur();
            activeFigure = canvas.getActiveObject();

            if (activeFigure){
                mapkey = $('#txtMapKey').val();
                activeFigure.mapKey = $('#txtMapKey').val();
                activeFigure.mapValue = $('#txtMapValue').val();
                activeFigure.alt = $('#txtAltValue').val();
                activeFigure.link = $('#hrefBox').val();
                //opacity
                activeFigure.stroke = $('#txtStrokeColor').val();
                activeFigure.strokeWidth = $('#txtStrokeWidth').val();

                if ($('input[id=cb_selectable]:checked').attr("checked") != "undefined" && $('input[id=cb_selectable]:checked').attr("checked") == "checked") {
                    activeFigure.selectable = true;
                }
                else {
                    activeFigure.selectable = false;
                }

                if ($('input[id=cb_hasControls]:checked').attr("checked") != "undefined" && $('input[id=cb_hasControls]:checked').attr("checked") == "checked") {
                    activeFigure.hasControls = true;
                }
                else {
                    activeFigure.hasControls = false;
                }                   
                if ($('input[id=cb_lockMovementX]:checked').attr("checked") != "undefined" && $('input[id=cb_lockMovementX]:checked').attr("checked") == "checked") {
                    activeFigure.lockMovementX = true;
                }
                else {
                    activeFigure.lockMovementX = false;
                }                   
                if ($('input[id=cb_lockMovementY]:checked').attr("checked") != "undefined" && $('input[id=cb_lockMovementY]:checked').attr("checked") == "checked") {
                    activeFigure.lockMovementY = true;
                }
                else {
                    activeFigure.lockMovementY = false;
                }                   
                if ($('input[id=cb_lockScaling]:checked').attr("checked") != "undefined" && $('input[id=cb_lockScaling]:checked').attr("checked") == "checked") {
                    activeFigure.lockScaling = true;
                }
                else {
                    activeFigure.lockScaling = false;
                }                   
                if ($('input[id=cb_lockRotation]:checked').attr("checked") != "undefined" && $('input[id=cb_lockRotation]:checked').attr("checked") == "checked") {
                    activeFigure.lockRotation = true;
                }
                else {
                    activeFigure.lockRotation = false;
                }                   
                if ($('input[id=cb_hasRotatingPoint]:checked').attr("checked") != "undefined" && $('input[id=cb_hasRotatingPoint]:checked').attr("checked") == "checked") {
                    activeFigure.hasRotatingPoint = true;
                }
                else {
                    activeFigure.hasRotatingPoint = false;
                }                   
                if ($('input[id=cb_transparentCorners]:checked').attr("checked") != "undefined" && $('input[id=cb_transparentCorners]:checked').attr("checked") == "checked") {
                    activeFigure.transparentCorners = true;
                }
                else {
                    activeFigure.transparentCorners = false;
                }                   
                if ($('input[id=cb_hasBorders]:checked').attr("checked") != "undefined" && $('input[id=cb_hasBorders]:checked').attr("checked") == "checked") {
                    activeFigure.hasBorders = true;
                }
                else {
                    activeFigure.hasBorders = false;
                }                   
                if ($('input[id=cb_perPixelTargetFind]:checked').attr("checked") != "undefined" && $('input[id=cb_perPixelTargetFind]:checked').attr("checked") == "checked") {
                    activeFigure.perPixelTargetFind = true;
                }
                else {
                    activeFigure.perPixelTargetFind = false;
                }                   
            }
            else {
                alert("No Object Selected!?");
            }
            canvas.renderAll();
            canvas.calcOffset()
            clearNodes();
            $('#ddproperties').parent().removeClass("open");
        });

        $("#fs").on("change", function (e) {
            $(".changeMe").css("font-family", $(this).val());
        });

        $("#size").on("change", function (e) {
            $(".changeMe").css("font-size", $(this).val() + "px");
        });

        // inner <li id="liText">
        $('#liText').on('click', function (e) {
            e.preventDefault;
            this.blur();
            e.stopPropagation();
        });

        // <a href="#" id="ddtext"
        $('#ddtext').on('click', function (e) {
            e.preventDefault;
            this.blur();
            $('#text2add').val('');
        });

        // button in <li> dropdown
        $("#btnAddText").on('click', function (e) {
            e.preventDefault;
            this.blur();
            gText = $('#text2add').val();
            var shape = new fabric.Text(gText, {
                fontFamily: $("#fs").val(),
                fontSize: parseInt($("#size").val()),
                fontWeight: "bold",
                left: canvas.width/2,
                top: canvas.height/2,
                lineHeight: 1,
                originX: "left"
            });
            shape.figureType = "text";
            canvas.add(shape);
            shapes.push(shape);
            //deselect();
            canvas.renderAll();
            canvas.calcOffset()
            clearNodes();
            $('#ddtext').parent().removeClass("open");
        });

        // Bill SerGio - this is how to add an image as a fabric element
        //fabric.util.loadImage(src, function (img) {
        //    imgMap = new fabric.Image(img);
        //    imgMap.set({
        //        left: imgMap.width / 2,
        //        top: imgMap.height / 2
        //    });

        //    imgMap.toObject = (function(toObject) {
        //        return function() {
        //        return fabric.util.object.extend(toObject.call(this), {
        //            mapKey: this.mapKey,
        //            link: this.link,
        //            alt: this.alt,
        //            mapValue: this.mapValue,
        //            pattern: this.pattern,
        //            lockMovementX: this.lockMovementX,
        //            lockMovementY: this.lockMovementY,
        //            lockScaling: this.lockScaling,
        //            lockRotation: this.lockRotation
        //        });
        //        };
        //    })(imgMap.toObject);
        //    imgMap.mapKey = mapKey;
        //    imgMap.link = '#';
        //    imgMap.alt = '';
        //    imgMap.mapValue = 'notitle';
        //    imgMap.pattern = '';
        //    gLeft = imgMap.left;
        //    gTop = imgMap.top;
        //    imgMap.hasRotatingPoint = true;
        //    imgMap.scaleX = imgMap.scaleY = 1.00;
        //    imgMap.hasControls = false;
        //    imgMap.selectable = false;
        //    canvas.add(imgMap);
        //    shapes.push(imgMap);
        //    canvas.renderAll();
        //    imgMap.sendToBack();
        //    canvas.calcOffset();
        //});

        $('#btnDelete').click(function (e) {
            e.preventDefault;
            this.blur();           
            var myObj = canvas.getActiveObject();
            shapes = _.without(shapes, myObj);
            canvas.remove(myObj);
            deselect();
        });

        $("#btnEraser").on('click', function (e) {
            //canvas.clear();
            //var objects = canvas.getObjects();
            canvas.forEachObject(function(object){
                shapes = _.without(shapes, object);
                canvas.remove(object);
            });
            canvas.renderAll();
            canvas.calcOffset()
            clearNodes();
        });

        fabric.Image.filters.Redify = fabric.util.createClass({
          type: 'Redify',
          applyTo: function(canvasEl) {
            var context = canvasEl.getContext('2d'),
              imageData = context.getImageData(0, 0, 
                canvasEl.width, canvasEl.height),
              data = imageData.data;
            for (var i = 0, len = data.length; i < len; i += 4) {
              data[i + 1] = 0;
              data[i + 2] = 0;
            }
            context.putImageData(imageData, 0, 0);
          }
        });
        fabric.Image.filters.Redify.fromObject = function(object) {
          return new fabric.Image.filters.Redify(object);
        };

        // sample animation of objects
        $('#btnAnimate a').click(function (e) {
            e.preventDefault;
            this.blur();
            var trial = $(this).text();
            if (trial == "Spin Selected Object") {
                var obj = canvas.getActiveObject();
                if (_.isUndefined(obj) || _.isNull(obj)) {
                    return false;
                }
                obj.set('angle', 0);
                //obj.animate('angle', 360, {
                //    onChange: canvas.renderAll.bind(canvas)
                //})
                obj.animate({ angle: 360 }, {
                    duration: 2000,
                    easing: fabric.util.ease.easeOutCubic,
                    onChange: canvas.renderAll.bind(canvas)
                });
                canvas.renderAll();
                canvas.calcOffset();
            }
            if (trial == "Cycle Patterns for MapValue") {
                $("#btnRotatePaterns").click();
            }         
        });


        $('#btnSelect a').on('click', function (e) {
            e.preventDefault;
            this.blur(); 
            //canvas.selection = true;
            var trial = $(this).text();
            if (trial == "Copy Object (CTRL C)") {
                copy();
            }
            if (trial == "Paste Object (CTRL V)") {
                paste();
            }
            if (trial == "Delete Object") {
                e.preventDefault;
                this.blur();
                var myObj = canvas.getActiveObject();
                shapes = _.without(shapes, myObj);
                canvas.remove(myObj);
                deselect();
            }
            if (trial == "Erase All Objects") {
                //canvas.clear();
                var objects = canvas.getObjects();
                canvas.forEachObject(function(object){
                    shapes = _.without(shapes, object);
                    canvas.remove(object);
                });
                canvas.renderAll();
                canvas.calcOffset()
                clearNodes();
            }
            if (trial == "Select All Objects") {
                var objects = canvas.getObjects();
                canvas.forEachObject(function(object){
                    object.lockRotation = false;
                    object.selectable = true;
                    object.hasBorders = true;
                    object.hasControls = true;
                    object.hasRotatingPoint = true;
                    object.perPixelTargetFind = true;

                    object.lockMovementX = false;
                    object.lockMovementY = false;
                    object.lockRotation = false;
                    object.lockScaling = false; 
                    object.hasRotatingPoint = true;

                    object.borderColor = 'gray';
                    object.cornerColor = 'black';
                    object.cornerSize = 12;
                    object.transparentCorners = true;
                    canvas.setActiveObject(object);

                    canvas.renderAll();
                    canvas.calcOffset();
                    //clearNodes();
                });
            }
            if (trial == "Lock All Objects") {        
                var objects = canvas.getObjects();
                canvas.forEachObject(function(object){
                    object.lockRotation = true;
                    object.selectable = false;
                    object.hasBorders = false;
                    object.hasControls = false;
                    object.hasRotatingPoint = false;
                    object.perPixelTargetFind = false;

                    object.lockMovementX = true;
                    object.lockMovementY = true;
                    object.lockRotation = true;
                    object.lockScaling = true; 
                    object.hasRotatingPoint = false;

                    canvas.setActiveObject(object);

                    canvas.renderAll();
                    canvas.calcOffset();
                    //clearNodes();
                });                  
            } 

            //There's 4 methods in Fabric to control z-index: 
            //bringForward (1 level up), 
            //bringToFront (all the way up), 
            //sendBackwards (1 level down), 
            //sendToBack (all the way down). 
            if (trial == "Bring Forward") {                    
                e.preventDefault;
                this.blur();
                var myObj = canvas.getActiveObject();
                if (myObj) {
                    myObj.bringForward();
                    deselect();   
                }       
            }
            if (trial == "Bring To Front") {          
                e.preventDefault;
                this.blur();
                var myObj = canvas.getActiveObject();
                if (myObj) {
                    myObj.bringToFront();
                    deselect();   
                }           
            }          
            if (trial == "Send Backwards") {                    
                e.preventDefault;
                this.blur();
                var myObj = canvas.getActiveObject();
                if (myObj) {
                    myObj.sendBackwards();
                    deselect();  
                }       
            }
            if (trial == "Send To Back") {          
                e.preventDefault;
                this.blur();
                var myObj = canvas.getActiveObject();
                if (myObj) {
                    myObj.sendToBack();
                    deselect();   
                }           
            }          
        });

        $("#txtStrokeWidth").filter();

        $(document).bind('keydown',function(e){
            //console.log(e);
            var keyCode = e.keyCode || e.which; 
            if(keyCode == 46){
                handleDelete();
                return false;
                //we must block delete in our input fields!
                ////delet key pressed
                //var myObj = canvas.getActiveObject();
                //shapes = _.without(shapes, myObj);
                //canvas.remove(myObj);
                //deselect();
            }
        });

        //First we capture delete and prevent its default behaviors. From there, we check to see if one of our
        //special element property boxes has focus. If so, we simply figure out if it's dirty, and use isSaving
        function handleDelete() {
            var focusedElement = $("[id$=hrefBox]:focus, [id$=txtMapValue]:focus, [id$=txtMapKey]:focus, [id$=txtAltValue]:focus, [id$=txtStrokeColor]:focus, [id$=txtStrokeWidth]:focus");

            //did we press delete with a field that triggers a callback selected?
            if (isCallbackElement(focusedElement) && isElementDirty(focusedElement)) {
                //Set details so that the callback can know that we're saving.
                isSaving = true;
                saveOnID = focusedElement.attr('id');
 
                //Trigger blur to cause the callback, if there was a change. Then bring the focus right back.
                focusedElement.trigger("change");
                focusedElement.focus();
            } else {
                forceSave();
                //delet key pressed
                var myObj = canvas.getActiveObject();
                shapes = _.without(shapes, myObj);
                canvas.remove(myObj);
                deselect();
            }
        }
        function isCallbackElement(element) {
            return (element.length == 1);
        }
        function isElementDirty(element) {
            if (element.length != 1)
                return false;
 
            return (element.val() != originalVal);
        }
        function forceSave() {
            isSaving = false;
            saveOnID = '';
            //$('input[id$="ButtonSave"]').click();
        }

        //Useage: _Redirect('anotherpage.aspx');
        function _Redirect (url) {
            // IE8 and lower fix
            if (navigator.userAgent.match(/MSIE\s(?!9.0)/)) {
                var referLink = document.createElement('a');
                referLink.href = url;
                document.body.appendChild(referLink);
                referLink.click();
            } 
            // All other browsers
            else { window.location.href = url; }
        }

        //////////////////////////////////////////////////////////////////////
        // ========= START COPY & PAST FUNCTIONS  CTRL-C / CTRL-V ============
        //////////////////////////////////////////////////////////////////////
        createListenersKeyboard();

        function createListenersKeyboard() {
            document.onkeydown = onKeyDownHandler;
            //document.onkeyup = onKeyUpHandler;
        }

        function onKeyDownHandler(event) {
            //event.preventDefault();
    
            var key;
            if(window.event){
                key = window.event.keyCode;
            }
            else{
                key = event.keyCode;
            }

            switch(key){
                //////////////
                // Shortcuts
                //////////////
                case 37: //left:37
                    if(ableToShortcut()){
                        //if(event.ctrlKey){
                            event.preventDefault();
                            moveleft();
                        //}
                    }
                    break;
                case 38: //up:38
                    if(ableToShortcut()){
                        //if(event.ctrlKey){
                            event.preventDefault();
                            moveup();
                        //}
                    }
                    break;
                case 39: //right:39
                    if(ableToShortcut()){
                        //if(event.ctrlKey){
                            event.preventDefault();
                            moveright();
                        //}
                    }
                    break;
                case 40: //down:40
                    if(ableToShortcut()){
                        //if(event.ctrlKey){
                            event.preventDefault();
                            movedown();
                        //}
                    }
                    break;
                // Copy (Ctrl+C)
                case 67: // Ctrl+C
                    if(ableToShortcut()){
                        if(event.ctrlKey){
                            event.preventDefault();
                            copy();
                        }
                    }
                    break;
                // Paste (Ctrl+V)
                case 86: // Ctrl+V
                    if(ableToShortcut()){
                        if(event.ctrlKey){
                            event.preventDefault();
                            paste();
                        }
                    }
                    break;  
                // Zoom In (Ctrl+ +)
                case 107: // Ctrl+ +
                    if(ableToShortcut()){
                        if(event.ctrlKey){
                            event.preventDefault();
                            $("#btnZoomIn").click();
                        }
                    }
                    break;  
                // Zoom Out (Ctrl+ -)
                case 109: // Ctrl+ -
                    if(ableToShortcut()){
                        if(event.ctrlKey){
                            event.preventDefault();
                            $("#btnZoomOut").click();
                        }
                    }
                    break;  
                // Reset Zoom (Ctrl+ 0)
                case (45 || 48 || 96): // Ctrl+ 0 (numpad)
                    if(ableToShortcut()){
                        if(event.ctrlKey){
                            event.preventDefault();
                            $("#btnResetZoom").click();
                        }
                    }
                    break;        
                default:
                    // TODO
                    break;
            }
        }

        function ableToShortcut(){
            /*
            TODO check all cases for this
    
            if($("textarea").is(":focus")){
                return false;
            }
            if($(":text").is(":focus")){
                return false;
            }
            */
            return true;
        }

        function moveleft(){
            if(canvas.getActiveGroup()){
                for(var i in canvas.getActiveGroup().objects){
                    var object = canvas.getActiveGroup().objects[i];
                    object.set("left", object.left-1);
                    canvas.renderAll();   
                }                    
            }
            else if(canvas.getActiveObject()){
                var object = canvas.getActiveObject();
                object.set("left", object.left-1);
                canvas.renderAll();   
            }
        }                                 
        function moveup(){
            if(canvas.getActiveGroup()){
                for(var i in canvas.getActiveGroup().objects){
                    var object = canvas.getActiveGroup().objects[i];
                    object.set("top", object.top-1);
                        canvas.renderAll();   
                }                    
            }
            else if(canvas.getActiveObject()){
                var object = canvas.getActiveObject();
                object.set("top", object.top-1);
                    canvas.renderAll();   
            }
        }             
        function moveright(){
            if(canvas.getActiveGroup()){
                for(var i in canvas.getActiveGroup().objects){
                    var object = canvas.getActiveGroup().objects[i];
                    object.set("left", object.left+1);
                    canvas.renderAll();   
                }                    
            }
            else if(canvas.getActiveObject()){
                var object = canvas.getActiveObject();
                object.set("left", object.left+1);
                canvas.renderAll();   
            }
        }
        function movedown(){
            if(canvas.getActiveGroup()){
                for(var i in canvas.getActiveGroup().objects){
                    var object = canvas.getActiveGroup().objects[i];
                    object.set("top", object.top+1);
                        canvas.renderAll();   
                }                    
            }
            else if(canvas.getActiveObject()){
                var object = canvas.getActiveObject();
                object.set("top", object.top+1);
                    canvas.renderAll();   
            }
        }

        function copy(){
            if(canvas.getActiveGroup()){
                for(var i in canvas.getActiveGroup().objects){
                    var object = fabric.util.object.clone(canvas.getActiveGroup().objects[i]);
                    object.set("top", object.top+5);
                    object.set("left", object.left+5);
                    copiedObjects[i] = object;
                }                    
            }
            else if(canvas.getActiveObject()){
                var object = fabric.util.object.clone(canvas.getActiveObject());
                object.set("top", object.top+5);
                object.set("left", object.left+5);
                copiedObject = object;
                copiedObjects = new Array();
            }
        }

        function paste(){
            if(copiedObjects.length > 0){
                for(var i in copiedObjects){
                    canvas.add(copiedObjects[i]);
                }                    
            }
            else if(copiedObject){
                canvas.add(copiedObject);
            }
            canvas.renderAll();    
        }
        //////////////////////////////////////////////////////////////////////
        // ========= END COPY & PAST FUNCTIONS  CTRL-C / CTRL-V ============
        //////////////////////////////////////////////////////////////////////



        /* PAINT FUNCTIONS */
        function loadPattern(obj, url) {
            obj.pattern = url;
            var tempX = obj.scaleX;
            var tempY = obj.scaleY;
            var zfactor = (100 / obj.scaleX) * canvasScale;

            fabric.Image.fromURL(url, function(img) { 
                img.scaleToWidth(zfactor).set({
                    originX: 'left',
                    originY: 'top'
             });

            // You can apply regualr or custom image filters at this point in applying patterns
            //img.filters.push(new fabric.Image.filters.Sepia(), new fabric.Image.filters.Brightness({ brightness: 100 }));
            //img.applyFilters(canvas.renderAll.bind(canvas));
            //img.filters.push(new fabric.Image.filters.Redify(), new fabric.Image.filters.Brightness({ brightness: 100 }));
            //img.applyFilters(canvas.renderAll.bind(canvas));

             var patternSourceCanvas = new fabric.StaticCanvas();
             patternSourceCanvas.add(img);

             var pattern = new fabric.Pattern({
                source: function() {
                    patternSourceCanvas.setDimensions({
                        width: img.getWidth(),
                        height: img.getHeight()
                    });
                    return patternSourceCanvas.getElement();
                    },
                    repeat: 'repeat'
                });

                fabric.util.loadImage(url, function(img) {
                    // you can customize what properties get applied at this point
                    //obj.lockMovementX = true;
                    //obj.lockMovementY = true;
                    //obj.lockRotation = true;
                    //obj.lockScaling = false; 
                    //obj.hasControls = false,
                    //obj.hasBorders = false,
                    //obj.opacity = 1.0,
                    obj.fill = pattern;
                    canvas.renderAll();
                });

            });
        }

        // "title" is the mapValue & "img" is the short path for the pattern image
        function SetMapSectionPattern(title, img) {
            canvas.forEachObject(function(object){
                if(object.mapValue == title){
                    loadPattern(object, img);
                }
            });
            canvas.renderAll();
            canvas.calcOffset()
            clearNodes();
        }

        function SetMapSectionColor(title, img) {
            var objects = canvas.getObjects();
            canvas.forEachObject(function(object){
                if(object.mapValue == title){
                    object.fill = currentColor;
                }
            });
            canvas.renderAll();
            canvas.calcOffset()
            clearNodes();
        }
        ///////////////////////////////////////////////////////////////////////

        function loadMapObjects(mapObjects)
        {
            fabric.Object.NUM_FRACTION_DIGITS = 10;

            if (_.isUndefined(mapObjects) || _.isNull(mapObjects)) {
                return false;
            }

            canvas.forEachObject(function(object){
                shapes = _.without(shapes, object);
                canvas.remove(object);
            });
            canvas.renderAll();
            canvas.calcOffset()
            clearNodes();
            canvas.clear();

            _.each(mapObjects, function (a) {

                var obj = {
                    left: a.left,
                    top: a.top,
                    fill: a.fill,     
                    width: a.width,
                    height: a.height,
                    scaleX: a.scaleX,
                    scaleY: a.scaleY,
                    opacity: a.opacity,
                    angle: 0,
                    perPixelTargetFind: a.perPixelTargetFind,
                    selectable: a.selectable,
                    hasControls: a.hasControls,
                    //lockMovementX: a.lockMovementX, //JSON doesn't serialize these properties!
                    //lockMovementY: a.lockMovementY,
                    //lockScaling: a.lockScaling,
                    //lockRotation: a.lockRotation,
                    hasRotatingPoint: a.hasRotatingPoint,
                    hasBorders: a.hasBorders,
                    overlayFill: null,
                    stroke: '#000000',
                    strokeWidth: 1,
                    transparentCorners: true,
                    borderColor: "black",
                    cornerColor: "black",
                    cornerSize: 12,
                    transparentCorners: true
                }

                // below you can customize the properties of types of figures added
                // add your own figure properties here for whatever "figures" you create
                var shape;
                switch (a.type) {
                    case "circle":
                        obj.radius = a.radius;
				        shape = new fabric.Circle(obj);
                        shape.lockUniScaling = false;
                        break;
                    case "ellipse":
                        obj.width = a.width;
                        obj.height = a.height;
                        obj.rx = a.width/2;
                        obj.ry = a.height/2;
				        shape = new fabric.Ellipse(obj);
                        shape.lockUniScaling = false;
                        break;
                    case "rect":
                        obj.width = a.width;
                        obj.height = a.height;
                        shape = new fabric.Rect(obj);
                        shape.lockUniScaling = false;
                        break;
                    case "polygon":
                        shape = new fabric.Polygon(a.points, obj)
                        shape = repositionPointsPolygon(a.points, obj);
                        break;
                    // NOTE: Thsi is an IMAGEMAP EDITOR and imagemaps don't have "text"!
                    // however if you create a Fabric ImageMap you can add anything you want!
                    //case "text":
                    //    shape = new fabric.Text ( text ,  objText);
                    //    break;
                }

                if (_.isUndefined(shape.pattern) || _.isNull(shape.pattern)) {
                    shape.pattern = "";
                }

                // Bill SerGio - We add custom properties we need for image maps here to fabric 
                // Below we extend a fabric element's toObject method with additional properties
                // In addition, JSON doesn't stire several of the Fabric properties !!!
                shape.toObject = (function(toObject) {
                  return function() {
                    return fabric.util.object.extend(toObject.call(this), {
                        mapKey: this.mapKey,
                        link: this.link,
                        alt: this.alt,
                        mapValue: this.mapValue,
                        pattern: this.pattern,
                        lockMovementX: this.lockMovementX,
                        lockMovementY: this.lockMovementY,
                        lockScaling: this.lockScaling,
                        lockRotation: this.lockRotation
                    });
                  };
                })(shape.toObject);
                shape.mapKey = mapKey;
                shape.link = a.link;
                shape.alt = a.alt;

                if(a.mapValue == "")
                    a.mapValue = "notitle";

                shape.mapValue = a.mapValue;
                shape.pattern = a.pattern;

                canvas.add(shape);
                shapes.push(shape);

                if(shape.pattern !== '') {
                    loadPattern(shape, shape.pattern);
                }

                lockMovementX: a.lockMovementX;
                lockMovementY: a.lockMovementY;
                lockScaling: a.lockScaling;
                lockRotation: a.lockRotation;

            });
            canvas.renderAll();
            canvas.calcOffset()
            clearNodes();  
        }

        // json doesn't store this properties!!!
        //canvas.loadFromJSON(jsonCanvas, function(){
        //    var objects = canvas.getObjects();
        //    // Trying to set the property after load JSON
        //    //alert('Objects: ' + objects);
        //    objects.forEach(function(o) {
        //    o.lockScalingX = o.lockScalingY = true;
        //    });
        //    canvas.renderAll(); 
        //});

        $('#btnCreateAreasDropDown').click(function () {
            var arAreas=[];
            //var objects = canvas.getObjects();
            canvas.forEachObject(function(object){
                if (!_.isUndefined(object.mapValue) && !_.isNull(object.mapValue)) {
                    if(object.mapValue == '') {
                        object.mapValue = 'notitle'; 
                    }
                    arAreas.push(object.mapValue);
                }
            });
            canvas.renderAll();
            canvas.calcOffset();
            var arAreasD = $.distinct(arAreas);
            $('#ddAreas').empty();
            $.each(arAreasD, function(jx, val) { 
                if(arAreasD[jx] != 'background') {
                    $('#ddAreas').append('<li><a href=\"#\">' + arAreasD[jx] + '</a></li>'); 
                }
            });
        });

        function GetRandomColor()
        { 
            return 'rgb(' + (Math.floor(Math.random() * 256)) + ',' + (Math.floor(Math.random() * 256)) + ',' + (Math.floor(Math.random() * 256)) + ')';
        }

        $.extend({
            distinct : function(anArray) {
               var result = [];
               $.each(anArray, function(i,v){
                   if ($.inArray(v, result) == -1) result.push(v);
               });
               return result;
            }
        });

        function loadMapObjectsNew(s)
        { 
            var mapObjects; 
            $.ajax({
                dataType: 'text',
                success: function(d) {
                    mapObjects = eval('(' + d + ')');
                    //$("#txtName").val(object.Name);
                    loadMapObjects(mapObjects);
                },
                url: s
            });
        }

      
        function add(left, top) {
            if (currentColor.length < 2)
            {
                currentColor = '#fff';
            }

            if ((window.figureType === undefined) || (window.figureType == "text"))
                return false;

            var x = (window.pageXOffset !== undefined) ? window.pageXOffset : (document.documentElement || document.body.parentNode || document.body).scrollLeft;
            var y = (window.pageYOffset !== undefined) ? window.pageYOffset : (document.documentElement || document.body.parentNode || document.body).scrollTop;
            //top = top - y;
            //left = left - x;

            //stroke: String, when `true`, an object is rendered via stroke and this property specifies its color
            //strokeWidth: Number, width of a stroke used to render this object

            if (figureType.length > 0) {
                var obj = {
                    left: left,
                    top: top,
                    fill: ' ' + currentColor,
                    opacity: 1.0,
                    fontFamily: 'Impact', 
                    stroke: '#000000', 
                    strokeWidth: 1,
                    textAlign: 'right'
                };

                var objText = {
                    left: left,
                    top: top,
                    fontFamily: 'Impact', 
                    strokeStyle: '#c3bfbf', 
                    strokeWidth: 3,
                    textAlign: 'right'  
                };

                var shape;
                switch (figureType) {
                    case "text":
                        //var text = document.getElementById("txtAddText").value;
                        var text = gText;
                        shape = new fabric.Text ( text ,  obj);
                        shape.scaleX = shape.scaleY = canvasScale;
                        shape.lockUniScaling = true;
                        shape.hasRotatingPoint = true;
                        break;
                    case "square":
                        obj.width = 50;
                        obj.height = 50;
                        shape = new fabric.Rect(obj);
                        shape.scaleX = shape.scaleY = canvasScale;
                        shape.lockUniScaling = false;
                        break;
                    case "circle":
                        obj.radius = 50;
                        shape = new fabric.Circle(obj);
                        shape.scaleX = shape.scaleY = canvasScale;
                        shape.lockUniScaling = true;
                        break;
                    case "ellipse":
                        obj.width = 100;
                        obj.height = 50;
                        obj.rx = 100;
                        obj.ry = 50;
                        shape = new fabric.Ellipse(obj);
                        shape.scaleX = shape.scaleY = canvasScale;
                        shape.lockUniScaling = false;
                        break;
                    case "polygon":
                        //$('#btnPolygonClose').show();
                        $('#closepolygon').show();

                        obj.selectable = false;
                        if (!currentPoly) {
                            shape = new fabric.Polygon([{ x: 0, y: 0}], obj);
                            shape.scaleX = shape.scaleY = canvasScale;
                            lastPoints = [{ x: 0, y: 0}];
                            lastPos = { left: left, top: top };
                        } else {
                            obj.left = lastPos.left;
                            obj.top = lastPos.top;
                            obj.fill = currentPoly.fill;
                            // while we are still adding nodes let's make the element 
                            // semi-transparent so we can see the canvas background
                            // we will reset opacity when we close the nodes
                            obj.opacity = .4;
                            currentPoly.points.push({ x: left - lastPos.left, y: top - lastPos.top });
                            shapes = _.without(shapes, currentPoly);
                            lastPoints.push({ x: left - lastPos.left, y: top - lastPos.top })
                            shape = repositionPointsPolygon(lastPoints, obj);
                            canvas.remove(currentPoly);
                        }
                        currentPoly = shape;
                        break;
                }

                shape.link = $('#hrefBox').val();
                shape.alt = $('#txtAltValue').val();
                mapKey = $('#txtMapKey').val();
                shape.mapValue = $('#txtMapValue').val();
                //shape.pattern = $('#txtPattern').val(); //path to pattern image

                // Bill SerGio - We add custom properties we need for image maps here to fabric 
                // Below we extend a fabric element's toObject method with additional properties
                // In addition, JSON doesn't stire several of the Fabric properties !!!
                shape.toObject = (function(toObject) {
                  return function() {
                    return fabric.util.object.extend(toObject.call(this), {
                        mapKey: this.mapKey,
                        link: this.link,
                        alt: this.alt,
                        mapValue: this.mapValue,
                        pattern: this.pattern,
                        lockMovementX: this.lockMovementX,
                        lockMovementY: this.lockMovementY,
                        lockScaling: this.lockScaling,
                        lockRotation: this.lockRotation
                    });
                  };
                })(shape.toObject);
                shape.mapKey = mapKey;
                shape.link = '#';
                shape.alt = '';
                shape.mapValue = '';
                shape.pattern = '';
                lockMovementX = false;
                lockMovementY = false;
                lockScaling = false;
                lockRotation = false;
                //////////////////////////////////////////////////////////////////////////////////// 

                canvas.add(shape);
                shapes.push(shape);
                if (figureType != "polygon") {
                    figureType = "";
                }
            } else {
                deselect();
            }

        }

        function groupObjects(objs) {

            //if(canvas.getActiveGroup()){
            //    for(var i in canvas.getActiveGroup().objects){
            //        var object = fabric.util.object.clone(canvas.getActiveGroup().objects[i]);
            //        object.set("top", object.top+5);
            //        object.set("left", object.left+5);
            //        copiedObjects[i] = object;
            //    }                    
            //}
            //else if(canvas.getActiveObject()){
            //    var object = fabric.util.object.clone(canvas.getActiveObject());
            //    object.set("top", object.top+5);
            //    object.set("left", object.left+5);
            //    copiedObject = object;
            //    copiedObjects = new Array();
            //}

            var temp = [];
            $.each(objs.reverse(), function(k, obj) {
                temp.push(obj);
            });
            var groupObj = new fabric.Group(temp, {
                lockRotation: true
            });
            return groupObj;
        }

        function unGroupObjects(groupObj) {
            groupObj.forEachObject(function(obj) {
                var new_scale = groupObj.currentWidth / groupObj.originalState.width;
                var clone = fabric.util.object.clone(obj);
                clone.set({
                    lockRotation: true,
                    top: groupObj.top + obj.top,
                    left: groupObj.left + obj.left,
                    width: obj.getWidth(),
                    height: obj.getHeight(),
                    scaleX: new_scale,
                    scaleY: new_scale
                });
                mainCanvas.add(clone);
                groupObj.remove(obj);
                //mainCanvas.sendToBack(clone);
            });
            mainCanvas.remove(groupObj);
            return;
        }

        //var deleteSelectedObject = document.getElementById('delete-item');
        //deleteSelectedObject.onclick = function()
        //{
        //    if(canvas.getActiveGroup()){
        //      canvas.getActiveGroup().forEachObject(function(o){ canvas.remove(o) });
        //      canvas.discardActiveGroup().renderAll();
        //    } else {
        //      canvas.remove(canvas.getActiveObject());
        //    }
        //};

        //_shouldHandleGroupLogic: function(e, target) {
        //    var activeObject = this.getActiveObject();
        //    return e.shiftKey &&
        //        (this.getActiveGroup() || (activeObject && activeObject !== target))
        //        && this.selection;
        //},

        //j a v a s c r i p t:void(0) will NOT scroll !!!
        var activeFigure;
        var activeNodes;
        canvas.observe('mouse:down', function (e) {
            if (!e.target) {
                add(e.e.layerX, e.e.layerY);
            } else {
                // mousedown on object!
                if (_.detect(shapes, function (a) { return _.isEqual(a, e.target) })) {
                    canvas.selection = true;
                    if (!_.isEqual(activeFigure, e.target)) {
                        if (event.shiftKey) { //shift down 
                        }
                        else {
                            clearNodes();
                        }
                    }
                    activeFigure = e.target;
                    if (activeFigure.type == "polygon") {
                        addNodes();
                    }
                    $('#hrefBox').val(activeFigure.link);
                    $('#txtAltValue').val(activeFigure.alt);
                    $('#txtMapValue').val(activeFigure.mapValue);
                }
            }
        });

        canvas.observe('object:moving', function (e) {
            readjustControls(e);
        });
        canvas.observe('mouse:up', function (e) {
            if (!_.isUndefined(activeFigure) && !_.isNull(activeFigure)) {
                if (activeFigure.type == "polygon") {
                    if((activeNodes != undefined) && (activeNodes != null) ) {
                        if (activeNodes.length == 0) {
                            addNodes();
                        }
                    }
                }
            }
        });
        canvas.observe('object:modified', function (e) {
            if (!_.isUndefined(activeFigure) && !_.isNull(activeFigure)) {
                if (activeFigure.type == "zpolygon") {
                    shapes = _.without(shapes, activeFigure);
                    canvas.remove(activeFigure);
                    var obj = {
                        left: activeFigure.left,
                        top: activeFigure.top,
                        fill: activeFigure.fill,
                        scaleX: activeFigure.scaleX,
                        scaleY: activeFigure.scaleY,
                        opacity: 0.7,
                        link: activeFigure.link,
                        alt: activeFigure.alt,
                        mapValue: activeFigure.mapValue,
                        angle: activeFigure.angle
                    }
                    activeFigure = repositionPointsPolygon(activeFigure.points, obj);
                    activeFigure.link = obj.link;
                    activeFigure.alt = obj.alt;
                    activeFigure.mapValue = obj.mapValue;
                    activeFigure.lockRotation = false;
                    canvas.add(activeFigure);
                    shapes.push(activeFigure);
                    clearNodes(e);
                }
            }
        });

        function repositionPointsPolygon(lastPoints, obj) {
            quickshape = new fabric.Polygon(lastPoints, obj);
            minX = _.min(lastPoints, function (a) { return a.x }).x;
            minY = _.min(lastPoints, function (a) { return a.y }).y;
            var newpoints = [];
            _.each(lastPoints, function (a) {
                var newPoint = {};
                newPoint.x = a.x - (quickshape.width / 2) - minX;
                newPoint.y = a.y - (quickshape.height / 2) - minY;
                newpoints.push(newPoint);
            });
            obj.left += (quickshape.width / 2 + minX) * quickshape.scaleX;
            obj.top += (quickshape.height / 2 + minY) * quickshape.scaleY;
            return new fabric.Polygon(newpoints, obj);
        }

        function deselect() {
            activeFigure = canvas.getActiveObject();
            if (!_.isUndefined(activeFigure) && !_.isNull(activeFigure)) {
                activeFigure.setActive(false);
                activeFigure = null;
                clearNodes();
            }
        }

        function readjustControls(e) {
            if (typeof e.target == "object") {
                if ((activeFigure == "undefined")  || (activeFigure == null)) { 
                    return;
                }
                if ((activeFigure.type == "undefined")  || (activeFigure.type == null)) { 
                    return;
                }

                tgt = e.target;
                if (_.detect(activeNodes, function (a) { return _.isEqual(a, tgt) })) {
                    activeFigure.points[tgt.pointIndex].x = (tgt.left - activeFigure.left) / activeFigure.scaleX;
                    activeFigure.points[tgt.pointIndex].y = (tgt.top - activeFigure.top) / activeFigure.scaleY;
                } else {
                    if (activeFigure.type == "polygon") {
                        _.each(activeNodes, function (p) {
                            //WS
                            try
                            {
                                p.left = activeFigure.left + (activeFigure.points[p.pointIndex].x * activeFigure.scaleX);
                                p.top = activeFigure.top + (activeFigure.points[p.pointIndex].y * activeFigure.scaleY);
                            }
                            catch (err)
                            {
                            }
                        });
                    }
                }
            }
        }

        function clearNodes() {
            _.each(activeNodes, function (item) { canvas.remove(item); });
            activeNodes = [];
        }

        function addNodes() {
            _.each(activeFigure.points, function (p, i) {
                var holdershape = new fabric.Circle({
                    left: activeFigure.left + (p.x * activeFigure.scaleX),
                    top: activeFigure.top + (p.y * activeFigure.scaleY),
                    strokeWidth: 3,
                    radius: 5,
                    fill: '#fff',
                    stroke: '#000'
                });
                //WS Bug Fix
                if (holdershape.length > 0) {
                    holdershape.hasControls = holdershape.hasBorders = false;
                    holdershape.pointIndex = i;
                    activeNodes.push(holdershape);
                    canvas.add(holdershape);
                }
            });
        }

        function pad(str, length) {
            while (str.length < length) {
                str = '0' + str;
            }
            return str;
        };


        // animation demo changing patterns
        var tick = setInterval(function() {
            runSlideShow();
        }, 500);
        var arPatterns = [];
        var k=0;
        //$("#btnRotatePaterns").click();
        $("#btnRotatePaterns").click(function () {
            if (_.isUndefined(selectedArea) || _.isNull(selectedArea) || (selectedArea.length < 1) ) {
                alert("You must create the list of Map Vlauess then select a mapValue!");
                return false;
            }
            arPatterns.length = 0;
            var s = '${ctx}/static/scripts/contenteditor/data/colors.js';
            $.getJSON(s, function (d) {
                $.each(d["Category_3tab"], function (i, item) {
                    arPatterns.push(item.image);
                }); // close each()
            });
            k=0;
            runSlideShow();
        });
        function runSlideShow() {
        	if (arPatterns.length > 0) {
	            var patternImage = "${ctx}/static/scripts/contenteditor/images/" + arPatterns[k].valueOf();
	            SetMapSectionPattern(selectedArea, patternImage);
	            k++;
	            tick();
	            if (k === arPatterns.length) { clearInterval(tick) }
        	}
        } 


        $(function () {
            $('#sl1').slider({
                formater: function (value) {
                    var zopacity = "1";
                    if(value != 10) {
                        zopacity = "."+value
                    }
                    activeFigure = canvas.getActiveObject();
                    if (!_.isUndefined(activeFigure) && !_.isNull(activeFigure)) {
                        activeFigure.opacity = zopacity;
                        canvas.renderAll();
                    } 
                    return 'Opacity: ' + zopacity;
                }
            });
        });


        //$("#btnShowHtmlMap").click();
        $("#btnShowHtmlMap").click(function () {
            createObjectsArray("map_template");
            return false;
        });

        //$("#btnSampleMapCustom").click();
        //$("#btnSampleMapJSON").click();

        //$("#btnShowCustomData").click();
        $("#btnShowCustomData").click(function () {
            createObjectsArray("map_data");
            return false;
        });

        //$("#btnShowJSONData").click();   
        $("#btnShowJSONData").click(function () {
            var getstr = getValidJSONString();

            $('#myModalLabel').html('Custom JSON Objects Data');
            $('.modal-body').text(getstr);
            $("#myModal").modal({
                show: true,
                backdrop: true,
                keyboard: true
            }).css({
                "width": function () { 
                return ($(document).width() * .6) + "px";  
                },
                "margin-left": function () { 
                return -($(this).width() / 2); 
                }
            });  
            return false;
        });

        //WS
        $("#btnSampleMapCustom").click(function () {
            //mapFile = "${ctx}/static/scripts/contenteditor/data/data" + imageid + ".js";
            mapFile = "${ctx}/static/scripts/contenteditor/data/data_custom.js";
            //this automatically loads the shapes array
            loadMapObjectsNew(mapFile);
        });

        $("#btnSampleMapJSON").click(function () {
            mapFile = "${ctx}/static/scripts/contenteditor/data/data_json.js";
            $.ajax({
                dataType: 'text',
                success: function(d) {
                    loadCanvasFromJSONString(d);
                },
                url: mapFile
            });
        });

        $("#btnSaveLocalStorage").click(function () {
            var getstr = getValidJSONString();
            localStorage.setItem("fabricLocalStorageMap", getstr);
        });

        $("#btnLoadLocalStorage").click(function () {
            var getstr = localStorage.getItem("fabricLocalStorageMap");
            loadCanvasFromJSONString(getstr);
        });

        function getValidJSONString() {
            // must do this!
            fabric.Object.NUM_FRACTION_DIGITS = 10;

            // zoom to 1:1 this is our standard for restoring
            $('#btnZoom img[id="btnResetZoom"]').click();

            removePatterns();
            canvas.setBackgroundImage("", canvas.renderAll.bind(canvas));

            var getstr = JSON.stringify(canvas);

            var src = "${ctx}/static/uploads/" + imageid;
            canvas.setBackgroundImage(src, canvas.renderAll.bind(canvas));
            reloadPatterns();

            return getstr;
        };

        function loadCanvasFromJSONString(s) {

            fabric.Object.NUM_FRACTION_DIGITS = 10;

            // zoom to 1:1 this is our standard for restoring
            $('#btnZoom img[id="btnResetZoom"]').click();

            canvas.forEachObject(function(object){
                shapes = _.without(shapes, object);
                canvas.remove(object);
            });

            // this data must NOT include canvas background or fill = pattern
            canvas.clear();

            canvas.loadFromJSON(s);

            canvas.forEachObject(function(object){
                // Bill SerGio - We add custom properties we need for image maps here to fabric 
                // Below we extend a fabric element's toObject method with additional properties
                // In addition, JSON doesn't store several of the Fabric properties !!!
                object.toObject = (function(toObject) {
                  return function() {
                    return fabric.util.object.extend(toObject.call(this), {
                        mapKey: this.mapKey,
                        link: this.link,
                        alt: this.alt,
                        mapValue: this.mapValue,
                        pattern: this.pattern,
                        lockMovementX: this.lockMovementX,
                        lockMovementY: this.lockMovementY,
                        lockScaling: this.lockScaling,
                        lockRotation: this.lockRotation
                    });
                  };
                })(object.toObject);

                // we MUST add object to our shapes array!!!
                shapes.push(object);

                // load patterns back
                if(object.pattern !== '') {
                    loadPattern(object, object.pattern);
                }
            });
         
            canvas.renderAll();
            canvas.calcOffset();

            // restore canvas background
            var src = "${ctx}/static/uploads/" + imageid;
            canvas.setBackgroundImage(src, canvas.renderAll.bind(canvas));;
        };


        function removePatterns() {
            // we need to remove object.fill data where pattern.length > 0 before serialize
            canvas.forEachObject(function(object){
                if (!_.isUndefined(object.pattern) && !_.isNull(object.pattern)) {
                    if(object.pattern.length > 0) {
                        object.fill = '#ff0000'; 
                    }
                }
            });
        };

        function reloadPatterns() {
            // loop through all objects & set patterns back
            canvas.forEachObject(function(object){
                if(!(object.pattern == '')) {
                    loadPattern(object, object.pattern);
                }
            });
        };

        //WS
        function createObjectsArray(t) {
            fabric.Object.NUM_FRACTION_DIGITS = 10;
            mapKey = $('#txtMapKey').val();
            if ($.isEmptyObject(mapKey)) {
                mapKey = "home1";
                $('#txtMapKey').val(mapKey);
            }

            // loop through all objects & assign ONE value to mapKey
            var objects = canvas.getObjects();
            canvas.forEachObject(function(object){
                object.mapKey = mapKey;
            });
            canvas.renderAll();
            canvas.calcOffset()
            clearNodes();

            var areas = []; //note the "s" on areas!


            _.each(objects, function (a) {
                var area = {}; //note that there is NO "s" on "area"!
                area.mapKey = a.mapKey;
                area.link = a.link;
                area.alt = a.alt;
                area.perPixelTargetFind = a.perPixelTargetFind;
                area.selectable = a.selectable;
                area.hasControls = a.hasControls;
                area.lockMovementX = a.lockMovementX;
                area.lockMovementY = a.lockMovementY;
                area.lockScaling = a.lockScaling;
                area.lockRotation = a.lockRotation;
                area.hasRotatingPoint = a.hasRotatingPoint;
                area.hasBorders = a.hasBorders;
                area.overlayFill = null;
                area.stroke = '#000000';
                area.strokeWidth = 1;
                area.transparentCorners = true;
                area.borderColor = "black";
                area.cornerColor = "black";
                area.cornerSize = 12;
                area.transparentCorners = true;
                area.mapValue = a.mapValue;
                area.pattern = a.pattern;
                area.opacity = a.opacity;
                area.fill = a.fill;
                area.left = a.left;
                area.top = a.top;
                area.scaleX = a.scaleX;
                area.scaleY = a.scaleY;
                area.radius = a.radius;
                area.width = a.width;
                area.height = a.height;
                area.rx = a.rx;
                area.ry = a.ry;
                switch (a.type) {
                    case "circle":
                        area.shape = a.type;
                        area.coords = [a.left, a.top, a.radius * a.scaleX];
                        break;
                    case "ellipse":
                        area.shape = a.type;
                        var thisWidth = a.width * a.scaleX;
                        var thisHeight = a.height * a.scaleY;
                        area.coords = [a.left - (thisWidth / 2), a.top - (thisHeight / 2), a.left + (thisWidth / 2), a.top + (thisHeight / 2)];
                        break;
                    case "rect":
                        area.shape = a.type;
                        var thisWidth = a.width * a.scaleX;
                        var thisHeight = a.height * a.scaleY;
                        area.coords = [a.left - (thisWidth / 2), a.top - (thisHeight / 2), a.left + (thisWidth / 2), a.top + (thisHeight / 2)];
                        break;
                    case "polygon":
                        area.shape = a.type;
                        var coords = [];
                        _.each(a.points, function (p) {
                            newX = (p.x * a.scaleX) + a.left;
                            newY = (p.y * a.scaleY) + a.top;
                            coords.push(newX);
                            coords.push(newY);
                        });
                        area.coords = coords;
                        break;
                }

                areas.push(area);

            });

            if(t == "map_template") {
                $('#myModalLabel').html('Image Map HTML');
                $('#textareaID').html(_.template($('#map_template').html(), { areas: areas }));
                $('#myModal').on('shown', function () {
                    $('#textareaID').focus();  
                });
                $("#myModal").modal({
                    show: true,
                    backdrop: true,
                    keyboard: true
                }).css({
                    "width": function () { 
                    return ($(document).width() * .6) + "px";  
                    },
                    "margin-left": function () { 
                    return -($(this).width() / 2); 
                    }
                });         
            }
            if(t == "map_data") {
                $('#myModalLabel').html('Custom JSON Objects Data');
                $('#textareaID').html(_.template($('#map_data').html(), { areas: areas }));
                $('#myModal').on('shown', function () {
                    $('#textareaID').focus();  
                });
                $("#myModal").modal({
                    show: true,
                    backdrop: true,
                    keyboard: true
                }).css({
                    "width": function () { 
                    return ($(document).width() * .6) + "px";  
                    },
                    "margin-left": function () { 
                    return -($(this).width() / 2); 
                    }
                });  
            }

            return false;
        };

    }

</script>


