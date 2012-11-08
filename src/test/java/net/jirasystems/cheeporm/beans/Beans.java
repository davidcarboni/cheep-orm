/**
 * 
 */
package net.jirasystems.cheeporm.beans;

import java.lang.reflect.Field;
import java.util.List;

import junit.framework.Assert;
import net.jirasystems.cheeporm.Reflection;

/**
 * @author david
 * 
 */
public final class Beans {

	/**
	 * No need to instantiate.
	 */
	private Beans() {
		// No need to instantiate.
	}

	// public Class<?>[] beans = { BeanColumnDefinition.class,
	// BeanEnumeration.class, BeanInsertable.class, BeanMultipleId.class,
	// BeanNonField.class, BeanNonMappableField.class, BeanNullable.class,
	// BeanPrecisionScale.class, BeanTypes.class, BeanUnique.class,
	// BeanUpdatable.class, BeanWithFieldName.class, BeanWithoutId.class,
	// BeanWithTableName.class, ExampleBean.class };

	private static Reflection reflection = new Reflection();

	/**
	 * This method compares two beans. It checks that they have the same number of fields, that they
	 * both have the same fields and that the values in each of those fields are equal, as
	 * determined by the equals method.
	 * 
	 * @param bean1
	 *            The first bean for comparison.
	 * @param bean2
	 *            The bean with which the first bean will be compared.
	 */
	public static void compareBeans(Object bean1, Object bean2) {

		// Check the types
		Assert.assertEquals(bean1.getClass(), bean2.getClass());

		List<Field> fields1 = reflection.listFields(bean1);
		List<Field> fields2 = reflection.listFields(bean2);

		// Check the sizes as a heuristic - very odd if this fails!
		Assert.assertEquals(fields1.size(), fields2.size());

		// Check that each list contains the same fields as the other - very odd
		// if this fails!
		for (Field field : fields1) {
			Assert.assertTrue(fields2.contains(field));
		}
		for (Field field : fields2) {
			Assert.assertTrue(fields1.contains(field));
		}

		// Now we are sure that we have the same types and fields,
		// check the values:
		for (Field field : fields1) {
			Object value1 = reflection.getFieldValue(field, bean1);
			Object value2 = reflection.getFieldValue(field, bean2);
			String valueType;
			if (value1 != null) {
				valueType = value1.getClass().getSimpleName();
			} else {
				valueType = null;
			}
			if (valueType == null) {
				if (value2 != null) {
					valueType = value2.getClass().getSimpleName();
				} else {
					valueType = "null";
				}
			}
			Assert.assertEquals("Values of type " + valueType + " are not equal: " + value1 + "/" + value2, value1,
					value2);
		}
	}
}
