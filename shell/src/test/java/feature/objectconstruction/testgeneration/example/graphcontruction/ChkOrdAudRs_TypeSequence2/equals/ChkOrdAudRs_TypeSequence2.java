package feature.objectconstruction.testgeneration.example.graphcontruction.ChkOrdAudRs_TypeSequence2.equals;

import java.io.Serializable;
import java.util.ArrayList;

public class ChkOrdAudRs_TypeSequence2 extends FrameworkBean implements Serializable {
	private RecCtrlOut _recCtrlOut;
	private ChkOrdAudRs_TypeSequence2Sequence _chkOrdAudRs_TypeSequence2Sequence;
	private ArrayList _chkOrdMsgRecList = new ArrayList();
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof ChkOrdAudRs_TypeSequence2) {
			ChkOrdAudRs_TypeSequence2 temp = (ChkOrdAudRs_TypeSequence2) obj;
			if (this._recCtrlOut != null) {
				if (temp._recCtrlOut == null)
					return false;
				if (!this._recCtrlOut.equals(temp._recCtrlOut))
					return false;
			} else if (temp._recCtrlOut != null) {
				return false;
			}
			if (this._chkOrdAudRs_TypeSequence2Sequence != null) {
				if (temp._chkOrdAudRs_TypeSequence2Sequence == null)
					return false;
				if (!this._chkOrdAudRs_TypeSequence2Sequence.equals(temp._chkOrdAudRs_TypeSequence2Sequence))
					return false;
			} else if (temp._chkOrdAudRs_TypeSequence2Sequence != null) {
				return false;
			}
			if (this._chkOrdMsgRecList != null) {
				if (temp._chkOrdMsgRecList == null)
					return false;
				if (!this._chkOrdMsgRecList.equals(temp._chkOrdMsgRecList))
					return false;
			} else if (temp._chkOrdMsgRecList != null) {
				return false;
			}
			return true;
		}
		return false;
	}
}
