package org.evosuite.graphs.interprocedural.interestednode;

import org.evosuite.graphs.interprocedural.DepVariable;

/**
 * interested node for smart seed analysis
 * 
 * @author Yun Lin
 *
 */
public class SmartSeedInterestedNodeFilter implements IInterestedNodeFilter {

	@Override
	public boolean isInterested(DepVariable node) {
		return node.isMethodInput() || node.isConstant();
	}

}
