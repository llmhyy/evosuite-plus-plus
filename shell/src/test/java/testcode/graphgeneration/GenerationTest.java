package testcode.graphgeneration;

import org.junit.Test;
import testcode.graphgeneration.model.ClassModel;
import testcode.graphgeneration.model.GeneratedCodeUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GenerationTest {
//	public static final String ROOT_FOLDER = System.getProperty("user.dir");
//	public static final String ROOT_FOLDER = "D:\\Github\\generated-code\\";
	public static final String ROOT_FOLDER = "";
//	public static final String CODE_ABSOLUTE_PATH = Paths.get(ROOT_FOLDER).resolve("src").resolve("main").resolve("java").resolve("test").toString();
	public static final String CODE_ABSOLUTE_PATH = "/Users/xucaiyi/Documents/EvoObj-EvoSuite-comparison-testrun/TestGenerationResult/generated-code/src/main/java/test";
	public static final String TARGET_METHOD_FILENAME = "targetMethods.txt";
	
	public static final int MINIMUM_DEPTH = 5;
	public static final int MAXIMUM_DEPTH = 20;
	public static final int MINIMUM_WIDTH = 6;
	public static final int MAXIMUM_WIDTH = 20;

	private static String getGraphVizPath() {
//		return "D:" + File.separator + "linyun" + File.separator + "graph";
		return "/Users/xucaiyi/Documents/EvoObj-EvoSuite-comparison-testrun/TestGenerationResult/graph";
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
		generateAndCompile(5, 6, new Random().nextLong());
	}
	
	@Test
	public void massValidateCodeSyntax() throws IOException {
		for (int depth = 5; depth < 20; depth++) {
			for (int width = 5; width < 20; width++) {
				for (int i = 0; i < 10; i++) {
					generateAndCompile(depth, width, new Random().nextLong());
				}
			}
		}
	}
	
	@Test
	public void massGenerateMethods() throws IOException {
		int numberOfMethods = 30000;
		
		for (int i = 0; i < numberOfMethods; i++) {
			int depth = new Random().nextInt(MAXIMUM_DEPTH - MINIMUM_DEPTH) + MINIMUM_DEPTH; 
			int width = new Random().nextInt(MAXIMUM_WIDTH - MINIMUM_WIDTH) + MINIMUM_WIDTH;
			generateAndCompile(depth, width, new Random().nextLong());
		}
		
		Runtime.getRuntime().exec("shutdown /s /f /t: 0");
	}
	
	private static void generateAndCompile(int depth, int width, long seed) throws IOException {
		RandomNumberGenerator.setSeed(seed);
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		String folderName = Long.toString(RandomNumberGenerator.getSeed()).replace("-", "_") + "L";
		graph.labelNodeType();
		graph.labelAccessibility();
		graph.visualize(1000, Paths.get(CODE_ABSOLUTE_PATH).resolve(folderName).toString(), "graph");
		GeneratedCodeUnit generatedCodeUnit = graph.transformToCode();
		Map<String, String> fileNameToSourceCode = generatedCodeUnit.getFilenameToSourceCode();
		String targetMethodSignature = generatedCodeUnit.getTargetMethodSignature();
		
		writeToFiles(fileNameToSourceCode, folderName);
		writeSignatureToSignatureFile(targetMethodSignature);
		String outputPrefix = "[" + folderName + ", depth = " + depth + ", width = " + width + "]: ";
		compile(folderName, outputPrefix);
	}
	
	private static void writeSignatureToSignatureFile(String signature) throws IOException {
		Path path = Paths.get(ROOT_FOLDER).resolve(TARGET_METHOD_FILENAME);
		File signatureFile = path.toFile();
		if (!signatureFile.exists()) {
			signatureFile.createNewFile();
			StringBuilder headerBuilder = new StringBuilder();
			headerBuilder.append("#------------------------------------------------------------------------");
			headerBuilder.append(System.lineSeparator());
			headerBuilder.append("#Project=generatedcode  -   0_generatedcode");
			headerBuilder.append(System.lineSeparator());
			headerBuilder.append("#------------------------------------------------------------------------");
			headerBuilder.append(System.lineSeparator());
			Files.write(path, headerBuilder.toString().getBytes(StandardCharsets.UTF_8));
		}
		
		signature = signature + System.lineSeparator();
		Files.write(path, signature.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
	}
	
	private static void compile(String folderName, String outputPrefix) throws IOException {
		String compileCommand = "javac " + CODE_ABSOLUTE_PATH + File.separator + folderName + File.separator + "*.java";
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(compileCommand);
		BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String stderrOutput = null;
		while ((stderrOutput = stderr.readLine()) != null ) {
			System.err.println(outputPrefix + stderrOutput);
		}
	}
	
	private static List<File> writeToFiles(Map<String, String> fileNameToSourceCode, String folderName) throws IOException {
		List<File> files = new ArrayList<>();
		Path testFolderPath = Paths.get(CODE_ABSOLUTE_PATH).resolve(folderName.replace("-", "_"));
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
