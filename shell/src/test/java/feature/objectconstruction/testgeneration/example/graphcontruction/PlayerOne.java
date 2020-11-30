package feature.objectconstruction.testgeneration.example.graphcontruction;

import java.io.Serializable;

public abstract class PlayerOne implements HighlightableItem, Serializable {
  private static final long serialVersionUID = -5372891552466311536L;
  
  private int start_x;
  
  private int start_y;
  
  private int current_x;
  
  private int current_y;
  
  private int playerNumber;
  
  private boolean hasBall;
  
  protected boolean hightlighted;
  
  protected boolean marked;
  
  public PlayerOne(int paramInt1, int paramInt2, int paramInt3) {
    this.start_x = paramInt1;
    this.start_y = paramInt2;
    this.current_x = paramInt1;
    this.current_y = paramInt2;
    this.playerNumber = paramInt3;
    this.hasBall = false;
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
  
  public void setCurrentPosition(int paramInt1, int paramInt2) {
    this.current_x = paramInt1;
    this.current_y = paramInt2;
  }
  
  public boolean hasBall() {
    return this.hasBall;
  }
  
  public void setHasBall(boolean paramBoolean) {
    this.hasBall = paramBoolean;
  }
  
  public int getPlayerNumber() {
    return this.playerNumber;
  }
  
  public int getStart_x() {
    return this.start_x;
  }
  
  public int getStart_y() {
    return this.start_y;
  }
  
  public void setStart_x(int paramInt) {
    this.start_x = paramInt;
    this.current_x = paramInt;
  }
  
  public void setStart_y(int paramInt) {
    this.start_y = paramInt;
    this.current_y = paramInt;
  }
  
  public void setStartPosition(int paramInt1, int paramInt2) {
    setStart_x(paramInt1);
    setStart_y(paramInt2);
  }
  
  public void resetPosition() {
    this.current_x = this.start_x;
    this.current_y = this.start_y;
  }
  
  public void setMarked(boolean paramBoolean) {
    this.marked = paramBoolean;
  }
  
  public void setHighlight(boolean paramBoolean) {
    this.hightlighted = paramBoolean;
  }
  
  public boolean isHightlighted() {
    return this.hightlighted;
  }
  
  public boolean isMarked() {
    return this.marked;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null)
      return false; 
    if (paramObject.getClass() != getClass())
      return false; 
    PlayerOne player = (PlayerOne)paramObject;
    return !(player.isMarked() != isMarked() || player.hasBall != hasBall() || player.getStart_x() != getStart_x() || player.getStart_y() != getStart_y() || player.getPlayerNumber() != getPlayerNumber());
  }
}
