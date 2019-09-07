package com.mds.cm.dao.hibernate;

import com.mds.cm.model.Banner;
import com.mds.common.model.JTableRequest;
import com.mds.common.model.JTableResult;
import com.mds.sys.model.User;
import com.mds.cm.dao.BannerDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;

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
    
    @SuppressWarnings("unchecked")
	public HashMap<Long, String> retrieveAll() {
        HashMap<Long, String> rslt = new HashMap<Long, String>();
        String sql = "Select id, strContentName From Banner";
        List<Banner> banners = getSession().createSQLQuery(sql).addEntity(Banner.class).list();
        for (Banner banner : banners) {
            rslt.put(banner.getId(), banner.getContentName());
        }
        return rslt;
    }

    @SuppressWarnings("unchecked")
	public JTableResult retrievePage(JTableRequest jTableRequest) {
        String orderBy = "strContentName";
        if (jTableRequest.getJtSorting() != null) orderBy = jTableRequest.getJtSorting();
        String sql = "Select * From banner Order By " + orderBy;// + " Limit " + jTableRequest.jtStartIndex + ", " + jTableRequest.jtPageSize;
        List<Banner> texts = getSession().createSQLQuery(sql).addEntity(Banner.class)
        		.setFirstResult(jTableRequest.getJtStartIndex() * jTableRequest.getJtPageSize())
        		.setMaxResults(jTableRequest.getJtPageSize()).list();

        JTableResult rslt = new JTableResult();
        rslt.Result = "OK";
        rslt.Records = texts;
        rslt.TotalRecordCount = ((BigInteger) getSession().createSQLQuery("Select count(*) From Banner").uniqueResult()).intValue();

        return rslt;
    }

    public String getNameById(int id) {
    	JdbcTemplate jdbcTemplate =
                new JdbcTemplate(SessionFactoryUtils.getDataSource(getSessionFactory()));
        Table table = AnnotationUtils.findAnnotation(Banner.class, Table.class);
        
        return jdbcTemplate.queryForObject(
                "select strContentName from " + table.name() + " where id=?", String.class, id);
    }

	/**
     * {@inheritDoc}
     */
    public Banner saveBanner(Banner banner) {
        if (log.isDebugEnabled()) {
            log.debug("banner's id: " + banner.getId());
        }
        getSession().saveOrUpdate(banner);
        // necessary to throw a DataIntegrityViolation and catch it in BannerManager
        getSession().flush();
        return banner;
    }
}
