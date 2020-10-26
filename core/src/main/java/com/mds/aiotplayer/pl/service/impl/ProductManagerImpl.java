/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.service.impl;

import com.mds.aiotplayer.pl.dao.ProductDao;
import com.mds.aiotplayer.pl.dao.CatalogueDao;
import com.mds.aiotplayer.pl.model.Product;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.pl.service.ProductExistsException;
import com.mds.aiotplayer.pl.service.ProductManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("productManager")
@WebService(serviceName = "ProductService", endpointInterface = "com.mds.aiotplayer.pl.service.ProductManager")
public class ProductManagerImpl extends GenericManagerImpl<Product, Long> implements ProductManager {
    ProductDao productDao;

    @Autowired
    public ProductManagerImpl(ProductDao productDao) {
        super(productDao);
        this.productDao = productDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Product saveProduct(final Product product) throws ProductExistsException {
        try {
            return productDao.save(product);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new ProductExistsException("Product '" + product.getProductIndex() + "' already exists!");
        }
    }
}