package com.mds.cm.content;

import java.io.Serializable;
import java.util.Date;

import com.mds.core.ApprovalAction;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.ArgumentOutOfRangeException;

/// <summary>
/// Represents a approval status for a content object.
/// </summary>
public class ContentObjectApproval implements Serializable, Comparable<ContentObjectApproval>{
	//#region Private Fields

	private long id;
	private String approveBy;
	private Date approveDate;
	private int seq;
	private ApprovalAction approvalAction;
	private boolean isNew;
	private boolean hasChanges;
	
	private ContentObjectBo contentObject;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="approvalAction"/> class with the specified <paramref name="userName" />.
	/// All other properties are left at default values.
	/// </summary>
	/// <param name="userName">The logon name of the membership user.</param>
	public ContentObjectApproval(ContentObjectBo contentObject)	{
		this.contentObject = contentObject;
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectApproval"/> class.
	/// </summary>
	/// <param name="id">Application-specific information for the membership user.</param>
	/// <param name="uiContentID">The date and time when the user was added to the membership data store.</param>
	/// <param name="uiUserID">The e-mail address for the membership user.</param>
	/// <param name="seq">Indicates whether the membership user can be authenticated.</param>
	/// <param name="strUserCode">Indicates whether the membership user is locked out.</param>
	/// <param name="strGroupCode">Indicates whether the membership user is online.</param>
	/// <param name="dtCreateDate">The date and time when the membership user was last authenticated or accessed the application.</param>
	/// <param name="dtLastModify">The most recent date and time that the membership user was locked out.</param>
	/// <param name="approvalAction">The date and time when the user was last authenticated.</param>
	public ContentObjectApproval(long id, ContentObjectBo contentObject, String approveBy, int seq, ApprovalAction approvalAction, Date approveDate, boolean hasChanges)	{
		//, String strUserCode, String strGroupCode, 
		//  Date dtCreateDate, Date dtLastModify
		this.id = id;
		this.contentObject = contentObject;
		this.approveBy = approveBy;
		this.approveDate = approveDate;
		this.seq = seq;
		this.approvalAction = approvalAction;
		this.hasChanges = hasChanges;
	}

	//#endregion

	//#region Public Properties

	public long getId()	{
		return this.id;
	}
	
	public void setId(long id)	{
		this.id = id;
	}

	public String getApproveBy(){
		return this.approveBy;
	}
	
	public void setApproveBy(String approveBy){
		if (this.approveBy != approveBy){
			this.approveBy = approveBy;
			this.hasChanges = true;
			contentObject.setHasChanges(true);
		}
	}

	public int getSeq()	{
		return this.seq;
	}
	
	public void setSeq(int seq)	{
		if (this.seq != seq){
			this.seq = seq;
			this.hasChanges = true;
			contentObject.setHasChanges(true);
		}
	}

	public ApprovalAction getApprovalAction(){
		return this.approvalAction;
	}
	
	public void setApprovalAction(ApprovalAction approvalAction){
		if (this.approvalAction != approvalAction){
			this.approvalAction = approvalAction;
			this.hasChanges = true;
			contentObject.setHasChanges(true);
		}
	}
	
	public Date getApproveDate(){
		return this.approveDate;
	}
	
	public void setApproveDate(Date approveDate){
		if (!this.approveDate.equals(approveDate)){
			this.approveDate = approveDate;
			this.hasChanges = true;
			contentObject.setHasChanges(true);
		}
	}

	/// <summary>
	/// Gets or sets a value indicating whether this object has changes that have not been persisted to the database.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance has changes; otherwise, <c>false</c>.
	/// </value>
	public boolean hasChanges(){
			return this.hasChanges;
	}
	
	public void setChanges(boolean hasChanges){
		this.hasChanges = hasChanges;
	}

	/// <summary>
	/// Gets a value indicating whether this object is new and has not yet been persisted to the data store.
	/// </summary>
	/// <value><c>true</c> if this instance is new; otherwise, <c>false</c>.</value>
	public boolean isNew(){
		return this.isNew;
	}
	
	public void setNew(boolean isNew){
		this.isNew = isNew;
	}
	
	/// <summary>
    /// Gets or sets the object this instance applies to.
    /// </summary>
    /// <value>The object this instance applies to.</value>
    public ContentObjectBo getCpntentObject() { 
    	return this.contentObject; 
    }
    
    public void setCpntentObject(ContentObjectBo contentObject) { 
    	this.contentObject = contentObject; 
    }
	
	//#endregion

	//#region Public Methods

	/// <summary>
	/// Copies the current account information to the specified <paramref name="approval" />. The <paramref name="approval" />
	/// must be able to be cast to an instance of <see cref="ContentObjectApproval" />. If not, an <see cref="ArgumentNullException" />
	/// is thrown.
	/// </summary>
	/// <param name="approval">The user account to populate with information from the current instance.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="approval" /> is null.</exception>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="approval" /> cannot be cast to an instance of 
	/// <see cref="ContentObjectApproval" />.</exception>
	public void copyTo(ContentObjectApproval approval){
		if (approval == null)
			throw new ArgumentNullException("approval");

		try{
			copyToInstance(approval);
		}catch (ArgumentNullException ae){
			throw new ArgumentOutOfRangeException("approval", "The parameter 'approval' cannot be cast to an instance of ContentObjectApproval.");
		}
	}

	//#endregion

	//#region Private Functions

	/// <summary>
	/// Copies the current account information to the specified <paramref name="approval" />.
	/// </summary>
	/// <param name="approval">The user account to populate with information from the current instance.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="approval" /> is null.</exception>
	private void copyToInstance(ContentObjectApproval approval)	{
		if (approval == null)
			throw new ArgumentNullException("approval");

		approval.id = this.id;
		approval.contentObject = this.contentObject;
		approval.approveBy = this.approveBy;
		approval.approveDate = this.approveDate;
		approval.seq = this.seq;
		approval.approvalAction = this.approvalAction;
	}

	//#endregion

	//#region IComparable

	/// <summary>
	/// Compares the current instance with another object of the same type.
	/// </summary>
	/// <param name="obj">An object to compare with this instance.</param>
	/// <returns>
	/// A 32-bit signed integer that indicates the relative order of the objects being compared. The return value has these meanings: Value Meaning Less than zero This instance is less than <paramref name="obj"/>. Zero This instance is equal to <paramref name="obj"/>. Greater than zero This instance is greater than <paramref name="obj"/>.
	/// </returns>
	/// <exception cref="T:System.ArgumentException">
	/// 	<paramref name="obj"/> is not the same type as this instance. </exception>
	public int compareTo(ContentObjectApproval obj){
		if (obj == null)
			return 1;
		else{
			return Integer.compare(seq, obj.getSeq());
		}
	}

	//#endregion
}
