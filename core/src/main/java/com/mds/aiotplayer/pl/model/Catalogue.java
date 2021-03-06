/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
// Created using LayerGen 4.0

package com.mds.aiotplayer.pl.model;

import com.mds.aiotplayer.common.model.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.JsonDateSerializer;
import com.mds.aiotplayer.sys.model.Organization;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import  org.apache.commons.lang.builder.HashCodeBuilder;
import  org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="pl_catalogue", uniqueConstraints = @UniqueConstraint(columnNames={"organization_id", "catalogueName"}))
@Indexed
@XmlRootElement
public class Catalogue extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -91728909106334522L;
	private String catalogueName;
    private String catalogueDesc;
    private short nScreenType;
    private short nSkin;
    private String strBtnLng;
    private short nBtnAlign;
    private short nBtnStyle;
    private short nFontSize;
    private short nQuantity;
    private short nBgType;
    private int crFontColor;
    private int crBGColor;
    private boolean bFontUnderline;
    private boolean bFontItalic;
    private boolean bFontBold;
    private boolean bBGMusic;
    private boolean bInteractive;
    private String strFontName;
    private String layoutName;
    private String strSkinCode;
    private String strMusicFile;
    private String strImageFile;
    
    private List<Product> products = Lists.newArrayList(); //products
    private Organization organization;	// organization
            
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
 
    @JsonProperty(value = "catalogueName")
    @Column(name="catalogueName", nullable=false, length=50)
    @Field
    public String getCatalogueName(){
        return this.catalogueName;
    }
    
    public void setCatalogueName(String catalogueName){
        this.catalogueName = catalogueName;
    }
    
    @JsonProperty(value = "catalogueDesc")
    @Column(name="catalogueDesc", nullable=false, length=256)
    @Field
    public String getCatalogueDesc(){
        return this.catalogueDesc;
    }
    
    public void setCatalogueDesc(String catalogueDesc){
        this.catalogueDesc = catalogueDesc;
    }
    
    @JsonProperty(value = "nScreenType")
    @Column(name="nScreenType", nullable=false)
    @Field
    public short getScreenType(){
        return this.nScreenType;
    }
    
    public void setScreenType(short nScreenType){
        this.nScreenType = nScreenType;
    }
    
    @JsonProperty(value = "nSkin")
    @Column(name="nSkin", nullable=false)
    @Field
    public short getSkin(){
        return this.nSkin;
    }
    
    public void setSkin(short nSkin){
        this.nSkin = nSkin;
    }
    
    @JsonProperty(value = "strBtnLng")
    @Column(name="strBtnLng", nullable=false, length=3)
    @Field
    public String getBtnLng(){
        return this.strBtnLng;
    }
    
    public void setBtnLng(String strBtnLng){
        this.strBtnLng = strBtnLng;
    }
    
    @JsonProperty(value = "nBtnAlign")
    @Column(name="nBtnAlign", nullable=false)
    @Field
    public short getBtnAlign(){
        return this.nBtnAlign;
    }
    
    public void setBtnAlign(short nBtnAlign){
        this.nBtnAlign = nBtnAlign;
    }
    
    @JsonProperty(value = "nBtnStyle")
    @Column(name="nBtnStyle", nullable=false)
    @Field
    public short getBtnStyle(){
        return this.nBtnStyle;
    }
    
    public void setBtnStyle(short nBtnStyle){
        this.nBtnStyle = nBtnStyle;
    }
    
    @JsonProperty(value = "nFontSize")
    @Column(name="nFontSize", nullable=false)
    @Field
    public short getFontSize(){
        return this.nFontSize;
    }
    
    public void setFontSize(short nFontSize){
        this.nFontSize = nFontSize;
    }
    
    @JsonProperty(value = "nQuantity")
    @Column(name="nQuantity", nullable=false)
    @Field
    public short getQuantity(){
        return this.nQuantity;
    }
    
    public void setQuantity(short nQuantity){
        this.nQuantity = nQuantity;
    }
    
    @JsonProperty(value = "nBgType")
    @Column(name="nBgType", nullable=false)
    @Field
    public short getBgType(){
        return this.nBgType;
    }
    
    public void setBgType(short nBgType){
        this.nBgType = nBgType;
    }
    
    @JsonProperty(value = "crFontColor")
    @Column(name="crFontColor", nullable=false)
    @Field
    public int getFontColor(){
        return this.crFontColor;
    }
    
    public void setFontColor(int crFontColor){
        this.crFontColor = crFontColor;
    }
    
    @JsonProperty(value = "crBGColor")
    @Column(name="crBGColor", nullable=false)
    @Field
    public int getBGColor(){
        return this.crBGColor;
    }
    
    public void setBGColor(int crBGColor){
        this.crBGColor = crBGColor;
    }
    
    @JsonProperty(value = "bFontUnderline")
    @Column(name="bFontUnderline", nullable=false)
    @Field
    public boolean isFontUnderline(){
        return this.bFontUnderline;
    }
    
    public void setFontUnderline(boolean bFontUnderline){
        this.bFontUnderline = bFontUnderline;
    }
    
    @JsonProperty(value = "bFontItalic")
    @Column(name="bFontItalic", nullable=false)
    @Field
    public boolean isFontItalic(){
        return this.bFontItalic;
    }
    
    public void setFontItalic(boolean bFontItalic){
        this.bFontItalic = bFontItalic;
    }
    
    @JsonProperty(value = "bFontBold")
    @Column(name="bFontBold", nullable=false)
    @Field
    public boolean isFontBold(){
        return this.bFontBold;
    }
    
    public void setFontBold(boolean bFontBold){
        this.bFontBold = bFontBold;
    }
    
    @JsonProperty(value = "bBGMusic")
    @Column(name="bBGMusic", nullable=false)
    @Field
    public boolean isBGMusic(){
        return this.bBGMusic;
    }
    
    public void setBGMusic(boolean bBGMusic){
        this.bBGMusic = bBGMusic;
    }
    
    @JsonProperty(value = "bInteractive")
    @Column(name="bInteractive", nullable=false)
    @Field
    public boolean isInteractive(){
        return this.bInteractive;
    }
    
    public void setInteractive(boolean bInteractive){
        this.bInteractive = bInteractive;
    }
    
    @JsonProperty(value = "strFontName")
    @Column(name="strFontName", nullable=false, length=50)
    @Field
    public String getFontName(){
        return this.strFontName;
    }
    
    public void setFontName(String strFontName){
        this.strFontName = strFontName;
    }
    
    @JsonProperty(value = "layoutName")
    @Column(name="layout_name", nullable=false, length=50)
    @Field
    public String getLayoutName(){
        return this.layoutName;
    }
    
    public void setLayoutName(String layoutName){
        this.layoutName = layoutName;
    }
    
    @JsonProperty(value = "strSkinCode")
    @Column(name="strSkinCode", nullable=false, length=25)
    @Field
    public String getSkinCode(){
        return this.strSkinCode;
    }
    
    public void setSkinCode(String strSkinCode){
        this.strSkinCode = strSkinCode;
    }
    
    @JsonProperty(value = "strMusicFile")
    @Column(name="strMusicFile", nullable=false, length=1024)
    @Field
    public String getMusicFile(){
        return this.strMusicFile;
    }
    
    public void setMusicFile(String strMusicFile){
        this.strMusicFile = strMusicFile;
    }
    
    @JsonProperty(value = "strImageFile")
    @Column(name="strImageFile", nullable=false, length=1024)
    @Field
    public String getImageFile(){
        return this.strImageFile;
    }
    
    public void setImageFile(String strImageFile){
        this.strImageFile = strImageFile;
    }
    

    /**
	 * @return the products
	 */
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="catalogue")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<Product> getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}

	/**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Catalogue pojo = (Catalogue)o;
        return (new EqualsBuilder()
             .append(catalogueName, pojo.catalogueName)
             .append(catalogueDesc, pojo.catalogueDesc)
             .append(nScreenType, pojo.nScreenType)
             .append(nSkin, pojo.nSkin)
             .append(strBtnLng, pojo.strBtnLng)
             .append(nBtnAlign, pojo.nBtnAlign)
             .append(nBtnStyle, pojo.nBtnStyle)
             .append(nFontSize, pojo.nFontSize)
             .append(nQuantity, pojo.nQuantity)
             .append(nBgType, pojo.nBgType)
             .append(crFontColor, pojo.crFontColor)
             .append(crBGColor, pojo.crBGColor)
             .append(bFontUnderline, pojo.bFontUnderline)
             .append(bFontItalic, pojo.bFontItalic)
             .append(bFontBold, pojo.bFontBold)
             .append(bBGMusic, pojo.bBGMusic)
             .append(bInteractive, pojo.bInteractive)
             .append(strFontName, pojo.strFontName)
             .append(layoutName, pojo.layoutName)
             .append(strSkinCode, pojo.strSkinCode)
             .append(strMusicFile, pojo.strMusicFile)
             .append(strImageFile, pojo.strImageFile)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(catalogueName)
             .append(catalogueDesc)
             .append(nScreenType)
             .append(nSkin)
             .append(strBtnLng)
             .append(nBtnAlign)
             .append(nBtnStyle)
             .append(nFontSize)
             .append(nQuantity)
             .append(nBgType)
             .append(crFontColor)
             .append(crBGColor)
             .append(bFontUnderline)
             .append(bFontItalic)
             .append(bFontBold)
             .append(bBGMusic)
             .append(bInteractive)
             .append(strFontName)
             .append(layoutName)
             .append(strSkinCode)
             .append(strMusicFile)
             .append(strImageFile)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("id").append("='").append(getId()).append("', ");
        sb.append("catalogueName").append("='").append(getCatalogueName()).append("', ");
        sb.append("catalogueDesc").append("='").append(getCatalogueDesc()).append("', ");
        sb.append("nScreenType").append("='").append(getScreenType()).append("', ");
        sb.append("nSkin").append("='").append(getSkin()).append("', ");
        sb.append("strBtnLng").append("='").append(getBtnLng()).append("', ");
        sb.append("nBtnAlign").append("='").append(getBtnAlign()).append("', ");
        sb.append("nBtnStyle").append("='").append(getBtnStyle()).append("', ");
        sb.append("nFontSize").append("='").append(getFontSize()).append("', ");
        sb.append("nQuantity").append("='").append(getQuantity()).append("', ");
        sb.append("nBgType").append("='").append(getBgType()).append("', ");
        sb.append("crFontColor").append("='").append(getFontColor()).append("', ");
        sb.append("crBGColor").append("='").append(getBGColor()).append("', ");
        sb.append("bFontUnderline").append("='").append(isFontUnderline()).append("', ");
        sb.append("bFontItalic").append("='").append(isFontItalic()).append("', ");
        sb.append("bFontBold").append("='").append(isFontBold()).append("', ");
        sb.append("bBGMusic").append("='").append(isBGMusic()).append("', ");
        sb.append("bInteractive").append("='").append(isInteractive()).append("', ");
        sb.append("strFontName").append("='").append(getFontName()).append("', ");
        sb.append("layoutName").append("='").append(getLayoutName()).append("', ");
        sb.append("strSkinCode").append("='").append(getSkinCode()).append("', ");
        sb.append("strMusicFile").append("='").append(getMusicFile()).append("', ");
        sb.append("strImageFile").append("='").append(getImageFile()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}