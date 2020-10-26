/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.exception;

import com.mds.aiotplayer.common.utils.MessageUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

/**
 * 基础异常
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:19
 * <p>Version: 1.0
 */
public class BaseException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1757305644851781951L;

	//所属模块
    private String module;

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误码对应的参数
     */
    private Object[] args;

    /**
     * 错误消息
     */
    private String defaultMessage;
    
    //
    // Summary:
    //     Gets a collection of key/value pairs that provide additional user-defined information
    //     about the exception.
    //
    // Returns:
    //     An object that implements the System.Collections.IDictionary interface and contains
    //     a collection of user-defined key/value pairs. The default is an empty collection.
    private Map<String, Object> data = null;
    
    public Map<String, Object> getData(){
    	return data;
    }
    
    public  void addData(String key, Object value){
    	if (this.data == null)
    		this.data = new HashMap<String, Object>();
    		
    	this.data.put(key, value);
    }

    public BaseException(String module, String code, Object[] args, String defaultMessage) {
    	super(defaultMessage);
    	
        this.module = module;
        this.code = code;
        this.args = args;
        this.defaultMessage = defaultMessage;
    }

    public BaseException(String module, String code, Object[] args) {
        this(module, code, args, code);
    }

    public BaseException(String module, String defaultMessage) {
        this(module, null, null, defaultMessage);
    }

    public BaseException(String code, Object[] args) {
        this(null, code, args, code);
    }

    public BaseException(String defaultMessage) {
        this(null, null, null, defaultMessage);
    }
    
    public BaseException(String module, String code, Object[] args, String defaultMessage, Throwable cause) {
    	super(defaultMessage, cause);
    	
        this.module = module;
        this.code = code;
        this.args = args;
        this.defaultMessage = defaultMessage;
    }

    public BaseException(String module, String code, Object[] args, Throwable cause) {
        this(module, code, args, code, cause);
    }

    public BaseException(String module, String defaultMessage, Throwable cause) {
        this(module, null, null, defaultMessage, cause);
    }

    public BaseException(String code, Object[] args, Throwable cause) {
        this(null, code, args, code, cause);
    }

    public BaseException(String defaultMessage, Throwable cause) {
        this(null, null, null, defaultMessage, cause);
    }

    @Override
    public String getMessage() {
        String message = null;
        if (!StringUtils.isEmpty(code)) {
        	Locale locale = LocaleContextHolder.getLocale();
            message = MessageUtils.message(code, locale, args);
        }
        if (message == null) {
            message = defaultMessage;
        }
        return message;
    }


    public String getModule() {
        return module;
    }

    public String getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public String toString() {
        return this.getClass() + "{" +
                "module='" + module + '\'' +
                ", message='" + getMessage() + '\'' +
                ", cause='" + getCause() != null ? getCause().getMessage() : "" + '\'' +
                '}';
    }
}
