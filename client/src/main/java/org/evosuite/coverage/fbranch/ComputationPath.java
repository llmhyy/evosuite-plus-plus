package org.evosuite.coverage.fbranch;

import java.util.List;

import org.evosuite.graphs.cfg.BytecodeInstruction;

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

}
