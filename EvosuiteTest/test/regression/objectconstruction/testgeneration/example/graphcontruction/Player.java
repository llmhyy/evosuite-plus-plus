package regression.objectconstruction.testgeneration.example.graphcontruction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class Player extends Party {
	public static final float TOLERANCE = 0.2F;
	public static final float MOVE_DISTANCE = 0.1F;
	public static final short MAGIC_NO = 4919;
	private int strength;
	private int pictureid;
	private boolean alive;
	private long deathtime;
	private float x;
	private float y;
	private float z;
	@SuppressWarnings("unused")
	private int packsize;
	@SuppressWarnings("unused")
	private byte[] data;
	@SuppressWarnings("unused")
	private boolean dirty;
	private String name;
	private boolean connected;
	private String ip;
	private float money;

	public Player() {
		this(-1);
	}

	public Player(int id) {
		super(id);
		this.x = 10.0F;
		this.y = 0.0F;
		this.packsize = 10;
		this.strength = 1;
		this.pictureid = 0;
		this.alive = true;
		this.dirty = true;
		this.name = "Player" + id;
		this.deathtime = 0L;
		this.connected = true;
		this.ip = "0.0.0.0";
	}

	public void reset(boolean newGame) {
		this.alive = true;
		this.dirty = true;
		this.deathtime = 0L;
		this.boss = null;
		this.head = null;
		this.next = null;
		this.prev = null;
		if (newGame) {
			this.money = 0.0F;
		}

	}

	public Player(int id, String name, int pictureid) {
		this(id);
		this.name = name;
		this.pictureid = pictureid;
		System.err.println(id + " " + name + " " + pictureid);
	}

	public Player(int id, String ip, String name, int pictureid) {
		this(id, name, pictureid);
		this.ip = ip;
	}

	public Player(int id, String ip, String name, int pid, int strength) {
		this(id, ip, name, pid);
		this.strength = strength;
	}

	public boolean isConnected() {
		return this.connected;
	}

	public void setConnected(boolean conned) {
		this.connected = conned;
	}

	@SuppressWarnings("rawtypes")
	public int gangStrength() {
		int str = 0;
		LinkedList<Party> gang = this.gangBoss().getSubparty();

		Party p;
		for (Iterator i$ = gang.iterator(); i$.hasNext(); str += ((Player) p).strength) {
			p = (Party) i$.next();
		}

		return str;
	}

	public void setDead(long deathtime, boolean alive) {
		this.deathtime = deathtime;
		this.alive = alive;
	}

	public void setDead() {
		this.setDead(System.currentTimeMillis());
	}

	public void setDead(long deathtime) {
		this.setDead(deathtime, false);
	}

	public boolean isDead() {
		return !this.alive;
	}

	public long getTimeOfDeath() {
		return this.deathtime;
	}

	public int getStrength() {
		return this.strength;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public float getMoney() {
		return this.money;
	}

	public String getIP() {
		return this.ip;
	}

	public String getName() {
		return this.name;
	}

	public int getPictureId() {
		return this.pictureid;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public void setY(float f) {
		this.y = f;
	}

	public void setX(float f) {
		this.x = f;
	}

	public void setZ(float f) {
		this.z = f;
	}

	public boolean isJoinOK(Player joiner, boolean invited) {

		return false;
	}

	public void setJoinOK(Player joiner, boolean invited) {
	}

	public void pack(DataOutputStream out) throws IOException {
		out.writeShort(4919);
		out.writeByte(this.id);
		out.writeShort(this.strength);
		out.writeShort(this.pictureid);
		out.writeFloat(this.x);
		out.writeFloat(this.y);
		out.writeFloat(this.z);
		out.writeUTF(this.name);
	}

	public boolean unpack(DataInputStream in) {
		Player tmp = null;

		try {
			if (in.readShort() != 4919) {
				System.err.println("Player.unpack(): *** WARNING *** data is not of proper type!");
				return false;
			}

			tmp = new Player();
			tmp.id = in.readByte();
			tmp.strength = in.readShort();
			tmp.pictureid = in.readShort();
			tmp.x = in.readFloat();
			tmp.y = in.readFloat();
			tmp.z = in.readFloat();
			tmp.name = in.readUTF();
		} catch (EOFException var4) {
			System.err.println("Player.unpack(): *** WARNING *** [EOF] data was incomplete: " + var4.getMessage());
			return false;
		} catch (IOException var5) {
			System.err.println("Player.unpack(): *** WARNING *** [IO] failed to unpack data: " + var5.getMessage());
			return false;
		}

		this.id = tmp.id;
		this.name = tmp.name;
		this.strength = tmp.strength;
		this.pictureid = tmp.pictureid;
		this.x = tmp.x;
		this.y = tmp.y;
		this.z = tmp.z;
		return true;
	}

	public byte[] pack() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(buf);
			this.pack(out);
			out.close();
			return buf.toByteArray();
		} catch (IOException var3) {
			System.err.println("Player.pack(): *** ERROR *** [IO] pack failed! current state is inconsistent: "
					+ var3.getMessage());
			return null;
		}
	}

	public void unpack(byte[] b) {
		this.unpack(new DataInputStream(new ByteArrayInputStream(b)));
	}

	public int type() {
		return 5;
	}

	public String toString() {
		return this.name;
	}
}