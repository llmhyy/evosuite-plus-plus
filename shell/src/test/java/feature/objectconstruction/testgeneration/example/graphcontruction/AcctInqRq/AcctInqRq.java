package feature.objectconstruction.testgeneration.example.graphcontruction.AcctInqRq;

import java.io.Serializable;

public class AcctInqRq implements Serializable {

	private String _rqUID;
	private String _asyncRqUID;
	private CustId _custId;
	private AcctInqRqChoice _acctInqRqChoice;
	private String _incExtBal;
	private String _incBal;
	private String _deliveryMethod;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof AcctInqRq) {
			AcctInqRq temp = (AcctInqRq) obj;
			if (_rqUID != null) {
				if (temp._rqUID == null)
					return false;
				if (!_rqUID.equals(temp._rqUID))
					return false;
			} else if (temp._rqUID != null)
				return false;
			if (_asyncRqUID != null) {
				if (temp._asyncRqUID == null)
					return false;
				if (!_asyncRqUID.equals(temp._asyncRqUID))
					return false;
			} else if (temp._asyncRqUID != null)
				return false;
			if (_custId != null) {
				if (temp._custId == null)
					return false;
				if (!_custId.equals(temp._custId))
					return false;
			} else if (temp._custId != null)
				return false;
			if (_acctInqRqChoice != null) {
				if (temp._acctInqRqChoice == null)
					return false;
				if (!_acctInqRqChoice.equals(temp._acctInqRqChoice))
					return false;
			} else if (temp._acctInqRqChoice != null)
				return false;
			if (_incExtBal != null) {
				if (temp._incExtBal == null)
					return false;
				if (!_incExtBal.equals(temp._incExtBal))
					return false;
			} else if (temp._incExtBal != null)
				return false;
			if (_incBal != null) {
				if (temp._incBal == null)
					return false;
				if (!_incBal.equals(temp._incBal))
					return false;
			} else if (temp._incBal != null)
				return false;
			if (_deliveryMethod != null) {
				if (temp._deliveryMethod == null)
					return false;
				if (!_deliveryMethod.equals(temp._deliveryMethod))
					return false;
			} else if (temp._deliveryMethod != null)
				return false;
			return true;
		} else {
			return false;
		}
	}

	public AcctInqRqChoice getAcctInqRqChoice() {
		return _acctInqRqChoice;
	}

	public String getAsyncRqUID() {
		return _asyncRqUID;
	}

	public CustId getCustId() {
		return _custId;
	}

	public String getDeliveryMethod() {
		return _deliveryMethod;
	}

	public String getIncBal() {
		return _incBal;
	}

	public String getIncExtBal() {
		return _incExtBal;
	}

	public String getRqUID() {
		return _rqUID;
	}

	public void setAcctInqRqChoice(AcctInqRqChoice acctInqRqChoice) {
		_acctInqRqChoice = acctInqRqChoice;
	}

	public void setAsyncRqUID(String asyncRqUID) {
		_asyncRqUID = asyncRqUID;
	}

	public void setCustId(CustId custId) {
		_custId = custId;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		_deliveryMethod = deliveryMethod;
	}

	public void setIncBal(String incBal) {
		_incBal = incBal;
	}

	public void setIncExtBal(String incExtBal) {
		_incExtBal = incExtBal;
	}

	public void setRqUID(String rqUID) {
		_rqUID = rqUID;
	}

}
