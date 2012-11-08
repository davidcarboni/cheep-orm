/**
 * 
 */
package net.jirasystems.cheeporm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import net.jirasystems.cheeporm.beans.Beans;
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
public class OrmTest {

	private static Connection connection;
	private static Orm<ExampleBean> orm;
	private static Class<ExampleBean> type = ExampleBean.class;
	private ExampleBean bean = newBean();
	private ExampleBean readBack = newBean();

	public OrmTest() throws IllegalAccessException, InstantiationException {
		// Here to cover the newInstance exceptions.
	}

	private ExampleBean newBean() throws InstantiationException, IllegalAccessException {
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

		bean = newBean();
		// Set required default value:
		final int id2 = 22;
		bean.setId2(Integer.valueOf(id2));

		connection = Database.beforeTest(bean);
		// Construct this for each connection we create:
		orm = Orm.<ExampleBean> newInstance(connection);
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
	 * Test method for
	 * {@link net.jirasystems.cheeporm.Orm#newInstance(java.lang.Class, java.sql.Connection)} .
	 */
	@Test
	public void testNewInstance() {
		Orm<ExampleBean> orm = Orm.<ExampleBean> newInstance(connection);
		Assert.assertNotNull(orm);
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Orm#Orm(java.sql.Connection)}.
	 */
	@Test
	public void testOrm() {
		Orm<ExampleBean> orm = new Orm<ExampleBean>(connection);
		Assert.assertNotNull(orm.getJdbc());
		Assert.assertNotNull(orm.getReflection());
		Assert.assertNotNull(orm.getSql());
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Orm#create(java.lang.Object)}.
	 * 
	 * @throws SQLException
	 *             {@link SQLException}
	 * @throws IllegalAccessException
	 *             {@link IllegalAccessException}
	 * @throws InstantiationException
	 *             {@link InstantiationException}
	 */
	@Test
	public void testCreate() throws SQLException, InstantiationException, IllegalAccessException {

		// Given
		bean = newBean();
		// Name and select are not null fields
		bean.setName("testCreate");
		bean.setSelection(ExampleBean.Select.c);

		// When
		int id = orm.create(bean);
		bean.setId(Integer.valueOf(id));
		readBack = newBean();
		readBack.setId(Integer.valueOf(id));
		readBack = orm.read(readBack);

		// Then
		Beans.compareBeans(bean, readBack);
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Orm#read(java.lang.Object)}.
	 * 
	 * @throws SQLException
	 *             {@link SQLException}
	 * @throws IllegalAccessException
	 *             {@link IllegalAccessException}
	 * @throws InstantiationException
	 *             {@link InstantiationException}
	 */
	@Test
	public void testRead() throws SQLException, InstantiationException, IllegalAccessException {

		// Given
		bean = newBean();
		// Name and select are not null fields
		bean.setName("testRead");
		bean.setSelection(ExampleBean.Select.b);
		int id = orm.create(bean);
		bean.setId(Integer.valueOf(id));

		// When
		readBack = newBean();
		readBack.setId(Integer.valueOf(id));
		readBack = orm.read(readBack);

		// Then
		Beans.compareBeans(bean, readBack);
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Orm#update(java.lang.Object)}.
	 * 
	 * @throws SQLException
	 *             {@link SQLException}
	 * @throws IllegalAccessException
	 *             {@link IllegalAccessException}
	 * @throws InstantiationException
	 *             {@link InstantiationException}
	 */
	@Test
	public void testUpdate() throws SQLException, InstantiationException, IllegalAccessException {

		// Given
		bean = newBean();
		// Name and select are not null fields
		bean.setName("testUpdate");
		bean.setSelection(ExampleBean.Select.a);
		int id = orm.create(bean);
		bean.setId(Integer.valueOf(id));

		// When
		bean.setName("updated");
		orm.update(bean);
		readBack = newBean();
		readBack.setId(Integer.valueOf(id));
		readBack = orm.read(readBack);

		// Then
		Beans.compareBeans(bean, readBack);
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Orm#delete(java.lang.Object)}.
	 * 
	 * @throws SQLException
	 *             {@link SQLException}
	 * @throws IllegalAccessException
	 *             {@link IllegalAccessException}
	 * @throws InstantiationException
	 *             {@link InstantiationException}
	 */
	@Test
	public void testDelete() throws SQLException, InstantiationException, IllegalAccessException {

		// Given
		bean = newBean();
		// Name and select are not null fields
		bean.setName("testDelete");
		bean.setSelection(ExampleBean.Select.a);
		int id = orm.create(bean);
		bean.setId(Integer.valueOf(id));

		// When
		orm.delete(bean);
		readBack = newBean();
		readBack.setId(Integer.valueOf(id));
		readBack = orm.read(readBack);

		// Then
		Assert.assertNull(readBack);
	}

	/**
	 * Test method for
	 * {@link net.jirasystems.cheeporm.Orm#list(java.lang.Object, java.lang.reflect.Field...)}.
	 * 
	 * @throws SQLException
	 *             {@link SQLException}
	 * @throws IllegalAccessException
	 *             {@link IllegalAccessException}
	 * @throws InstantiationException
	 *             {@link InstantiationException}
	 */
	@Test
	public void testList() throws SQLException, InstantiationException, IllegalAccessException {

		// Given
		final int count = 5;
		for (int i = 0; i < count; i++) {

			bean = newBean();
			// Name and select are not null fields
			bean.setName("testList " + i);
			bean.setSelection(ExampleBean.Select.a);
			orm.create(bean);
		}
		ExampleBean example = new ExampleBean();

		// When
		List<ExampleBean> results = orm.list(example);

		// Then
		Assert.assertEquals(count, results.size());
	}

	/**
	 * Test method for
	 * {@link net.jirasystems.cheeporm.Orm#iterate(Object, java.lang.reflect.Field...)} .
	 * 
	 * @throws SQLException
	 *             {@link SQLException}
	 * @throws IllegalAccessException
	 *             {@link IllegalAccessException}
	 * @throws InstantiationException
	 *             {@link InstantiationException}
	 */
	@Test
	public void testIterate() throws SQLException, InstantiationException, IllegalAccessException {

		// Given
		final int count = 5;
		for (int i = 0; i < count; i++) {

			bean = newBean();
			// Name and select are not null fields
			bean.setName("testList " + i);
			bean.setSelection(ExampleBean.Select.a);
			orm.create(bean);
		}
		ExampleBean example = new ExampleBean();

		// When
		Iterator<ExampleBean> results = orm.iterate(example);

		// Then
		int size = 0;
		while (results.hasNext()) {
			if (results.next() != null) {
				size++;
			}
		}
		Assert.assertEquals(count, size);
	}

}
