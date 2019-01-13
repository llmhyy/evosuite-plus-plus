package com.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class FilterConfiguration {
	private Set<String> exclusives = new HashSet<>();
	
	public FilterConfiguration(String exclusivesFiles) throws IOException {
		File file = new File(exclusivesFiles);
		if (!file.exists()) {
			return;
		}
		List<String> lines = FileUtils.readLines(file , Charset.defaultCharset());
		for (String line : lines) {
			if (!line.trim().isEmpty() && !line.startsWith("#")) {
				exclusives.add(line);
			}
		}
	}

	public boolean contains(String projectName) {
		return exclusives.contains(projectName);
	}
}
