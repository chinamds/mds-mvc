package com.mds.aiotplayer.pl.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.pl.model.Product;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductDaoTest extends BaseDaoTestCase {
    @Autowired
    private ProductDao productDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveProduct() {
        Product product = new Product();

        // enter all required fields
        product.setProductIndex(new Short("14463"));
        product.setProductName("UzSeYcWqKeJlMgIqHtPe");

        log.debug("adding product...");
        product = productDao.save(product);

        product = productDao.get(product.getId());

        assertNotNull(product.getId());

        log.debug("removing product...");

        productDao.remove(product.getId());

        // should throw DataAccessException 
        productDao.get(product.getId());
    }
}