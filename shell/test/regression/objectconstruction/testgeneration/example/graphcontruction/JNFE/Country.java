package regression.objectconstruction.testgeneration.example.graphcontruction.JNFE;

public class Country {

	private static final long serialVersionUID = 1L;
	private int id;
	private Operator operator;
	private String countryCode;
	private String countryName;

	public static Country countryFactory(Operator requiredOperator) {
		Country country = new Country();
		country.setOperator(requiredOperator);
		return country;
	}

	public Country() {
		this("");
	}

	public Country(String countryCode) {
		setCountryCode(countryCode);
		setCountryName("");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		buffer.append("code").append("='").append(getCountryCode()).append("' ");
		buffer.append("]");
		return buffer.toString();
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof Country)) {
			return false;
		} else {
			Country castOther = (Country) other;
			return (getOperator() == castOther.getOperator() || getOperator() != null && castOther.getOperator() != null
					&& getOperator().equals(castOther.getOperator()))
					&& (getCountryCode() == castOther.getCountryCode()
							|| getCountryCode() != null && castOther.getCountryCode() != null
									&& getCountryCode().equals(castOther.getCountryCode()));
		}
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + (getOperator() != null ? getOperator().hashCode() : 0);
		result = 37 * result + (getCountryCode() != null ? getCountryCode().hashCode() : 0);
		return result;
	}
}