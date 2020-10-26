/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.player;

import java.io.Serializable;
import java.util.Date;

import com.mds.aiotplayer.pm.model.Player;
import com.mds.aiotplayer.pm.util.PlayersUtils;


public class PlayerBo  implements Serializable, Comparable<PlayerBo>{
	
	private long uiID;
	private String strServerAddress;
	private short nRetries;
	private String strDescription;
	private String strLogin;
	private String strPlayerName;
	private String strPassword;
	private String strUniqueName;
	private int nPort;
	private short nRetryDelay;
	private boolean bUsePASVMode;
	private boolean bUseFirewall;
	private String strDiskSerial;
	private String strLocation;
	private short nFtpPeriod;
	private short nBeforeDay;
	private int dwFtpContent;
	private boolean bReplaceFile;
	private String strMACAddress;
	private String strMACAddress1;
	private String strLocalAddress;
	private String strLocalLogin;
	private String strLocalPassword;
	private int nLocalPort;
	private String strTimeOuts;
	private String strDeviceID;
	private String strMACID;
	private String strPublicIP;
	private boolean bBinary;
	private long dbLimit;
	private int dwConnectionTimeout;
	private String sUserAgent;
	private String sProxyServer;
	private Short connectionType;
	private Integer dwReadBufferSize;
	private Date dtRegTime;
	private String strMDSVersion;
	private boolean bOnline;
	private Date dtLastOnlineTime;
	private String strPhoneNumber;
	private String strPhoneNumberServer;
	private String guidReg;
	private Date dtStartup;
	private Date dtShutdown;
	private Date dtLastSyncTime;

   
	public long getId()	{
		return this.uiID;
	}
	
	public void setId(long uiID) {
		this.uiID = uiID;
	}

	public String getServerAddress(){
		return this.strServerAddress;
	}
	
	public void setServerAddress(String strServerAddress) {
		this.strServerAddress = strServerAddress;
	}

	public short getRetries()	{
		return this.nRetries;
	}
	
	public void setRetries(short nRetries) {
		this.nRetries = nRetries;
	}

	public String getDescription(){
		return this.strDescription;
	}
	
	public void setDescription(String strDescription) {
		this.strDescription = strDescription;
	}

	public String getLogin()	{
		return this.strLogin;
	}
	
	public void setLogin(String strLogin) {
		this.strLogin = strLogin;
	}

	public String getPlayerName()	{
		return this.strPlayerName;
	}
	
	public void setPlayerName(String strPlayerName) {
		this.strPlayerName = strPlayerName;
	}

	public String getPassword(){
		return this.strPassword;
	}
	
	public void setPassword(String strPassword) {
		this.strPassword = strPassword;
	}

	public String getUniqueName(){
		return this.strUniqueName;
	}
	
	public void setUniqueName(String strUniqueName) {
		this.strUniqueName = strUniqueName;
	}

	public int getPort()	{
		return this.nPort;
	}
	
	public void setPort(int nPort) {
		this.nPort = nPort;
	}

	public short getRetryDelay(){
		return this.nRetryDelay;
	}
	
	public void setRetryDelay(short nRetryDelay) {
		this.nRetryDelay = nRetryDelay;
	}

	public boolean getUsePASVMode()	{
		return this.bUsePASVMode;
	}
	
	public void setUsePASVMode(boolean bUsePASVMode) {
		this.bUsePASVMode = bUsePASVMode;
	}

	public boolean getUseFirewall()	{
		return this.bUseFirewall;
	}
	
	public void setUseFirewall(boolean bUseFirewall) {
		this.bUseFirewall = bUseFirewall;
	}

	public String getDiskSerial()	{
		return this.strDiskSerial;
	}
	
	public void setDiskSerial(String strDiskSerial) {
		this.strDiskSerial = strDiskSerial;
	}

	public String getLocation()	{
		return this.strLocation;
	}
	
