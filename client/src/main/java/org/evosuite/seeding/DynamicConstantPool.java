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

import org.evosuite.Properties;
import org.evosuite.result.seedexpr.DynamicPoolEvent;
import org.evosuite.result.seedexpr.EventSequence;
import org.evosuite.result.seedexpr.SamplingDataType;
import org.evosuite.utils.DefaultRandomAccessQueue;
import org.evosuite.utils.RandomAccessQueue;
import org.evosuite.utils.Randomness;
import org.objectweb.asm.Type;

/**
 * @author Gordon Fraser
 * 
 */
public class DynamicConstantPool implements ConstantPool {

	private final RandomAccessQueue<String> stringPool = new DefaultRandomAccessQueue<String>();

	private final RandomAccessQueue<Type> typePool = new DefaultRandomAccessQueue<Type>();

	private final RandomAccessQueue<Integer> intPool = new DefaultRandomAccessQueue<Integer>();

	private final RandomAccessQueue<Double> doublePool = new DefaultRandomAccessQueue<Double>();

	private final RandomAccessQueue<Long> longPool = new DefaultRandomAccessQueue<Long>();

	private final RandomAccessQueue<Float> floatPool = new DefaultRandomAccessQueue<Float>();
	
	private final RandomAccessQueue<Character> charPool = new DefaultRandomAccessQueue<Character>();

	public DynamicConstantPool() {
		/*
		 * all pools HAVE to be non-empty 
		 */
		stringPool.restrictedAdd("");
		if (Properties.TARGET_CLASS != null && !Properties.TARGET_CLASS.isEmpty()) {
			typePool.restrictedAdd(Type.getObjectType(Properties.TARGET_CLASS));
		} else {
			typePool.restrictedAdd(Type.getType(Object.class));
		}
		intPool.restrictedAdd(0);
		longPool.restrictedAdd(0L);
		floatPool.restrictedAdd(0.0f);
		doublePool.restrictedAdd(0.0);
	}

	/* (non-Javadoc)
	 * @see org.evosuite.primitives.ConstantPool#getRandomString()
	 */
	@Override
	public String getRandomString() {
		String value = stringPool.getRandomValue();
		if(Randomness.nextDouble() >= Properties.PRIMITIVE_POOL && Properties.APPLY_SMART_SEED == true) {
			char value_char = getRandomChar();
			double r = Randomness.nextDouble(0, 1);
			int stringSize = value.length();
			if (r > 0.7 && stringSize > 1) {
				char[] charList = value.toCharArray();
				if (r > 0.9) {
					// repalce
					value.replace(value.charAt(Randomness.nextInt(stringSize)), value_char);
				} else if (r > 0.8) {
					// remove
					for (int i = 0; i < stringSize; i++) {
						if (charList[i] == value_char) {
							if(i == 0) {
								value = value.substring(i + 1);
							}else if(i == stringSize - 1) {
								value = value.substring(0, i);
							}else
								value = value.substring(0, i) + value.substring(i + 1);
							break;
						}
					}
				} else{
					// add
					int position = Randomness.nextInt(0, stringSize + 1);
					if(position == 0) {
						value = value_char + value;
					}else if(position == stringSize) {
						value = value + value_char;
					}else
						value = value.substring(0, position) + value_char + value.substring(position);
				}

			}
		}
		EventSequence.addEvent(new DynamicPoolEvent(System.currentTimeMillis(), SamplingDataType.STRING, stringPool.size(), value));
		return value;
	}

	@Override
	public Type getRandomType() {
		Type value = typePool.getRandomValue();
		EventSequence.addEvent(new DynamicPoolEvent(System.currentTimeMillis(), SamplingDataType.CLASS, typePool.size(), value.toString()));
		return value;
	}

	/* (non-Javadoc)
	 * @see org.evosuite.primitives.ConstantPool#getRandomInt()
	 */
	@Override
	public int getRandomInt() {
		int value = intPool.getRandomValue();
		EventSequence.addEvent(new DynamicPoolEvent(System.currentTimeMillis(), SamplingDataType.INT, intPool.size(), String.valueOf(value)));
		return value;
	}

