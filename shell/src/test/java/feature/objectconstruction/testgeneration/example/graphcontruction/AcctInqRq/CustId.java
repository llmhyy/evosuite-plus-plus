package feature.objectconstruction.testgeneration.example.graphcontruction.AcctInqRq;

import java.io.Serializable;

public class CustId implements Serializable {

	private String _SPName;
	private String _custPermId;
	private String _custLoginId;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof CustId) {
			CustId temp = (CustId) obj;
			if (_SPName != null) {
				if (temp._SPName == null)
					return false;
				if (!_SPName.equals(temp._SPName))
					return false;
			} else if (temp._SPName != null)
				return false;
			if (_custPermId != null) {
				if (temp._custPermId == null)
					return false;
				if (!_custPermId.equals(temp._custPermId))
					return false;
			} else if (temp._custPermId != null)
				return false;
			if (_custLoginId != null) {
				if (temp._custLoginId == null)
					return false;
				if (!_custLoginId.equals(temp._custLoginId))
					return false;
			} else if (temp._custLoginId != null)
				return false;
			return true;
		} else {
			return false;
		}
	}

	public String getCustLoginId() {
		return _custLoginId;
	}

	public String getCustPermId() {
		return _custPermId;
	}

	public String getSPName() {
		return _SPName;
	}


	public void setCustLoginId(String custLoginId) {
		_custLoginId = custLoginId;
	}

	public void setCustPermId(String custPermId) {
		_custPermId = custPermId;
	}

	public void setSPName(String SPName) {
		_SPName = SPName;
	}

}