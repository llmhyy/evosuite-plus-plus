package org.evosuite.graphs.dataflow;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.synthesizer.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.PartialGraph;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;

public class GraphVisualizer {
	public static void visualizeComputationGraph(PartialGraph partialGraph) {
		
		List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
		
		/**
		 * use BFS on partial graph to generate test code.
		 */
		Queue<DepVariableWrapper> queue = new ArrayDeque<>(topLayer);
		List<LinkSource> links = new ArrayList<LinkSource>();
		
		while(!queue.isEmpty()) {
			DepVariableWrapper source = queue.remove();
			
			for(DepVariableWrapper target: source.children) {
				guru.nidi.graphviz.model.Node n = node(source.var.getUniqueLabel()).link(node(target.var.getUniqueLabel()));

				if (!links.contains(n)) {
					links.add(n);
					queue.add(target);	
				}

			}
			
			if(source.children.isEmpty()){
				guru.nidi.graphviz.model.Node n = node(source.var.getUniqueLabel());
				if (!links.contains(n)) {
					links.add(n);
				}
			}
		}
		

		Graph g = graph("example2").directed().graphAttr().with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
		try {
			File f = new File("D://linyun/ex2.png");
			Graphviz.fromGraph(g).height(1000).render(Format.PNG).toFile(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void visualizeComputationGraph(Branch b) {
		for (String methodName : Dataflow.branchDepVarsMap.keySet()) {
			Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(methodName);

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
				File f = new File("D://linyun/ex1.png");
				Graphviz.fromGraph(g).height(1000).render(Format.PNG).toFile(f);
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

		if(isolated){
			guru.nidi.graphviz.model.Node n = node(source.getUniqueLabel());
			if (!links.contains(n)) {
				links.add(n);
			}
		}
	}
}
