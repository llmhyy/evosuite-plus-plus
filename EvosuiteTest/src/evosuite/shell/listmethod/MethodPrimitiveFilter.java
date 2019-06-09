package evosuite.shell.listmethod;

import evosuite.shell.utils.LoggerUtils;
import org.evosuite.classpath.ResourceList;
import org.evosuite.ga.metaheuristics.RuntimeRecord;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.utils.CommonUtility;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MethodPrimitiveFilter implements IMethodFilter {

    private static Logger log = LoggerUtils.getLogger(MethodPrimitiveFilter.class);

    @Override
    public List<String> listTestableMethods(Class<?> targetClass, ClassLoader classLoader) throws AnalyzerException,
            IOException {
        InputStream is = ResourceList.getInstance(classLoader).getClassAsStream(targetClass.getName());

        List<String> validMethods = new ArrayList<>();
        try {
            List<MethodNode> l = getMethodNodeList(is);

            for (MethodNode m : l) {
                String methodName = CommonUtility.getMethodName(m);
                if ((m.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
                    continue;
                }
                if ((m.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC
                        || (m.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED
                        || (m.access & Opcodes.ACC_PRIVATE) == 0) {

                    if (checkMethod(classLoader, targetClass.getName(), methodName, m)) {
                        validMethods.add(methodName);
                    }
                }
            }
        } finally {
            is.close();
        }
        return validMethods;
    }

    @SuppressWarnings("unchecked")
    private boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode methodNode) throws AnalyzerException, IOException {

        boolean hasBranch = false;
        boolean hasBranchInside = false;

        // All parameters are primitive type
        if (!checkParam(methodNode)) {
            return  false;
        }

        for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insnNode = it.next();

            // Has Branch
            if (isBranchInsn(insnNode)) {
                hasBranch = true;
            }

            // GETFIELD or GETSTATIC returns primitive
            if (!checkPrimField(insnNode)) {
                return false;
            }
        }

        if (hasBranch) {

            // CFG for the analyzed method
            ActualControlFlowGraph cfg = retrieveCFG(classLoader, className, methodName, methodNode);

            // Entry point of outer CFG
            BytecodeInstruction entryInstruction = cfg.getEntryPoint();

            while (entryInstruction.getNextInstruction() != null) {
                AbstractInsnNode insnNode = entryInstruction.getASMNode();

                // Check if INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE, INVOKEDYNAMIC
                if (CommonUtility.isInvokeMethodInsn(insnNode)) {

                    // The actual CFG of invoked method
                    ActualControlFlowGraph innerCFG = entryInstruction.getCalledActualCFG();
                    ActualControlFlowGraph innerActualCFG = null;

                    String innerClassName = entryInstruction.getCalledMethodsClass();
                    String innerMethodName = entryInstruction.getCalledMethodName();

                    String signature = innerClassName + "." + entryInstruction.getCalledMethod();
                    // Get number of available method
                    RuntimeRecord.methodCallAvailabilityMap.put(signature, innerCFG != null);

                    InputStream innerIS = ResourceList.getInstance(classLoader).getClassAsStream(innerClassName);
                    List<MethodNode> innerMethodList = getMethodNodeList(innerIS);

                    // Look for matched invoked method by methodInsnNode
                    for (MethodNode innerMethod : innerMethodList) {
                        if (innerMethodName.equals(innerMethod.desc)) {

                            // All parameters are primitive type
                            if (!checkParam(innerMethod)) {
                                return false;
                            }

                            innerActualCFG = retrieveCFG(classLoader, innerClassName, innerMethodName, innerMethod);
                            break;
                        }
                    }

                    BytecodeInstruction innerEntryInsn = innerActualCFG.getEntryPoint();
                    while (innerEntryInsn.getNextInstruction() != null) {
                        AbstractInsnNode innerNode = innerEntryInsn.getASMNode();

                        if (!hasBranchInside && isBranchInsn(innerNode)) {
                            hasBranchInside = true;
                        }
                        if (!checkPrimField(insnNode)) {
                            return false;
                        }
                        innerEntryInsn = innerEntryInsn.getNextInstruction(); // update
                    }
                }
                entryInstruction = entryInstruction.getNextInstruction(); // update
            }
        }
        return hasBranchInside;
    }

    @SuppressWarnings("unchecked")
    private List<MethodNode> getMethodNodeList(InputStream is) throws IOException {
        ClassReader reader = new ClassReader(is);
        ClassNode cn = new ClassNode();
        reader.accept(cn, ClassReader.SKIP_FRAMES);
        return cn.methods;
    }

    private ActualControlFlowGraph retrieveCFG(ClassLoader classLoader, String className, String methodName,
                                               MethodNode methodNode) throws AnalyzerException {
        ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);

        if (cfg == null) {
            BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
            bytecodeAnalyzer.analyze(classLoader, className, methodName, methodNode);
            bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
            cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
        }
        return cfg;
    }

    private boolean isPrimitiveType (Type type){
        return type.equals(Type.BYTE_TYPE) || type.equals(Type.CHAR_TYPE) || type.equals(Type.SHORT_TYPE) ||
                type.equals(Type.INT_TYPE) || type.equals(Type.LONG_TYPE) || type.equals(Type.FLOAT_TYPE) ||
                type.equals(Type.DOUBLE_TYPE) || type.equals(Type.BOOLEAN_TYPE) || type.toString().equals("Ljava/lang/String;");
    }

    private boolean isBranchInsn(AbstractInsnNode insnNode) {
        return insnNode instanceof JumpInsnNode;
    }

    private boolean checkPrimField(AbstractInsnNode insnNode) {
        if (insnNode.getOpcode() == Opcodes.GETSTATIC || insnNode.getOpcode() == Opcodes.GETFIELD) {
            Type type = Type.getType(((FieldInsnNode) insnNode).desc);
            return isPrimitiveType(type);
        }
        return true;
    }

    private boolean checkParam(MethodNode methodNode) {
        Type[] typeArgs = Type.getArgumentTypes(methodNode.desc);

        // No parameters
        if (typeArgs.length == 0) {
            return true;
        }

        else {
            for (Type type : typeArgs) {
                if (!isPrimitiveType(type)) {
                    return false;
                }
            }
        }
        return true;
    }

}
