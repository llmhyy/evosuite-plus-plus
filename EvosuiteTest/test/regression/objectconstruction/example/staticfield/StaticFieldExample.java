package regression.objectconstruction.example.staticfield;

/**
 * this example is to test when the computation graph work well to generate static field. 
 * @author Yun Lin
 *
 */
public class StaticFieldExample {
	
	private String name;
	
	public StaticFieldExample(String name) {
		super();
		this.setName(name);
	}

	public void method(){
		if(Config.num > 1000){
			if(Config.object.getName().equals("test")){
				System.currentTimeMillis();
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
