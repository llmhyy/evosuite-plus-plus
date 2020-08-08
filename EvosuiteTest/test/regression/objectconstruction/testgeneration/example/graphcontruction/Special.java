package regression.objectconstruction.testgeneration.example.graphcontruction;

import java.io.Serializable;

public class Special implements Serializable {
  public static Special FINTE = new Special("Finte", "attack", 5);
  
  public static Special AUSWEICHEN_I = new Special("Ausweichen I", "passive", -3);
  
  public static Special AUSWEICHEN_II = new Special("Ausweichen II", "passive", -6);
  
  public static Special AUSWEICHEN_III = new Special("Ausweichen III", "passiv", -9);
  
  public static Special AUFMERKSAMKEIT = new Special("Aufmerksamkeit", "passiv", -4);
  
  private String name;
  
  private String mode;
  
  private int baseMod;
  
  public Special(String name, String mode, int baseMod) {
    this.name = name;
    this.mode = mode;
    this.baseMod = baseMod;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getMode() {
    return this.mode;
  }
  
  public int getBaseMod() {
    return this.baseMod;
  }
}
