package com.mds.aiotplayer.pl.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.pl.model.Product;
import com.mds.aiotplayer.sys.model.User;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface ProductManager extends GenericManager<Product, Long> {
	
	/**
     * Saves a product's information.
     *
     * @param product the product's information
     * @throws UserExistsException thrown when user already exists
     * @return product the updated product object
     */
	Product saveProduct(Product product) throws ProductExistsException;
    
}