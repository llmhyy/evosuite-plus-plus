package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.cfg.ControlDependency;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

/**
 * This filter applies to branched methods also satisfying following rules: 
 * 1.All parameters are primitive 
 * 2. IF branch only depends on primitive type field 
 * 3. If the branch depends on a method call, the method call needs to be:
 * 		1.All parameters are primitive 
 * 		2.Return type is primitive
 * 		3.Return value only depends on primitive type
 */
public class PrimitiveBasedFilter extends MethodFlagCondFilter {

	public static final String excelProfileSubfix = "_primitiveMethodProfiles.xlsx";
	private static Logger log = LoggerUtils.getLogger(PrimitiveBasedFilter.class);
	private ExcelWriter writer;

	public PrimitiveBasedFilter() {
		String statisticFile = new StringBuilder(Settings.getReportFolder()).append(File.separator)
				.append(EvosuiteForMethod.projectId).append(excelProfileSubfix).toString();
		File newFile = new File(statisticFile);
		if (newFile.exists()) {
			newFile.delete();
		}
		writer = new ExcelWriter(new File(statisticFile));
		writer.getSheet("data",
				new String[] { "ProjectId", "ProjectName", "Target Method", "Flag Method", "branch", "const0/1",
						"branch", "getfield", "branch", "iLoad", "branch", "invokemethod", "other", "Remarks",
						"has Primitve type", "hasPrimitiveComparison", "isValid" },
				0);
	}

	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
		log.debug(String.format("#Method %s#%s", className, methodName));

		boolean hasIfBranch = false;
		// Get actual CFG for target method
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		if (cfg == null) {
			BytecodeInstructionPool.getInstance(classLoader).registerMethodNode(node, className, node.name + node.desc);
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		}

		if (FilterHelper.containNonPrimitiveParameter(node.desc)) {
			return false;
		}

