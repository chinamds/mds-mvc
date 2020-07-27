package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.sys.service.MyMessageRecipientManager;
import com.mds.aiotplayer.sys.model.MyMessageRecipient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/myMessageRecipients*")
public class MyMessageRecipientController {
    private MyMessageRecipientManager myMessageRecipientManager;

    @Autowired
    public void setMyMessageRecipientManager(MyMessageRecipientManager myMessageRecipientManager) {
        this.myMessageRecipientManager = myMessageRecipientManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(myMessageRecipientManager.search(query, MyMessageRecipient.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(myMessageRecipientManager.getAll());
        }
        return model;
    }
}
