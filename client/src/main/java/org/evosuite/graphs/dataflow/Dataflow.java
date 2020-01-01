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
						for (Value value : operandValues) {
							searchDependantVariables(value, cfg, allDepVars, visitedIns);
						}
					}

					Dataflow.addEntry(b, allDepVars);
				}
			}
		}

	}

	
	private static DepVariable parseVariable(SourceValue value, BytecodeInstruction defIns) {
		//TODO
		
		return null;
	}
	
	/**
	 * 
	 * This method collects all the variable depended by {@code value} and put them into {@code allDepVars}
	 * {@code depVars}
	 * 
	 * This search algorithm should take a walk through data flows until we find static field, instance field, or parameter.
	 * We would like to take the following usage into account:
	 * a.m1(x1, x2).m2(x3).f
	 * in which a can be either a static field, instance field, or parameter.
	 * 
	 * @param value
	 * @param cfg
	 * @param allDepVars
	 * @param visitedIns
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static void searchDependantVariables(Value value, ActualControlFlowGraph cfg, List<DepVariable> allDepVars,
			Set<BytecodeInstruction> visitedIns) {
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		MethodNode node = getMethodNode(classLoader, className, methodName);
		
		if (value instanceof SourceValue) {
			if (!visitedNode.contains(node)) {
				DefUseAnalyzer defUseAnalyzer = new DefUseAnalyzer();
				defUseAnalyzer.analyze(classLoader, node, className, methodName, node.access);
				visitedNode.add(node);
			}

			SourceValue srcValue = (SourceValue) value;
			for(AbstractInsnNode insNode: srcValue.insns) {
				BytecodeInstruction defIns = convert2BytecodeInstruction(cfg, node, insNode);
				if (visitedIns.contains(defIns)) {
					continue;
				}
				visitedIns.add(defIns);
				
				DepVariable outputVar = parseVariable(srcValue, defIns);
				/**
				 * the variable is computed by values on stack
				 */
				if (defIns.getFrame().getStackSize() > 0) {
					Frame frame = defIns.getFrame();
					int stackSize = frame.getStackSize();
					for (int i = 0; i < stackSize; i++) {
						Value val = frame.getStack(i);
						SourceValue sVal = (SourceValue)val;
						for(AbstractInsnNode newDefInsNode: sVal.insns) {
							BytecodeInstruction newDefIns = convert2BytecodeInstruction(cfg, node, newDefInsNode);
							DepVariable inputVar = parseVariable((SourceValue)val, newDefIns);
							
							RelationBuilder.buildRelation(outputVar, inputVar);
							searchDependantVariables(val, cfg, allDepVars, visitedIns);							
						}
					}
				}
				/**
				 * the variable is computed by local variables, constant, or static field
				 */
				else {
					//TODO handle load/const/get static, need to put the variale into the return list
					if(outputVar.isParameter() || outputVar.isStaticField() || outputVar.isInstaceField()) {
						allDepVars.add(outputVar);
					}
					else {
						//keep traverse
						Use use = DefUseFactory.makeUse(defIns);
						// Ignore method parameter
						List<Definition> defs = DefUsePool.getDefinitions(use);
						for (Definition def : CollectionUtil.nullToEmpty(defs)) {
							if (def != null) {
								Frame frame = def.getFrame();
								int stackSize = frame.getStackSize();
								for (int i = 0; i < stackSize; i++) {
									Value val = frame.getStack(i);
									searchDependantVariables(val, cfg, allDepVars, visitedIns);
								}
							}
						}
					}
				}
				
				if(defIns.isMethodCall()) {
					//TODO recurisvely analyze the method information, need to take care the method 
					//inside or outside of the class
					searchInterproceduralVariables(allDepVars, visitedIns, defIns);
				}
				
				if (defIns.getFrame().getStackSize() > 0) {
					Frame frame = defIns.getFrame();
					int stackSize = frame.getStackSize();
					for (int i = 0; i < stackSize; i++) {
						Value val = frame.getStack(i);
						searchDependantVariables(val, cfg, allDepVars, visitedIns);
					}
				}
			}
			

		}
	}

	private static BytecodeInstruction convert2BytecodeInstruction(ActualControlFlowGraph cfg, MethodNode node,
			AbstractInsnNode ins) {
		AbstractInsnNode condDefinition = (AbstractInsnNode)ins;
		BytecodeInstruction defIns = cfg.getInstruction(node.instructions.indexOf(condDefinition));
		return defIns;
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
	private static void searchInterproceduralVariables(
			List<DepVariable> allDepVars, Set<BytecodeInstruction> visitedIns, BytecodeInstruction condBcDef) {
		
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
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
							searchDependantVariables(val, calledCfg, allDepVars, visitedIns);
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
					searchDependantVariables(val, calledCfg, allDepVars, visitedIns);
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

}
