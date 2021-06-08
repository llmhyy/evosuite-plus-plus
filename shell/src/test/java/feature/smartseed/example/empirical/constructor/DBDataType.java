package feature.smartseed.example.empirical.constructor;

import java.io.Serializable;
import java.sql.Types;
import java.util.HashMap;
import java.util.Set;



/*
 * (c) Copyright 2006-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


/**
 * Represents a database column type.<br/><br/>
 * Created: 06.01.2007 10:12:29
 * @author Volker Bergmann
 */
public class DBDataType implements Named, Serializable {

    private static final long serialVersionUID = 7725335502838132325L;
    
    private static final Set<Integer> ALPHA_TYPES = CollectionUtil.toSet(
    	Types.CHAR, Types.CLOB, Types.LONGVARCHAR, Types.LONGNVARCHAR, Types.NCHAR, Types.NCLOB, Types.NVARCHAR, Types.VARCHAR
    );

    private static final Set<Integer> NUMBER_TYPES = CollectionUtil.toSet(
        	Types.BIGINT, Types.DECIMAL, Types.DOUBLE, Types.FLOAT, Types.INTEGER, Types.NUMERIC, Types.SMALLINT, Types.TINYINT
        );

    @SuppressWarnings("unchecked")
	private static final HashMap<String, Integer> JDBC_TYPE_FOR_DB_TYPE = (HashMap<String, Integer>) CollectionUtil.buildMap(
        	"ARRAY", Types.ARRAY,
        	"BIGINT", Types.BIGINT,
        	"BINARY", Types.BINARY,
        	"BIT", Types.BIT,
        	"BLOB", Types.BLOB,
        	"BOOLEAN", Types.BOOLEAN,
        	"BOOL", Types.BOOLEAN,
        	"CHAR", Types.CHAR,
        	"CHARACTER", Types.CHAR, // ANSI SQL
        	"CLOB", Types.CLOB,
        	"DATALINK", Types.DATALINK,
        	"DATE", Types.DATE,
        	"DECIMAL", Types.DECIMAL,
        	"DEC", Types.DECIMAL,
        	"NUMBER", Types.DECIMAL, // Oracle
        	"DISTINCT", Types.DISTINCT,
        	"DOUBLE", Types.DOUBLE,
        	"DOUBLE PRECISION", Types.DOUBLE, // ANSI SQL
        	"BINARY_DOUBLE", Types.DOUBLE, // Oracle
        	"FLOAT", Types.FLOAT,
        	"BINARY_FLOAT", Types.FLOAT, // Oracle
        	"INT", Types.INTEGER,
        	"INTEGER", Types.INTEGER,
        	"BINARY_INTEGER", Types.INTEGER, // Oracle
        	"PLS_INTEGER", Types.INTEGER, // Oracle
        	"OBJECT", Types.JAVA_OBJECT,
        	"LONGNVARCHAR", Types.LONGNVARCHAR,
        	"LONG", Types.LONGNVARCHAR, // Oracle
        	"LONGVARBINARY", Types.LONGVARBINARY,
        	"LONGVARCHAR", Types.LONGVARCHAR,
        	"NCHAR", Types.NCHAR,
        	"NATIONAL CHARACTER", Types.NCHAR, // ANSI SQL
        	"NCLOB", Types.NCLOB,
        	"NULL", Types.NULL,
        	"NUMERIC", Types.NUMERIC,
        	"NVARCHAR", Types.NVARCHAR,
        	"NATIONAL CHARACTER VARYING", Types.NVARCHAR, // ANSI SQL
        	"NVARCHAR2", Types.NVARCHAR, // Oracle
        	"REAL", Types.REAL,
        	"REF", Types.REF,
        	"ROWID", Types.ROWID,
        	"UROWID", Types.ROWID, // Oracle
        	"SMALLINT", Types.SMALLINT,
        	"XML", Types.SQLXML, // MS SQL Server
        	"XMLType", Types.SQLXML, // Oracle
        	"STRUCT", Types.STRUCT,
        	"TIME", Types.TIME,
        	"DATETIME", Types.TIMESTAMP,
        	"TIMESTAMP", Types.TIMESTAMP,
        	"TINYINT", Types.TINYINT,
        	"VARBINARY", Types.VARBINARY,
        	"TEXT", Types.VARCHAR, // MS Access
        	"VARCHAR", Types.VARCHAR,
        	"CHARACTER VARYING", Types.VARCHAR, // ANSI SQL
        	"VARCHAR2", Types.VARCHAR, // Oracle
        	"BIT VARYING", Types.OTHER, // ANSI SQL
        	"INTERVAL", Types.OTHER // ANSI SQL
	);

