package com.mds.aiotplayer.pm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.pm.model.Player;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

//@WebService
public interface PlayerManager extends GenericManager<Player, Long> {
    Response removePlayer(String playerIds);
	
	Player savePlayer(Player player) throws RecordExistsException;
	Player addPlayer(Player player) throws RecordExistsException;
	
	Player userAppointment(String mobile, String idNumber, Player player) throws RecordExistsException;
	
	/*Response changePlayerStatus(String organizationPlayerTypeId, String playerStatus);*/  
}