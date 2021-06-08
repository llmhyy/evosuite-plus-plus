package feature.smartseed.example.empirical.constructor;

public interface DBTable{

    String getName();
    String getDoc();
//    DBCatalog getCatalog();
//    DBSchema getSchema();

//    List<DBColumn> getColumns();
    DBColumn[] getColumns(String[] columnNames);
    DBColumn getColumn(String columnName);
	void addColumn(DBColumn column);
    
//    List<DBIndex> getIndexes();
//    DBIndex getIndex(String indexName);
//	void addIndex(DBIndex dbIndex);
//
//    DBPrimaryKeyConstraint getPrimaryKeyConstraint();
//    void setPrimaryKey(DBPrimaryKeyConstraint dbPrimaryKeyConstraint);
	String[] getPKColumnNames();

//	Set<DBUniqueConstraint> getUniqueConstraints(boolean includePK);
//	void addUniqueConstraint(DBUniqueConstraint uniqueConstraint);
//
//	List<DBCheckConstraint> getCheckConstraints();
//	void addCheckConstraint(DBCheckConstraint checkConstraint);

//	Set<DBForeignKeyConstraint> getForeignKeyConstraints();
//	DBForeignKeyConstraint getForeignKeyConstraint(String[] columnNames);
//	void addForeignKey(DBForeignKeyConstraint dbForeignKeyConstraint);

//	Collection<DBTable> getReferrers();
	void addReferrer(DBTable table);
	
//	long getRowCount(Connection connection);
//    DBRow queryByPK(Object pk, Connection connection) throws SQLException;
//	DBRowIterator allRows(Connection connection) throws SQLException;
//    DBRowIterator queryRowsByCellValues(String[] columnNames, Object[] values, Connection connection) throws SQLException;
//    DBRowIterator queryRows(String whereClause, Connection connection) throws SQLException;
//    HeavyweightIterator<Object> queryPKs(Connection connection);
//	TableRowIterator query(String query, Connection connection);
    
}
