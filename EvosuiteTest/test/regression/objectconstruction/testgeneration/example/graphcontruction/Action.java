package regression.objectconstruction.testgeneration.example.graphcontruction;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Action  {
	public static final int ACTION_JOIN = 0;
	public static final int ACTION_PART = 1;
	public static final int ACTION_ATTACK = 2;
	public static final int ACTION_JOIN_APPLY = 3;
	public static final int ACTION_JOIN_INVITE = 4;
	public static final int ACTION_JOIN_ALLOW = 5;
	public static final int ACTION_JOIN_AGREE = 6;
	public static final int ACTION_MOVE = 7;
	public static final int ACTION_KICK = 8;
	@SuppressWarnings("unused")
	private static final int PACK_TYPE = 2;
	private int actor;
	private int target;
	private int action;
	private byte destX;
	private byte destY;
	private byte destZ;
	@SuppressWarnings("unused")
	private DataOutputStream outputStream;
	@SuppressWarnings("unused")
	private DataInputStream inputStream;

	public Action() {
		this(-1, -1, -1);
	}

	public Action(int action, int actor, int target) {
		this.actor = actor;
		this.action = action;
		this.target = target;
	}

	public Action(int action, int actor, byte x, byte y, byte z) {
		this(action, actor, -1);
		this.destX = x;
		this.destY = y;
		this.destZ = z;
	}

	public void set(int action, int actor, int target, byte x, byte y, byte z) {
		this.actor = actor;
		this.action = action;
		this.target = target;
		this.destX = x;
		this.destY = y;
		this.destZ = z;
	}

	public void setActor(int actor) {
		this.actor = actor;
	}

	public int getAction() {
		return this.action;
	}

	public int getActor() {
		return this.actor;
	}

	public int getTarget() {
		return this.target;
	}

	public int type() {
		return 2;
	}

	public void pack(DataOutputStream out)  {
		try {
			out.writeByte(this.actor);
			out.writeByte(this.target);
			out.writeByte(this.action);
			if (this.action == 7) {
				out.writeByte(this.destX);
				out.writeByte(this.destY);
				out.writeByte(this.destZ);
			}

		} catch (Exception var3) {
			
		}
	}

	public void unpack(DataInputStream in)  {
		try {
			this.actor = in.readByte();
			this.target = in.readByte();
			this.action = in.readByte();
			if (this.action == 7) {
				this.destX = in.readByte();
				this.destY = in.readByte();
				this.destZ = in.readByte();
			}

		} catch (Exception var3) {
			
		}
	}


	public Action clone() {
		Action a = new Action();
		return a;
	}

	public String toString() {
		return String.format("Action[type=%d,action=%s,actor=%s,target=%s,destX=%d,destY=%d,destZ=%d]", 2, this.action,
				this.actor, this.target, this.destX, this.destY, this.destZ);
	}
}
