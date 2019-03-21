package instrumenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.instrumentation.BytecodeInstrumentation;
import org.evosuite.runtime.RuntimeSettings;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author LLT
 *
 */
public class InstrumenterTestForRealCode {
	private static final String INSTRUMENT_TARGET_FOLDER;
	
	static {
		INSTRUMENT_TARGET_FOLDER = System.getProperty("user.dir") + "/instr_result";
	}

	@Test
	public void writeFile() throws Exception {
		String className = Sample.class.getName();
		String classPath = className.replace(".", "/") + ".class";
		String clazzFile = new StringBuilder("/").append(classPath).toString();

		File outFile = getFile(INSTRUMENT_TARGET_FOLDER, clazzFile);
		FileOutputStream out = new FileOutputStream(outFile);
		System.out.println(outFile.getAbsolutePath());
		
		File inFile = new File("D:/_1_Projects/evosuite/experiment/colt/cern/jet/random/Gamma.class");
		FileInputStream in = new FileInputStream(inFile);
		className = "cern/jet/random/Gamma";
		byte[] data = new byte[100000];
		in.read(data);
		data = instrument1(data, className, "D:/_1_Projects/evosuite/experiment/4Projects-clean/102_colt/colt.jar;D:/_1_Projects/evosuite/experiment/4Projects-clean/102_colt/lib/concurrent.jar;D:/_1_Projects/evosuite/experiment/4Projects-clean/102_colt/lib/junit.jar;D:/_1_Projects/evosuite/experiment/4Projects-clean/102_colt/lib/org.hamcrest.core_1.3.0.v201303031735.jar");
		out.write(data);
		out.close();
		in.close();
	}

	private File getFile(String folder, String fileName) throws Exception {
		File file = new File(folder + fileName);
		evosuite.shell.FileUtils.getFileCreateIfNotExist(file.getPath());
		return file;
	}
	
	private byte[] instrument1(byte[] data, String className, String cp) throws Exception {
		RuntimeSettings.activateAllMocking();
		ClassReader reader = new ClassReader(data);
		BytecodeInstrumentation instrm = new BytecodeInstrumentation();
		return instrm.transformBytes(getClassLoader(cp), className, reader);
	}
	
	private URLClassLoader getClassLoader(String classpath) {
		List<URL> urls = new ArrayList<>();
		for (String classPathElement : classpath.split(File.pathSeparator)) {
			try {
				File f = new File(classPathElement);
				urls.add(f.toURI().toURL());
			} catch (IOException e) {
			}
		}
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
	}

	private byte[] instrument(byte[] data, String className) throws Exception {
		ClassReader reader = new ClassReader(data);
		int asmFlags = ClassWriter.COMPUTE_FRAMES;
		int readFlags = ClassReader.SKIP_FRAMES;

		ClassWriter writer = new ClassWriter(asmFlags);
		
		ClassVisitor cv = writer;
		cv = new Instrumenter(cv, className);
		ClassNode cn = new ClassNode();
		reader.accept(cn, readFlags);
		
		cn.accept(cv);
		return writer.toByteArray();
	}
}
