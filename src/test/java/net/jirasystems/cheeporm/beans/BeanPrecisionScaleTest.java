/**
 * 
 */
package net.jirasystems.cheeporm.beans;

import java.math.BigDecimal;
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
public class BeanPrecisionScaleTest {

	private static Connection connection;
	private static Orm<BeanPrecisionScale> orm;
	private static Class<BeanPrecisionScale> type = BeanPrecisionScale.class;
	private BeanPrecisionScale bean = newBean();
	private BeanPrecisionScale readBack = newBean();

	public BeanPrecisionScaleTest() throws IllegalAccessException, InstantiationException {
		// Here to cover the newInstance exceptions.
	}

	private BeanPrecisionScale newBean() throws InstantiationException, IllegalAccessException {
		return type.newInstance();
	}

	/**
	 * 
	 * @throws Exception
	 *             If the database setup throws any error
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Database.beforeSuite();
	}

	/**
	 * 
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
		orm = Orm.<BeanPrecisionScale> newInstance(connection);
	}

	/**
	 * @throws Exception
	 *             If the database teardown throws any error
	 */
	@After
	public void tearDown() throws Exception {
		Database.afterTest(connection);
	}

	// /**
	// * Test method for
	// * {@link net.jirasystems.cheeporm.Orm#create(java.lang.Object)}.
	// *
	// * @throws SQLException
	// * @throws IllegalAccessException
	// * @throws InstantiationException
	// */
	// @Test
	// public void testCreate() throws SQLException, InstantiationException,
	// IllegalAccessException {
	//
	// // Given
	// populateBean();
	//
	// // When
	// int id = orm.create(bean);
	// bean.setId(id);
	// readBack = newBean();
	// readBack.setId(id);
	// readBack = orm.read(readBack);
	//
	// // Then
	// Beans.compareBeans(bean, readBack);
	// }
	//
	// /**
	// * Test method for
	// * {@link net.jirasystems.cheeporm.Orm#create(java.lang.Object)}.
	// *
	// * @throws SQLException
	// * @throws IllegalAccessException
	// * @throws InstantiationException
	// */
	// @Test
	// public void testCreateNull() throws SQLException, InstantiationException,
	// IllegalAccessException {
	//
	// // Given
	// bean = newBean();
	// String nonNullableFieldValue = null;
	// String nullableFieldValue = "nullableFieldValue";
	// // bean.setNonNullableField(nonNullableFieldValue);
	// // bean.setNullableField(nullableFieldValue);
	//
	// // When
	// try {
	// int id = orm.create(bean);
	//
	// // Then
	// Assert.fail("Succeeded in setting a non-nullable field to null.");
	// } catch (MySQLIntegrityConstraintViolationException e) {
	// // Expected
	// }
	// }

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
		String nonNullableFieldValue = "nonNullableFieldValue";
		String nullableFieldValue = null;
		// bean.setNonNullableField(nonNullableFieldValue);
		// bean.setNullableField(nullableFieldValue);

		int id = orm.create(bean);
		bean.setId(id);

		// When
		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// Then
		// Assert.assertEquals(nonNullableFieldValue, readBack
		// .getNonNullableField());
		// Assert.assertNull(readBack.getNullableField());
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
		String[] nonNullableFieldValue = new String[] {"nonNullableFieldValue", "nonNullableFieldValue 2"};
		String[] nullableFieldValue = new String[] {"nullableFieldValue", null};
		// bean.setNonNullableField(nonNullableFieldValue[0]);
		// bean.setNullableField(nullableFieldValue[0]);

		int id = orm.create(bean);
		bean.setId(id);

		// When
		// bean.setNonNullableField(nonNullableFieldValue[1]);
		// bean.setNullableField(nullableFieldValue[1]);
		orm.update(bean);
		readBack = newBean();
		readBack.setId(id);
		readBack = orm.read(readBack);

