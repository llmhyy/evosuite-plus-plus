package com.example.passedExamples;

/**
 * Adapted from 102_colt cern.colt.Soring.mergeSortInPlace
 *
 */
public class ColtExample {

	public static void mergeSortInPlace(int[] a, int fromIndex, int toIndex) {
		rangeCheck(a.length, fromIndex, toIndex);
		int length = toIndex - fromIndex;
		if (length < 7) {
			for (int i = fromIndex; i < toIndex; i++) {
				for (int j = i; j > fromIndex && a[j - 1] > a[j]; j--) {
					int tmp = a[j];
					a[j] = a[j - 1];
					a[j - 1] = tmp;
				}
			}
			return;
		}
		int mid = (fromIndex + toIndex) / 2;
		mergeSortInPlace(a, fromIndex, mid);
		mergeSortInPlace(a, mid, toIndex);
		if (a[mid - 1] <= a[mid])
			return;
		inplace_merge(a, fromIndex, mid, toIndex);
	}

	public static void rangeCheck(int arrayLen, int fromIndex, int toIndex) {
		if (fromIndex > toIndex)
			throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
		if (fromIndex < 0)
			throw new ArrayIndexOutOfBoundsException(fromIndex);
		if (toIndex > arrayLen)
			throw new ArrayIndexOutOfBoundsException(toIndex);
	}

	public static void inplace_merge(int[] array, int first, int middle, int last) {
		int firstCut, secondCut;
		if (first >= middle || middle >= last)
			return;
		if (last - first == 2) {
			if (array[middle] < array[first]) {
				int tmp = array[first];
				array[first] = array[middle];
				array[middle] = tmp;
			}
			return;
		}
		if (middle - first > last - middle) {
			firstCut = first + (middle - first) / 2;
			secondCut = lower_bound(array, middle, last, array[firstCut]);
		} else {
			secondCut = middle + (last - middle) / 2;
			firstCut = upper_bound(array, first, middle, array[secondCut]);
		}
		int first2 = firstCut, middle2 = middle, last2 = secondCut;
		if (middle2 != first2 && middle2 != last2) {
			int first1 = first2, last1 = middle2;
			while (first1 < --last1) {
				int tmp = array[first1];
				array[last1] = array[first1];
				array[first1++] = tmp;
			}
			first1 = middle2;
			last1 = last2;
			while (first1 < --last1) {
				int tmp = array[first1];
				array[last1] = array[first1];
				array[first1++] = tmp;
			}
			first1 = first2;
			last1 = last2;
			while (first1 < --last1) {
				int tmp = array[first1];
				array[last1] = array[first1];
				array[first1++] = tmp;
			}
		}
		middle = firstCut + secondCut - middle;
		inplace_merge(array, first, firstCut, middle);
		inplace_merge(array, middle, secondCut, last);
	}

	public static int upper_bound(int[] array, int first, int last, int x) {
		int len = last - first;
		while (len > 0) {
			int half = len / 2;
			int middle = first + half;
			if (x < array[middle]) {
				len = half;
				continue;
			}
			first = middle + 1;
			len -= half + 1;
		}
		return first;
	}

	public static int lower_bound(int[] array, int first, int last, int x) {
		int len = last - first;
		while (len > 0) {
			int half = len / 2;
			int middle = first + half;
			if (array[middle] < x) {
				first = middle + 1;
				len -= half + 1;
				continue;
			}
			len = half;
		}
		return first;
	}
}
