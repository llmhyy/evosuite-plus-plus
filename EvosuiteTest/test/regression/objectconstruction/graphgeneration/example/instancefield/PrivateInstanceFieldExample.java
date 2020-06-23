package regression.objectconstruction.graphgeneration.example.instancefield;

/**
 * Test the computation graph of private field generation. 
 *
 */
public class PrivateInstanceFieldExample {
	private int field;
	
	public void targetM(int index){
		if(field < 2000){
			System.currentTimeMillis();
		}
	}
	
	public void setField(int field) {
		this.field = field;
	}
	
	public int getField() {
		return field;
	}
}