	public void setLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public short getFtpPeriod()	{
		return this.nFtpPeriod;
	}
	
	public void setFtpPeriod(short nFtpPeriod) {
		this.nFtpPeriod = nFtpPeriod;
	}

	public short getBeforeDay()	{
		return this.nBeforeDay;
	}
	
	public void setBeforeDay(short nBeforeDay) {
		this.nBeforeDay = nBeforeDay;
	}

	public int getFtpContent()	{
		return this.dwFtpContent;
	}
	
	public void setFtpContent(int dwFtpContent) {
		this.dwFtpContent = dwFtpContent;
	}

	public boolean getReplaceFile()	{
		return this.bReplaceFile;
	}
	
	public void setReplaceFile(boolean bReplaceFile) {
		this.bReplaceFile = bReplaceFile;
	}

	public String getMACAddress()	{
		return this.strMACAddress;
	}
	
	public void setMACAddress(String strMACAddress) {
		this.strMACAddress = strMACAddress;
	}

	public String getMACAddress1()	{
		return this.strMACAddress1;
	}
	
	public void setMACAddress1(String strMACAddress1) {
		this.strMACAddress1 = strMACAddress1;
	}

	public String getLocalAddress()	{
		return this.strLocalAddress;
	}
	
	public void setLocalAddress(String strLocalAddress) {
		this.strLocalAddress = strLocalAddress;
	}

	public String getLocalLogin(){
		return this.strLocalLogin;
	}
	
	public void setLocalLogin(String strLocalLogin) {
		this.strLocalLogin = strLocalLogin;
	}

	public String getLocalPassword()	{
		return this.strLocalPassword;
	}
	
	public void setLocalPassword(String strLocalPassword) {
		this.strLocalPassword = strLocalPassword;
	}

	public int getLocalPort(){
		return this.nLocalPort;
	}
	
	
	public void setLocalPort(int nLocalPort) {
		this.nLocalPort = nLocalPort;
	}

	public String getTimeOuts()	{
		return this.strTimeOuts;
	}
	
	public void setTimeOuts(String strTimeOuts) {
		this.strTimeOuts = strTimeOuts;
	}

	public String getDeviceID()	{
		return this.strDeviceID;
	}
		
	public void setDeviceID(String strDeviceID) {
		this.strDeviceID = strDeviceID;
	}

	public String getMACID(){
		return this.strMACID;
	}
	
	public void setMACID(String strMACID) {
		this.strMACID = strMACID;
	}

	public String getPublicIP()	{
		return this.strPublicIP;
	}
	
	public void setPublicIP(String strPublicIP) {
		this.strPublicIP = strPublicIP;
	}

	public boolean getBinary(){
		return this.bBinary;
	}
	
	public void setBinary(boolean bBinary) {
		this.bBinary = bBinary;
	}

	public long getDbLimit(){
		return this.dbLimit;
	}
	
	public void setDbLimit(long dbLimit) {
		this.dbLimit = dbLimit;
	}

	public int getConnectionTimeout(){
		return this.dwConnectionTimeout;
	}
	
	public void setConnectionTimeout(int dwConnectionTimeout) {
		this.dwConnectionTimeout = dwConnectionTimeout;
	}

	public String getUserAgent(){
		return this.sUserAgent;
	}
	
	public void setUserAgent(String sUserAgent) {
		this.sUserAgent = sUserAgent;
	}

	public String getProxyServer()	{
		return this.sProxyServer;
	}
	
	public void setProxyServer(String sProxyServer) {
		this.sProxyServer = sProxyServer;
	}

	public Short getConnectionType(){
		return this.connectionType;
	}
	
	public void setConnectionType(Short connectionType) {
		this.connectionType = connectionType;
	}

	public Integer getReadBufferSize()	{
		return this.dwReadBufferSize;
	}
	
	public void setReadBufferSize(Integer dwReadBufferSize) {
		this.dwReadBufferSize = dwReadBufferSize;
	}

