/**
 * 
 */
package net.jirasystems.cheeporm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

/**
 * TODO why not make all these methods static
 * 
 * @author david
 */
public class Reflection {

	private Pattern pattern;
	private Matcher matcher;

	/**
	 * @param bean
	 *            The bean to inspect.
	 * @return The name of the table that this bean is associated with, or null if the bean has no
	 *         {@link Table} annotation.
	 */
	public String getTable(Object bean) {
		return getTable(bean.getClass());
	}

	/**
	 * @param beanClass
	 *            The bean to inspect.
	 * @return The name of the table that this bean is associated with, or null if the bean has no
	 *         {@link Table} annotation.
	 */
	public String getTable(Class<?> beanClass) {
		Table table = beanClass.getAnnotation(Table.class);
		if (table == null) {
			return null;
		}
		if (!table.name().equals("")) {
			return table.name();
		}
		return camelCaseToDatabase(beanClass.getSimpleName());
	}

	/**
	 * Get the {@link Field} from the given bean class which has the given name.
	 * 
	 * @param fieldName
	 *            the name of the field
	 * @param beanClass
	 *            the class to find it in
	 * @return the Field found (null if none is found)
	 */
	public Field getField(String fieldName, Class<?> beanClass) {

		Field result = null;

		// Attempt to return a cached value:
		List<Field> fieldList = getAllFields(beanClass);
		if (fieldList != null) {
			loop: for (Field field : fieldList) {
				if (StringUtils.equals(field.getName(), fieldName)) {
					result = field;
					break loop;
				}
			}
		}

		return result;
	}

	/**
	 * @param field
	 *            The field to inspect
	 * @return If this field has an {@link Id} annotation, true. Otherwise, false.
	 */
	public boolean isId(Field field) {

		// Look for the annotation
		Id id = field.getAnnotation(Id.class);

		// Return true if this field is an @Id
		return id != null;
	}

	/**
	 * @param field
	 *            The field to inspect
	 * @return If this field has a {@link Column} annotation, and it specifies nullable=true, true.
	 *         Otherwise, false.
	 */
	public boolean isNullable(Field field) {

		// Look for the annotation
		Column column = field.getAnnotation(Column.class);

		// Return true if this field is nullable
		if (column == null) {
			return false;
		}
		return column.nullable();
	}

	/**
	 * @param field
	 *            The field to inspect
	 * @return If this field has a {@link Column} annotation, the value of length. Otherwise, -1.
	 */
	public int getLength(Field field) {

		// Look for the annotation
		Column column = field.getAnnotation(Column.class);

		// Return the length for this column
		if (column == null) {
			return -1;
		}
		return column.length();
	}

	/**
	 * @param field
	 *            The field to inspect
	 * @return If this field has a {@link Column} annotation, the value of scale. Otherwise, -1.
	 */
	public int getScale(Field field) {

		// Look for the annotation
		Column column = field.getAnnotation(Column.class);

		// Return the length for this column
		if (column == null) {
			return -1;
		}
		return column.scale();
	}

	/**
	 * @param field
	 *            The field to inspect
	 * @return If this field has a {@link Column} annotation, the value of precision. Otherwise, -1.
	 */
	public int getPrecision(Field field) {

		// Look for the annotation
		Column column = field.getAnnotation(Column.class);

		// Return the length for this column
		if (column == null) {
			return -1;
		}
		return column.precision();
	}

	/**
	 * @param field
	 *            The field to inspect.
	 * @return The value of {@link Column#insertable()}.
	 */
	public boolean isInsertable(Field field) {

		// Look for the annotation
		Column column = field.getAnnotation(Column.class);

		// Return true if this field is an @Id
		return column.insertable();
	}

	/**
	 * @param field
	 *            The field to inspect.
	 * @return The value of {@link Column#updatable()}.
	 */
	public boolean isUpdateable(Field field) {

		// Look for the annotation
		Column column = field.getAnnotation(Column.class);

		// Return true if this field is an @Id
		return column.updatable();
	}

