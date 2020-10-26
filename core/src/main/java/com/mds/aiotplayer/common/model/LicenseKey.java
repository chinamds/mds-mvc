/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */

package com.mds.aiotplayer.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LicenseKey
 * @author mmdsplus
 * @version 2013-8-23
 */
//@SuppressWarnings("rawtypes")
public class LicenseKey implements Comparable<LicenseKey>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3130176710304818948L;
	private String licenseNo;
	private String licensee ;
	private String licenseKey ;
	private Date expireDate ;
	private Date licenseDate ;
	private int maxNumberOnlineUsers ;
	private int maxNumberActiveStaffs ;
    
 // --------------------------------------------------------- Public Methods

	@JsonProperty(value = "LicenseNo")
    public String getLicenseNo() {
		return this.licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	@JsonProperty(value = "Licensee")
	public String getLicensee() {
		return this.licensee;
	}

	public void setLicensee(String licensee) {
		this.licensee = licensee;
	}

	@JsonProperty(value = "LicenseKey")
	public String getLicenseKey() {
		return this.licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	@JsonProperty(value = "ExpireDate")
	public Date getExpireDate() {
		return this.expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	@JsonProperty(value = "LicenseDate")
	public Date getLicenseDate() {
		return licenseDate;
	}

	public void setLicenseDate(Date licenseDate) {
		this.licenseDate = licenseDate;
	}

	@JsonProperty(value = "MaxNumberOnlineUsers")
	public int getMaxNumberOnlineUsers() {
		return maxNumberOnlineUsers;
	}

	public void setMaxNumberOnlineUsers(int maxNumberOnlineUsers) {
		this.maxNumberOnlineUsers = maxNumberOnlineUsers;
	}

	@JsonProperty(value = "MaxNumberActiveStaffs")
	public int getMaxNumberActiveStaffs() {
		return maxNumberActiveStaffs;
	}

	public void setMaxNumberActiveStaffs(int maxNumberActiveStaffs) {
		this.maxNumberActiveStaffs = maxNumberActiveStaffs;
	}

	/**
     * Compare LicenseKeyBeans based on the label, because that's the human
     * viewable part of the object.
     *
     * @see Comparable
     * @param o LicenseKey object to compare to
     * @return 0 if labels match for compared objects
     */
    public int compareTo(LicenseKey o) {
        // Implicitly tests for the correct type, throwing
        // ClassCastException as required by interface
        String otherLabel = ((LicenseKey) o).getLicenseKey();

        return this.getLicenseKey().compareTo(otherLabel);
    }

    /**
     * Return a string representation of this object.
     * @return object as a string
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("LicenseKey[");
        sb.append(this.licenseNo);
        sb.append(", ");
        sb.append(this.licensee);
        sb.append(", ");
        sb.append(this.licenseKey);
        sb.append("]");
        return (sb.toString());
    }

    /**
     * LicenseKeyBeans are equal if their values are both null or equal.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj object to compare to
     * @return true/false based on whether values match or not
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof LicenseKey)) {
            return false;
        }

        LicenseKey bean = (LicenseKey) obj;
        int nil = (this.getLicenseKey() == null) ? 1 : 0;
        nil += (bean.getLicenseKey() == null) ? 1 : 0;

        if (nil == 2) {
            return true;
        } else if (nil == 1) {
            return false;
        } else {
            return this.getLicenseKey().equals(bean.getLicenseKey());
        }

    }

    /**
     * The hash code is based on the object's value.
     *
     * @see java.lang.Object#hashCode()
     * @return hashCode
     */
    public int hashCode() {
        return (this.getLicenseKey() == null) ? 17 : this.getLicenseKey().hashCode();
    }
}
