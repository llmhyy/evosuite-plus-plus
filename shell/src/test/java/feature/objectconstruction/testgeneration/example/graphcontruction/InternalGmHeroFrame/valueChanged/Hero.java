package feature.objectconstruction.testgeneration.example.graphcontruction.InternalGmHeroFrame.valueChanged;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;
import org.dom4j.Document;
import org.dom4j.Element;

import feature.objectconstruction.testgeneration.example.graphcontruction.Attribute;
import feature.objectconstruction.testgeneration.example.graphcontruction.Special;
import feature.objectconstruction.testgeneration.example.graphcontruction.Spell;
import feature.objectconstruction.testgeneration.example.graphcontruction.Talent;

public class Hero implements Serializable {
  private static final long serialVersionUID = 4235381890809856112L;
  
  private Vector<Attribute> attributes;
  
  Vector<Talent> talents;
  
  Vector<FightValue> fightvalues;
  
  Vector<Special> specials;
  
  private String name;
  
  private int INI;
  
  private int currINI = 0;
  
  private int currLEP = 0;
  
  private int currAUP = 0;
  
  private int currASP = 0;
  
  private int currKAP = 0;
  
  private Weapon selectedWeapon = null;
  
  private boolean priest = false;
  
  private boolean magican = false;
  
  public Hero(Document xml) {
    Element root = xml.getRootElement();
    this.talents = new Vector<Talent>();
    this.attributes = new Vector<Attribute>();
    this.fightvalues = new Vector<FightValue>();
    this.specials = new Vector<Special>();
    iter(root);
    this.currLEP = getAttrValue("LEP");
    this.currAUP = getAttrValue("AUP");
    this.currASP = getAttrValue("ASP");
    this.currKAP = getAttrValue("KAP");
  }
  
