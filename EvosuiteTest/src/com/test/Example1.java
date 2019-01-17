package com.test;

//import org.apache.commons.math.exception.DimensionMismatchException;
//import org.apache.commons.math.exception.NullArgumentException;
//import org.apache.commons.math.exception.util.LocalizedFormats;
//import org.apache.commons.math.stat.descriptive.summary.Sum;


public class Example1 {
	public double example(double[] values, double[] weights, int begin, int length) {
		if (test(values, weights, begin, length)) {
//			Sum sum = new Sum();
//
//			// Compute initial estimate using definitional formula
//			double sumw = sum.evaluate(weights, begin, length);
//			double xbarw = sum.evaluate(values, weights, begin, length) / sumw;
//
//			// Compute correction factor in second pass
//			double correction = 0;
//			for (int i = begin; i < begin + length; i++) {
//				correction += weights[i] * (values[i] - xbarw);
//			}
//			return xbarw + (correction / sumw);
			return 0;
		}
		return 1;
	}

	private boolean test(double[] values, double[] weights, int begin, int length) {

		if (weights == null) {
//			throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
		}

		if (weights.length != values.length) {
//			throw new DimensionMismatchException(weights.length, values.length);
		}

		boolean containsPositiveWeight = false;
		for (int i = begin; i < begin + length; i++) {
			if (Double.isNaN(weights[i])) {
				//throw MathRuntimeException.createIllegalStateException(LocalizedFormats.NAN_ELEMENT_AT_INDEX, i);
			}
			if (Double.isInfinite(weights[i])) {
				//throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INFINITE_ARRAY_ELEMENT,
				//		weights[i], i);
			}
			if (weights[i] < 0) {
//				throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NEGATIVE_ELEMENT_AT_INDEX, i,
//						weights[i]);
			}
			if (!containsPositiveWeight && weights[i] > 0.0) {
				containsPositiveWeight = true;
			}
		}

		if (!containsPositiveWeight) {
//			throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.WEIGHT_AT_LEAST_ONE_NON_ZERO);
		}

		return test(values, begin, length);
	}

	private boolean test(final double[] values, final int begin, final int length) {

		if (values == null) {
//			throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
		}

		if (begin < 0) {
//			throw new NotPositiveException(LocalizedFormats.START_POSITION, begin);
		}

		if (length < 0) {
//			throw new NotPositiveException(LocalizedFormats.LENGTH, length);
		}

		if (begin + length > values.length) {
//			throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.SUBARRAY_ENDS_AFTER_ARRAY_END);
		}

		if (length == 0) {
			return false;
		}

		return true;

	}
}
