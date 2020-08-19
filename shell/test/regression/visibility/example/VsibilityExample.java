package regression.visibility.example;

public class VsibilityExample {
	
	private String s = "asdfadadsfasdfasdvsdfafasfasdfsdfadfasfasdfasdfasdfadsfasfds";
	
	
	public void example(String str) {
		if(str.equals(s)) {
			return;
		}
	}
	
	
	private String getString1() {
		return s; 	
	}
	
	protected String getString2() {
		return s;
	}
}
