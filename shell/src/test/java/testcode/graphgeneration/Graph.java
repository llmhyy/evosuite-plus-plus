package testcode.graphgeneration;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.LinkSource;

public class Graph {
	public static String path = "D://linyun/";
	private List<GraphNode> nodeSet = new ArrayList<>();

	public void addNode(GraphNode node) {
		if(!nodeSet.contains(node)) {
			nodeSet.add(node);
		}
		
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
		}

		guru.nidi.graphviz.model.Graph g = graph(fileName).directed().graphAttr()
				.with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
		try {
			File f = new File(path + folderName + File.separator + fileName + ".png");
			Graphviz.fromGraph(g).height(resolution).render(Format.PNG).toFile(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void labelNodeType() {
		// TODO Auto-generated method stub
		
	}

	public void labelAccessibility() {
		// TODO Auto-generated method stub
		
	}

	public void generateCode() {
		// TODO Auto-generated method stub
		
	}
	
}
