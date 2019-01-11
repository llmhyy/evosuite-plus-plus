package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	
	public static String getFilePath(String... fragments) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fragments.length - 1; i++) {
			sb.append(fragments[i]).append(File.separator);
		}
		sb.append(fragments[fragments.length - 1]);
		return sb.toString();
	}
	
	public static void writeFile(String fileName, String content, boolean append) {
		File file = getFileCreateIfNotExist(fileName);
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(file, append);
			stream.write(content.getBytes());
			stream.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static File getFileCreateIfNotExist(String path) {
		File file = new File(path);
		if (!file.exists()) {
			File folder = file.getParentFile();
			if (!folder.exists()) {
				folder.mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return file;
	}
}
