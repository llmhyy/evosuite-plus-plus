package testcode.graphgeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OCGGenerator {
	
	public static Random RANDOM;
	
	private int maxParentNum = 2;
	private int maxChildNum = 2;
	
	public OCGGenerator(long seed) {
		if (RANDOM == null) {
			RANDOM = new Random();
			RANDOM.setSeed(seed);
			System.out.println("Seed for OCGGenerator: " + seed);
		}
	}
	
	public OCGGenerator() {
		if (RANDOM == null) {
			RANDOM = new Random();
			long seed = new Random().nextLong();
			RANDOM.setSeed(seed);
			System.out.println("Seed for OCGGenerator: " + seed);
		}
	}
	
	public Graph generate(int depth, int width, boolean allowLoop) {
		Graph graph = generateGraph(depth, width, allowLoop);
		graph.labelNodeType();
		graph.labelAccessibility();
		graph.transformToCode();
		return graph;
	}

	public Graph generateGraph(int depth, int width, boolean allowLoop) {
		this.maxChildNum = width;
		
		Graph graph = new Graph();
		List<ArrayList<GraphNode>> graphLayers = new ArrayList<>();
		
		int count = 0;
		
		for (int layer = 0; layer < depth; layer++) {
			/**
			 * at least one node at each layer
			 */
			int randomWidth = RANDOM.nextInt(width);
			randomWidth = Math.max(1, randomWidth);
			
			ArrayList<GraphNode> list = new ArrayList<GraphNode>();
			for (int i = 0; i < randomWidth; i++) {
				GraphNode node = new GraphNode(count++);
				
				if (layer != 0) {
					/**
					 * shall guarantee parents still have the quota of children
					 */
					int parentNum = Math.max(1, RANDOM.nextInt(maxParentNum - node.getParents().size()));
					for (int k = 0; k < parentNum; k++) {
						GraphNode selectedParent = selectParent(graphLayers, layer);
						if (selectedParent!=null &&
								selectedParent.getChildren().size() < this.maxChildNum && 
								node.getParents().size() < this.maxParentNum) {
							selectedParent.addChild(node);							
							node.addParent(selectedParent);
						}
					}
				}
				
				if (allowLoop) {
					double r = RANDOM.nextDouble();
					if (r < 0.1) {
						/**
						 * generate loop
						 */
						GraphNode parent = selectParentForLoop(graphLayers, layer);
						if (parent != null && 
								parent.getParents().size() < this.maxParentNum &&
								node.getChildren().size() < this.maxChildNum) {
							parent.addParent(node);
							node.addChild(parent);
						}
					}
				}
				
				list.add(node);
				graph.addNode(node);
			}
			
			graphLayers.add(list);
		}
		
		return graph;
	}

	private GraphNode selectParentForLoop(List<ArrayList<GraphNode>> graphLayers, int layer) {
		if(layer == 0) {
			return null;
		}
		
		/**
		 * each node is with a selection score = layerNum * parentQuota
		 */
		double sum = 0;
		Map<GraphNode, Double> nodeMap = new HashMap<>();
		Map<GraphNode, Double> nodeStartMap = new HashMap<>();
		for(int l=0; l<graphLayers.size(); l++) {
			ArrayList<GraphNode> layerNodes = graphLayers.get(l);
			for(GraphNode parent: layerNodes) {
				double score = (l+1) * (this.maxParentNum - parent.getParents().size()); 
				nodeMap.put(parent, score);
				nodeStartMap.put(parent, sum);
				
				sum += score;
				
			}
		}
		
		double dice = RANDOM.nextDouble() * sum;
		
		for(GraphNode parent: nodeMap.keySet()) {
			double start = nodeStartMap.get(parent);
			double length = nodeMap.get(parent);
			
			if(start <= dice && dice <= start + length) {
				return parent;
			}
		}
		
		return null;
	}

	/**
	 * 
	 * @param graphLayers
	 * @return
	 */
	private GraphNode selectParent(List<ArrayList<GraphNode>> graphLayers, int layer) {
		
		if(layer == 0) {
			return null;
		}
		
//		System.currentTimeMillis();
		
		/**
		 * each node is with a selection score = layerNum * childrenQuota
		 */
		double sum = 0;
		Map<GraphNode, Double> nodeMap = new HashMap<>();
		Map<GraphNode, Double> nodeStartMap = new HashMap<>();
		for(int l=0; l<graphLayers.size(); l++) {
			ArrayList<GraphNode> layerNodes = graphLayers.get(l);
			for(GraphNode parent: layerNodes) {
				double score = (l+1) * (this.maxChildNum - parent.getChildren().size()); 
				nodeMap.put(parent, score);
				nodeStartMap.put(parent, new Double(sum));
				
				sum += score;
				
			}
		}
		
		double dice = RANDOM.nextDouble() * sum;
		
		for(GraphNode parent: nodeMap.keySet()) {
			double start = nodeStartMap.get(parent);
			double length = nodeMap.get(parent);
			
			if(start <= dice && dice <= start + length) {
				return parent;
			}
		}
		
		return null;
	}
}
