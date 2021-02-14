package utiltest;

import org.evosuite.TestGenerationContext;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

public class MethodUtilTest {
	
	@Test
	public void testSignatureParser1() {
		String sigDesc = "(Lempirical/IMenuItem;Lempirical/MenuItemList;)Lempirical/MenuItemList;";
		String[] inputArray = MethodUtil.parseSignature(sigDesc);
		
		assert inputArray.length ==3;
		assert inputArray[0].equals("empirical.IMenuItem");
		assert inputArray[1].equals("empirical.MenuItemList");
		assert inputArray[2].equals("empirical.MenuItemList");
	}
	
	@Test
	public void testSignatureParser2() {
		String sigDesc = "(Lempirical/IMenuItem;IZLempirical/MenuItemList;)V";
		String[] inputArray = MethodUtil.parseSignature(sigDesc);
		
		assert inputArray.length == 5;
		assert inputArray[0].equals("empirical.IMenuItem");
		assert inputArray[1].equals("int");
		assert inputArray[2].equals("boolean");
		assert inputArray[3].equals("empirical.MenuItemList");
		assert inputArray[4].equals("void");
	}
	
	@Test
	public void testSignatureParser3() {
		String sigDesc = "(JLempirical/IMenuItem;IILempirical/MenuItemList;)D";
		String[] inputArray = MethodUtil.parseSignature(sigDesc);
		
		assert inputArray.length == 6;
		assert inputArray[0].equals("long");
		assert inputArray[1].equals("empirical.IMenuItem");
		assert inputArray[2].equals("int");
		assert inputArray[3].equals("int");
		assert inputArray[4].equals("empirical.MenuItemList");
		assert inputArray[5].equals("double");
	}
	
	@Test
	public void testSignatureParser4() {
		String sigDesc = "(JLempirical/IMenuItem;[II[Lempirical/MenuItemList;)D";
		String[] inputArray = MethodUtil.parseSignature(sigDesc);
		
		assert inputArray.length == 6;
		assert inputArray[0].equals("long");
		assert inputArray[1].equals("empirical.IMenuItem");
		assert inputArray[2].equals("int[]");
		assert inputArray[3].equals("int");
		assert inputArray[4].equals("empirical.MenuItemList[]");
		assert inputArray[5].equals("double");
	}
	
	@Test
	public void testSignatureParser5() {
		String sigDesc = "([JLempirical/IMenuItem;[[II[Lempirical/MenuItemList;)D";
		String[] inputArray = MethodUtil.parseSignature(sigDesc);
		
		assert inputArray.length == 6;
		assert inputArray[0].equals("long[]");
		assert inputArray[1].equals("empirical.IMenuItem");
		assert inputArray[2].equals("int[][]");
		assert inputArray[3].equals("int");
		assert inputArray[4].equals("empirical.MenuItemList[]");
		assert inputArray[5].equals("double");
	}
	
	@Test
	public void testSignatureParser6() {
		String sigDesc = "()V";
		String[] inputArray = MethodUtil.parseSignature(sigDesc);
		
		assert inputArray.length == 2;
		assert inputArray[0].equals("void");
		assert inputArray[1].equals("void");
	}
	
	@Test
	public void testEstimateInformationSensitivity() {
		
		String inputType = "java.util.ArrayList[]";
		String outputType = "java.util.ArrayList";
		
		double score = ComputationPath.estimateInformationSensitivity(inputType, outputType);
		assert score > 0.5;
	}
}
