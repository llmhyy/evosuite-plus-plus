package feature.objectconstruction.testgeneration.example.graphcontruction;

import java.io.Serializable;

public class Spell extends Talent implements Serializable {
  private static final long serialVersionUID = -5760627863734848549L;
  
  public Spell(String name, int value, String chal) {
    super(name, value, chal);
  }
}
