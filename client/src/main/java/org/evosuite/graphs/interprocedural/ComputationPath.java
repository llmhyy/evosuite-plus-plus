package org.evosuite.graphs.interprocedural;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.instrumentation.testability.StringHelper;
import org.evosuite.seeding.smart.BranchSeedInfo;
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.testcase.SensitivityMutator;
import org.evosuite.testcase.SensitivityPreservance;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.MethodUtil;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

/**
 * each computation path starts with a method input, ends with an operand
 * 
 * @author Yun Lin
 *
 */
public class ComputationPath {
	private double score = -1;
	private List<DepVariable> computationNodes;
	private Branch branch;
	
	private ComputationPath(Branch branch) {this.branch = branch;}

	public double getScore() {
		if (score == -1) {
			this.score = this.evaluateFastChannelScore();
		}

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
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		int count = 1;
		for(DepVariable var: computationNodes) {
			buffer.append(count++ + ":" + var.getInstruction() + "\n");
		}
		
		return buffer.toString();
	}

	/**
	 * (1) the start node should be a method input/parameter. (2) the path from the
	 * start node to the brand operand is "simple".
	 * 
	 * @param operands
	 * @return
	 */
	public double evaluateFastChannelScore() {
		boolean isParameter = isStartWithMethodInput(this);
		if (!isParameter)
			return 0;

//		return 1;

		if (this.computationNodes.size() == 1)
			return 1.0;

		DepVariable head = this.computationNodes.get(0);
		/**
		 * tail is operand
		 */
		String tailType = null;
		DepVariable tail = this.computationNodes.get(this.computationNodes.size() - 1);
		if (tail.isMethodCall()) {
			String methodDesc = tail.getInstruction().getCalledMethod();
			String[] parameters = MethodUtil.parseSignature(methodDesc);

//			String returnType = parameters[parameters.length - 1];
			if(this.branch.getInstruction().getOperandNum() == 1) {
				DepVariable prevNode = this.computationNodes.get(this.computationNodes.size() - 2);
				int order = tail.getInputOrder(prevNode);

				if (!tail.getInstruction().isInvokeStatic()) {
					order--;
				}

				if (order != -1) {
					tailType = parameters[order];
				} else {
					tail = this.computationNodes.get(this.computationNodes.size() - 2);
				}
			}
			

			
		}

		String headType = head.getDataType();
		if (headType.contains("[")) {
			headType = headType.substring(0, headType.indexOf("["));
		}
//		headType = MethodUtil.convertType(headType);
		if (tailType == null) {
			tailType = tail.getDataType();
		}
		if (tailType.contains("[")) {
			tailType = tailType.substring(0, tailType.indexOf("["));
			tailType = MethodUtil.convertType(tailType);
		}
		if (tailType.equals(BranchSeedInfo.OTHER)) {
			return 0;
		}
		System.currentTimeMillis();
		if (isCompatible(headType, tailType)) {

			double base = 6;

			double effectiveNodeNumber = calculateEffectiveNodeNumber();
			
			double factor = Math.max(0, effectiveNodeNumber - base);
			double score = 1 - factor / (factor + 1);

			return score;
		}

		return 0;

		// TODO later, we need a better way to evaluate the semantics of a path.
//		double value = 1;
//		
//		DepVariable prevNode = null;
//		for(int i=0; i<this.computationNodes.size()-1; i++) {
//			DepVariable node = computationNodes.get(i);
//			/**
//			 * conq is between (0, 1).
//			 */
//			double conq = evaluateConsequence(node, prevNode);
//			value = value * conq;
//			
//			prevNode = node;
//		}
//		
//		return value;
	}

	private double calculateEffectiveNodeNumber() {
		int count = 0;
		for(DepVariable node: this.computationNodes) {
			if(node.incurZeroInformation()) {
				continue;
			}
			
			count++;
		}
		
		return count;
	}

