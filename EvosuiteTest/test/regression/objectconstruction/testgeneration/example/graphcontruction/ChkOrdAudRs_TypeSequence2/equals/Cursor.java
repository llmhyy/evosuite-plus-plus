package regression.objectconstruction.testgeneration.example.graphcontruction.ChkOrdAudRs_TypeSequence2.equals;

public class Cursor {
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof Cursor) {
			Cursor temp = (Cursor) obj;
			return true;
		}
		return false;
	}
}
