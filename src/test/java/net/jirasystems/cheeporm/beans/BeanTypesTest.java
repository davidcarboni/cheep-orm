/**
 * 
 */
package net.jirasystems.cheeporm.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

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
public class BeanTypesTest {

	private static Connection connection;
	private static Orm<BeanTypes> orm;
	private static Class<BeanTypes> type = BeanTypes.class;
	private BeanTypes bean = newBean();
	private BeanTypes readBack = newBean();

	private final long day = 1000 * 60 * 60 * 24;

	public BeanTypesTest() throws IllegalAccessException, InstantiationException {
		// Here to cover the newInstance exceptions.
	}

	private BeanTypes newBean() throws InstantiationException, IllegalAccessException {
		return type.newInstance();
	}

	/**
	 * @throws java.lang.Exception
	 * 
	 * @throws Exception
	 *             If the database setup throws any error
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Database.beforeSuite();
	}

	/**
	 * @throws java.lang.Exception
	 * 
	 * @throws Exception
	 *             If the database teardown throws any error
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Database.afterSuite();
	}

	/**
	 * 
	 * @throws Exception
	 *             If the database setup throws any error
	 */
	@Before
	public void setUp() throws Exception {
		connection = Database.beforeTest(new BeanTypes());
		// Construct this for each connection we create:
		orm = Orm.<BeanTypes> newInstance(connection);
	}

