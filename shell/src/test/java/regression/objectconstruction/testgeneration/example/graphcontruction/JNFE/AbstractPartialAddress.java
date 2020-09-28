package regression.objectconstruction.testgeneration.example.graphcontruction.JNFE;

//Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

import java.io.Serializable;

public abstract class AbstractPartialAddress implements Serializable {

	private int id;
	private String address1;
	private String addressClassifier;
	private String address2;
	private String postalCode;
	private Province province;
	private String cityName;

	public AbstractPartialAddress() {
		setAddress1("");
		setAddress2("");
		setPostalCode("");
		setCityName("");
	}

	public AbstractPartialAddress(Province province) {
		this();
		setProvince(province);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddressClassifier() {
		return addressClassifier;
	}

	public void setAddressClassifier(String addressClassifier) {
		this.addressClassifier = addressClassifier;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public City getCity() {
		if (getProvince() instanceof City)
			return (City) getProvince();
		else
			return null;
	}

	public Province getParentProvince() {
		if (getCity() != null)
			return getCity().getParent();
		else
			return null;
	}

	public String getProvinceCode() {
		if (getParentProvince() != null)
			return getParentProvince().getProvinceCode();
		if (getProvince() != null)
			return getProvince().getProvinceCode();
		else
			return "";
	}

	public String getProvinceName() {
		if (getParentProvince() != null)
			return getParentProvince().getProvinceName();
		if (getProvince() != null)
			return getProvince().getProvinceName();
		else
			return "";
	}

	public String getCityCode() {
		if (getCity() != null)
			return getCity().getProvinceCode();
		else
			return "";
	}

	public String getCityName() {
		if (getCity() != null)
			return getCity().getProvinceName();
		else
			return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
}