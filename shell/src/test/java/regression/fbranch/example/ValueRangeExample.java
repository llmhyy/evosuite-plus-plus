package regression.fbranch.example;

import com.example.Util;

public class ValueRangeExample {
	public Integer targetM(int[] hashValues, int value) {
		if (!Util.checkPrecondition(hashValues, value)) {
			return null;
		}

		for (int i = 0; i < hashValues.length; i++) {
			if (Util.checkValue(hashValues, i, value)) {
				return hashValues[i];
			}
		}

		return null;
	}
}
