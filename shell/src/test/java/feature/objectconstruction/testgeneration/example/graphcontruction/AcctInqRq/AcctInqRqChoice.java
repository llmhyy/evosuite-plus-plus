package feature.objectconstruction.testgeneration.example.graphcontruction.AcctInqRq;

import java.io.Serializable;

public class AcctInqRqChoice implements Serializable {

	private DepAcctId _depAcctId;
	private CardAcctId _cardAcctId;
	private LoanAcctId _loanAcctId;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof AcctInqRqChoice) {
			AcctInqRqChoice temp = (AcctInqRqChoice) obj;
			if (_depAcctId != null) {
				if (temp._depAcctId == null)
					return false;
				if (!_depAcctId.equals(temp._depAcctId))
					return false;
			} else if (temp._depAcctId != null)
				return false;
			if (_cardAcctId != null) {
				if (temp._cardAcctId == null)
					return false;
				if (!_cardAcctId.equals(temp._cardAcctId))
					return false;
			} else if (temp._cardAcctId != null)
				return false;
			if (_loanAcctId != null) {
				if (temp._loanAcctId == null)
					return false;
				if (!_loanAcctId.equals(temp._loanAcctId))
					return false;
			} else if (temp._loanAcctId != null)
				return false;
			return true;
		} else {
			return false;
		}
	}

	public CardAcctId getCardAcctId() {
		return _cardAcctId;
	}

	public DepAcctId getDepAcctId() {
		return _depAcctId;
	}

	public LoanAcctId getLoanAcctId() {
		return _loanAcctId;
	}

	public void setCardAcctId(CardAcctId cardAcctId) {
		_cardAcctId = cardAcctId;
	}

	public void setDepAcctId(DepAcctId depAcctId) {
		_depAcctId = depAcctId;
	}

	public void setLoanAcctId(LoanAcctId loanAcctId) {
		_loanAcctId = loanAcctId;
	}

}
