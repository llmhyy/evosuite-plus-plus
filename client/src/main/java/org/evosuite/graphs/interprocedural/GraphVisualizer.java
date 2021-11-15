package org.evosuite.graphs.interprocedural;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Style;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.graphviz.SimpleBytecodeInstruction;
import org.evosuite.testcase.synthesizer.graphviz.SimpleControlFlowGraph;
import org.evosuite.testcase.synthesizer.graphviz.SimpleDepVariableWrapper;
import org.evosuite.testcase.synthesizer.graphviz.SimplePartialGraph;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;

public class GraphVisualizer {

	public static String path = "D://linyun/";
	
	// Number of characters per line in nodes.
	public static int lineWidth = 25;

	// Format a string to have only a certain
	// number of characters (n) per line
	private static String format(String s, int n) {
		// Regex magic, see https://stackoverflow.com/questions/2297347/splitting-a-string-at-every-n-th-character
		String regexPrefix = "(?<=\\G";
	    String regexSuffix = ")";
	    StringBuilder regexBuilder = new StringBuilder();
	    regexBuilder.append(regexPrefix);
	    for (int i = 0; i < n; i++) {
	      regexBuilder.append(".");
	    }
	    regexBuilder.append(regexSuffix);
	    String regex = regexBuilder.toString();
		
	    String[] parts = s.split(regex);
	    return String.join("\n", parts);
	}
	
