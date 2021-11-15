package org.evosuite.testcase.synthesizer.graphviz;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.evosuite.TestGenerationContext;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.testcase.synthesizer.PartialGraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GraphVisualisationData implements Serializable {
	private static final long serialVersionUID = -4827425055695415717L;
	
	public SimplePartialGraph partialGraph;
	public SimpleControlFlowGraph cfg;
	
	public GraphVisualisationData() {	
	}
	
	public void setPartialGraph(SimplePartialGraph simplePartialGraph) {
		partialGraph = simplePartialGraph;
	}
	
	public SimplePartialGraph getPartialGraph() {
		return partialGraph;
	}
	
	public void setCfg(SimpleControlFlowGraph simpleCfg) {
		cfg = simpleCfg;
	}
	
	@JsonIgnore
	public void writeTo(File file) throws StreamWriteException, DatabindException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(file, this);
	}
	
	@JsonIgnore
	public static GraphVisualisationData from(File file) throws StreamReadException, DatabindException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		GraphVisualisationData toReturn = mapper.readValue(file, GraphVisualisationData.class);
		return toReturn;
	}
}
