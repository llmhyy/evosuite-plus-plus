package org.evosuite.graphs.interprocedural.interestednode;

import org.evosuite.graphs.interprocedural.var.DepVariable;

/**
 * The interested-node filter for OCG.
 * 
 * @author Yun Lin
 *
 */
public class OCGInterestedNodeFilter implements IInterestedNodeFilter {

	@Override
	public boolean isInterested(DepVariable node) {
		return node.isStateVariable();
	}

}
