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
public class BeanInsertableTest {

	private static Connection connection;
	private static Orm<BeanInsertable> orm;
	private static Class<BeanInsertable> type = BeanInsertable.class;
	private BeanInsertable bean = newBean();
	private BeanInsertable readBack = newBean();

	public BeanInsertableTest() throws IllegalAccessException, InstantiationException {
		// Here to cover the newInstance exceptions.
	}

	private BeanInsertable newBean() throws InstantiationException, IllegalAccessException {
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
		orm = Orm.<BeanInsertable> newInstance(connection);
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
		String insertable = "testCreate insertable";
		String notInsertable = "testCreate non-insertable";
		bean.setInsertable(insertable);
		bean.setNotInsertable(notInsertable);

		// When

		int id = orm.create(bean);
		bean.setId(id);

		// Then

		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// Verify that the not-insertable field was not inserted into the
		// record:
		Assert.assertNotNull(bean.getNotInsertable());
		Assert.assertNull(readBack.getNotInsertable());

		// Verify that the insertable fied was inserted into the record:
		Assert.assertNotNull(bean.getInsertable());
		Assert.assertNotNull(readBack.getInsertable());
		Assert.assertEquals(bean.getInsertable(), readBack.getInsertable());
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
		String insertable = "testCreate insertable";
		String notInsertable = "testCreate non-insertable";
		bean.setInsertable(insertable);
		bean.setNotInsertable(notInsertable);
		int id = orm.create(bean);

		// When
		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// Then

		// Verify that the not-insertable field was not inserted into the
		// record:
		Assert.assertNotNull(bean.getNotInsertable());
		Assert.assertNull(readBack.getNotInsertable());

		// Verify that the insertable fied was inserted into the record:
		Assert.assertNotNull(bean.getInsertable());
		Assert.assertNotNull(readBack.getInsertable());
		Assert.assertEquals(bean.getInsertable(), readBack.getInsertable());
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
		String[] insertable = {"testUpdate insertable 1", "testUpdate insertable 2"};
		String[] notInsertable = {"testUpdate non-insertable 1", "testUpdate non-insertable 2"};
		bean.setInsertable(insertable[0]);
		bean.setNotInsertable(notInsertable[0]);
		int id = orm.create(bean);
		bean.setId(id);

		// When
		bean.setInsertable(insertable[1]);
		bean.setNotInsertable(notInsertable[1]);
		orm.update(bean);
		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// Then
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

		bean = newBean();
		String insertable = "testDelete insertable";
		String notInsertable = "testDelete non-insertable";
		bean.setInsertable(insertable);
		bean.setNotInsertable(notInsertable);
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
