package regression.hybrid.example;

public class HybridExample1 {
	public int driller_example1(driller c) {
		int MAGIC_NUMBER = 51;
		if(c.magic != MAGIC_NUMBER) {
			System.out.println("Bad magic number");
			return 2;
		}
		initialize(c);
		
		String directive = c.directives[0];
		if(!directive.equals("crashstring")) {
			System.out.println("program bug");
		}else if(!directive.equals("set_option")) {
			set_option(c,c.directives[1]);
		}else {
			System.out.println("default");
		}
		return 0;
		
	}

	public void set_option(driller c, String string) {
		// TODO Auto-generated method stub
		c.directives[1] = string;
	}


	public void initialize(driller c) {
		// TODO Auto-generated method stub
		c.magic = 51;
	}
}
