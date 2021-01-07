package org.evosuite.graphs.interprocedural.interestednode;

import org.evosuite.graphs.interprocedural.DepVariable;

/**
 * 
 * This interface is used for us to build interprocedural analysis, i.e., building a PDG with
 * specific criteria. When traversing through the PDG starting from the target method, we would
 * like to know the instructions (i.e., nodes in the graph) we interested.
 * 
 * For example, 
 * (1) for building object construction graph, we collect the state-variable relevant to
 * the target branch. <br>
 * (2) for analyzing the methods with structure enabling smart seed, we collect the method inputs 
 * and constants. <br>
 * (3) for analyzing the methods of disappearing gradient, we just collect the method inputs. <br>
 * 
 * @author Yun Lin
 *
 */
public interface IInterestedNodeFilter {
	public boolean isInterested(DepVariable node);
}
