package regression.objectconstruction.testgeneration.example.graphcontruction;

import java.io.Serializable;

public class Circle implements HighlightableItem, Serializable {
  private static final long serialVersionUID = -5372891552466311536L;
  
  private int current_x;
  
  private int current_y;
  
  private int radius;
  
  private boolean highlighted = false;
  
  public Circle(int paramInt1, int paramInt2, int paramInt3) {
    this.current_x = paramInt1;
    this.current_y = paramInt2;
    this.radius = paramInt3;
  }
  
  public int getCurrent_x() {
    return this.current_x;
  }
  
  public void setCurrent_x(int paramInt) {
    this.current_x = paramInt;
  }
  
  public int getCurrent_y() {
    return this.current_y;
  }
  
  public void setCurrent_y(int paramInt) {
    this.current_y = paramInt;
  }
  
  public int getRadius() {
    return this.radius;
  }
  
  public void setHighlight(boolean paramBoolean) {
    this.highlighted = paramBoolean;
  }
  
  public boolean isHighlighted() {
    return this.highlighted;
  }
}
