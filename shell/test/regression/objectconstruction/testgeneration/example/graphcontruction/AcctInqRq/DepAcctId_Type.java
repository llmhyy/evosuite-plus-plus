package regression.objectconstruction.testgeneration.example.graphcontruction.AcctInqRq;

import java.io.Serializable;

public class DepAcctId_Type implements Serializable {

	private String _acctId;
	private String _acctType;
	private String _acctKey;
	private String _acctCur;
	private BankInfo _bankInfo;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof DepAcctId_Type) {
			DepAcctId_Type temp = (DepAcctId_Type) obj;
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
			if (_acctKey != null) {
				if (temp._acctKey == null)
					return false;
				if (!_acctKey.equals(temp._acctKey))
					return false;
			} else if (temp._acctKey != null)
				return false;
			if (_acctCur != null) {
				if (temp._acctCur == null)
					return false;
				if (!_acctCur.equals(temp._acctCur))
					return false;
			} else if (temp._acctCur != null)
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

	public String getAcctCur() {
		return _acctCur;
	}

	public String getAcctId() {
		return _acctId;
	}

	public String getAcctKey() {
		return _acctKey;
	}

	public String getAcctType() {
		return _acctType;
	}

	public BankInfo getBankInfo() {
		return _bankInfo;
	}

	public void setAcctCur(String acctCur) {
		_acctCur = acctCur;
	}

	public void setAcctId(String acctId) {
		_acctId = acctId;
	}

	public void setAcctKey(String acctKey) {
		_acctKey = acctKey;
	}

	public void setAcctType(String acctType) {
		_acctType = acctType;
	}

	public void setBankInfo(BankInfo bankInfo) {
		_bankInfo = bankInfo;
	}

}
