package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.cm.model.UiTemplate;

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