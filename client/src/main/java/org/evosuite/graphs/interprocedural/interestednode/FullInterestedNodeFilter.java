package org.evosuite.graphs.interprocedural.interestednode;

import org.evosuite.graphs.interprocedural.var.DepVariable;

/**
 * interested node for smart seed analysis
 * 
 * @author Yun Lin
 *
 */
public class FullInterestedNodeFilter implements IInterestedNodeFilter {

	@Override
	public boolean isInterested(DepVariable node) {
		return true;
	}

}