		for (Branch b : BranchPool.getInstance(classLoader).retrieveBranchesInMethod(className, methodName)) {
			if (b.isSwitchCaseBranch()) {
				continue;
			}

			if (!b.isInstrumented()) {
				hasIfBranch = true;
				if (!checkInsnUseAllPrimitiveOperands(b.getInstruction())) {
					return false;
				}
			}
		}
		return true && hasIfBranch;

	}

	private boolean checkInsnUseAllPrimitiveOperands(BytecodeInstruction insn) {
		int numberOfOperands = insn.getOperandNum();

		// IF condition
		// Only 1 value on the stack is used
		if (numberOfOperands == 1) {
			List<BytecodeInstruction> list = insn.getSourceOfStackInstructionList(0);

			boolean noUseOfObject = checkNoUseOfObject(list, insn);
			if (noUseOfObject) {
				return true;
			}
		}
		// 2 values on the stack will be used
		else if (numberOfOperands == 2) {
			List<BytecodeInstruction> list0 = insn.getSourceOfStackInstructionList(0);
			boolean noUseOfObject1 = checkNoUseOfObject(list0, insn);

			List<BytecodeInstruction> list1 = insn.getSourceOfStackInstructionList(1);
			boolean noUseOfObject2 = checkNoUseOfObject(list1, insn);

			if (noUseOfObject1 && noUseOfObject2) {
				return true;
			}
		} else {
			System.currentTimeMillis();
		}
		return false;
	}

	private boolean checkNoUseOfObject(List<BytecodeInstruction> list, BytecodeInstruction stackParent) {
		boolean valid = true;

		for (BytecodeInstruction defIns : list) {

			// ALOAD 0
			// this is to distinguish field and method
//			if (defIns.loadsReferenceToThis()) {
//				if (stackParent.isMethodCall()) {
//					continue;
//				} if (stackParent.getASMNode() instanceof FieldInsnNode) {
//					FieldInsnNode fieldNode = ((FieldInsnNode) stackParent.getASMNode());
//					String desc = fieldNode.desc;
//					if (desc.length() == 1 || desc.equals("Ljava/lang/String"))
//					System.currentTimeMillis();
//				} 
//			} else if (isParameter(defIns)) {
//				continue;
//			} 
			if (defIns.getASMNode().getOpcode() == Opcodes.GETSTATIC
					|| defIns.getASMNode().getOpcode() == Opcodes.GETFIELD) {
				FieldInsnNode fieldNode = ((FieldInsnNode) defIns.getASMNode());
				String desc = fieldNode.desc;
				if (desc.length() != 1 || !desc.equals("Ljava/lang/String")) {
					if (desc.contains("[")) {
						String element = desc.substring(desc.indexOf("[") + 1);
						if (element.length() != 1 && !element.equals("Ljava/lang/String;")) {
							return false;
						}
						System.currentTimeMillis();
					} else {
						return false;
					}
				}
			} else if (defIns.isArrayLoadInstruction()) {
				return false;
			} else if (defIns.getASMNode().getOpcode() == Opcodes.ALOAD && !isParameter(defIns)) {
				return false;
			} else if (defIns.isMethodCall()) {
				if (defIns.getASMNode().getOpcode() == Opcodes.INVOKEINTERFACE) {
					return false;
				}

				if (defIns.getCalledCFG() == null) {
					return false;
				}
				String calledName = defIns.getCalledCFG().getMethodName();
				String calledDesc = calledName.substring(calledName.indexOf("("));
				Type returnType = Type.getReturnType(calledDesc);
				if (!considerAsPrimitiveType(returnType)) {
					return false;
				}
				MethodInsnNode methodInsnNode = (MethodInsnNode) (defIns.getASMNode());
				if (FilterHelper.containNonPrimitiveParameter(methodInsnNode.desc)) {
					return false;
				}
				
				if (methodInsnNode.owner.equals("java/lang/String")) {
					continue;
				}
				if (!checkReturnDependOnBranchUsingAllPrimitiveOperands(defIns.getCalledCFG().getClassName(), defIns.getCalledCFG().determineExitPoints())) {
					return false;
				};
			}

			if (defIns.getOperandNum() > 0) {
				valid = checkInsnUseAllPrimitiveOperands(defIns);
				if (!valid) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isParameter(BytecodeInstruction insn) {
		if (insn.isLocalVariableUse()) {
			String methodName = insn.getRawCFG().getMethodName();
			String methodDesc = methodName.substring(methodName.indexOf("("), methodName.length());
			Type[] typeArgs = Type.getArgumentTypes(methodDesc);
			int paramNum = typeArgs.length;

			int slot = insn.getLocalVariableSlot();

			if (insn.getRawCFG().isStaticMethod()) {
				return slot < paramNum;
			} else {
				return slot < paramNum + 1 && slot != 0;
			}
		}
		return false;
	}

	private boolean considerAsPrimitiveType(Type type) {
		if (type.getSort() == Type.ARRAY) {
			return checkIsPrimitiveType(type.getElementType());
		}
		return checkIsPrimitiveType(type);
	}

	private boolean checkIsPrimitiveType(Type type) {
		return type == Type.BOOLEAN_TYPE || type == Type.BYTE_TYPE || type == Type.CHAR_TYPE || type == Type.SHORT_TYPE
				|| type == Type.INT_TYPE || type == Type.LONG_TYPE || type == Type.FLOAT_TYPE
				|| type == Type.DOUBLE_TYPE || type.getClassName().equals("java.lang.String");
	}
	
	private boolean checkReturnDependOnBranchUsingAllPrimitiveOperands(String className,
			Set<BytecodeInstruction> exitPoints) {
		for (BytecodeInstruction exit : exitPoints) {
			if (exit.isReturn()) {
				List<BytecodeInstruction> sourceList = exit.getSourceOfStackInstructionList(0);
				for (BytecodeInstruction source : sourceList) {
					for (ControlDependency cd : source.getControlDependencies()) {
						Branch b = cd.getBranch();
						if (!checkInsnUseAllPrimitiveOperands(b.getInstruction())) {
							return false;
						}
					};
				}
			}
		}

		return false;
	}
}
