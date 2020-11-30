package feature.objectconstruction.testgeneration.example.graphcontruction;

import feature.objectconstruction.testgeneration.example.graphcontruction.InternalGmHeroFrame.valueChanged.Hero;

public class ValueHolder {
  public String name;
  
  public Hero parent;
  
  public ValueHolder(String name, Hero parent) {
    this.name = name;
    this.parent = parent;
  }
  
  public String toString() {
    return this.name;
  }
}