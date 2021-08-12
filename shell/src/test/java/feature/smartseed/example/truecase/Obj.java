package feature.smartseed.example.truecase;

public class Obj {
	private int attribute;
	public String name;

	public Obj(int attribute, String name) {
		super();
		this.attribute = attribute;
		this.name = name;
	}

	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
