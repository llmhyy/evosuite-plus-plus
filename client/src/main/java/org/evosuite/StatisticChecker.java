package org.evosuite;

import java.util.List;

public class StatisticChecker implements Runnable{

	private List<Double> progressInformation;
	private CoverageProgressGetter coverageGetter;
//	private long interval;
//	private long timeout;
	
	public StatisticChecker(List<Double> progressInformation, CoverageProgressGetter coverageGetter) {
		super();
		this.progressInformation = progressInformation;
		this.coverageGetter = coverageGetter;
//		this.interval = interval;
	}


	@Override
	public void run() {
		long start = System.currentTimeMillis();
		long t = System.currentTimeMillis();
		while(t-start < Properties.SEARCH_BUDGET*1000) {
			try {
				Thread.sleep(Properties.RECORD_INTERVAL*1000);
				double currentCoverage = coverageGetter.getCoverage();
				progressInformation.add(currentCoverage);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			t = System.currentTimeMillis();
		}
	}


	public List<Double> getProgressInformation() {
		return progressInformation;
	}



	public void setProgressInformation(List<Double> progressInformation) {
		this.progressInformation = progressInformation;
	}

}
