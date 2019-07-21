package org.evosuite.coverage.fbranch;

import java.util.Collections;
import java.util.List;

public class FitnessAggregator {
	
	public static final double epsilon = 10E-8;
	public static final int aggreationSizeLimit = 3;
	
	public static double aggreateFitenss(List<Double> fitnessList) {

		if(fitnessList.isEmpty()) {
			return 1000000000d;
		}
		
		Collections.sort(fitnessList);
		
		if(fitnessList.get(0)==0) {
			return 0;
		}
		
		int size = fitnessList.size();
		
		double fit = 0;
		for(double f: fitnessList) {
			fit += 1/f;
		}
		fit = size/fit;
		
//		double fit = 0;
//		for(int i=0; i<size; i++) {
//			fit += fitnessList.get(i);
//		}
//		fit /= size;
		
		
		return fit;
	}
}
