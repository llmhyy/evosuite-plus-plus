package org.evosuite.coverage.fbranch;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.setup.Call;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

public class FlagEffectChecker {
	public static FlagEffectResult checkFlagEffect(BranchCoverageGoal goal) {
		Branch branch = goal.getBranch();
		BytecodeInstruction ins = branch.getInstruction();
		
		int numberOfOperands = getOperands(ins);
		
		if(numberOfOperands == 1) {
			BytecodeInstruction defIns = ins.getSourceOfStackInstruction(0);
			if(defIns.isMethodCall()) {
				return checkFlagEffect(defIns);
			}
		}
		else if(numberOfOperands == 2){
			BytecodeInstruction defIns1 = ins.getSourceOfStackInstruction(1);
			BytecodeInstruction defIns2 = ins.getSourceOfStackInstruction(2);
			
			if(defIns1.isMethodCall()) {
				FlagEffectResult r = checkFlagEffect(defIns1);
				if(r.isInterproceduralFlag) {
					if(defIns2.isConstant() || defIns2.isLoadConstant()) {
						return r;						
					}
				}
			}
			
			if(defIns2.isMethodCall()) {
				FlagEffectResult r = checkFlagEffect(defIns2);
				if(r.isInterproceduralFlag) {
					if(defIns1.isConstant() || defIns1.isLoadConstant()) {
						return r;						
					}
				}
			}
		}
		
		return new FlagEffectResult(ins, false, null);
	}

	public static FlagEffectResult checkFlagEffect(BytecodeInstruction defIns) {
		RawControlFlowGraph calledGraph = defIns.getCalledCFG();
//		String signature = defIns.getCalledMethodsClass() + "." + defIns.getCalledMethod();
		if (calledGraph == null) {
			return new FlagEffectResult(defIns, false, null);
		}
		
		for(BytecodeInstruction exit: defIns.getActualCFG().getExitPoints()) {
			BytecodeInstruction returnDef = exit.getSourceOfStackInstruction(0);
			if(returnDef.isConstant() || returnDef.isLoadConstant()) {
				
				Call callInfo = new Call(defIns.getClassName(), defIns.getMethodName(), 
						defIns.getInstructionId());
				FlagEffectResult result = new FlagEffectResult(defIns, true, callInfo);
				return result;
			}
		}
		
		
		return new FlagEffectResult(defIns, false, null);
	}

	private static int getOperands(BytecodeInstruction ins) {
		AbstractInsnNode node = ins.getASMNode();
		if(node instanceof JumpInsnNode) {
			JumpInsnNode jNode = (JumpInsnNode)node;
			if(jNode.getOpcode() == Opcodes.IFEQ ||
					jNode.getOpcode() == Opcodes.IFGE ||
					jNode.getOpcode() == Opcodes.IFGT ||
					jNode.getOpcode() == Opcodes.IFLE ||
					jNode.getOpcode() == Opcodes.IFNE ||
					jNode.getOpcode() == Opcodes.IFNONNULL ||
					jNode.getOpcode() == Opcodes.IFNULL) {
				return 1;
			}
			else {
				return 2;
			}
		}
		return 0;
	}
}
