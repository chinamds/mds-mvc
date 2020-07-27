package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.content.ContentTemplateBo;
import com.mds.aiotplayer.cm.content.ContentTemplateBoCollection;
import com.mds.aiotplayer.cm.model.ContentTemplate;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@WebService
public interface ContentTemplateManager extends GenericManager<ContentTemplate, Long> {
	
	/**
     * Retrieves a list of contentTemplates.
     * @return List
     */
	/*@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<ContentTemplate> getContentTemplates();*/
	
    /**
     * Saves a contentTemplate's information
     *
     * @param contentTemplate the contentTemplate's information
     * @return updated contentTemplate
     * @throws RecordExistsException thrown when contentTemplate already exists
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    ContentTemplate saveContentTemplate(ContentTemplate contentTemplate) throws RecordExistsException;

	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeContentTemplate(Long id) ;

	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	Response removeContentTemplate(final String contentTemplateIds);
	
	ContentTemplateBoCollection getContentTemplates();
	void saveContentTemplate(ContentTemplateBo mediaTemplate) throws RecordExistsException;
	
	/**
     * Retrieves a cache key.
     * @return String
     */
	String getCacheKey();
}