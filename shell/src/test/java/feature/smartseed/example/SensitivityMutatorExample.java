package feature.smartseed.example;

import java.util.ArrayList;
import java.util.List;

public class SensitivityMutatorExample {
	String[] noiseString = {"substring","abc10","aacc","!\\ms","abc1","longer"};
	char[] noiseChar = {',','"','`','!'};
	
	//aaload 
		public void aaloadExample(String[] x,String y) {
			int index = (int) (Math.random() * noiseString.length);
			String local = noiseString[index] + noiseChar.toString();
			
			index = (int) (Math.random() * x.length);
			if(x[index].equalsIgnoreCase(local)) {
				System.currentTimeMillis();
			}
		}
	
	//aload
	public void aloadExample(String x,int y) {
		x = x + "bytecode";
		if(x.equals("!!list_bytecode")) {
			System.currentTimeMillis();
		}
	}
	
	//iload_0
	public boolean iload_0MethodExample(String x) {
		List<String> y = new ArrayList<String>();
		int index = (int) (Math.random() * noiseChar.length);
		y.add(x);
		if(y.contains(noiseChar[index])) {
			System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	//iload_1 ??
	public void iload_1Example(char a) {
		char c = '\\';
		a = (char) (a + c);
		if(a == 's') {
			System.currentTimeMillis();
		}	
	}
	
	//iload_2
	public void iload_2Example(char cc,char a) {
		char b = 'B';
		a = (char) (a + b);
		if(a < 'A') {
			System.currentTimeMillis();
		}	
	}
	
	//caload
	public void caloadExample(char[] clist) {
		int i = (int) (Math.random() * 2230);
		int length = clist.length;
		clist[length - 1] = (char) (clist[length - 1] + i);
		if(clist.equals(noiseChar)) {
			System.currentTimeMillis();
		}
	}
	
	
	//invokevirtual toSting
	public void invokevirtualExample(String x) {
		List<String> y = new ArrayList<String>();
		y.add(x);
		int index = (int) (Math.random() * noiseString.length);
		String cons = noiseString[index];
		index = (int) (Math.random() * noiseChar.length);
		if(y.contains(cons + noiseChar[index])) {
			System.currentTimeMillis();
		}
	}
	
}
