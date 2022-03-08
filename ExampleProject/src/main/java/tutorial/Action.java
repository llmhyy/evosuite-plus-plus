package tutorial;

public class Action {
    private int actorId;

    public Action(int actorId) {
        this.actorId = actorId; 
    }
    public void setActor(int id) {
        actorId = id;
    }
    public int getActor() {
        return actorId;
    }
}
