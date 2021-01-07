package org.evosuite.graphs.interprocedural.interestednode;

import org.evosuite.graphs.interprocedural.DepVariable;

/**
 * 
 * interested ndoe for analyzing gradients of computation graph.
 * 
 * @author Yun Lin
 *
 */
public class GradientGraphInterestedNodeFilter implements IInterestedNodeFilter {

	@Override
	public boolean isInterested(DepVariable node) {
		return node.isMethodInput();
	}

}