	public Date getRegTime(){
		return this.dtRegTime;
	}
	
	public void setRegTime(Date dtRegTime) {
		this.dtRegTime = dtRegTime;
	}

	public String getMDSVersion(){
		return this.strMDSVersion;
	}
	
	public void setMDSVersion(String strMDSVersion) {
		this.strMDSVersion = strMDSVersion;
	}

	public boolean getOnline(){
		return this.bOnline;
	}
	
	public void setOnline(boolean bOnline) {
		this.bOnline = bOnline;
	}

	public Date getLastOnlineTime(){
		return this.dtLastOnlineTime;
	}
	
	public void setLastOnlineTime(Date dtLastOnlineTime) {
		this.dtLastOnlineTime = dtLastOnlineTime;
	}

	public String getPhoneNumber(){
		return this.strPhoneNumber;
	}
	
	public void setPhoneNumber(String strPhoneNumber) {
		this.strPhoneNumber = strPhoneNumber;
	}

	public String getPhoneNumberServer(){
		return this.strPhoneNumberServer;
	}
	
	public void setPhoneNumberServer(String strPhoneNumberServer) {
		this.strPhoneNumberServer = strPhoneNumberServer;
	}

	public String getGuidReg(){
		return this.guidReg;
	}
	
	public void setGuidReg(String guidReg) {
		this.guidReg = guidReg;
	}

	public Date getStartup(){
		return this.dtStartup;
	}
	
	public void setStartup(Date dtStartup) {
		this.dtStartup = dtStartup;
	}

	public Date getShutdown()	{
		return this.dtShutdown;
	}
	
	public void setShutdown(Date dtShutdown) {
		this.dtShutdown = dtShutdown;
	}

	public Date getLastSyncTime()	{
		return this.dtLastSyncTime;
	}
	
