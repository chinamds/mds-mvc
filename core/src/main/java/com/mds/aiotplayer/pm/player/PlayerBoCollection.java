package com.mds.aiotplayer.pm.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.mds.aiotplayer.core.exception.ArgumentNullException;

/// <summary>
/// A collection of <see cref="PlayerBo" /> objects.
/// </summary>
public class PlayerBoCollection extends ArrayList<PlayerBo>{
	/// <summary>
	/// Initializes a new instance of the <see cref="PlayerBoCollection"/> class.
	/// </summary>
	public PlayerBoCollection()	{
		super();
	}

	/// <summary>
	/// Sort the objects in this collection based on the <see cref="PlayerBo.Id" /> property.
	/// </summary>
	public void sort(){
		// We know playerss is actually a List<PlayerBo> because we passed it to the constructor.
		Collections.sort(this);
	}

	/// <summary>
	/// Adds the playerss to the current collection.
	/// </summary>
	/// <param name="playerss">The playerss to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="playerss" /> is null.</exception>
	public void addRange(Collection<PlayerBo> players){
		if (players == null)
			throw new ArgumentNullException("playerss");

		addAll(players);
	}

	/// <summary>
	/// Find the players in the collection that matches the specified <paramref name="playersId" />. If no matching object is found,
	/// null is returned.
	/// </summary>
	/// <param name="playersId">The ID that uniquely identifies the players.</param>
	/// <returns>Returns an <see cref="PlayerBo" />object from the collection that matches the specified <paramref name="playersId" />,
	/// or null if no matching object is found.</returns>
	public PlayerBo findById(int playersId){
		return this.stream().filter(p->p.getId() == playersId).findFirst().orElse(null);
	}

	/// <summary>
	/// Creates a new, empty instance of an <see cref="PlayerBo" /> object. This method can be used by code that only has a 
	/// reference to the interface layer and therefore cannot create a new instance of an object on its own.
	/// </summary>
	/// <returns>Returns a new, empty instance of an <see cref="PlayerBo" /> object.</returns>
	public PlayerBo createEmptyPlayerInstance(){
		return new PlayerBo();
	}

	/// <summary>
	/// Creates a new, empty instance of an <see cref="PlayerBoCollection" /> object. This method can be used by code that only has a 
	/// reference to the interface layer and therefore cannot create a new instance of an object on its own.
	/// </summary>
	/// <returns>Returns a new, empty instance of an <see cref="PlayerBoCollection" /> object.</returns>
	public PlayerBoCollection CreateEmptyPlayerCollection(){
		return new PlayerBoCollection();
	}

	/// <summary>
	/// Creates a new collection containing deep copies of the items it contains.
	/// </summary>
	/// <returns>Returns a new collection containing deep copies of the items it contains.</returns>
	public PlayerBoCollection copy(){
		PlayerBoCollection copy = new PlayerBoCollection();
		for (PlayerBo players : this){
			copy.add(players.copy());
		}

		return copy;
	}

	/// <summary>
	/// Determines whether the <paramref name="item"/> is already a member of the collection. An object is considered a member
	/// of the collection if they both have the same <see cref="PlayerBo.PlayersId" />.
	/// </summary>
	/// <param name="item">An <see cref="PlayerBo"/> to determine whether it is a member of the current collection.</param>
	/// <returns>Returns <c>true</c> if <paramref name="item"/> is a member of the current collection;
	/// otherwise returns <c>false</c>.</returns>
	public boolean contains(PlayerBo item){
		if (item == null)
			return false;

		for (PlayerBo playersInCollection : this){
			if (playersInCollection.getId() == item.getId()){
				return true;
			}
		}
		
		return false;
	}

	/// <summary>
	/// Adds the specified players.
	/// </summary>
	/// <param name="item">The players to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addPlayer(PlayerBo item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing PlayerBoCollection. Items.Count = " + size());

		add(item);
	}
}
