package regression.objectconstruction.testgeneration.example.graphcontruction.AcctInqRq;

import java.io.Serializable;

public class LoanAcctId_Type implements Serializable {

	private String _acctId;
	private String _acctType;
	private BankInfo _bankInfo;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof LoanAcctId_Type) {
			LoanAcctId_Type temp = (LoanAcctId_Type) obj;
			if (_acctId != null) {
				if (temp._acctId == null)
					return false;
				if (!_acctId.equals(temp._acctId))
					return false;
			} else if (temp._acctId != null)
				return false;
			if (_acctType != null) {
				if (temp._acctType == null)
					return false;
				if (!_acctType.equals(temp._acctType))
					return false;
			} else if (temp._acctType != null)
				return false;
			if (_bankInfo != null) {
				if (temp._bankInfo == null)
					return false;
				if (!_bankInfo.equals(temp._bankInfo))
					return false;
			} else if (temp._bankInfo != null)
				return false;
			return true;
		} else {
			return false;
		}
	}

	public String getAcctId() {
		return _acctId;
	}

	public String getAcctType() {
		return _acctType;
	}

	public BankInfo getBankInfo() {
		return _bankInfo;
	}

	public void setAcctId(String acctId) {
		_acctId = acctId;
	}

	public void setAcctType(String acctType) {
		_acctType = acctType;
	}

	public void setBankInfo(BankInfo bankInfo) {
		_bankInfo = bankInfo;
	}

}
