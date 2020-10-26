/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.util;
 
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
 
/**
 * @author Crunchify.com 
 * Best way to generate very secure random Password automatically
 * 
 */
 
public class RandomPasswordGenerator {
 
	// SecureRandom() constructs a secure random number generator (RNG) implementing the default random number algorithm.
	private SecureRandom crunchifyRandomNo = new SecureRandom();
 
	private ArrayList<Object> crunchifyValueObj;
 
	public static String nextString() {
		RandomPasswordGenerator passwordGenerator = new RandomPasswordGenerator();
 
		log("Crunchify Password Generator Utility: \n");
		StringBuilder crunchifyBuffer = new StringBuilder();
 
		// Password length should be 23 characters
		for (int length = 0; length < 23; length++) {
			crunchifyBuffer.append(passwordGenerator.crunchifyGetRandom());
		}
 		
		return crunchifyBuffer.toString();
	}
 
	// Just initialize ArrayList crunchifyValueObj and add ASCII Decimal Values
	public RandomPasswordGenerator() {
 
		crunchifyValueObj = new ArrayList<>();
 
		// Adding ASCII Decimal value between 33 and 53
		for (int i = 33; i < 53; i++) {
			crunchifyValueObj.add((char) i);
		}
 
		// Adding ASCII Decimal value between 54 and 85
		for (int i = 54; i < 85; i++) {
			crunchifyValueObj.add((char) i);
		}
 
		// Adding ASCII Decimal value between 86 and 128
		for (int i = 86; i < 127; i++) {
			crunchifyValueObj.add((char) i);
		}
		crunchifyValueObj.add((char) 64);
 
		// rotate() rotates the elements in the specified list by the specified distance. This will create strong password
		Collections.rotate(crunchifyValueObj, 5);
	}
 
	// Get Char value from above added Decimal values
	// Enable Logging below if you want to debug
	public char crunchifyGetRandom() {
		char crunchifyChar = (char) this.crunchifyValueObj.get(crunchifyRandomNo.nextInt(this.crunchifyValueObj.size()));
 
		// log(String.valueOf(crunchifyChar));
		return crunchifyChar;
	}
 
	// Simple log util
	private static void log(String string) {
		System.out.println(string);
 
	}
 
	// Simple log util
	private static void log(int count, String password) {
		System.out.println("Password sample " + count + ": " + password);
 
	}
 
}