/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.util;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.pm.model.Player;
import com.mds.aiotplayer.pm.player.PlayerBo;
import com.mds.aiotplayer.pm.player.PlayerBoCollection;
import com.mds.aiotplayer.pm.rest.PlayerItem;
import com.mds.aiotplayer.pm.service.PlayerManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Contains functionality for interacting with players (that is, content objects and albums). Typically web pages 
/// directly call the appropriate business layer objects, but when a task involves multiple steps or the functionality 
/// does not exist in the business layer, the methods here are used.
/// </summary>
public final class PlayersUtils{
	//#region Public Static Methods
	private static PlayerManager playerManager = SpringContextHolder.getBean(PlayerManager.class);

	/// <summary>
	/// Persist the content object to the data store. This method updates the audit fields before saving. The currently logged
	/// on user is recorded as responsible for the changes. All players should be
	/// saved through this method rather than directly invoking the content object's Save method, unless you want to 
	/// manually update the audit fields yourself.
	/// </summary>
	/// <param name="player">The content object to persist to the data store.</param>
	/// <remarks>When no user name is available through <see cref="UserUtils.getLoginName()" />, the String &lt;unknown&gt; is
	/// substituted. Since MDS requires users to be logged on to edit objects, there will typically always be a user name 
	/// available. However, in some cases one won't be available, such as when an error occurs during self registration and
	/// the exception handling code needs to delete the just-created user album.</remarks>
	public static void savePlayer(PlayerBo player)	{
		String userName = (StringUtils.isBlank(UserUtils.getLoginName()) ? I18nUtils.getMessage("site.Missing_Data_Text") : UserUtils.getLoginName());
		savePlayer(player, userName);
	}

	/// <summary>
	/// Persist the content object to the data store. This method updates the audit fields before saving. All players should be
	/// saved through this method rather than directly invoking the content object's Save method, unless you want to
	/// manually update the audit fields yourself.
	/// </summary>
	/// <param name="player">The content object to persist to the data store.</param>
	/// <param name="userName">The user name to be associated with the modifications. This name is stored in the internal
	/// audit fields associated with this content object.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="player" /> is null.</exception>
	public static void savePlayer(PlayerBo player, String userName)	{
		if (player == null)
			throw new ArgumentNullException("player");

		Date currentTimestamp = DateUtils.Now();

		if (player.isNew())	{
			player.setRegTime(currentTimestamp);
		}

		// Persist to data store.
		player.save();
	}

	public static PlayerItem[] getAllPlayerItems(){
		PlayerBoCollection players = getPlayersFromDataStore();

		PlayerItem[] playerItems = new PlayerItem[players.size()];
		for (int i = 0; i < playerItems.length; i++){
			PlayerBo player = players.get(i);

			playerItems[i] = new PlayerItem(player.getUniqueName(), player.getPlayerName());
		}

		return playerItems;
	}

	/// <summary>
	/// Gets a list of Player corresponding to the specified <paramref name="searchOptions" />.
	/// Guaranteed to not return null.
	/// </summary>
	/// <returns>Iterable{Player}.</returns>
/*	public static Collection<Player> getPlayers(){
		return getPlayers();
	}*/
	
	public static PlayerBo CreatePlayerInstance(String strServerAddress, short nRetries, String strDescription, String strLogin, String strPlayerName, String strPassword, String strUniqueName,
			int nPort, short nRetryDelay, boolean bUsePASVMode, boolean bUseFirewall, String strDiskSerial, String strLocation, short nFtpPeriod, short nBeforeDay,
			int dwFtpContent, boolean bReplaceFile, String strMACAddress, String strMACAddress1, String strLocalAddress, String strLocalLogin, String strLocalPassword,
			int nLocalPort, String strTimeOuts, String strDeviceID, String strMACID, String strPublicIP, boolean bBinary, long dbLimit, int dwConnectionTimeout,
			String sUserAgent, String sProxyServer, Short ConnectionType, Integer dwReadBufferSize, Date dtRegTime, String strMDSVersion, boolean bOnline,
			Date dtLastOnlineTime, String strPhoneNumber, String strPhoneNumberServer, String guidReg, Date dtStartup, Date dtShutdown, Date dtLastSyncTime)
	{
		return new PlayerBo(Long.MIN_VALUE, strServerAddress, nRetries, strDescription, strLogin, strPlayerName, strPassword, strUniqueName, nPort, nRetryDelay, bUsePASVMode, bUseFirewall,
			strDiskSerial, strLocation, nFtpPeriod, nBeforeDay, dwFtpContent, bReplaceFile, strMACAddress, strMACAddress1, strLocalAddress, strLocalLogin,
			strLocalPassword, nLocalPort, strTimeOuts, strDeviceID, strMACID, strPublicIP, bBinary, dbLimit, dwConnectionTimeout, sUserAgent, sProxyServer,
			ConnectionType, dwReadBufferSize, dtRegTime==null? DateUtils.MinValue : dtRegTime, strMDSVersion, bOnline, dtLastOnlineTime == null ? DateUtils.MinValue : dtLastOnlineTime, strPhoneNumber, strPhoneNumberServer, guidReg, dtStartup,
			dtShutdown, dtLastSyncTime);
	}
	
