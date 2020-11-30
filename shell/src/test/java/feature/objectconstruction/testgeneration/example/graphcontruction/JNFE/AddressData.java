package feature.objectconstruction.testgeneration.example.graphcontruction.JNFE;

public class AddressData extends AbstractPartialAddress {

	private Operator operator;

	public AddressData(Operator operator) {
		setOperator(operator);
	}

	public AddressData(Operator operator, Province province) {
		this(operator);
		setProvince(province);
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		buffer.append("UF/Municipio").append("='").append(getProvince()).append("' ");
		buffer.append("]");
		return buffer.toString();
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof AddressData)) {
			return false;
		} else {
			AddressData castOther = (AddressData) other;
			return (getOperator() == castOther.getOperator() 
					|| 
					getOperator() != null 
						&& castOther.getOperator() != null
							&& getOperator().equals(castOther.getOperator()))
								&& 
								(getProvince() == castOther.getProvince() 
									|| getProvince() != null
										&& castOther.getProvince() != null 
										&& getProvince().equals(castOther.getProvince()));
		}
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + (getProvince() != null ? getProvince().hashCode() : 0);
		return result;
	}
}
