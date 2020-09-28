package regression.objectconstruction.testgeneration.example.cascadecall;

/**
 * Test the computation graph of cascading call to set field. 
 *
 */
public class CascadingCallExample {

	private int fieldToSet;
	
	public CascadingCallExample(ClassA a, ClassB b) {
//		m1(a, b);
	}
	
	public void targetM() {
		if (fieldToSet > 10) {
			return;
		}
	}
	
	public void m1(ClassA a, ClassB b) {
		this.m2(a.c, b.d);
	}
	
	public void m2(ClassC c, ClassD d) {
		this.m3(c.e, d.f);
	}
	
	public void m3(ClassE e, ClassF f) {
		this.fieldToSet = e.field + f.field;
	}
}