	/**
	 * @param field
	 *            The field to inspect
	 * @return The database column name for this field, or null if no {@link Column} annotation is
	 *         present.
	 */
	public String getColumnName(Field field) {

		// Look for the annotation
		Column column = field.getAnnotation(Column.class);

		// If there is no annotation, return null
		if (column == null) {
			return null;
		}

		// Return the name property from the annotation, if present
		String name = column.name();
		if (!name.equals("")) {
			return name;
		}

		// Work out a sensible name based on the field name:
		return camelCaseToDatabase(field.getName());
	}

	/**
	 * This method converts an identifier from Java camel-case (with initial capital or initial
	 * lowercase) to database style.
	 * 
	 * For example:
	 * <ul>
	 * <li>camelCaseIdentifier becomes camel_case_identifier</li>
	 * <li>MyClass becomes my_class</li>
	 * </ul>
	 * 
	 * @param camelCase
	 *            A Java-style camel-case identifier.
	 * @return A database-styled version of the identifier.
	 */
	public String camelCaseToDatabase(String camelCase) {

		// Lowercase the initial letter so that we can work out where to put
		// underscores by locating capital letters
		String identifier = "";
		if (camelCase.length() > 0) {
			identifier += camelCase.substring(0, 1).toLowerCase();
		}
		if (camelCase.length() > 1) {
			identifier += camelCase.substring(1);
		}

		// Set up the pattern and matcher
		if (pattern == null) {
			pattern = Pattern.compile("[A-Z]");
		}
		matcher = pattern.matcher(identifier);

		// Find the segments and compile a database identifier
		StringBuilder databaseName = new StringBuilder();
		int start = 0;
		while (matcher.find()) {
			if (databaseName.length() > 0) {
				databaseName.append("_");
			}
			int end = matcher.start();
			databaseName.append(identifier.substring(start, end).toLowerCase());
			start = end;
		}
		if (databaseName.length() > 0) {
			databaseName.append("_");
		}
		databaseName.append(identifier.substring(start).toLowerCase());

		return databaseName.toString();
	}

	private Map<Class<?>, List<Field>> typeCache = new ConcurrentHashMap<Class<?>, List<Field>>();

	/**
	 * This method returns a list of all declared fields that have an {@link Id} or {@link Column}
	 * annotation, from the bean class and all super classes, excluding Object (as no annotations
	 * can have been added to Object).
	 * 
	 * The list of fields is cached, so subsequent calls to this method do not require reflection
	 * calls. This is reasonable, as a class is unlikely to change at runtime.
	 * 
	 * NB: the returned instance is the same instance which is stored in the cache. Should it be
	 * necessary to make alterations to the list, this can be done on the returned instance and all
	 * other references and subsequent calls to this method will see the changes.
	 * 
	 * @param beanClass
	 *            The bean class to be inspected.
	 * @return A list of all fields in the type hierarchy of the given bean.
	 */
	private List<Field> getAllFields(Class<?> beanClass) {

		// Attempt to return a cached value:
		List<Field> result = typeCache.get(beanClass);
		if (result != null) {
			return result;
		}

		// We don't have a cached value, so compute the list of fields.

		// Recursively collect declared fields, starting with the class of the bean:
		Class<?> hierarchyClass = beanClass;
		result = new ArrayList<Field>();
		do {
			// List all fields declared by this type
			Field[] fields = hierarchyClass.getDeclaredFields();

			// Add those fields that have an Id or Column annotation to the result
			for (Field field : fields) {

				Column column = field.getAnnotation(Column.class);
				// TODO: All columns should be marked with @Column. 
				// @Id should never appear without @Column.
				// Id id = field.getAnnotation(Id.class);

				if (column != null) {
					//|| (id != null)) {
					result.add(field);
				}
			}

			// Recurse up the class hierachy:
			hierarchyClass = hierarchyClass.getSuperclass();

		} while (!hierarchyClass.equals(Object.class));

		// Cache the result:
		typeCache.put(beanClass, Collections.unmodifiableList(result));

		return result;
	}

	/**
	 * Gets all non-ID fields. That is all fields marked with {@link Column} which are not also
	 * marked with {@link Id}.
	 * 
	 * @param bean
	 *            The bean whose fields should be listed.
	 * @return A list of all the fields which are marked with {@link Column} but not with {@link Id}
	 *         .
	 */
	public List<Field> listFields(Object bean) {

		return listFields(bean.getClass());
	}

