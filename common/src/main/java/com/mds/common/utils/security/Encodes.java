/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.common.utils.security;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.StringEscapeUtils;

//
import com.mds.common.exception.EncryptDecryptException;
import com.mds.common.exception.Exceptions;

/**
 * 封装各种格式的编码解码工具类.
 * 1.Commons-Codec的 hex/base64 编码
 * 2.自制的base62 编码
 * 3.Commons-Lang的xml/html escape
 * 4.JDK提供的URLEncoder
 * @author calvin
 * @version 2013-01-15
 */
public class Encodes {

	private static final String DEFAULT_URL_ENCODING = "UTF-8";
	private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
	
	//Define encrypt method，DES、DESede(3DES)、Blowfish
	private static final String Algorithm = "DESede";    
    private static final String PASSWORD_CRYPT_KEY = "*&^K^%X威dVd56T6$M$J$#sw冠H#q1@Q@$A$&*fdNd";
    //int[] byKeys = {42,38,94,75,94,37,88,229,168,129,100,86,100,53,54,84,54,36,77,36,74,36,35,115,119,229,134,160,72,35,113,49,64,81,64,36,65,36,38,42,102,100,78,100};

	/**
	 * Hex编码.
	 */
	public static String encodeHex(byte[] input) {
		return Hex.encodeHexString(input);
	}

	/**
	 * Hex解码.
	 */
	public static byte[] decodeHex(String input) {
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (DecoderException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * Base64编码.
	 */
	public static String encodeBase64(byte[] input) {
		return Base64.encodeBase64String(input);
	}

	/**
	 * Base64编码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548).
	 */
	public static String encodeUrlSafeBase64(byte[] input) {
		return Base64.encodeBase64URLSafeString(input);
	}

	/**
	 * Base64解码.
	 */
	public static byte[] decodeBase64(String input) {
		return Base64.decodeBase64(input);
	}

	/**
	 * Base62编码。
	 */
	public static String encodeBase62(byte[] input) {
		char[] chars = new char[input.length];
		for (int i = 0; i < input.length; i++) {
			chars[i] = BASE62[((input[i] & 0xFF) % BASE62.length)];
		}
		return new String(chars);
	}

	/**
	 * Html 转码.
	 */
	public static String escapeHtml(String html) {
		return StringEscapeUtils.escapeHtml4(html);
	}

	/**
	 * Html 解码.
	 */
	public static String unescapeHtml(String htmlEscaped) {
		return StringEscapeUtils.unescapeHtml4(htmlEscaped);
	}

	/**
	 * Xml 转码.
	 */
	public static String escapeXml(String xml) {
		return StringEscapeUtils.escapeXml11(xml);
	}

	/**
	 * Xml 解码.
	 */
	public static String unescapeXml(String xmlEscaped) {
		return StringEscapeUtils.unescapeXml(xmlEscaped);
	}

	/**
	 * URL 编码, Encode默认为UTF-8. 
	 */
	public static String urlEncode(String part) {
		try {
			return URLEncoder.encode(part, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * URL 解码, Encode默认为UTF-8. 
	 */
	public static String urlDecode(String part) {

		try {
			return URLDecoder.decode(part, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}
	
	public static byte[] encrypt(byte[] message, byte[] password) throws EncryptDecryptException  {	
		try {
			if (password == null)
	    		password = PASSWORD_CRYPT_KEY.getBytes("utf-8");
			
	        final MessageDigest md = MessageDigest.getInstance("md5");
	        final byte[] digestOfPassword = md.digest(password);
	        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
	        for (int j = 0, k = 16; j < 8;) {
	            keyBytes[k++] = keyBytes[j++];
	        }
	
	        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
	        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
	        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
	
	        //final byte[] plainTextBytes = message.getBytes("utf-8");
	        final byte[] cipherText = cipher.doFinal(message);
	        // final String encodedCipherText = new sun.misc.BASE64Encoder()
	        // .encode(cipherText);
	        return cipherText;
		}catch(Exception ex) {
			throw new EncryptDecryptException("Encrypt failure", ex);
		}
	}
	
	public static byte[] encrypt(String message, String password) throws EncryptDecryptException, UnsupportedEncodingException  {
		if (password == null)
    		password = PASSWORD_CRYPT_KEY;
		
		return encrypt(message.getBytes("utf-8"), password.getBytes("utf-8"));
    }
	
	public static String decrypt(byte[] message, byte[] password) throws EncryptDecryptException {
		try {
			if (password == null)
	    		password = PASSWORD_CRYPT_KEY.getBytes("utf-8");
			
	    	final byte[] digestOfPassword = Encodes.encodeBase64(password).getBytes("UTF-8"); //PASSWORD_CRYPT_KEY.getBytes("utf-8");
	        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
	        final byte[] ivBytes = Arrays.copyOf(digestOfPassword, 8);
	        /*for (int j = 0, k = 16; j < 8;) {
	            keyBytes[k++] = keyBytes[j++];
	        }*/
	
	        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
	        final IvParameterSpec iv = new IvParameterSpec(ivBytes);
	        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding"); //
	        decipher.init(Cipher.DECRYPT_MODE, key, iv);
	
	        // final byte[] encData = new
	        // sun.misc.BASE64Decoder().decodeBuffer(message);
	        final byte[] plainText = decipher.doFinal(message);
	
	        return new String(plainText, "UTF-8");
		}catch(Exception ex) {
			throw new EncryptDecryptException("Decrypt failure", ex);
		}
	}

    public static String decrypt(String encryptedText, String password) throws EncryptDecryptException, UnsupportedEncodingException {
        /*final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(PASSWORD_CRYPT_KEY
                .getBytes("utf-8"));*/
    	if (password == null)
    		password = PASSWORD_CRYPT_KEY;
    	
    	return decrypt(Encodes.decodeBase64(encryptedText), password.getBytes("utf-8"));
    }
}
