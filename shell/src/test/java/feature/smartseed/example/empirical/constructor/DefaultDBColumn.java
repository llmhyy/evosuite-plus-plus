package feature.smartseed.example.empirical.constructor;



import java.util.ArrayList;
import java.util.List;


/**
* Default implementation of the {@link DBColumn} interface.<br/><br/>
* Created: 14.11.2010 19:33:30
* @since 0.6.4
* @author Volker Bergmann
*/
public class DefaultDBColumn implements DBColumn {

	private static final long serialVersionUID = 5693941485232520002L;
	
   protected DBDataType type;
   protected Integer size;
   protected Integer fractionDigits;
   protected String defaultValue;
   protected boolean versionColumn;

//   protected List<DBUniqueConstraint> ukConstraints; // constraints may be unnamed, so a Map does not make sense
//   protected DBNotNullConstraint notNullConstraint;

   // constructors ----------------------------------------------------------------------------------------------------

   public DefaultDBColumn(String name, DBTable table, DBDataType type) {
       this(name, table, type, null);
   }

   public DefaultDBColumn(String name, DBTable table, int jdbcType, String typeAndSize) {
       this(name, table, null, null);
       Object[] tokens = SQLUtil.parseColumnTypeAndSize(typeAndSize);
       if (tokens.length > 0)
       	this.type = DBDataType.getInstance(jdbcType, (String) tokens[0]);
       if (tokens.length > 1)
       	this.size = (Integer) tokens[1];
       if (tokens.length > 2)
       	this.fractionDigits = (Integer) tokens[2];
   }

   public DefaultDBColumn(String name, DBTable table, DBDataType type, Integer size) {
       this(name, table, type, size, null);
   }
   
   public DefaultDBColumn(String name, DBTable table, DBDataType type, Integer size, Integer fractionDigits) {
//   	super(name, "column");
   	if (table != null)
   		table.addColumn(this);
//       this.name = name;
       this.type = type;
       this.size = size;
       this.fractionDigits = fractionDigits;
//       this.doc = null;
       this.defaultValue = null;
//       this.ukConstraints = new ArrayList<DBUniqueConstraint>();
//       this.notNullConstraint = null;
       this.versionColumn = false;
   }

   // properties ------------------------------------------------------------------------------------------------------

   public DBDataType getType() {
       return type;
   }

   public void setType(DBDataType type) {
       this.type = type;
   }

   public Integer getSize() {
       return size;
   }

   public void setSize(Integer size) {
       this.size = size;
   }

   public Integer getFractionDigits() {
       return fractionDigits;
   }

   public void setFractionDigits(Integer fractionDigits) {
       this.fractionDigits = fractionDigits;
   }

   public String getDefaultValue() {
       return defaultValue;
   }

   public void setDefaultValue(String defaultValue) {
       this.defaultValue = defaultValue;
   }

//   public boolean isUnique() {
//   	for (DBUniqueConstraint constraint : ukConstraints)
//   		if (constraint.getColumnNames().length == 1)
//   			return true;
//   	return false;
//   }
//   
//	public void setUnique(boolean unique) {
//		DBUniqueConstraint constraint = new DBUniqueConstraint(getTable(), name + "_UK", name);
//		addUkConstraint(constraint);
//	}
//
//   public List<DBUniqueConstraint> getUkConstraints() {
//       return ukConstraints;
//   }
//
//   public void addUkConstraint(DBUniqueConstraint constraint) {
//       this.ukConstraints.add(constraint);
//   }
//
//   public DBNotNullConstraint getNotNullConstraint() {
//       return notNullConstraint;
//   }
//
//   public void setNotNullConstraint(DBNotNullConstraint notNullConstraint) {
//       this.notNullConstraint = notNullConstraint;
//   }
//
//   public boolean isNullable() {
//       return (notNullConstraint == null);
//   }
//
//   public void setNullable(boolean nullable) {
//       if (nullable) {
//           // if a NotNullConstraint exists then remove it
//           notNullConstraint = null;
//       } else {
//           // if there needs to be a NotNullConstraint, check if there exists one, first
//           if (this.isNullable())
//               this.notNullConstraint = new DBNotNullConstraint(getTable(), null, name);
//       }
//   }

   public boolean isVersionColumn() {
       return versionColumn;
   }

   public void setVersionColumn(boolean versionColumn) {
       this.versionColumn = versionColumn;
   }

//	public DBForeignKeyConstraint getForeignKeyConstraint() {
//		for (DBForeignKeyConstraint fk : getTable().getForeignKeyConstraints())
//			if (ArrayUtil.contains(name, fk.getColumnNames()))
//				return fk;
//	    return null;
//	}
	
   // java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !DBColumn.class.isAssignableFrom(obj.getClass()))
			return false;
		DBColumn that = (DBColumn) obj;
		return 
//				NullSafeComparator.equals(this.name, that.getName())
			 this.type.equals(that.getType())
			&& NullSafeComparator.equals(this.size, that.getSize())
			&& NullSafeComparator.equals(this.fractionDigits, that.getFractionDigits())
			&& NullSafeComparator.equals(this.defaultValue, that.getDefaultValue())
			&& this.versionColumn == that.isVersionColumn();
//			&& this.ukConstraints.equals(that.getUkConstraints())
//			&& NullSafeComparator.equals(notNullConstraint, that.getNotNullConstraint());
	}
	
//	@Override
//	public int hashCode() {
//		return HashCodeBuilder.hashCode(name, type, size, fractionDigits, defaultValue, versionColumn, 
//				ukConstraints, notNullConstraint/*, fkConstraint*/);
//	}
	
//   @Override
//   public String toString() {
//       StringBuilder builder = new StringBuilder(name).append(" : ");
//       SQLUtil.renderColumnTypeWithSize(this, builder);
//       if (!isNullable())
//           builder.append(" NOT NULL");
//       return builder.toString();
//   }

	public boolean isIdentical(DBObject other) {
		if (this == other)
			return true;
		if (other == null || !(other instanceof DBColumn))
			return false;
		return isEquivalent(other);
	}

	public boolean isEquivalent(DBObject other) {
		if (this == other)
			return true;
		if (other == null || !(other instanceof DBColumn))
			return false;
		DBColumn that = (DBColumn) other;
		return this.type.equals(that.getType()) 
			&& NullSafeComparator.equals(this.size, that.getSize())
			&& NullSafeComparator.equals(this.fractionDigits, that.getFractionDigits());
	}

	@Override
	public boolean isUnique() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setUnique(boolean unique) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isNullable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNullable(boolean nullable) {
		// TODO Auto-generated method stub
		
	}

}

