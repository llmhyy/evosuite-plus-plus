package org.evosuite.graphs.dataflow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ResourceList;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.coverage.dataflow.DefUseFactory;
import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.coverage.dataflow.Definition;
import org.evosuite.coverage.dataflow.Use;
import org.evosuite.coverage.fbranch.FBranchDefUseAnalyzer;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.CFGFrame;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.CollectionUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;

public class Dataflow {
	/**
	 * a map maintains what variables are dependent by which branch
	 * 
	 */
	public static Map<Branch, List<DepVariable>> branchDepVarsMap = new HashMap<Branch, List<DepVariable>>();
	public static Map<Branch, List<Branch>> branchDepBranchesMap = new HashMap<Branch, List<Branch>>();

	private static Set<MethodNode> visitedNode = new HashSet<MethodNode>();

//	public Map<Branch, List<DepVariable>> getBranchDependentMap() {
//		return this.branchDepVarsMap;
//	}

	public static void addEntry(Branch branch, List<DepVariable> varList) {
		branchDepVarsMap.put(branch, varList);
	}

	public static void initializeDataflow() {
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();

		for (String className : BranchPool.getInstance(classLoader).knownClasses()) {
			// when limitToCUT== true, if not the class under test of a inner/anonymous
			// class, continue
			if (!isCUT(className))
				continue;
			// when limitToCUT==false, consider all classes, but excludes libraries ones
			// according the INSTRUMENT_LIBRARIES property
			if (!Properties.INSTRUMENT_LIBRARIES && !DependencyAnalysis.isTargetProject(className))
				continue;

			// Branches
			for (String methodName : BranchPool.getInstance(classLoader).knownMethods(className)) {

				ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
				FBranchDefUseAnalyzer.analyze(cfg.getRawGraph());

				// TODO determine dependent branches
				Set<BytecodeInstruction> exits = determineExitPoints(cfg);

				MethodNode node = getMethodNode(classLoader, className, methodName);

				for (Branch b : BranchPool.getInstance(classLoader).retrieveBranchesInMethod(className, methodName)) {

					List<DepVariable> allDepVars = new ArrayList<DepVariable>();
					Set<BytecodeInstruction> visitedIns = new HashSet<BytecodeInstruction>();

					if (!b.isInstrumented()) {
						List<Value> operandValues = getOperands(b.getInstruction());
//						DepVariable parent = new DepVariable(className, "this", null, null);
						for (Value value : operandValues) {
							checkUseForInstruction(value, cfg, node, classLoader, className, methodName, allDepVars,
									visitedIns, null);
						}
					}

					Dataflow.addEntry(b, allDepVars);
				}
			}
		}

	}

	private static List<Value> getOperands(BytecodeInstruction branchInstruction) {
		List<Value> values = new ArrayList<Value>();
		CFGFrame frame = branchInstruction.getFrame();
		values.add(frame.getStack(0));

		if (!CollectionUtil.existIn(branchInstruction.getASMNode().getOpcode(), Opcodes.IFEQ, Opcodes.IFNE,
				Opcodes.IFGE, Opcodes.IFGT, Opcodes.IFLE, Opcodes.IFLT, Opcodes.IFNULL, Opcodes.IFNONNULL)) {
			values.add(frame.getStack(1));
		}

		return values;
	}

	private static Set<BytecodeInstruction> determineExitPoints(ActualControlFlowGraph cfg) {
		return cfg.getExitPoints();
	}

