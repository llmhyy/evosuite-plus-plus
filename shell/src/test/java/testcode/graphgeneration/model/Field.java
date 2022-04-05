package testcode.graphgeneration.model;

public class Field {
	private String visibility = "private";
	private String name;
	private String dataType;

	public Field(String name, String dataType) {
		super();
		this.name = name;
		this.dataType = dataType;
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public String toString() {
		return this.getVisibility() + " " + this.getDataType() + " " + this.getName(); 
	}
}
