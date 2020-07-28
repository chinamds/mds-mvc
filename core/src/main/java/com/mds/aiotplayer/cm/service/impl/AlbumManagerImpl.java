package com.mds.aiotplayer.cm.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.nullobjects.NullContentObject;
import com.mds.aiotplayer.cm.dao.AlbumDao;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.cm.service.AlbumManager;
import com.mds.aiotplayer.cm.service.AlbumService;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.model.UiTemplate;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.ConvertUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("albumManager")
@WebService(serviceName = "AlbumService", endpointInterface = "com.mds.aiotplayer.cm.service.AlbumService")
public class AlbumManagerImpl extends GenericManagerImpl<Album, Long> implements AlbumManager, AlbumService {
    AlbumDao albumDao;

    @Autowired
    public AlbumManagerImpl(AlbumDao albumDao) {
        super(albumDao);
        this.albumDao = albumDao;
    }
    
    public List<Map<String, Object>> getAlbumTree(Long galleryId){
		//get all album
		List<Album> albums = albumDao.getAll();
		//convert album data to bootstarp tree format
		List<Map<String,Object>> resultData = new LinkedList<Map<String,Object>>();
		List<Map<String, Object>> data = toTreeData(albums, resultData);
		
		return data;
	}
    
    public List<Album> getAlbums(long galleryId){
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "parent");
        searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleryId);
        
        return albumDao.findAll(searchable);
    }
    
    public List<Map<Long, Long>> findAlbumMap(long galleryId){
    	return albumDao.find("select new map(parent.id as parentId, id) from Album where gallery.id = :p1", new Parameter(galleryId));
    }
    
    @Transactional
    public void saveUiTemplateAlbumTable(Album rootAlbum, List<UiTemplate> uiTemplates) {
    	rootAlbum = albumDao.get(rootAlbum.getId());
    	for(UiTemplate uiTemplate : uiTemplates) {
			if (!rootAlbum.getUiTemplates().stream().anyMatch(t->t.getId()==uiTemplate.getId())) {
				rootAlbum.getUiTemplates().add(uiTemplate);
			}
		}
		//List<UiTemplate> uiTemplates = uiTemplateAll.stream().filter(u->!rootAlbum.getUiTemplates().stream().anyMatch(t->t.getId()==u.getId())).collect(Collectors.toList());
		//rootAlbum.getUiTemplates().addAll(uiTemplates);
		rootAlbum.setCurrentUser(Constants.SystemUserName);
		albumDao.saveAlbum(rootAlbum);
    }
    
    /**
	 * convert album data to bootstarp tree format
	 * @param albums
	 * @return
	 */
	private  List<Map<String,Object>> toTreeData(List<Album> albums, List<Map<String,Object>> resultData){
		for (Album u : albums) {
			if (u.getParent() == null){		
				//Album list
				Map<String,Object> map = new LinkedHashMap<String, Object>();
				map.put("text", u.getName());//album name
				map.put("href", "javascript:void(0)");//link
				map.put("id", u.getId());//album id
				List<Album> ps = u.getChildren();
				map.put("tags",  new Integer[]{ps.size()});//show child data size
				if(null != ps && ps.size() > 0){
					List<Map<String,Object>> list = new LinkedList<Map<String,Object>>();
					list = toTreeData(ps, list);
					map.put("nodes", list);
				}
				resultData.add(map);
			}else{
				//album list
				for (Album up : albums) {
					Map<String,Object> mapx = new LinkedHashMap<String, Object>();
					mapx.put("text", up.getName());//album name
					//mapx.put("href", up.getUrl());//album url
					//mapx.put("tags", "0");//
					mapx.put("id", u.getId());//album id
					resultData.add(mapx);
				}
			}
		}
		return resultData;
	}

		/**
     * {@inheritDoc}
     */
    @Override
	public void removeAlbum(Long id) {
		albumDao.remove(id);
	}

	/**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Album saveAlbum(final Album album) throws RecordExistsException {
    	
        try {
        	Album result =  albumDao.save(album);
            //CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Album '" + album.getName() + "' already exists!");
        }
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public Response removeAlbum(final String albumIds) {
        log.debug("removing album: " + albumIds);
        try {
	        albumDao.remove(ConvertUtil.StringtoLongArray(albumIds));
	        //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("album(id=" + albumIds + ") was successfully deleted.");
        return Response.ok().build();
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public Album getAlbum(final String albumId) {
        return albumDao.get(new Long(albumId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Album> getAlbums() {
    	log.debug("get all albums from db");
        return albumDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Album> searchAlbums(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return albumDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> albumsSelect2(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(albumDao.find(pageable).getContent(), request);
       
        return toSelect2Data(albumDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent(), request);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> albumsTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        
    	Page<Album> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = albumDao.find(searchable);
    	}else {
    		list = albumDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
		
    }
    
    /**
	 * convert album data to select2 format(https://select2.org/data-sources/formats)
	 * {
	 *	  "results": [
	 *	    {
	 *	      "id": 1,
	 *	      "text": "Option 1"
	 *    	},
	 *	    {
	 *	      "id": 2,
	 *	      "text": "Option 2",
	 *	      "selected": true
	 *	    },
	 *	    {
	 *	      "id": 3,
	 *	      "text": "Option 3",
	 *	      "disabled": true
	 *	    }
	 *	  ]
	 *	}
	 * @param albums
	 * @return
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<Album> albums, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Album u : albums) {
			//album list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("parent", u.getFullName());//album parent
			mapData.put("name", u.getName());//album name
			mapData.put("gallery", u.getGallery().getName());//gallery
			mapData.put("id", u.getId());//album id
			mapData.put("isPrivate", u.isIsPrivate());//is private
			list.add(mapData);
		}
				
		return list;
	}  
    
    /**
	 * convert album data to select2 format(https://select2.org/data-sources/formats)
	 * {
	 *	  "results": [
	 *	    {
	 *	      "id": 1,
	 *	      "text": "Option 1"
	 *    	},
	 *	    {
	 *	      "id": 2,
	 *	      "text": "Option 2",
	 *	      "selected": true
	 *	    },
	 *	    {
	 *	      "id": 3,
	 *	      "text": "Option 3",
	 *	      "disabled": true
	 *	    }
	 *	  ]
	 *	}
	 * @param albums
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Album> albums, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Album u : albums) {
			//album list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getName());//album name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//album id
			list.add(mapData);
		}
		
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	@Override
    public List<HashMap<String, Object>> albumsTreeSelector(String excludeIdStr) throws InvalidAlbumException, InvalidGalleryException {
		List<HashMap<String, Object>> mapList = Lists.newArrayList();
		Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "parent.id", "seq");
        
		List<Album> list = albumDao.findAll(searchable);
		if (StringUtils.isBlank(excludeIdStr)) {
			for (int i=0; i<list.size(); i++){
				//Album e = list.get(i);
				AlbumBo e= CMUtils.getAlbumFromDto(list.get(i));
				/*if (e.isRoot())
					continue;*/
				
				HashMap<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", (e.getParent() instanceof NullContentObject) ? 0:e.getParent().getId());
				map.put("name", e.getTitle());
				mapList.add(map);
			}
		}else {
			long excludeId = new Long(excludeIdStr);
			List<Long> parentIds = list.stream().filter(c->!c.getParentIds().contains(excludeId)).map(Album::getId).collect(Collectors.toList());
			for (int i=0; i<list.size(); i++){
				Album e = list.get(i);
				/*if (e.isRoot())
					continue;*/ 
				
				if ((excludeId != e.getId() && parentIds.contains(e.getId()))){
					AlbumBo album= CMUtils.getAlbumFromDto(list.get(i));
					HashMap<String, Object> map = Maps.newHashMap();
					map.put("id", album.getId());
					map.put("pId", (album.getParent() instanceof NullContentObject) ? 0:album.getParent().getId());
					map.put("name", album.getTitle());
					mapList.add(map);
				}
			}
		}
		
		return mapList;
	}
	
	public String getCacheKey() {
    	return CacheItem.cm_albums.toString();
    }
}