	private static MethodNode getMethodNode(InstrumentingClassLoader classLoader, String className, String methodName) {
		InputStream is = ResourceList.getInstance(classLoader).getClassAsStream(className);
		try {
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> l = cn.methods;

			for (MethodNode n : l) {
				String methodSig = n.name + n.desc;
				if (methodSig.equals(methodName)) {
					return n;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static boolean isCUT(String className) {
		if (!Properties.TARGET_CLASS.equals("") && !(className.equals(Properties.TARGET_CLASS)
				|| className.startsWith(Properties.TARGET_CLASS + "$"))) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * This method collects all the variable depended by {@code value} to
	 * {@code depVars}
	 * 
	 * @param value
	 * @param cfg
	 * @param node
	 * @param classLoader
	 * @param className
	 * @param methodName
	 * @param allDepVars
	 * @param visitedIns
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static void checkUseForInstruction(Value value, ActualControlFlowGraph cfg, MethodNode node,
			InstrumentingClassLoader classLoader, String className, String methodName, List<DepVariable> allDepVars,
			Set<BytecodeInstruction> visitedIns, DepVariable callParent) {

		if (value instanceof SourceValue) {

			if (!visitedNode.contains(node)) {
				DefUseAnalyzer defUseAnalyzer = new DefUseAnalyzer();
				defUseAnalyzer.analyze(classLoader, node, className, methodName, node.access);
				visitedNode.add(node);
			}

			SourceValue srcValue = (SourceValue) value;
			for(AbstractInsnNode ins: srcValue.insns) {
				AbstractInsnNode condDefinition = (AbstractInsnNode)ins;
				BytecodeInstruction condDefinitionInstruction = cfg
						.getInstruction(node.instructions.indexOf(condDefinition));
				
				if (visitedIns.contains(condDefinitionInstruction)) {
					continue;
				}
				
				visitedIns.add(condDefinitionInstruction);
				if (condDefinitionInstruction.getFrame().getStackSize() > 0) {
					Frame frame = condDefinitionInstruction.getFrame();
					int stackSize = frame.getStackSize();
					for (int i = 0; i < stackSize; i++) {
						Value val = frame.getStack(i);
						checkUseForInstruction(val, cfg, node, classLoader, className, methodName, allDepVars, visitedIns, callParent);
					}
				}
				
				// ALOAD 0
//			if (condBcDef.loadsReferenceToThis()) {
////				depDefs.add(e);
////				condBcDef.getASMNode()
//			}
				
				// Field access, local variable or array load
				if (condDefinitionInstruction.isUse()) {
					// method call
					if (condDefinitionInstruction.isMethodCall()) {
						DepVariable newCalledParent = null;
						if (!condDefinitionInstruction.getCalledCFG().isStaticMethod()) {
							newCalledParent = searchParent(condDefinitionInstruction, cfg, node);
							newCalledParent.setParent(callParent);
						}
						
						involveRecursiveValueInMethod(classLoader, allDepVars, visitedIns, condDefinitionInstruction,
								callParent);
					}
					// getField or getStatic
					else if (condDefinitionInstruction.isFieldUse()) {
						assert condDefinitionInstruction.getASMNode() instanceof FieldInsnNode;
						FieldInsnNode fieldInsnNode = ((FieldInsnNode) condDefinitionInstruction.getASMNode());
						String varName = fieldInsnNode.name;
						String clazz = fieldInsnNode.owner;
						
						if (condDefinitionInstruction.isStaticDefUse()) {
							DepVariable depVar = new DepVariable(clazz, varName, condDefinitionInstruction, null);
							allDepVars.add(depVar);
						} else {
							DepVariable parent = searchParent(condDefinitionInstruction, cfg, node);
							
							DepVariable depVar = new DepVariable(clazz, varName, condDefinitionInstruction, parent);
							allDepVars.add(depVar);
						}
						
					}
					// local variable
					else {
						Use use = DefUseFactory.makeUse(condDefinitionInstruction);
						// Ignore method parameter
						List<Definition> defs = DefUsePool.getDefinitions(use);
						for (Definition def : CollectionUtil.nullToEmpty(defs)) {
							if (def != null) {
								Frame frame = def.getFrame();
								int stackSize = frame.getStackSize();
								for (int i = 0; i < stackSize; i++) {
									Value val = frame.getStack(i);
									checkUseForInstruction(val, cfg, node, classLoader, className, methodName, allDepVars, visitedIns, callParent);
								}
							}
						}
					}
				}
			}
			

		}
	}

	private static DepVariable searchParent(BytecodeInstruction condDefinitionInstruction, ActualControlFlowGraph cfg,
			MethodNode node) {
		Value objVal = condDefinitionInstruction.getFrame().getStack(0);
		if (objVal instanceof SourceValue) {
			SourceValue objValue = (SourceValue) objVal;
			AbstractInsnNode fieldDef = (AbstractInsnNode) objValue.insns.iterator().next();
			BytecodeInstruction ins = cfg.getInstruction(node.instructions.indexOf(fieldDef));
			
			DepVariable parent = new DepVariable(condDefinitionInstruction.getClassName(), "this", ins, null);
			if(ins.getFrame().getStackSize() == 0) {
				return parent;
			}
			else {
				DepVariable grandparent = new DepVariable(condDefinitionInstruction.getClassName(), ((FieldInsnNode)ins.getASMNode()).name, ins, null);
				System.currentTimeMillis();
				if(ins.isFieldUse()) {
					objVal = condDefinitionInstruction.getFrame().getStack(0);
				}
				
				return parent;
			}
		}
		
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static void involveRecursiveValueInMethod(InstrumentingClassLoader classLoader,
			List<DepVariable> allDepVars, Set<BytecodeInstruction> visitedIns, BytecodeInstruction condBcDef,
			DepVariable callParent) {
		String innerClass = condBcDef.getCalledMethodsClass();
		String innerMethod = condBcDef.getCalledMethod();
		ActualControlFlowGraph calledCfg = GraphPool.getInstance(classLoader).getActualCFG(innerClass, innerMethod);
		MethodNode innerNode = getMethodNode(classLoader, innerClass, innerMethod);
		if (calledCfg == null) {
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			try {
				bytecodeAnalyzer.analyze(classLoader, innerClass, innerMethod, innerNode);
			} catch (AnalyzerException e) {
				e.printStackTrace();
			}
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			calledCfg = GraphPool.getInstance(classLoader).getActualCFG(innerClass, innerMethod);
		}
		FBranchDefUseAnalyzer.analyze(calledCfg.getRawGraph());

		Set<BytecodeInstruction> exits = determineExitPoints(calledCfg);
		for (BytecodeInstruction exit : exits) {
			// control flow
			if (!exit.getControlDependencies().isEmpty()) {
				Set<ControlDependency> cds = exit.getControlDependencies();
				for (ControlDependency cd : cds) {
					BytecodeInstruction ins = cd.getBranch().getInstruction();
					if (ins.getFrame().getStackSize() > 0) {
						Frame frame = ins.getFrame();
						int stackSize = frame.getStackSize();
						for (int i = 0; i < stackSize; i++) {
							Value val = frame.getStack(i);
							checkUseForInstruction(val, calledCfg, innerNode, classLoader, innerClass, innerMethod, allDepVars, visitedIns, callParent);
						}
					}
				}
			}
			// data flow
			if (exit.getFrame().getStackSize() > 0) {
				Frame frame = exit.getFrame();
				int stackSize = frame.getStackSize();
				for (int i = 0; i < stackSize; i++) {
					Value val = frame.getStack(i);
					checkUseForInstruction(val, calledCfg, innerNode, classLoader, innerClass, innerMethod, allDepVars, visitedIns, callParent);
				}
			}
		}
	}

//	@SuppressWarnings("rawtypes")
//	private static void involveRecursiveValueOnStack(BytecodeInstruction def, ActualControlFlowGraph cfg,
//			MethodNode node, InstrumentingClassLoader classLoader, String className, String methodName,
//			List<DepVariable> allDepVariables, Set<BytecodeInstruction> vistiedIns, DepVariable callParent) {
//		Frame frame = def.getFrame();
//		int stackSize = frame.getStackSize();
//		for (int i = 0; i < stackSize; i++) {
//			Value val = frame.getStack(i);
//			checkUseForInstruction(val, cfg, node, classLoader, className, methodName, allDepVariables, vistiedIns, callParent);
//		}
//	}
}
