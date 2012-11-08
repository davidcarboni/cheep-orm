/**
 * 
 */
package net.jirasystems.cheeporm;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines a subset of the standard JDBC type mappings. These are expected to be as
 * follows. Because a number of the JDBC types map to the same Java type, this makes clean
 * one-to-one mapping difficult. As a result, some of the JDBC types are effectively "dropped" by
 * this mapping. For example, both VARCHAR and CHAR map to String, but String maps only to VARCHAR.
 * 
 * In practice, if you use the {@link CreateTable} class to generate the SQL for the table creation
 * scripts, you will get a one-to-one mapping because the Java types all map to one preferred SQL
 * type.
 * 
 * As a result, the mappings from database types to Java types, and back again, are as follows:
 * <ul>
 * <li>NVARCHAR - String - NVARCHAR</li>
 * <li>CHAR - String - VARCHAR</li>
 * <li>LONGVARCHAR - String - VARCHAR</li>
 * <li>DECIMAL - java.math.BigDecimal - DECIMAL</li>
 * <li>NUMERIC - java.math.BigDecimal - DECIMAL</li>
 * <li>BOOLEAN - Boolean - BIT (because MSSQL doesn't have BOOLEAN)</li>
 * <li>BIT - Boolean - BIT</li>
 * <li>TINYINT - Byte - TINYINT</li>
 * <li>SMALLINT - Short - SMALLINT</li>
 * <li>INTEGER - Int - INTEGER</li>
 * <li>BIGINT - Long - BIGINT</li>
 * <li>REAL - Float - REAL</li>
 * <li>DOUBLE - Double - DOUBLE</li>
 * <li>FLOAT - Double - DOUBLE</li>
 * <li>DATE - java.sql.Date - DATE</li>
 * <li>TIME - java.sql.Time - TIME</li>
 * <li>TIMESTAMP - java.sql.Timestamp - TIMESTAMP</li>
 * </ul>
 * 
 * The following JDBC standard mappings are not supported:
 * <ul>
 * <li>BINARY byte[]</li>
 * <li>VARBINARY byte[]</li>
 * <li>LONGVARBINARY byte[]</li>
 * <li>CLOB Clob</li>
 * <li>BLOB Blob</li>
 * <li>ARRAY Array</li>
 * <li>DISTINCT mapping of underlying type</li>
 * <li>STRUCT Struct</li>
 * <li>REF Ref</li>
 * <li>DATALINK java.net.URL</li>
 * <li>JAVA_OBJECT underlying Java class</li>
 * </ul>
 * 
 * @author david
 * 
 */
public enum SqlType {

	NVARCHAR,
	CHAR,
	LONGVARCHAR,
	DECIMAL,
	NUMERIC,
	BOOLEAN,
	BIT,
	TINYINT,
	SMALLINT,
	INTEGER,
	BIGINT,
	REAL,
	DOUBLE,
	FLOAT,
	DATE,
	TIME,
	TIMESTAMP;

	public int sqlType;

	public static Map<SqlType, Class<?>> toJavaMap = new HashMap<SqlType, Class<?>>();
	public static Map<Class<?>, SqlType> toSqlMap = new HashMap<Class<?>, SqlType>();

	static {
		setupToJavaMap();
		setupToSqlMap();
	}

	private SqlType() {
		Exception exception = null;
		Object sqlType;
		try {
			sqlType = java.sql.Types.class.getField(name()).get(this);
			this.sqlType = ((Integer) sqlType).intValue();
		} catch (IllegalArgumentException e) {
			exception = e;
		} catch (SecurityException e) {
			exception = e;
		} catch (IllegalAccessException e) {
			exception = e;
		} catch (NoSuchFieldException e) {
			exception = e;
		}
		if (exception != null) {
			throw new RuntimeException("Unable to instantiate " + this.getClass().getName() + " for value " + this,
					exception);
		}
	}

	private static void setupToJavaMap() {

		Map<SqlType, Class<?>> result = new HashMap<SqlType, Class<?>>();

		result.put(NVARCHAR, String.class);
		result.put(CHAR, Character.class);
		result.put(LONGVARCHAR, String.class);
		result.put(DECIMAL, java.math.BigDecimal.class);
		result.put(NUMERIC, java.math.BigDecimal.class);
		result.put(BOOLEAN, Boolean.class);
		result.put(BIT, Boolean.class);
		result.put(TINYINT, Byte.class);
		result.put(SMALLINT, Short.class);
		result.put(INTEGER, Integer.class);
		result.put(BIGINT, Long.class);
		result.put(REAL, Float.class);
		result.put(DOUBLE, Double.class);
		result.put(FLOAT, Double.class);
		result.put(DATE, java.sql.Date.class);
		result.put(TIME, java.sql.Time.class);
		result.put(TIMESTAMP, java.sql.Timestamp.class);

		toJavaMap = result;
	}

	private static void setupToSqlMap() {

		Map<Class<?>, SqlType> result = new HashMap<Class<?>, SqlType>();

		result.put(String.class, NVARCHAR);
//		result.put(String.class, VARCHAR);
		result.put(Character.class, CHAR);
		// toSqlMap.put(String.class, CHAR);
		// toSqlMap.put(String.class, LONGVARCHAR);
		result.put(java.math.BigDecimal.class, DECIMAL);
		// toSqlMap.put(java.math.BigDecimal.class, NUMERIC);
//		result.put(Boolean.class, BOOLEAN);
		result.put(Boolean.class, BIT);
		result.put(Byte.class, TINYINT);
		result.put(Short.class, SMALLINT);
		result.put(Integer.class, INTEGER);
		result.put(Long.class, BIGINT);
		result.put(Float.class, REAL);
		result.put(Double.class, DOUBLE);
		// toSqlMap.put(Double.class, FLOAT);
		result.put(java.sql.Date.class, DATE);
		result.put(java.sql.Time.class, TIME);
		result.put(java.sql.Timestamp.class, TIMESTAMP);

		toSqlMap = result;
	}
}
