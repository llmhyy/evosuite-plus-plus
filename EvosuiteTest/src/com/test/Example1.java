package com.test;

import java.util.ArrayList;
import java.util.List;

public class Example1 {
	public int test(String str){
		List<String> list = new ArrayList<>();
		list.add("abc");
		if(list.contains(str)){
			return 1;
		}
		
		return 0;
	}
}
