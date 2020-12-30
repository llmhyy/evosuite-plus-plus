package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.dataflow.Dataflow;
import org.evosuite.graphs.dataflow.DefUseAnalyzer;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;

public class StatisticBranchFlagEvaluator {
	public static double evaluate(Branch b) {
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> rootVariables = branchesInTargetMethod.get(b);
		
		List<BytecodeInstruction> operands = retrieveOperands(b);
		
		List<ComputationPath> pathList = new ArrayList<>();
		for(DepVariable root: rootVariables) {
			List<ComputationPath> computationPathList = computePath(root, operands);
			ComputationPath bestPath = findPathWithLeastEntropyLoss(computationPathList);
			if(bestPath != null) {
				pathList.add(bestPath);				
			}
		}
		
		double sum = 0;
		for(ComputationPath p: pathList) {
			sum += p.getScore();
		}
		
		if(sum == 0) {
			return 0;
		}
		
		return sum/pathList.size();
	}
	
	@SuppressWarnings("rawtypes")
	private static List<BytecodeInstruction> retrieveOperands(Branch b) {
		List<BytecodeInstruction> operands = new ArrayList<>();
		Frame frame = b.getInstruction().getFrame();
		
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(b.getClassName(), b.getMethodName());
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		MethodNode node = DefUseAnalyzer.getMethodNode(classLoader, className, methodName);
		
		for (int i = 0; i < b.getInstruction().getOperandNum(); i++) {
			int index = frame.getStackSize() - i - 1;
			Value val = frame.getStack(index);
			
			if (val instanceof SourceValue) {
				SourceValue srcValue = (SourceValue) val;
				/**
				 * get all the instruction defining the value.
				 */
				for(AbstractInsnNode insNode: srcValue.insns) {
					BytecodeInstruction defIns = DefUseAnalyzer.convert2BytecodeInstruction(cfg, node, insNode);
					if (defIns != null) {
						operands.add(defIns);
					}
				}
			}
		}
		return operands;
	}

	private static ComputationPath findPathWithLeastEntropyLoss(List<ComputationPath> computationPathList) {
		// TODO Auto-generated method stub
		return null;
	}

	private static List<ComputationPath> computePath(DepVariable root, List<BytecodeInstruction> operands) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
