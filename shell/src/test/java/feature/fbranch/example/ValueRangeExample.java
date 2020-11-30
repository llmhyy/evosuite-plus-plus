package feature.fbranch.example;

public class ValueRangeExample {
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
}
