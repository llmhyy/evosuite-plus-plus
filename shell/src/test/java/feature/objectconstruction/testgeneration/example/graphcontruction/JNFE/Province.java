package feature.objectconstruction.testgeneration.example.graphcontruction.JNFE;

public class Province implements Comparable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Operator operator;
	private Province parent;
	private String provinceCode;
	private String provinceName;
	private Country country;
	private char priority;

	public Province() {
		setProvinceCode("");
		setProvinceName("");
	}

	public Province(Operator operator, String provinceCode) {
		this();
		this.operator = operator;
		setProvinceCode(provinceCode);
	}

	public Province(Operator operator, String provinceCode, String provinceName) {
		this(operator, provinceCode);
		setProvinceName(provinceName);
	}

	public Province(String provinceCode, String provinceName, Province parent) {
		this(parent.getOperator(), provinceCode, provinceName);
		setParent(parent);
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

	public Province getParent() {
		return parent;
	}

	public void setParent(Province parent) {
		this.parent = parent;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public char getPriority() {
		return priority;
	}

	public void setPriority(char priority) {
		this.priority = priority;
	}

	public int compareTo(Province next) {
		if (getPriority() == next.getPriority()) {
			if (getProvinceCode() != null && next.getProvinceCode() != null)
				return getProvinceCode().compareTo(next.getProvinceCode());
			else
				return 0;
		} else {
			return getPriority() - next.getPriority();
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		buffer.append("code").append("='").append(getProvinceCode()).append("' ");
		buffer.append("]");
		return buffer.toString();
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof Province)) {
			return false;
		} else {
			Province castOther = (Province) other;
			return (getOperator() == castOther.getOperator() || getOperator() != null && castOther.getOperator() != null
					&& getOperator().equals(castOther.getOperator()))
					&& (getProvinceCode() == castOther.getProvinceCode()
							|| getProvinceCode() != null && castOther.getProvinceCode() != null
									&& getProvinceCode().equals(castOther.getProvinceCode()));
		}
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + (getOperator() != null ? getOperator().hashCode() : 0);
		result = 37 * result + (getProvinceCode() != null ? getProvinceCode().hashCode() : 0);
		return result;
	}

	public int compareTo(Object x0)
	{
	    return compareTo((Province)x0);
	}
}
