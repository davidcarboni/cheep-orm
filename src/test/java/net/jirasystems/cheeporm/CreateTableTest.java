/**
 * 
 */
package net.jirasystems.cheeporm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import junit.framework.Assert;
import net.jirasystems.cheeporm.beans.BeanTypes;
import net.jirasystems.cheeporm.beans.ExampleBean;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author david
 * 
 */
public class CreateTableTest {

	private static Connection connection;

	private static Reflection reflection = new Reflection();

	private static ExampleBean bean = new ExampleBean();
	private static String tableName = reflection.getTable(bean);

	private static List<Field> fields = reflection.listFields(bean);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Database.beforeSuite();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Database.afterSuite();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		connection = Database.beforeTest(new BeanTypes());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		Database.afterTest(connection);
	}

	/**
	 * Test method for
	 * {@link net.jirasystems.cheeporm.CreateTable#createStatement(java.lang.Object)}
	 * .
	 */
	@Test
	public void testCreateStatement() {

		// Given
		CreateTable createTable = new CreateTable();
		reflection.listFields(bean);

		// When
		String sql = createTable.createStatement(bean);

		// Then
		Assert.assertTrue(sql.toLowerCase().startsWith("create table "));
		Assert.assertTrue(sql.contains(tableName));
		for (Field field : fields) {
			String columnName = reflection.getColumnName(field);
			if (columnName != null) {
				Assert.assertTrue(columnName.contains(columnName));
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.jirasystems.cheeporm.CreateTable#createTable(java.lang.Object, java.sql.Connection)}
	 * .
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreateTable() throws SQLException {

		// Given
		Database.dropTableIfExists(bean, connection);
		CreateTable createTable = new CreateTable();
		Orm<ExampleBean> orm = new Orm<ExampleBean>(connection);

		// When
		createTable.createTable(bean, connection);

		// Then
		bean.setName("name");
		int id = orm.create(bean);
		ExampleBean readBack = new ExampleBean();
		readBack.setId(id);
		readBack.setId2(bean.getId2());
		readBack = orm.read(readBack);
		for (Field field : fields) {
			Object expected = reflection.getFieldValue(field, bean);
			Object actual = reflection.getFieldValue(field, readBack);
			// Round down Timestamp fields as MySQL doesn't store milliseconds:
			if ((expected != null) && (actual != null)
					&& (Timestamp.class.isAssignableFrom(field.getType()))) {
				expected = ((Timestamp) expected).getTime() / 1000;
				actual = ((Timestamp) actual).getTime() / 1000;
			}
			Assert.assertEquals(expected, actual);
		}
	}

	/**
	 * Test method for
	 * {@link net.jirasystems.cheeporm.CreateTable#getReflection()} and
	 * {@link net.jirasystems.cheeporm.CreateTable#setReflection(net.jirasystems.cheeporm.Reflection)}
	 * .
	 */
	@Test
	public void testGetReflection() {

		// Given
		CreateTable createTable = new CreateTable();
		Reflection reflection = new Reflection();

		// When
		createTable.setReflection(reflection);

		// Then
		Assert.assertSame(reflection, createTable.getReflection());
	}

}
