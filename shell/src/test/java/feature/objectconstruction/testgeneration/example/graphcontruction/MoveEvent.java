package feature.objectconstruction.testgeneration.example.graphcontruction;

import java.io.Serializable;

public class MoveEvent implements Comparable<MoveEvent>, Serializable {
  private static final long serialVersionUID = -5372891552466311536L;
  
  protected PlayerOne player;
  
  protected int sequenceNr;
  
  protected int delay;
  
  protected MovePoint destinationPoint;
  
  protected MovePoint controlPoint;
  
  protected int oldPlayerX;
  
  protected int oldPlayerY;
  
  protected boolean destinationPointTemporary = true;
  
  protected boolean controlPointTemporary = true;
  
  protected boolean marked = false;
  
  public MoveEvent(PlayerOne paramPlayer, int paramInt) {
    this(paramPlayer, paramInt, 0);
  }
  
  public MoveEvent(PlayerOne paramPlayer, int paramInt1, int paramInt2) {
    this.player = paramPlayer;
    this.sequenceNr = paramInt1;
    this.delay = paramInt2;
    this.destinationPoint = null;
    this.controlPoint = null;
    this.oldPlayerX = paramPlayer.getCurrent_x();
    this.oldPlayerY = paramPlayer.getCurrent_y();
  }
  
  public int getDelay() {
    return this.delay;
  }
  
  public void setDelay(int paramInt) {
    this.delay = paramInt;
  }
  
  public PlayerOne getPlayer() {
    return this.player;
  }
  
  public void setPlayer(PlayerOne paramPlayer) {
    this.player = paramPlayer;
  }
  
  public int getSequenceNr() {
    return this.sequenceNr;
  }
  
  public void setSequenceNr(int paramInt) {
    this.sequenceNr = paramInt;
  }
  
  public int compareTo(MoveEvent paramMoveEvent) {
    return (this.sequenceNr < paramMoveEvent.sequenceNr) ? -1 : ((this.sequenceNr > paramMoveEvent.sequenceNr) ? 1 : ((this.delay < paramMoveEvent.getDelay()) ? -1 : ((this.delay > paramMoveEvent.getDelay()) ? 1 : ((this.player.getPlayerNumber() < paramMoveEvent.getPlayer().getPlayerNumber()) ? -1 : ((this.player.getPlayerNumber() > paramMoveEvent.getPlayer().getPlayerNumber()) ? 1 : ((this.player instanceof Offender && paramMoveEvent.getPlayer() instanceof Defender) ? -1 : ((this.player instanceof Defender && paramMoveEvent.getPlayer() instanceof Offender) ? 1 : ((this instanceof PassEvent && !(paramMoveEvent instanceof PassEvent)) ? -1 : ((paramMoveEvent instanceof PassEvent && !(this instanceof PassEvent)) ? 1 : 0)))))))));
  }
  
  public void setPoint(MovePoint paramMovePoint, int paramInt1, int paramInt2) {
    if (paramMovePoint.equals(this.controlPoint)) {
      setControlPoint(paramInt1, paramInt2, false);
    } else if (paramMovePoint.equals(this.destinationPoint)) {
      setDestinationPoint(paramInt1, paramInt2, false);
    } 
  }
  
  public void setControlPoint(int paramInt1, int paramInt2, boolean paramBoolean) {
    this.controlPointTemporary = paramBoolean;
    if (this.controlPoint == null) {
      this.controlPoint = new MovePoint(paramInt1, paramInt2);
    } else {
      this.controlPoint.setCurrent_x(paramInt1);
      this.controlPoint.setCurrent_y(paramInt2);
    } 
  }
  
  public void setDestinationPoint(int paramInt1, int paramInt2, boolean paramBoolean) {
    this.destinationPointTemporary = paramBoolean;
    if (this.destinationPoint == null) {
      this.destinationPoint = new MovePoint(paramInt1, paramInt2);
    } else {
      this.destinationPoint.setCurrent_x(paramInt1);
      this.destinationPoint.setCurrent_y(paramInt2);
    } 
  }
  
  public String toString() {
    String str = "";
    if (this.destinationPoint == null) {
      str = "Laufweg nicht definiert";
    } else {
      str = "Laufweg zu Position (" + this.destinationPoint.getCurrent_x() + "," + this.destinationPoint.getCurrent_y() + ")";
    } 
    return str;
  }
  
  public int getDestinationX() {
    return this.destinationPoint.getCurrent_x();
  }
  
  public int getDestinationY() {
    return this.destinationPoint.getCurrent_y();
  }
  
  public MovePoint getControlPoint() {
    return this.controlPoint;
  }
  
  public MovePoint getDestinationPoint() {
    return this.destinationPoint;
  }
  
  public int getControlPointX() {
    return this.controlPoint.getCurrent_x();
  }
  
  public int getControlPointY() {
    return this.controlPoint.getCurrent_y();
  }
  
  public boolean isControlPointSet() {
    return !this.controlPointTemporary;
  }
  
  public boolean isDestinationPointSet() {
    return !this.destinationPointTemporary;
  }
  
  public boolean isMarked() {
    return this.marked;
  }
  
  public void setMarked(boolean paramBoolean) {
    this.marked = paramBoolean;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null)
      return false; 
    if (paramObject.getClass() != getClass())
      return false; 
    MoveEvent moveEvent = (MoveEvent)paramObject;
    if ((((moveEvent.getControlPoint() == null) ? 1 : 0) ^ ((getControlPoint() == null) ? 1 : 0)) != 0)
      return false; 
    if (moveEvent.getControlPoint() == null) {
      if (moveEvent.getDestinationX() != getDestinationX() || moveEvent.getDestinationY() != getDestinationY() || moveEvent.getDelay() != getDelay() || !moveEvent.getPlayer().equals(getPlayer()) || moveEvent.getSequenceNr() != getSequenceNr())
        return false; 
    } else if (moveEvent.getControlPointX() != getControlPointX() || moveEvent.getControlPointY() != getControlPointY() || moveEvent.getDestinationX() != getDestinationX() || moveEvent.getDestinationY() != getDestinationY() || moveEvent.getDelay() != getDelay() || !moveEvent.getPlayer().equals(getPlayer()) || moveEvent.getSequenceNr() != getSequenceNr()) {
      return false;
    } 
    return true;
  }
}
