package evosuite.shell;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.evosuite.utils.CollectionUtil;
/**
 * 
 * @author thilyly_tran
 *
 */
public class FilterConfiguration {
	private List<Filter> filters;
	
	public FilterConfiguration(String inclusiveFile, Set<String> succeedMethods) throws Exception {
		filters = new ArrayList<FilterConfiguration.Filter>();
		if (CollectionUtil.isNotEmpty(succeedMethods)) {
			filters.add(new ExclusiveFilter(succeedMethods));
		}
		
		if (StringUtils.isNotEmpty(inclusiveFile)) {
			filters.add(new InclusiveFilter(inclusiveFile));
		}
	}

	public boolean isValidProject(String projectName) {
		for (Filter filter : filters) {
			if (!filter.isValidProject(projectName)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isValidElementId(String projectName, String elementId) {
		for (Filter filter : filters) {
			if (!filter.isValidElementId(projectName, elementId)) {
				return false;
			}
		}
		return true;
	}
	
	public static interface Filter {

		boolean isValidProject(String projectName);

		boolean isValidElementId(String projectName, String elementId);
	}
	
	public static class EmptyFilter implements Filter {

		@Override
		public boolean isValidProject(String projectName) {
			return true;
		}

		@Override
		public boolean isValidElementId(String projectName, String elementId) {
			return true;
		}
		
	}
	
	private static class ExclusiveFilter implements Filter {
		private Set<String> exclusives = new HashSet<>();
		
		public ExclusiveFilter(Set<String> exclusiveSet) {
			this.exclusives = exclusiveSet;
		}
		
		@Override
		public boolean isValidProject(String projectName) {
			return true;
		}

		@Override
		public boolean isValidElementId(String projectName, String elementId) {
			return !exclusives.contains(elementId);
		}

	}
	
	/**
	 * format of inclusive file: <p>
	 *  		#Project=[your_project_name] <p>
	 *  		[your_method_name_with_this_format: className#methodNameWithSignature]<p>
	 *  		# all_text_after_this_will_be_consider_as_comment <p>
	 *  ex: <p>
	 *  #------------------------------------------------------------------------<p>
	 *	#Project=fooProject<p>
	 *	#------------------------------------------------------------------------<p>
	 *	com.FOO#bar()V<p>
	 * @author thilyly_tran
	 *
	 */
	public static class InclusiveFilter implements Filter {
		private Map<String, Set<String>> inclusives = new HashMap<>();
		
		public InclusiveFilter(String inclFile) throws IOException {
			List<String> lines = FileUtils.readLines(new File(inclFile), Charset.defaultCharset());
			String curProject = null;
			for (String line : lines) {
				if (line.toLowerCase().startsWith("#project=")) {
					curProject = line.split("=")[1].split(" ")[0];
				} else {
					if (!line.startsWith("#")) {
						CollectionUtil.getSetInitIfEmpty(inclusives, curProject).add(line);
					}
				}
			}
		}

		@Override
		public boolean isValidProject(String projectName) {
			return inclusives.containsKey(projectName);
		}

		@Override
		public boolean isValidElementId(String projectName, String elementId) {
			return inclusives.get(projectName).contains(elementId);
		}
		
	}
}
