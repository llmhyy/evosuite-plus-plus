package regression.objectconstruction.example.instancefield;

import com.example.Student;

/**
 * Test the computation graph of public field generation. 
 *
 */
public class PublicInstanceFieldExample {
	public Student student1;
	
	public void method(int index){
		if(student1.getAge() < 18){
			System.currentTimeMillis();
		}
	}
}
