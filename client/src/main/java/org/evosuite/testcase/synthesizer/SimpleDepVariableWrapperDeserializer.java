package org.evosuite.testcase.synthesizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class SimpleDepVariableWrapperDeserializer extends KeyDeserializer {
	// Maps @json_id to instance of SimpleDepVariableWrapper.
	private Map<String, SimpleDepVariableWrapper> memory = new HashMap<>();
	
	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("key = [" + key + "]");
		return null;
	}

}
