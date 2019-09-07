/* ============================================================
* scrollmenu.js v3.0.0
* ============================================================
* Copyright 2012 Bill SerGio, The Infomercial King�
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ============================================================ */

var maxHeight = 400;

$(function () {

    $(".dd > li").hover(function () {

        var $container = $(this),
             $list = $container.find("ul"),
             $anchor = $container.find("a"),
             height = $list.height() * 1.1,       // extend length so there is room at bottom
             multiplier = height / maxHeight;     // longer the list, the faster it needs to scroll

        // save height - used later for mouseout, etc.            
        $container.data("origHeight", $container.height());

        // we want to keep some color as the rollover color while dropdown is open
        $anchor.addClass("hover");

        // align dropdown directly under parent list item    
        $list.show().css({
        	paddingTop: $container.data("origHeight")
        });

        // we don't want any animation when list is shorter than max
        if (multiplier > 1) {
            $container
                .css({
                    height: maxHeight,
                    overflow: "hidden"
                })
                .mousemove(function (e) {
                    var offset = $container.offset();
                    var relativeY = ((e.pageY - offset.top) * multiplier) - ($container.data("origHeight") * multiplier);
                    if (relativeY > $container.data("origHeight")) {
                        $list.css("top", -relativeY + $container.data("origHeight"));
                    };
                });
        }

    }, function () {

        var $el = $(this);

        // return to original settings
        $el
            .height($(this).data("origHeight"))
            .find("ul")
            .css({ top: 0 })
            .hide()
            .end()
            .find("a")
            .removeClass("hover");

    });

    // add down arrow only to menu items with submenus
    //$(".dd > li:has('ul')").each(function () {
   //     $(this).find("a:first").append("<img src='img/down-arrow.png' />");
    //});


});



