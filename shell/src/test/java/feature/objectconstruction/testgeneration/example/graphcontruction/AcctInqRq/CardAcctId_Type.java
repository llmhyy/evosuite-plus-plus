package feature.objectconstruction.testgeneration.example.graphcontruction.AcctInqRq;

import java.io.Serializable;

public class CardAcctId_Type implements Serializable {

	private CardAcctId_TypeChoice _cardAcctId_TypeChoice;
	private String _acctType;
	private CCMotoAcct _CCMotoAcct;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof CardAcctId_Type) {
			CardAcctId_Type temp = (CardAcctId_Type) obj;
			if (_cardAcctId_TypeChoice != null) {
				if (temp._cardAcctId_TypeChoice == null)
					return false;
				if (!_cardAcctId_TypeChoice.equals(temp._cardAcctId_TypeChoice))
					return false;
			} else if (temp._cardAcctId_TypeChoice != null)
				return false;
			if (_acctType != null) {
				if (temp._acctType == null)
					return false;
				if (!_acctType.equals(temp._acctType))
					return false;
			} else if (temp._acctType != null)
				return false;
			if (_CCMotoAcct != null) {
				if (temp._CCMotoAcct == null)
					return false;
				if (!_CCMotoAcct.equals(temp._CCMotoAcct))
					return false;
			} else if (temp._CCMotoAcct != null)
				return false;
			return true;
		} else {
			return false;
		}
	}

	public String getAcctType() {
		return _acctType;
	}

	public CCMotoAcct getCCMotoAcct() {
		return _CCMotoAcct;
	}

	public CardAcctId_TypeChoice getCardAcctId_TypeChoice() {
		return _cardAcctId_TypeChoice;
	}



	public void setAcctType(String acctType) {
		_acctType = acctType;
	}

	public void setCCMotoAcct(CCMotoAcct CCMotoAcct) {
		_CCMotoAcct = CCMotoAcct;
	}

	public void setCardAcctId_TypeChoice(CardAcctId_TypeChoice cardAcctId_TypeChoice) {
		_cardAcctId_TypeChoice = cardAcctId_TypeChoice;
	}

}
