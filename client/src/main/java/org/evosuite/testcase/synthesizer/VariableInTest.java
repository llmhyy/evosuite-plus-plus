package org.evosuite.testcase.synthesizer;

import java.util.List;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.variable.VariableReference;

/**
 * This class describes to cover a node in the object construction graph, 
 * (1) which caller object, <code>var</code>, in the test case should be used and
 * (2) what other nodes should be covered when constructing statements based on <code>var</code>.  
 * 
 * Some node cannot correspond to any variable in test source code, but their children can.
 * For example, arraylist.elementData[i] has a path like: arraylist -> elementData => [].
 * the elementData in arraylist cannot be accessed, but we still need to find the correspondence
 * between arraylist.elementData[i] and some variables in the source code.
 * 
 * 
 * @author Yun Lin
 *
 */
public class VariableInTest {
	/**
	 * the variable to construct the statement of a node
	 */
	public VariableReference callerObject;
	/**
	 * a list of nodes in the graph should be covered by constructing a statement. Its size will be always larger than
	 * or equal to 1.
	 * 
	 * the last node is the original node.
	 */
	public List<DepVariableWrapper> nodePath;
	
	public VariableInTest(VariableReference variable, List<DepVariableWrapper> nodePath) {
		super();
		this.callerObject = variable;
		this.nodePath = nodePath;
	}
	
	public DepVariableWrapper getNode() {
		return nodePath.get(0);
	}

	/**
	 * check whether we need to cover its parent nodes when visiting this node
	 * @return
	 */
	public boolean isDirentNodeAccess() {
		return nodePath.size() == 1;
	}
}
