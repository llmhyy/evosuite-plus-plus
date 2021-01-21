package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.objectweb.asm.Opcodes;

public class StatisticBranchFlagEvaluator {
	// Opcode : Score
	private static Map<Integer, Double> entropyMap;
	static {
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		// High entropy loss
		map.put(Opcodes.DCMPG, 3.0);
		map.put(Opcodes.DCMPL, 3.0);
		map.put(Opcodes.FCMPG, 3.0);
		map.put(Opcodes.FCMPL, 3.0);
		map.put(Opcodes.LCMP, 3.0);
		map.put(Opcodes.INSTANCEOF, 3.0);

		// Mid entropy loss
		map.put(Opcodes.DDIV, 2.0);
		map.put(Opcodes.DREM, 2.0);
		map.put(Opcodes.FDIV, 2.0);
		map.put(Opcodes.FREM, 2.0);
		map.put(Opcodes.IDIV, 2.0);
		map.put(Opcodes.IREM, 2.0);
		map.put(Opcodes.ISHR, 2.0);
		map.put(Opcodes.IUSHR, 2.0);
		map.put(Opcodes.LDIV, 2.0);
		map.put(Opcodes.LREM, 2.0);
		map.put(Opcodes.LSHR, 2.0);
		map.put(Opcodes.LUSHR, 2.0);

		// Low entropy loss
		map.put(Opcodes.D2F, 1.0);
		map.put(Opcodes.D2I, 1.0);
		map.put(Opcodes.D2L, 1.0);
		map.put(Opcodes.F2I, 1.0);
		map.put(Opcodes.F2L, 1.0);
		map.put(Opcodes.I2B, 1.0);
		map.put(Opcodes.I2S, 1.0);
		map.put(Opcodes.L2I, 1.0);

		entropyMap = Collections.unmodifiableMap(map);
	}

	public static double evaluate(Branch b) {
		// Get instructions for debugging
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		BytecodeInstructionPool insPool = BytecodeInstructionPool.getInstance(classLoader);
		List<BytecodeInstruction> instructions = insPool.getAllInstructionsAtMethod(b.getClassName(),
				b.getMethodName());
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(b.getClassName(),
				b.getMethodName());

		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		Set<DepVariable> rootVariables = branchesInTargetMethod.get(b);
		
		//FIXME Aaron, the root variables can contain multiple instructions pointing to the same parameters
		// we shall remove the duplication then.

		List<BytecodeInstruction> operands = b.getInstruction().getOperands();

		List<ComputationPath> pathList = new ArrayList<>();
		for (DepVariable root : rootVariables) {
			if (!root.isParameter()) {
				continue;
			}

			List<ComputationPath> computationPathList = ComputationPath.computePath(root, operands);
			ComputationPath bestPath = findPathWithMostEntropyLoss(computationPathList);
			if (bestPath != null) {
				pathList.add(bestPath);
			}
		}

		double sum = 0;
		for (ComputationPath p : pathList) {
			sum += p.getScore();
		}

		if (sum == 0) {
			return 0;
		}

		return sum / pathList.size();
	}

	private static ComputationPath findPathWithMostEntropyLoss(List<ComputationPath> computationPathList) {
		double maxScore = -1.0;
		ComputationPath bestPath = null;

		for (ComputationPath path : computationPathList) {
			computePathScore(path);
			if (path.getScore() > maxScore) {
				bestPath = path;
			}
		}

		return bestPath;
	}

	private static void computePathScore(ComputationPath path) {
		List<BytecodeInstruction> nodes = path.getComputationNodes();
		double pathScore = 0;
		for (BytecodeInstruction node : nodes) {
			pathScore += entropyMap.getOrDefault(node.getASMNode().getOpcode(), 0.0);
		}
		path.setScore(pathScore);
	}

}
