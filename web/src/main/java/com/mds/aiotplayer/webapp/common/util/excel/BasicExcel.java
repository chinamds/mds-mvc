/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/chinamds/mdsplus">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.webapp.common.util.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Embedded;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.utils.excel.annotation.ExcelField;
import com.mds.aiotplayer.i18n.util.I18nUtils;


/**
 * basic operation for Excel file(import/export) 
 * @Modify by John Lee
 * @version 17/07/2017
 */
public class BasicExcel {
	protected String getFieldTypePackage(){
		return "com.mds.aiotplayer.util.excel";
	}
	
	protected int getSort(Object[] os){
		int emLevel = 0;
		int sort = 0;
		while(os.length > emLevel){
			sort += ((ExcelField)os[emLevel]).sort();

			emLevel += 2;
		}
		
		return sort;
	}
	
	protected String getHeader(final HttpServletRequest request, Object[] os, int type){
		int emLevel = 0;
		String title = null;
		while(os.length > emLevel){
			String t = ((ExcelField)os[emLevel]).title();
			// If it is exported, remove the comment
			if (type==1){
				String[] ss = StringUtils.split(t, "**", 2);
				if (ss.length==2){
					t = ss[0];
				}
			}
			if (StringUtils.isBlank(title))
				title = ((ExcelField)os[emLevel]).title();
			else
			{
				title += ".";
				title += ((ExcelField)os[emLevel]).title();
			}
			emLevel += 2;
		}
		String header = I18nUtils.getString(title, request.getLocale());
		if (header == null)
			header = title;
		
		return header;
	}
	
	@SuppressWarnings("unchecked")
	protected List<Object[]> getExcelField(List annos, int sort,  Class<?> cls, int type, int... groups){
		List<Object[]> annotationList = Lists.newArrayList();
		// Get annotation field
		Field[] fs = cls.getDeclaredFields();
		for (Field f : fs){
			ExcelField ef = f.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type()==0 || ef.type()==type)){
				List annoList = Lists.newArrayList();
				if (annos != null)
					annoList.addAll(annos);
				annoList.add(ef);
				Embedded em = f.getAnnotation(Embedded.class);
				if (em != null)
				{
					annoList.add(f);
					getExcelField(annoList, ef.sort(), f.getClass(), type, groups);
				}else{
					if (groups!=null && groups.length>0){
						boolean inGroup = false;
						for (int g : groups){
							if (inGroup){
								break;
							}
							for (int efg : ef.groups()){
								if (g == efg){
									inGroup = true;
									annoList.add(f);
									//annotationList.add(new Object[]{ef, f});
									annotationList.add(annoList.toArray());
									break;
								}
							}
						}
					}else{
						annoList.add(f);
						annotationList.add(annoList.toArray());
						//annotationList.add(new Object[]{ef, f});
					}
				}
			}
		}
		// Get annotation method
		Method[] ms = cls.getDeclaredMethods();
		for (Method m : ms){
			ExcelField ef = m.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type()==0 || ef.type()==type)){
				if (ef.complex()==1 && m.getReturnType() != cls)
					continue;
				
				List annoList = Lists.newArrayList();
				if (annos != null)
					annoList.addAll(annos);
				annoList.add(ef);
				Embedded em = m.getAnnotation(Embedded.class);
				if (em != null)
				{
					annoList.add(m);
					getExcelField(annoList, ef.sort(), ef.fieldType(), type, groups);
					continue;
				}
				if (groups!=null && groups.length>0){
					boolean inGroup = false;
					for (int g : groups){
						if (inGroup){
							break;
						}
						for (int efg : ef.groups()){
							if (g == efg){
								inGroup = true;
								//annotationList.add(new Object[]{ef, m});
								annoList.add(m);
								annotationList.add(annoList.toArray());
								break;
							}
						}
					}
				}else{
					//annotationList.add(new Object[]{ef, m});					
					annoList.add(m);
					annotationList.add(annoList.toArray());
				}
			}
		}
		
		return annotationList;
	}	
}
