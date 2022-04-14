package testcode.graphgeneration;

public class NodeType {
	/**
	 * data type of the variable
	 */
	private String type;
	/**
	 * variable name (field name, array name ..)
	 */
	private String name;

	
	
	public NodeType(String type, String name) {
		super();
		this.type = type;
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
