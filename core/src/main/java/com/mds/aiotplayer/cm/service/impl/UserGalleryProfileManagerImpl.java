package com.mds.aiotplayer.cm.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.content.UserProfile;
import com.mds.aiotplayer.cm.dao.GalleryDao;
import com.mds.aiotplayer.cm.dao.UserGalleryProfileDao;
import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.cm.model.MimeType;
import com.mds.aiotplayer.cm.model.UserGalleryProfile;
import com.mds.aiotplayer.cm.service.UserGalleryProfileManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("userGalleryProfileManager")
@WebService(serviceName = "UserGalleryProfileService", endpointInterface = "com.mds.aiotplayer.cm.service.UserGalleryProfileManager")
public class UserGalleryProfileManagerImpl extends GenericManagerImpl<UserGalleryProfile, Long> implements UserGalleryProfileManager {
    UserGalleryProfileDao userGalleryProfileDao;
    GalleryDao galleryDao;

    @Autowired
    public UserGalleryProfileManagerImpl(UserGalleryProfileDao userGalleryProfileDao) {
        super(userGalleryProfileDao);
        this.userGalleryProfileDao = userGalleryProfileDao;
    }
    
    @Autowired
    public void setGalleryManagerDao(GalleryDao galleryDao) {
        this.galleryDao = galleryDao;
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeUserGalleryProfile(Long id) {
		userGalleryProfileDao.remove(id);
		//CacheUtils.remove(CacheItem.Albums.toString());
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserGalleryProfile> getUserGalleryProfiles(long galleryId){
    	Searchable searchable = Searchable.newSearchable();
        //searchable.addSort(Direction.ASC, "name");
        searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleryId);
        
        return userGalleryProfileDao.findAll(searchable);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserGalleryProfile> getUserGalleryProfiles(final String userName){
    	Searchable searchable = Searchable.newSearchable();
        //searchable.addSort(Direction.ASC, "name");
        searchable.addSearchFilter("userName", SearchOperator.eq, userName);
        
        return userGalleryProfileDao.findAll(searchable);
    	
    }
    
	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserGalleryProfile saveUserGalleryProfile(final UserGalleryProfile userGalleryProfile) throws RecordExistsException {
    	
        try {
        	UserGalleryProfile result =  userGalleryProfileDao.save(userGalleryProfile);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("UserGalleryProfile '" + userGalleryProfile.getId() + "' already exists!");
        }
    }
    /**
     * {@inheritDoc}
     * @throws JsonProcessingException 
     */
    @Transactional
    @Override
    public void saveUserGalleryProfile(UserProfile userProfile, long templateGalleryId) throws JsonProcessingException {
		// AlbumProfiles
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSearchFilter("userName", SearchOperator.eq, userProfile.UserName);
        searchable.addSearchFilter("settingName", SearchOperator.eq, userProfile.ProfileNameAlbumProfiles);
        
    	List<UserGalleryProfile> userGalleryProfiles = userGalleryProfileDao.findAll(searchable);
    	UserGalleryProfile pDto = userGalleryProfiles.isEmpty() ? null : userGalleryProfiles.get(0);
		if (pDto == null){
			pDto = new UserGalleryProfile();
			pDto.setUserName(userProfile.UserName);
			pDto.setGallery(galleryDao.get(templateGalleryId));
			pDto.setSettingName(userProfile.ProfileNameAlbumProfiles);
		}
		pDto.setSettingValue(userProfile.getAlbumProfiles().serialize());

		userGalleryProfileDao.save(pDto);

		// Content Object Profiles
		searchable = Searchable.newSearchable();
        searchable.addSearchFilter("userName", SearchOperator.eq, userProfile.UserName);
        searchable.addSearchFilter("settingName", SearchOperator.eq, userProfile.ProfileNameContentObjectProfiles);
        
    	userGalleryProfiles = userGalleryProfileDao.findAll(searchable);
    	pDto = userGalleryProfiles.isEmpty() ? null : userGalleryProfiles.get(0);
		if (pDto == null){
			pDto = new UserGalleryProfile();
			pDto.setUserName(userProfile.UserName);
			pDto.setGallery(galleryDao.get(templateGalleryId));
			pDto.setSettingName(userProfile.ProfileNameContentObjectProfiles);
		}
		pDto.setSettingValue(userProfile.getContentObjectProfiles().serialize());
		
		userGalleryProfileDao.save(pDto);


		// User Gallery Profiles
		for (com.mds.aiotplayer.cm.content.UserGalleryProfile userGalleryProfile : userProfile.getGalleryProfiles())
		{
			com.mds.aiotplayer.cm.content.UserGalleryProfile ugp = userGalleryProfile;

			// EnableUserAlbum
			searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("userName", SearchOperator.eq, userProfile.UserName);
	        searchable.addSearchFilter("gallery.id", SearchOperator.eq, ugp.getGalleryId());
	        searchable.addSearchFilter("settingName", SearchOperator.eq, userProfile.ProfileNameEnableUserAlbum);
	        
	    	userGalleryProfiles = userGalleryProfileDao.findAll(searchable);
	    	pDto = userGalleryProfiles.isEmpty() ? null : userGalleryProfiles.get(0);		    	
			if (pDto == null){
				pDto = new UserGalleryProfile();
				pDto.setUserName(userProfile.UserName);
				pDto.setGallery(galleryDao.get(ugp.getGalleryId()));
				pDto.setSettingName(userProfile.ProfileNameEnableUserAlbum);
			}
			pDto.setSettingValue(Boolean.toString(ugp.getEnableUserAlbum()));
			
			userGalleryProfileDao.save(pDto);
			
			// UserAlbumId
			searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("userName", SearchOperator.eq, userProfile.UserName);
	        searchable.addSearchFilter("gallery.id", SearchOperator.eq, ugp.getGalleryId());
	        searchable.addSearchFilter("settingName", SearchOperator.eq, userProfile.ProfileNameUserAlbumId);
	        
	    	userGalleryProfiles = userGalleryProfileDao.findAll(searchable);
	    	pDto = userGalleryProfiles.isEmpty() ? null : userGalleryProfiles.get(0);		    	
			if (pDto == null){
				pDto = new UserGalleryProfile();
				pDto.setUserName(userProfile.UserName);
				pDto.setGallery(galleryDao.get(ugp.getGalleryId()));
				pDto.setSettingName(userProfile.ProfileNameUserAlbumId);
			}
			pDto.setSettingValue(Long.toString(ugp.getUserAlbumId()));
			
			userGalleryProfileDao.save(pDto);
		}
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeUserGalleryProfiles(final String userName) {
    	Searchable searchable = Searchable.newSearchable();
        //searchable.addSort(Direction.ASC, "name");
        searchable.addSearchFilter("userName", SearchOperator.eq, userName);
        userGalleryProfileDao.remove(userGalleryProfileDao.findAll(searchable));
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeUserGalleryProfiles(final long galleryId) {
    	Searchable searchable = Searchable.newSearchable();
        //searchable.addSort(Direction.ASC, "name");
        searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleryId);
        userGalleryProfileDao.remove(userGalleryProfileDao.findAll(searchable));
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeUserGalleryProfile(final String userGalleryProfileIds) {
        log.debug("removing userGalleryProfile: " + userGalleryProfileIds);
        try {
	        userGalleryProfileDao.remove(ConvertUtil.StringtoLongArray(userGalleryProfileIds));
	        //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + userGalleryProfileIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}