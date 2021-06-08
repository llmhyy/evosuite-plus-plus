package feature.smartseed.example.empirical.constructor;

public interface DBColumn {
	
    DBDataType getType();
    void setType(DBDataType type);
    
    Integer getSize();
    void setSize(Integer size);
    
    Integer getFractionDigits();
    void setFractionDigits(Integer fractionDigits);
    
    String getDefaultValue();
    void setDefaultValue(String defaultValue);
    
    boolean isUnique();
    void setUnique(boolean unique);
    
//    List<DBUniqueConstraint> getUkConstraints();
//    void addUkConstraint(DBUniqueConstraint constraint);
//    
//    DBNotNullConstraint getNotNullConstraint();
//    void setNotNullConstraint(DBNotNullConstraint constraint);
    
    boolean isNullable();
    void setNullable(boolean nullable);
    
    boolean isVersionColumn();
    void setVersionColumn(boolean versionColumn);
	
//	DBForeignKeyConstraint getForeignKeyConstraint();
}