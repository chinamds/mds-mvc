package com.mds.pl.service.impl;

import com.mds.pl.dao.ProductDao;
import com.mds.pl.dao.CatalogueDao;
import com.mds.pl.model.Product;
import com.mds.sys.model.User;
import com.mds.pl.service.ProductExistsException;
import com.mds.pl.service.ProductManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("productManager")
@WebService(serviceName = "ProductService", endpointInterface = "com.mds.pl.service.ProductManager")
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