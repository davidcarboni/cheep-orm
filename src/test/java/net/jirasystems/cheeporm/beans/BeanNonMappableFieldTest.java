/**
 * 
 */
package net.jirasystems.cheeporm.beans;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.Assert;
import net.jirasystems.cheeporm.Database;
import net.jirasystems.cheeporm.Orm;
import net.jirasystems.cheeporm.test.BeanTester;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author david
 * 
 */
public class BeanNonMappableFieldTest {

	private static Connection connection;
	private static Orm<BeanNonMappableField> orm;
	private static Class<BeanNonMappableField> type = BeanNonMappableField.class;
	private BeanNonMappableField bean = newBean();
	private BeanNonMappableField readBack = newBean();

	public BeanNonMappableFieldTest() throws IllegalAccessException, InstantiationException {
		// Here to cover the newInstance exceptions.
	}

	private BeanNonMappableField newBean() throws InstantiationException, IllegalAccessException {
		return type.newInstance();
	}

	/**
	 * @throws Exception
	 *             If the database setup throws any error
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Database.beforeSuite();
	}

	/**
	 * @throws Exception
	 *             If the database teardown throws any error
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Database.afterSuite();
	}

	/**
	 * @throws Exception
	 *             If the database setup throws any error
	 */
	@Before
	public void setUp() throws Exception {
		connection = Database.beforeTest(bean);
		// Construct this for each connection we create:
		orm = Orm.<BeanNonMappableField> newInstance(connection);
	}

	/**
	 * @throws Exception
	 *             If the database teardown throws any error
	 */
	@After
	public void tearDown() throws Exception {
		Database.afterTest(connection);
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Orm#create(java.lang.Object)}.
	 * 
	 * @throws SQLException .
	 * @throws IllegalAccessException .
	 * @throws InstantiationException .
	 */
	@Test
	public void testCreate() throws SQLException, InstantiationException, IllegalAccessException {

		// Given
		bean = newBean();
		String mappableFieldValue = "mappableFieldValue";
		BeanNonMappableField nonMappableFieldValue = newBean();
		bean.setMappableField(mappableFieldValue);
		bean.setNonMappableField(nonMappableFieldValue);

		// When
		int id = orm.create(bean);
		bean.setId(id);
		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// Then
		Assert.assertEquals(mappableFieldValue, readBack.getMappableField());
		Assert.assertNull(readBack.getNonMappableField());
		Beans.compareBeans(bean, readBack);
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Orm#read(java.lang.Object)}.
	 * 
	 * @throws SQLException .
	 * @throws IllegalAccessException .
	 * @throws InstantiationException .
	 */
	@Test
	public void testRead() throws SQLException, InstantiationException, IllegalAccessException {

		// Given
		bean = newBean();
		String mappableFieldValue = "mappableFieldValue";
		BeanNonMappableField nonMappableFieldValue = newBean();
		bean.setMappableField(mappableFieldValue);
		bean.setNonMappableField(nonMappableFieldValue);

		int id = orm.create(bean);
		bean.setId(id);

		// When
		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// ThencolumnValue
		Assert.assertEquals(mappableFieldValue, readBack.getMappableField());
		Assert.assertNull(readBack.getNonMappableField());
		Beans.compareBeans(bean, readBack);
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Orm#update(java.lang.Object)}.
	 * 
	 * @throws SQLException .
	 * @throws IllegalAccessException .
	 * @throws InstantiationException .
	 */
	@Test
	public void testUpdate() throws SQLException, InstantiationException, IllegalAccessException {

		// Given
		bean = newBean();
		String[] mappableFieldValue = new String[] {"mappableFieldValue o", "mappableFieldValue e"};
		BeanNonMappableField[] nonMappableFieldValue = new BeanNonMappableField[] {newBean(), newBean()};
		nonMappableFieldValue[0].setMappableField("nonMappableFieldValue c");
		nonMappableFieldValue[1].setMappableField("nonMappableFieldValue d");
		bean.setMappableField(mappableFieldValue[0]);
		bean.setNonMappableField(nonMappableFieldValue[0]);

		int id = orm.create(bean);
		bean.setId(id);

		// When
		bean.setMappableField(mappableFieldValue[1]);
		bean.setNonMappableField(nonMappableFieldValue[1]);
		orm.update(bean);
		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// Then
		Assert.assertEquals(mappableFieldValue[1], readBack.getMappableField());
		Assert.assertNull(readBack.getNonMappableField());
		Beans.compareBeans(bean, readBack);
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Orm#delete(java.lang.Object)}.
	 * 
	 * @throws SQLException .
	 * @throws IllegalAccessException .
	 * @throws InstantiationException .
	 */
	@Test
	public void testDelete() throws SQLException, InstantiationException, IllegalAccessException {

		// Given
		bean = newBean();
		String mappableFieldValue = "mappableFieldValue";
		BeanNonMappableField nonMappableFieldValue = newBean();
		bean.setMappableField(mappableFieldValue);
		bean.setNonMappableField(nonMappableFieldValue);

		int id = orm.create(bean);
		bean.setId(id);

		// When
		orm.delete(bean);
		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// Then
		Assert.assertNull(readBack);
	}

	// /**
	// * Test method for
	// * {@link net.jirasystems.cheeporm.Orm#list(java.lang.Object)}.
	// *
	// * @throws SQLException
	// */
	// @Test
	// public void testList() throws SQLException {
	//
	// // Given
	// int count = 5;
	// for (int i = 0; i < count; i++) {
	//
	// BeanTypes bean = new BeanTypes();
	// // Name and select are not null fields
	// bean.setName("testList " + i);
	// bean.setSelection(BeanTypes.Select.a);
	// orm.create(bean);
	// }
	// BeanTypes example = new BeanTypes();
	//
	// // When
	// List<BeanTypes> results = orm.list(example);
	//
	// // Then
	// Assert.assertEquals(count, results.size());
	// }

	/**
	 * This method exercises all of the getters and setters of the bean to assure that the beans are
	 * correct and to clean up test coverage as not all of the methods in the beans are called
	 * explicitly by the tests.
	 * 
	 * @throws Exception
	 *             If the bean test throws any error
	 */
	@Test
	public void testCoverage() throws Exception {

		BeanTester beanTester = new BeanTester();
		beanTester.testBean(newBean());
	}
}
