package com.mds.pl.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.pl.service.ProductManager;
import com.mds.pl.model.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pl/products*")
public class ProductController {
    private ProductManager productManager;

    @Autowired
    public void setProductManager(ProductManager productManager) {
        this.productManager = productManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(productManager.search(query, Product.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(productManager.getAll());
        }
        return model;
    }
}
