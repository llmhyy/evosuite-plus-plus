package evosuite.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	
	public static String getFilePath(String... fragments) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fragments.length - 1; i++) {
			sb.append(fragments[i]).append(File.separator);
		}
		sb.append(fragments[fragments.length - 1]);
		return sb.toString();
	}
	
	public static File newFile(String... fragments) {
		return new File(getFilePath(fragments));
	}
	
	public static File createFolder(String folderPath) {
		File folder = new File(folderPath);
		if (folder.exists()) {
			if (folder.isDirectory()) {
				return folder;
			}
			throw new RuntimeException(String.format("Path %s is not a folder!", folderPath));
		}
		folder.mkdirs();
		return folder;
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
	
	public static List<String> toFilePath(List<File> files) {
		List<String> paths = new ArrayList<>(files.size());
		for (File file : files) {
			paths.add(file.getAbsolutePath());
		}
		return paths;
	}
}
