package feature.objectconstruction.testgeneration.example.gap;

/**
 * Naming convention:
 * We following an alphanumeric naming convention:
 * 1) Classes are named based on how they are composed
 *    e.g. A is composed of B, which is composed of C, and so on
 */
public class Employee {
	private Friend friend = new Friend(); //private Friend friend; //
	
	public Friend getFriend() {
		return this.friend;
	}
	
	/**
	 * [Long setter case]
	 * This method aims to trigger generation (and test) the
	 * following OCG:
	 * 
	 *           X    X
 	 * A -> B -> C -> D -> I
	 * 
	 * where there exists no getter for C, D but there exists
	 * a way to set I directly. The new approach should be able to
	 * 1) Detect this situation (cannot construct getters for both C, D)
	 * 2) Detect the method that can solve this situation 
	 * 3) Construct the setter for I in the test case
	 * 
	 * The optimal test case would be similar to:
	 *   
	 *   A a0 = new A();
	 *   B b0 = a0.getB();
	 *   int int0 = <some non-zero value>;
	 *   b0.setI(int0);
	 *   a0.method();
	 */
	public void method() {
		if (this.friend.parent.getAccount().getIndex() != 0) {
			System.currentTimeMillis();
		}
	}
}
