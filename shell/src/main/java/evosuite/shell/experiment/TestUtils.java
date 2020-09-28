package evosuite.shell.experiment;

public class TestUtils {

	public static String baseDir = System.getProperty("user.dir");

	public static String getAbsolutePath(String relativePath) {
		return baseDir + "/" + relativePath;
	}
}
