package com.mds.aiotplayer.pl.dao.hibernate;

import com.mds.aiotplayer.pl.model.Product;
import com.mds.aiotplayer.pl.dao.ProductDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("productDao")
public class ProductDaoHibernate extends GenericDaoHibernate<Product, Long> implements ProductDao {

    public ProductDaoHibernate() {
        super(Product.class);
    }
}
