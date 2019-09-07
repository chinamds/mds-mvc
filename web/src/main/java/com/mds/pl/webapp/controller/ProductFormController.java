package com.mds.pl.webapp.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.pl.service.CatalogueManager;
import com.mds.pl.service.ProductManager;
import com.mds.pl.model.Catalogue;
import com.mds.pl.model.Product;
import com.mds.sys.model.User;
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

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/pl/productform*")
public class ProductFormController extends BaseFormController {
    private ProductManager productManager = null;
    private CatalogueManager catalogueManager = null;

    @Autowired
    public void setProductManager(ProductManager productManager) {
        this.productManager = productManager;
    }
    
    @Autowired
    public void setCatalogueManager(CatalogueManager catalogueManager) {
        this.catalogueManager = catalogueManager;
    }

    public ProductFormController() {
        setCancelView("redirect:products");
        setSuccessView("redirect:products");
    }
    
    @ModelAttribute("catalogueList")
    protected List<Catalogue> loadCatalogues(final HttpServletRequest request) {
        return catalogueManager.getAll();
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Product showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
        	return productManager.get(new Long(id));
        }
        
        Product product =new Product();
        String catalogueId = request.getParameter("catalogueId");
        if (!StringUtils.isBlank(catalogueId)) {
        	product.setCatalogue(catalogueManager.get(new Long(catalogueId)));
        }
        

        return product;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Product product, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(product, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "productform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (product.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            productManager.remove(product.getId());
            saveMessage(request, getText("product.deleted", locale));
        } else {   
        	//product.setCatalogue(catalogueManager.get(product.getCatalogueId())); //request.getParameter("catalogueID")
        	if (product.getCatalogue() == null){
        		if (!StringUtils.isBlank(request.getParameter("catalogueId"))) {
        			product.setCatalogue(catalogueManager.get(new Long(request.getParameter("catalogueId"))));
        		}
        	}
            productManager.save(product);
            String key = (isNew) ? "product.added" : "product.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:pl/productform?id=" + product.getId();
            }
        }

        return success;
    }
}
