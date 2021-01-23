package feature.smartseed.example.empirical;

import java.util.LinkedList;
import java.util.TreeMap;

public class ResourcesDirectory {
	private String folderName;
	private final TreeMap<String, ResourceDescriptor> subResources = new TreeMap();
    private final TreeMap<String, ResourcesDirectory> subdirs = new TreeMap();

	public ResourcesDirectory(String folderName)
	 {
		 this.folderName = folderName;
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
}
