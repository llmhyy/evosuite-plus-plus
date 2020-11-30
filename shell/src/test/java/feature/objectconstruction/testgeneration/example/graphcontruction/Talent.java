package feature.objectconstruction.testgeneration.example.graphcontruction;

import java.io.Serializable;

public class Talent implements Serializable {
  private static final long serialVersionUID = 7349793007364986386L;
  
  private String name;
  
  private int value;
  
  private String challenge;
  
  public Talent(String n, int v, String c) {
    setName(n);
    setValue(v);
    setChallenge(c);
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getValue() {
    return this.value;
  }
  
  public void setValue(int value) {
    this.value = value;
  }
  
  public String getChallenge() {
    return this.challenge;
  }
  
  public void setChallenge(String challenge) {
    String c = challenge;
    c = c.substring(2, 10);
    this.challenge = c;
  }
  
  public String toString() {
    return this.name + " (" + this.challenge + ") " + this.value;
  }
}
