package testcode.graphgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GraphNode {

	private int index;
	private NodeType nodeType;
	
	public GraphNode(int index) {
		this.index = index;
	}

	private List<GraphNode> parents = new ArrayList<GraphNode>();
	private List<GraphNode> children = new ArrayList<GraphNode>();

	public List<GraphNode> getParents() {
		return parents;
	}

	public void setParents(List<GraphNode> parents) {
		this.parents = parents;
	}

	public List<GraphNode> getChildren() {
		return children;
	}

	public void setChildren(List<GraphNode> children) {
		this.children = children;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void addChild(GraphNode child) {
		if(!this.children.contains(child)) {
			this.children.add(child);
		}
	}
	
	public void addParent(GraphNode parent) {
		if(!this.parents.contains(parent)) {
			this.parents.add(parent);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(index);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode other = (GraphNode) obj;
		return index == other.index;
	}

	public String getName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.index);
		
		if(this.nodeType != null) {
			buffer.append("\n" + this.nodeType.getType());
			buffer.append("\n" + this.nodeType.getName());
		}
		
		return buffer.toString();
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public boolean isLeaf() {
		return this.children.isEmpty();
	}

	public boolean isSingleParent() {
		return !this.parents.isEmpty() &&
				this.parents.size()==1;
	}

	public boolean isSingleChild() {
		return !this.children.isEmpty() &&
				this.children.size()==1;
	}

	
}