	/**
	 * Gets all non-ID fields. That is all fields marked with {@link Column} which are not also
	 * marked with {@link Id}.
	 * 
	 * @param beanClass
	 *            The bean class whose fields should be listed.
	 * @return A list of all the fields which are marked with {@link Column} but not with {@link Id}
	 *         .
	 */
	public List<Field> listFields(Class<?> beanClass) {

		List<Field> result = new ArrayList<Field>();
		List<Field> fields = getAllFields(beanClass);

		for (Field field : fields) {
			// Check whether the field is a column and not an Id
			if (getColumnName(field) != null) {
				if (!isId(field)) {
					result.add(field);
				}
			}
		}

		return result;
	}

	/**
	 * @param bean
	 *            The bean whose fields should be listed.
	 * @return A list of all the fields which are marked with {@link Column} and have
	 *         {@link Column#insertable()}=true.
	 */
	public List<Field> listInsertFields(Object bean) {

		List<Field> result = new ArrayList<Field>();
		List<Field> fields = getAllFields(bean.getClass());

		for (Field field : fields) {
			// Check whether the field is a column and not an Id
			if (getColumnName(field) != null) {
				if (!isId(field)) {
					if (isInsertable(field)) {
						result.add(field);
					}
				}
			}
		}

		return result;
	}

	/**
	 * @param bean
	 *            The bean whose fields should be listed.
	 * @return A list of all the fields which are marked with {@link Column} and have
	 *         {@link Column#updatable()}=true.
	 */
	public List<Field> listUpdateFields(Object bean) {

		List<Field> result = new ArrayList<Field>();
		List<Field> fields = getAllFields(bean.getClass());

		for (Field field : fields) {
			// Check whether the field is a column and not an Id
			if (getColumnName(field) != null) {
				if (!isId(field)) {
					if (isUpdateable(field)) {
						result.add(field);
					}
				}
			}
		}

		return result;
	}

	/**
	 * List all fields marked with {@link Id}.
	 * 
	 * @param bean
	 *            The bean to be examined
	 * @return Those fields of the bean marked with {@link Id}.
	 */
	public List<Field> listKeys(Object bean) {

		return listKeys(bean.getClass());
	}

	/**
	 * List all fields marked with {@link Id}.
	 * 
	 * @param beanClass
	 *            The bean class to be examined
	 * @return Those fields of the bean marked with {@link Id}.
	 */
	public List<Field> listKeys(Class<?> beanClass) {

		List<Field> result = new ArrayList<Field>();
		List<Field> fields = getAllFields(beanClass);

		for (Field field : fields) {
			// Check whether the field is an ID
			if (isId(field)) {
				result.add(field);
			}
		}

		return result;
	}

	/**
	 * List all fields marked with {@link Id} that are {@link Column#insertable()}=true.
	 * 
	 * @param bean
	 *            The bean to be examined
	 * @return Those fields of the bean marked with {@link Id} and have {@link Column#insertable()}
	 *         =true.
	 */
	public List<Field> listInsertKeys(Object bean) {

		List<Field> result = new ArrayList<Field>();
		List<Field> fields = getAllFields(bean.getClass());

		for (Field field : fields) {
			// Check whether the field is an ID
			if (isId(field) && isInsertable(field)) {
				result.add(field);
			}
		}

		return result;
	}

	/**
	 * List all fields in the bean which have a column annotation.
	 * 
	 * @param bean
	 *            the bean to be examined
	 * @return all fields including id fields
	 */
	public List<Field> listAll(Object bean) {

		return listAll(bean.getClass());
	}

	/**
	 * List all fields in the bean which have a column annotation.
	 * 
	 * @param beanClass
	 *            the bean to be examined
	 * @return all fields including id fields
	 */
	public List<Field> listAll(Class<?> beanClass) {
		List<Field> result = new ArrayList<Field>();
		List<Field> fields = getAllFields(beanClass);

		// TODO: do we really need to do this?
		for (Field field : fields) {
			if (getColumnName(field) != null) {
				result.add(field);
			}
		}

		return result;
	}

