package org.evosuite.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

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
	
	@Override
	public Object clone() {
		ImmutableArrayList<?> v = (ImmutableArrayList<?>) super.clone();
        try {
            
            Field elementDataField = 
            		ArrayList.class.getDeclaredField("elementData");
            elementDataField.setAccessible(true);
            Object[] elementData = (Object[]) elementDataField.get(this);
            
            Field sizeField = 
            		ArrayList.class.getDeclaredField("size");
            sizeField.setAccessible(true);
            int size = (int) sizeField.get(this);
            
            Field vElementDataField = 
            		ArrayList.class.getDeclaredField("elementData");
            vElementDataField.setAccessible(true);
            Object[] vElementData = (Object[]) vElementDataField.get(v);
            
            vElementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
        } catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
        
        return v;
    }
}
