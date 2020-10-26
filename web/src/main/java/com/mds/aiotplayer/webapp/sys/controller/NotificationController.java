/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.sys.service.NotificationManager;
import com.mds.aiotplayer.webapp.sys.bind.annotation.CurrentUser;
import com.mds.aiotplayer.common.web.bind.annotation.PageableDefaults;
import com.mds.aiotplayer.common.web.controller.BaseController;
import com.mds.aiotplayer.sys.model.Notification;
import com.mds.aiotplayer.sys.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sys/notifications*")
public class NotificationController extends BaseController<Notification, Long>{
    private NotificationManager notificationManager;

    @Autowired
    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequestDefault(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(notificationManager.search(query, Notification.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(notificationManager.getAll());
        }
        return model;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        /*try {
            model.addAttribute(notificationManager.search(query, Notification.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(notificationManager.getAll());
        }*/
        return model;
    }
    
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @PageableDefaults(value = 20, sort = "id=desc")
    public String list(@CurrentUser User user, Pageable pageable, Model model) {

        /*Searchable searchable = Searchable.newSearchable();
        searchable.addSearchFilter("user.id", SearchOperator.eq, user.getId());

        Page<Notification> page = notificationManager.getAllPaging(pageable);

        model.addAttribute("page", page);
        if(pageable.getPageNumber() == 0) {
        	notificationManager.markReadAll(user.getId());
        }*/

        return viewName("list");
    }

    @RequestMapping("/markRead")
    @ResponseBody
    public String markRead(@RequestParam("id") Long id) {
    	notificationManager.markRead(id);
        return "";
    }
}
