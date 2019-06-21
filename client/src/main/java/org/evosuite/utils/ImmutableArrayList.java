package org.evosuite.utils;

import java.util.ArrayList;

public class ImmutableArrayList<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6857993631949397384L;

	private int hash = -1;
	
	@Override
	public int hashCode() {
		if(hash == -1) {
			hash = super.hashCode();
		}
		
		return hash;
	}
	
}
