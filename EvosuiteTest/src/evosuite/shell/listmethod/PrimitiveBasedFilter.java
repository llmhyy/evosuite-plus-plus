package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
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
 * 2.For GETSTATIC and GETFIELD instruction in method, corresponding variable is primitive
 *
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

		// Get actual CFG for target method
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		if (cfg == null) {
			BytecodeInstructionPool.getInstance(classLoader).registerMethodNode(node, className, node.name + node.desc);
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		}

		Set<BytecodeInstruction> insList = cfg.getRawGraph().vertexSet();

		if (FilterHelper.containNonPrimitiveParameter(node.desc)) {
			return false;
		}

		for (BytecodeInstruction ins : insList) {
			if (ins.getASMNode().getOpcode() == Opcodes.GETSTATIC || ins.getASMNode().getOpcode() == Opcodes.GETFIELD) {
				FieldInsnNode fieldNode = ((FieldInsnNode) ins.getASMNode());
				String desc = fieldNode.desc;
				if (desc.length() != 1 || !desc.equals("Ljava/lang/String")) {
					if (desc.contains("[")) {
						String element = desc.substring(desc.indexOf("[") + 1);
						if (element.length() != 1 && !element.equals("Ljava/lang/String;")) {
							return false;
						}
					} else {
						return false;
					}
				}
			}
		}
		return true;
	}
}
