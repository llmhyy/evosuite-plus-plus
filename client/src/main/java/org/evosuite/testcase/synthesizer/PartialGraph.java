package org.evosuite.testcase.synthesizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapperFactory;

public class PartialGraph {
	Map<DepVariableWrapper, DepVariableWrapper> allRelevantNodes = new HashMap<DepVariableWrapper, DepVariableWrapper>();
	
	public int getGraphSize() {
		return allRelevantNodes.keySet().size();
	}
	
	/**
	 * In the original computation graph, multiple node can represent the same variable.
	 * Therefore, we need to merge those nodes when generating the partial graph. This method
	 * works by tracking the nodes by hash (so the graph only ever has one node with that hash)
	 * and merging the nodes with the same hash. Merging two nodes means that we combine their 
	 * relations and reverse relations.
	 * 
	 * @param var The variable to merge into the graph.
	 * @return The merged node.
	 */
	public DepVariableWrapper fetchAndMerge(DepVariable var) {
		DepVariableWrapper newNode = DepVariableWrapperFactory.createWrapperInstance(var);
		DepVariableWrapper nodeInGraph = allRelevantNodes.get(newNode);
		
		if (nodeInGraph == null) {
			nodeInGraph = new DepVariableWrapper(var);
			allRelevantNodes.put(newNode, newNode);
			return nodeInGraph;
		}
		
		// Else, nodeInGraph isn't null
		// So there is some node in the graph matching that hash
		// We merge our new node (constructed from the input var) into
		// the node in the graph
		List<DepVariable>[] nodeInGraphRelations = nodeInGraph.var.getRelations();
		List<DepVariable>[] newNodeRelations = var.getRelations();
		for (int i = 0; i < nodeInGraphRelations.length; i++) {
			List<DepVariable> nodeInGraphRelation = nodeInGraphRelations[i];
			List<DepVariable> newNodeRelation = newNodeRelations[i];
			
			// No relations to merge in.
			if (newNodeRelation == null) {
				continue;
			}
			
			if (nodeInGraphRelation == null) {
				nodeInGraphRelation = new ArrayList<>();
			}
			
			// Merge in all the dependent variables
			for (DepVariable v : newNodeRelation) {
				if (!nodeInGraphRelation.contains(v)) {
					nodeInGraphRelation.add(v);
				}
			}
			
			// In the event that the original wrapperVarRelation was null
			// We need to store it back.
			nodeInGraphRelations[i] = nodeInGraphRelation;
		}
		
		List<DepVariable>[] nodeInGraphReverseRelations = nodeInGraph.var.getReverseRelations();
		List<DepVariable>[] newNodeReverseRelations = var.getReverseRelations();
		for (int i = 0; i < nodeInGraphReverseRelations.length; i++){
			List<DepVariable> nodeInGraphReverseRelation = nodeInGraphReverseRelations[i];
			List<DepVariable> newNodeReverseRelation = newNodeReverseRelations[i];
			
			// Nothing to merge.
			if (newNodeReverseRelation == null) {
				continue;
			}
			
			if (nodeInGraphReverseRelation == null) {
				nodeInGraphReverseRelation = new ArrayList<>();
			}
			
			for (DepVariable v : newNodeReverseRelation) {
				if (!nodeInGraphReverseRelation.contains(v)) {
					nodeInGraphReverseRelation.add(v);
				}
			}
			
			// In the event that the original wrapperVarReverseRelation was null
			// We need to store it back in.
			nodeInGraphReverseRelations[i] = nodeInGraphReverseRelation;
		}
		return nodeInGraph;
	}
	
	public List<DepVariableWrapper> getTopLayer(){
		List<DepVariableWrapper> list = new ArrayList<DepVariableWrapper>();
		for(DepVariableWrapper node: allRelevantNodes.values()) {
			if(node.parents.isEmpty()) {
				list.add(node);
			}
		}
		
		return list;
	}

	private Branch targetBranch;
	
	public void setBranch(Branch b) {
		this.targetBranch = b;
	}
	
	public Branch getBranch() {
		return this.targetBranch;
	}
}
