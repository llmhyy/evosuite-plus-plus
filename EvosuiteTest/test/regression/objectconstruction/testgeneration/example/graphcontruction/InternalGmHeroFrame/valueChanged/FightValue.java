package regression.objectconstruction.testgeneration.example.graphcontruction.InternalGmHeroFrame.valueChanged;

import java.io.Serializable;
import java.util.Vector;

public class FightValue implements Serializable {
  private static final long serialVersionUID = 8843015037627509214L;
  
  private String name;
  
  private int attack;
  
  private int defense;
  
  private Vector<Weapon> weapons = new Vector<Weapon>();
  
  public FightValue(String name, int attack, int defense) {
    this.name = name;
    this.attack = attack;
    this.defense = defense;
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getAttack() {
    return this.attack;
  }
  
  public int getDefense() {
    return this.defense;
  }
  
  public Vector<Weapon> getWeapons() {
    return this.weapons;
  }
  
  public void setAttack(int value) {
    this.attack = value;
  }
  
  public void setDefense(int value) {
    this.defense = value;
  }
  
  public String toString() {
    return this.name + " AT: " + this.attack + " PA: " + this.defense;
  }
}
