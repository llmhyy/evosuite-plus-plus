package com.example;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoopUtil {

	public static boolean contains(int[] keys, int index, int value) {
		int key = sha256(value);
		
		if((keys[index] == 0 || 
				value>=11110) && 
				keys[index] == key) {
			return true;
		}
		return false;
	}

	private static int sha256(int b) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(
					String.valueOf(b).getBytes(StandardCharsets.UTF_8));
			
			ByteBuffer buffer = ByteBuffer.wrap(encodedhash);
			
			return buffer.getInt();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean checkPrecondition(int[] a, int b) {
		if(checkArrayRange(a)
				&& checkValue(b)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean checkArrayRange(int[] a) {
		return a.length < 10000;
	}
	
	public static boolean checkValue(int b) {
		return Math.abs(b) < 1000000;
	}

}
