package evosuite.shell.resanalyzer;

public class RecordItem {
	String className;
	String methodName;
	int time;
	double coverage;
	int age;
	double callAvailability;
	double IPFlag;
	String uncoveredFlags;
	long randomSeed;
	String unavaiableCalls;

	public RecordItem(String className, String methodName, int time, double coverage, int age, double callAvailability,
			double iPFlag, String uncoveredFlags, long randomSeed, String unavaiableCalls) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.time = time;
		this.coverage = coverage;
		this.age = age;
		this.callAvailability = callAvailability;
		IPFlag = iPFlag;
		this.uncoveredFlags = uncoveredFlags;
		this.randomSeed = randomSeed;
		this.unavaiableCalls = unavaiableCalls;
	}
	
	public RecordItem clone() {
		return new RecordItem(
				className, 
				methodName, 
				time, 
				coverage, 
				age, 
				callAvailability, 
				IPFlag, 
				uncoveredFlags, 
				randomSeed,
				unavaiableCalls);
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
		RecordItem other = (RecordItem) obj;
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
	
	

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((className == null) ? 0 : className.hashCode());
//		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
//		result = prime * result + (int) (randomSeed ^ (randomSeed >>> 32));
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		RecordItem other = (RecordItem) obj;
//		if (className == null) {
//			if (other.className != null)
//				return false;
//		} else if (!className.equals(other.className))
//			return false;
//		if (methodName == null) {
//			if (other.methodName != null)
//				return false;
//		} else if (!methodName.equals(other.methodName))
//			return false;
//		if (randomSeed != other.randomSeed)
//			return false;
//		return true;
//	}
	
	

}
