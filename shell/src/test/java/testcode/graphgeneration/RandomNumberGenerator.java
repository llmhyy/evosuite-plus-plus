package testcode.graphgeneration;

import java.util.Random;

public class RandomNumberGenerator {
	private static Random random = null;
	private static long seed = 0L;
	
	private RandomNumberGenerator() {
	}
	
	public static void setSeed(long seed) {
		RandomNumberGenerator.seed = seed;
		random = new Random();
		random.setSeed(seed);
	}
	
	public static Random getInstance() {
		if (random == null) {
			seed = new Random().nextLong();
			random = new Random();
			random.setSeed(seed);
		}
		
		return random;
	}
	
	public static long getSeed() {
		return seed;
	}
}
