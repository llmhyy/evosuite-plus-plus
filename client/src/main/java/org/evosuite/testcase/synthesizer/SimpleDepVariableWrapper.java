package org.evosuite.testcase.synthesizer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Simplified form of DepVariableWrapper for use in graph visualisation.
 *
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@json_id")
public class SimpleDepVariableWrapper implements Serializable {
	private static final long serialVersionUID = -3231490456320227506L;
	
	public String shortLabel;
	
	public List<SimpleDepVariableWrapper> parents = new ArrayList<>();
	
	public List<SimpleDepVariableWrapper> children = new ArrayList<>();
	
	@Override
	public String toString() {
		return shortLabel;
	}
	
	@Override
	public int hashCode() {
		return shortLabel.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleDepVariableWrapper)) {
			return false;
		}
		
		SimpleDepVariableWrapper other = (SimpleDepVariableWrapper) obj;
		return this.shortLabel.equals(other.shortLabel);
	}
}