	private static PlayerBoCollection getPlayersFromPlayerDtos(Collection<Player> playerDtos)	{
		PlayerBoCollection players = new PlayerBoCollection();
		
		for (Player playerDto : playerDtos)	{
			PlayerBo player = new PlayerBo(playerDto.getId(),
				playerDto.getServerAddress(),
				playerDto.getRetries(),
				playerDto.getDescription(),
				playerDto.getLogin(),
				playerDto.getPlayerName(),
				playerDto.getPassword(),
				playerDto.getUniqueName(),
				playerDto.getPort(),
				playerDto.getRetryDelay(),
				playerDto.isUsePASVMode(),
				playerDto.isUseFirewall(),
				playerDto.getDiskSerial(),
				playerDto.getLocation(),
				playerDto.getFtpPeriod(),
				playerDto.getBeforeDay(),
				playerDto.getFtpContent(),
				playerDto.isReplaceFile(),
				playerDto.getMACAddress(),
				playerDto.getMACAddress1(),
				playerDto.getLocalAddress(),
				playerDto.getLocalLogin(),
				playerDto.getLocalPassword(),
				playerDto.getLocalPort(),
				playerDto.getTimeOuts(),
				playerDto.getDeviceID(),
				playerDto.getMACID(),
				playerDto.getPublicIP(),
				playerDto.isBinary(),
				playerDto.getDbLimit(),
				playerDto.getConnectionTimeout(),
				playerDto.getUserAgent(),
				playerDto.getProxyServer(),
				playerDto.getConnectionType() == null ? 0 : playerDto.getConnectionType(),
				playerDto.getReadBufferSize() == null ? 0 : playerDto.getReadBufferSize(),
				playerDto.getRegTime() == null ? DateUtils.MinValue : playerDto.getRegTime(),
				playerDto.getMDSVersion(),
				playerDto.isOnline(),
				playerDto.getLastOnlineTime() == null ? DateUtils.MinValue : playerDto.getLastOnlineTime(),
				playerDto.getPhoneNumber(),
				playerDto.getPhoneNumberServer(),
				playerDto.getGuidReg(),
				playerDto.getStartup(),
				playerDto.getShutdown(),
				playerDto.getLastSyncTime());
		
			players.add(player);
		}
		
		return players;
	}
	
	/// <summary>
	/// Get all players. Guaranteed to not return null.
	/// </summary>
	/// <returns>Returns all MDS System players for the current system.</returns>
	public static PlayerBoCollection getPlayersFromDataStore(){
		// Create the players.
		PlayerBoCollection players;
		players = getPlayersFromPlayerDtos(playerManager.getAll());
		
		players.sort();
		
		return players;
	}
	
	/// <summary>
	/// Gets a list of players corresponding to the specified <paramref name="searchOptions" />.
	/// Guaranteed to not return null.
	/// </summary>
	/// <returns>Iterable{Player}.</returns>
	public static Collection<PlayerItem> getPlayers(){
		List<Player> players = playerManager.getAll();
		List<PlayerItem> playerItems = Lists.newArrayList();
		for(Player player:players) {
			playerItems.add(new PlayerItem(player.getUniqueName(), player.getPlayerName()));
		}
	
		return playerItems;
	}
	
