/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.beust.jcommander.internal.Lists;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.UserAction;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.UserStatus;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Represents a user in the current application.
/// </summary>
@SuppressWarnings("serial")
public class UserAccount implements Comparable<UserAccount>,  UserDetails{
	//#region Private Fields

    private Long id;
    private String tenantId;
	private String comment;
	private Date creationDate;
	private String email;
	private boolean isApproved;
	private boolean isLockedOut;
	private boolean isOnline;
	private Date lastActivityDate;
	private Date lastLockoutDate;
	private Date lastLoginDate;
	private Date lastPasswordChangedDate;
	private String passwordQuestion;
	private String providerName;
	private Object providerUserKey;
	private String userName;
	 private String password;
	private boolean isSuperUser;
	private String firstName;
	private String lastName;
	private String displayName;
	
	private Byte type;
    private int status = UserStatus.disabled.getStatus();
    private long organizationId=Long.MIN_VALUE; //Owner organization Id
    private long staffId=Long.MIN_VALUE; //Owner staff Id
    private Integer version;

    /** 登录时间. */
    private Date loginTime;

    private MDSRoleCollection roles;
    private Set<GrantedAuthority> grantedAuthorities = null;
    private List<String> authorizationInfo = null;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="UserAccount"/> class with the specified <paramref name="userName" />.
	/// All other properties are left at default values.
	/// </summary>
	/// <param name="userName">The logon name of the membership user.</param>
	public UserAccount(String userName){
		this.userName = userName;
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="UserAccount"/> class.
	/// </summary>
	/// <param name="comment">Application-specific information for the membership user.</param>
	/// <param name="creationDate">The date and time when the user was added to the membership data store.</param>
	/// <param name="email">The e-mail address for the membership user.</param>
	/// <param name="isApproved">Indicates whether the membership user can be authenticated.</param>
	/// <param name="isLockedOut">Indicates whether the membership user is locked out.</param>
	/// <param name="isOnline">Indicates whether the membership user is online.</param>
	/// <param name="lastActivityDate">The date and time when the membership user was last authenticated or accessed the application.</param>
	/// <param name="lastLockoutDate">The most recent date and time that the membership user was locked out.</param>
	/// <param name="lastLoginDate">The date and time when the user was last authenticated.</param>
	/// <param name="lastPasswordChangedDate">The date and time when the membership user's password was last updated.</param>
	/// <param name="passwordQuestion">The password question for the membership user.</param>
	/// <param name="providerName">The name of the membership provider that stores and retrieves user information for the membership user.</param>
	/// <param name="providerUserKey">The user identifier from the membership data source for the user.</param>
	/// <param name="userName">The logon name of the membership user.</param>
	/// <param name="isSuperUser">Indicates whether the user has no restrictions on actions. DotNetNuke only. Specify <c>false</c> for 
	/// non-DotNetNuke versions.</param>
	/// <param name="firstName">The first name. DotNetNuke only. Specify <see cref="StringUtils.EMPTY" /> for non-DotNetNuke versions.</param>
	/// <param name="lastName">The last name. DotNetNuke only. Specify <see cref="StringUtils.EMPTY" /> for non-DotNetNuke versions.</param>
	/// <param name="displayName">The display name. DotNetNuke only. Specify <see cref="StringUtils.EMPTY" /> for non-DotNetNuke versions.</param>
	public UserAccount(String comment, Date creationDate, String email, boolean isApproved, boolean isLockedOut, boolean isOnline, Date lastActivityDate
			, Date lastLockoutDate, Date lastLoginDate, Date lastPasswordChangedDate, String passwordQuestion, String providerName, Object providerUserKey
			, String userName, boolean isSuperUser, String firstName, String lastName, String displayName)	{
		this.comment = comment;
		this.creationDate = creationDate;
		this.email = email;
		this.isApproved = isApproved;
		this.isLockedOut = isLockedOut;
		this.isOnline = isOnline;
		this.lastActivityDate = lastActivityDate;
		this.lastLockoutDate = lastLockoutDate;
		this.lastLoginDate = lastLoginDate;
		this.lastPasswordChangedDate = lastPasswordChangedDate;
		this.passwordQuestion = passwordQuestion;
		this.providerName = providerName;
		this.providerUserKey = providerUserKey;
		this.userName = userName;
		this.isSuperUser = isSuperUser;
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = displayName;
	}

	//#endregion

	//#region Public Properties
	
	public void setId(Long id) {
        this.id = id;
    }

	public Long getId() {
        return this.id;
    }
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	public long getOrganizationId()	{
		return this.organizationId;
	}
	
	public void setOrganizationId(long organizationId){
		this.organizationId = organizationId;
	}
	
	public long getStaffId()	{
		return this.staffId;
	}
	
	public void setStaffId(long staffId){
		this.staffId = staffId;
	}
	
	public Integer getVersion() {
        return version;
    }
	
	/// <summary>
	/// Gets or sets application-specific information for the membership user. 
	/// </summary>
	/// <value>Application-specific information for the membership user.</value>
	public String getComment(){
		return this.comment;
	}
	
	public void setComment(String comment){
		this.comment = comment;
	}

	/// <summary>
	/// Gets the date and time when the user was added to the membership data store.
	/// </summary>
	/// <value>The date and time when the user was added to the membership data store.</value>
	public Date getCreationDate(){
		return this.creationDate;
	}

	/// <summary>
	/// Gets or sets the e-mail address for the membership user.
	/// </summary>
	/// <value>The e-mail address for the membership user.</value>
	public String getEmail(){
		return this.email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}

	/// <summary>
	/// Gets or sets whether the membership user can be authenticated.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if user can be authenticated; otherwise, <c>false</c>.
	/// </value>
	public boolean getIsApproved(){
		return this.isApproved;
	}
	
	public void setIsApproved(boolean isApproved){
		this.isApproved = isApproved;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the membership user is locked out and unable to be validated.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the membership user is locked out and unable to be validated; otherwise, <c>false</c>.
	/// </value>
	public boolean getIsLockedOut()	{
		return this.isLockedOut;
	}
	
	public void setIsLockedOut(boolean isLockedOut)	{
		this.isLockedOut = isLockedOut;
	}

	/// <summary>
	/// Gets whether the user is currently online.
	/// </summary>
	/// <value><c>true</c> if the user is online; otherwise, <c>false</c>.</value>
	public boolean getIsOnline(){
		return this.isOnline;
	}

	/// <summary>
	/// Gets or sets the date and time when the membership user was last authenticated or accessed the application.
	/// </summary>
	/// <value>The date and time when the membership user was last authenticated or accessed the application.</value>
	public Date getLastActivityDate(){
		return this.lastActivityDate;
	}
	
	public void setLastActivityDate(Date lastActivityDate){
		this.lastActivityDate = lastActivityDate;
	}

	/// <summary>
	/// Gets the most recent date and time that the membership user was locked out.
	/// </summary>
	/// <value>The most recent date and time that the membership user was locked out.</value>
	public Date getLastLockoutDate(){
		return this.lastLockoutDate;
	}

	/// <summary>
	/// Gets or sets the date and time when the user was last authenticated.
	/// </summary>
	/// <value>The date and time when the user was last authenticated.</value>
	public Date getLastLoginDate(){
		return this.lastLoginDate;
	}
	
	public void setLastLoginDate(Date lastLoginDate){
		this.lastLoginDate = lastLoginDate;
	}

	/// <summary>
	/// Gets the date and time when the membership user's password was last updated.
	/// </summary>
	/// <value>The date and time when the membership user's password was last updated.</value>
	public Date getLastPasswordChangedDate(){
		return this.lastPasswordChangedDate;
	}

	/// <summary>
	/// Gets the password question for the membership user.
	/// </summary>
	/// <value>The password question for the membership user.</value>
	public String getPasswordQuestion(){
		return this.passwordQuestion;
	}

	/// <summary>
	/// Gets the name of the membership provider that stores and retrieves user information for the membership user.
	/// </summary>
	/// <value>The name of the membership provider that stores and retrieves user information for the membership user.</value>
	public String getProviderName()	{
		return this.providerName;
	}

	/// <summary>
	/// Gets the user identifier from the membership data source for the user.
	/// </summary>
	/// <value>The user identifier from the membership data source for the user.</value>
	public Object getProviderUserKey(){
		return this.providerUserKey;
	}

	/// <summary>
	/// Gets the logon name of the membership user.
	/// </summary>
	/// <value>The logon name of the membership user.</value>
	public String getUserName()	{
		return this.userName;
	}

	/// <summary>
	/// Gets a value indicating whether the user has no restrictions on actions.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user is a super user; otherwise, <c>false</c>.
	/// </value>
	//[Obsolete("Not implemented in current version of MDS System, but may be implemented in versions that derive from this code, such as the DotNetNuke module.", true)]
	@Deprecated
	public boolean getIsSuperUser()	{
		return isSuperUser;
	}

	/// <summary>
	/// NOT IMPLEMENTED: Gets or sets the user's first name.
	/// </summary>
	/// <value>The user's first name.</value>
	//[Obsolete("Not implemented in current version of MDS System, but may be implemented in versions that derive from this code, such as the DotNetNuke module.", true)]
	@Deprecated
	public String getFirstName(){
		return firstName;
	}
	
	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	/// <summary>
	/// NOT IMPLEMENTED: Gets or sets the user's last name.
	/// </summary>
	/// <value>The user's last name.</value>
	//[Obsolete("Not implemented in current version of MDS System, but may be implemented in versions that derive from this code, such as the DotNetNuke module.", true)]
	@Deprecated
	public String getLastName()	{
		return this.lastName;
	}
	
	public void setLastName(String lastName)	{
		this.lastName = lastName;
	}

	/// <summary>
	/// NOT IMPLEMENTED: Gets or sets the user's display name.
	/// </summary>
	/// <value>The user's display name.</value>
	//[Obsolete("Not implemented in current version of MDS System, but may be implemented in versions that derive from this code, such as the DotNetNuke module.", true)]
	@Deprecated
	public String getDisplayName(){
		return this.displayName;
	}
	
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	
	 @Override
    public String getPassword() {
        return this.password;
    }

    public List<Long> getRoleIds() {
        List<Long> roleIds = new ArrayList<Long>();
        for (MDSRole role : getRoles()) {
            roleIds.add(role.getRoleId());
        }
        return roleIds;
    }

    public MDSRoleCollection getRoles() {
        return this.roles;
    }

    public Byte getType() {
        return this.type;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
	    
	@Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.enabled.getStatus();
    }

    /**
     * @return GrantedAuthority[] an array of roles.
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        if (this.grantedAuthorities == null) {
            this.grantedAuthorities = new HashSet<GrantedAuthority>();
            for (MDSRole role : this.roles) {
                /*if (StringUtils.isNotEmpty(role.getName())) {
                    grantedAuthorities.add(new GrantedAuthorityImpl(role.getName()));
                }*/
            	grantedAuthorities.add(new SimpleGrantedAuthority(role.getRoleType().toString()));
            }
        }
        return this.grantedAuthorities;
    }
    
    public Collection<String> getAuthorizationInfo() throws InvalidMDSRoleException {
        if (this.authorizationInfo == null) {
            this.authorizationInfo = Lists.newArrayList();
			List<MenuFunction> list = UserUtils.getMenuFunctionList(this, false);
			List<MenuFunctionPermission> userMenuFunctionPermissions = UserUtils.getUserMenuFunctionPermissions(this);
			for (MenuFunction menuFunction : list){
				if (StringUtils.isNotBlank(menuFunction.getPermission())){
					//List<MenuFunctionPermission> menuFunctionPermissions = UserUtils.getMenuFunctionPermissionList(menuFunctionIds);
					List<MenuFunctionPermission> menuFunctionPermissions = userMenuFunctionPermissions.stream()
							.filter(mp->mp.getMenuFunction().getId() == menuFunction.getId()).collect(Collectors.toList());
					for(MenuFunctionPermission menuFunctionPermission : menuFunctionPermissions){
						if (menuFunctionPermission.getPermission() != null){
							for(UserAction action : menuFunctionPermission.getPermission().getPermissions()) {
								if (action != UserAction.notspecified) {
									authorizationInfo.add(menuFunction.getPermission() + ":" + action.toString()); //menuFunctionPermission.getPermission().getPermission());
								}
							}
						}
					}
				}
			}
        }
        
        return this.authorizationInfo;
    }
    
    public boolean isPermitted(String p) {
    	try {
			return getAuthorizationInfo().contains(p);
		} catch (InvalidMDSRoleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return false;
	}
    
    public boolean hasRole(String roleName) {
    	for (MDSRole role : this.roles) {
    		if (role.getRoleName().equals(roleName))
    			return true;
        }
    	
    	return false;
    }
    
    public boolean isSystem() {
    	if (this.roles != null) {
    		for(MDSRole role : this.roles) {
    			if (role.getRoleType() == RoleType.sa) {
    				return true;
    			}
    		}
    	}
    		
        return false;
    }
    
    public boolean isRoleType(RoleType roleType) {
    	if (this.roles != null) {
            for (MDSRole role : roles) {
                //if (role.getName().equals(Constants.SYSTEM_ROLE))
            	if (role.getRoleType() == roleType)
                	return true;
            }
        }
    	
    	return false;
    }
    
    public boolean isSysUser(){
    	if (this.roles != null) {
            for (MDSRole r : roles) {
            	if (r.getRoleType() == RoleType.sa || r.getRoleType() == RoleType.ad|| r.getRoleType() == RoleType.ur)
                	return true;
            }
        }
    	
    	return false;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(MDSRoleCollection roles) {   	
        this.roles  = roles;
    }

    public void setType(Byte type) {
        this.type = type;
    }

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Copies the current account information to the specified <paramref name="userAccount" />. The <paramref name="userAccount" />
	/// must be able to be cast to an instance of <see cref="UserAccount" />. If not, an <see cref="ArgumentNullException" />
	/// is thrown.
	/// </summary>
	/// <param name="userAccount">The user account to populate with information from the current instance.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="userAccount" /> is null.</exception>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="userAccount" /> cannot be cast to an instance of 
	/// <see cref="UserAccount" />.</exception>
	public UserAccount copyTo(UserAccount userAccount)	{
		if (userAccount == null)
			throw new ArgumentNullException("userAccount");

		try{
			
			userAccount = copyToInstance(userAccount);			
		}catch (ArgumentNullException ex){
			throw new ArgumentOutOfRangeException("userAccount", "The parameter 'userAccount' cannot be cast to an instance of UserAccount.");
		}
		
		return userAccount;
	}

	//#endregion

	//#region Private Functions

	/// <summary>
	/// Copies the current account information to the specified <paramref name="userAccount" />.
	/// </summary>
	/// <param name="userAccount">The user account to populate with information from the current instance.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="userAccount" /> is null.</exception>
	private UserAccount copyToInstance(UserAccount userAccount){
		if (userAccount == null)
			throw new ArgumentNullException("userAccount");

		userAccount.comment = this.comment;
		userAccount.creationDate = this.creationDate;
		userAccount.email = this.email;
		userAccount.isApproved = this.isApproved;
		userAccount.isLockedOut = this.isLockedOut;
		userAccount.isOnline = this.isOnline;
		userAccount.lastActivityDate = this.lastActivityDate;
		userAccount.lastLockoutDate = this.lastLockoutDate;
		userAccount.lastLoginDate = this.lastLoginDate;
		userAccount.lastPasswordChangedDate = this.lastPasswordChangedDate;
		userAccount.passwordQuestion = this.passwordQuestion;
		userAccount.providerName = this.providerName;
		userAccount.providerUserKey = this.providerUserKey;
		userAccount.userName = this.userName;
		
		return userAccount;
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
	@Override
	public int compareTo(UserAccount obj){
		if (obj == null)
			return 1;
		else{
			UserAccount other = Reflections.as(UserAccount.class, obj);
			if (other != null)
				return this.userName.compareTo(other.userName);
			else
				return 1;
		}
	}

	//#endregion
}
