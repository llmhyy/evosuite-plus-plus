package org.evosuite.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProgramArgumentUtils {
	
	public static boolean hasOpt(String[] args, String opt) throws Exception {
		for (int i = 0; i < args.length; i++) {
			if (opt.equals(args[i])) {
				return true;
			}
		}
		return false;
	}
	
	public static int indexOfOpt(String[] args, String opt) throws Exception {
		for (int i = 0; i < args.length; i++) {
			if (opt.equals(args[i])) {
				return i;
			}
		}
		return -1;
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
	
	public static String[] extractArgs(String[] args, Collection<String> excludedOpts) {
		List<String> newArgs = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			if (excludedOpts.contains(args[i])) {
				i++;
				while (i < args.length && !args[i].startsWith("-")) {
					i++;
				}
				i--;
				continue;
			}
			newArgs.add(args[i]);
		}
		return newArgs.toArray(new String[newArgs.size()]);
	}
}
