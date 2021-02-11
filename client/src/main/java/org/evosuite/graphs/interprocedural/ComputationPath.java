package org.evosuite.graphs.interprocedural;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;

import ch.qos.logback.classic.Logger;

/**
 * each computation path starts with a method input, ends with an operand
 * @author Yun Lin
 *
 */
public class ComputationPath {
	private double score = 0;
	private List<DepVariable> computationNodes;

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public List<DepVariable> getComputationNodes() {
		return computationNodes;
	}

	public void setComputationNodes(List<DepVariable> computationNodes) {
		this.computationNodes = computationNodes;
	}

	/**
	 * (1) the start node should be a method input/parameter.
	 * (2) the path from the start node to the brand operand is "simple".
	 * @param operands
	 * @return
	 */
	public boolean isFastChannel(List<BytecodeInstruction> operands) {
		boolean isParameter = isStartWithMethodInput(this);
		if(!isParameter) return false;
		
		double value = 1;
		
		DepVariable prevNode = null;
		for(DepVariable node: computationNodes) {
//			BytecodeInstruction ins = node.getInstruction();
			/**
			 * conq is between (0, 1).
			 */
			double conq = evaluateConsequence(node, prevNode, operands);
			value = value * conq;
			
			prevNode = node;
		}
		
//		if(!(this.getComputationNodes().get(0).isParameter() 
//				|| this.getComputationNodes().get(0).getName().contains("LOAD")))
//			value = value * 0.6;
		return value > 0.6;
				
		
	}

	public static boolean isStartWithMethodInput(ComputationPath computationPath) {
		if(computationPath.getComputationNodes().isEmpty()) {
			return false;
		}
		
		DepVariable node = computationPath.getComputationNodes().get(0);
		if(node.isParameter()) {
			return true;
		}
		else if(node.isInstaceField()) {
			String className = node.getInstruction().getClassName();
			if(className.equals(Properties.TARGET_CLASS)) {
				return true;
			}
		}
		
		return false;
	}