	/**
	 * Gets an instance of the given type by instantiating the default constructor.
	 * <p>
	 * If the default constructor is private/protected then the bailiffs are sent over to open the
	 * door
	 * 
	 * @param <B>
	 *            the bean's type
	 * @param bean
	 *            the bean
	 * @return a new instance of the bean
	 */
	public <B> B newInstance(B bean) {

		@SuppressWarnings("unchecked")
		Class<B> beanClass = (Class<B>) bean.getClass();
		return newInstance(beanClass);
	}

	/**
	 * Gets an instance of the given type by instantiating the default constructor.
	 * <p>
	 * If the default constructor is private/protected then the bailiffs are sent over to open the
	 * door
	 * 
	 * @param <B>
	 *            the bean's type
	 * @param beanClass
	 *            the bean
	 * @return a new instance of the bean
	 */
	public <B> B newInstance(Class<B> beanClass) {

		B instance;
		try {
			instance = beanClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("Unable to instantiate bean class", e);
		} catch (IllegalAccessException e) {
			try {
				// looks like a private constructor... ATTACK!!
				Constructor<B> constructor = beanClass.getDeclaredConstructor();
				constructor.setAccessible(true);
				instance = constructor.newInstance();
				constructor.setAccessible(false);
			} catch (SecurityException e1) {
				throw new RuntimeException("Unable to instantiate bean class", e);
			} catch (NoSuchMethodException e1) {
				throw new RuntimeException("Unable to instantiate bean class", e);
			} catch (IllegalArgumentException e1) {
				throw new RuntimeException("Unable to instantiate bean class", e);
			} catch (InstantiationException e1) {
				throw new RuntimeException("Unable to instantiate bean class", e);
			} catch (IllegalAccessException e1) {
				throw new RuntimeException("Unable to instantiate bean class", e);
			} catch (InvocationTargetException e1) {
				throw new RuntimeException("Unable to instantiate bean class", e);
			}
		}
		return instance;
	}

	/**
	 * @param field
	 *            The field to read.
	 * @param bean
	 *            The bean from which to read the field.
	 * @return The value of the given field.
	 */
	public Object getFieldValue(Field field, Object bean) {

		// Disable access-checking;
		field.setAccessible(true);
		try {
			return field.get(bean);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unable to access field " + field.getName(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to access field " + field.getName(), e);
		} finally {
			// Re-enable access-checking
			// NB this won't make a public field inaccessible,
			// it just enables security checks:
			field.setAccessible(false);
		}
	}

	/**
	 * @param field
	 *            The field to read.
	 * @return The type of the given field.
	 */
	public Class<?> getFieldType(Field field) {

		field.setAccessible(true);
		try {
			return field.getType();
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unable to access field " + field.getName(), e);
		} finally {
			// Be polite and set it back again
			field.setAccessible(false);
		}
	}

	/**
	 * @param field
	 *            The field to be set.
	 * @param bean
	 *            The bean on which to set the field.
	 * @param value
	 *            The value to be set.
	 */
	public void setFieldValue(Field field, Object bean, Object value) {

		boolean current = field.isAccessible();
		field.setAccessible(true);
		try {
			if (Enum.class.isAssignableFrom(field.getType())) {
				setEnum(field, bean, (String) value);
			} else {
				field.set(bean, value);
			}
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to access field " + field.getName(), e);
		} finally {
			// Be polite and set it back again
			field.setAccessible(current);
		}
	}

	/**
	 * Sets an enum field.
	 * 
	 * @param field
	 *            The field to be set.
	 * @param bean
	 *            The instance in which the field is to be set.
	 * @param value
	 *            The value that the field is to be set to.
	 */
	private void setEnum(Field field, Object bean, String value) {
		try {
			Object enumValue = null;
			if (value != null) {
				try {
					enumValue = field.getType().getMethod("valueOf", String.class).invoke(field.getType(), value);
				} catch (Exception e) {
					// Compare the values returned by the toString method as this may have been
					// overridden
					Object[] possibleValues = (Object[]) field.getType().getMethod("values").invoke(field.getType());
					loop: for (Object possibleValue : possibleValues) {
						if (value.equals(possibleValue.toString())) {
							enumValue = possibleValue;
							break loop;
						}
					}

					if (enumValue == null) {
						throw e;
					}
				}
			}
			field.set(bean, enumValue);
			return;
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to set field of type " + field.getType().getSimpleName()
					+ " with value " + value, e);
		}
	}
}
