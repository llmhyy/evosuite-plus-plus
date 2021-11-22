package feature.objectconstruction.testgeneration.example.xbus;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.sun.rowset.CachedRowSetImpl;

public class DBConnection {

	public static DBConnection getInstance(String dbConnectionName) {
		// TODO Auto-generated method stub
		return new DBConnection();
	}

	public ResultSet executeRead(String sqlBefehl) throws SQLException {
		// TODO Auto-generated method stub
		return new CachedRowSetImpl();
	}

}
