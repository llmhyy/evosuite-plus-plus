package regression.objectconstruction.testgeneration.example.graphcontruction;

public class PassEvent extends MoveEvent {
  private static final long serialVersionUID = -5172891552466311536L;
  
  private Offender destinationPlayer = null;
  
  private boolean goalPass = false;
  
  public PassEvent(PlayerOne paramPlayer, int paramInt1, int paramInt2) {
    super(paramPlayer, paramInt1, paramInt2);
  }
  
  public void setDestinationPlayer(Offender paramOffender, boolean paramBoolean) {
    this.destinationPlayer = paramOffender;
    this.destinationPointTemporary = paramBoolean;
  }
  
  public Offender getDestinationPlayer() {
    return this.destinationPlayer;
  }
  
  public boolean isGoalPass() {
    return this.goalPass;
  }
  
  public void setGoalPass(boolean paramBoolean) {
    this.goalPass = paramBoolean;
    if (paramBoolean)
      this.destinationPointTemporary = false; 
  }
  
  public String toString() {
    String str = "";
    if (this.goalPass) {
      str = "Torwurf";
    } else if (this.destinationPlayer == null) {
      str = "Passweg nicht definiert";
    } else {
      str = "Pass zu Spieler \" " + this.destinationPlayer + "\"";
    } 
    return str;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null)
      return false; 
    if (paramObject.getClass() != getClass())
      return false; 
    PassEvent passEvent = (PassEvent)paramObject;
    if ((((passEvent.getDestinationPoint() == null) ? 1 : 0) ^ ((getDestinationPoint() == null) ? 1 : 0)) != 0)
      return false; 
    if (passEvent.getDestinationPoint() == null) {
      if (passEvent.getDelay() == getDelay() && passEvent.getPlayer().equals(getPlayer()) && passEvent.getSequenceNr() == getSequenceNr() && passEvent.isGoalPass() == isGoalPass()) {
        if ((((passEvent.getDestinationPlayer() == null) ? 1 : 0) ^ ((getDestinationPlayer() == null) ? 1 : 0)) != 0)
          return false; 
      } else {
        return false;
      } 
    } else if (passEvent.getDestinationX() == getDestinationX() && passEvent.getDestinationY() == getDestinationY() && passEvent.getDelay() == getDelay() && passEvent.getPlayer().equals(getPlayer()) && passEvent.getSequenceNr() == getSequenceNr() && passEvent.isGoalPass() == isGoalPass()) {
      if ((((passEvent.getDestinationPlayer() == null) ? 1 : 0) ^ ((getDestinationPlayer() == null) ? 1 : 0)) != 0)
        return false; 
    } else {
      return false;
    } 
    return !(passEvent.getDestinationPlayer() != null && getDestinationPlayer() != null && !passEvent.getDestinationPlayer().equals(getDestinationPlayer()));
  }
}

