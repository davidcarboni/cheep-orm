package net.jirasystems.cheeporm.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

/**
 * This class is designed to generically verify getters and setters for bean classes, to ensure that
 * they are getting and setting the correct values in the bean.
 * 
 * @author David Carboni
 * 
 */
public class BeanTester {

	private List<Class<?>> supportedTypes;

	/**
	 * Tests all of the primitive, primitive-wrapper and enum getters and setters of the given bean
	 * instance.
	 * 
	 * @param instance
	 *            The instance to be tested.
	 * 
	 * @throws Exception .
	 */
	public void testBean(Object instance) throws Exception {

		List<Field> fields = getFields(instance);
		StringBuilder message = new StringBuilder();
		boolean alert = false;
		message.append("Testing " + instance.getClass().getSimpleName() + "\n");

		for (Field field : fields) {

			field.setAccessible(true);

			Method get = getGetter(field, instance);
			Method set = getSetter(field, instance);

			boolean getter = testGet(field, get, instance);
			boolean setter = testSet(field, set, instance);

			if (!(getter && setter)) {

				// These fields seem to be added to all objects by Emma code coverage, so we're not
				// interested in knowing about them.
				if (!(field.getName().equals("serialVersionUID") || field.getName().equals("$VRc"))) {
					alert = true;
					message.append(" - testing field: " + field.getName() + "\n");
					if (!getter) {
						message.append("   - no getter\n");
					}
					if (!setter) {
						message.append("   - no setter\n");
					}
				}
			}
		}

		if (alert) {
			System.out.println(message);
		}
	}

	/**
	 * Tests the given getter method.
	 * 
	 * @param field
	 *            The field to test the getter for.
	 * @param get
	 *            The getter method.
	 * @param instance
	 *            The instance to test on.
	 * @return If the getter was successfully tested, true. Otherwise false.
	 * @throws Exception .
	 */
	private boolean testGet(Field field, Method get, Object instance) throws Exception {

		// Only test the getter if one was found:
		if (get != null) {

			final int trials = 5;
			for (int i = 0; i < trials; i++) {
				Object value = ValueGenerator.valueExample(field.getType(), i);
				field.set(instance, value);
				Object returned = get.invoke(instance);
				Assert.assertEquals(value, returned);
			}
			return true;

		}
		return false;
	}

	/**
	 * Tests the given setter method.
	 * 
	 * @param field
	 *            The field to test the setter for.
	 * @param set
	 *            The setter method.
	 * @param instance
	 *            The instance to test on.
	 * @return If the setter was successfully tested, true. Otherwise false.
	 * @throws Exception .
	 */
	private boolean testSet(Field field, Method set, Object instance) throws Exception {

		// Only test the setter if one was found:
		if (set != null) {

			final int trials = 5;
			for (int i = 0; i < trials; i++) {
				Object value = ValueGenerator.valueExample(field.getType(), i);
				set.invoke(instance, value);
				Object returned = field.get(instance);
				Assert.assertEquals(value, returned);
			}
			return true;

		}
		return false;
	}

	/**
	 * @param instance
	 *            The instance whose class is to be inspected.
	 * @return The list of declared fields for this instance.
	 */
	public List<Field> getFields(Object instance) {

		Field[] fields = instance.getClass().getDeclaredFields();
		List<Field> results = new ArrayList<Field>();

		// Check each field
		for (Field field : fields) {
			// Check each supported type
			for (Class<?> type : getSupportedTypes()) {
				// If there is a match, and this is not a final field, add this field to the return
				// list. NB we check whether the supported type can be assigned from the field type,
				// rather than the other way around, so that this can generically handle any type of
				// enum:
				if ((type.isAssignableFrom(field.getType())) && (!Modifier.isFinal(field.getModifiers()))) {
					results.add(field);
				}
			}
		}
		return results;
	}

	/**
	 * Finds a getter for the given field, based on String manipulation of the Field name.
	 * 
	 * @param field
	 *            The field to find a getter for.
	 * @param instance
	 *            The instance whose class will be examined.
	 * @return The getMethod or isMethod to get the value of this field.
	 */
	public Method getGetter(Field field, Object instance) {

		// Look for a standard "getMethod"
		String prefix = "get";
		String name = accessorName(prefix, field);
		Method method = getMethod(name, instance);
		if (method != null) {
			return method;
		}

		// Look for a boolean "isMethod"
		prefix = "is";
		name = accessorName(prefix, field);
		method = getMethod(name, instance);
		return method;
	}

	/**
	 * Finds a setter for the given field, based on String manipulation of the Field name.
	 * 
	 * @param field
	 *            The field to find a setter for.
	 * @param instance
	 *            The instance whose class will be examined.
	 * @return The setMethod to set the value of this field.
	 */
	public Method getSetter(Field field, Object instance) {

		// Look for a standard "setMethod"
		String prefix = "set";
		String getName = accessorName(prefix, field);
		Method getMethod = getMethod(getName, instance, field.getType());
		return getMethod;
	}

	/**
	 * @param name
	 *            The name of the method.
	 * @param instance
	 *            An object instance, whose class will be queried for the specified method.
	 * @param parameters
	 *            The method parameters, if any.
	 * @return The method of the given name for the instance class, or null if the method either
	 *         does not exist or cannot be accessed.
	 */
	public Method getMethod(String name, Object instance, Class<?>... parameters) {
		int i = 0;
		try {
			return instance.getClass().getMethod(name, parameters);
		} catch (SecurityException e) {
			i++; // Prevents a Checkstyle warning for an empty block.
		} catch (NoSuchMethodException e) {
			i++; // Prevents a Checkstyle warning for an empty block.
		}
		return null;
	}

	/**
	 * Generates a possible accessor name for the given field, using the given prefix (e.g. "get",
	 * "set", "is").
	 * 
	 * @param prefix
	 *            The accessor prefix.
	 * @param field
	 *            The field for which to generate an accessor name.
	 * @return The candidate accessor name.
	 */
	public String accessorName(String prefix, Field field) {

		String fieldName = field.getName();
		String accessorName = prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

		return accessorName;
	}

	/**
	 * @return The list of field types that are supported for bean testing.
	 */
	private List<Class<?>> getSupportedTypes() {

		// Return a cached copy if available
		if (supportedTypes == null) {

			supportedTypes = new ArrayList<Class<?>>();

			// Build up a list of supported types
			supportedTypes.add(String.class);
			supportedTypes.add(char.class);
			supportedTypes.add(Character.class);
			supportedTypes.add(int.class);
			supportedTypes.add(Integer.class);
			supportedTypes.add(long.class);
			supportedTypes.add(Long.class);
			supportedTypes.add(short.class);
			supportedTypes.add(Short.class);
			supportedTypes.add(byte.class);
			supportedTypes.add(Byte.class);
			supportedTypes.add(double.class);
			supportedTypes.add(Double.class);
			supportedTypes.add(float.class);
			supportedTypes.add(Float.class);
			supportedTypes.add(boolean.class);
			supportedTypes.add(Boolean.class);
			supportedTypes.add(Enum.class);
			supportedTypes.add(Timestamp.class);
		}

		return supportedTypes;
	}

}
