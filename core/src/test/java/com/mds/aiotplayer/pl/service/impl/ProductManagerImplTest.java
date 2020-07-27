package com.mds.aiotplayer.pl.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.pl.dao.ProductDao;
import com.mds.aiotplayer.pl.model.Product;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class ProductManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private ProductManagerImpl manager;

    @Mock
    private ProductDao dao;

    @Test
    public void testGetProduct() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Product product = new Product();
        given(dao.get(id)).willReturn(product);

        //when
        Product result = manager.get(id);

        //then
        assertSame(product, result);
    }

    @Test
    public void testGetProducts() {
        log.debug("testing getAll...");
        //given
        final List<Product> products = new ArrayList<>();
        given(dao.getAll()).willReturn(products);

        //when
        List result = manager.getAll();

        //then
        assertSame(products, result);
    }

    @Test
    public void testSaveProduct() {
        log.debug("testing save...");

        //given
        final Product product = new Product();
        // enter all required fields
        product.setProductIndex(new Short("14852"));
        product.setProductName("BlElQgJrUsZqNbHoLhHk");

        given(dao.save(product)).willReturn(product);

        //when
        manager.save(product);

        //then
        verify(dao).save(product);
    }

    @Test
    public void testRemoveProduct() {
        log.debug("testing remove...");

        //given
        final Long id = -11L;
        willDoNothing().given(dao).remove(id);

        //when
        manager.remove(id);

        //then
        verify(dao).remove(id);
    }
}
