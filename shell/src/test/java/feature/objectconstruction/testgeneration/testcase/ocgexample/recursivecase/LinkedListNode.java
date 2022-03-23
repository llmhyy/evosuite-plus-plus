package feature.objectconstruction.testgeneration.testcase.ocgexample.recursivecase;

public class LinkedListNode {
	private final int value;
	private LinkedListNode next;
	
	public LinkedListNode(int value) {
		this.value = value;
	}
	
	public void setNext(LinkedListNode next) {
		this.next = next;
	}
	
	public LinkedListNode getNext() {
		return next;
	}
	
	public int getValue() {
		return value;
	}
	
	public void method() {
		if (next.value != 0) {
			System.currentTimeMillis();
		}
	}
}
