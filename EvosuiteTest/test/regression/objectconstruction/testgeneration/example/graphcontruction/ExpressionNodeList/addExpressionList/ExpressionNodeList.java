package regression.objectconstruction.testgeneration.example.graphcontruction.ExpressionNodeList.addExpressionList;

import java.util.ArrayList;

public class ExpressionNodeList {
	private ArrayList items = new ArrayList();

	public void addExpressionList(ExpressionNodeList list) {
		for (int i = 0; i < list.size(); i++) {
			int item = list.getItem(i);
			if (!isInList(item))
				addItem(item);
		}
	}

	public int size() {
		return this.items.size();
	}

	public boolean isInList(int item) {
		for (int i = 0; i < size(); i++) {
			if (getItem(i) == item)
				return true;
		}
		return false;
	}

	public int getItem(int index) {
		if (index >= 0 && index < this.items.size())
			return ((Integer) this.items.get(index)).intValue();
		return -1;
	}

	public void addItem(int item) {
		this.items.add(new Integer(item));
	}
}
