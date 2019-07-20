package org.evosuite.coverage.fbranch;

import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.setup.Call;

public class FlagEffectResult {
	BytecodeInstruction interproceduralFlagCall;
	boolean isInterproceduralFlag;
	Call call;

	public FlagEffectResult(BytecodeInstruction interproceduralFlagCall, boolean isInterproceduralFlag, Call call) {
		super();
		this.interproceduralFlagCall = interproceduralFlagCall;
		this.isInterproceduralFlag = isInterproceduralFlag;
		this.call = call;
	}
}
