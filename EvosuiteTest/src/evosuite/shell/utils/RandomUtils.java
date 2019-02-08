/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.shell.utils;

import java.util.Random;

import org.evosuite.utils.CollectionUtil;

/**
 * @author LLT
 *
 */
public class RandomUtils {
	
	public static <T> T randomMember(T[] arr, Random random) {
		if (CollectionUtil.isEmpty(arr)) {
			return null;
		}
		return arr[random.nextInt(arr.length)];
	}
	
	public static int nextInt(int min, int max, Random random) {
		if (max - min + 1 > 0) {
			return random.nextInt((max - min) + 1) + min;
		} 
		return random.nextInt(max - min) + min;
	}
	
	public static boolean weighedCoinFlip(double trueProb, Random random) {
		if (trueProb < 0 || trueProb > 1) {
			throw new IllegalArgumentException("arg must be between 0 and 1.");
		}
		double falseProb = 1 - trueProb;
		return (random.nextDouble() >= falseProb);
	}
	
	public static Long nextLong(int min, int max, Random random) {
		// error checking and 2^x checking removed for simplicity.
		long bits, val;
		long n = max - min;
		do {
			bits = (random.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return val + min;
	}
	
}