	/* (non-Javadoc)
	 * @see org.evosuite.primitives.ConstantPool#getRandomFloat()
	 */
	@Override
	public float getRandomFloat() {
		float value = floatPool.getRandomValue();
		EventSequence.addEvent(new DynamicPoolEvent(System.currentTimeMillis(), SamplingDataType.FLOAT, floatPool.size(), String.valueOf(value)));
		return value;
	}

	/* (non-Javadoc)
	 * @see org.evosuite.primitives.ConstantPool#getRandomDouble()
	 */
	@Override
	public double getRandomDouble() {
		double value = doublePool.getRandomValue();
		EventSequence.addEvent(new DynamicPoolEvent(System.currentTimeMillis(), SamplingDataType.DOUBLE, doublePool.size(), String.valueOf(value)));
		return value;
	}

	/* (non-Javadoc)
	 * @see org.evosuite.primitives.ConstantPool#getRandomLong()
	 */
	@Override
	public long getRandomLong() {
		long value = longPool.getRandomValue();
		EventSequence.addEvent(new DynamicPoolEvent(System.currentTimeMillis(), SamplingDataType.LONG, longPool.size(), String.valueOf(value)));
		return value;
	}
	
	@Override
	public char getRandomChar() {
		if(Randomness.nextDouble() >= Properties.PRIMITIVE_POOL) {
			int va = intPool.getRandomValue();
			char value = (char) va;
			EventSequence.addEvent(new DynamicPoolEvent(System.currentTimeMillis(), SamplingDataType.INT,
					intPool.size(), String.valueOf(value)));
			return value;
		}
		return (char) (Randomness.nextChar());
	}

	/* (non-Javadoc)
	 * @see org.evosuite.primitives.ConstantPool#add(java.lang.Object)
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
			stringPool.restrictedAdd(string);
		} else if (object instanceof Type) {
			typePool.restrictedAdd((Type) object);
		}

		else if (object instanceof Integer) {
			if (Properties.RESTRICT_POOL) {
				int val = (Integer) object;
				if (Math.abs(val) < Properties.MAX_INT) {
					intPool.restrictedAdd((Integer) object);
				}
			} else {
				intPool.restrictedAdd((Integer) object);
			}
		} else if (object instanceof Long) {
			if (Properties.RESTRICT_POOL) {
				long val = (Long) object;
				if (Math.abs(val) < Properties.MAX_INT) {
					longPool.restrictedAdd((Long) object);
				}
			} else {
				longPool.restrictedAdd((Long) object);
			}
		} else if (object instanceof Float) {
			if (Properties.RESTRICT_POOL) {
				float val = (Float) object;
				if (Math.abs(val) < Properties.MAX_INT) {
					floatPool.restrictedAdd((Float) object);
				}
			} else {
				floatPool.restrictedAdd((Float) object);
			}
		} else if (object instanceof Double) {
			if (Properties.RESTRICT_POOL) {
				double val = (Double) object;
				if (Math.abs(val) < Properties.MAX_INT) {
					doublePool.restrictedAdd((Double) object);
				}
			} else {
				doublePool.restrictedAdd((Double) object);
			}
		} else if (object instanceof Character) {
			if (Properties.RESTRICT_POOL) {
				double val = (Character) object;
				if (Math.abs(val) < Properties.MAX_INT) {
					charPool.restrictedAdd((Character) object);
				}
			} else {
				charPool.restrictedAdd((Character) object);
			}
		}
	}

	@Override
	public String toString() {
		String res = "DynamicConstantPool:{";
		res += "stringPool=" + stringPool.toString() + " ; ";
		res += "typePool=" + typePool.toString() + " ; ";
		res += "intPool=" + intPool.toString() + " ; ";
		res += "longPool=" + longPool.toString() + " ; ";
		res += "floatPool=" + floatPool.toString() + " ; ";
		res += "charPool=" + charPool.toString() + " ; ";
		res += "doublePool=" + doublePool.toString() + "}";	
		return res;
	}

	
}
