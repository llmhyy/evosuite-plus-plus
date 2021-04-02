package feature.fbranch.example;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.runtime.Random;

import feature.smartseed.example.empirical.IMenuItem;

public class SensitivityMutatorExample {
	String[] noiseString = { "substring", "abc10", "aacc", "!\\ms", "abc1", "longer" };
	char[] noiseChar = { ',', '"', '`', '!' };
	long[] noiseLong = { 8989l, 2147483690l, -5146483690l, 9223372036854775806l, -3372036854775808l };
	float[] noiseFloat = { 800f, -0.9f, -45.0999f, 0.9873f, 30000f, 0.989f, 0.963f };
	double[] noiseDouble = { 0.99, -23.66, 89, -100, 50.09 };

	// aaload
	public void aaloadExample(String[] x, String y) {
		int index = (int) (Math.random() * noiseString.length);
		String local = noiseString[index] + noiseChar.toString();

		index = (int) (Math.random() * x.length);
		if (x[index].equalsIgnoreCase(local)) {
			System.currentTimeMillis();
		}
	}

	// aload
	public void aloadExample(String x, int y) {
		x = x + "bytecode";
		if (x.equals("!!list_bytecode")) {
			System.currentTimeMillis();
		}
	}

	// aload_0
//	public void aload_0Example(String s,String local0) {
//		String local = local0 + "abcc";
//		int index = (int) (Math.random() * noiseChar.length);
//		noiseString[index] =  s + noiseChar[index];
//		if(noiseString[0].endsWith(local)) {
//			System.currentTimeMillis();
//		}
//		
//	}

	// baload
	public void baloadExample(boolean[] blist) {
		boolean[] local = blist.clone();
		int i = (int) (Math.random() * blist.length);
		int length = blist.length;
		local[i] = false;
		if (local[i]) {
			System.currentTimeMillis();
		}
	}

	// caload
	public void caloadExample(char[] clist) {
		char[] local = clist.clone();
		int i = (int) (Math.random() * 2230);
		int length = clist.length;
		local[length - 1] = (char) (clist[length - 1] + i);
		if (local[length - 1] == noiseChar[noiseChar.length - 1]) {
			System.currentTimeMillis();
		}
	}

	// d2f
	public void d2fExample(double x) {
		float f = Random.nextFloat();
		x = (float) ((x - 30000000) * 23.5);
		if ((float) x == f) {
			System.currentTimeMillis();
		}
	}

	// d2i
	public void d2iExample(double x, double y) {
		int i = Random.nextInt();
		if ((int) (x + 5863) == i) {
			System.currentTimeMillis();
		}
	}

	// d2l
	public long d2lExample(double x) {
		long local = Random.nextLong();
		if ((long) x == local - 13) {
			System.currentTimeMillis();
		}
		return (long) x - local;
	}

	// dadd
	public double daddExample(int[] x) {
		int index = (int) (Math.random() * x.length);
		double d = Random.nextDouble();
		if ((double) (x[index] + d) == (double) x[0] + d) {
			System.currentTimeMillis();
		}
		return 0;
	}

	// daload
	public void daloadExample(double[] dlist) {
		double[] local = dlist.clone();
		int size = noiseChar.length;
		local[dlist.length - 1] = Random.nextDouble() - 5400000.232;
		if (local[dlist.length - 1] == noiseChar[noiseChar.length - 1]) {
			System.currentTimeMillis();
		}
	}

	// ddiv
	public double ddivExample(double d) {
		int n = Random.nextInt(9999) + 13;
		n = Math.abs(n) + 5000;
		if (d / n == 12) {
			System.currentTimeMillis();
		}
		return d;
	}

	// dload
	public void dloadExample(double d) {
		double d0 = d + 99999;
		if (d0 == d * 23) {
			System.currentTimeMillis();
		}
	}

	// dload_0
//	public double dload_0Example(double d0) {
//		d0 = 0.15 + d0;
//		double[] d = noiseDouble.clone();
//		d[0] = d[0] + 0.151;
//		if(d0 == d[0]) {
//			System.currentTimeMillis();
//		}
//		return 1;
//	}

	// dmul
	public double dmulExample(double mul, double re) {
		mul = mul + Random.nextDouble();
		mul = Math.floor((mul));
		if (mul * Random.nextDouble() == noiseDouble[noiseDouble.length - 1]) {
			System.currentTimeMillis();
		}
		return re;
	}

	// dneg
	public double dnegExample(double d) {
		d = Math.abs(d) + 8292324;
		if (-d > 7777232) {
			System.currentTimeMillis();
		}
		return d;
	}

