package org.evosuite.testcase.synthesizer;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.graphs.cfg.BytecodeInstruction;

public class PotentialSetter {
	public List<Executable> setterList = new ArrayList<>();
	public List<Map<BytecodeInstruction, List<BytecodeInstruction>>> difficultyList = new ArrayList<>();
	public List<Set<Integer>> numberOfValidParams = new ArrayList<>();

	public PotentialSetter(List<Executable> setterList,
			List<Map<BytecodeInstruction, List<BytecodeInstruction>>> difficultyList,
			List<Set<Integer>> numberOfValidParams) {
		super();
		this.setterList = setterList;
		this.difficultyList = difficultyList;
		this.numberOfValidParams = numberOfValidParams;
	}

}
