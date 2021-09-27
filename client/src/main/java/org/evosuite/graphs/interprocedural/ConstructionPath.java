package org.evosuite.graphs.interprocedural;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.graphs.interprocedural.var.DepVariable;

public class ConstructionPath {

	private List<DepVariable> path = new ArrayList<>();
	/**
	 * the position[i] indicates the position of path[i] is used for path[i+1],
	 * the invariant is that path.length = positions.length + 1
	 */
	private List<Integer> positions = new ArrayList<>();

	public ConstructionPath(List<DepVariable> path, List<Integer> positions) {
		super();
		this.path = path;
		this.positions = positions;
	}

	public List<DepVariable> getPath() {
		return path;
	}

	public void setPath(List<DepVariable> path) {
		this.path = path;
	}

	public boolean equals(Object obj) {
		if (obj instanceof ConstructionPath) {
			ConstructionPath otherPath = (ConstructionPath) obj;

			if (otherPath.size() == path.size()) {
				for (int i = 0; i < otherPath.size(); i++) {
					if (!otherPath.getPath().get(i).equals(this.path.get(i))) {
//							&& !otherPath.getPosition().get(i).equals(this.positions.get(i)))) {
						return false;
					}
				}
				
				return true;
			}
		}

		return false;
	}

	public boolean isDifficult() {
		for (DepVariable var : path) {
			if (var.isStaticField()) {
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

	public List<Integer> getPosition() {
		return positions;
	}

	public void setPosition(List<Integer> position) {
		this.positions = position;
	}

	public boolean hasValidRoot() {
		if(this.getPath().isEmpty()) return false;
		
		DepVariable root = this.getPath().get(0);
		if(root.getInstruction().getClassName().equals(Properties.TARGET_CLASS)) {
			return true;
		}
		
		if(root.getInstruction().isStaticDefUse()) {
			return true;
		}
		
		return false;
	}

	public DepVariable get(int i) {
		return this.path.get(i);
	}
}
