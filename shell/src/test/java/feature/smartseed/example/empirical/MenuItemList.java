package feature.smartseed.example.empirical;

import java.util.Iterator;
import java.util.Vector;

public class MenuItemList {
	private static String lineseparator = System.getProperty("line.separator");
	private Vector children;
	private IMenuItem parentMenuItem;
	
	public MenuItemList(IMenuItem parent) {
	this.setParentMenuItem(parent);
	this.children = new Vector();
	}

	public IMenuItem getParentMenuItem() {
		return parentMenuItem;
	}

	public void setParentMenuItem(IMenuItem parentMenuItem) {
		this.parentMenuItem = parentMenuItem;
	}
	
	public Iterator getChildrenIterator() {
		return this.children.iterator();
	}
	public IMenuItem getMenuItem() {
		return this.parentMenuItem;
	}
	
	public int getChildrenSize() {
		return this.children.size();
		}
	public void addChild(MenuItemList item) {
		if (this.parentMenuItem == null) {
			item.getMenuItem().setDepth(0);
			} else {
				item.getMenuItem().setDepth(this.parentMenuItem.getDepth() + 1);
				}
		this.children.add(item);
		}
}
