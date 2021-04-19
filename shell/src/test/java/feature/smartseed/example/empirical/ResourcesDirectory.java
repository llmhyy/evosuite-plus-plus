package feature.smartseed.example.empirical;

import java.util.LinkedList;
import java.util.TreeMap;

public class ResourcesDirectory {
	private String folderName;
	private TreeMap<String, ResourceDescriptor> subResources = new TreeMap<String, ResourceDescriptor>();
    private TreeMap<String, ResourcesDirectory> subdirs = new TreeMap<String, ResourcesDirectory>();

	public ResourcesDirectory(String folderName)
	 {
		 this.setFolderName(folderName);
	}
	
//	public void addResource(ResourceDescriptor resource)
//	{
//		addResource(resource, new LinkedList((Collection)Arrays.asList(resource.getDestination())));
//		}
	
	//3 addResource
	void addResource(ResourceDescriptor resource, LinkedList<String> path)
    {
      if (path.size() == 1)
      {
        String resourceName = (String)path.getFirst();
  
        if (subdirs.containsKey(resourceName)) {
          throw new AssertionError("A resource and a folder have the same destination: " + resource.getDestination());
        }
        if (subResources.containsKey(resourceName)) {
          throw new AssertionError("Two resources have the same destination: " + resource.getDestination());
        }
        subResources.put(resourceName, resource);
      }
      else
      {
        String nextFolder = (String)path.removeFirst();
  
        if (!subdirs.containsKey(nextFolder))
        {
          if (subResources.containsKey(nextFolder)) {
            throw new AssertionError("A resource and a folder have the same destination: " + resource.getDestination());
          }
          subdirs.put(nextFolder, new ResourcesDirectory(nextFolder));
  
        }
        ((ResourcesDirectory)subdirs.get(nextFolder)).addResource(resource, path);
      }
    }

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	public TreeMap<String, ResourceDescriptor> getSubResources() {
		return subResources;
	}

	public void setSubResources(TreeMap<String, ResourceDescriptor> subResources) {
		this.subResources = subResources;
	}
	
	public TreeMap<String, ResourcesDirectory> getSubdirs() {
		return subdirs;
	}

	public void setSubdirs(TreeMap<String, ResourcesDirectory> subdirs) {
		this.subdirs = subdirs;
	}
}
