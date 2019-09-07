<script type="text/javascript">

    function loadIframePage (pane, $Pane) {
        if (!$Pane) $Pane = $('.ui-layout-'+ pane);
        var $Iframe = $Pane.prop('tagName')=='IFRAME' ? $Pane : $Pane.find('IFRAME:first');
        var src  = $Iframe.attr('src')
           ,page = $Iframe.attr('longdesc')
                ;
        if (page && src != page) $Iframe.attr('src',page);
    }

    function unloadIframePage (pane, $Pane) {
        if (!$Pane) $Pane = $('.ui-layout-'+ pane);
        var $Iframe = $Pane.prop('tagName')=='IFRAME' ? $Pane : $Pane.find('IFRAME:first');
        $Iframe.attr('src',"about:blank");
    }


    $(document).ready(function () {
        $('body').layout({
                north__size:                    300
           ,    north__spacing_closed:        20
           ,    north__togglerLength_closed:    200
           ,    initClosed : true
           ,    north__togglerContent_closed:"显示组织机构和工作职务查询"
           ,    north__togglerTip_closed:    "显示组织机构和工作职务查询"
           ,    north__sliderTip:            "显示组织机构和工作职务查询"
           ,    resizerTip:         "调整大小"
           ,    togglerTip_open: "隐藏组织机构和工作职务查询"
           ,    togglerTip_closed: "显示组织机构和工作职务查询"
           ,    maskContents:        true // IMPORTANT - enable iframe masking
           ,    north__onopen : loadIframePage
           ,    north__onclose_start:	$.layout.callbacks.pseudoClose
           ,	north__pseudoClose:		{ skipIE: true } // simple iframe - OK in IE

        });
        });
</script>