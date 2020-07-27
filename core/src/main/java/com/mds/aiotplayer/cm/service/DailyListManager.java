package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.core.ApprovalAction;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.DailyList;
import com.mds.aiotplayer.cm.model.DailyListItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

//@WebService
public interface DailyListManager extends GenericManager<DailyList, Long> {
	/**
     * Saves a DailyList's information
     *
     * @param DailyList the DailyList's information
     * @return updated DailyList
     * @throws RecordExistsException thrown when DailyList already exists
     */
    DailyList saveDailyList(DailyList DailyList) throws RecordExistsException, UnsupportedContentObjectTypeException, InvalidGalleryException;
    
    DailyList dailyListApprove(final DailyList dailyList, ApprovalAction approvalAction, String approvalOpinion) throws RecordExistsException, UnsupportedContentObjectTypeException, InvalidGalleryException;
    
    Response dailyListApprove(List<HashMap<String,Object>> dailyListIds) throws UnsupportedContentObjectTypeException, InvalidGalleryException;

	void removeDailyList(Long id) ;

	Response removeDailyList(final String DailyListIds);
	
	List<DailyList> getDailyLists(long galleryId);
	
	List<Map<Long, Long>> findDailyListMap(long galleryId);
	
	HashMap<String,Object> toAppendGridData(List<DailyListItem> dailyListItems, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException, Exception;
}