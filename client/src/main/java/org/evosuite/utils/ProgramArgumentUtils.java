package org.evosuite.utils;

public class ProgramArgumentUtils {
	
	public static boolean hasOpt(String[] args, String opt) throws Exception {
		for (int i = 0; i < args.length; i++) {
			if (opt.equals(args[i])) {
				return true;
			}
		}
		return false;
	}
	
	public static String getOptValue(String[] args, String opt) throws Exception {
		for (int i = 0; i < args.length; i++) {
			if (opt.equals(args[i])) {
				if (i == args.length - 1 || args[i + 1].startsWith("-")) {
					return "";
				}
				return args[i + 1];
			}
		}
		return null;
	}
}
