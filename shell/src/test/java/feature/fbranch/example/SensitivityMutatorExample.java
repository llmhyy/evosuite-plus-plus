package feature.fbranch.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
		if (x[index].compareTo(local) >= 2) {
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
	public void baloadExample(byte[] blist) {
		byte[] local = blist.clone();
		int i = (int) (Math.random() * blist.length);
		for(int j = 0;j < blist.length;j++)
			local[j] = (byte) (local[j] * 13 + 5);
		if (local[i] >= (byte)(140>>2)) {
			System.currentTimeMillis();
		}
	}

	// caload
	public void caloadExample(char[] clist) {
		char[] local = clist.clone();
		int i = (int) (Math.random() * (clist.length-1));
		int length = clist.length;
		local[i] = (char) (local[i] + '2');
		if (local[i] >= '*') {
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
	public double daddExample(double x,int y) {
		double z = x + y * (-233);
		z = z / 2;
		if ((double) (z + y) == 2300.0) {
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
	
	//i2b
	public void i2bExample(int x) {
		x = (x/1000) ^ (x >>> 20);
		if((byte) x > 99999) {
			System.currentTimeMillis();
		}
	}
	
	//i2c
	public void i2cExample(int x) {
		int index = (int) (Math.random() * noiseChar.length);
		x = x - noiseChar[index];
		if((char) x > noiseChar[index] >>> 2) {
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
	
	//i2f、i2l、i2s
	public void i2flsExample(int x,int y) {
		int index = (int) (Math.random() * noiseLong.length);
		float f = Random.nextFloat();
		if((float) (x * 333) >= (float) Math.pow(10, y)) {//i2f
			System.currentTimeMillis();
		}
		if((long) y > noiseLong[index] >>> 4) {//i2l
			y = y * y * y;
			if((short) y <= (short)f) {//i2s
				System.currentTimeMillis();
			}
		}
	}
	
	//iadd、idiv、imul、ineg、isub
	public void iaddExample(int x,int y,int z) {
		int x0 = x + y;
		int y0 = y * z;
		int z0 = z / (Math.abs(x) >>> 33);
		if(z0 + x < 233) {//iadd
			y0 = y0 - z0;}
			if(y0 / z0 > 18888) {//idiv
				x = x << 10;}
				if(x0 * y >= 189009) {//imul
					y = y * y;}
					if(-y >= x + z0) {//ineg
						System.currentTimeMillis();
					}
//				}
//			}
//		}
		
		if(y - y0 > z0) {//isub
			System.currentTimeMillis();
		}
	}
	
	//iaload、iload
	public void ialoadExample(int[] x,int y) {
		int[] x0 = x.clone();
		y = y * x0[0] - 999992;
		for(int i = 0;i < x0.length - 1;i++) {
			if(x[i] == y) {//iload
				System.currentTimeMillis();
			}else {
				x[i] += y;
				if(x[i] >= 99999999) {//iaload
					System.currentTimeMillis();
				}
			}
		}
	}
	
	//iand、ior、ixor
	public void iandExample(int x,int y,int z) {
		int x0 = x * z;
		int y0 = Math.abs(y);
		if((x0 & y0) > 167776512) {//iand
			x = x + 167776512;}
			if((x | y0) > 167776512) {//ior
				System.currentTimeMillis();
			}else {
				if((x ^ z) > y + y0) {//ixor
					System.currentTimeMillis();
				}
			}
//		}
	}
		
	//iinc、irem
	public void iremExample(int x,int y) {
		x = x & y;
		if(x % 33 > 32) {//irem
			System.currentTimeMillis();
		}
	}

	// iload_0 ??
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
	
	//instanceof
	public void instanceofExample(Object c) {
		String local = "stringAbc";
		int x = 12;
		if(c instanceof String) {
			System.currentTimeMillis();
		}
	}
	
	//invokeinterface、invokespecial、invokestatic
	public void invokeinterfaceExample(String s) {
		List<String> y = new ArrayList<String>();
		if(!y.add(s)) {//invokeinterface
			System.currentTimeMillis();
		}
		String x = "random" + s;
		IMenuItem imenu = new IMenuItem(s);
		if(specialMethod(x).equals("str")) {//invokespecial
			System.currentTimeMillis();}
			if(imenu.staticInvokeMethod(s).contains("with")) {//invokestatic
				System.currentTimeMillis();
			}
//		}
	}
	
	private String specialMethod(String x) {
		x = x + "end";
		return x;
	}
	

	// invokevirtual
	public void invokevirtualExample(String x) {
		int index = (int) (Math.random() * noiseString.length);
		x = x + noiseString[index];
		index = 0;
		List<String> y = new ArrayList<String>();
		y.add(x);
		if (x.substring(index).contains("su")) {
			System.currentTimeMillis();
		}
	}
	
	//ishl、ishr、iushr
	public void ishlExample(int x,int y,int z) {
		int x0 = x * z;
		int y0 = y / 1333;
		if((x0 << 10) > 55555) {//ishl
			x0 = x0 << 3;}
			if((x0 >> 10) == 9999999) {//ishr
				System.currentTimeMillis();
			}
			if(x0 >>> 10 == 9999999) {//iushr
				System.currentTimeMillis();
			}
//		}
	}
	
	//l2d、l2f、l2i
	public void l2dExample(long x,long y,long z) {
		int index = (int) (Math.random() * noiseLong.length);
		x = x + 10;
		if((int) x > index) {//l2i
			y = y + x;}
		if((double)y <= (-23.5) * z) {//l2d
			System.currentTimeMillis();
		}
		z = Math.abs(z + 89) - 332231;
		if((float) (z + 23000) >= (float) (x - y)){//l2f
			System.currentTimeMillis();
		}
//		}
	}
	
	//ladd、ldiv、lmul、lneg、lsub、lrem
	public void laddExample(long x,long y) {
		long z = x + y - 2233l;
		if((x + z) > 1119999l) {//ladd
			x += 1119999l;}
			if((x / 19) > 200) {//ldiv
				y = y >> 2;}
			if((y + 233l) * z <= -2000230l) {//lmul
				System.currentTimeMillis();
			}
			if(-x < -1l) {//lneg
				x = -x;
				}
			if(x - y >= 1119999l)//lsub
				System.currentTimeMillis();
			if(x % 900 == 371)//lrem
				System.currentTimeMillis();
//		}
	}
	
	//laload、lload、lload_0
	public void laloadExample(long[] x,long y) {
		long y0 = y * x[0];
		y0 = y0 % (Math.abs(y) + 9) - 343;
		for(int i = 0;i < x.length - 1;i++) {
			if(y0 == y) {//lload
				System.currentTimeMillis();
			}else {
				x[i] += y;
				if(x[i] >= 99999999l) {//laload
					System.currentTimeMillis();
				}
			}
		}
	}
	
	//land、lor、lxor
	public void landExample(long x,long y,long z) {
		long x0 = x * z;
		long y0 = Math.abs(y) + 3333333;
		if((x0 & y0) > 167776512l) {//land
			x = x + 167776512;}
			if((x | y0) > 167776512l) {//lor
				System.currentTimeMillis();
			}else {
				if((x ^ z) > y + y0) {//lxor
					System.currentTimeMillis();
				}
			}
//		}
	}
	

	//lookupswitch ??
	public void lookupswitchExample(String x,String y) {
		char[] x1 = x.toCharArray();
		char[] y1 = y.toCharArray();
		for(char c : x1) {
			switch(c) {
			case 'A':
			case 'a':
				for(char c2:y1) {
					switch(c2) {
					case 's':
					case 'S':
						break;
					}
				}
				break;
			case ':':
				break;
			case '!':
				break;
			case ' ':
				break;
			default:
				break;
			}
		}
	}
	
	//lshl、lshr、lushr
	public void lshlExample(long x,long y,long z) {
		long x0 = x * z;
		long y0 = y / 1333;
		if((x0 << 10) > 55555l) {//lshl
			x0 = x0 << 3;
			if((x0 >> 10) == 9999999l) {//lshr
				System.currentTimeMillis();
			}
			if(x0 >>> 10 == 9999999l) {//lushr
				System.currentTimeMillis();
			}
		}
	}
		
	//saload
	public void saloadExample(short[] s) {
		int i = (int) (Math.random() * s.length);
		s[i] = (short) (s[i] + 1100);
		if(s[i] > (short) 99999999) {//TODO
			System.currentTimeMillis();
		}
	}
	
	//i2s
	public void i2sExample(int x,int y) {
		int index = (int) (Math.random() * noiseLong.length);
		float f = Random.nextFloat();
		if((float) (x * 333) >= (float) Math.pow(10, y)) {//i2f
			System.currentTimeMillis();
		}
		y = y * y * y;
		if((short) y <= (short)f) {//i2s
			System.currentTimeMillis();
		}
	}
	
}
