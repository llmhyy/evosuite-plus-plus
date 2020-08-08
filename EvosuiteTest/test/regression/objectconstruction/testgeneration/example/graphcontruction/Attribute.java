package regression.objectconstruction.testgeneration.example.graphcontruction;

import java.io.Serializable;

public class Attribute implements Serializable {
  private static final long serialVersionUID = 9100340328348471402L;
  
  private int value;
  
  private String name;
  
  private String shortcut;
  
  public Attribute(String name, String shortcut, int value) {
    this.name = name;
    this.value = value;
    this.shortcut = shortcut;
  }
  
  public int getValue() {
    return this.value;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getShortcut() {
    return this.shortcut;
  }
  
  public String toString() {
    return this.name + " (" + this.shortcut + ") :" + this.value;
  }
}