	// drem
	public void dremExample(double d) {
		d = Math.abs(d) + 8292324;
		d = Math.sqrt(d);
		if (d % 234 == 23) {
			System.currentTimeMillis();
		}
	}

	// dsub
	public void dsubExample(int x) {
		double d = Math.sqrt(x);
		if (d - 1200 == 58.2) {
			System.currentTimeMillis();
		}
	}

	// f2d
	public void f2dExample(float x) {
		float f = (float) (x + Math.rint(x - 77.934));
		if ((double) f == Random.nextDouble()) {
			System.currentTimeMillis();
		}
	}

	// f2i
	public void f2iExample(float x, int y) {
		float f = x + y * y * y;
		f = f / 1234;
		if ((int) f == (int) Math.ceil(noiseLong[0])) {
			System.currentTimeMillis();
		}
	}

	// f2l
	public void f2lExample() {
		float f = noiseFloat[noiseFloat.length - 2] / 0.89f;
		if ((long) f == noiseLong[noiseLong.length - 2]) {
			System.currentTimeMillis();
		}
	}

	// fadd
	public void faddExample() {
		float f0 = Random.nextFloat();
		float f1 = noiseFloat[0] + noiseFloat[noiseFloat.length - 1];
		if (f0 + f1 == Random.nextFloat()) {
			System.currentTimeMillis();
		}
	}

	// faload
	public void faloadExample() {
		List<String> flist = new ArrayList<String>();
		float f = Random.nextFloat();
		for (int i = 0; i < noiseFloat.length - 1; i++) {
			if (noiseFloat[i] == f + noiseFloat[i + 1]) {
				System.currentTimeMillis();
			}
		}
	}

	// fdiv
	public float fdivExample(float f) {
		int n = Random.nextInt(-9999) + 13;
		n = Math.abs(n) + 5000;
		if (f / n == 200.0f) {
			System.currentTimeMillis();
		}
		return f + 1;
	}

	// fload
	public void floadExample() {
		float f0 = Random.nextFloat();
		float f1 = noiseFloat[0] + noiseFloat[noiseFloat.length - 1];
		if (f0 + 56.23f == f1) {
			System.currentTimeMillis();
		}
	}

	// fload_0

	// fmul
	public void fmulExample(float f) {
		f = f + Random.nextFloat();
		if (f * Random.nextFloat() == noiseFloat[0]) {
			System.currentTimeMillis();
		}
	}

	// fneg
	public void fnegExample(float f) {
		f = f + Random.nextFloat();
		if (-(f / Random.nextFloat()) == noiseFloat[0] - noiseFloat[1]) {
			System.currentTimeMillis();
		}
	}

	// frem
	public void fremExample(double d) {
		float f = (float) (d + 23.35f);
		if (f % 23 == 0) {
			System.currentTimeMillis();
		}
	}

	// fsub
	public void fsubExample() {
		float f0 = noiseFloat[0];
		int index = (int) (Math.random() * noiseFloat.length);
		float f1 = noiseFloat[index] * f0;
		if (f1 - f0 > 3600000) {
			System.currentTimeMillis();
		}
	}

	// getfield
	public void getfieldExample(IMenuItem i) {
		String str = "sample";
		if (i.name.equals(str)) {
			System.currentTimeMillis();
		}
	}

	// getstatic
	public void getstaticExample(IMenuItem i) {
		String str = noiseString[noiseString.length - 3];
		if (IMenuItem.staticValue.contains(str)) {
			System.currentTimeMillis();
		}
	}

	// i2d
	public void i2dExample(int x, int y) {
		double z = Math.floor((y + 999999) / 1333);
		if (x == z) {
			System.currentTimeMillis();
		}
	}

	// iload_0
	public boolean iload_0Example(String x) {
		List<String> y = new ArrayList<String>();
		int index = (int) (Math.random() * noiseChar.length);
		y.add(x);
		if (y.contains(noiseChar[index])) {
			System.currentTimeMillis();
			return true;
		}
		return false;
	}

	// iload_1
	public void iload_1Example(char a) {
		char c = '\\';
		a = (char) (a + c);
		if (a == 's') {
			System.currentTimeMillis();
		}
	}

	// iload_2
	public void iload_2Example(char cc, char a) {
		char b = 'B';
		a = (char) (a + b);
		if (a < 'A') {
			System.currentTimeMillis();
		}
	}

	// invokevirtual
	public void invokevirtualExample(String x) {
		List<String> y = new ArrayList<String>();
		y.add(x);
		int index = (int) (Math.random() * noiseString.length);
		String cons = noiseString[index];
		index = (int) (Math.random() * noiseChar.length);
		if (y.contains(cons + noiseChar[index])) {
			System.currentTimeMillis();
		}
	}

}
