/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.web.validate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>User: John Lee
 * <p>Date: 18 November 2017 18:21:15
 * <p>Version: 1.0
 */
public class AjaxResponse {
	private Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
	
	public AjaxResponse() {
    }

    public static AjaxResponse newInstance() {
        return new AjaxResponse();
    }

    public AjaxResponse(Boolean success) {
        this(200, null);
    }

    public AjaxResponse(int status, String message) {
    	resultMap.put("status", status);
    	resultMap.put("message", message);
    }


    public static AjaxResponse fail() {
        return fail(null);
    }

    public static AjaxResponse fail(String message) {
        return new AjaxResponse(0, message);
    }

    public static AjaxResponse success() {
        return success(null);
    }

    public static AjaxResponse success(String message) {
        return new AjaxResponse(200, message);
    }


    public Boolean getSuccess() {
        return ((int)resultMap.get("status")==200);
    }

    public void add(String key, Object data) {
    	resultMap.put(key, data);
    }
    
    public void clear() {
    	resultMap.clear();
    }

    public  Map<String,Object> result() {
    	return resultMap;
    }
}
