package org.evosuite.graphs.interprocedural;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.graphs.cfg.BytecodeInstruction;

/**
 * each computation path starts with a method input, ends with an operand
 * @author Yun Lin
 *
 */
public class ComputationPath {
	private double score = 0;
	private List<BytecodeInstruction> computationNodes;

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public List<BytecodeInstruction> getComputationNodes() {
		return computationNodes;
	}

	public void setComputationNodes(List<BytecodeInstruction> computationNodes) {
		this.computationNodes = computationNodes;
	}

	public boolean isFastChannel(List<BytecodeInstruction> operands) {
		
		double value = 1;
		
		for(BytecodeInstruction ins: computationNodes) {
			/**
			 * conq is between (0, 1).
			 */
			double conq = evaluateConsequence(ins, operands);
			value = value * conq;
		}
		
		return value >= 0.8;
		
		// TODO Cheng Yan
//		if(this.score < 5)
//			return true;
//		return false;
	}

	private double evaluateConsequence(BytecodeInstruction ins, List<BytecodeInstruction> operands) {
		
		if(operands.contains(ins)) {
			return 1;
		}
		
		// TODO Cheng Yan, need to evaluate all the possbilities of Java bytecode instructions.
		switch(ins.getInstructionType()) {
		case "ALOAD":
			return 1;
		case "ASTORE":
			return 1;
		}
		
		return 0;
	}

	public boolean isConstant() {
		// TODO Cheng Yan
		for(int i = 0; i < this.computationNodes.size();i++) {
			System.out.print(this.computationNodes.get(i).getInstructionType());
			if(this.computationNodes.get(i).getInstructionType().equals("LDC"))
				return true;
		}
		return false;
	}
	
	public static List<ComputationPath> computePath(DepVariable root, List<BytecodeInstruction> operands) {
		// TODO Cheng Yan
		List<ComputationPath> computationPath = new ArrayList<>();
		List<BytecodeInstruction> nodes = new ArrayList<>(); 
		//traverse input to oprands
		nodes.add(root.getInstruction());
		dfsRoot(root,operands,computationPath,nodes);
		return computationPath;
		
//		return null;
	}

	private static void dfsRoot(DepVariable root, List<BytecodeInstruction> operands,
			List<ComputationPath> computationPath,List<BytecodeInstruction> nodes) {
		DepVariable node = root;
		Boolean isVisted = true;
		while(node.getRelations()[0] != null && isVisted) {
			for(int i = 0;i < node.getRelations().length;i++) {
				List<DepVariable> nodeList = node.getRelations()[i];
				if(nodeList == null) {
					isVisted = false;
					break;
				}
				node = nodeList.get(0);
				if(!operands.contains(node.getInstruction())) {
					nodes.add(node.getInstruction());
					dfsRoot(node,operands,computationPath,nodes);
				}
				else {
					nodes.add(node.getInstruction());
					break;
				}
			}
		}
		
		if(operands.contains(nodes.get(nodes.size() - 1))) {
			List<BytecodeInstruction> computationNodes = new ArrayList<>();
			for(int i = 0; i< nodes.size();i++) {
				computationNodes.add(nodes.get(i));
			}
			ComputationPath pathRecord = new ComputationPath();
			pathRecord.setComputationNodes(computationNodes);
			pathRecord.setScore(computationNodes.size());
			computationPath.add(pathRecord);
		}
		nodes.remove(nodes.size() - 1);
	}

}