	/**
	 * 
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
		BigDecimal bigDecimalValue = new BigDecimal(7);
		Boolean booleanValue = Boolean.TRUE;
		Short shortValue = Short.valueOf((short) 2468);
		Byte byteValue = Byte.valueOf((byte) 128);
		Date dateValue = newDate(0);
		Double doubleValue = Double.valueOf(234.567D);
		Float floatValue = Float.valueOf(567.890F);
		Integer integerValue = Integer.valueOf(235467);
		Long longValue = Long.valueOf(System.currentTimeMillis());
		String stringValue = "testCreate";
		Timestamp timestampValue = newTimestamp(0);
		Time timeValue = newTime(0);

		bean.setBigDecimalValue(bigDecimalValue);
		bean.setBooleanValue(booleanValue);
		bean.setShortValue(shortValue);
		bean.setByteValue(byteValue);
		bean.setDateValue(dateValue);
		bean.setDoubleValue(doubleValue);
		bean.setFloatValue(floatValue);
		bean.setIntegerValue(integerValue);
		bean.setLongValue(longValue);
		bean.setStringValue(stringValue);
		bean.setTimestampValue(timestampValue);
		bean.setTimeValue(timeValue);

		// When
		int id = orm.create(bean);
		bean.setId(id);
		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// Then
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
		BigDecimal bigDecimalValue = new BigDecimal(78);
		Boolean booleanValue = Boolean.FALSE;
		Short shortValue = Short.valueOf((short) 2648);
		Byte byteValue = Byte.valueOf((byte) 64);
		Date dateValue = newDate(day);
		Double doubleValue = Double.valueOf(234.765D);
		Float floatValue = Float.valueOf(567.098F);
		Integer integerValue = Integer.valueOf(675423);
		Long longValue = Long.valueOf(System.currentTimeMillis() + 20000);
		String stringValue = "testRead";
		Timestamp timestampValue = newTimestamp(-day);
		Time timeValue = newTime(day * 7);

		bean.setBigDecimalValue(bigDecimalValue);
		bean.setBooleanValue(booleanValue);
		bean.setShortValue(shortValue);
		bean.setByteValue(byteValue);
		bean.setDateValue(dateValue);
		bean.setDoubleValue(doubleValue);
		bean.setFloatValue(floatValue);
		bean.setIntegerValue(integerValue);
		bean.setLongValue(longValue);
		bean.setStringValue(stringValue);
		bean.setTimestampValue(timestampValue);
		bean.setTimeValue(timeValue);

		int id = orm.create(bean);
		bean.setId(id);

		// When
		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// Then
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
		BigDecimal[] bigDecimalValue = new BigDecimal[] {new BigDecimal(78), new BigDecimal(79)};
		Boolean[] booleanValue = new Boolean[] {Boolean.FALSE, Boolean.TRUE};
		Short[] shortValue = new Short[] {Short.valueOf((short) 2648), Short.valueOf((short) 2649)};
		Byte[] byteValue = new Byte[] {Byte.valueOf((byte) 64), Byte.valueOf((byte) 65)};
		Date[] dateValue = new Date[] {newDate(day * 2), newDate(day * 3)};
		Double[] doubleValue = new Double[] {Double.valueOf(234.765D), Double.valueOf(235.765D)};
		Float[] floatValue = new Float[] {Float.valueOf(236.765F), Float.valueOf(237.765F)};
		Integer[] integerValue = new Integer[] {Integer.valueOf(675423), Integer.valueOf(675424)};
		Long[] longValue = new Long[] {Long.valueOf(System.currentTimeMillis() + 20000),
				Long.valueOf(System.currentTimeMillis() - 20000)};
		String[] stringValue = new String[] {"testUpdate 1", "testUpdate 2"};
		Timestamp[] timestampValue = new Timestamp[] {newTimestamp(day * 4), newTimestamp(day * 5)};
		Time[] timeValue = new Time[] {newTime(day * 6), newTime(day * 7)};

		bean.setBigDecimalValue(bigDecimalValue[0]);
		bean.setBooleanValue(booleanValue[0]);
		bean.setShortValue(shortValue[0]);
		bean.setByteValue(byteValue[0]);
		bean.setDateValue(dateValue[0]);
		bean.setDoubleValue(doubleValue[0]);
		bean.setFloatValue(floatValue[0]);
		bean.setIntegerValue(integerValue[0]);
		bean.setLongValue(longValue[0]);
		bean.setStringValue(stringValue[0]);
		bean.setTimestampValue(timestampValue[0]);
		bean.setTimeValue(timeValue[0]);

		bean.setId(orm.create(bean));

		// When
		bean.setBigDecimalValue(bigDecimalValue[1]);
		bean.setBooleanValue(booleanValue[1]);
		bean.setShortValue(shortValue[1]);
		bean.setByteValue(byteValue[1]);
		bean.setDateValue(dateValue[1]);
		bean.setDoubleValue(doubleValue[1]);
		bean.setFloatValue(floatValue[1]);
		bean.setIntegerValue(integerValue[1]);
		bean.setLongValue(longValue[1]);
		bean.setStringValue(stringValue[1]);
		bean.setTimestampValue(timestampValue[1]);
		bean.setTimeValue(timeValue[1]);

		orm.update(bean);
		readBack = orm.read(bean);

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

		// Given
		bean = newBean();
		BigDecimal bigDecimalValue = new BigDecimal(78);
		Boolean booleanValue = Boolean.FALSE;
		Short shortValue = Short.valueOf((short) 2648);
		Byte byteValue = Byte.valueOf((byte) 64);
		Date dateValue = newDate(day * -1);
		Double doubleValue = Double.valueOf(234.765D);
		Float floatValue = Float.valueOf(567.098F);
		Integer integerValue = Integer.valueOf(675423);
		Long longValue = Long.valueOf(System.currentTimeMillis() + 20000);
		String stringValue = "testRead";
		Timestamp timestampValue = newTimestamp(day * -2);
		Time timeValue = newTime(day * -3);

		bean.setBigDecimalValue(bigDecimalValue);
		bean.setBooleanValue(booleanValue);
		bean.setShortValue(shortValue);
		bean.setByteValue(byteValue);
		bean.setDateValue(dateValue);
		bean.setDoubleValue(doubleValue);
		bean.setFloatValue(floatValue);
		bean.setIntegerValue(integerValue);
		bean.setLongValue(longValue);
		bean.setStringValue(stringValue);
		bean.setTimestampValue(timestampValue);
		bean.setTimeValue(timeValue);

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
	// bean = newBean();
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

	private Time newTime(long offset) {

		// Here, we need to truncate to to 1 Jan 1970 as the time value does
		// actually store the date and therefore doesn't match what comes back
		// from the DB:
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.YEAR, 1970);
		calendar.set(Calendar.MILLISECOND, 0);
		Time time = new Time(calendar.getTimeInMillis());

		return time;
	}

	private Date newDate(long offset) {

		// Here, we need to truncate to just the current day as date only stores
		// the day/month/year:

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date date = new Date(calendar.getTimeInMillis());

		return date;
	}

	private Timestamp newTimestamp(long offset) {

		// Here, we need to truncate to the second as the db does not store
		// milliseconds:

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
		calendar.set(Calendar.MILLISECOND, 0);
		Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

		return timestamp;
	}

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
