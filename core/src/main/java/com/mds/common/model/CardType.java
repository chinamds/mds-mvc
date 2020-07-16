package com.mds.common.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

/**
 * CardType Entity
 * <p>User: John Lee
 * <p>Date: 2017/8/18 11:03
 * <p>Version: 1.0
 */
@Entity
@Table(name = "common_cardtype")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate

public class CardType extends DataEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7631050152798030532L;

	//
	private String CardTypeCode;

	//
	private String CardTypeName;

	/**
	 * @return the cardTypeCode
	 */
	@Column(name="card_type_code")
	public String getCardTypeCode() {
		return CardTypeCode;
	}

	/**
	 * @param cardTypeCode the cardTypeCode to set
	 */
	public void setCardTypeCode(String cardTypeCode) {
		CardTypeCode = cardTypeCode;
	}

	/**
	 * @return the cardTypeName
	 */
	@Column(name="card_type_name")
	public String getCardTypeName() {
		return CardTypeName;
	}

	/**
	 * @param cardTypeName the cardTypeName to set
	 */
	public void setCardTypeName(String cardTypeName) {
		CardTypeName = cardTypeName;
	}
}