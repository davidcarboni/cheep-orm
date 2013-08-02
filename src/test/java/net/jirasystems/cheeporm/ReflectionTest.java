package net.jirasystems.cheeporm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import junit.framework.Assert;
import net.jirasystems.cheeporm.beans.BeanLength;
import net.jirasystems.cheeporm.beans.BeanMinimal;
import net.jirasystems.cheeporm.beans.BeanNonField;
import net.jirasystems.cheeporm.beans.BeanNullable;
import net.jirasystems.cheeporm.beans.BeanPrecisionScale;
import net.jirasystems.cheeporm.beans.BeanWithTableName;

import org.junit.Test;

/**
 * Test for {@link Reflection}.
 * 
 * @author David Carboni
 * 
 */
public class ReflectionTest {

	private Reflection reflection = new Reflection();

	/**
	 * Test for {@link Reflection#getTable(Object)} where the bean does not have a {@link Table}
	 * annotation.
	 */
	@Test
	public void testGetTableNoAnnotation() {

		// Given
		Object bean = new Object();

		// When
		String tableName = reflection.getTable(bean);

		// Then
		Assert.assertNull(tableName);
	}

	/**
	 * Test for {@link Reflection#getTable(Object)} where the bean does not have a name specified in
	 * the {@link Table} annotation.
	 */
	@Test
	public void testGetTableNoExplicitName() {

		// Given
		Object bean = new BeanMinimal();
		String expected = reflection.camelCaseToDatabase(bean.getClass().getSimpleName());

		// When
		String tableName = reflection.getTable(bean);

		// Then
		Assert.assertEquals(expected, tableName);
	}

	/**
	 * Test for {@link Reflection#getTable(Object)} where the bean has a name explicitly specified
	 * in the {@link Table} annotation.
	 */
	@Test
	public void testGetTableExplicitName() {

		// Given
		Object bean = new BeanWithTableName();
		String expected = bean.getClass().getAnnotation(Table.class).name();

		// When
		String tableName = reflection.getTable(bean);

		// Then
		Assert.assertEquals(expected, tableName);
	}

	/**
	 * Test for {@link Reflection#listAll(Object)}.
	 */
	@Test
	public void testListAll() {

		// Given
		Object bean = new BeanMinimal();
		List<Field> fields = Arrays.asList(bean.getClass().getDeclaredFields());
		List<Field> expected = new ArrayList<Field>();
		for (Field field : fields) {
			// Filter out Emma code coverage field:
			if ((!field.getName().equals("$VRc")) && (!field.getName().equals("serialVersionUID"))) {
				expected.add(field);
			}
			// System.out.println(" *** " + field.getName());
		}

		// When
		List<Field> actual = reflection.listAll(bean);

		// Then
		Assert.assertEquals(expected.size(), actual.size());
		for (Field field : expected) {
			Assert.assertTrue(actual.contains(field));
		}
		for (Field field : actual) {
			Assert.assertTrue(expected.contains(field));
		}
	}

	/**
	 * Test for {@link Reflection#listAll(Object)}.
	 */
	@Test
	public void testListAllNonField() {

		// Given
		Object bean = new BeanNonField();
		List<Field> fields = Arrays.asList(bean.getClass().getDeclaredFields());
		List<Field> expected = new ArrayList<Field>();
		for (Field field : fields) {
			// Filter out Emma code coverage field:
			if ((!field.getName().equals("$VRc")) && (!field.getName().equals("serialVersionUID"))) {

				// Only add fields that are annotated as columns:
				if (field.getAnnotation(Column.class) != null) {
					expected.add(field);
				}
			}
			// System.out.println(" *** " + field.getName());
		}

		// When
		List<Field> actual = reflection.listAll(bean);

		// Then
		Assert.assertEquals(expected.size(), actual.size());
		for (Field field : expected) {
			Assert.assertTrue(actual.contains(field));
		}
		for (Field field : actual) {
			Assert.assertTrue(expected.contains(field));
		}
	}

	/**
	 * Test for {@link Reflection#isNullable(Field)}.
	 * 
	 * @throws NoSuchFieldException
	 *             If an error occurs in getting a declared field of {@link BeanNullable}.
	 */
	@Test
	public void testIsNullable() throws NoSuchFieldException {

		// Given
		Field nullableField = BeanNullable.class.getDeclaredField("nullableField");
		Field nonNullableField = BeanNullable.class.getDeclaredField("nonNullableField");

		// When
		boolean nullable = reflection.isNullable(nullableField);
		boolean nonNullable = reflection.isNullable(nonNullableField);

		// Then
		Assert.assertTrue(nullable);
		Assert.assertFalse(nonNullable);
	}