	public static void visualizeCfg(SimpleControlFlowGraph cfg, int resolution, String folderName, String graphName) {
		Set<SimpleBytecodeInstruction> entryPoints = cfg.entryPoints;

		/**
		 * use BFS on partial graph to generate test code.
		 */
		Queue<SimpleBytecodeInstruction> queue = new ArrayDeque<>(entryPoints);
		List<LinkSource> links = new ArrayList<LinkSource>();

		while (!queue.isEmpty()) {
			SimpleBytecodeInstruction source = queue.remove();
			
			List<SimpleBytecodeInstruction> childNodes = cfg.stringRepresentationToAdjacencyList.get(source.toString());
			for (SimpleBytecodeInstruction target : childNodes) {
				guru.nidi.graphviz.model.Node n = node(format(source.toString(), lineWidth))
						.link(node(format(target.toString(), lineWidth)));
				if (!links.contains(n)) {
					links.add(n);
					queue.add(target);
				}

			}

			if (childNodes.isEmpty()) {
				guru.nidi.graphviz.model.Node n = node(format(source.toString(), lineWidth));
				if (!links.contains(n)) {
					links.add(n);
				}
			}
		}

		Graph g = graph(graphName).directed().graphAttr()
				.with(Rank.dir(RankDir.TOP_TO_BOTTOM)).with(links);
		try {
			File f = new File(path + folderName + File.separator + graphName + ".png");
			Graphviz.fromGraph(g).height(resolution).render(Format.PNG).toFile(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void visualizeComputationGraph(SimplePartialGraph partialGraph, int resolution, String folderName) {
		List<SimpleDepVariableWrapper> topLayer = partialGraph.getTopLayer();

		/**
		 * use BFS on partial graph to generate test code.
		 */
		Queue<SimpleDepVariableWrapper> queue = new ArrayDeque<>(topLayer);
		List<LinkSource> links = new ArrayList<LinkSource>();

		while (!queue.isEmpty()) {
			SimpleDepVariableWrapper source = queue.remove();

			for (SimpleDepVariableWrapper target : source.children) {
				guru.nidi.graphviz.model.Node n = node(source.shortLabel)
						.link(node(target.shortLabel));
				if (!links.contains(n)) {
					links.add(n);
					queue.add(target);
				}

			}

			if (source.children.isEmpty()) {
				guru.nidi.graphviz.model.Node n = node(source.shortLabel);
				if (!links.contains(n)) {
					links.add(n);
				}
			}
		}

		Graph g = graph(partialGraph.getBranch().toString()).directed().graphAttr()
				.with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
		try {
			File f = new File(path + folderName + File.separator + partialGraph.getBranch().toString() + ".png");
			Graphviz.fromGraph(g).height(resolution).render(Format.PNG).toFile(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Generate graphs with highlighted nodes
		Set<String> nodeIds = new HashSet<String>();
		for (int i = 0; i < links.size(); i++) {
//			List<LinkSource> coloredLinks = new ArrayList<LinkSource>(links);
			guru.nidi.graphviz.model.Node node = (guru.nidi.graphviz.model.Node) links.get(i);
			
			// Some nodes are duplicate
			String name = node.name().toString();
			String nodeId = name.substring(name.indexOf("ID: ") + 4, name.indexOf("\nLINE:"));
			if (nodeIds.contains(nodeId)) {
				continue;
			}
			nodeIds.add(nodeId);
	
			// Color node and save graph
//			guru.nidi.graphviz.model.Node coloredNode = node.with(Style.FILLED, Color.YELLOW2);
//			coloredLinks.set(i, coloredNode);
			links.set(i, ((guru.nidi.graphviz.model.Node) links.get(i)).with(Style.FILLED, Color.YELLOW3));
			Graph coloredGraph = graph(partialGraph.getBranch().toString()).directed().graphAttr()
					.with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
			try {
				File f = new File(path + folderName + File.separator + partialGraph.getBranch().toString() + "#" + nodeId + ".png");
				Graphviz.fromGraph(coloredGraph).height(resolution).render(Format.PNG).toFile(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Color node back to normal
			links.set(i, ((guru.nidi.graphviz.model.Node) links.get(i)).with(Style.ROUNDED, Color.BLACK));
		}
	}
	
	public static void visualizeComputationGraph(PartialGraph partialGraph, int resolution, String folderName) {

		List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();

		/**
		 * use BFS on partial graph to generate test code.
		 */
		Queue<DepVariableWrapper> queue = new ArrayDeque<>(topLayer);
		List<LinkSource> links = new ArrayList<LinkSource>();

		while (!queue.isEmpty()) {
			DepVariableWrapper source = queue.remove();

			for (DepVariableWrapper target : source.children) {
				guru.nidi.graphviz.model.Node n = node(source.var.getShortLabel())
						.link(node(target.var.getShortLabel()));
				if (!links.contains(n)) {
					links.add(n);
					queue.add(target);
				}

			}

			if (source.children.isEmpty()) {
				guru.nidi.graphviz.model.Node n = node(source.var.getShortLabel());
				if (!links.contains(n)) {
					links.add(n);
				}
			}
		}

		Graph g = graph(partialGraph.getBranch().toString()).directed().graphAttr()
				.with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
		try {
			File f = new File(path + folderName + File.separator + partialGraph.getBranch().toString() + ".png");
			Graphviz.fromGraph(g).height(resolution).render(Format.PNG).toFile(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Generate graphs with highlighted nodes
		Set<String> nodeIds = new HashSet<String>();
		for (int i = 0; i < links.size(); i++) {
//			List<LinkSource> coloredLinks = new ArrayList<LinkSource>(links);
			guru.nidi.graphviz.model.Node node = (guru.nidi.graphviz.model.Node) links.get(i);
			
			// Some nodes are duplicate
			String name = node.name().toString();
			String nodeId = name.substring(name.indexOf("ID: ") + 4, name.indexOf("\nLINE:"));
			if (nodeIds.contains(nodeId)) {
				continue;
			}
			nodeIds.add(nodeId);
	
			// Color node and save graph
//			guru.nidi.graphviz.model.Node coloredNode = node.with(Style.FILLED, Color.YELLOW2);
//			coloredLinks.set(i, coloredNode);
			links.set(i, ((guru.nidi.graphviz.model.Node) links.get(i)).with(Style.FILLED, Color.YELLOW3));
			Graph coloredGraph = graph(partialGraph.getBranch().toString()).directed().graphAttr()
					.with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
			try {
				File f = new File(path + folderName + File.separator + partialGraph.getBranch().toString() + "#" + nodeId + ".png");
				Graphviz.fromGraph(coloredGraph).height(resolution).render(Format.PNG).toFile(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Color node back to normal
			links.set(i, ((guru.nidi.graphviz.model.Node) links.get(i)).with(Style.ROUNDED, Color.BLACK));
		}
	}

	public static void visualizeComputationGraph(Branch b, int resolution) {
		for (String methodName : InterproceduralGraphAnalysis.branchInterestedVarsMap.keySet()) {
			Map<Branch, Set<DepVariable>> map = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(methodName);

			Set<DepVariable> variables = map.get(b);

			if (variables == null)
				continue;

			List<LinkSource> links = new ArrayList<LinkSource>();
			HashSet<DepVariable> roots = new HashSet<DepVariable>();
			for (DepVariable source : variables) {
				for (DepVariable root : source.getRootVars().keySet()) {

					roots.add(root);
					collectLinks(root, links);

				}

			}

			Graph g = graph("example1").directed().graphAttr().with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
			try {
				File f = new File(path + File.separator + "overall.png");
				Graphviz.fromGraph(g).height(resolution).render(Format.PNG).toFile(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void collectLinks(DepVariable source, List<LinkSource> links) {
		boolean isolated = true;
		List<DepVariable>[] relations = source.getRelations();
		for (int i = 0; i < relations.length; i++) {
			List<DepVariable> child = relations[i];

			if (child == null)
				continue;

			isolated = false;
			for (DepVariable target : child) {

				guru.nidi.graphviz.model.Node n = node(source.getUniqueLabel()).link(node(target.getUniqueLabel()));

				if (!links.contains(n)) {
					links.add(n);
					collectLinks(target, links);
				}
			}
		}

		if (isolated) {
			guru.nidi.graphviz.model.Node n = node(source.getUniqueLabel());
			if (!links.contains(n)) {
				links.add(n);
			}
		}
	}
}
