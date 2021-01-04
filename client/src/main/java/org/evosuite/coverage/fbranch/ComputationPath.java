package org.evosuite.coverage.fbranch;

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

	public boolean isFastChannel() {
		// TODO Cheng Yan
		return false;
	}

	public boolean isConstant() {
		// TODO Cheng Yan
		return false;
	}

}
