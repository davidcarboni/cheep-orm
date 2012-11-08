/**
 * 
 */
package net.jirasystems.cheeporm;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author david
 * 
 */
public class SqlTypeTest {

	/**
	 * This method just covers the generated methods of the enum for coverage
	 * purposes. No actual testing is performed here.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		for (SqlType value : SqlType.values()) {
			SqlType rechecked = SqlType.valueOf(value.name());
			Assert.assertEquals(value, rechecked);
		}
	}

	/**
	 * Tests that the entries in {@link SqlType#toJavaMap} and
	 * {@link SqlType#toSqlMap} can be mapped back and forth repeatably.
	 * 
	 * @throws java.lang.Exception
	 */
	@Test
	public void testCommutableFromSql() throws Exception {

		// Some of the SQL types map to the same Java type, but once mapped to
		// Java, the mapping should be stable. Therefore, we go from the SQL
		// type to a Java type to start, then to SQL and back to Java. Finally,
		// we check that the Java types are the same.

		// Given
		for (SqlType sqlType : SqlType.toJavaMap.keySet()) {

			// When
			Class<?> javaType1 = SqlType.toJavaMap.get(sqlType);
			SqlType commuted = SqlType.toSqlMap.get(javaType1);
			Class<?> javaType2 = SqlType.toJavaMap.get(commuted);

			// Then
			Assert.assertEquals(javaType1, javaType2);
		}
	}

	/**
	 * Tests that the entries in {@link SqlType#toJavaMap} and
	 * {@link SqlType#toSqlMap} can be mapped back and forth repeatably.
	 * 
	 * @throws java.lang.Exception
	 */
	@Test
	public void testCommutableFromJava() throws Exception {

		// Test the Java mappings in the same way:

		// Given
		for (Class<?> javaType : SqlType.toSqlMap.keySet()) {

			// When
			SqlType sqlType1 = SqlType.toSqlMap.get(javaType);
			Class<?> commuted = SqlType.toJavaMap.get(sqlType1);
			SqlType sqlType2 = SqlType.toSqlMap.get(commuted);

			// Then
			Assert.assertEquals(sqlType1, sqlType2);
		}
	}

}
