package regression.objectconstruction.graphgeneration.example.arrayelement;

/**
 * Test the computation graph of array element generation. 
 *
 */
public class ArrayElementExample {
	
	private int[] array = new int[10];
	
	public void targetM(int index){
		if(array[index] < 2000){
			System.currentTimeMillis();
		}
	}
	
	public void addElement(int a, int i){
		if(a > 1000){
			this.getArray()[i] = a;
		}
	}

	public int[] getArray() {
		return array;
	}

	public void setArray(int[] array) {
		this.array = array;
	}
	
}
