package regression.objectconstruction.testgeneration.example.graphcontruction.ChkOrdAudRs_TypeSequence2.equals;

import java.io.Serializable;

public class RecCtrlOut extends FrameworkBean implements Serializable {
	private long _matchedRec;
	private boolean _has_matchedRec;
	private long _sentRec;
	private boolean _has_sentRec;
	private Cursor _cursor;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof RecCtrlOut) {
			RecCtrlOut temp = (RecCtrlOut) obj;
			if (this._matchedRec != temp._matchedRec)
				return false;
			if (this._has_matchedRec != temp._has_matchedRec)
				return false;
			if (this._sentRec != temp._sentRec)
				return false;
			if (this._has_sentRec != temp._has_sentRec)
				return false;
			if (this._cursor != null) {
				if (temp._cursor == null)
					return false;
				if (!this._cursor.equals(temp._cursor))
					return false;
			} else if (temp._cursor != null) {
				return false;
			}
			return true;
		}
		return false;
	}
}
