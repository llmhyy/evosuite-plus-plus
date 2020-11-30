package feature.hybrid.example;

public class DrillerExample {
	public int x;
	public int magic;
	public String[] directives;
	
	public DrillerExample(int m, int x, String s1,String s2) {
		this.x = x;
		magic = m;
		directives = new String[] {s1,s2};
	}
}
