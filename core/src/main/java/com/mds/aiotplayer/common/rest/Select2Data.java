package com.mds.aiotplayer.common.rest;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A simple JavaBean to represent select2 data format. This is most commonly used
 * when constructing user interface elements which have a label to be displayed
 * to the user, and a corresponding value to be returned to the server. One
 * example is the <code>&lt;html:options&gt;</code> tag.
 * 
 * <p>Note: this class has a natural ordering that is inconsistent with equals.
 *
 * @see org.apache.struts.util.Select2DataBean
 */
public class Select2Data implements Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = -5226631932064156482L;
	private List<Map<String,Object>> results = new LinkedList<Map<String,Object>>();
	private Map<String,Object> pagination = new LinkedHashMap<String, Object>();
	
    // ----------------------------------------------------------- Constructors


    /**
     * Default constructor.
     */
    public Select2Data() {
        super();
    }

    /**
     * Construct an instance with the supplied property values.
     *
     * @param label The label to be displayed to the user.
     * @param value The value to be returned to the server.
     */
    public Select2Data(final List<Map<String,Object>> results, final Map<String,Object> pagination) {
        this.results = results;
        this.pagination = pagination;
    }

    // ------------------------------------------------------------- Properties


    /**
	 * @return the results
	 */
	public List<Map<String,Object>> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(List<Map<String,Object>> results) {
		this.results = results;
	}

	/**
	 * @return the pagination
	 */
	public Map<String,Object> getPagination() {
		return pagination;
	}

	/**
	 * @param pagination the pagination to set
	 */
	public void setPagination(Map<String,Object> pagination) {
		this.pagination = pagination;
	}

    // --------------------------------------------------------- Public Methods
    /**
     * Return a string representation of this object.
     * @return object as a string
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("Select2Data[");
        sb.append(this.results);
        sb.append(", ");
        sb.append(this.pagination);
        sb.append("]");
        return (sb.toString());
    }

    /**
     * Select2DataBeans are equal if their values are both null or equal.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj object to compare to
     * @return true/false based on whether values match or not
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Select2Data)) {
            return false;
        }

        Select2Data bean = (Select2Data) obj;
        int nil = (this.getResults() == null) ? 1 : 0;
        nil += (bean.getResults() == null) ? 1 : 0;

        if (nil == 2) {
            return true;
        } else if (nil == 1) {
            return false;
        } else {
            return this.getPagination().equals(bean.getPagination());
        }

    }

    /**
     * The hash code is based on the object's value.
     *
     * @see java.lang.Object#hashCode()
     * @return hashCode
     */
    public int hashCode() {
        return (this.getPagination() == null) ? 17 : this.getPagination().hashCode();
    }
}