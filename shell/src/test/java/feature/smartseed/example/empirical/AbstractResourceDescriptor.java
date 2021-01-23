package feature.smartseed.example.empirical;

import java.util.LinkedList;

public abstract class AbstractResourceDescriptor {
//	static final MessageDigest messageDigest;
	private static int resourceCounter;
	private final String resourceID;
	private final String name;
//	private final LinkedList<ShortcutDescriptor> shortcuts;
	private final LinkedList<ResourceDescriptor> relatedResources;
	public AbstractResourceDescriptor(String name) {
		this.resourceID = "Resource" + resourceCounter++;
//		this.shortcuts = new LinkedList();
		this.relatedResources = new LinkedList();
		this.name = name;
		}
	
	
}
