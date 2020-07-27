package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.LogDao;
import com.mds.aiotplayer.sys.model.Log;
import com.mds.aiotplayer.sys.service.LogManager;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

@Service("logManager")
@WebService(serviceName = "LogService", endpointInterface = "com.mds.aiotplayer.sys.service.LogManager")
public class LogManagerImpl extends GenericManagerImpl<Log, Long> implements LogManager {
    LogDao logDao;

    @Autowired
    public LogManagerImpl(LogDao logDao) {
        super(logDao);
        this.logDao = logDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Log> find(Pageable page, Map<String, Object> paramMap) {
		/*DetachedCriteria dc = logDao.createDetachedCriteria();

		Long createById = StringUtils.toLong(paramMap.get("createById"));
		if (createById > 0){
			dc.add(Restrictions.eq("createBy.id", createById));
		}
		
		String requestUri = ObjectUtils.toString(paramMap.get("requestUri"));
		if (StringUtils.isNotBlank(requestUri)){
			dc.add(Restrictions.like("requestUri", "%"+requestUri+"%"));
		}

		String exception = ObjectUtils.toString(paramMap.get("exception"));
		if (StringUtils.isNotBlank(exception)){
			dc.add(Restrictions.eq("type", Log.TYPE_EXCEPTION));
		}
		
		Date beginDate = DateUtils.parseDate(paramMap.get("beginDate"));
		if (beginDate == null){
			beginDate = DateUtils.setDays(new Date(), 1);
			paramMap.put("beginDate", DateUtils.formatDate(beginDate, "yyyy-MM-dd"));
		}
		Date endDate = DateUtils.parseDate(paramMap.get("endDate"));
		if (endDate == null){
			endDate = DateUtils.addDays(DateUtils.addMonths(beginDate, 1), -1);
			paramMap.put("endDate", DateUtils.formatDate(endDate, "yyyy-MM-dd"));
		}
		dc.add(Restrictions.between("createDate", beginDate, endDate));
		
		dc.addOrder(Order.desc("id"));
		return logDao.find(page, dc);*/
    	
    	return null;
	}
}