	public void setLastSyncTime(Date dtLastSyncTime) {
		this.dtLastSyncTime = dtLastSyncTime;
	}

	
	/// <summary>
	/// Initializes a new instance of the <see cref="PlayerBo"/> class.
	/// </summary>
	public PlayerBo()
	{
		this.uiID = Long.MIN_VALUE;
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="PlayerBo"/> class from variables.
	/// </summary>
	public PlayerBo(long id, String strServerAddress, short nRetries, String strDescription, String strLogin, String strPlayerName, String strPassword, String strUniqueName
		, int nPort, short nRetryDelay, boolean bUsePASVMode, boolean bUseFirewall, String strDiskSerial, String strLocation, short nFtpPeriod, short nBeforeDay, int dwFtpContent
		, boolean bReplaceFile, String strMACAddress, String strMACAddress1, String strLocalAddress, String strLocalLogin, String strLocalPassword, int nLocalPort, String strTimeOuts
		, String strDeviceID, String strMACID, String strPublicIP, boolean bBinary, long dbLimit, int dwConnectionTimeout, String sUserAgent, String sProxyServer, short connectionType
		, int dwReadBufferSize, Date dtRegTime, String strMDSVersion, boolean bOnline, Date dtLastOnlineTime, String strPhoneNumber, String strPhoneNumberServer, String guidReg
		, Date dtStartup, Date dtShutdown, Date dtLastSyncTime)
	{
		this.uiID = id;
		this.strServerAddress = strServerAddress;
		this.nRetries = nRetries;
		this.strDescription = strDescription;
		this.strLogin = strLogin;
		this.strPlayerName = strPlayerName;
		this.strPassword = strPassword;
		this.strUniqueName = strUniqueName;
		this.nPort = nPort;
		this.nRetryDelay = nRetryDelay;
		this.bUsePASVMode = bUsePASVMode;
		this.bUseFirewall = bUseFirewall;
		this.strDiskSerial = strDiskSerial;
		this.strLocation = strLocation;
		this.nFtpPeriod = nFtpPeriod;
		this.nBeforeDay = nBeforeDay;
		this.dwFtpContent = dwFtpContent;
		this.bReplaceFile = bReplaceFile;
		this.strMACAddress = strMACAddress;
		this.strMACAddress1 = strMACAddress1;
		this.strLocalAddress = strLocalAddress;
		this.strLocalLogin = strLocalLogin;
		this.strLocalPassword = strLocalPassword;
		this.nLocalPort = nLocalPort;
		this.strTimeOuts = strTimeOuts;
		this.strDeviceID = strDeviceID;
		this.strMACID = strMACID;
		this.strPublicIP = strPublicIP;
		this.bBinary = bBinary;
		this.dbLimit = dbLimit;
		this.dwConnectionTimeout = dwConnectionTimeout;
		this.sUserAgent = sUserAgent;
		this.sProxyServer = sProxyServer;
		this.connectionType = connectionType;
		this.dwReadBufferSize = dwReadBufferSize;
		this.dtRegTime = dtRegTime;
		this.strMDSVersion = strMDSVersion;
		this.bOnline = bOnline;
		this.dtLastOnlineTime = dtLastOnlineTime;
		this.strPhoneNumber = strPhoneNumber;
		this.strPhoneNumberServer = strPhoneNumberServer;
		this.guidReg = guidReg;
		this.dtStartup = dtStartup;
		this.dtShutdown = dtShutdown;
		this.dtLastSyncTime = dtLastSyncTime;

	}

	/// <summary>
	/// Initializes a new instance of the <see cref="PlayerBo"/> class from the data stroe.
	/// </summary>
	public PlayerBo(Player playersDto){
		copyFrom(playersDto);
	}

	/// <summary>
	/// copy from players data.
	/// </summary>
	public void copyFrom(Player playersDto){
		if (playersDto != null)
		{
			this.setId(playersDto.getId());
			this.setServerAddress(playersDto.getServerAddress());
			this.setRetries(playersDto.getRetries());
			this.setDescription(playersDto.getDescription());
			this.setLogin(playersDto.getLogin());
			this.setPlayerName(playersDto.getPlayerName());
			this.setPassword(playersDto.getPassword());
			this.setUniqueName(playersDto.getUniqueName());
			this.setPort(playersDto.getPort());
			this.setRetryDelay(playersDto.getRetryDelay());
			this.setUsePASVMode(playersDto.isUsePASVMode());
			this.setUseFirewall(playersDto.isUseFirewall());
			this.setDiskSerial(playersDto.getDiskSerial());
			this.setLocation(playersDto.getLocation());
			this.setFtpPeriod(playersDto.getFtpPeriod());
			this.setBeforeDay(playersDto.getBeforeDay());
			this.setFtpContent(playersDto.getFtpContent());
			this.setReplaceFile(playersDto.isReplaceFile());
			this.setMACAddress(playersDto.getMACAddress());
			this.setMACAddress1(playersDto.getMACAddress1());
			this.setLocalAddress(playersDto.getLocalAddress());
			this.setLocalLogin(playersDto.getLocalLogin());
			this.setLocalPassword(playersDto.getLocalPassword());
			this.setLocalPort(playersDto.getLocalPort());
			this.setTimeOuts(playersDto.getTimeOuts());
			this.setDeviceID(playersDto.getDeviceID());
			this.setMACID(playersDto.getMACID());
			this.setPublicIP(playersDto.getPublicIP());
			this.setBinary(playersDto.isBinary());
			this.setDbLimit(playersDto.getDbLimit());
			this.setConnectionTimeout(playersDto.getConnectionTimeout());
			this.setUserAgent(playersDto.getUserAgent());
			this.setProxyServer(playersDto.getProxyServer());
			this.setConnectionType(playersDto.getConnectionType());
			this.setReadBufferSize(playersDto.getReadBufferSize());
			this.setRegTime(playersDto.getRegTime());
			this.setMDSVersion(playersDto.getMDSVersion());
			this.setOnline(playersDto.isOnline());
			this.setLastOnlineTime(playersDto.getLastOnlineTime());
			this.setPhoneNumber(playersDto.getPhoneNumber());
			this.setPhoneNumberServer(playersDto.getPhoneNumberServer());
			this.setGuidReg(playersDto.getGuidReg());
			this.setStartup(playersDto.getStartup());
			this.setShutdown(playersDto.getShutdown());
			this.setLastSyncTime(playersDto.getLastSyncTime());

		}
	}

	/// <summary>
	/// copy to players data.
	/// </summary>
	public void copyTo(Player playersDto){
		if (playersDto != null)	{
			playersDto.setId(this.getId());
			playersDto.setServerAddress(this.getServerAddress());
			playersDto.setRetries(this.getRetries());
			playersDto.setDescription(this.getDescription());
			playersDto.setLogin(this.getLogin());
			playersDto.setPlayerName(this.getPlayerName());
			playersDto.setPassword(this.getPassword());
			playersDto.setUniqueName(this.getUniqueName());
			playersDto.setPort(this.getPort());
			playersDto.setRetryDelay(this.getRetryDelay());
			playersDto.setUsePASVMode(this.getUsePASVMode());
			playersDto.setUseFirewall(this.getUseFirewall());
			playersDto.setDiskSerial(this.getDiskSerial());
			playersDto.setLocation(this.getLocation());
			playersDto.setFtpPeriod(this.getFtpPeriod());
			playersDto.setBeforeDay(this.getBeforeDay());
			playersDto.setFtpContent(this.getFtpContent());
			playersDto.setReplaceFile(this.getReplaceFile());
			playersDto.setMACAddress(this.getMACAddress());
			playersDto.setMACAddress1(this.getMACAddress1());
			playersDto.setLocalAddress(this.getLocalAddress());
			playersDto.setLocalLogin(this.getLocalLogin());
			playersDto.setLocalPassword(this.getLocalPassword());
			playersDto.setLocalPort(this.getLocalPort());
			playersDto.setTimeOuts(this.getTimeOuts());
			playersDto.setDeviceID(this.getDeviceID());
			playersDto.setMACID(this.getMACID());
			playersDto.setPublicIP(this.getPublicIP());
			playersDto.setBinary(this.getBinary());
			playersDto.setDbLimit(this.getDbLimit());
			playersDto.setConnectionTimeout(this.getConnectionTimeout());
			playersDto.setUserAgent(this.getUserAgent());
			playersDto.setProxyServer(this.getProxyServer());
			playersDto.setConnectionType(this.getConnectionType());
			playersDto.setReadBufferSize(this.getReadBufferSize());
			playersDto.setRegTime(this.getRegTime());
			playersDto.setMDSVersion(this.getMDSVersion());
			playersDto.setOnline(this.getOnline());
			playersDto.setLastOnlineTime(this.getLastOnlineTime());
			playersDto.setPhoneNumber(this.getPhoneNumber());
			playersDto.setPhoneNumberServer(this.getPhoneNumberServer());
			playersDto.setGuidReg(this.getGuidReg());
			playersDto.setStartup(this.getStartup());
			playersDto.setShutdown(this.getShutdown());
			playersDto.setLastSyncTime(this.getLastSyncTime());

		}
	}

	/// <summary>
	/// Gets a value indicating whether this object is new and has not yet been persisted to the data store.
	/// </summary>
	/// <value><c>true</c> if this instance is new; otherwise, <c>false</c>.</value>
	public boolean isNew(){
		return (this.uiID == Long.MIN_VALUE);
	}

	//#region Public Methods

	/// <summary>
	/// Creates a deep copy of this instance.
	/// </summary>
	/// <returns>Returns a deep copy of this instance.</returns>
	public PlayerBo copy(){
		PlayerBo playersCopy = new PlayerBo();
		playersCopy.setId(this.getId());
		playersCopy.setServerAddress(this.getServerAddress());
		playersCopy.setRetries(this.getRetries());
		playersCopy.setDescription(this.getDescription());
		playersCopy.setLogin(this.getLogin());
		playersCopy.setPlayerName(this.getPlayerName());
		playersCopy.setPassword(this.getPassword());
		playersCopy.setUniqueName(this.getUniqueName());
		playersCopy.setPort(this.getPort());
		playersCopy.setRetryDelay(this.getRetryDelay());
		playersCopy.setUsePASVMode(this.getUsePASVMode());
		playersCopy.setUseFirewall(this.getUseFirewall());
		playersCopy.setDiskSerial(this.getDiskSerial());
		playersCopy.setLocation(this.getLocation());
		playersCopy.setFtpPeriod(this.getFtpPeriod());
		playersCopy.setBeforeDay(this.getBeforeDay());
		playersCopy.setFtpContent(this.getFtpContent());
		playersCopy.setReplaceFile(this.getReplaceFile());
		playersCopy.setMACAddress(this.getMACAddress());
		playersCopy.setMACAddress1(this.getMACAddress1());
		playersCopy.setLocalAddress(this.getLocalAddress());
		playersCopy.setLocalLogin(this.getLocalLogin());
		playersCopy.setLocalPassword(this.getLocalPassword());
		playersCopy.setLocalPort(this.getLocalPort());
		playersCopy.setTimeOuts(this.getTimeOuts());
		playersCopy.setDeviceID(this.getDeviceID());
		playersCopy.setMACID(this.getMACID());
		playersCopy.setPublicIP(this.getPublicIP());
		playersCopy.setBinary(this.getBinary());
		playersCopy.setDbLimit(this.getDbLimit());
		playersCopy.setConnectionTimeout(this.getConnectionTimeout());
		playersCopy.setUserAgent(this.getUserAgent());
		playersCopy.setProxyServer(this.getProxyServer());
		playersCopy.setConnectionType(this.getConnectionType());
		playersCopy.setReadBufferSize(this.getReadBufferSize());
		playersCopy.setRegTime(this.getRegTime());
		playersCopy.setMDSVersion(this.getMDSVersion());
		playersCopy.setOnline(this.getOnline());
		playersCopy.setLastOnlineTime(this.getLastOnlineTime());
		playersCopy.setPhoneNumber(this.getPhoneNumber());
		playersCopy.setPhoneNumberServer(this.getPhoneNumberServer());
		playersCopy.setGuidReg(this.getGuidReg());
		playersCopy.setStartup(this.getStartup());
		playersCopy.setShutdown(this.getShutdown());
		playersCopy.setLastSyncTime(this.getLastSyncTime());

		return playersCopy;

	}

	/// <summary>
	/// Persist this players object to the data store.
	/// </summary>
	public void save(){
		PlayersUtils.save(this);
	}

	/// <summary>
	/// Permanently delete the current players from the data store, including all related records. This action cannot
	/// be undone.
	/// </summary>
	public void delete(){
		PlayersUtils.delete(this);
	}

	//#endregion

	//#region IComparable Members

	/// <summary>
	/// Compares the current instance with another object of the same type.
	/// </summary>
	/// <param name="obj">An object to compare with this instance.</param>
	/// <returns>
	/// A 32-bit signed integer that indicates the relative order of the objects being compared. The return value has these meanings: Value Meaning Less than zero This instance is less than <paramref name="obj"/>. Zero This instance is equal to <paramref name="obj"/>. Greater than zero This instance is greater than <paramref name="obj"/>.
	/// </returns>
	/// <exception cref="T:System.ArgumentException">
	/// 	<paramref name="obj"/> is not the same type as this instance. </exception>
	public int compareTo(PlayerBo obj){
		if (obj == null)
			return 1;
		else{
			return Long.compare(this.getId(), obj.getId());
		}
	}

	//#endregion
}
