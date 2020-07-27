package com.mds.aiotplayer.pm.service.impl;

import com.mds.aiotplayer.pm.dao.PlayerDao;
import com.mds.aiotplayer.pm.model.Player;
import com.mds.aiotplayer.pm.service.PlayerManager;
import com.mds.aiotplayer.pm.service.PlayerService;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.StringUtils;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("playerManager")
@WebService(serviceName = "PlayerService", endpointInterface = "com.mds.aiotplayer.pm.service.PlayerService")
public class PlayerManagerImpl extends GenericManagerImpl<Player, Long> implements PlayerManager, PlayerService {
    PlayerDao playerDao;

    @Autowired
    public PlayerManagerImpl(PlayerDao playerDao) {
        super(playerDao);
        this.playerDao = playerDao;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public Player getPlayer(final String playerId) {
        return playerDao.get(new Long(playerId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Player> getPlayers() {
        return playerDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Player> searchPlayers(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return playerDao.search(pageable, new String[]{"code", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> playersSelect2(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(playerDao.find(pageable).getContent());
       
        return toSelect2Data(playerDao.search(pageable, new String[]{"code", "description"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> playersTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.setPage(pageable);
        
        Page<Player> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		searchable.addSort(Direction.DESC, "apptDate", "apptPeriod.timeFrom", "apptPeriod.apptItem.code");
    		list = playerDao.find(searchable);
    	}else {
    		searchable.addSort(Direction.DESC, "apptDate", "timeRange", "apptCode");
    		list = playerDao.search(searchable, searchTerm);
    	}
    	    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", list.getContent());
		
		return resultData;
		
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> playersTable(String organizationId, String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "playerName");
        searchable.setPage(pageable);
        
        UserAccount user = UserUtils.getUser();
    	long oId = StringUtils.toLong(organizationId);
    	if (!user.isSystem()) {
    		searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
    	}else {
    		if (oId != Long.MIN_VALUE && oId > 0) {
    			searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
    		}
    	}
        
    	List<Player> list =  null;
    	long totalElements = 0;
    	if (StringUtils.isBlank(searchTerm)){
    		Page<Player> page = playerDao.find(searchable);
    		list = page.getContent();
    		totalElements = page.getTotalElements();
    	}else {
    		list = playerDao.findAll(searchable);
    		totalElements = list.size();
    		list = list.stream().skip(offset).limit(limit).collect(Collectors.toList());
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", totalElements);
		resultData.put("rows", toBootstrapTableData(list, request));
		
		return resultData;
    }
    
    /**
	 * convert role data to select2 format(https://select2.org/data-sources/formats)
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
	 *<th data-field="playerName" data-formatter="thumbnailFormatter"><fmt:message key="player.playerName"/></th>
	        <th data-field="uniqueName"><fmt:message key="player.uniqueName"/></th>       
	        <th data-field="location"><fmt:message key="player.location"/></th>        
	        <th data-field="publicIP"><fmt:message key="player.publicIP"/></th>
	        <th data-field="diskSerial"><fmt:message key="player.diskSerial"/></th>
	        <th data-field="lastOnlineTime"><fmt:message key="player.lastOnlineTime"/></th>
	        <th data-field="lastSyncTime"><fmt:message key="player.lastSyncTime"/></th>
	        <th data-field="startup"><fmt:message key="player.startup"/></th>
	        <th data-field="shutdown"><fmt:message key="player.shutdown"/></th>
	        <th data-field="MDSVersion"><fmt:message key="player.MDSVersion"/></th>
	        <th data-field="localAddress"><fmt:message key="player.localAddress"/></th>
	        <th data-field="MACAddress"><fmt:message key="player.MACAddress"/></th>
	        <th data-field="description"><fmt:message key="player.description"/></th>
	        <th data-field="createdBy"><fmt:message key="player.createdBy"/></th>
	        <th data-field="dateAdded" data-formatter="dateTimeFormatter"><fmt:message key="player.dateAdded"/></th>
	 * @param roles
	 * @return
     * @throws Exception 
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<Player> players, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Player u : players) {
			//Organization's Workflow Type list		
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("playerName", u.getPlayerName());//workflow type title
			mapData.put("uniqueName", u.getUniqueName());//workflow type title
			mapData.put("location", u.getLocation());//workflow type title
			mapData.put("publicIP", u.getPublicIP());//workflow type title
			mapData.put("diskSerial", u.getDiskSerial());//workflow type title
			mapData.put("lastOnlineTime", u.getLastOnlineTime());//organization Code
			mapData.put("lastSyncTime", u.getLastSyncTime());//organization Code
			mapData.put("startup", u.getStartup());//startup time
			mapData.put("shutdown", u.getShutdown());//shutdown time
			mapData.put("MDSVersion", u.getMDSVersion());//MDS version
			mapData.put("localAddress", u.getLocalAddress());//Local address
			mapData.put("MACAddress", u.getMACAddress());//MAC address
			mapData.put("id", u.getId());//role id
			mapData.put("createdBy", u.getCreatedBy());
			mapData.put("dateAdded", "/Date(" + u.getDateAdded().getTime() + ")/");//create date
			mapData.put("description", u.getDescription());//
			list.add(mapData);
		}
				
		return list;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Player savePlayer(final Player player) throws RecordExistsException {

        try {
            return playerDao.savePlayer(player);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Player type: '" + player.getPlayerName() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Player addPlayer(final Player player) throws RecordExistsException {

        try {
            return playerDao.addPlayer(player);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Player type: '" + player.getPlayerName() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Player userAppointment(String mobile, String idNumber, Player player) 
    		throws RecordExistsException {
		
		return savePlayer(player);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Response removePlayer(final String playerIds) throws WebApplicationException{
        log.debug("removing player: " + playerIds);
        try {
        	playerDao.remove(ConvertUtil.StringtoLongArray(playerIds));
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + playerIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
     * {@inheritDoc}
     */
/*    @Override
    public Response changePlayerStatus(String playerId, String playerStatus) {
    	try {
        	Player player = playerDao.get(new Long(playerId));
        	if (player != null){
        		player.setPlayerType(PlayerType.valueOf(playerStatus));
        		playerDao.save(player);
        	}
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    	
    	return Response.ok().build();
    }*/
        
    /**
	 * convert player data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param players
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Player> players){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Player u : players) {
			//player list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getPlayerName());//player name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//player id
			list.add(mapData);
		}
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	@Override
    public Response getQRCode(String refNo){
		Searchable searchable = Searchable.newSearchable();
	    searchable.addSearchFilter("refNo", SearchOperator.eq, refNo);
	    Player player = playerDao.findOne(searchable);
        // uncomment line below to send non-streamed
		 //QRCode.from("Hello World").to(ImageType.PNG).writeTo(outputStream);

        //return Response.ok(QRCode.from(player.getRefNo()).withSize(250, 250).stream()).build();
        // uncomment line below to send streamed
        // return Response.ok(new ByteArrayInputStream(imageData)).build();
	    ByteArrayInputStream bas = null;
    	try {
	    	File file =QRCode.from(player.getPlayerName()).to(ImageType.PNG).withSize(250, 250).file();
	    	bas =new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
    	} catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        
        return Response.ok(bas).build();
    }
}