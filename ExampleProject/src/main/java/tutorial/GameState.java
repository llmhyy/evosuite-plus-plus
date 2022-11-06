package tutorial;

public class GameState {
    private Player[] players;

    public GameState(Player[] players) {
        this.players = players;
    }

    public Player player(int id) {
        if (id > 100 || id < 0) {
            return null;
        }
        return players[id];
    }
}
