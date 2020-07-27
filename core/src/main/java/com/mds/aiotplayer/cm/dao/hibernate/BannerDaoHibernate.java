package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.Banner;
import com.mds.aiotplayer.common.model.JTableRequest;
import com.mds.aiotplayer.common.model.JTableResult;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.cm.dao.BannerDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Table;

import org.hibernate.exception.SQLGrammarException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.stereotype.Repository;

@Repository("bannerDao")
public class BannerDaoHibernate extends GenericDaoHibernate<Banner, Long> implements BannerDao {

    public BannerDaoHibernate() {
        super(Banner.class);
    }
    
	/**
     * {@inheritDoc}
     */
    public Banner saveBanner(Banner banner) {
        if (log.isDebugEnabled()) {
            log.debug("banner's id: " + banner.getId());
        }
        Banner b = super.save(banner);
        // necessary to throw a DataIntegrityViolation and catch it in BannerManager
        getEntityManager().flush();
        
        return b;
    }
}