	private double evaluateConsequence(DepVariable node, DepVariable prevNode, List<BytecodeInstruction> operands) {
		
		BytecodeInstruction ins = node.getInstruction();
		
		if(operands.contains(ins)) {
			return 1;
		}
		
		// TODO Cheng Yan, need to evaluate all the possbilities of Java bytecode instructions.
		switch(ins.getInstructionType()) {
		case "ALOAD":
			return 1;
		case "ASTORE":
			return 1;
		case "AALOAD":
			return 1;
		case "AASTORE":
			return 1;
		case "ACONST_NULL":
			return 1;
		case "ALOAD_0":
			return 1;
		case "ALOAD_1":
			return 1;
		case "ALOAD_2":
			return 1;
		case "ALOAD_3":
			return 1;
		case "ANEWARRAY":
			return 0.9;
		case "ARETURN":
			return 1;
		case "ARRAYLENGTH":
			return 0.9;
		case "ASTORE_0":
			return 1;
		case "ASTORE_1":
			return 1;
		case "ASTORE_2":
			return 1;
		case "ASTORE_3":
			return 1;
		case "ATHROW":
			return 0.8;
		case "BLOAD":
			return 1;
		case "BSTORE":
			return 1;
		case "BIPUSH":
			return 1;
		case "BREAKPOINT":
			return 0.1;
		case "CALOAD":
			return 1;
		case "CASTORE":
			return 1;
		case "CHECKCAST":
			return 0.9;
		case "D2F":
			return 0.9;
		case "D2I":
			return 0.9;
		case "D2L":
			return 1;
		case "DADD":
			return 0.9;
		case "DALOAD":
			return 1;
		case "DASTORE":
			return 1;
		case "DCMPG":
			return 0.7;
		case "DCMPL":
			return 0.7;
		case "DCONST_0":
			return 1;
		case "DCONST_1":
			return 1;
		case "DDIV":
			return 0.8;
		case "DLOAD":
			return 1;
		case "DLOAD_0":
			return 1;
		case "DLOAD_1":
			return 1;
		case "DLOAD_2":
			return 1;
		case "DLOAD_3":
			return 1;
		case "DMUL":
			return 0.8;
		case "DNEG":
			return 0.9;
		case "DREM":
			return 0.8;
		case "DRETURN":
			return 1;
		case "DSTORE":
			return 1;
		case "DSTORE_0":
			return 1;
		case "DSTORE_1":
			return 1;
		case "DSTORE_2":
			return 1;
		case "DSTORE_3":
			return 1;
		case "DSUB":
			return 0.9;
		case "DUP":
			return 1;
		case "DUP_X1":
			return 0.9;
		case "DUP_X2":
			return 0.9;
		case "DUP2":
			return 1;
		case "DUP2_X1":
			return 0.9;
		case "DUP2_X2":
			return 0.9;
		case "F2D":
			return 0.9;
		case "F2I":
			return 0.9;
		case "F2L":
			return 0.9;
		case "FADD":
			return 0.9;
		case "FALOAD":
			return 1;
		case "FASTORE":
			return 1;
		case "FCMPG":
			return 0.7;
		case "FCMPL":
			return 0.7;
		case "FCONST_0":
			return 1;
		case "FCONST_1":
			return 1;
		case "FCONST_2":
			return 1;
		case "FDIV":
			return 0.8;
		case "FLOAD":
			return 1;
		case "FLOAD_0":
			return 1;
		case "FLOAD_1":
			return 1;
		case "FLOAD_2":
			return 1;
		case "FLOAD_3":
			return 1;
		case "FMUL":
			return 0.8;
		case "FNEG":
			return 0.9;
		case "FREM":
			return 0.8;
		case "FRETURN":
			return 1;
		case "FSTORE":
			return 1;
		case "FSTORE_0":
			return 1;
		case "FSTORE_1":
			return 1;
		case "FSTORE_2":
			return 1;
		case "FSTORE_3":
			return 1;
		case "FSUB":
			return 0.9;
		case "GETFIELD":
			return 1;
		case "GETSTATIC":
			return 1;
		case "GOTO":
			return 0.9;
		case "GOTO_W":
			return 0.8;
		case "I2B":
			return 0.9;
		case "I2C":
			return 0.9;
		case "I2D":
			return 0.9;
		case "I2F":
			return 0.9;
		case "I2L":
			return 0.9;
		case "I2S":
			return 0.9;
		case "IADD":
			return 0.9;
		case "IALOAD":
			return 1;
		case "IAND":
			return 0.8;
		case "IASTORE":
			return 0.9;
		case "ICONST_M1":
			return 1;
		case "ICONST_0":
			return 1;
		case "ICONST_1":
			return 1;
		case "ICONST_2":
			return 1;
		case "ICONST_3":
			return 1;
		case "ICONST_4":
			return 1;
		case "ICONST_5":
			return 1;
		case "IDIV":
			return 0.8;
		case "IF_ACMPEQ":
			return 0.7;
		case "IF_ACMPNE":
			return 0.7;
		case "IF_ICMPEQ":
			return 0.7;
		case "IF_ICMPGE":
			return 0.7;
		case "IF_ICMPGT":
			return 0.7;
		case "IF_ICMPLE":
			return 0.7;
		case "IF_ICMPLT":
			return 0.7;
		case "IF_ICMPNE":
			return 0.7;
		case "IFEQ":
			return 0.7;
		case "IFGE":
			return 0.7;
		case "IFGT":
			return 0.7;
		case "IFLE":
			return 0.7;
		case "IFLT":
			return 0.7;
		case "IFNE":
			return 0.7;
		case "IFNONNULL":
			return 0.7;
		case "IFNULL":
			return 0.7;
		case "IINC":
			return 1;
		case "ILOAD":
			return 1;
		case "ILOAD_0":
			return 1;
		case "ILOAD_1":
			return 1;
		case "ILOAD_2":
			return 1;
		case "ILOAD_3":
			return 1;
		case "IMPDEP1":
			return 0.1;
		case "IMPDEP2":
			return 0.1;
		case "IMUL":
			return 0.8;
		case "INEG":
			return 0.8;
		case "INSTANCEOF":
			return 1;
		case "INVOKEDYNAMIC":
		case "INVOKEINTERFACE":
		case "INVOKESPECIAL":
		case "INVOKESTATIC":
		case "INVOKEVIRTUAL":
			double score = estimateMethodCall(node, prevNode);
			return score;
		case "IOR":
			return 0.8;
		case "IREM":
			return 0.8;
		case "IRETURN":
			return 1;
		case "ISHL":
			return 0.8;
		case "ISHR":
			return 0.8;
		case "ISTORE":
			return 1;
		case "ISTORE_0":
			return 1;
		case "ISTORE_1":
			return 1;
		case "ISTORE_2":
			return 1;
		case "ISTORE_3":
			return 1;
		case "ISUB":
			return 0.9;
		case "IUSHR":
			return 0.8;
		case "IXOR":
			return 0.8;
		case "JSR":
			return 0.8;
		case "JSR_W":
			return 0.7;
		case "L2D":
			return 0.9;
		case "L2F":
			return 0.9;
		case "L2I":
			return 0.9;
		case "LADD":
			return 0.9;
		case "LALOAD":
			return 1;
		case "LAND":
			return 0.8;
		case "LASTORE":
			return 1;
		case "LCMP":
			return 0.7;
		case "LCONST_0":
			return 1;
		case "LCONST_1":
			return 1;
		case "LDC":
			return 1;
		case "LDC_W":
			return 1;
		case "LDC2_W":
			return 1;
		case "LDIV":
			return 0.8;
		case "LLOAD":
			return 1;
		case "LLOAD_0":
			return 1;
		case "LLOAD_1":
			return 1;
		case "LLOAD_2":
			return 1;
		case "LLOAD_3":
			return 1;
		case "LMUL":
			return 0.8;
		case "LNEG":
			return 0.8;
		case "LOOKUPSWITCH":
			return 0.9;
		case "LOR":
			return 0.8;
		case "LREM":
			return 0.8;
		case "LRETURN":
			return 1;
		case "LSHL":
			return 0.8;
		case "LSHR":
			return 0.8;
		case "LSTORE":
			return 1;
		case "LSTORE_0":
			return 1;
		case "LSTORE_1":
			return 1;
		case "LSTORE_2":
			return 1;
		case "LSTORE_3":
			return 1;
		case "LSUB":
			return 0.9;
		case "LUSHR":
			return 0.8;
		case "LXOR":
			return 0.8;
		case "MONITORENTER":
			return 1;
		case "MONITOREXIT":
			return 1;
		case "MULTIANEWARRAY":
			return 0.9;
		case "NEW":
			return 0.9;
		case "NEWARRAY":
			return 0.9;
		case "NOP":
			return 1;
		case "POP":
			return 0.9;
		case "POP2":
			return 0.9;
		case "PUTFIELD":
			return 0.9;
		case "PUTSTATIC":
			return 0.9;
		case "RET":
			return 0.8;
		case "RETURN":
			return 1;
		case "SALOAD":
			return 1;
		case "SASTORE":
			return 1;
		case "SIPUSH":
			return 1;
		case "SWAP":
			return 1;
		case "TABLESWITCH":
			return 0.9;
		case "WIDE":
			return 1;	
		}
		
		return 0;
	}


