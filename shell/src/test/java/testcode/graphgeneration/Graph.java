package testcode.graphgeneration;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Link.to;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.LinkSource;
import testcode.graphgeneration.model.ClassModel;

public class Graph {
	private class GraphNodeWrapper {
		private GraphNode node;
		private List<GraphNode> path;
		
		public GraphNodeWrapper(GraphNode node, List<GraphNode> path) {
			this.node = node;
			this.path = new ArrayList<>(path);
		}
		
		public GraphNodeWrapper(GraphNode node) {
			this.node = node;
			this.path = new ArrayList<>();
		}
		
		public void addToPath(GraphNode node) {
			this.path.add(node);
		}
		
		public GraphNode getNode() {
			return node;
		}
		
		public List<GraphNode> getPath() {
			return new ArrayList<>(path);
		}
	}
	
	public static String path = "D://linyun/";
	private List<GraphNode> nodeSet = new ArrayList<>();
	private Map<GraphNode, List<GraphNode>> accessibilityMap = new HashMap();
	public static final String[] PRIMITIVE_TYPES = {
			"boolean",
			"byte",
			"char",
			"short",
			"float",
			"double",
			"int",
			"long"
	};

	public void addNode(GraphNode node) {
		if(!nodeSet.contains(node)) {
			nodeSet.add(node);
		}
	}
	
	public List<GraphNode> getNodesAccessibleFrom(GraphNode node) {
		return new ArrayList<>(accessibilityMap.get(node));
	}

	public List<GraphNode> getTopLayer(){
		List<GraphNode> topLayer = new ArrayList<GraphNode>();
		for(GraphNode node: this.nodeSet) {
			if(node.getParents().isEmpty()) {
				topLayer.add(node);
			}
			
			if(node.getIndex() == 0) {
				if(!topLayer.contains(node)) {
					topLayer.add(node);
				}
			}
		}
		
		return topLayer;
	}
	