	public static boolean isCompatible(String headType, String tailType) {

		if (headType.equals(tailType)) {
			return true;
		}

		if (isPrimitiveNumber(headType) && isPrimitiveNumber(tailType)) {
			return true;
		} else if (isPrimitiveNumber(headType) ^ isPrimitiveNumber(tailType)) {
			return false;
		}

		if (isPrimitiveBoolean(headType) && isPrimitiveBoolean(tailType)) {
			return true;
		} else if (isPrimitiveBoolean(headType) ^ isPrimitiveBoolean(tailType)) {
			return false;
		}

		if (headType.equals("java.lang.Object") || tailType.equals("java.lang.Object")) {
			return true;
		}

		if (headType.equals(BranchSeedInfo.OTHER) || tailType.equals(BranchSeedInfo.OTHER)) {
			return false;
		}

		ClassLoader loader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		try {
			Class<?> headClass = loader.loadClass(headType);
			Class<?> tailClass = loader.loadClass(tailType);

			if (headClass.isAssignableFrom(tailClass) || tailClass.isAssignableFrom(headClass)) {
				return true;
			}

			Class<?> containerClass = loader.loadClass("java.util.Collection");
			if (containerClass.isAssignableFrom(headClass)) {
				return true;
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static boolean isPrimitiveBoolean(String typeString) {
		String[] primitiveNums = new String[] { "boolean", "java.lang.Boolean" };
		return ArrayUtil.contains(primitiveNums, typeString);
	}

	private static boolean isPrimitiveNumber(String typeString) {
		String[] primitiveNums = new String[] { "int", "byte", "float", "double", "short", "long", "char",
				"java.lang.Integer", "java.lang.Byte", "java.lang.Float", "java.lang.Double", "java.lang.Float",
				"java.lang.Short", "java.lang.Long", "java.,lang.Character" };
		return ArrayUtil.contains(primitiveNums, typeString);
	}

	public static boolean isStartWithMethodInput(ComputationPath computationPath) {
		if (computationPath.getComputationNodes().isEmpty()) {
			return false;
		}

		DepVariable node = computationPath.getComputationNodes().get(0);
		if (node.isParameter()) {
			return true;
		} else if (node.isInstaceField()) {
			String className = node.getInstruction().getClassName();
			if (className.equals(Properties.TARGET_CLASS)) {
				return true;
			}
		}

		return false;
	}

	private double evaluateConsequence(DepVariable node, DepVariable prevNode) {
		if (prevNode != null && prevNode.equals(node))
			return 1;

		BytecodeInstruction ins = node.getInstruction();

//		if(operands.contains(ins)) {
//			return 1;
//		}

		switch (ins.getInstructionType()) {
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
		if (prevNode == null)
			return 1;

		int inputOrder = node.getInputOrder(prevNode);
		if (inputOrder >= 0) {
			BytecodeInstruction ins = node.getInstruction();

			String desc = ins.getMethodCallDescriptor();
			String[] separateTypes = MethodUtil.parseSignature(desc);
			if (!ins.isInvokeStatic()) {
				inputOrder--;
			}

			if (inputOrder == -1) {
				/**
				 * It means the input is the caller object of the method, to be conservative, we
				 * make its sensitivity score to be 0. Nevertheless, we can attach some
				 * sensitivity for some String type.
				 */
				// TODO Cheng Yan/Lin Yun we can be smarter if we can analyze the method body
				/**
				 * input -> obj string b = obj.m() // m() can be getName(); obj can be string;
				 * ... if(b.length()>10){...}
				 */

				String callerObjectType = ins.getCalledMethodsClass();
				String methodName = ins.getCalledMethodName();

				// TODO for Cheng Yan, we can add rules here
				double value = SensitiveCallRules.getSensitivity(callerObjectType, methodName);
				return value;
			}

			String inputType = separateTypes[inputOrder];
			String outType = separateTypes[separateTypes.length - 1];

			double score = estimateInformationSensitivity(inputType, outType);
			return score;
		}

		System.err.println("DepVariable cannot locate its input");
		return 0.7;
	}

	public static double estimateInformationSensitivity(String inputType, String outputType) {
		if (inputType.equals(outputType)) {
			return 1;
		}

		if (isPrimitive(inputType) && isPrimitive(outputType)) {
			return 0.8;
		}

		if (isPrimitive(inputType) && !isPrimitive(outputType)) {
			return 0;
		}

		if (!isPrimitive(inputType) && isPrimitive(outputType)) {
			return 0;
		}

		ClassLoader loader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		try {
			String input = inputType.contains("[") ? inputType.substring(0, inputType.indexOf("[")) : inputType;
			String output = outputType.contains("[") ? outputType.substring(0, outputType.indexOf("[")) : outputType;

			Class<?> inputClazz = loader.loadClass(input);
			Class<?> outputClazz = loader.loadClass(output);

			if (inputClazz.isAssignableFrom(outputClazz) || outputClazz.isAssignableFrom(inputClazz)) {
				return 0.8;
			} else {
				double score = getRelevanceWithHeuristics(inputClazz, outputClazz);
				return score;
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return 0;
	}

	private static double getRelevanceWithHeuristics(Class<?> inputClazz, Class<?> outputClazz) {
		String className1 = inputClazz.getCanonicalName();
		String className2 = outputClazz.getCanonicalName();

		String common = greatestCommonPrefix(className1, className2);
		double numerator = common.length();
		double denominator = Math.min(className1.length(), className2.length());
		return numerator / denominator;
	}

	private static String greatestCommonPrefix(String a, String b) {
		int minLength = Math.min(a.length(), b.length());
		for (int i = 0; i < minLength; i++) {
			if (a.charAt(i) != b.charAt(i)) {
				return a.substring(0, i);
			}
		}
		return a.substring(0, minLength);
	}

	private static boolean isPrimitive(String inputType) {
		if (inputType.equals(int.class.toString()) || inputType.equals(long.class.toString())// LONG
				|| inputType.equals(float.class.toString()) || inputType.equals(double.class.toString())
				|| inputType.equals(char.class.toString()) || inputType.equals(boolean.class.toString())// BOOLEAN
				|| inputType.equals(byte.class.toString()) || inputType.equals(short.class.toString())) {
			return true;
		}

		return false;
	}

	public boolean isHardConstant(List<BytecodeInstruction> operands) {
		boolean isConstant = false;
		/**
		 * We assume that each path can only have two nodes, one for operand, and the
		 * other for the constant
		 * 
		 * x == 34500000 computationNodes.size() == 1
		 * 
		 */
		BytecodeInstruction ins = computationNodes.get(0).getInstruction();
		if (!ins.isConstant()) {
			isConstant = false;
			return isConstant;
		} else {
			if (ins.getASMNode().getType() == AbstractInsnNode.LDC_INSN)
				return true;

			Object obj = getConstantValue(ins);
			// TODO Cheng Yan non-string value should be larger 100?
			if (obj instanceof Double) {
				return (Double) obj > 100;
			} else {
				return false;
			}
		}

//		return isConstant;
	}

	public static Object getConstantValue(BytecodeInstruction ins) {
		AbstractInsnNode node = ins.getASMNode();
		if (node.getType() == AbstractInsnNode.INT_INSN) {
			IntInsnNode iins = (IntInsnNode) node;
			return Double.valueOf(iins.operand);
		} else if (node.getType() == AbstractInsnNode.INSN) {
			// small value
			return 5.0;
		} else if (node.getType() == AbstractInsnNode.LDC_INSN) {
			LdcInsnNode ldc = (LdcInsnNode) node;
			if (ldc.cst.getClass() != String.class) {
				return Double.valueOf(ldc.cst.toString());
			}
		}

		return "string";
	}

	public static List<ComputationPath> computePath(DepVariable root, Branch branch){
		List<BytecodeInstruction> operands = branch.getInstruction().getOperands();
		List<ComputationPath> computationPath = new ArrayList<>();
		List<DepVariable> nodes = new ArrayList<>();
		// traverse input to oprands
		nodes.add(root);
		dfsRoot(root, operands, computationPath, nodes, branch);
		return computationPath;
	}
	
	
//	private static List<ComputationPath> computePath(DepVariable root, List<BytecodeInstruction> operands) {
//		
//		
////		return null;
//	}

	private static void dfsRoot(DepVariable root, List<BytecodeInstruction> operands,
			List<ComputationPath> computationPath, List<DepVariable> nodes, Branch branch2) {
		DepVariable node = root;
//		int n = node.getInstruction().getOperandNum();
//		if(n == 0)
//			n += 1;
		for (int i = 0; i < node.getRelations().length; i++) {
			List<DepVariable> nodeList = node.getRelations()[i];
			if (nodeList == null) {
				continue;
			}
			for (int j = 0; j < nodeList.size(); j++) {
				node = nodeList.get(j);
				if (!(nodes.contains(node) ||  
						operands.contains(node.getInstruction()) 
//						&& nodes.contains(node)
				)) {
					nodes.add(node);
					dfsRoot(node,operands,computationPath,nodes, branch2);
				}
				else {
					nodes.add(node);
					break;
				}
			}
		}

		if (operands.contains(nodes.get(nodes.size() - 1).getInstruction())) {
			List<DepVariable> computationNodes = new ArrayList<>();
			for (int i = 0; i < nodes.size(); i++) {
				computationNodes.add(nodes.get(i));
			}
			ComputationPath pathRecord = new ComputationPath(branch2);
			pathRecord.setComputationNodes(computationNodes);
//			pathRecord.setScore(computationNodes.size());
			computationPath.add(pathRecord);
		}
		nodes.remove(nodes.size() - 1);
	}

	public int size() {
		return this.computationNodes.size();
	}

	public boolean containsInstruction(BytecodeInstruction ins) {
		for (DepVariable node : this.computationNodes) {
			if (node.getInstruction().equals(ins)) {
				return true;
			}
		}
		return false;
	}

//	private Boolean isFastChannel = null;
	private SensitivityPreservance preservingList = null;

	public boolean isFastChannel() {
		if (!(this.getComputationNodes().get(0).isParameter()
				|| this.getComputationNodes().get(0).isStaticField()
				|| this.getComputationNodes().get(0).isInstaceField()))
			return false;
		
		boolean staticRuleMath = matchStaticFastChannelRule();
		if(staticRuleMath) {
			return true;
		}
		
		if (preservingList == null) {

			preservingList = SensitivityMutator.testBranchSensitivity(this);
//			isFastChannel = this.evaluateFastChannelScore() > Properties.FAST_CHANNEL_SCORE_THRESHOLD;
		}

		boolean isFastChannel = preservingList.isValuePreserving() && preservingList.isSensitivityPreserving();
		return isFastChannel;
	}

	private boolean matchStaticFastChannelRule() {
		if(this.size() <= 2) {
			DepVariable var = this.computationNodes.get(this.computationNodes.size()-1);
			String methodName = var.getInstruction().getCalledMethod();
			
			if(!SeedingApplicationEvaluator.isBooleanReturnType(methodName)) {
				return true;
			}
			
		}
		return false;
	}

	public BytecodeInstruction getInstruction(int i) {
		return this.computationNodes.get(i).getInstruction();
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}
	
	public boolean shouldIgnore() {
		
		if(this.computationNodes.size() == 0) return true;
		
		if(this.computationNodes.size() == 1) return false;
		
		DepVariable operand = this.computationNodes.get(this.computationNodes.size()-1);
		if(operand.getInstruction().isMethodCall()) {
			String className = operand.getInstruction().getCalledMethodsClass();
			if(className.equals(StringHelper.class.getCanonicalName()) || 
					className.equals(CollectionHelper.class.getCanonicalName())) {
				DepVariable prevNode = this.computationNodes.get(this.computationNodes.size()-2);
				
				int position = operand.getInputOrder(prevNode);
				if(position > 1) {
					return true;
				}
			}
		}
		
		return false;
	}

	public boolean isPureConstantPath() {
		if(this.getInstruction(0).isConstant()) {
			
			if(this.computationNodes.size() <= 2) return true;
			
			for(int i=1; i<this.computationNodes.size()-1; i++) {
				DepVariable node = this.computationNodes.get(i);
				
				if(!node.incurZeroInformation()) {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}

	public BytecodeInstruction getRelevantTailInstruction() {
		DepVariable node = this.computationNodes.get(this.computationNodes.size()-1);
		
		if(node.getInstruction().getASMNode().getOpcode() == Opcodes.FCMPG ||
				node.getInstruction().getASMNode().getOpcode() == Opcodes.FCMPL ||
				node.getInstruction().getASMNode().getOpcode() == Opcodes.DCMPG ||
				node.getInstruction().getASMNode().getOpcode() == Opcodes.DCMPL ||
				node.getInstruction().getASMNode().getOpcode() == Opcodes.LCMP ) {
			DepVariable prevNode = this.computationNodes.get(this.computationNodes.size()-2);
			return prevNode.getInstruction();
		}
		else {
			if(node.getInstruction().isMethodCall()) {
				if(branch.getInstruction().getOperandNum() == 1) {
					DepVariable prevNode = this.computationNodes.get(this.computationNodes.size()-2);
					return prevNode.getInstruction();
					
//					String methodSig = node.getInstruction().getCalledMethod();
//					String[] types = MethodUtil.parseSignature(methodSig);
//					String returnType = types[types.length-1];
//					if(returnType.equals("boolean")) {
//						
//					}
				}
				
			}
		}
		
		
		return node.getInstruction();	
	}

}
