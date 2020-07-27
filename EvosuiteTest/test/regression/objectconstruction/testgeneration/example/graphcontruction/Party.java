package regression.objectconstruction.testgeneration.example.graphcontruction;


import java.util.LinkedList;

public class Party {
	int id;
	public Party boss;
	public Party head;
	public Party next;
	public Party prev;

	public Party() {
		this(-1);
	}

	public Party(int id) {
		this.id = id;
		this.boss = null;
		this.head = null;
		this.next = null;
		this.prev = null;
	}

	public boolean isBoss() {
		return this.boss == null;
	}

	public Party gangBoss() {
		return this.isBoss() ? this : this.boss.gangBoss();
	}

	public void add(Party p) {
		try {
			p.boss = this;
			p.next = this.head;
			p.prev = null;
			if (this.head != null) {
				this.head.prev = p;
			}

			this.head = p;
		} catch (NullPointerException var3) {
			var3.printStackTrace(System.err);
		}

	}

	public void remove(Party p) {
		if (p != null) {
			if (p.prev != null) {
				p.prev.next = p.next;
			} else {
				this.head = p.next;
			}

			if (p.next != null) {
				p.next.prev = p.prev;
			}

			p.next = null;
			p.prev = null;
			p.boss = null;
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LinkedList<Party> getSubparty() {
		LinkedList<Party> plist = new LinkedList();
		plist.add(this);

		for (Party tmp = this.head; tmp != null; tmp = tmp.next) {
			plist.addAll(tmp.getSubparty());
		}

		return plist;
	}

	public int getId() {
		return this.id;
	}

	public String toString() {
		return "";
	}
}
