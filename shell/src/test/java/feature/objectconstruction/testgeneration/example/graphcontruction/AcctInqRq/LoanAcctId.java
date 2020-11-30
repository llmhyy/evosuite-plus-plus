package feature.objectconstruction.testgeneration.example.graphcontruction.AcctInqRq;

import java.io.Serializable;

public class LoanAcctId extends LoanAcctId_Type implements Serializable {

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj instanceof LoanAcctId) {
			LoanAcctId temp = (LoanAcctId) obj;
			return true;
		} else {
			return false;
		}
	}
}