	public void visualize(int resolution, String folderName, String fileName) {

		List<LinkSource> links = new ArrayList<LinkSource>();

		for (GraphNode source: nodeSet) {
			for (GraphNode target : source.getChildren()) {
				guru.nidi.graphviz.model.Node n = node(source.getName())
						.link(node(target.getName()));
				if (!links.contains(n)) {
					links.add(n);
				}

			}

			if (source.getChildren().isEmpty()) {
				guru.nidi.graphviz.model.Node n = node(source.getName());
				if (!links.contains(n)) {
					links.add(n);
				}
			}
			
			List<GraphNode> accessibleChildren = this.accessibilityMap.get(source);
			for(GraphNode child: accessibleChildren) {
				guru.nidi.graphviz.model.Node n = node(source.getName())
						.link(to(node(child.getName())).with(Color.GREEN, guru.nidi.graphviz.attribute.Style.DASHED));
//				n.linkTo().with(Color.rgb("ffcc00"));
//				n.linkTo(node(child.getName())).with(Color.rgb("ffcc00"), guru.nidi.graphviz.attribute.Style.DASHED);
//				n.asLinkSource().asLinkTarget().with(Color.rgb("ffcc00"), guru.nidi.graphviz.attribute.Style.DASHED);
				links.add(n);
			}
		}

		guru.nidi.graphviz.model.Graph g = graph(fileName).directed().graphAttr()
				.with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
		try {
			String filePath = path + folderName + File.separator + fileName + ".png";
			File f = new File(filePath);
			Graphviz.fromGraph(g).height(resolution).render(Format.PNG).toFile(f);
			System.out.println("Saved graph to " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void visualizeStep2(int resolution, String folderName, String fileName) {

		List<LinkSource> links = new ArrayList<LinkSource>();

		for (GraphNode source: nodeSet) {
			for (GraphNode target : source.getChildren()) {
				guru.nidi.graphviz.model.Node n = node(source.getName())
						.link(node(target.getName()));
				if (!links.contains(n)) {
					links.add(n);
				}

			}

			if (source.getChildren().isEmpty()) {
				guru.nidi.graphviz.model.Node n = node(source.getName());
				if (!links.contains(n)) {
					links.add(n);
				}
			}
		}

		guru.nidi.graphviz.model.Graph g = graph(fileName).directed().graphAttr()
				.with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
		try {
			String filePath = path + folderName + File.separator + fileName + ".png";
			File f = new File(filePath);
			Graphviz.fromGraph(g).height(resolution).render(Format.PNG).toFile(f);
			System.out.println("Saved graph to " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void labelNodeType() {
		List<String> typePool = new ArrayList<String>();

		String dataType = null;
		
		List<GraphNode> topLayer = this.getTopLayer();
		Queue<GraphNode> queue = new LinkedList<GraphNode>();
		queue.addAll(topLayer);
		
		/**
		 * make sure the parents are always processed before the children
		 */
		while(!queue.isEmpty()) {
			GraphNode node = queue.poll();
			
			/**
			 * non-leaf node, reference type access
			 */
			if(!node.isLeaf()) {
				if(typePool.isEmpty()) {
					dataType = createRandomType();
				}
				else {
					if(OCGGenerator.RANDOM.nextDouble() > 0.5) {
						dataType = createRandomType();
					}
					else {
						int index = OCGGenerator.RANDOM.nextInt(typePool.size());
						dataType = typePool.get(index);
					}
				}
				
				if(!typePool.contains(dataType)) {
					typePool.add(dataType);
				}
			}
			/**
			 * leaf node, primitive type access
			 */
			else {
				dataType = randomPrimitiveType();
			}
			
			/**
			 * either field access or method call here
			 */
			Relation relation = Relation.randomRelation();
			
			/**
			 * constraint 1: the top layer can only be parameter or field
			 */
			if(node.getParents().isEmpty()) {
				if(OCGGenerator.RANDOM.nextDouble() < 0.5) {
					relation = Relation.PARAM;
				}
				else {
					relation = Relation.FIELD;					
				}
			}
			
			/**
			 * constraint 2: leaf node can only be field or array element, if it is array element, its parent's data type
			 * must be an array.
			 */
			if(node.isLeaf() && node.isSingleParent()) {
				GraphNode par = node.getParents().get(0);
				if(par.isSingleChild()) {
					if(OCGGenerator.RANDOM.nextDouble() < 0.9) {
						relation = Relation.ARRAY_ELEMENT;

						/**
						 * if the leaf is an array element access, we need to change the data type in the parent access.
						 */
						NodeType parentAccessType = par.getNodeType();
						parentAccessType.setType(dataType + "[]");
					}
				}
				
			}
			
			NodeType accessType = generateNodeType(relation, dataType, node);
			
			node.setNodeType(accessType);
			
			queue.addAll(node.getChildren());
		}
		
	}

	private String createRandomType() {
		return "Class" + OCGGenerator.RANDOM.nextInt(100);
	}

	private String randomPrimitiveType() {
		return PRIMITIVE_TYPES[OCGGenerator.RANDOM.nextInt(PRIMITIVE_TYPES.length)];
	}

	private NodeType generateNodeType(Relation relation, String type, GraphNode node) {
		String name = relation.name() + node.getName();
		if(relation.equals(Relation.ARRAY_ELEMENT)) {
			return new ArrayElementAccess(type, name);
		}
		else if(relation.equals(Relation.FIELD)) {
			return new FieldAccess(type, name);
		}
		else if(relation.equals(Relation.METHOD)) {
			return new MethodCallAccess(type, name);
		}
		else if(relation.equals(Relation.PARAM)) {
			return new Parameter(type, name);
		}
		
		return null;
	}

	private void setAsAccessible(GraphNode ancestor, GraphNode descendant) {
//		System.out.println("ACCESSIBILITY: " + ancestor + " -> " + descendant);
		accessibilityMap.get(ancestor).add(descendant);
	}
	
	public void labelAccessibility() {
		/**
		 * for every pair of ancestor-descendant, we random decide whether the ancestor can access descendant
		 * store the relation in {@code this.accessbilityMap},
		 * use OCGGenerator.RANDOM to for the random algorithm for debugging.
		*/
		
		// We assume here that the accessibility map only records "first-hop" accessibility
		// e.g. that A -> C if there is some method call/field access/etc. that returns us B from an instance of A
		// This thus requires that A be one of
		// 1) Object (only for field access, method call)
		// 2) Array (only for array element access, type must be enforced)
		// TODO: What do we do about parameters?
		// We also need to enforce that some ancestor-descendant pairs are always accessible
		// e.g. array -> array element
		Queue<GraphNode> queue = new ArrayDeque<>();
		for (GraphNode topLayerNode : this.getTopLayer()) {
			queue.offer(topLayerNode);
		}
		while (!queue.isEmpty()) {
			GraphNode ancestor = queue.poll();
			if (!accessibilityMap.containsKey(ancestor)) {
				accessibilityMap.put(ancestor, new ArrayList<>());
			}
			List<GraphNode> descendants = getDescendantsOf(ancestor);
			NodeType ancestorNodeType = ancestor.getNodeType();
			// Simple heuristics
			// Starts with "Class" = object
			// Ends with "[]" = array (we only consider the 1D case)
			// Otherwise primitive
			boolean isAncestorObject = ancestorNodeType.getType().startsWith("Class");
			boolean isAncestorArray = ancestorNodeType.getType().endsWith("[]");
			for (GraphNode descendant : descendants) {
				if (isAncestorObject) {
					boolean isDescendantMethodCall = descendant.getNodeType().getName().startsWith("METHOD");
					boolean isAncestorDirectParent = ancestor.getChildren().contains(descendant);
					// Special check, if the descendant is a method call
					// only add if the ancestor is the direct parent
					if (isDescendantMethodCall) {
						if (isAncestorDirectParent) {
							setAsAccessible(ancestor, descendant);
						}
						continue;
					}
					
					if (OCGGenerator.RANDOM.nextFloat() > 0.5f) {
						setAsAccessible(ancestor, descendant);
					}
				} else if (isAncestorArray) {
					// Only allow array element if it's direct child
					boolean isDescendantArrayElement = descendant.getNodeType().getName().contains("ARRAY_ELEMENT");
					boolean isDescendantDirectChild = ancestor.getChildren().contains(descendant);
					if (isDescendantArrayElement && isDescendantDirectChild) {
						setAsAccessible(ancestor, descendant);
//						accessibilityMap.get(ancestor).add(descendant);
					}
				}
			}
			
			for (GraphNode child : ancestor.getChildren()) {
				queue.offer(child);
			}
		}
	}

	public void generateCode() {
		// TODO Darien
		
		/**
		 * step 1: build an intermediate model to describe the relations of class, field, method
		 */
		ClassModel classModel = new ClassModel(this);
		
		
		
		/**
		 * step 3: generate graph
		 */
		classModel.transformToCode();
	}

	public List<GraphNode> getDescendantsOf(GraphNode node) {
		List<GraphNode> descendants = new ArrayList<>();
		descendants.addAll(node.getChildren());
		for (GraphNode child : node.getChildren()) {
			descendants.addAll(getDescendantsOf(child));
		}
		return descendants;
	}
	
	public List<GraphNode> getPath(GraphNode fromNode, GraphNode toNode) {
		if (!getDescendantsOf(fromNode).contains(toNode)) {
			return null;
		}
		
		// We want to determine the path between a pair of nodes 
		// that does not use a particular edge (if it exists)
		// To do that, we remove this edge from the accessibility map
		// before the BFS and add it back after
		// Don't do this if the toNode is a direct child of the fromNode,
		// since in this case removing the edge will ensure that we can never get a path
		boolean isToNodeAccessibleFromFromNode = getNodesAccessibleFrom(fromNode).contains(toNode);
		boolean isDirectChild = fromNode.getChildren().contains(toNode);
		if (isToNodeAccessibleFromFromNode && !isDirectChild) {
			accessibilityMap.get(fromNode).remove(toNode);
		}
		
		// Enhanced BFS to track the path taken
		// See https://stackoverflow.com/questions/8922060/how-to-trace-the-path-in-a-breadth-first-search/50575971#50575971
		Set<GraphNode> visitedNodes = new HashSet<>();
		List<GraphNode> path = null;
		
		Queue<GraphNodeWrapper> queue = new ArrayDeque<>();
		List<GraphNode> initialPath = new ArrayList<>();
		initialPath.add(fromNode);
		queue.offer(new GraphNodeWrapper(fromNode, initialPath));
		visitedNodes.add(fromNode);
		while (!queue.isEmpty()) {
			GraphNodeWrapper currentNodeWrapper = queue.poll();
			List<GraphNode> currentPath = currentNodeWrapper.getPath();
			if (currentNodeWrapper.getNode().equals(toNode)) {
				path = currentPath;
				break;
			}
			List<GraphNode> currentNodeNeighbours = getNodesAccessibleFrom(currentNodeWrapper.getNode());
			for (GraphNode neighbour : currentNodeNeighbours) {
				if (!visitedNodes.contains(neighbour)) {
					visitedNodes.add(neighbour);
					List<GraphNode> currentPathPlusNeighbour = new ArrayList<>(currentPath);
					currentPathPlusNeighbour.add(neighbour);
					queue.offer(new GraphNodeWrapper(neighbour, currentPathPlusNeighbour));
				}
			}
		}
		
		if (isToNodeAccessibleFromFromNode && !isDirectChild) {
			accessibilityMap.get(fromNode).add(toNode);
		}
		
		return path;
	}
}