	private static final HashMap<TypeDescriptor, DBDataType> INSTANCES_BY_TYPE_AND_NAME = new HashMap<TypeDescriptor, DBDataType>();
	private static final HashMap<String, DBDataType> INSTANCES_BY_NAME = new HashMap<String, DBDataType>();

	public static DBDataType getInstance(String name) {
		name = name.toUpperCase();
        DBDataType result = INSTANCES_BY_NAME.get(name);
        if (result == null) {
        	result = new DBDataType(jdbcTypeFor(name), name);
        	INSTANCES_BY_NAME.put(name, result);
        }
		return result;
    }

	public static DBDataType getInstance(int jdbcType, String name) {
		TypeDescriptor descriptor = new TypeDescriptor(jdbcType, name.toUpperCase());
        DBDataType result = INSTANCES_BY_TYPE_AND_NAME.get(descriptor);
        if (result == null) {
        	result = new DBDataType(jdbcType, name);
        	INSTANCES_BY_TYPE_AND_NAME.put(descriptor, result);
        	if (result.jdbcType != descriptor.jdbcType) {
        		// since some DBs return improver types, we might have mapped it to another JDBC type, 
        		// so let's store it with both values to ensure consistency of the 'outside' view with the 'inside'.
        		INSTANCES_BY_TYPE_AND_NAME.put(new TypeDescriptor(jdbcType, name), result);
        	}
        }
		return result;
    }

    private String name;
    private int jdbcType;

    // constructors ----------------------------------------------------------------------------------------------------

    private DBDataType(String name) {
    	this(jdbcTypeFor(name), name);
    }

	private DBDataType(int sqlType, String name) {
    	if (name.equals("NCLOB"))
    		sqlType = Types.NCLOB; // fix for Oracle
        this.jdbcType = sqlType;
        this.name = name.toUpperCase();
    }

    public static int jdbcTypeFor(String name) {
		return JDBC_TYPE_FOR_DB_TYPE.get(name.toUpperCase());
	}


// properties ------------------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public boolean isLOB() {
        return jdbcType == Types.BLOB || jdbcType == Types.CLOB || jdbcType == Types.NCLOB || 
        	name.endsWith("CLOB") || "BLOB".equals(name);
    }

    public boolean isAlpha() {
    	if (ALPHA_TYPES.contains(jdbcType)) // standard types
    		return true;
        return name.endsWith("VARCHAR2") || name.endsWith("CLOB"); // fixes for Oracle
    }

	public boolean isNumber() {
        return NUMBER_TYPES.contains(jdbcType);
	}

	public boolean isTemporal() {
	    return jdbcType == Types.DATE || 
	    	jdbcType == Types.TIMESTAMP || 
	    	name.contains("DATE") || 
	    	name.contains("TIME");
    }

// java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final DBDataType that = (DBDataType) o;
        return name.equals(that.name);
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    private static class TypeDescriptor {
    	final int jdbcType;
    	final String name;
    	
		TypeDescriptor(int jdbcType, String name) {
			this.jdbcType = jdbcType;
			this.name = name;
		}

		@Override
		public int hashCode() {
			return jdbcType * 31 + name.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (other == null || getClass() != other.getClass())
				return false;
			TypeDescriptor that = (TypeDescriptor) other;
			return (this.jdbcType == that.jdbcType && name.equals(that.name));
		}

    }
}

