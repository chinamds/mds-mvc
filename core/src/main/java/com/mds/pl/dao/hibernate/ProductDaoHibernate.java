package com.mds.pl.dao.hibernate;

import com.mds.pl.model.Product;
import com.mds.pl.dao.ProductDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("productDao")
public class ProductDaoHibernate extends GenericDaoHibernate<Product, Long> implements ProductDao {

    public ProductDaoHibernate() {
        super(Product.class);
    }
}
