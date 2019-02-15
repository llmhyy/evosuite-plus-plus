package com.example;

import java.lang.reflect.Method;
import java.util.List;

import net.sourceforge.beanbin.BeanBinException;
import net.sourceforge.beanbin.data.EntityUtils;

public class BeanBin {

	public static void main(String[] args) throws BeanBinException {
		Class<Object> class0 = Object.class;
		Class<Object> class1 = Object.class;
		boolean boolean0 = EntityUtils.hasBlobs(class0);
		Class<Object> class2 = Object.class;
		boolean boolean1 = EntityUtils.hasBlobs(class2);
		List<Method> list0 = EntityUtils.getSubEntityMethods(class0);


	}

}
