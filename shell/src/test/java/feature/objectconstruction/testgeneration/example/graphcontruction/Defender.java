package feature.objectconstruction.testgeneration.example.graphcontruction;

public class Defender extends PlayerOne {
  public static int counter = 0;
  
  public Defender(int paramInt1, int paramInt2) {
    super(paramInt1, paramInt2, ++counter);
  }
  
  public String toString() {
    return "Verteidiger " + getPlayerNumber();
  }
  
  public static void setCounter(int paramInt) {
    counter = paramInt;
  }
}
