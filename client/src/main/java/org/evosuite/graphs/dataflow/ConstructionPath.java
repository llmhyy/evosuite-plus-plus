package org.evosuite.graphs.dataflow;

import java.util.ArrayList;
import java.util.List;

public class ConstructionPath {

	private List<DepVariable> path = new ArrayList<>();
	
	public ConstructionPath(List<DepVariable> linkPath) {
		this.setPath(linkPath);
	}

	public List<DepVariable> getPath() {
		return path;
	}

	public void setPath(List<DepVariable> path) {
		this.path = path;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof ConstructionPath) {
			ConstructionPath otherPath = (ConstructionPath)obj;
			
			if(otherPath.size() == path.size()) {
				for(int i=0; i<otherPath.size(); i++) {
					if(!otherPath.getPath().get(i).equals(this.path.get(i))) {
						return false;
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isDifficult() {
		for(DepVariable var: path) {
			if(var.isStaticField()) {
				return true;
			}
		}
		
		return this.path.size() > 2;
	}

	public int size() {
		return path.size();
	}
	
	public String toString() {
		return path.toString();
	}
}
