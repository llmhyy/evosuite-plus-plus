package testcode.graphgeneration.model;

public class Method {
	private String visibility = "private";
	private String name;
	private String returnType;

	public Method(String name, String returnType) {
		super();
		this.name = name;
		this.returnType = returnType;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

}
