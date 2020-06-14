package regression.objectconstruction.example.staticfield;

/**
 * this example is to test when the computation graph work well to generate private static field. 
 *
 */
public class PrivateStaticFieldExample {
	private String name;

	public PrivateStaticFieldExample(String name) {
		this.setName(name);
	}

	public void method() {
		if (Config.getNumPrivate() > 1000) {
			if (Config.getObjPrivate().getName().equals("test")) {
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
