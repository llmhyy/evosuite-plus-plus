package evosuite.shell.resanalyzer;

import evosuite.shell.resanalyzer.ComparativeResultMerger.Record;

public class CompareResult {
	String projectID;
	String className;
	String methodName;
	int timeF;
	int timeB;
	double coverageF;
	double coverageB;
	double IPConverageF;
	double IPConverageB;
	int ageF;
	int ageB;
	String uncoveredIPF;

	public CompareResult(String projectID, String className, String methodName, int timeF, int timeB, double coverageF,
			double coverageB, double iPConverageF, double iPConverageB, int ageF, int ageB, String uncoveredIPF) {
		super();
		this.projectID = projectID;
		this.className = className;
		this.methodName = methodName;
		this.timeF = timeF;
		this.timeB = timeB;
		this.coverageF = coverageF;
		this.coverageB = coverageB;
		IPConverageF = iPConverageF;
		IPConverageB = iPConverageB;
		this.ageF = ageF;
		this.ageB = ageB;
		this.uncoveredIPF = uncoveredIPF;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Record other = (Record) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}


	public String getMethodID() {
		return className + "#" + methodName;
	}
	
	public double getCoverageAdvantage() {
		return coverageF - coverageB;
	}
	
	public double getTimeAdvantage() {
		if(coverageB == coverageF && coverageB == 1) {
			return timeB - timeF;				
		}
		
		return 0;
	}

	public boolean isGoodCoverage() {
		return this.coverageF > this.coverageB;
	}

	public boolean isGoodTime() {
		return this.coverageF == this.coverageB && 
				(this.timeF < 100 || this.timeB < 100) &&
				this.timeF < this.timeB;
	}

	public boolean isWorseCoverage() {
		return this.coverageF < this.coverageB;
	}

	public boolean isWorseTime() {
		return this.coverageF == this.coverageB && 
				(this.timeF < 100 || this.timeB < 100) &&
				this.timeF > this.timeB;
	}
	
	
	
}	
