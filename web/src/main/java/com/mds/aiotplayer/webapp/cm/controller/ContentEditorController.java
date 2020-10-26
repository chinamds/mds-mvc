/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.cm.service.BannerManager;
import com.mds.aiotplayer.cm.model.Banner;
import com.mds.aiotplayer.common.model.JTableResult;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/cm/contenteditor*")
public class ContentEditorController extends BaseFormController {

    public ContentEditorController() {
        setCancelView("redirect:/home");
        setSuccessView("redirect:/home");
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String contentEditor() {
        return "cm/contenteditor";
    }
}
