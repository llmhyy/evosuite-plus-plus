package regression.objectconstruction.testgeneration.example.graphcontruction.JNFE;

public class City extends Province
{

    private static final long serialVersionUID = 1L;
    private boolean capital;


    public City(String provinceCode, String provinceName, Province parent)
    {
        super(provinceCode, provinceName, parent);
    }

    public String getCityCode()
    {
        return super.getProvinceCode();
    }

    public void setCityCode(String cityCode)
    {
        setProvinceCode(cityCode);
    }

    public String getCityName()
    {
        return super.getProvinceName();
    }

    public void setCityName(String cityName)
    {
        setProvinceName(cityName);
    }

    public boolean isCapital()
    {
        return capital;
    }

    public void setCapital(boolean capital)
    {
        this.capital = capital;
    }

    public boolean equals(Object other)
    {
        if(!(other instanceof City))
            return false;
        else
            return super.equals(other);
    }
}
