/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.webapp.common.controller.BaseListController;
import com.mds.aiotplayer.sys.model.UserStatus;
import com.mds.aiotplayer.sys.model.UserStatusHistory;
import com.mds.aiotplayer.sys.service.UserStatusHistoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-28 下午4:29
 * <p>Version: 1.0
 */
@Controller
@RequestMapping(value = "/sys/user/statusHistory")
public class UserStatusHistoryController extends BaseListController<UserStatusHistory, Long> {

    public UserStatusHistoryController() {
    }

    @Override
    protected void preModelData(Model model) {
        model.addAttribute("statusList", UserStatus.values());
    }

}
