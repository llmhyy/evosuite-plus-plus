package regression.stringconstraints.example;

public class StringConstraintExample {

	public int method(String var0, String var1, String var2) {
		if (var0.length() == 100) {
			System.currentTimeMillis();
		} else
			return 0;
		
		if(!var0.contains(var1))
			return 0;
		
		if (!var2.endsWith(var1))
			return 0;
		
		String tmp  = var0.replace(var1, "abc");
		if (var2.equals(tmp)) {
			return 1;
		} else
			return 0;
		
		
	}

}
