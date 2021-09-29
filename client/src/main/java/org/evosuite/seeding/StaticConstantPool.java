/**
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package org.evosuite.seeding;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.result.seedexpr.EventFactory;
import org.evosuite.result.seedexpr.EventSequence;
import org.evosuite.result.seedexpr.SamplingDataType;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;
import org.objectweb.asm.Type;

/**
 * @author Gordon Fraser
 * 
 */
public class StaticConstantPool implements ConstantPool {

	private final Set<String> stringPool = Collections.synchronizedSet(new LinkedHashSet<String>());

	private final Set<Type> typePool = Collections.synchronizedSet(new LinkedHashSet<Type>());

	private final Set<Integer> intPool = Collections.synchronizedSet(new LinkedHashSet<Integer>());

	private final Set<Double> doublePool = Collections.synchronizedSet(new LinkedHashSet<Double>());

	private final Set<Long> longPool = Collections.synchronizedSet(new LinkedHashSet<Long>());

	private final Set<Float> floatPool = Collections.synchronizedSet(new LinkedHashSet<Float>());
	
	private final Set<Character> charPool = Collections.synchronizedSet(new LinkedHashSet<Character>());
	
	private boolean isContextual;

	public StaticConstantPool(boolean isContextual) {
		/*
		 * all pools HAVE to be non-empty 
		 */

		this.isContextual = isContextual;
		
		stringPool.add("");

		if (Properties.TARGET_CLASS != null && !Properties.TARGET_CLASS.isEmpty()) {
			typePool.add(Type.getObjectType(Properties.TARGET_CLASS));
		} else {
			typePool.add(Type.getType(Object.class));
		}

		intPool.add(0);
		intPool.add(1);
		intPool.add(-1);

		longPool.add(0L);
		longPool.add(1L);
		longPool.add(-1L);

		floatPool.add(0.0f);
		floatPool.add(1.0f);
		floatPool.add(-1.0f);

		doublePool.add(0.0);
		doublePool.add(1.0);
		doublePool.add(-1.0);
	}
	
	public StaticConstantPool(boolean isContextual, boolean noInit) {
		/*
		 * all pools HAVE to be non-empty 
		 */

		this.isContextual = isContextual;
		
		stringPool.add("");

		if (Properties.TARGET_CLASS != null && !Properties.TARGET_CLASS.isEmpty()) {
			typePool.add(Type.getObjectType(Properties.TARGET_CLASS));
		} else {
			typePool.add(Type.getType(Object.class));
		}

		intPool.add(0);
		intPool.add(1);
		intPool.add(-1);

		longPool.add(0L);
		longPool.add(1L);
		longPool.add(-1L);

		floatPool.add(0.0f);
		floatPool.add(1.0f);
		floatPool.add(-1.0f);

		doublePool.add(0.0);
		doublePool.add(1.0);
		doublePool.add(-1.0);
	}

	/**
	 * <p>
	 * getRandomString
	 * </p>
	 * 
	 * @return a {@link java.lang.String} object.
	 */
	@Override
	public String getRandomString() {
		String value = Randomness.choice(stringPool);
		EventSequence.addEvent(EventFactory.createStaticEvent(isContextual, System.currentTimeMillis(), SamplingDataType.STRING, stringPool.size(), String.valueOf(value)));
		return value;
	}

	@Override
	public Type getRandomType() {
		Type value = Randomness.choice(typePool);
		EventSequence.addEvent(EventFactory.createStaticEvent(isContextual, System.currentTimeMillis(), SamplingDataType.CLASS, typePool.size(), String.valueOf(value)));
		return value;
	}

	/**
	 * <p>
	 * getRandomInt
	 * </p>
	 * 
	 * @return a int.
	 */
	@Override
	public int getRandomInt() {
		int value = Randomness.choice(intPool);
//		System.currentTimeMillis();
		EventSequence.addEvent(EventFactory.createStaticEvent(isContextual, System.currentTimeMillis(), SamplingDataType.INT, intPool.size(), String.valueOf(value)));
		return value;
	}

	/**
	 * <p>
	 * getRandomFloat
	 * </p>
	 * 
	 * @return a float.
	 */
	@Override
	public float getRandomFloat() {
		float value = Randomness.choice(floatPool);
		EventSequence.addEvent(EventFactory.createStaticEvent(isContextual, System.currentTimeMillis(), SamplingDataType.FLOAT, floatPool.size(), String.valueOf(value)));
		return value;
	}