  private void iter(Element e) {
    for (Iterator<Element> i = e.elementIterator(); i.hasNext(); ) {
      Element next = i.next();
      if (next.getName().equals("held"))
        this.name = next.attributeValue("name"); 
      if (next.getName().equals("eigenschaft")) {
        if (next.attributeValue("name").equals("Mut"))
          this.attributes.addElement(new Attribute("Mut", "MU", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")))); 
        if (next.attributeValue("name").equals("Klugheit"))
          this.attributes.addElement(new Attribute("Klugheit", "KL", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")))); 
        if (next.attributeValue("name").equals("Intuition"))
          this.attributes.addElement(new Attribute("Intuition", "IN", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")))); 
        if (next.attributeValue("name").equals("Charisma"))
          this.attributes.addElement(new Attribute("Charisma", "CH", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")))); 
        if (next.attributeValue("name").equals("Fingerfertigkeit"))
          this.attributes.addElement(new Attribute("Fingerfertigkeit", "FF", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")))); 
        if (next.attributeValue("name").equals("Gewandtheit"))
          this.attributes.addElement(new Attribute("Gewandtheit", "GE", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")))); 
        if (next.attributeValue("name").equals("Konstitution"))
          this.attributes.addElement(new Attribute("Konstitution", "KO", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")))); 
        if (next.attributeValue("name").endsWith("perkraft"))
          this.attributes.addElement(new Attribute("K", "KK", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")))); 
        if (next.attributeValue("name").equals("Lebensenergie"))
          this.attributes.addElement(new Attribute("Lebensenergie", "LEP", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")) + (getAttrValue("KO") * 2 + getAttrValue("KK")) / 2)); 
        if (next.attributeValue("name").equals("Ausdauer"))
          this.attributes.addElement(new Attribute("Ausdauer", "AUP", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")) + (getAttrValue("MU") + getAttrValue("GE") + getAttrValue("KO")) / 2)); 
        if (next.attributeValue("name").equals("Astralenergie"))
          this.attributes.addElement(new Attribute("Astralenergie", "ASP", Integer.parseInt(next.attributeValue("grossemeditation")) + Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")) + (getAttrValue("MU") + getAttrValue("IN") + getAttrValue("CH")) / 2)); 
        if (next.attributeValue("name").equals("Karmaenergie"))
          this.attributes.addElement(new Attribute("Karmaenergie", "KAP", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value")))); 
        if (next.attributeValue("name").equals("ini")) {
          this.attributes.addElement(new Attribute("ini", "INI", Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value"))));
          this.INI = Integer.parseInt(next.attributeValue("mod")) + Integer.parseInt(next.attributeValue("value"));
        } 
      } 
      if (next.getName().equals("vorteil"));
      if (next.getName().equals("sonderfertigkeit")) {
        String name = next.attributeValue("name");
        name = name.replace(' ', '_');
        try {
          Special s = (Special)Special.class.getDeclaredField(name.toUpperCase()).get(Special.FINTE);
          this.specials.addElement(s);
        } catch (Exception e1) {
          e1.printStackTrace();
        } 
      } 
      if (next.getName().equals("talent")) {
        String name = next.attributeValue("name");
        String chall = next.attributeValue("probe");
        int val = Integer.parseInt(next.attributeValue("value"));
        Talent t = new Talent(name, val, chall);
        this.talents.addElement(t);
      } 
      if (next.getName().equals("zauber")) {
        String name = next.attributeValue("name");
        String chall = next.attributeValue("probe");
        int val = Integer.parseInt(next.attributeValue("value"));
        Spell s = new Spell(name, val, chall);
        this.talents.addElement(s);
      } 
      if (next.getName().equals("kampfwerte")) {
        String name = next.attributeValue("name");
        FightValue fv = new FightValue(name, 0, 0);
        if (name.matches("(Raufen)|(Ringen)"))
          try {
            Weapon w = (Weapon)Weapon.class.getDeclaredField(("Hand_" + name).toUpperCase()).get(new Weapon());
            w.setType(fv);
            fv.getWeapons().addElement(w);
          } catch (Exception e1) {
            e1.printStackTrace();
          }  
        this.fightvalues.addElement(fv);
      } 
      if (next.getName().equals("attacke"))
        searchFV(next.getParent().attributeValue("name")).setAttack(Integer.parseInt(next.attributeValue("value"))); 
      if (next.getName().equals("parade"))
        searchFV(next.getParent().attributeValue("name")).setDefense(Integer.parseInt(next.attributeValue("value"))); 
      if (next.getName().equals("ausruestungneu") && 
        !next.attributeValue("name").equals("jagtwaffe")) {
        Weapon weapon;
        String name = next.attributeValue("waffenname");
        if (name.matches(".*[\\(\\)].*")) {
          String[] split = name.split("[\\(\\)]");
          String tmpName = "";
          for (String s : split)
            tmpName = tmpName + s; 
          name = tmpName;
        } 
        name = name.replace('.', '_');
        name = name.replace(' ', '_');
        name = name.replace("", "ss");
        try {
          weapon = (Weapon)Weapon.class.getDeclaredField(name.toUpperCase()).get(new Weapon());
        } catch (Exception e1) {
          weapon = new Weapon(name);
        } 
        FightValue fv = searchFV(next.attributeValue("talent"));
        if (fv != null) {
          weapon.setType(fv);
          fv.getWeapons().addElement(weapon);
        } 
      } 
      if (next.elements().size() != 0)
        iter(next); 
    } 
  }
  
  private FightValue searchFV(String parentName) {
    for (FightValue fv : this.fightvalues) {
      if (fv.getName().equals(parentName))
        return fv; 
    } 
    return null;
  }
  
  public String attack(String special, int mod) {
    return null;
  }
  
  public String defense(String special, int mod) {
    return null;
  }
  
  public String cast(String spell, int mod) {
    return null;
  }
  
  public String use(String talent, int mod) {
    return null;
  }
  
  public String toString() {
    return this.name;
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getINI() {
    return this.INI;
  }
  
  public Vector<Talent> getTalents() {
    return this.talents;
  }
  
  public int getAttrValue(String shortcut) {
    for (int i = 0; i < this.attributes.size(); i++) {
      if (((Attribute)this.attributes.elementAt(i)).getShortcut().equals(shortcut))
        return ((Attribute)this.attributes.elementAt(i)).getValue(); 
    } 
    return -1;
  }
  
  public Vector<Attribute> getAttributes() {
    return this.attributes;
  }
  
  public Vector<FightValue> getFightvalues() {
    return this.fightvalues;
  }
  
  public Vector<Special> getSpecials() {
    return this.specials;
  }
  
  public int getCurrINI() {
    return this.currINI;
  }
  
  public int getCurrLEP() {
    return this.currLEP;
  }
  
  public int getCurrAUP() {
    return this.currAUP;
  }
  
  public int getCurrASP() {
    return this.currASP;
  }
  
  public int getCurrKAP() {
    return this.currKAP;
  }
  
  public Weapon getSelectedWeapon() {
    return this.selectedWeapon;
  }
  
  public void setSelectedWeapon(Weapon selectedWeapon) {
    this.selectedWeapon = selectedWeapon;
  }
  
  public void setCurrINI(int currINI) {
    this.currINI = currINI;
  }
  
  public boolean isPriest() {
    return this.priest;
  }
  
  public boolean isMagican() {
    return this.magican;
  }
}