		// Then
		// Assert.assertEquals(nonNullableFieldValue[1], readBack
		// .getNonNullableField());
		// Assert.assertNull(readBack.getNullableField());
		Beans.compareBeans(bean, readBack);
	}

	// /**
	// * Test method for
	// * {@link net.jirasystems.cheeporm.Orm#update(java.lang.Object)}.
	// *
	// * @throws SQLException
	// * @throws IllegalAccessException
	// * @throws InstantiationException
	// */
	// @Test
	// public void testUpdateNull() throws SQLException, InstantiationException,
	// IllegalAccessException {
	//
	// // Given
	// bean = newBean();
	// String[] nonNullableFieldValue = new String[] {
	// "nonNullableFieldValue", null };
	// String[] nullableFieldValue = new String[] { "nullableFieldValue", null
	// };
	// // bean.setNonNullableField(nonNullableFieldValue[0]);
	// // bean.setNullableField(nullableFieldValue[0]);
	//
	// int id = orm.create(bean);
	// bean.setId(id);
	//
	// // When
	// try {
	// // bean.setNonNullableField(nonNullableFieldValue[1]);
	// // bean.setNullableField(nullableFieldValue[1]);
	// orm.update(bean);
	//
	// // Then
	// Assert.fail("Succeeded in setting a non-nullable field to null.");
	// } catch (MySQLIntegrityConstraintViolationException e) {
	// // Expected
	// }
	// }

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
		String nonNullableFieldValue = "nonNullableFieldValue";
		String nullableFieldValue = null;
		// bean.setNonNullableField(nonNullableFieldValue);
		// bean.setNullableField(nullableFieldValue);

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

	private void populateBean() throws InstantiationException, IllegalAccessException {

		bean = newBean();

		double noneDouble = 1.1;
		float noneFloat = 2.2F;
		int noneInteger = 3;
		long noneLong = 4L;
		short noneShort = 5;
		byte noneByte = 6;
		BigDecimal noneBigDecimal = BigDecimal.valueOf(noneByte + 1);

		double precisionDouble = noneDouble * 2;
		float precisionFloat = noneFloat * 2;
		int precisionInteger = noneInteger * 2;
		long precisionLong = noneLong * 2;
		short precisionShort = (short) (noneShort * 2);
		byte precisionByte = (byte) (noneByte * 2);
		BigDecimal precisionBigDecimal = BigDecimal.valueOf(noneByte + 1);

		double scaleDouble = precisionDouble * 2;
		float scaleFloat = precisionFloat * 2;
		int scaleInteger = precisionInteger * 2;
		long scaleLong = precisionLong * 2;
		short scaleShort = (short) (precisionShort * 2);
		byte scaleByte = (byte) (precisionByte * 2);
		BigDecimal scaleBigDecimal = BigDecimal.valueOf(precisionBigDecimal.longValue() * 2);

		double precisionScaleDouble = scaleDouble * 2;
		float precisionScaleFloat = scaleFloat * 2;
		int precisionScaleInteger = scaleInteger * 2;
		long precisionScaleLong = scaleLong * 2;
		short precisionScaleShort = (short) (scaleShort * 2);
		byte precisionScaleByte = (byte) (scaleByte * 2);
		BigDecimal precisionScaleBigDecimal = BigDecimal.valueOf(scaleBigDecimal.longValue() * 2);

		bean.setNoneDouble(noneDouble);
		bean.setNoneFloat(noneFloat);
		bean.setNoneInteger(noneInteger);
		bean.setNoneLong(noneLong);
		bean.setNoneShort(noneShort);
		bean.setNoneByte(noneByte);
		bean.setNoneBigDecimal(noneBigDecimal);

		bean.setPrecisionDouble(precisionDouble);
		bean.setPrecisionFloat(precisionFloat);
		bean.setPrecisionInteger(precisionInteger);
		bean.setPrecisionLong(precisionLong);
		bean.setPrecisionShort(precisionShort);
		bean.setPrecisionByte(precisionByte);
		bean.setPrecisionBigDecimal(precisionBigDecimal);

		bean.setScaleDouble(scaleDouble);
		bean.setScaleFloat(scaleFloat);
		bean.setScaleInteger(scaleInteger);
		bean.setScaleLong(scaleLong);
		bean.setScaleShort(scaleShort);
		bean.setScaleByte(scaleByte);
		bean.setScaleBigDecimal(scaleBigDecimal);

		bean.setPrecisionScaleDouble(precisionScaleDouble);
		bean.setPrecisionScaleFloat(precisionScaleFloat);
		bean.setPrecisionScaleInteger(precisionScaleInteger);
		bean.setPrecisionScaleLong(precisionScaleLong);
		bean.setPrecisionScaleShort(precisionScaleShort);
		bean.setPrecisionScaleByte(precisionScaleByte);
		bean.setPrecisionScaleBigDecimal(precisionScaleBigDecimal);
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
