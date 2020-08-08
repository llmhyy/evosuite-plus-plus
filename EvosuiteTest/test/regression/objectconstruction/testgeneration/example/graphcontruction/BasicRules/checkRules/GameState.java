package regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Hashtable;

public class GameState  {
	public static final int MAX_PLAYER_LIMIT = 128;
	@SuppressWarnings("unused")
	private static final short MAGIC_NO = 15290;
	private Player[] players;
	private int numOfPlayers;
	private int numDead;
	private long gamestart;
	private int gamestate;
	public static final int STATE_WAITING = 1;
	public static final int STATE_WARMUP = 2;
	public static final int STATE_PLAYING = 3;
	public static final int STATE_ENDED = 4;
	private Player me;
	private long mintimemove;
	private int totalRounds;
	private int currentRound;
	private Hashtable<Integer, Long> lastmove;

	public GameState() {
		this(15000L);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GameState(long mintimemove) {
		this.players = new Player[128];
		this.numOfPlayers = 0;
		this.numDead = 0;
		this.gamestart = 0L;
		this.gamestate = 1;
		this.currentRound = 1;
		this.totalRounds = 1;
		this.mintimemove = mintimemove;
		this.lastmove = new Hashtable();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void reset(boolean newGame) {
		this.numDead = 0;

		for (int i = 0; i < this.players.length; ++i) {
			if (this.players[i] != null) {
				if (!this.players[i].isConnected()) {
					if (newGame) {
						this.players[i] = null;
						--this.numOfPlayers;
					} else {
						this.players[i].reset(newGame);
						this.setDead(this.players[i]);
					}
				} else {
					this.players[i].reset(newGame);
				}
			}
		}

		if (newGame) {
			this.currentRound = 1;
		}

		this.lastmove = new Hashtable();
	}

	public void setTotalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
	}

	public int getTotalRounds() {
		return this.totalRounds;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	public int getCurrentRound() {
		return this.currentRound;
	}

	public int getGameState() {
		return this.gamestate;
	}

	public void setGameState(int gamestate) {
		this.gamestate = gamestate;
		System.err.println("Notifying observers...:" + this.gamestate);
	}

	public boolean isMoveTimeOK(Player player) {
		if (this.lastmove.containsKey(player.getId())) {
			long time = (Long) this.lastmove.get(new Integer(player.getId()));
			if (time + this.mintimemove > System.currentTimeMillis()) {
				return false;
			}
		}

		return true;
	}

	public void updateLastMove(Player player) {
		this.lastmove.put(player.getId(), System.currentTimeMillis());
	}

	public void addPlayer(Player p) {
		try {
			if (this.players[p.getId()] != null) {
				System.err.println("GameState.addPlayer(): PlayerID exists!");
				return;
			}

			this.players[p.getId()] = p;
			++this.numOfPlayers;
			this.updateLastMove(p);
		} catch (IndexOutOfBoundsException var3) {
			System.err
					.println("GameState.addPlayer(): PlayerID '" + p.id + "' out of the allowed " + "range 0 - " + 128);
		} catch (NullPointerException var4) {
			var4.printStackTrace(System.err);
		}

	}

	public void removePlayer(Player p) {
		try {
			if (this.players[p.getId()] == null) {
				System.err.println("GameState.removePlayer(): PlayerID doesn't exist!");
			}

			if (!p.isBoss()) {
				this.part(p.boss, p);
			}

			while (p.head != null) {
				this.part(p, p.head);
			}

			if (this.gamestate == 1) {
				this.players[p.getId()] = null;
				--this.numOfPlayers;
			} else {
				this.setDead(p);
				p.setConnected(false);
			}

		} catch (IndexOutOfBoundsException var3) {
			System.err.println(
					"GameState.removePlayer(): PlayerID '" + p.getId() + "' out of the allowed " + "range 0 - " + 128);
		} catch (NullPointerException var4) {
			var4.printStackTrace(System.err);
		}

	}

	public void join(Party parent, Party child) {
		try {
			parent.add(child);
		} catch (NullPointerException var4) {
			System.err.println("GameState.join(): parent=" + parent + " " + "child=" + child);
			var4.printStackTrace(System.err);
		}

	}

	public void part(Party parent, Party child) {
		try {
			parent.remove(child);
		} catch (NullPointerException var4) {
			var4.printStackTrace(System.err);
		}

	}

	public Player player(int id) {
		return id >= 0 && id < 128 ? this.players[id] : null;
	}

	public Player[] players() {
		return this.players;
	}

	public void setMe(Player p) {
		this.me = p;
	}

	public Player getMe() {
		return this.me;
	}

	public int getNumOfPlayers() {
		return this.numOfPlayers;
	}

	public void reload() {
	}

	public void setDead(Player p, long deathtime) {
		if (p != null && !p.isDead()) {
			++this.numDead;
			p.setDead(deathtime);
		}

	}

	public void setDead(Player p) {
		this.setDead(p, System.currentTimeMillis());
	}

	public void move(Player p, int direction) {
	}

	public int getNumDead() {
		return this.numDead;
	}

	public long getGamestart() {
		return this.gamestart;
	}

	public void setGamestart(long l) {
		this.gamestart = l;
	}

	public byte[] pack() {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		Player p = null;

		try {
			int numplayers = this.numOfPlayers;

			int i;
			for (i = 0; i < this.players.length; ++i) {
				if (this.players[i] != null && !this.players[i].isConnected()) {
					--numplayers;
				}
			}

			out.writeShort(15290);
			out.writeByte(this.gamestate);
			out.writeByte(numplayers);
			i = 0;

			for (int k = -1; i < this.numOfPlayers; ++i) {
				do {
					++k;
				} while (this.players[k] == null);

				p = this.players[k];
				if (p.isConnected()) {
					p.pack(out);
					out.writeByte(this.pidOf(p.boss));
					out.writeByte(this.pidOf(p.next));
					out.writeByte(this.pidOf(p.prev));
					out.writeByte(this.pidOf(p.head));
				}
			}
		} catch (IOException var7) {
			System.err.println("GameState.pack(): *** WARNING *** [IO] failed to pack data: " + var7.getMessage());
			return null;
		} catch (ArrayIndexOutOfBoundsException var8) {
			System.err.println("GameState.pack(): *** ERROR *** [IDX] failed to pack data: " + var8.getMessage());
			return null;
		}

		return buf.toByteArray();
	}

	public void unpack(byte[] b) {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
			if (in.readShort() != 15290) {
				System.err.println("GameState.unpack(): *** WARNING *** data is not of proper type!");
				return;
			}

			this.gamestate = in.readByte();
			int n = in.readByte();
			Player p = null;

			for (int i = 0; i < n; ++i) {
				in.mark(3);
				in.readShort();
				p = this.playerAt(in.readByte());
				in.reset();
				if (p.unpack(in)) {
					p.boss = this.playerAt(in.readByte());
					p.next = this.playerAt(in.readByte());
					p.prev = this.playerAt(in.readByte());
					p.head = this.playerAt(in.readByte());
				} else {
					System.err.println(
							"GameState.unpack(): *** ERROR *** data is incomplete! current state is inconsistent!");
				}
			}
		} catch (EOFException var6) {
			System.err.println(
					"GameState.unpack(): *** ERROR *** [EOF] data is incomplete! current state is inconsistent: "
							+ var6.getMessage());
		} catch (IOException var7) {
			System.err.println(
					"GameState.unpack(): *** ERROR *** [IO] unpack data failed! current state is inconsistent: "
							+ var7.getMessage());
		}

	}

	public int type() {
		return 4;
	}

	private Player playerAt(int id) {
		Player p = null;

		try {
			p = this.players[id];
			if (p == null) {
				p = new Player(id);
				this.addPlayer(p);
			}
		} catch (ArrayIndexOutOfBoundsException var4) {
			;
		}

		return p;
	}

	private int pidOf(Party p) {
		return p == null ? -1 : p.getId();
	}

	public String toString() {
		StringBuffer str = new StringBuffer("GameState[");
		boolean first = true;
		int i = 0;

		for (int t = -1; i < this.numOfPlayers; ++i) {
			do {
				++t;
			} while (t < this.players.length && this.players[t] == null);

			if (t >= this.players.length) {
				return "ERR," + this.numOfPlayers + "]";
			}

			if (first) {
				str.append(this.players[t]);
				first = false;
			} else {
				str.append(" , " + this.players[t]);
			}
		}

		return str + "]";
	}
}