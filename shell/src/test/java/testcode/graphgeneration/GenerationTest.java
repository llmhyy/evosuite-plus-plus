package testcode.graphgeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import testcode.graphgeneration.model.ClassModel;

public class GenerationTest {
	public static final String CODE_FOLDER = "generated-code";
	public static final String CODE_ABSOLUTE_PATH = Paths.get(System.getProperty("user.dir")).resolve(CODE_FOLDER).toString();
	
	private static String getGraphVizPath() {
		return "D:" + File.separator + "linyun" + File.separator + "graph";
	}

	private static String getSeedSpecificPath(long seed) {
		return System.getProperty("user.dir") + File.separator + "generated-code" + File.separator + Long.toString(seed) + "L";
	}
	@Test
	public void testStep1() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.visualize(1000, getGraphVizPath(), "graph");
	}
	
	
	@Test
	public void testStep2() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.visualize(1000, getGraphVizPath(), "graph");
	}

	@Test
	public void testStep3() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.labelAccessibility();
		graph.visualize(1000, getGraphVizPath(), "graph");
	}
	
	@Test
	public void testStep4() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.labelAccessibility();
		graph.transformToCode();
		graph.visualize(1000, getGraphVizPath(), "graph");
	}
	
	@Test
	public void testClassModelGeneration() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.labelAccessibility();
		ClassModel classModel = new ClassModel(graph);
	}
	
	@Test
	public void testCodeGeneration() {
//		long seed = -2220534647818254570L;
//		OCGGenerator generator = new OCGGenerator(seed); // Specific seed
		OCGGenerator generator = new OCGGenerator(); // Random seed
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.labelAccessibility();
		graph.visualize(1000, getGraphVizPath(), "graph");
		graph.transformToCode();
	}
	
	@Test
	public void validateCodeSyntax() throws IOException {
//		RandomNumberGenerator.setSeed(-975324895466187908L);
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.labelAccessibility();
		graph.visualize(1000, getGraphVizPath(), "graph");
		Map<String, String> fileNameToSourceCode = graph.transformToCode();
		
		String folderName = Long.toString(RandomNumberGenerator.getSeed()) + "L";
		writeToFiles(fileNameToSourceCode, folderName);
		compile(folderName);
	}
	
	@Test
	public void massValidateCodeSyntax() throws IOException {
		for (int i = 0; i < 100; i++) {
			RandomNumberGenerator.setSeed(new Random().nextLong());
			OCGGenerator generator = new OCGGenerator();
			Graph graph = generator.generateGraph(5, 6, false);
			String folderName = Long.toString(RandomNumberGenerator.getSeed()) + "L";
			graph.labelNodeType();
			graph.labelAccessibility();
			graph.visualize(1000, getSeedSpecificPath(RandomNumberGenerator.getSeed()), "graph");
			Map<String, String> fileNameToSourceCode = graph.transformToCode();
			
			
			writeToFiles(fileNameToSourceCode, folderName);
			compile(folderName);
		}
	}
	
	private static void compile(String folderName) throws IOException {
		String compileCommand = "javac " + CODE_ABSOLUTE_PATH + File.separator + folderName + File.separator + "*.java";
		Runtime runtime = Runtime.getRuntime();
		System.out.println("Output of " + compileCommand);
		Process process = runtime.exec(compileCommand);
		BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String stderrOutput = null;
		while ((stderrOutput = stderr.readLine()) != null ) {
			System.err.println(stderrOutput);
		}
	}
	
	private static List<File> writeToFiles(Map<String, String> fileNameToSourceCode, String folderName) throws IOException {
		List<File> files = new ArrayList<>();
		Path rootFolder = Paths.get(System.getProperty("user.dir"));
		Path testFolderPath = rootFolder.resolve("generated-code").resolve(folderName);
		File testFolder = testFolderPath.toFile();
		if (!testFolder.exists()) {
			testFolder.mkdir();
		}
		
		for (Map.Entry<String, String> fileNameAndSourceCode : fileNameToSourceCode.entrySet()) {
			String fileName = fileNameAndSourceCode.getKey();
			String sourceCode = fileNameAndSourceCode.getValue();
			
			Path absoluteFileName = testFolderPath.resolve(fileName);
			File file = absoluteFileName.toFile();
			if (!file.exists()) {
				file.createNewFile();	
			}
			Files.write(absoluteFileName, sourceCode.getBytes(StandardCharsets.UTF_8));
			files.add(file);
		}
		
		return files;
	}
	
	
}
