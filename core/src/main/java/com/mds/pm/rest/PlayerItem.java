package com.mds.pm.rest;

/// <summary>
/// A client-optimized object that stores Players.
/// </summary>
public class PlayerItem{
	public PlayerItem() {
		
	}
	
	public PlayerItem(String uniqueName, String playerName) {
		this.UniqueName = uniqueName;
		this.PlayerName = playerName;
	}
	/// <summary>
	/// Gets the unique name, relative to the current player
	/// </summary>
	/// <value>
	/// The player's unique name.
	/// </value>
	public String UniqueName;

	/// <summary>
	/// Gets the player name, relative to the current player
	/// </summary>
	/// <value>The player name.</value>
	public String PlayerName;
}