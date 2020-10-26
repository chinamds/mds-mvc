/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.model.ContentActivity;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.core.exception.ArgumentNullException;

/// <summary>
/// A collection of <see cref="ContentObjectApproval" /> objects.
/// </summary>
public class ContentObjectApprovalCollection extends ArrayList<ContentObjectApproval>{
	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectApprovalCollection"/> class.
	/// </summary>
	public ContentObjectApprovalCollection(){
		super();
	}

	/// <summary>
	/// Gets a list of user names for accounts in the collection. This is equivalent to iterating through each <see cref="ContentObjectApproval" />
	/// and compiling a String array of the <see cref="ContentObjectApproval.UserName" /> properties.
	/// </summary>
	/// <returns>Returns a String array of user names of accounts in the collection.</returns>
	public String[] getUserNames(){
		List<String> users = Lists.newArrayList(); 
		for (ContentObjectApproval approval : this){
			users.add(approval.getApproveBy());
		}

		return users.toArray(new String[0]);
	}

	/// <summary>
	/// Sort the objects in this collection based on the <see cref="ContentObjectApproval.UserName" /> property.
	/// </summary>
	public void sort(){
		Collections.sort(this);
	}

	/// <summary>
	/// Adds the user accounts to the current collection.
	/// </summary>
	/// <param name="approvals">The user accounts to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="approvals" /> is null.</exception>
	public void addRange(Collection<ContentObjectApproval> approvals){
		if (approvals == null)
			throw new ArgumentNullException("approvals");

		addAll(approvals);
	}

	/// <overloads>
	/// Determines whether a user is a member of the collection.
	/// </overloads>
	/// <summary>
	/// Determines whether the <paramref name="item"/> is a member of the collection. An object is considered a member
	/// of the collection if they both have the same <see cref="ContentObjectApproval.UserName"/>.
	/// </summary>
	/// <param name="item">An <see cref="ContentObjectApproval"/> to determine whether it is a member of the current collection.</param>
	/// <returns>
	/// Returns <c>true</c> if <paramref name="item"/> is a member of the current collection;
	/// otherwise returns <c>false</c>.
	/// </returns>
	public boolean contains(ContentObjectApproval item){
		if (item == null)
			return false;

		for (ContentObjectApproval approvalInCollection : this)	{
			if (approvalInCollection.getApproveBy().equals(item.getApproveBy()) && approvalInCollection.getApproveDate().equals(item.getApproveDate())){
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
	public boolean contains(String userName) {
		for (ContentObjectApproval approvalInCollection : this)	{
			if (approvalInCollection.getApproveBy().equals(userName)){
				return true;
			}
		}

		return false;
	}

	/// <summary>
	/// Adds the specified user approval.
	/// </summary>
	/// <param name="item">The user approval to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addApproval(ContentObjectApproval item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing ContentObjectApprovalCollection. Items.Count = " + size());

		item.getCpntentObject().setHasChanges(true);

		add(item);
	}

	/// <summary>
	/// Converts the <paramref name="approvalDtos" /> to an instance of <see cref="ContentObjectApprovalCollection" /> and
	/// returns it. An empty collection is returned if <paramref name="approvalDtos" /> is null or empty. Guaranteed to not return null.
	/// </summary>
	/// <param name="contentObject">The gallery object the <paramref name="approvalDtos" /> belong to.</param>
	/// <param name="approvalDtos">An enumerable collection of <see cref="Data.ApprovalDto" /> instances.</param>
	/// <returns>An instance of <see cref="ContentObjectApprovalCollection" />.</returns>
	public static ContentObjectApprovalCollection fromApprovalDtos(ContentObjectBo contentObject, Collection<ContentActivity> approvalDtos)
	{
		ContentObjectApprovalCollection approvaldata = CMUtils.createApprovalCollection();

		if (approvalDtos != null){
			for (ContentActivity mDto : approvalDtos){
				approvaldata.add(CMUtils.createApprovalItem(
					mDto.getId(), 
					contentObject,
					mDto.getUser().getUsername(),
					mDto.getWorkflowDetail().getSeq(),
					mDto.getApprovalAction(),
					mDto.getDateAdded(),
					false));
			}
		}

		return approvaldata;
	}

	/// <summary>
	/// Gets the <see cref="ContentObjectApproval"/> object that matches the specified
	/// <see cref="MetadataItemName"/>. The <paramref name="metadataItem"/>
	/// parameter remains null if no matching object is in the collection.
	/// </summary>
	/// <param name="userName">The <see cref="MetadataItemName"/> of the
	/// <see cref="ContentObjectApproval"/> to get.</param>
	/// <param name="metadataItem">When this method returns, contains the <see cref="ContentObjectBoMetadataItem"/> associated with the
	/// specified <see cref="MetadataItemName"/>, if the key is found; otherwise, the
	/// parameter remains null. This parameter is passed uninitialized.</param>
	/// <returns>
	/// Returns true if the <see cref="ContentObjectApprovalCollection"/> contains an element with the specified
	/// <see cref="UserName"/>; otherwise, false.
	/// </returns>
	public ContentObjectApproval tryGetApprovalItem(String userName){
		ContentObjectApproval approval = null;
		if (!isEmpty())	{
			// We know contentObjectApprovals is actually a List<ContentObjectApproval> because we passed it to the constructor.
			Collections.sort(this);
			
			//if (String.Compare(contentObjectApprovals[0].ApproveBy, userName, StringComparison.Ordinal) == 0)
			/*{
				approval = contentObjectApprovals[Items.Count-1];
			}*/
			return this.get(this.size() - 1);
		}

		return approval;
	}

	public ContentObjectApproval getLatestApprovalItem(){
		// We know contentObjectMetadataItems is actually a List<ContentObjectBoMetadataItem> because we passed it to the constructor.
		if (!isEmpty())	{
			Collections.sort(this);

			return this.get(this.size() - 1);
		}


		return null;
	}
}
