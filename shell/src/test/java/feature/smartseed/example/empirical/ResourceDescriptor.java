package feature.smartseed.example.empirical;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class ResourceDescriptor extends AbstractResourceDescriptor{
	private final File source;
	private final String[] destination;
	private final TreeMap<String, ResourceDescriptor> subResources = new TreeMap<String, ResourceDescriptor>();
	 private final TreeMap<String, ResourcesDirectory> subdirs = new TreeMap<String, ResourcesDirectory>();

		public ResourceDescriptor(File source, String destination) {
			super(destination.replaceAll("^.*/", ""));
			this.source = source;
			if (!destination.matches("(/[a-zA-Z0-9_\\-\\.]+)+")) {
				throw new AssertionError("Bad resource destination: " + destination);
			} else {
				StringTokenizer st = new StringTokenizer(destination, "/");
				this.destination = new String[st.countTokens()];
				for (int i = 0; st.hasMoreTokens(); ++i) {
					this.destination[i] = st.nextToken();
				}
			}
		}

		public File getSource() {
			return source;
		}

		public String[] getDestination() {
			return this.destination;
		}

	   public boolean contains(ResourceDescriptor resource) {
		    return contains(resource, new LinkedList<String>(Arrays.asList(resource.getDestination())));
	   }
		
		public boolean contains(ResourceDescriptor resource, LinkedList<String> path) {
			if (path.size() == 1) {

				String resourceName = path.getFirst();

				if (this.subdirs.containsKey(resourceName))
					throw new AssertionError(
							"A resource and a folder have the same destination: " + resource.getDestination());

				return this.subResources.containsKey(resourceName);

			}
			String nextFolder = path.removeFirst();

			if (!this.subdirs.containsKey(nextFolder))
				return false;

			return ((ResourcesDirectory)this.subdirs.get(nextFolder)).contains(resource, path);
		}
	}