	/**
	 * Test for {@link Reflection#getLength(Field)}.
	 * 
	 * @throws NoSuchFieldException
	 *             If an error occurs in getting a declared field of {@link BeanLength}.
	 */
	@Test
	public void testGetLength() throws NoSuchFieldException {

		// Given

		final int definedLength = BeanLength.class.getDeclaredField("lengthLimited").getAnnotation(Column.class)
				.length();
		final int defaultLength = 255;
		final int nonColumnLength = -1;

		Field lengthLimitedField = BeanLength.class.getDeclaredField("lengthLimited");
		Field lengthDefaultField = BeanLength.class.getDeclaredField("lengthDefault");
		Field lengthNonColumnField = BeanLength.class.getDeclaredField("lengthNotColumn");

		// When
		int lengthLimited = reflection.getLength(lengthLimitedField);
		int lengthDefault = reflection.getLength(lengthDefaultField);
		int lengthNonColumn = reflection.getLength(lengthNonColumnField);

		// Then
		Assert.assertEquals(definedLength, lengthLimited);
		Assert.assertEquals(defaultLength, lengthDefault);
		Assert.assertEquals(nonColumnLength, lengthNonColumn);
	}

	/**
	 * Test for {@link Reflection#getScale(Field)}.
	 * 
	 * @throws NoSuchFieldException
	 *             If an error occurs in getting a declared field of {@link BeanPrecisionScale} or
	 *             {@link BeanNonField}.
	 */
	@Test
	public void testGetScale() throws NoSuchFieldException {

		final int definedScale = BeanPrecisionScale.class.getDeclaredField("scaleDouble").getAnnotation(Column.class)
				.scale();
		final int defaultScale = 0;
		final int nonColumnScale = -1;

		Field scaleSpecifiedField = BeanPrecisionScale.class.getDeclaredField("scaleDouble");
		Field scaleDefaultField = BeanPrecisionScale.class.getDeclaredField("noneDouble");
		Field scaleNonColumnField = BeanNonField.class.getDeclaredField("nonColumn");

		// When
		int scaleDefined = reflection.getScale(scaleSpecifiedField);
		int scaleDefault = reflection.getScale(scaleDefaultField);
		int scaleNonColumn = reflection.getScale(scaleNonColumnField);

		// Then
		Assert.assertEquals(definedScale, scaleDefined);
		Assert.assertEquals(defaultScale, scaleDefault);
		Assert.assertEquals(nonColumnScale, scaleNonColumn);
	}

	/**
	 * Test for {@link Reflection#getPrecision(Field)}.
	 * 
	 * @throws NoSuchFieldException
	 *             If an error occurs in getting a declared field of {@link BeanPrecisionScale} or
	 *             {@link BeanNonField}.
	 */
	@Test
	public void testGetPrecision() throws NoSuchFieldException {

		final int definedPrecision = BeanPrecisionScale.class.getDeclaredField("precisionDouble")
				.getAnnotation(Column.class).precision();
		final int defaultPrecision = 0;
		final int nonColumnPrecision = -1;

		Field precisionSpecifiedField = BeanPrecisionScale.class.getDeclaredField("precisionDouble");
		Field precisionDefaultField = BeanPrecisionScale.class.getDeclaredField("noneDouble");
		Field precisionNonColumnField = BeanNonField.class.getDeclaredField("nonColumn");

		// When
		int precisionDefined = reflection.getPrecision(precisionSpecifiedField);
		int precisionDefault = reflection.getPrecision(precisionDefaultField);
		int precisionNonColumn = reflection.getPrecision(precisionNonColumnField);

		// Then
		Assert.assertEquals(definedPrecision, precisionDefined);
		Assert.assertEquals(defaultPrecision, precisionDefault);
		Assert.assertEquals(nonColumnPrecision, precisionNonColumn);
	}

	// @Test
	// public void testGetColumnName() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testCamelCaseToDatabase() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testListFields() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testListInsertFields() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testListUpdateFields() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testListKeys() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testListAll() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testNewInstance() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetFieldValue() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetFieldValue() {
	// fail("Not yet implemented");
	// }

}
