package evosuite.experiment;

import org.evosuite.Properties.Strategy;
import org.evosuite.symbolic.expr.Constraint;

public class Outcome {
	public Strategy strategy;
	public long TIMEOUT_MILLIS;
	public Constraint<?> constraint;
	
	public Outcome(Strategy s,long timeout, Constraint<?> constraint) {
		this.strategy=s;
		this.TIMEOUT_MILLIS=timeout;
		this.constraint=constraint;
	}
}
