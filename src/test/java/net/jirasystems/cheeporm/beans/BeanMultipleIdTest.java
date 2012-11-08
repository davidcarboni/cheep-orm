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
public class BeanMultipleIdTest {

	private static Connection connection;
	private static Orm<BeanMultipleId> orm;
	private static Class<BeanMultipleId> type = BeanMultipleId.class;
	private BeanMultipleId bean = newBean();
	private BeanMultipleId readBack = newBean();

	public BeanMultipleIdTest() throws IllegalAccessException, InstantiationException {
		// Here to cover the newInstance exceptions.
	}

	private BeanMultipleId newBean() throws InstantiationException, IllegalAccessException {
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
		orm = Orm.<BeanMultipleId> newInstance(connection);
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
		int id1 = 0;
		int id2 = 999;
		String name = "testCreate";
		bean.setId1(id1);
		bean.setId2(id2);
		bean.setName(name);

		// When

		// NB, only one ID can be an auto-increment and in this case it is the
		// first ID:
		int id = orm.create(bean);
		bean.setId1(id);

		// Then

		readBack = newBean();
		readBack.setId1(id);
		readBack = orm.read(bean);

		// Verify that the first ID was not inserted:
		Assert.assertFalse(id == id1);
		// Verify that the second ID was inserted:
		Assert.assertEquals(id2, readBack.getId2());
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
		int id1 = 0;
		int id2 = 999;
		String name = "testRead";
		bean.setId1(id1);
		bean.setId2(id2);
		bean.setName(name);
		// NB, only one ID can be an auto-increment and in this case it is the
		// first ID:
		int id = orm.create(bean);
		bean.setId1(id);

		// When

		readBack = newBean();
		readBack.setId1(id);
		readBack.setId2(bean.getId2());
		readBack = orm.read(readBack);

		// Then

		// Verify that the first ID was not inserted:
		Assert.assertFalse(id == id1);
		// Verify that the second ID was inserted:
		Assert.assertEquals(id2, readBack.getId2());
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
		int id2 = 999;
		String[] name = new String[] {"testUpdate 1", "testUpdate 2"};
		bean.setId2(id2);
		bean.setName(name[0]);
		// NB, only one ID can be an auto-increment and in this case it is the
		// first ID:
		int id = orm.create(bean);
		bean.setId1(id);

		// When

		bean.setName(name[1]);
		orm.update(bean);
		readBack = newBean();
		readBack.setId1(id);
		readBack.setId2(id2);
		readBack = orm.read(readBack);

		// Then

		// Verify that the name was updated and retrieved using the two IDs:
		Assert.assertEquals(name[1], readBack.getName());
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
		int id2 = 997;
		String name = "testDelete";
		bean.setId2(id2);
		bean.setName(name);
		// NB, only one ID can be an auto-increment and in this case it is the
		// first ID:
		int id = orm.create(bean);
		bean.setId1(id);

		// When

		orm.delete(bean);
		readBack = newBean();
		readBack.setId1(id);
		readBack.setId2(bean.getId2());
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
