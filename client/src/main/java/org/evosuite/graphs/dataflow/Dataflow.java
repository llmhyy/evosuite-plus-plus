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
	 * a map maintains what variables are dependent by which branch, method->branch->dependent variables
	 * 
	 */
	public static Map<String, Map<Branch, Set<DepVariable>>> branchDepVarsMap = new HashMap<>();

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
				if(Properties.TARGET_METHOD.equals(methodName)) {
					ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
					FBranchDefUseAnalyzer.analyze(cfg.getRawGraph());
					
					Map<Branch, Set<DepVariable>> map = analyzeMethod(cfg);
					branchDepVarsMap.put(methodName, map);					
				}
			}
		}

	}

	private static Map<Branch, Set<DepVariable>> analyzeMethod(ActualControlFlowGraph cfg) {
		Map<Branch, Set<DepVariable>> map = new HashMap<>();
		
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		
		for (Branch b : BranchPool.getInstance(classLoader).retrieveBranchesInMethod(className, methodName)) {
			Set<DepVariable> allDepVars = new HashSet<DepVariable>();
			Set<BytecodeInstruction> visitedIns = new HashSet<BytecodeInstruction>();
			if (!b.isInstrumented()) {
				List<Value> operandValues = getBranchOperands(b.getInstruction());
				for (Value value : operandValues) {
					searchDependantVariables(value, cfg, allDepVars, visitedIns);
				}
			}
			
			map.put(b, allDepVars);
		}
		
		return map;
	}
	
	@SuppressWarnings("rawtypes")
	private static Set<DepVariable> analyzeReturnValueFromMethod(BytecodeInstruction instruction){
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		String className = instruction.getCalledMethodsClass();
		String methodName = instruction.getCalledMethod();
		ActualControlFlowGraph calledCfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		MethodNode innerNode = getMethodNode(classLoader, className, methodName);
		if (calledCfg == null) {
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			try {
				bytecodeAnalyzer.analyze(classLoader, className, methodName, innerNode);
			} catch (AnalyzerException e) {
				e.printStackTrace();
			}
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			calledCfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		}
		FBranchDefUseAnalyzer.analyze(calledCfg.getRawGraph());
		
		Set<DepVariable> allDepVars = new HashSet<DepVariable>();
		Set<BytecodeInstruction> visitedIns = new HashSet<BytecodeInstruction>();
		for (BytecodeInstruction exit : calledCfg.getExitPoints()) {
			if (exit.getOperandNum() > 0) {
				Frame frame = exit.getFrame();
				for (int i = 0; i < exit.getOperandNum(); i++) {
					Value val = frame.getStack(i);
					searchDependantVariables(val, calledCfg, allDepVars, visitedIns);
				}
			}
		}
		
		return allDepVars;
	}
	
	public static Map<BytecodeInstruction, DepVariable> instructionPool = new HashMap<BytecodeInstruction, DepVariable>();
	
	/**
	 * value is written by {@code defIns}
	 * @param value
	 * @param defIns
	 * @return
	 */
	private static DepVariable parseVariable(SourceValue value, BytecodeInstruction defIns) {
		String className = defIns.getClassName();
		String varName = "$unknown";
		
		DepVariable var = instructionPool.get(defIns); 
		if(var == null) {
			var = new DepVariable(className, varName, DepVariable.OTHER, defIns);
			setType(var);
			instructionPool.put(defIns, var);
		}
		
		return var;
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
	private static void searchDependantVariables(Value value, ActualControlFlowGraph cfg, Set<DepVariable> allDepVars,
			Set<BytecodeInstruction> visitedIns) {
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		MethodNode node = getMethodNode(classLoader, className, methodName);
		
		if (value instanceof SourceValue) {
			SourceValue srcValue = (SourceValue) value;
			for(AbstractInsnNode insNode: srcValue.insns) {
				BytecodeInstruction defIns = convert2BytecodeInstruction(cfg, node, insNode);
				if (visitedIns.contains(defIns)) {
					continue;
				}
				visitedIns.add(defIns);
				
				DepVariable outputVar = parseVariable(srcValue, defIns);
				List<DepVariable> intputVars = new ArrayList<DepVariable>();
				/**
				 * the variable is computed by values on stack
				 */
				int operandNum = defIns.getOperandNum();
				for (int i = 0; i < operandNum; i++) {
					Frame frame = defIns.getFrame();
					int index = frame.getStackSize() - i -1;
					Value val = frame.getStack(index);
					
					SourceValue inputVal = (SourceValue)val;
					for(AbstractInsnNode newDefInsNode: inputVal.insns) {
						BytecodeInstruction newDefIns = convert2BytecodeInstruction(cfg, node, newDefInsNode);
						DepVariable inputVar = parseVariable((SourceValue)val, newDefIns);
						inputVar.buildRelation(outputVar);
						
						searchDependantVariables(val, cfg, allDepVars, visitedIns);
						
						intputVars.add(inputVar);
					}
				}
				
				/**
				 * if defIns is a method call, we need to explore more potential variables.
				 */
				if(defIns.isMethodCall()) {
					DepVariable objectVar = null;
					List<DepVariable> paramVars = new ArrayList<DepVariable>();
					/**
					 * is the method static?
					 */
					if(defIns.getCalledMethodsArgumentCount() != intputVars.size()) {
						objectVar = intputVars.get(intputVars.size()-1);
						for(int i=0; i<intputVars.size()-1; i++) {
							paramVars.add(intputVars.get(i));
						}
					}
					else {
						paramVars.addAll(intputVars);
					}
					
					Set<DepVariable> relatedVariables = analyzeReturnValueFromMethod(defIns);
					for(DepVariable var: relatedVariables) {
						DepVariable rootVar = var.getRootVar();
						if(var.getType() == DepVariable.STATIC_FIELD) {
							allDepVars.add(var);					
						}
						else {
							List<DepVariable> path = rootVar.findPath(var);
							if(path != null) {
								DepVariable secVar = path.get(1);
								if(rootVar.getType() == DepVariable.PARAMETER) {
									int paramOrder = secVar.getParamOrder();
									DepVariable param = paramVars.get(paramOrder);
									param.buildRelation(secVar);
								}
								else if(rootVar.getType() == DepVariable.THIS) {
									objectVar.buildRelation(secVar);
								}
							}
						}
						
						allDepVars.add(var);
					}
				}
				
				/**
				 *  handle load/get static, need to put the variable into the return list
				 */
				if(outputVar.isStaticField() || outputVar.isInstaceField()) {
					allDepVars.add(outputVar);
				}
				
				if(outputVar.referenceToThis() || outputVar.isParameter() || outputVar.isStaticField()) {
					//return;
				}
				
				/**
				 * the variable is computed by local variables
				 */
				if (defIns.isLocalVariableUse()){
					//keep traverse
					DefUseAnalyzer defUseAnalyzer = new DefUseAnalyzer();
					defUseAnalyzer.analyze(classLoader, node, className, methodName, node.access);
					Use use = DefUseFactory.makeUse(defIns);
					// Ignore method parameter
					List<Definition> defs = DefUsePool.getDefinitions(use);
					for (Definition def : CollectionUtil.nullToEmpty(defs)) {
						if (def != null) {
							BytecodeInstruction defInstruction = convert2BytecodeInstruction(cfg, node, def.getASMNode());
							Frame frame = def.getFrame();
							
							for (int i = 0; i < defInstruction.getOperandNum(); i++) {
								int index = frame.getStackSize() - i -1;
								Value val = frame.getStack(index);
								SourceValue sVal = (SourceValue)val;
								
								for(AbstractInsnNode newDefInsNode: sVal.insns) {
									BytecodeInstruction newDefIns = convert2BytecodeInstruction(cfg, node, newDefInsNode);
									DepVariable inputVar = parseVariable((SourceValue)val, newDefIns);
									
									inputVar.buildRelation(outputVar);
									searchDependantVariables(val, cfg, allDepVars, visitedIns);							
								}
							}
						}
						
						
					}
				}
				
				
				/**
				 * TODO handle control flow, for ziheng
				 */
			}
			

		}
	}

	private static void setType(DepVariable outputVar) {
		BytecodeInstruction ins = outputVar.getInstruction();
		if(outputVar.referenceToThis()) {
			outputVar.setType(DepVariable.THIS);
			outputVar.setName("this");
		}
		else if(outputVar.isParameter()) {
			outputVar.setType(DepVariable.PARAMETER);
			outputVar.setName(ins.getVariableName());
		}
		else if(outputVar.isStaticField()) {
			outputVar.setType(DepVariable.STATIC_FIELD);
			outputVar.setName(((FieldInsnNode)ins.getASMNode()).name);
		}
		else if(outputVar.isInstaceField()) {
			outputVar.setType(DepVariable.INSTANCE_FIELD);
			outputVar.setName(((FieldInsnNode)ins.getASMNode()).name);
		}
			
	}

	private static BytecodeInstruction convert2BytecodeInstruction(ActualControlFlowGraph cfg, MethodNode node,
			AbstractInsnNode ins) {
		AbstractInsnNode condDefinition = (AbstractInsnNode)ins;
		BytecodeInstruction defIns = cfg.getInstruction(node.instructions.indexOf(condDefinition));
		return defIns;
	}


	private static List<Value> getBranchOperands(BytecodeInstruction branchInstruction) {
		List<Value> values = new ArrayList<Value>();
		CFGFrame frame = branchInstruction.getFrame();
		values.add(frame.getStack(0));

		if (!CollectionUtil.existIn(branchInstruction.getASMNode().getOpcode(), Opcodes.IFEQ, Opcodes.IFNE,
				Opcodes.IFGE, Opcodes.IFGT, Opcodes.IFLE, Opcodes.IFLT, Opcodes.IFNULL, Opcodes.IFNONNULL)) {
			values.add(frame.getStack(1));
		}

		return values;
	}

	public static MethodNode getMethodNode(InstrumentingClassLoader classLoader, String className, String methodName) {
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
