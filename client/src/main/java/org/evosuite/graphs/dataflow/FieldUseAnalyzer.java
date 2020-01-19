package org.evosuite.graphs.dataflow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ResourceList;
import org.evosuite.coverage.dataflow.DefUseFactory;
import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.coverage.dataflow.Definition;
import org.evosuite.coverage.dataflow.Use;
import org.evosuite.coverage.fbranch.FBranchDefUseAnalyzer;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.utils.CollectionUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;

public class FieldUseAnalyzer {
	private Map<BytecodeInstruction, DepVariable> instructionPool = new HashMap<BytecodeInstruction, DepVariable>();
	
	@SuppressWarnings("rawtypes")
	private Set<DepVariable> analyzeReturnValueFromMethod(BytecodeInstruction instruction){
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
		boolean canBeAnalyzed = FBranchDefUseAnalyzer.analyze(calledCfg.getRawGraph());
		if(!canBeAnalyzed) {
			return new HashSet<DepVariable>();
		}
		
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
	
	
	
	/**
	 * value is written by {@code defIns}
	 * @param value
	 * @param defIns
	 * @return
	 */
	private DepVariable parseVariable(SourceValue value, BytecodeInstruction defIns) {
		String className = defIns.getClassName();
		String varName = "$unknown";
		
		DepVariable var = instructionPool.get(defIns); 
		if(var == null) {
			var = new DepVariable(className, varName, defIns);
			
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
	 * @param allLeafDepVars
	 * @param visitedIns
	 * @return
	 */
	public void searchDependantVariables(Value value, ActualControlFlowGraph cfg, Set<DepVariable> allLeafDepVars,
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
				/**
				 * the variable is computed by values on stack
				 */
				List<DepVariable> intputVars = buildInputOutputForInstruction(defIns, node,
						outputVar, cfg, allLeafDepVars, visitedIns);
				
				/**
				 * if defIns is a method call, we need to explore more potential variables (fields) inside the method.
				 */
				if(defIns.isMethodCall()) {
					exploreInterproceduralInstruction(allLeafDepVars, defIns, intputVars);
				}
				
				/**
				 *  handle load/get static, need to put the variable into the return list
				 */
				if(outputVar.isStaticField() || outputVar.isInstaceField()) {
					allLeafDepVars.add(outputVar);
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
							buildInputOutputForInstruction(defInstruction, node,
									outputVar, cfg, allLeafDepVars, visitedIns);
						}
					}
				}
				
				/**
				 * handle control flow
				 */
				for(ControlDependency control: defIns.getControlDependencies()) {
					buildInputOutputForInstruction(control.getBranch().getInstruction(), node,
							outputVar, cfg, allLeafDepVars, visitedIns);
				}
				
			}
			

		}
	}

	private void exploreInterproceduralInstruction(Set<DepVariable> allLeafDepVars, BytecodeInstruction defIns,
			List<DepVariable> intputVars) {
		DepVariable objectVar = null;
		List<DepVariable> paramVars = new ArrayList<DepVariable>();
		/**
		 * is the method static?
		 */
		if(defIns.getCalledMethodsArgumentCount() != intputVars.size()) {
			objectVar = intputVars.get(intputVars.size()-1);
			for(int i=intputVars.size()-1; i>=1; i--) {
				paramVars.add(intputVars.get(i));
			}
		}
		else {
			paramVars.addAll(intputVars);
		}
		
		Set<DepVariable> relatedVariables = analyzeReturnValueFromMethod(defIns);
		for(DepVariable var: relatedVariables) {
			for(DepVariable rootVar: var.getRootVars()) {
				if(rootVar.getInstruction().getClassName().equals(defIns.getCalledCFG().getClassName()) && 
						rootVar.getInstruction().getMethodName().equals(defIns.getCalledCFG().getMethodName())) {
					if(var.getType() == DepVariable.STATIC_FIELD) {
						allLeafDepVars.add(var);					
					}
					else {
						ConstructionPath path = rootVar.findPath(var);
						if(path != null) {
							if(path.size() < 2) {
								System.currentTimeMillis();
								path = rootVar.findPath(var);
							}
							
							DepVariable secVar = path.getPath().get(1);
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
				}
			}
			
			allLeafDepVars.add(var);
		}
	}

	@SuppressWarnings("rawtypes")
	private List<DepVariable> buildInputOutputForInstruction(BytecodeInstruction defInstruction, MethodNode node, 
			DepVariable outputVar, ActualControlFlowGraph cfg, Set<DepVariable> allDepVars, Set<BytecodeInstruction> visitedIns) {
		List<DepVariable> intputVars = new ArrayList<DepVariable>();
		int operandNum = defInstruction.getOperandNum();
		for (int i = 0; i < operandNum; i++) {
			Frame frame = defInstruction.getFrame();
			int index = frame.getStackSize() - i - 1;
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
		
		return intputVars;
	}

	private BytecodeInstruction convert2BytecodeInstruction(ActualControlFlowGraph cfg, MethodNode node,
			AbstractInsnNode ins) {
		AbstractInsnNode condDefinition = (AbstractInsnNode)ins;
		BytecodeInstruction defIns = cfg.getInstruction(node.instructions.indexOf(condDefinition));
		return defIns;
	}

	public MethodNode getMethodNode(InstrumentingClassLoader classLoader, String className, String methodName) {
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
}
