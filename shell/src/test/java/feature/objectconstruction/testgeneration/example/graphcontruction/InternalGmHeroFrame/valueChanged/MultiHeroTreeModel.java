package feature.objectconstruction.testgeneration.example.graphcontruction.InternalGmHeroFrame.valueChanged;

import java.util.Vector;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import feature.objectconstruction.testgeneration.example.graphcontruction.ValueHolder;

public class MultiHeroTreeModel implements TreeModel {
  Vector<TreeModelListener> list = new Vector<TreeModelListener>();
  
  Vector<Hero> heros = new Vector<Hero>();
  
  String root;
  
  public MultiHeroTreeModel(String name) {
    this.root = name;
  }
  
  public MultiHeroTreeModel(String name, Vector<Hero> heros) {
    this.root = name;
    this.heros = heros;
  }
  
  public void addTreeModelListener(TreeModelListener l) {
    this.list.addElement(l);
  }
  
  public Object getChild(Object parent, int index) {
    if (parent.getClass().equals(String.class)) {
      String p = parent.toString();
      if (p.equals(this.root))
        return this.heros.elementAt(index); 
    } 
    if (parent.getClass().equals(Hero.class)) {
      Hero h = (Hero)parent;
      switch (index) {
        case 0:
          return new ValueHolder("Attributes", h);
        case 1:
          return new ValueHolder("Talents", h);
        case 2:
          return new ValueHolder("Weapons", h);
      } 
      return null;
    } 
    if (parent.getClass().equals(ValueHolder.class)) {
      ValueHolder vh = (ValueHolder)parent;
      if (vh.name.equals("Attributes"))
        return vh.parent.getAttributes().elementAt(index); 
      if (vh.name.equals("Talents"))
        return vh.parent.getTalents().elementAt(index); 
      if (vh.name.equals("Weapons"))
        return vh.parent.getFightvalues().elementAt(index); 
    } 
    if (parent.getClass().equals(FightValue.class))
      return ((FightValue)parent).getWeapons().elementAt(index); 
    return null;
  }
  
  public int getChildCount(Object parent) {
    if (parent.getClass().equals(String.class)) {
      String p = parent.toString();
      if (p.equals(this.root))
        return this.heros.size(); 
    } 
    if (parent.getClass().equals(Hero.class))
      return 3; 
    if (parent.getClass().equals(ValueHolder.class)) {
      ValueHolder vh = (ValueHolder)parent;
      String p = parent.toString();
      if (p.equals("Attributes"))
        return vh.parent.getAttributes().size(); 
      if (p.equals("Talents"))
        return vh.parent.getTalents().size(); 
      if (p.equals("Weapons"))
        return vh.parent.getFightvalues().size(); 
    } 
    if (parent.getClass().equals(FightValue.class))
      return ((FightValue)parent).getWeapons().size(); 
    return 0;
  }
  
  public int getIndexOfChild(Object parent, Object child) {
    if (parent.toString().equals(this.root))
      return this.heros.indexOf(child); 
    if (parent.getClass().equals(Hero.class)) {
      String s = child.toString();
      if (s.equals("Attributes"))
        return 0; 
      if (s.equals("Talents"))
        return 1; 
      if (s.equals("Weapons"))
        return 2; 
    } 
    if (parent.getClass().equals(ValueHolder.class)) {
      ValueHolder vh = (ValueHolder)parent;
      String p = vh.toString();
      if (p.equals("Talents"))
        return vh.parent.getTalents().indexOf(child); 
      if (p.equals("Attributes"))
        return vh.parent.getAttributes().indexOf(child); 
      if (p.equals("Weapons"))
        return vh.parent.getFightvalues().indexOf(child); 
    } 
    if (parent.getClass().equals(FightValue.class))
      return ((FightValue)parent).getWeapons().indexOf(child); 
    return -1;
  }
  
  public Object getRoot() {
    return this.root;
  }
  
  public boolean isLeaf(Object node) {
    String s = node.toString();
    if (s.equals(this.root))
      return false; 
    if (node.getClass().equals(Hero.class))
      return false; 
    if (s.equals("Attributes"))
      return false; 
    if (s.equals("Talents"))
      return false; 
    if (s.equals("Weapons"))
      return false; 
    if (node.getClass().equals(FightValue.class))
      return false; 
    return true;
  }
  
  public void removeTreeModelListener(TreeModelListener l) {
    this.list.removeElement(l);
  }
  
  public void valueForPathChanged(TreePath path, Object newValue) {}
  
  protected void fireTreeStructureChanged(Vector<Hero> h2) {
    this.heros = h2;
    TreeModelEvent e = new TreeModelEvent(this, this.heros.toArray());
    for (TreeModelListener tml : this.list)
      tml.treeStructureChanged(e); 
  }
}
