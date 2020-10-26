/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// A collection of <see cref="UserAccount" /> objects.
/// </summary>
public class UserAccountCollection extends ArrayList<UserAccount>{
	/// <summary>
	/// Initializes a new instance of the <see cref="UserAccountCollection"/> class.
	/// </summary>
	public UserAccountCollection(){	
	}

	/// <summary>
	/// Gets a list of user names for accounts in the collection. This is equivalent to iterating through each <see cref="UserAccount" />
	/// and compiling a String array of the <see cref="UserAccount.UserName" /> properties.
	/// </summary>
	/// <returns>Returns a String array of user names of accounts in the collection.</returns>
	public String[] getUserNames(){
		List<String> users = new ArrayList<String>(size());

		for (UserAccount user : this){
			users.add(user.getUserName());
		}

		return users.toArray(new String[0]);
	}

	/// <summary>
	/// Sort the objects in this collection based on the <see cref="UserAccount.UserName" /> property.
	/// </summary>
	public void Sort(){
		// We know userAccounts is actually a List<UserAccount> because we passed it to the constructor.
		Collections.sort(this);
	}

	/// <summary>
	/// Adds the user accounts to the current collection.
	/// </summary>
	/// <param name="userAccounts">The user accounts to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="userAccounts" /> is null.</exception>
	public void addRange(Iterable<UserAccount> userAccounts){
		if (userAccounts == null)
			throw new ArgumentNullException("userAccounts");

		for (UserAccount userAccount : userAccounts){
			add(userAccount);
		}
	}

	/// <overloads>
	/// Determines whether a user is a member of the collection.
	/// </overloads>
	/// <summary>
	/// Determines whether the <paramref name="item"/> is a member of the collection. An object is considered a member
	/// of the collection if they both have the same <see cref="UserAccount.UserName"/>.
	/// </summary>
	/// <param name="item">An <see cref="UserAccount"/> to determine whether it is a member of the current collection.</param>
	/// <returns>
	/// Returns <c>true</c> if <paramref name="item"/> is a member of the current collection;
	/// otherwise returns <c>false</c>.
	/// </returns>
	public boolean contains(UserAccount item){
		if (item == null)
			return false;

		for(UserAccount userAccountInCollection : this)	{
			if (userAccountInCollection.getUserName().equalsIgnoreCase(item.getUserName())){
				return true;
			}
		}
		
		return false;
	}

	/// <summary>
	/// Determines whether a user account with the specified <paramref name="userName"/> is a member of the collection.
	/// </summary>
	/// <param name="userName">The user name that uniquely identifies the user.</param>
	/// <returns>Returns <c>true</c> if <paramref name="userName"/> is a member of the current collection;
	/// otherwise returns <c>false</c>.</returns>
	public boolean contains(String userName){
		return contains(new UserAccount(userName));
	}

	/// <summary>
	/// Adds the specified user account.
	/// </summary>
	/// <param name="item">The user account to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addUserAccount(UserAccount item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing UserAccountCollection. Items.Count = " + size());

		add(item);
	}

	/// <summary>
	/// Find the user account in the collection that matches the specified <paramref name="userName" />. If no matching object is found,
	/// null is returned.
	/// </summary>
	/// <param name="userName">The user name that uniquely identifies the user.</param>
	/// <returns>Returns an <see cref="UserAccount" />object from the collection that matches the specified <paramref name="userName" />,
	/// or null if no matching object is found.</returns>
	public UserAccount findByUserName(String userName){
		return this.stream().filter(u->u.getUserName().equals(userName)).findFirst().orElse(null);
	}

	/// <summary>
	/// Finds the users whose <see cref="UserAccount.UserName" /> begins with the specified <paramref name="userNameSearchString" />. 
	/// This method can be used to find a set of users that match the first few characters of a String. Returns an empty collection if 
	/// no matches are found. The match is case-insensitive. Example: If <paramref name="userNameSearchString" />="Rob", this method 
	/// returns users with names like "Rob", "Robert", and "robert" but not names such as "Boston Rob".
	/// </summary>
	/// <param name="userNameSearchString">A String to match against the beginning of a <see cref="UserAccount.UserName" />. Do not
	/// specify a wildcard character. If value is null or an empty String, all users are returned.</param>
	/// <returns>Returns an <see cref="UserAccountCollection" />object from the collection where the <see cref="UserAccount.UserName" /> 
	/// begins with the specified <paramref name="userNameSearchString" />, or an empty collection if no matching object is found.</returns>
	public UserAccountCollection findAllByUserName(String userNameSearchString)	{
		UserAccountCollection matchingUsers = new UserAccountCollection();

		if (StringUtils.isBlank(userNameSearchString)){
			matchingUsers.addAll(this);
			
			return matchingUsers;
		}

		for (UserAccount user : this){
			if (StringUtils.startsWithIgnoreCase(user.getUserName(), userNameSearchString))	{
				matchingUsers.add(user);
			}
		}

		return matchingUsers;
	}
}
