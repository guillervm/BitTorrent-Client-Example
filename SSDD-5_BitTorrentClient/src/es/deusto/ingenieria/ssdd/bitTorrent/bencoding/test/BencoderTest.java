package es.deusto.ingenieria.ssdd.bitTorrent.bencoding.test;

/*
 * Copyright 2006 Robert Sterling Moore II
 * 
 * This computer program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This computer program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this computer program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * 
 * UPDATED BY: Roberto Carballedo (Septembre 2013)
 * - Code has been updated in order to avoid Java 1.7 compiler warnings.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import es.deusto.ingenieria.ssdd.bitTorrent.bencoding.Bencoder;

public class BencoderTest {
	public BencoderTest() {
		Bencoder bencoder = new Bencoder();

		/*
		 * Strings
		 */
		byte[] encoded = bencoder.bencodeString("Hello World!");
		System.out.print("- Bencoded String: '");
		
		for (int i = 0; i < encoded.length; i++) {
			System.out.print((char) encoded[i]);
		}
		
		System.out.println("'\n- Unbencoded String: " + bencoder.unbencodeString(encoded));
		/*
		 * Integer
		 */
		encoded = bencoder.bencodeInteger(new Integer(-123456));	
		System.out.print("\n- Bencoded Integer: '");
		
		for (int i = 0; i < encoded.length; i++) {
			System.out.print((char) encoded[i]);
		}
		
		System.out.println("'\n- Unbencoded Integer: " + bencoder.unbencodeInteger(encoded).intValue());

		/*
		 * Dictionary
		 */
		HashMap<String, Object> encodedMap = new HashMap<>();
		encodedMap.put("Text", "Hello!");
		encodedMap.put("Number", new Integer(234));
		encoded = bencoder.bencodeDictionary(encodedMap);
		
		System.out.print("\n- Bencoded Dictionary: '");

		for (int i = 0; i < encoded.length; i++) {
			System.out.print((char) encoded[i]);
		}
		
		System.out.println("'\n- Unbencoded Dictionary:");
		
		HashMap<String, Object> unbencodedMap = bencoder.unbencodeDictionary(encoded);
		Iterator<Entry<String, Object>> it = unbencodedMap.entrySet().iterator();
		Entry<String, Object> entry = null;
		
		while (it.hasNext()) {
			entry = it.next();
			System.out.println("   * Key: '" + entry.getKey() + "' - Value: '" + entry.getValue().toString() + "'");
		}

		/*
		 * List
		 */
		List<Object> encodedList = new ArrayList<Object>();
		encodedList.add("Hello!");
		encodedList.add(new Integer(-345));
		encodedList.add("Goodbye!");
		encodedList.add(unbencodedMap);
		encoded = bencoder.bencodeList(encodedList);
		
		System.out.print("\n- Bencoded List: '");
		
		for (int i = 0; i < encoded.length; i++) {
			System.out.print((char) encoded[i]);
		}
		
		List<Object> unbencodedList = bencoder.unbencodeList(encoded);
		System.out.print("'\n- Unbencoded List: " + unbencodedList);
	}

	public static void main(String[] args) {
		new BencoderTest();
	}
}