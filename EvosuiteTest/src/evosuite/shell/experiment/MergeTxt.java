package evosuite.shell.experiment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.CollectionUtil;
import org.junit.Before;

public class MergeTxt {

	@Before
	public void setup() {
		SFConfiguration.sfBenchmarkFolder = "E:/lyly/Projects/evosuite/experiment/SF100-unittest";
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
	}
	
	public static void main(String[] args) throws IOException {
		String root = SFConfiguration.sfBenchmarkFolder + "/evoTest-reports";
		List<String> inclusiveFiles = Arrays.asList(root + "/flagFilteredMethods.txt");
		List<String> exclusivesFiles = Arrays.asList(root + "/executed_methods.txt");
		
		String resultTxt = root + "/merge.txt";
		merge(inclusiveFiles, exclusivesFiles, resultTxt);
	}

	private static void merge(List<String> inclusiveFiles, List<String> exclusivesFiles, String resultTxt)
			throws IOException {
		MergeTxt mergeTxt = new MergeTxt();
		Map<String, Set<String>> inclusives = new HashMap<>();
		for (String file : inclusiveFiles) {
			inclusives.putAll(mergeTxt.readData(file));
		}
		Map<String, Set<String>> exclusives = new HashMap<>();
		for (String file : exclusivesFiles) {
			exclusives.putAll(mergeTxt.readData(file));
		}

		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		for (String projectName : projectFolders.keySet()) {
			String projectId = projectFolders.get(projectName).getName();
			List<String> remainMethods = new ArrayList<>(CollectionUtil.nullToEmpty(inclusives.get(projectName)));
			remainMethods.removeAll(CollectionUtil.nullToEmpty(exclusives.get(projectName)));
			StringBuilder sb = new StringBuilder()
						.append("#------------------------------------------------------------------------\n")
						.append("#Project=").append(projectName).append("  -  ").append(projectId).append("\n")
						.append("#------------------------------------------------------------------------\n");
			for (String method : remainMethods) {
				sb.append(method).append("\n");
			}
			evosuite.shell.FileUtils.writeFile(resultTxt, sb.toString(), true);
		}
		System.out.println("Done!");
	}
	
	private Map<String, Set<String>> readData(String file) throws IOException {
		Map<String, Set<String>> content = new LinkedHashMap<>();
		List<String> lines = FileUtils.readLines(new File(file), Charset.defaultCharset());
		String curProject = null;
		for (String line : lines) {
			if (line.toLowerCase().startsWith("#project=")) {
				curProject = line.split("=")[1].split(" ")[0];
			} else {
				if (!line.startsWith("#")) {
					CollectionUtil.getSetInitIfEmpty(content, curProject).add(line);
				}
			}
		}
		return content;
	}
	
}
