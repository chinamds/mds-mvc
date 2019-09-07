/**
 * Copyright (c) 2016-2018 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.util.excel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.google.common.collect.Lists;
import com.mds.i18n.util.I18nUtils;

/**
 * <p>User: John Lee
 * <p>Date: 18 November 2017 18:21:15
 * <p>Version: 1.0
 */
public class ExcelImportResult<E> {
	private Map<Integer, E> dataMap = new LinkedHashMap<Integer, E>();
	private Map<Integer, List<String>> resultMap = new LinkedHashMap<Integer, List<String>>();
	
	public ExcelImportResult() {
    }

    public static <E> ExcelImportResult<E> newInstance() {
        return new ExcelImportResult<E>();
    }

    public void add(int row, E data) {
    	dataMap.put(row, data);
    }

	public void addResult(int row, String data) {
		if (resultMap.containsKey(row)){
			resultMap.get(row).add(data);
		}else{
			List<String> result = Lists.newArrayList();
			result.add(data);
			resultMap.put(row, result);
		}
    }
	
	public void addResult(int row, List<String> datas) {
		if (resultMap.containsKey(row)){
			resultMap.get(row).addAll(datas);
		}else{
			List<String> result = Lists.newArrayList();
			result.addAll(datas);
			resultMap.put(row, result);
		}
    }
	
	public void addResult(int row, BindingResult bindingResult) {       
        ObjectError objectError = bindingResult.getAllErrors().get(0);
        String message = objectError.getDefaultMessage();
        addResult(row, message);
        
        if (objectError instanceof FieldError) {
            FieldError fieldError = (FieldError) objectError;
            String fieldName = fieldError.getField();
            addResult(row, fieldName + " " + message);
            String[] codes = fieldError.getCodes();
            if (codes != null && codes.length > 2) {
                if ("typeMismatch.java.util.Date".equals(codes[2])) {
                	addResult(row, fieldName + " is Invalid date format");
                }
            }
        }
    }
	
	public void addResult(int row, BindingResult bindingResult, Locale locale) {       
        ObjectError objectError = bindingResult.getAllErrors().get(0);
        String message = objectError.getDefaultMessage();
        addResult(row, message);
        
        if (objectError instanceof FieldError) {
            FieldError fieldError = (FieldError) objectError;
            String fieldName = fieldError.getField();
            addResult(row, I18nUtils.getString(message, locale, fieldName));
            String[] codes = fieldError.getCodes();
            if (codes != null && codes.length > 2) {
                if ("typeMismatch.java.util.Date".equals(codes[2])) {
                	addResult(row, I18nUtils.getString("errors.dateformat", locale, fieldName));
                }
            }
        }
    }
	
	public boolean hasErrors(){
		return resultMap.size()>0;
	}
	
	public boolean hasErrors(int row){
		return resultMap.containsKey(row);
	}
	    
    public void clear() {
    	resultMap.clear();
    }
    
    public  Set<Integer> dataRow() {
    	return dataMap.keySet();
    }
    
    public  List<E> toList() {
    	return new ArrayList<E>(dataMap.values());
    }
    
    public  Map<Integer, E> data() {
    	return dataMap;
    }
    
    public  E data(int row) {
    	return dataMap.get(row);
    }

    public  Map<Integer, List<String>> result() {
    	return resultMap;
    }
    
    public  List<String> result(int row) {
    	return resultMap.get(row);
    }
    
    public  String resultToString(Locale locale) {
    	StringBuilder result = new StringBuilder();
    	for (int row : resultMap.keySet()){
    		StringBuilder errs = new StringBuilder();
    		for(String err : resultMap.get(row)){
    			errs.append(err).append(resultMap.get(row).size()>1?"<br/>":"");
    		}
    		result.append(I18nUtils.getString("message.import.row", locale) + ": " + row + ", ").append(errs).append(resultMap.keySet().size()>1?"<br/>":"");;
    	}
    	
    	return result.toString();
    }
    
    public  String resultToString(int successNum, Locale locale) {
    	if (!hasErrors()){
    		return I18nUtils.getString("message.import.result", locale, successNum, resultMap.keySet().size());
    	}else{
    		StringBuilder result = new StringBuilder();
        	result.append(I18nUtils.getString("message.import.result", locale, successNum, resultMap.keySet().size())).append("<br/>");
        	for (int row : resultMap.keySet()){
        		StringBuilder errs = new StringBuilder();
        		for(String err : resultMap.get(row)){
        			errs.append(err).append(resultMap.get(row).size()>1?"<br/>":"");
        		}
        		result.append(I18nUtils.getString("message.import.row", locale) + ": " + row + ", ").append(errs).append(resultMap.keySet().size()>1?"<br/>":"");;
        	}
        	
        	return result.toString();
    	}
    }
}
