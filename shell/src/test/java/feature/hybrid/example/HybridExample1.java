package feature.hybrid.example;

public class HybridExample1 {
	public int driller_example1(DrillerExample c) {
		int MAGIC_NUMBER = 51;
		
		if(number(c.x) * Math.log(c.x) < 100000000000000l) {
			return 0;
		}
		
		if(c.magic != MAGIC_NUMBER) {
			System.out.println("Bad magic number");
			return 2;
		}
		initialize(c);
		
		String directive = c.directives[0];
		if(directive.equals("crashstring")) {
			System.out.println("program bug");
		}else if(directive.equals("set_option")) {
			set_option(c,c.directives[1]);
		}else {
			System.out.println("default");
		}
		return 0;
		
	}

	public void set_option(DrillerExample c, String string) {
		// TODO Auto-generated method stub
		c.directives[1] = string;
	}


	public void initialize(DrillerExample c) {
		// TODO Auto-generated method stub
		c.magic = 51;
	}
	
	public int number(int x) {
		if(x<=1) {
			return 1;
		}
		else {
			return x * number(x-1);
		}
	}
}
