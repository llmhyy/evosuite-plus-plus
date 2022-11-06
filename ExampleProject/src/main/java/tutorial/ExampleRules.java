package tutorial;

public class ExampleRules {
    public boolean checkRules(Action action, GameState state) {
        Player actor = state.player(action.getActor());

        if (actor == null) {
            return false;
        }
        
        if (actor.getAction() == 1) {
            return true;
        }
        return false;
    }
}
