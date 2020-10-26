/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.mds.aiotplayer.common.model.BaseObject;
import com.mds.aiotplayer.common.utils.excel.annotation.ExcelField;

/**
 * This class is used to represent an address with address,
 * city, province and postal-code information.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Embeddable
@Indexed
public class Address extends BaseObject implements Serializable {
    private static final long serialVersionUID = 3617859655330969141L;
    private String address;
    private String address2;
    private String address3;
    private String address4;
    private String city;
    private String province;
    private String country;
    private String postalCode;

    @ExcelField(title="address", align=2, sort=1)
    @Column(length = 1024)
    @Field
    public String getAddress() {
        return address;
    }
    
    @ExcelField(title="address2", align=2, sort=2)
    @Column(length = 1024)
    @Field
    public String getAddress2() {
        return address2;
    }
    
    @ExcelField(title="address3", align=2, sort=3)
    @Column(length = 1024)
    @Field
    public String getAddress3() {
        return address3;
    }
    
    @ExcelField(title="address4", align=2, sort=4)
    @Column(length = 1024)
    @Field
    public String getAddress4() {
        return address4;
    }

    @ExcelField(title="city", align=2, sort=5)
    @Column(length = 50)
    @Field
    public String getCity() {
        return city;
    }

    @ExcelField(title="province", align=2, sort=6)
    @Column(length = 100)
    @Field
    public String getProvince() {
        return province;
    }

    @ExcelField(title="country", align=2, sort=7)
    @Column(length = 100)
    @Field
    public String getCountry() {
        return country;
    }

    @ExcelField(title="postalcode", align=2, sort=8)
    @Column(name = "postal_code", length = 15)
    @Field(analyze= Analyze.NO)
    public String getPostalCode() {
        return postalCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    
    public void setAddress3(String address3) {
        this.address3 = address3;
    }
    
    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Overridden equals method for object comparison. Compares based on hashCode.
     *
     * @param o Object to compare
     * @return true/false based on hashCode
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }

        final Address address1 = (Address) o;

        return this.hashCode() == address1.hashCode();
    }

    /**
     * Overridden hashCode method - compares on address, city, province, country and postal code.
     *
     * @return hashCode
     */
    public int hashCode() {
        int result;
        result = (address != null ? address.hashCode() : 0);
        result = (address2 != null ? address2.hashCode() : 0);
        result = (address3 != null ? address3.hashCode() : 0);
        result = (address4 != null ? address4.hashCode() : 0);
        result = 29 * result + (city != null ? city.hashCode() : 0);
        result = 29 * result + (province != null ? province.hashCode() : 0);
        result = 29 * result + (country != null ? country.hashCode() : 0);
        result = 29 * result + (postalCode != null ? postalCode.hashCode() : 0);
        return result;
    }

    /**
     * Returns a multi-line String with key=value pairs.
     *
     * @return a String representation of this class.
     */
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("country", this.country)
                .append("address", this.address)
                .append("address2", this.address2)
                .append("address3", this.address3)
                .append("address4", this.address4)
                .append("province", this.province)
                .append("postalCode", this.postalCode)
                .append("city", this.city).toString();
    }

	@Override
	public void copyFrom(Object source) {
		// TODO Auto-generated method stub
		
	}
}
