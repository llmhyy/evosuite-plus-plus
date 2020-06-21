package regression.objectconstruction.graphgeneration.example.staticfield;

/**
 * this example is to test when the computation graph work well to generate public static field. 
 * @author Yun Lin
 *
 */
public class PublicStaticFieldExample {
	
	private String name;
	
	public PublicStaticFieldExample(String name) {
		this.setName(name);
	}

	public void targetM(){
		if(Config.numPublic > 1000){
			if(Config.objPublic.getName().equals("test")){
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
