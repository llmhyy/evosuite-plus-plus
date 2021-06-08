package feature.smartseed.example.empirical.constructor;

import java.io.Serializable;

public interface DBObject extends Named, Serializable {
	
	/** @return the type of the DBObject as used in DDL in lower case letters. */
    public String getObjectType();
    
    /** @return documentation of the DBObject if available, otherwise null. */
    public String getDoc();
    
    /** @return the owner of the DBObject instance or null if no owner has been set. */
//    public CompositeDBObject<?> getOwner();
    
    /** sets the owner of the DBObject instance. */
//	public void setOwner(CompositeDBObject<?> owner);
	
	/** tells if an object has the same definition as another one. */
//	public boolean isIdentical(DBObject other);
	
}