	/**
	 * <p>
	 * getRandomDouble
	 * </p>
	 * 
	 * @return a double.
	 */
	@Override
	public double getRandomDouble() {
		double value = Randomness.choice(doublePool);
		EventSequence.addEvent(EventFactory.createStaticEvent(isContextual, System.currentTimeMillis(), SamplingDataType.DOUBLE, doublePool.size(), String.valueOf(value)));
		return value;
	}

	/**
	 * <p>
	 * getRandomLong
	 * </p>
	 * 
	 * @return a long.
	 */
	@Override
	public long getRandomLong() {
		long value = Randomness.choice(longPool);
		EventSequence.addEvent(EventFactory.createStaticEvent(isContextual, System.currentTimeMillis(), SamplingDataType.LONG, longPool.size(), String.valueOf(value)));
		return value;
	}
	
	/**
	 * <p>
	 * getRandomChar
	 * </p>
	 * 
	 * @return a Character.
	 */
	@Override
	public char getRandomChar() {
		Character value = Randomness.choice(charPool);
		if(value == null) {
			value = Randomness.nextChar();
		}
		EventSequence.addEvent(EventFactory.createStaticEvent(isContextual, System.currentTimeMillis(), SamplingDataType.CHARACTER, charPool.size(), String.valueOf(value)));
		return value;
	}

	/**
	 * <p>
	 * add
	 * </p>
	 * 
	 * @param object
	 *            a {@link java.lang.Object} object.
	 */
	@Override
	public void add(Object object) {
		// We don't add null because this is explicitly handled in the TestFactory
		if (object == null)
			return;

		if (object instanceof String) {
			String string = (String) object;
			if(string.length() > Properties.MAX_STRING)
				return;
			// String literals are constrained to 65535 bytes 
			// as they are stored in the constant pool
			if (string.length() > 65535)
				return;
			stringPool.add(string);
		} else if (object instanceof Type) {
			while (((Type) object).getSort() == Type.ARRAY) {
				object = ((Type) object).getElementType();
			}
			typePool.add((Type) object);
		}

		else if (object instanceof Integer) {
			if (Properties.RESTRICT_POOL) {
				int val = (Integer) object;
				if (Math.abs(val) < Properties.MAX_INT) {
					intPool.add((Integer) object);
				}
				if (Properties.APPLY_CHAR_POOL) {
					if (val >= 0 && val <= 255) {
						char c = (char) val;
						charPool.add((Character) c);
					}
				}
			} else {
				intPool.add((Integer) object);
				if (Properties.APPLY_CHAR_POOL) {
					int val = (Integer) object;
					if (val >= 0 && val <= 255) {
						char c = (char) val;
						charPool.add((Character) c);
					}
				}
			}
		} else if (object instanceof Long) {
			if (Properties.RESTRICT_POOL) {
				long val = (Long) object;
				if (Math.abs(val) < Properties.MAX_INT) {
					longPool.add((Long) object);
				}
			} else {
				longPool.add((Long) object);
			}
		} else if (object instanceof Float) {
			if (Properties.RESTRICT_POOL) {
				float val = (Float) object;
				if (Math.abs(val) < Properties.MAX_INT) {
					floatPool.add((Float) object);
				}
			} else {
				floatPool.add((Float) object);
			}
		} else if (object instanceof Double) {
			if (Properties.RESTRICT_POOL) {
				double val = (Double) object;
				if (Math.abs(val) < Properties.MAX_INT) {
					doublePool.add((Double) object);
				}
			} else {
				doublePool.add((Double) object);
			}
		} else if (object instanceof Character) {
			if (Properties.RESTRICT_POOL) {
				int val = (Character) object;
				if (Math.abs(val) < Properties.MAX_INT) {
					charPool.add((Character) object);
				}
			} else {
				charPool.add((Character) object);
			}
		} else {
			LoggingUtils.getEvoLogger().info("Constant of unknown type: " + object.getClass());
		}
	}
	
    public long poolSize() {
    	long num = 0;
    	num += this.stringPool.size();
//    	num += this.typePool.size();
    	num += this.intPool.size();
    	num += this.doublePool.size();
    	num += this.longPool.size();
    	num += this.floatPool.size();
//    	num += this.charPool.size();
    	
    	return num;
    }
    
    public void clear() {
    	this.stringPool.clear();
    	this.typePool.clear();
    	this.intPool.clear();
    	this.doublePool.clear();
    	this.longPool.clear();
    	this.floatPool.clear();
    	this.charPool.clear();
    }
    

}
