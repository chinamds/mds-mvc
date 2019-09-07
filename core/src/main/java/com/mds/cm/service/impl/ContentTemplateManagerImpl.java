package com.mds.cm.service.impl;

import com.mds.cm.content.ContentTemplateBo;
import com.mds.cm.content.ContentTemplateBoCollection;
import com.mds.cm.dao.ContentTemplateDao;
import com.mds.cm.model.ContentTemplate;
import com.mds.cm.service.ContentTemplateManager;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.search.Searchable;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.core.CacheItem;
import com.mds.core.exception.ArgumentNullException;
import com.mds.util.CacheUtils;
import com.mds.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("contentTemplateManager")
@WebService(serviceName = "ContentTemplateService", endpointInterface = "com.mds.cm.service.ContentTemplateManager")
public class ContentTemplateManagerImpl extends GenericManagerImpl<ContentTemplate, Long> implements ContentTemplateManager {
    ContentTemplateDao contentTemplateDao;

    @Autowired
    public ContentTemplateManagerImpl(ContentTemplateDao contentTemplateDao) {
        super(contentTemplateDao);
        this.contentTemplateDao = contentTemplateDao;
    }

    /**
     * {@inheritDoc}
     */
    /*@Override
    public List<ContentTemplate> getContentTemplates(){
    	log.debug("get all content templates from db");
        return contentTemplateDao.getAllDistinct();
    }*/
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void removeContentTemplate(Long id) {
		contentTemplateDao.remove(id);
		//CacheUtils.remove(CacheItem.ContentTemplates.toString());
	}
    
    /// <summary>
    /// Fill the <paramref name="emptyCollection"/> with all the media templates in the current application. The return value is the same reference
    /// as the parameter.
    /// </summary>
    /// <param name="emptyCollection">An empty <see cref="ContentTemplateBoCollection"/> object to populate with the list of media templates in the current
    /// application. This parameter is required because the library that implements this interface does not have
    /// the ability to directly instantiate any object that implements <see cref="ContentTemplateBoCollection"/>.</param>
    /// <returns>
    /// Returns an <see cref="ContentTemplateBoCollection"/> representing the media templates in the current application. The returned object is the
    /// same object in memory as the <paramref name="emptyCollection"/> parameter.
    /// </returns>
    /// <exception cref="ArgumentNullException">Thrown when <paramref name="emptyCollection" /> is null.</exception>
    public ContentTemplateBoCollection getContentTemplates()   {
      ContentTemplateBoCollection emptyCollection = new ContentTemplateBoCollection();
      Searchable searchable = Searchable.newSearchable();
      searchable.addSort(Direction.ASC, "mimeType");
      for (ContentTemplate btDto : contentTemplateDao.findAll(searchable)){
    	  	ContentTemplateBo bt = emptyCollection.CreateEmptyContentTemplateInstance();
    	  	bt.ContentTemplateId = btDto.getId();
			bt.MimeType = btDto.getMimeType().trim();
			bt.BrowserId = btDto.getBrowserId().trim();
			bt.HtmlTemplate = btDto.getHtmlTemplate().trim();
			bt.ScriptTemplate = btDto.getScriptTemplate().trim();
			
			emptyCollection.add(bt);
      }

      return emptyCollection;
    }

	/// <summary>
	/// Persist the media template to the data store.
	/// </summary>
	/// <param name="mediaTemplate">An instance of <see cref="ContentTemplateBo"/> to persist to the data store.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="mediaTemplate" /> is null.</exception>
	public void saveContentTemplate(ContentTemplateBo mediaTemplate) throws RecordExistsException
	{
		if (mediaTemplate == null)
			throw new ArgumentNullException("mediaTemplate");

		ContentTemplate uiTmplDto = new ContentTemplate();
		if (!mediaTemplate.isNew()){
			uiTmplDto.setId(mediaTemplate.ContentTemplateId);
		}
		uiTmplDto.setMimeType(mediaTemplate.MimeType);
		uiTmplDto.setBrowserId(mediaTemplate.BrowserId);
		uiTmplDto.setHtmlTemplate( mediaTemplate.HtmlTemplate);
		uiTmplDto.setScriptTemplate(mediaTemplate.ScriptTemplate);
			
		saveContentTemplate(uiTmplDto);
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public ContentTemplate saveContentTemplate(final ContentTemplate contentTemplate) throws RecordExistsException {
    	
        try {
        	ContentTemplate result =  contentTemplateDao.save(contentTemplate);
        	//CacheUtils.remove(CacheItem.ContentTemplates.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("ContentTemplate '" + contentTemplate.getMimeType() + "' already exists!");
        }
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public Response removeContentTemplate(final String contentTemplateIds) {
        log.debug("removing contentTemplate: " + contentTemplateIds);
        try {
	        contentTemplateDao.remove(ConvertUtil.StringtoLongArray(contentTemplateIds));
	        //CacheUtils.remove(CacheItem.ContentTemplates.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Content Template(id=" + contentTemplateIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCacheKey() {
    	return CacheItem.cm_contenttemplates.toString();
    }
}