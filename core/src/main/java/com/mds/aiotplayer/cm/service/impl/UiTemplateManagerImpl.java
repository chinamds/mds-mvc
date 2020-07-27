package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.content.UiTemplateBo;
import com.mds.aiotplayer.cm.dao.UiTemplateDao;
import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.cm.model.UiTemplate;
import com.mds.aiotplayer.cm.service.UiTemplateManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("uiTemplateManager")
@WebService(serviceName = "UiTemplateService", endpointInterface = "com.mds.aiotplayer.cm.service.UiTemplateManager")
public class UiTemplateManagerImpl extends GenericManagerImpl<UiTemplate, Long> implements UiTemplateManager {
    UiTemplateDao uiTemplateDao;

    @Autowired
    public UiTemplateManagerImpl(UiTemplateDao uiTemplateDao) {
        super(uiTemplateDao);
        this.uiTemplateDao = uiTemplateDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<UiTemplate> getUiTemplate(){
    	log.debug("get all UI template from db");
        return uiTemplateDao.getAllDistinct();
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeUiTemplate(Long id) {
		uiTemplateDao.remove(id);
		//CacheUtils.remove(CacheItem.UiTemplates.toString());
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UiTemplate saveUiTemplate(final UiTemplate uiTemplate) throws RecordExistsException {   	
        try {
        	UiTemplate result =  uiTemplateDao.save(uiTemplate);
        	//CacheUtils.remove(CacheItem.UiTemplates.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("UiTemplate '" + uiTemplate.getName() + "' already exists!");
        }
    }
    
	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeUiTemplate(final String uiTemplateIds) {
        log.debug("removing uiTemplate: " + uiTemplateIds);
        try {
	        uiTemplateDao.remove(ConvertUtil.StringtoLongArray(uiTemplateIds));
	        //CacheUtils.remove(CacheItem.UiTemplates.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("UI Template(id=" + uiTemplateIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCacheKey() {
    	return CacheItem.cm_uitemplates.toString();
    }
}