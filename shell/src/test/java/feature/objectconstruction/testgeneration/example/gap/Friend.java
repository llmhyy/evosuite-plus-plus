package feature.objectconstruction.testgeneration.example.gap;

public class Friend {
	Parent parent = new Parent();
	
	public void setIndex(int index) {
		this.parent.getAccount().setIndex(index);
	}
}
