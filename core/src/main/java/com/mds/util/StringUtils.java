/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Lists;
import com.mds.cm.metadata.MetaValue;
import com.mds.common.utils.security.Encodes;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * @author ThinkGem
 * @version 2013-05-22
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	private static final char SEPARATOR = '_';
    private static final String CHARSET_NAME = "UTF-8";
    private static Pattern intRegEx;
    
	/**
	 * format string as C#
	 * 
	 * @param str
	 * @param args
	 * @return
	 */
	public static String format(String str, Object... args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null){
				//str = str.replaceAll("\\{" + i + "\\}", args[i].toString());
				str = StringUtils.replace(str, "{" + i + "}", args[i].toString());
			}
		}
		
		return str;
	}
	
	public static boolean contains(List<String> list, String soughtFor, boolean ignoreCase) {
	    for (String current : list) {
	        if ((ignoreCase && current.equalsIgnoreCase(soughtFor)) || current.equals(soughtFor)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public static String format(double value, String pattern) {
		return new DecimalFormat(pattern).format(value);
	}
	
	public static String lowerFirst(String str){
		if(StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.substring(0,1).toLowerCase() + str.substring(1);
		}
	}
	
	public static String toLowerInvariant(String str){
		if(StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.toLowerCase(Locale.ROOT);
		}
	}
	
	public static String toUpperInvariant(String str){
		if(StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.toUpperCase(Locale.ROOT);
		}
	}
	
	public static String upperFirst(String str){
		if(StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.substring(0,1).toUpperCase() + str.substring(1);
		}
	}
	
	public static String remove(String str, int startIndex){
		return removeEnd(str, str.substring(startIndex));
	}
	
	public static String removeStart(String str, int endIndex){
		return removeStart(str, str.substring(0, endIndex));
	}
		
	public static String insert(final String target, final int position, final String insert) {
	    final int targetLen = target.length();
	    if (position < 0 || position > targetLen) {
	        throw new IllegalArgumentException("position=" + position);
	    }
	    if (insert.isEmpty()) {
	        return target;
	    }
	    if (position == 0) {
	        return insert.concat(target);
	    } else if (position == targetLen) {
	        return target.concat(insert);
	    }
	    final int insertLen = insert.length();
	    final char[] buffer = new char[targetLen + insertLen];
	    target.getChars(0, position, buffer, 0);
	    insert.getChars(0, insertLen, buffer, position);
	    target.getChars(position, targetLen, buffer, position + insertLen);
	    return new String(buffer);
	}

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)){
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 缩略字符串（替换html）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String rabbr(String str, int length) {
        return abbr(replaceHtml(str), length);
	}
		
	
	/**
	 * convert to Double
	 */
	public static Double toDouble(Object val, double defaValue){
		if (val == null){
			return defaValue;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return defaValue;
		} 
	}
	
	public static Double toDouble(Object val){
		return toDouble(val, Double.MIN_VALUE);
	}

	/**
	 * convert to Float
	 */
	public static Float toFloat(Object val, float defaValue){
		return toDouble(val, defaValue).floatValue();
	}
	
	public static Float toFloat(Object val){
		return toFloat(val, Float.MIN_VALUE);
	}

	/**
	 * convert to Long
	 */
	public static Long toLong(Object val){
		return toLong(val, Long.MIN_VALUE);
	}
	
	public static Long toLong(Object val, long defaValue){
		return toDouble(val, defaValue).longValue();
	}
	
	/**
	 * convert to Short
	 */
	public static Short toShort(Object val){
		return toShort(val, Short.MIN_VALUE);
	}
	
	public static Short toShort(Object val, short defaValue){
		return toLong(val, defaValue).shortValue();
	}

	/**
	 * convert to Integer
	 */
	public static Integer toInteger(Object val){
		return toInteger(val, Integer.MIN_VALUE);
	}
	
	/**
	 * convert to Integer
	 */
	public static Integer toInt(Object val){
		if (intRegEx == null)
			intRegEx = Pattern.compile("\\d+");
		
		Matcher m = intRegEx.matcher(val.toString());
		
		return (m.find() ? toInteger(m.group(0).trim()) : Integer.MIN_VALUE);
	}
		
	public static Integer toInteger(Object val, int defaValue){
		return toLong(val, defaValue).intValue();
	}
	
	public static BigDecimal toDecimal(Object val){
		return toDecimal(val, BigDecimal.ZERO);
	}
	
	public static BigDecimal toDecimal(Object val, BigDecimal defaValue){
		if (val == null){
			return defaValue;
		}
		try {
			return new BigDecimal(trim(val.toString()));
		} catch (Exception e) {
			return defaValue;
		} 
	}
	
	/**
	 * convert to specified type
	 */
	public static Optional<Object> toValue(Object src, Class<?> valType){
		return toValue(src, valType, getDefaultValue(valType));
	}
	
	public static Optional<Object> toValue(Object src, Class<?> valType, Object defaValue){
		Object val = null;
		try {
			if (valType == String.class){
				val = src;
			}else if (valType == Integer.class || valType == int.class){
				val = toInteger(src, defaValue == null ? Integer.MIN_VALUE : (int)defaValue);
			}else if (valType == Long.class || valType == long.class){
				val = toLong(src, defaValue == null ? Long.MIN_VALUE : (long)defaValue);
			}else if (valType == Double.class || valType == double.class){
				val = toDouble(src, defaValue == null ? Double.MIN_VALUE : (double)defaValue);
			}else if (valType == Float.class || valType == float.class){
				val = toFloat(src, defaValue == null ? Float.MIN_VALUE : (float)defaValue);
			}else if (valType == Boolean.class || valType == boolean.class){
				val = Boolean.parseBoolean(src.toString());	
			}else if (valType == Date.class){
				val = DateUtils.parseDate(src);
			}else if (valType.isEnum()){
				if (!StringUtils.isBlank(src.toString())) {
					val = valType.getMethod("valueOf", String.class).invoke(null, src);
				}else {
					val = defaValue;
				}
			}else if (valType.isArray()){
				val= HelperFunctions.toListFromCommaDelimited(src.toString());
			}
		} catch (Exception ex) {
			val = defaValue;
		}
		
		return (val!= null && val != getDefaultValue(valType)) ? Optional.of(val) : Optional.empty();
	}
	
	public static Object getDefaultValue(Class<?> valType){
		Object val = null;
		if (valType == String.class){
			val = null;
		}else if (valType == Integer.class || valType == int.class){
			val = Integer.MIN_VALUE;
		}else if (valType == Long.class || valType == long.class){
			val = Long.MIN_VALUE;
		}else if (valType == Double.class || valType == double.class){
			val = Double.MIN_VALUE;
		}else if (valType == Float.class || valType == float.class){
			val = Float.MIN_VALUE;
		}else if (valType == Boolean.class || valType == boolean.class){
			val = null; //Boolean.FALSE;	
		}else if (valType == Short.class || valType == short.class){
			val = Short.MIN_VALUE;
		}else if (valType == Byte.class || valType == byte.class){
			val = Byte.MIN_VALUE;
		}else if (valType == Date.class){
			val = DateUtils.MinValue;
		}else if (valType.isEnum()){
			val = null; //valType.getEnumConstants()[0];
		}
		
		return val;
	}
	
	/**
	 * covert array to set
	 * @param array
	 * @return
	 */
	public static Set<?> array2Set(Object[] array) {
		Set<Object> set = new TreeSet<Object>();
		for (Object id : array) {
			if(null != id){
				set.add(id);
			}
		}
		return set;
	}
	
	/**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static byte[] getBytes(String str){
    	if (str != null){
    		try {
				return str.getBytes(CHARSET_NAME);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
    	}else{
    		return null;
    	}
    }
    
    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static String toString(byte[] bytes){
    	try {
			return new String(bytes, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			return EMPTY;
		}
    }
    
    /**
     * 是否包含字符串
     * @param str 验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... strs){
    	if (str != null){
        	for (String s : strs){
        		if (str.equals(trim(s))){
        			return true;
        		}
        	}
    	}
    	return false;
    }
    
	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * @param html
	 * @return
	 */
	public static String replaceMobileHtml(String html){
		if (html == null){
			return "";
		}
		return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
	}
	
	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * @param txt
	 * @return
	 */
	public static String toHtml(String txt){
		if (txt == null){
			return "";
		}
		return replace(replace(Encodes.escapeHtml(txt), "\n", "<br/>"), "\t", "&nbsp; &nbsp; ");
	}

	public static String abbr2(String param, int length) {
		if (param == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		int n = 0;
		char temp;
		boolean isCode = false; // 是不是HTML代码
		boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;
		for (int i = 0; i < param.length(); i++) {
			temp = param.charAt(i);
			if (temp == '<') {
				isCode = true;
			} else if (temp == '&') {
				isHTML = true;
			} else if (temp == '>' && isCode) {
				n = n - 1;
				isCode = false;
			} else if (temp == ';' && isHTML) {
				isHTML = false;
			}
			try {
				if (!isCode && !isHTML) {
					n += String.valueOf(temp).getBytes("GBK").length;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			if (n <= length - 3) {
				result.append(temp);
			} else {
				result.append("...");
				break;
			}
		}
		// 取出截取字符串中的HTML标记
		String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)",
				"$1$2");
		// 去掉不需要结素标记的HTML标记
		temp_result = temp_result
				.replaceAll(
						"</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
						"");
		// 去掉成对的HTML标记
		temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>",
				"$2");
		// 用正则表达式取出标记
		Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
		Matcher m = p.matcher(temp_result);
		List<String> endHTML = Lists.newArrayList();
		while (m.find()) {
			endHTML.add(m.group(1));
		}
		// 补全不成对的HTML标记
		for (int i = endHTML.size() - 1; i >= 0; i--) {
			result.append("</");
			result.append(endHTML.get(i));
			result.append(">");
		}
		return result.toString();
	}
	
		
	/**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    /**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }
 
    /**
     * 转换为JS获取对象值，生成三目运算返回结果
     * @param objectString 对象串
     *   例如：row.user.id
     *   返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
     */
    public static String jsGetVal(String objectString){
    	StringBuilder result = new StringBuilder();
    	StringBuilder val = new StringBuilder();
    	String[] vals = split(objectString, ".");
    	for (int i=0; i<vals.length; i++){
    		val.append("." + vals[i]);
    		result.append("!"+(val.substring(1))+"?'':");
    	}
    	result.append(val.substring(1));
    	return result.toString();
    }
}
