package com.mds.pl.webapp.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.pl.service.ProductManager;
import com.mds.pl.service.ZoneManager;
import com.mds.pl.model.Zone;
import com.mds.common.webapp.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/pl/zoneform*")
public class ZoneFormController extends BaseFormController {
    private ZoneManager zoneManager = null;
    private ProductManager productManager = null;

    @Autowired
    public void setZoneManager(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }
    
    @Autowired
    public void setProductManager(ProductManager productManager) {
        this.productManager = productManager;
    }

    public ZoneFormController() {
        setCancelView("redirect:zones");
        setSuccessView("redirect:zones");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Zone showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return zoneManager.get(new Long(id));
        }

        return new Zone();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Zone zone, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(zone, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "zoneform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (zone.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            zoneManager.remove(zone.getId());
            saveMessage(request, getText("zone.deleted", locale));
        } else {
        	if (zone.getProduct() == null){
        		if (!StringUtils.isBlank(request.getParameter("productId"))) {
        			zone.setProduct(productManager.get(new Long(request.getParameter("productId"))));
        		}
        	}
            zoneManager.save(zone);
            String key = (isNew) ? "zone.added" : "zone.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:zoneform?id=" + zone.getId();
            }
        }

        return success;
    }
}
