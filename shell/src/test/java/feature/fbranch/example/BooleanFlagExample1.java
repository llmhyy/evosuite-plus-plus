package feature.fbranch.example;

public class BooleanFlagExample1 {
	int[] hashValues;
	int value;
	
	public Integer targetM(int[] hashValues, int value) {
		if (!ValueUtil.checkPrecondition(hashValues, value)) {
			return null;
		}

		for (int i = 0; i < hashValues.length; i++) {
			if (ValueUtil.checkValue(hashValues, i, value)) {
				return hashValues[i];
			}
		}

		return null;
	}
	
	public Integer targetM2() {
		int[] hashValues = new int[5];
		int value = 10;
		
		if (!ValueUtil.checkPrecondition(hashValues, value)) {
			return null;
		}

		for (int i = 0; i < hashValues.length; i++) {
			if (ValueUtil.checkValue(hashValues, i, value)) {
				return hashValues[i];
			}
		}

		return null;
	}
	
	public Integer targetM3() {
		if (!ValueUtil.checkPrecondition(this.hashValues, this.value)) {
			return null;
		}

		for (int i = 0; i < this.hashValues.length; i++) {
			if (ValueUtil.checkValue(this.hashValues, i, this.value)) {
				return this.hashValues[i];
			}
		}

		return null;
	}
}
