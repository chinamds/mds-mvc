/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.controller;

import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.common.model.AbstractEntity;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.common.web.controller.BaseController;
import com.mds.aiotplayer.common.web.bind.annotation.PageableDefaults;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;

/**
 * Base list controller
 * <p>User: John Lee
 * <p>Date: 19 September 2017 20:27:41
 * <p>Version: 1.0
 */
public abstract class BaseListController<M extends AbstractEntity, ID extends Serializable>
        extends BaseController<M, ID> {

    protected GenericManager<M, ID> genericManager;

    /**
     * set generic manager
     *
     * @param baseManager
     */
    //@Autowired
    public void setGenericManager(GenericManager<M, ID> genericManager) {
        this.genericManager = genericManager;
    }


    @RequestMapping(method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    public String list(Searchable searchable, Model model) {

        model.addAttribute("page", genericManager.findPaging(searchable));
       	preModelData(model);

        return viewName("list");
    }

    /**
     * 仅返回表格数据
     *
     * @param searchable
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, headers = "table=true")
    @PageableDefaults(sort = "id=desc")
    public String listTable(Searchable searchable, Model model) {
        list(searchable, model);
        return viewName("listTable");
    }
}