	private double estimateMethodCall(DepVariable node, DepVariable prevNode) {
		//TODO
		
		if(prevNode == null) return 1;
		
		int inputOrder = node.getInputOrder(prevNode);
		if(inputOrder != 0) {
			BytecodeInstruction ins = node.getInstruction();
			if(ins.isInvokeStatic()) {
				String desc = ins.getMethodCallDescriptor();
			}
			else {
				inputOrder--;
				String desc = ins.getMethodCallDescriptor();
				String[] separateTypes = parseSignature(desc);
				String inputType = separateTypes[inputOrder];
				String outType = separateTypes[separateTypes.length-1];
				
				
				System.currentTimeMillis();
			}
		}
		
//		node.getRelations();
		
//		ActualControlFlowGraph graph = ins.getCalledActualCFG();
		System.err.println("DepVariable cannot locate its input");
		return 0.5;
	}

	private String[] parseSignature(String desc) {
		// TODO Auto-generated method stub
		String[] separateTypes = desc.split(";");
		for(int i = 0; i < separateTypes.length ;i++) {
			if(separateTypes[i].contains("(")) {
				separateTypes[i] = 
						separateTypes[i].substring(separateTypes[i].indexOf('(') + 1, separateTypes[i].length());
			}else if(separateTypes[i].contains(")")) {
				separateTypes[i] = 
						separateTypes[i].substring(separateTypes[i].indexOf(')') + 1, separateTypes[i].length());
			}
		}

		return separateTypes;
	}

	public boolean isHardConstant(List<BytecodeInstruction> operands) {
		// TODO Cheng Yan
		boolean isConstant = true;
		/**
		 * We assume that each path can only have two nodes, one for operand, and the other for the constant
		 */
		if(computationNodes.size() == 2) {
			//TODO what is the order of computation node?
			BytecodeInstruction ins = computationNodes.get(1).getInstruction();
			if(!ins.isConstant()) {
				isConstant = false;
				return isConstant;
			}
			else {
				Object obj = getConstantValue(ins);
				//TODO non-string value should be larger 100?
				
				if(obj instanceof Double) {
					return (Double)obj > 100;
				}
				else {
					return true;
				}
			}
		}
		
		return isConstant;
	}
	
	private Object getConstantValue(BytecodeInstruction ins) {
		//TODO Cheng Yan
		return "abstda";
	}

	public static List<ComputationPath> computePath(DepVariable root, List<BytecodeInstruction> operands) {
		// TODO Cheng Yan
		List<ComputationPath> computationPath = new ArrayList<>();
		List<DepVariable> nodes = new ArrayList<>(); 
		//traverse input to oprands
		nodes.add(root);
		dfsRoot(root, operands, computationPath, nodes);
		return computationPath;
		
//		return null;
	}

	private static void dfsRoot(DepVariable root, List<BytecodeInstruction> operands,
			List<ComputationPath> computationPath, List<DepVariable> nodes) {
		DepVariable node = root;
//		Boolean isVisted = true;
		int n = node.getInstruction().getOperandNum();
		if(n == 0)
			n += 1;
		for(int i = 0; i < node.getRelations().length;i++) {
			List<DepVariable> nodeList = node.getRelations()[i];
			if(nodeList == null) {
				continue;
			}
			for(int j = 0;j < nodeList.size();j++) {
				node = nodeList.get(j);
				if(!(operands.contains(node.getInstruction()) 
//						&& operands.contains(node.getInstruction().getLineNumber())
						)) {
					nodes.add(node);
					dfsRoot(node,operands,computationPath,nodes);
				}
				else {
					nodes.add(node);
					break;
				}
			}
		}
		
		if(operands.contains(nodes.get(nodes.size() - 1).getInstruction())) {
			List<DepVariable> computationNodes = new ArrayList<>();
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
