package evosuite.shell;

import java.io.File;
import java.io.FilenameFilter;

class ProjectNameFileter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		File f = new File(name);
		if (f.isDirectory() && name.contains("_")) {
			String index = name.substring(0, name.indexOf("_"));
			return isNumeric(index);

		}
		return false;
	}
	
	public static boolean isNumeric(String strNum) {
	    return strNum.matches("-?\\d+(\\.\\d+)?");
	}

}