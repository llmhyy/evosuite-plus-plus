package evosuite.shell.experiment;

import java.io.File;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;

public class EvoTestSingleMethod {
	
	String fitnessAppraoch = "branch";

	@Before
	public void setup() {
		SFConfiguration.sfBenchmarkFolder = "E:\\linyun\\git_space\\SF100-clean";
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	@Test
	public void runTullibee() {
		String projectId = "1_tullibee";
		String projectName = "tullibee";
		String[] targetMethods = new String[]{
//				"com.ib.client.EClientSocket#placeOrder(ILcom/ib/client/Contract;Lcom/ib/client/Order;)V",
				"com.ib.client.EClientSocket#reqNewsBulletins(Z)V"
				
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		fitnessAppraoch = "fbranch";
		for (int i = 0; i < 3; i++) {
			FileUtils.deleteFolder(new File(FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, "evosuite-tests")));
			FileUtils.deleteFolder(new File(FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, "evosuite-report")));
			evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
			System.out.println("i=" + i);
		}
	}
	
	@Test
	public void runA4j() {
		String projectId = "2_a4j";
		String projectName = "a4j";
		String[] targetMethods = new String[]{
					"net.kencochrane.a4j.file.FileUtil#renameFile(Ljava/lang/String;Ljava/lang/String;)V",
					"net.kencochrane.a4j.file.FileUtil#getBrowseNodeFile(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/io/File;"
				};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	@Test
	public void jigen() {
		String projectId = "3_jigen";
		String projectName = "jigen";
		String[] targetMethods = new String[]{
//				"com.jigen.ConfigFileGenerator#generate()Ljava/io/File;",
//				"com.jigen.gui.JLink#paint(Ljava/awt/Graphics;)V"
				"com.jigen.XmlReader#parseJigenDocument(Ljava/io/File;)Lcom/jigen/xsd/JigenDocument;"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	@Test
	public void rif() {
		String projectId = "4_rif";
		String projectName = "rif";
		String[] targetMethods = new String[]{
				"org.apache.axis2.transport.http.turnup.server.SimpleHttpServer#destroy()V",
				"org.apache.axis2.transport.http.turnup.server.SimpleHttpServerConnection#writeRequest(Lorg/apache/axis2/transport/http/server/SimpleRequest;)V"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	@Test
	public void templateit() {
		String projectId = "5_templateit";
		String projectName = "templateit";
		String[] targetMethods = new String[]{
				"org.apache.poi.hssf.usermodel.HSSFDataFormat#getBuiltinFormat(Ljava/lang/String;)S"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	@Test
	public void jnfe() {
		String projectId = "6_jnfe";
		String projectName = "jnfe";
		String[] targetMethods = new String[]{
//				"br.com.jnfe.base.adapter.AbstractNFeAdaptadorBean#afterPropertiesSet()V",
//				"br.com.jnfe.pl005d.JNFeAdaptadorImpl#preencheInfo(Lbr/com/jnfe/core/JNFe;Ljavax/xml/stream/XMLStreamWriter;)V"
				"br.com.jnfe.core.JNFeICMS#getVCredICMSSN()Ljava/math/BigDecimal;"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	@Test
	public void sfmis() {
		String projectId = "7_sfmis";
		String projectName = "sfmis";
		String[] targetMethods = new String[]{
				"com.hf.sfm.util.Loader#getArrayResults()Ljava/lang/String;"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	@Test
	public void gfarcegestionfa() {
		String projectId = "8_gfarcegestionfa";
		String projectName = "gfarcegestionfa";
		String[] targetMethods = new String[]{
				"fr.unice.gfarce.interGraph.ModifTableStockage#separerColonne(I)Lfr/unice/gfarce/interGraph/TableStockage;"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}

	@Test
	public void watersimulator() {
		String projectId = "10_water-simulator";
		String projectName = "water-simulator";
		String[] targetMethods = new String[]{
				"simulator.util.ParameterAttributes#valueFor(F)F"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	@Test
	public void imsmart() {
		String projectId = "11_imsmart";
		String projectName = "imsmart";
		String[] targetMethods = new String[]{
				"com.momed.parser.MParserFactory#getParser()Lcom/momed/parser/MParser;"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	@Test
	public void dsachat() {
		String projectId = "12_dsachat";
		String projectName = "dsachat";
		String[] targetMethods = new String[]{
//				"dsachat.gm.gui.MultiHeroTreeModel#getIndexOfChild(Ljava/lang/Object;Ljava/lang/Object;)I"
				"dsachat.client.gui.SingleHeroTreeModel#getChild(Ljava/lang/Object;I)Ljava/lang/Object;"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	@Test
	public void jdbacl() {
		String projectId = "13_jdbacl";
		String projectName = "jdbacl";
		String[] targetMethods = new String[]{
				"org.databene.jdbacl.ResultSetIterator#close()V"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	@Test
	public void calorieCount() {
		String projectId = "78_caloriecount";
		String projectName = "caloriecount";
		String[] targetMethods = new String[]{
				"com.lts.util.prop.PropertiesUtil#findRef(Ljava/util/Properties;)Lcom/lts/util/prop/PropertiesUtil$PropertyRef;"
		};
		
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
	}
	
	public void evoTestSingleMethod(String projectId, String projectName,
			String[] targetMethods, String fitnessAppraoch) {
		/* configure */
	
		/* run */
		File file = new File(SFConfiguration.sfBenchmarkFolder + "/tempInclusives.txt");
		file.deleteOnExit();
		SFBenchmarkUtils.writeInclusiveFile(file, false, projectName, targetMethods);

		long seconds = 90;
		boolean instrumentContext = true;
		String[] args = new String[] {
				"-criterion", fitnessAppraoch,
				"-target", FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, projectName + ".jar"),
				"-inclusiveFile", file.getAbsolutePath(),
//				"-Djunit_check", "false"
////				"-generateRandom",
//				"-generateSuite",
//				// "-generateMOSuite",
////				"-generateSuiteUsingDSE",
////				"-Dstrategy", "random",
////				"-class", targetClass, 
////				"-projectCP", cp, //;lib/commons-math-2.2.jar
////				"-setup", "bin", "lib/commons-math-2.2.jar",
////				"-Dtarget_method", targetMethod, 
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.1",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.1",
//				"-Dlocal_search_rate", "3",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "5",
				"-Dmax_initial_tests", "30",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
				"-Dmax_length", "1",
				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", "100"
				
		};
		SFBenchmarkUtils.setupProjectProperties(projectId);
		EvosuiteForMethod.main(args);
	}
	
	public void evoSuiteSingleMethod() {
		String projectId = "1_tullibee";
		SFBenchmarkUtils.setupProjectProperties(projectId);
	}
}
