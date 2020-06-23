package regression.objectconstruction.graphgeneration.example.instancefield;

/**
 * Test the computation graph of public field generation.
 *
 */
public class PublicInstanceFieldExample {
	public Student student1;

	public void targetM(int index) {
		if (student1.getAge() < 18) {
			System.currentTimeMillis();
		}
	}
}
