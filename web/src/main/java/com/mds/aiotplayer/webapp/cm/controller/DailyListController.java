package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.cm.service.DailyListManager;
import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.webapp.common.controller.AbstractBaseController;
import com.mds.aiotplayer.util.Utils;
import com.mds.aiotplayer.cm.model.DailyList;
import com.mds.aiotplayer.cm.rest.TreeViewOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cm/dailyLists*")
public class DailyListController extends AbstractBaseController<DailyList, Long> {
    private DailyListManager dailyListManager;

    @Autowired
    public void setDailyListManager(DailyListManager dailyListManager) {
        this.dailyListManager = dailyListManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        /*try {
            model.addAttribute(dailyListManager.search(query, DailyList.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(dailyListManager.getAll());
        }*/
        model.addAttribute("independentSpaceForDailyList", Utils.isIndependentSpaceForDailyList());
        
        return model;
    }
    
    @RequestMapping(value ="dailyListtreeview", method = RequestMethod.GET) //value = {"", "main"}
    public String getTreeView(Model model) {
    	TreeViewOptions treeViewOptions = new TreeViewOptions();
    	model.addAttribute("treeViewOptions", treeViewOptions);
    	
        return viewName("dailyListtreeview");
    }
}
