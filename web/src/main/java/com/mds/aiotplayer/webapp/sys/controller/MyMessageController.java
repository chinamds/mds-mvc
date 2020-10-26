/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.sys.service.MyMessageManager;
import com.mds.aiotplayer.webapp.common.util.MyMessageApi;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.webapp.sys.bind.annotation.CurrentUser;
import com.mds.aiotplayer.common.web.bind.annotation.PageableDefaults;
import com.mds.aiotplayer.common.web.controller.BaseController;
import com.mds.aiotplayer.sys.model.MessageOperate;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.MyMessage;
import com.mds.aiotplayer.sys.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sys/myMessages*")
public class MyMessageController extends BaseController<MyMessage, Long>{
    private MyMessageManager myMessageManager;
    private MyMessageApi messageApi;
    
    @Autowired
    public void setMyMessageApi(MyMessageApi messageApi) {
        this.messageApi = messageApi;
    }
    
    @Autowired
    public void setMyMessageManager(MyMessageManager myMessageManager) {
        this.myMessageManager = myMessageManager;
    }

    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(myMessageManager.search(query, MyMessage.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(myMessageManager.getAll());
        }
        return model;
    }
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    public String listDefault(Pageable pageable,
                              Model model) {
        return list(MessageFolder.inbox, pageable, model);
    }

    @RequestMapping(value = "{state}/list", method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    public String list(@PathVariable("state") MessageFolder state,
            Pageable pageable,
            Model model) {

        model.addAttribute("state", state);
        model.addAttribute("action", MessageOperate.newmessage);
        //model.addAttribute("page", messageApi.findUserMyMessage(user.getId(), state, pageable));
        model.addAttribute("states", MessageFolder.values());

        return viewName("list");
    }

    /**
     * 仅返回表格数据
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET, headers = "table=true")
    @PageableDefaults(sort = "id=desc")
    public String listTableDefault(Pageable pageable,
                            Model model) {
        list(MessageFolder.inbox, pageable, model);
        return viewName("listTable");
    }
    
    @RequestMapping(value = "{state}/list", method = RequestMethod.GET, headers = "table=true")
    @PageableDefaults(sort = "id=desc")
    public String listTable(@PathVariable("state") MessageFolder state,
                            Pageable pageable,
                            Model model) {
        list(state, pageable, model);
        return viewName("listTable");
    }

}
