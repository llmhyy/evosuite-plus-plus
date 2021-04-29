package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
	public static Map<Integer, Double> entropyMap;
	static {
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		// High entropy loss
		map.put(Opcodes.DCMPG, 0.5);
		map.put(Opcodes.DCMPL, 0.5);
		map.put(Opcodes.FCMPG, 0.5);
		map.put(Opcodes.FCMPL, 0.5);
		map.put(Opcodes.LCMP, 0.5);
		map.put(Opcodes.INSTANCEOF, 0.5);

		// Mid entropy loss
		map.put(Opcodes.DDIV, 0.7);
		map.put(Opcodes.DREM, 0.7);
		map.put(Opcodes.FDIV, 0.7);
		map.put(Opcodes.FREM, 0.7);
		map.put(Opcodes.IDIV, 0.7);
		map.put(Opcodes.IREM, 0.7);
		map.put(Opcodes.ISHR, 0.7);
		map.put(Opcodes.IUSHR, 0.7);
		map.put(Opcodes.LDIV, 0.7);
		map.put(Opcodes.LREM, 0.7);
		map.put(Opcodes.LSHR, 0.7);
		map.put(Opcodes.LUSHR, 0.7);

		// Low entropy loss
		map.put(Opcodes.D2F, 0.9);
		map.put(Opcodes.D2I, 0.9);
		map.put(Opcodes.D2L, 0.9);
		map.put(Opcodes.F2I, 0.9);
		map.put(Opcodes.F2L, 0.9);
		map.put(Opcodes.I2B, 0.9);
		map.put(Opcodes.I2S, 0.9);
		map.put(Opcodes.L2I, 0.9);

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

		// FIXME Aaron, the root variables can contain multiple instructions pointing to
		// the same parameters
		// we shall remove the duplication then.

		// Remove duplicate parameter instructions
		rootVariables = removeDuplicateVariables(rootVariables);

		// Compute paths
		List<BytecodeInstruction> operands = b.getInstruction().getOperands();
		List<ComputationPath> pathList = new ArrayList<>();
		for (DepVariable root : rootVariables) {
			if (!root.isParameter()) {
				continue;
			}

			List<ComputationPath> computationPathList = ComputationPath.computePath(root, b);
			ComputationPath bestPath = findPathWithMostEntropyLoss(computationPathList);
			if (bestPath != null) {
				pathList.add(bestPath);
			}
		}

		// Get average entropy loss score
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
		// Most entropy loss == smallest score
		double minScore = 1.1;
		ComputationPath bestPath = null;

		for (ComputationPath path : computationPathList) {
			computePathScore(path);
			if (path.getScore() < minScore) {
				bestPath = path;
			}
		}

		return bestPath;
	}

	private static void computePathScore(ComputationPath path) {
//		List<DepVariable> nodes = path.getComputationNodes();
//		double pathScore = 0;
//		for (DepVariable node : nodes) {
//			BytecodeInstruction ins = node.getInstruction();
//			pathScore += entropyMap.getOrDefault(ins.getASMNode().getOpcode(), 0.0);
//		}
		List<DepVariable> nodes = path.getComputationNodes();
		double pathScore = 1.0;
		for (DepVariable node : nodes) {
			pathScore *= entropyMap.getOrDefault(node.getInstruction().getASMNode().getOpcode(), 1.0);
		}
		path.setScore(pathScore);
	}

	private static Set<DepVariable> removeDuplicateVariables(Set<DepVariable> rootVariables) {
		Map<String, DepVariable> smallestSoFar = new HashMap<String, DepVariable>();

		for (DepVariable root : rootVariables) {
			String varName = root.getInstruction().getVariableName();

			// store {varName : DepVariable} if varName is not in or if stored DepVariable
			// has bigger ID
			if (!smallestSoFar.containsKey(varName) || smallestSoFar.get(varName).getInstruction()
					.getInstructionId() > root.getInstruction().getInstructionId()) {
				smallestSoFar.put(varName, root);
			}
		}

		return new HashSet<DepVariable>(smallestSoFar.values());
	}

}
