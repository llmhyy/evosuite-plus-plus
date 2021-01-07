package org.evosuite.graphs.interprocedural.interestednode;

import org.evosuite.graphs.interprocedural.DepVariable;

public class EmptyInterestedNodeFilter implements IInterestedNodeFilter {

	@Override
	public boolean isInterested(DepVariable node) {
		return false;
	}

}
