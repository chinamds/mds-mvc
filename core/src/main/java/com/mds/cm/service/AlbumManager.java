package com.mds.cm.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.cm.model.Album;
import com.mds.cm.model.UiTemplate;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.core.Response;

//@WebService
public interface AlbumManager extends GenericManager<Album, Long> {
	List<Map<String, Object>> getAlbumTree(Long galleryId);

	/**
     * Saves a album's information
     *
     * @param album the album's information
     * @return updated album
     * @throws RecordExistsException thrown when album already exists
     */
    Album saveAlbum(Album album) throws RecordExistsException;

	void removeAlbum(Long id) ;

	Response removeAlbum(final String albumIds);
	
	List<Album> getAlbums(long galleryId);
	
	List<Map<Long, Long>> findAlbumMap(long galleryId);
	void saveUiTemplateAlbumTable(Album rootAlbum, List<UiTemplate> uiTemplates);
}