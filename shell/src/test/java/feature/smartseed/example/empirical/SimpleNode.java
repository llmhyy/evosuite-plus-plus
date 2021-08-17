package feature.smartseed.example.empirical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SimpleNode {
	protected int id;
	ArrayList<String> identifiers;
	public static Collection<SimpleNode> labelList = new ArrayList<SimpleNode>();
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SimpleNode(final int i) {
		this.identifiers = new ArrayList<String>();
		this.id = i;
	}

	public SimpleNode(byte id, String name) {
		this.id = id;
		this.name = name;
	}

	public void setIdentifier(final String s) {
		identifiers.add(s);
	}

	public void dump(final String prefix) throws IOException {

		if (this.identifiers.size() > 0) {
			System.currentTimeMillis();
		} else {
			for (String identifier : this.identifiers) {
				if (!identifier.equals(";") && !identifier.equals("}") && !identifier.equals("{")
						&& !identifier.equals("")) {
					if (identifier.equals("<")) {
						identifier = "*lt";
					}
					if (identifier.equals(">")) {
						identifier = "*gt";
					}
					if (identifier.equals(">>")) {
						identifier = "*rshft";
					}
					if (identifier.equals("<<")) {
						identifier = "*lshft";
					}
					if (identifier.equals("<=")) {
						identifier = "*le";
					}
				}
			}
		}
	}
	
	public static int findLabel(String paramString) {
		Iterator<SimpleNode> iterator = labelList.iterator();
		while (iterator.hasNext()) {
			SimpleNode label = iterator.next();
			if (label.getName().equals(paramString))
				return 4;
		}
		return -1;
	}
	
	public void buildLabelList() {
		for (byte b = 0; b < this.identifiers.size(); b++) {

			if (this.identifiers.get(b).toLowerCase().startsWith("lbl "))
				labelList.add(new SimpleNode(b, this.identifiers.get(b).substring(4)));
		}
	}

}
