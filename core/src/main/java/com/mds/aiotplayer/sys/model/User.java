/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.bridge.builtin.IntegerBridge;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.common.model.Address;
import com.mds.aiotplayer.common.model.TenantSupport;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.common.model.DataEntity;
import com.mds.aiotplayer.hrm.model.Staff;
import com.mds.aiotplayer.util.Collections3;
import com.mds.aiotplayer.util.StringUtils;

/**
 * User Entity
 * @author John Lee
 * @version 2013-12-05
 */
@Entity
@Table(name = "sys_user", uniqueConstraints = @UniqueConstraint(columnNames={"organization_id", "username"}))
@Indexed
@XmlRootElement
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = "string")})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class User extends DataEntity implements TenantSupport, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -292031685135399800L;
	private Staff staff;	// user's staff
	private String username;// User name
	private String password;// password
    private String confirmPassword;
    private String passwordHint;
    private String firstName;                   // not required
    private String lastName;                    // not required
    private String nickName;                    // not required
	private String email;	// email
	private String phoneNumber;	// telephone
	private String mobile;	// mobile
	private Byte userType;// user type
	private String loginIp;	// last login IP
	private Date loginDate;	// last login date
	//private byte[] photo;	// Head portrait
    private String website;
    private Integer version;
    private int status;
    
    private Tenant tenant;
    
    private Organization organization;	// organization
    private Set<UserPhoto> userPhotos;
    private List<UserAddress> userAddresses;// = new Address();
    private List<UserContact> userContacts;// = new Address();
    private Set<Role> roles = new HashSet<Role>();
        
    private List<Staff> staffs = Lists.newArrayList();   // staffs
    private List<GalleryMapping> galleryMappings = Lists.newArrayList();   // gallery mappings
    private List<Notification> notifications = Lists.newArrayList();   // notifications
    private List<MyMessage> userMessages = Lists.newArrayList();	// messages
    private List<MyCalendar> userCalendars = Lists.newArrayList();	// calendars
    private List<MyMessageRecipient> messageRecipients = Lists.newArrayList();	// message recipients
    private List<MyMessage> sentMessages = Lists.newArrayList();	// sent messages by user 

    @ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="tenant_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	
    /**
	 * @return the status
	 */
    @Column(name = "status")
    //@Enumerated(EnumType.ORDINAL)
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the notifications
	 */
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="user")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
	public List<Notification> getNotifications() {
		return notifications;
	}

	/**
	 * @param notifications the notifications to set
	 */
	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	@Transient
	@JsonIgnore
    public List<Staff> getStaffs() {
		return staffs;
	}

	public void setStaffs(List<Staff> staffs) {
		this.staffs = staffs;
	}

	/**
	 * @return the galleryMappings
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="user")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<GalleryMapping> getGalleryMappings() {
		return galleryMappings;
	}

	/**
	 * @param galleryMappings the galleryMappings to set
	 */
	public void setGalleryMappings(List<GalleryMapping> galleryMappings) {
		this.galleryMappings = galleryMappings;
	}

	/**
     * Default constructor - creates a new instance with no values set.
     */
	public User() {
		super();
		this.userType = 1;
		this.status = 1;
	}
	
	public User(User user) {
		this.id = user.getId();
			    
	    this.username = user.getUsername();
		this.password = user.getPassword();
		this.passwordHint = user.getPasswordHint();
		this.nickName = user.getNickName();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.phoneNumber = user.getPhoneNumber();
		this.email = user.getEmail();
		this.mobile = user.getMobile();
		this.userType = user.getUserType();
		this.loginIp = user.getLoginIp();
		this.loginDate = user.getLoginDate();
		this.website = user.getWebsite();
		this.status = user.getStatus();
	}

	/**
     * Create a new instance and set the username.
     *
     * @param username login name for user.
     */
    public User(final String username) {
        this.username = username;
        this.userType = 1;
		this.status = 1;
    }
    
    /*@Basic(fetch = FetchType.LAZY )
    @Column(name="photo", length = 1048576)
    //@Type(type="org.hibernate.type.WrappedMaterializedBlobType")
	@JsonIgnore
	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}*/
    
    @Transient
    @JsonIgnore
	public byte[] getPhoto() {
    	if (this.userPhotos != null && !this.userPhotos.isEmpty())
    		return this.userPhotos.iterator().next().getPhoto();
    	
    	return null;
	}
    
    @Transient
    @JsonIgnore
	public void addPhoto(byte[] photo) {
    	if (photo != null) {
    		if(userPhotos == null) {
        		userPhotos = new HashSet<UserPhoto>();
            }
    		userPhotos.add(new UserPhoto(this, photo));
    	}
	}
	
	/**
	 * @return the userPhotos
	 */
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="user")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public Set<UserPhoto> getUserPhotos() {
    	if(userPhotos == null) {
    		userPhotos = new HashSet<UserPhoto>();
        }
    	
		return userPhotos;
	}

	/**
	 * @param userPhotos the userPhotos to set
	 */
	public void setUserPhotos(Set<UserPhoto> userPhotos) {
		this.userPhotos = userPhotos;
	}

	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="staff_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}
		

	@Column(nullable = false, length = 100)
    @Field(analyze=Analyze.NO)
    @SortableField
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", nullable = true, length = 255)
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "nick_name", nullable = true, length = 50)
    @Field
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Column(name = "email", nullable = true, length = 100, unique=true)
    @Field
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "phone_number", length = 50)
    @Field(analyze= Analyze.NO)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Column(name = "mobile", length = 50, unique=true)
    @Field(analyze= Analyze.NO)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name="user_type")
    @Type(type = "org.hibernate.type.ByteType")
    @FieldBridge(impl = IntegerBridge.class)
    @Field
	public Byte getUserType() {
		return userType;
	}

	public void setUserType(Byte userType) {
		this.userType = userType;
	}

	@Column(name="login_ip")
	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	@Column(name="login_date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "sys_user_role", 
			joinColumns = { @JoinColumn(name = "user_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "role_id") })
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public Set<Role> getRoles() {
		return roles;
	}
	
    /**
     * Adds a role for the user
     *
     * @param role the fully instantiated role
     */
    public void addRole(Role role) {
        getRoles().add(role);
    }
	
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@JsonIgnore
	@Transient
	public List<Long> getRoleIdList() {
		List<Long> roleIdList = Lists.newArrayList();
		if (this.roles != null) {
			for (Role role : roles) {
				roleIdList.add(role.getId());
			}
		}
		
		return roleIdList;
	}

	/*@Transient
	public void setRoleIdList(List<Long> roleIdList) {
		roles = new HashSet<Role>();
		for (Long roleId : roleIdList) {
			Role role = new Role();
			role.setId(roleId);
			roles.add(role);
		}
	}*/
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="organization_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	@Transient
	public String getOrganizationCode() {
		if (organization != null)
			return organization.getCode();
		
		return StringUtils.EMPTY;
	}
	
	@Transient
    @XmlTransient
    @JsonIgnore
    public String getConfirmPassword() {
        return confirmPassword;
    }

    @Column(name = "password_hint")
    @XmlTransient
    public String getPasswordHint() {
        return passwordHint;
    }

    @Column(name = "first_name", nullable = true, length = 50)
    @Field
    public String getFirstName() {
        return firstName;
    }

    @Column(name = "last_name", nullable = true, length = 50)
    @Field
    public String getLastName() {
        return lastName;
    }
    
    @Field
    public String getWebsite() {
        return website;
    }

    /**
     * Returns the full name.
     *
     * @return firstName + ' ' + lastName
     */
    @Transient
    public String getFullName() {
        return firstName + ' ' + lastName;
    }

    /*@Embedded
    @IndexedEmbedded
    public Address getAddress() {
        return address;
    }*/
	
	/**
     * @return GrantedAuthority[] an array of roles.
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    /*@Transient
    @JsonIgnore // needed for UserApiITest in appfuse-ws archetype
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.addAll(roles);
        return authorities;
    }*/

    @Version
    public Integer getVersion() {
        return version;
    }

    @Transient
    public boolean isEnabled() {
    	return ((status & UserStatus.enabled.getStatus()) > 0);
    }

    @Transient
    public boolean isAccountExpired() {
        return ((status & UserStatus.accountExpired.getStatus()) > 0);
    }

    /**
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     * @return true if account is still active
     */
    @Transient
    public boolean isAccountNonExpired() {
        return !isAccountExpired();
    }

    @Transient
    public boolean isAccountLocked() {
    	return ((status & UserStatus.accountLocked.getStatus()) > 0);
    }

    /**
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     * @return false if account is locked
     */
    @Transient
    public boolean isAccountNonLocked() {
        return !isAccountLocked();
    }

    @Transient
    public boolean isCredentialsExpired() {
    	return ((status & UserStatus.credentialsExpired.getStatus()) > 0);
    }

    /**
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     * @return true if credentials haven't expired
     */
    @Transient
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired();
    }
	
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }

    /*public void setAddress(Address address) {
        this.address = address;
    }*/

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Transient
    public void setEnabled(boolean enabled) {
    	this.status = (enabled ? (this.status | UserStatus.enabled.getStatus()) : (this.status ^ UserStatus.enabled.getStatus()));
    }

    @Transient
    public void setAccountExpired(boolean accountExpired) {
    	this.status = (accountExpired ? (this.status| UserStatus.accountExpired.getStatus()) : (this.status ^ UserStatus.accountExpired.getStatus()));
    }

    @Transient
    public void setAccountLocked(boolean accountLocked) {
    	this.status = (accountLocked ? (this.status| UserStatus.accountLocked.getStatus()) : (this.status ^ UserStatus.accountLocked.getStatus()));
    }

    @Transient
    public void setCredentialsExpired(boolean credentialsExpired) {
    	this.status = (credentialsExpired ? (this.status| UserStatus.credentialsExpired.getStatus()) : (this.status ^ UserStatus.credentialsExpired.getStatus()));
    }
    
    /**
	 * @return the userMessages
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="user")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public List<MyMessage> getUserMessages() {
		return userMessages;
	}

	/**
	 * @param userMessages the userMessages to set
	 */
	public void setUserMessages(List<MyMessage> userMessages) {
		this.userMessages = userMessages;
	}
	
	/**
	 * @return the sentMessages
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="sender")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public List<MyMessage> getSentMessages() {
		return sentMessages;
	}

	/**
	 * @param sentMessages the sentMessages to set
	 */
	public void setSentMessages(List<MyMessage> sentMessages) {
		this.sentMessages = sentMessages;
	}

	/**
	 * @return the userCalendars
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="user")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public List<MyCalendar> getUserCalendars() {
		return userCalendars;
	}

	/**
	 * @param userCalendars the userCalendars to set
	 */
	public void setUserCalendars(List<MyCalendar> userCalendars) {
		this.userCalendars = userCalendars;
	}

	/**
	 * @return the messageRecipients
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="user")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public List<MyMessageRecipient> getMessageRecipients() {
		return messageRecipients;
	}

	/**
	 * @param messageRecipients the messageRecipients to set
	 */
	public void setMessageRecipients(List<MyMessageRecipient> messageRecipients) {
		this.messageRecipients = messageRecipients;
	}
    
	/**
	 * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
	 */
	@Transient
	@XmlTransient
    @JsonIgnore
	public String getRoleNames() {
		return Collections3.extractToString(roles, "name", ",");
	}
	
	@Transient
	@XmlTransient
    @JsonIgnore
	public boolean isSystem(){
		if (userType == 0)
			return true;
		
		if (this.roles != null && !this.roles.isEmpty()) {
			for(Role role : this.roles) {
				if (role.getType() == RoleType.sa)
					return true;
			}
		}
		
		return false;
	}
		
	/**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        final User user = (User) o;

        return !(username != null ? !username.equals(user.getUsername()) : user.getUsername() != null);

    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (username != null ? username.hashCode() : 0);
    }
    
	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("username", this.username)
                .append("nickName", this.nickName)
                .append("firstName", this.firstName)
                .append("lastName", this.lastName)
                .append("phoneNumber", this.phoneNumber);

        /*if (roles != null) {
            sb.append("Granted Authorities: ");

            int i = 0;
            for (Role role : roles) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(role.toString());
                i++;
            }
        } else {
            sb.append("No Granted Authorities");
        }*/
        return sb.toString();
	}
	
	@Override
	public void copyFrom(Object source) {
		final User user = (User) source;
		
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.passwordHint = user.getPasswordHint();
		this.nickName = user.getNickName();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.phoneNumber = user.getPhoneNumber();
		this.email = user.getEmail();
		this.mobile = user.getMobile();
		this.userType = user.getUserType();
		this.loginIp = user.getLoginIp();
		this.loginDate = user.getLoginDate();
		this.website = user.getWebsite();
		this.status = user.getStatus();
	}

	/**
	 * @return the user addresses
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="user")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public List<UserAddress> getUserAddresses() {
		return userAddresses;
	}

	/**
	 * @param useAddresses the user addresses to set
	 */
	public void setUserAddresses(List<UserAddress> userAddresses) {
		this.userAddresses = userAddresses;
	}

	/**
	 * @return the userContacts
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="user")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public List<UserContact> getUserContacts() {
		return userContacts;
	}

	/**
	 * @param userContacts the userContacts to set
	 */
	public void setUserContacts(List<UserContact> userContacts) {
		this.userContacts = userContacts;
	}
	
    @Transient
    @JsonIgnore
    public boolean isRoleType(RoleType roleType) {
    	if (this.roles != null) {
            for (Role role : roles) {
                //if (role.getName().equals(Constants.SYSTEM_ROLE))
            	if (role.getType() == roleType)
                	return true;
            }
        }
    	
    	return false;
    }
}