package org.evosuite.testcase.synthesizer;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

public class NodePair {
	private DepVariableWrapper first;
	private DepVariableWrapper second;
	
	public NodePair(DepVariableWrapper first, DepVariableWrapper second) {
		if (first == null || second == null) {
			throw new IllegalArgumentException("Nodes cannot be null!");
		}
		
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NodePair)) {
			return false;
		}
		
		NodePair other = (NodePair) o;
		return this.first.equals(other.first) && this.second.equals(other.second);
	}
	
	@Override
	public int hashCode() {
		return first.hashCode() + 31 * second.hashCode();
	}
	
	public DepVariableWrapper getFirst() {
		return first;
	}
	
	public DepVariableWrapper getSecond() {
		return second;
	}
}
