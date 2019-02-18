package instrumenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author LLT
 *
 */
public class InstrumenterTest {
	private static final String CLASS_FOLDER;
	private static final String INSTRUMENT_TARGET_FOLDER;
	
	static {
		CLASS_FOLDER = System.getProperty("user.dir") + "/target/classes";
		INSTRUMENT_TARGET_FOLDER = System.getProperty("user.dir") + "/instr_result";
	}

	@Test
	public void writeFile() throws Exception {
		String className = Sample.class.getName();
		String classFolder = CLASS_FOLDER;
		String classPath = className.replace(".", "/") + ".class";
		String clazzFile = new StringBuilder("/").append(classPath).toString();

		File outFile = getFile(INSTRUMENT_TARGET_FOLDER, clazzFile);
		FileOutputStream out = new FileOutputStream(outFile);
		System.out.println(outFile.getAbsolutePath());
		
		File inFile = new File(classFolder + clazzFile);
		FileInputStream in = new FileInputStream(inFile);

		byte[] data = new byte[100000];
		in.read(data);
		data = instrument(data, className);
		out.write(data);
		out.close();
		in.close();
	}

	private File getFile(String folder, String fileName) throws Exception {
		File file = new File(folder + fileName);
		evosuite.shell.FileUtils.getFileCreateIfNotExist(file.getPath());
		return file;
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
