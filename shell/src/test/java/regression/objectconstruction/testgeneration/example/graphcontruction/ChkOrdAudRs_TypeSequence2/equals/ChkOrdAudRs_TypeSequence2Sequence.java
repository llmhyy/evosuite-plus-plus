package regression.objectconstruction.testgeneration.example.graphcontruction.ChkOrdAudRs_TypeSequence2.equals;

import java.io.Serializable;
import java.util.ArrayList;

public class ChkOrdAudRs_TypeSequence2Sequence extends FrameworkBean implements Serializable {
	private SelRangeDt _selRangeDt;

	private ArrayList _methodList = new ArrayList();

	private ArrayList _chkOrdIdList = new ArrayList();

	private ArrayList _recChkOrdIdList = new ArrayList();

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof ChkOrdAudRs_TypeSequence2Sequence) {
			ChkOrdAudRs_TypeSequence2Sequence temp = (ChkOrdAudRs_TypeSequence2Sequence) obj;
			if (this._selRangeDt != null) {
				if (temp._selRangeDt == null)
					return false;
				if (!this._selRangeDt.equals(temp._selRangeDt))
					return false;
			} else if (temp._selRangeDt != null) {
				return false;
			}
			if (this._methodList != null) {
				if (temp._methodList == null)
					return false;
				if (!this._methodList.equals(temp._methodList))
					return false;
			} else if (temp._methodList != null) {
				return false;
			}
			if (this._chkOrdIdList != null) {
				if (temp._chkOrdIdList == null)
					return false;
				if (!this._chkOrdIdList.equals(temp._chkOrdIdList))
					return false;
			} else if (temp._chkOrdIdList != null) {
				return false;
			}
			if (this._recChkOrdIdList != null) {
				if (temp._recChkOrdIdList == null)
					return false;
				if (!this._recChkOrdIdList.equals(temp._recChkOrdIdList))
					return false;
			} else if (temp._recChkOrdIdList != null) {
				return false;
			}
			return true;
		}
		return false;
	}
}
