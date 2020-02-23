package org.evosuite.ga.comparators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.evosuite.ga.Chromosome;

/**
 * we do not allow duplicated test cases to avoid local optima in the search process. 
 * @author linyun
 *
 */
public class NonduplicationComparator<T extends Chromosome> implements Comparator<Chromosome>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6438630516040148684L;

	private List<T> population;
	
	public NonduplicationComparator(List<T> population2) {
		super();
		this.population = population2;
	}

	@Override
	public int compare(Chromosome c1, Chromosome c2) {
		//FIXME FOR ZIHENG, check duplication
		if(population.contains(c1) && !population.contains(c2)) {
			return +1;
		}
		
		if(population.contains(c2) && !population.contains(c1)) {
			return -1;
		}
		
		if (c1.getDistance() > c2.getDistance()) {
			return -1;
		} else if (c1.getDistance() < c2.getDistance()) {
			return +1;
		} else { 
			return 0;
		}
	}

}
