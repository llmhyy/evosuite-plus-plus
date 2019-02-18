package org.evosuite.coverage.fbranch;

import java.util.Collections;
import java.util.List;

public class FitnessAggregator {
	
	public static final double epsilon = 10E-8;
	public static final int aggreationSizeLimit = 3;
	
	public static double aggreateFitenss(List<Double> fitnessList) {
//		double sum = epsilon;
//		
//		for (Double f : fitnessList) {
//			sum += 1 / (f + epsilon);
//		}
//		
//		double fit = fitnessList.size() / sum;
		
//		double fit = 0;
//		for(double d: fitnessList) {
//			fit += d;
//		}
//		fit /= fitnessList.size();

		if(fitnessList.isEmpty()) {
			System.currentTimeMillis();
			return 1000000000d;
		}
		
		Collections.sort(fitnessList);
		
		if(fitnessList.get(0)==0) {
			return 0;
		}
		
		double fit = 0;
//		int size = fitnessList.size() > aggreationSizeLimit ? aggreationSizeLimit : fitnessList.size();
		int size = fitnessList.size();
		for(int i=0; i<size; i++) {
			fit += fitnessList.get(i);
		}
		fit /= size;
		
		
		return fit;
	}
}
