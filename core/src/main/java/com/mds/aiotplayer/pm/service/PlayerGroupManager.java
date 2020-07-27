package com.mds.aiotplayer.pm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.pm.model.PlayerGroup;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

//@WebService
public interface PlayerGroupManager extends GenericManager<PlayerGroup, Long> {
    Response removePlayerGroup(String playerGroupIds);
	
	PlayerGroup savePlayerGroup(PlayerGroup playerGroup) throws RecordExistsException;
	PlayerGroup addPlayerGroup(PlayerGroup playerGroup) throws RecordExistsException;
	
	PlayerGroup userAppointment(String mobile, String idNumber, PlayerGroup playerGroup) throws RecordExistsException;
	
	/*Response changePlayerGroupStatus(String organizationPlayerGroupTypeId, String playerGroupStatus);*/  
}