	/// <summary>
	/// Persist this players object to the data store.
	/// </summary>
	public static void save(PlayerBo player){
		Player playersDto = null;
		if (player.isNew()){
			playersDto = new Player();
		}else{
			playersDto = playerManager.get(player.getId());
		}
		
		if (playersDto != null) {
			playersDto.setServerAddress(player.getServerAddress());
			playersDto.setRetries(player.getRetries());
			playersDto.setDescription(player.getDescription());
			playersDto.setLogin(player.getLogin());
			playersDto.setPlayerName(player.getPlayerName());
			playersDto.setPassword(player.getPassword());
			playersDto.setUniqueName(player.getUniqueName());
			playersDto.setPort(player.getPort());
			playersDto.setRetryDelay(player.getRetryDelay());
			playersDto.setUsePASVMode(player.getUsePASVMode());
			playersDto.setUseFirewall(player.getUseFirewall());
			playersDto.setDiskSerial(player.getDiskSerial());
			playersDto.setLocation(player.getLocation());
			playersDto.setFtpPeriod(player.getFtpPeriod());
			playersDto.setBeforeDay(player.getBeforeDay());
			playersDto.setFtpContent(player.getFtpContent());
			playersDto.setReplaceFile(player.getReplaceFile());
			playersDto.setMACAddress(player.getMACAddress());
			playersDto.setMACAddress1(player.getMACAddress1());
			playersDto.setLocalAddress(player.getLocalAddress());
			playersDto.setLocalLogin(player.getLocalLogin());
			playersDto.setLocalPassword(player.getLocalPassword());
			playersDto.setLocalPort(player.getLocalPort());
			playersDto.setTimeOuts(player.getTimeOuts());
			playersDto.setDeviceID(player.getDeviceID());
			playersDto.setMACID(player.getMACID());
			playersDto.setPublicIP(player.getPublicIP());
			playersDto.setBinary(player.getBinary());
			playersDto.setDbLimit(player.getDbLimit());
			playersDto.setConnectionTimeout(player.getConnectionTimeout());
			playersDto.setUserAgent(player.getUserAgent());
			playersDto.setProxyServer(player.getProxyServer());
			playersDto.setConnectionType(player.getConnectionType());
			playersDto.setReadBufferSize(player.getReadBufferSize());
			playersDto.setRegTime(player.getRegTime());
			playersDto.setMDSVersion(player.getMDSVersion());
			playersDto.setOnline(player.getOnline());
			playersDto.setLastOnlineTime(player.getLastOnlineTime());
			playersDto.setPhoneNumber(player.getPhoneNumber());
			playersDto.setPhoneNumberServer(player.getPhoneNumberServer());
			playersDto.setGuidReg(player.getGuidReg());
			playersDto.setStartup(player.getStartup());
			playersDto.setShutdown(player.getShutdown());
			playersDto.setLastSyncTime(player.getLastSyncTime());
		}else {
			throw new BusinessException(MessageFormat.format("Cannot save players: No existing players with Id {0} was found in the database.", player.getId()));
		}
		playerManager.save(playersDto);
	}

	/// <summary>
	/// Permanently delete the current players from the data store, including all related records. This action cannot
	/// be undone.
	/// </summary>
	public static void delete(PlayerBo player)	{
		playerManager.remove(player.getId());
	}

	//#endregion

	//#region Private Static Methods
	/// <summary>
	/// Persists the current user's sort preference for the specified <paramref name="album" />. No action is taken if the 
	/// album is virtual. Anonymous user data is stored in session only; logged on users' data are permanently stored.
	/// </summary>
	/// <param name="album">The album whose sort preference is to be preserved.</param>
	/// <param name="sortByMetaName">Name of the metadata item to sort by.</param>
	/// <param name="sortAscending">Indicates the sort direction.</param>
	/*private static void persistUserSortPreference(AlbumBo album, MetadataItemName sortByMetaName, boolean sortAscending){
		if (album.getIsVirtualAlbum())
			return;

		var profile = ProfileUtils.GetProfile();

		var aProfile = profile.AlbumProfiles.Find(album.getId());

		if (aProfile == null)
		{
			profile.AlbumProfiles.add(new AlbumProfile(album.getId(), sortByMetaName, sortAscending));
		}
		else
		{
			aProfile.SortByMetaName = sortByMetaName;
			aProfile.SortAscending = sortAscending;
		}

		ProfileUtils.SaveProfile(profile);
	}*/

	//#endregion
}