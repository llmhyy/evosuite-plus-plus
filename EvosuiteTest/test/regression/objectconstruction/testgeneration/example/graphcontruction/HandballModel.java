package regression.objectconstruction.testgeneration.example.graphcontruction;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JOptionPane;

import regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.GameState;

public class HandballModel implements Serializable {
	private static final long serialVersionUID = -5372891552466311536L;

	private static final int MAX_OFFENDERS = 6;

	private static final int MAX_DEFENDERS = 6;

	private static final int MARK_RADIUS = 12;

	private static final String APP_NAME = "jHandballMoves";

	private List<Defender> defenders;

	private List<Offender> offenders;

	private SortedSet<MoveEvent> events;

	private PlayerOne markedPlayer;

	private Offender ballOwner;

	private Offender firstBallOwner;

	private int animationSequenz;

	private transient HandballModel lastSavedModel;

	private String comment;

	private String moveName;

	public List<Defender> getDefenders() {
		return this.defenders;
	}

	public List<Offender> getOffenders() {
		return this.offenders;
	}

	public List<MoveEvent> getEvents() {
		return new ArrayList<MoveEvent>(this.events);
	}

	public boolean isSaved() {
		int i = 0;
		if (this.lastSavedModel != null) {
			if (this.lastSavedModel.getMoveName() != null) {
				if (getMoveName() == null || !this.lastSavedModel.getMoveName().equals(getMoveName()))
					return false;
			} else if (getMoveName() != null) {
				return false;
			}
			if (changedPlayerList(getOffenders().<PlayerOne>toArray(new PlayerOne[getOffenders().size()]),
					this.lastSavedModel.getOffenders()
							.<PlayerOne>toArray(new PlayerOne[this.lastSavedModel.getOffenders().size()]))
					|| changedPlayerList(getDefenders().<PlayerOne>toArray(new PlayerOne[getDefenders().size()]),
							this.lastSavedModel.getDefenders()
									.<PlayerOne>toArray(new PlayerOne[this.lastSavedModel.getDefenders().size()]))) {
				i = 1;
			} else {
				List<MoveEvent> list1 = getEvents();
				List<MoveEvent> list2 = this.lastSavedModel.getEvents();
				if (list1.size() == list2.size()) {
					for (byte b = 0; b < list1.size(); b++) {
						if (!((MoveEvent) list1.get(b)).equals(list2.get(b))) {
							i = 1;
							break;
						}
					}
				} else {
					i = 1;
				}
			}
			if (i == 0 && (getFirstBallOwner() != null || this.lastSavedModel.getFirstBallOwner() != null)) {
				i = ((getFirstBallOwner() == null) ? 1 : 0)
						^ ((this.lastSavedModel.getFirstBallOwner() == null) ? 1 : 0);
				if (i == 0)
					i = !getFirstBallOwner().equals(this.lastSavedModel.getFirstBallOwner()) ? 1 : 0;
			}
			if (i == 0)
				if (this.comment == null || this.comment.equals("")) {
					if (this.lastSavedModel.getComment() != null && !this.lastSavedModel.getComment().equals(""))
						i = 1;
				} else if (this.lastSavedModel.getComment() != null
						&& !this.lastSavedModel.getComment().equals(this.comment)) {
					i = 1;
				}
		}
		return (i == 0);
	}

	private boolean changedPlayerList(PlayerOne[] paramArrayOfPlayer1, PlayerOne[] paramArrayOfPlayer2) {
		boolean bool = false;
		int i = paramArrayOfPlayer1.length;
		int j = paramArrayOfPlayer2.length;
		if (i == j) {
			for (byte b = 0; b < i; b++) {
				if (!paramArrayOfPlayer1[b].equals(paramArrayOfPlayer2[b])) {
					bool = true;
					break;
				}
			}
		} else {
			bool = true;
		}
		return bool;
	}

	public Offender getFirstBallOwner() {
		return this.firstBallOwner;
	}

	public String getMoveName() {
		return this.moveName;
	}

	public void setMoveName(String paramString) {
		this.moveName = paramString;
		if (Main.getWindow() != null) {
			StringBuffer stringBuffer = new StringBuffer("jHandballMoves");
			stringBuffer.append(" - ");
			if (paramString == null || paramString.equals("")) {
				stringBuffer.append("Unbenannt");
			} else {
				stringBuffer.append(paramString);
			}
			if (!isSaved())
				stringBuffer.append("*");
			Main.getWindow().setTitle(stringBuffer.toString());
		}
	}

//  public void test(String a, int x, String b, GameState state) {
//	  if (a.equals("Hello") || x > 0) {
//		  System.out.println("1");
//	  } else if (b.equals("testing") || a.equals("testt") && x < 0 && state.getGameState() == 1) {
//		  System.out.println("2");
//	  } else if (state.getGameState() == 1) {
//		  System.out.println("3");
//	  }
//  }

	public String getComment() {
		if (this.comment == null)
			this.comment = "";
		return this.comment;
	}
}
