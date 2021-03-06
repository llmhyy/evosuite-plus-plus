package feature.smartseed.example.empirical;

public class IMenuItem {
	private int index;
	private int depth;
	private Object myObject;
	private Object myParent;
	private IMenuItem parent;
	public String name;
	public static String staticValue;
//	private IMenuItemRenderer renderer;
	private boolean leaf = true;
	
	public IMenuItem(Object containedObject)
	{
	setContained(containedObject);
	}
	public IMenuItem()
	{
		this.setName("primitive");
		this.setStaticValue("sta");
	}
	public void setContained(Object myObject)
	{
	 this.setMyObject(myObject);
	}
	public Object getContained()
	{
		return myObject;
	}
	public IMenuItem getParent() {
		return parent;
	}
	public void setParent(IMenuItem parent) {
		this.parent = parent;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getMyObject() {
		return myObject;
	}
	public void setMyObject(Object myObject) {
		this.myObject = myObject;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Object getMyParent() {
		return myParent;
	}
	public void setMyParent(Object myParent) {
		this.myParent = myParent;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public String getStaticValue() {
		return staticValue;
	}
	public void setStaticValue(String staticValue) {
		this.staticValue = staticValue;
	}
	
	public static String staticInvokeMethod(String s) {
		if(s.startsWith("static")) {
			return s + "static";
		}
		return s;
	}
}
