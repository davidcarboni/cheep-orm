/**
 * 
 */
package net.jirasystems.cheeporm;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

/**
 * This class generates a basic SQL create statement for a table that could hold the given bean. The
 * statement makes use of the available parameters on the {@link Column} annotation, including the
 * <code>columndefinition</code>, which allows a specific SQL column definition to be set.
 * 
 * Note that the current default value for <code>length</code> in {@link Column} is 255, so by
 * default, all String values will be of length 255.
 * 
 * It is not necessary to specify the column name in the {@link Column} annotation. If it is not
 * specified, the Java field name will be converted into a database-style identifier by inserting
 * underscores before each capital letter and lowercasing the result.
 * 
 * @see <a href="http://java.sun.com/javaee/5/docs/api/javax/persistence/Column.html# length()">Sun
 *      JavaDoc</a>
 * 
 * @author david
 * 
 */
public class CreateTable {

	private Reflection reflection;
	// private Sql sql;
	private Jdbc jdbc;

	/**
	 * The default constructor creates an internal {@link Reflection} instance.
	 */
	public CreateTable() {
		reflection = new Reflection();
		// sql = new Sql();
	}

	/**
	 * Generates a create statement that could create a table to hold the given bean.
	 * 
	 * @param bean
	 *            The bean for which a table create statement is to be generated. Default values for
	 *            the table can be provided by setting the fields of this instance.
	 * @return A SQL "create table" statement.
	 */
	public String createStatement(Object bean) {

		boolean autoIncrementSpecified = false;

		String table = reflection.getTable(bean);
		List<Field> fields = reflection.listFields(bean);
		List<Field> keys = reflection.listKeys(bean);

		StringBuilder createStatement = new StringBuilder();
		createStatement.append("create table ");
		createStatement.append(table);
		createStatement.append(" (\n\n");

		// Key fields
		for (Field key : keys) {

			createStatement.append("  ");
			createStatement.append(columnDefinition(key, bean, Boolean.TRUE));
			if (!autoIncrementSpecified) {
				// Generate the first key as an autoincrement column
				createStatement.append(" auto_increment");
				autoIncrementSpecified = true;
			}
			createStatement.append(",\n");
		}

		// Data fields
		for (Field field : fields) {

			createStatement.append("  ");
			createStatement.append(columnDefinition(field, bean));
			createStatement.append(",\n");
		}

		// Primary key
		createStatement.append("\n");
		createStatement.append(primaryKey(keys));

		createStatement.append("\n)");

		return createStatement.toString();
	}

	/**
	 * Creates a suitable table for the given bean, using the provided connection.
	 * 
	 * @param bean
	 *            The bean for which a table is to be created.
	 * @param connection
	 *            A JDBC {@link Connection} with sufficient privileges to create the table.
	 * @throws SQLException
	 *             If the {@link Connection} or the {@link PreparedStatement} used to create the
	 *             table throw an exception.
	 */
	public void createTable(Object bean, Connection connection) throws SQLException {

		// Generate a create statement
		String sql = createStatement(bean);

		createTable(sql, bean, connection);
	}

	/**
	 * Creates a suitable table for the given bean, using the provided SQL and connection.
	 * 
	 * This version of the method is useful if you want to use {@link #createStatement(Object)} as
	 * the basis of the SQL, but want to perform post-processing on the SQL before creating the
	 * table, such as adding "if not exists", for example.
	 * 
	 * @param sql
	 *            A create table statement, optionally with parameter placeholders for default
	 *            values.
	 * @param bean
	 *            The bean for which a table is to be created, containing any default values needed
	 *            for parameter placeholders in the create statement. If null is passed, no
	 *            parameters will be set in the {@link PreparedStatement}.
	 * @param connection
	 *            A JDBC {@link Connection} with sufficient privileges to create the table.
	 * @throws SQLException
	 *             If the {@link Connection} or the {@link PreparedStatement} used to create the
	 *             table throw an exception.
	 */
	public void createTable(String sql, Object bean, Connection connection) throws SQLException {

		// System.out.println("Creating table:");
		// System.out.println(sql);
		PreparedStatement preparedStatement = connection.prepareStatement(sql);

		// Set the parameters where there are default parameters
		jdbc = new Jdbc(connection);
		if (bean != null) {
			List<Field> defaultFields = getDefaultFields(bean);
			jdbc.setParameters(preparedStatement, defaultFields, bean);
		}

		// Run the table creation
		try {
			preparedStatement.execute();
		} finally {
			preparedStatement.close();
		}
	}

	/**
	 * Gets the list of fields from the given bean which have default valuess set in this instance.
	 * 
	 * @param bean
	 *            The bean to be checked.
	 * @return A list of the fields that contain default values.
	 */
	private List<Field> getDefaultFields(Object bean) {

		List<Field> fields = reflection.listFields(bean);
		List<Field> keys = reflection.listKeys(bean);
		List<Field> defaultFields = new ArrayList<Field>();

		// Key fields
		for (Field key : keys) {
			if (defaultField(key, bean)) {
				defaultFields.add(key);
			}
		}

		// Data fields
		for (Field field : fields) {
			if (defaultField(field, bean)) {
				defaultFields.add(field);
			}
		}

		return defaultFields;
	}

	/**
	 * @param field
	 *            The field to be checked for a default value.
	 * @param bean
	 *            The bean to be inspected.
	 * @return If there is a default value parameter for the given field, true, false otherwise.
	 */
	private boolean defaultField(Field field, Object bean) {

		// Get the column annotation
		Column column = field.getAnnotation(Column.class);
		String definition = column.columnDefinition();

		// If an explicit column definition was provided, there will be no
		// default value parameter to set
		if (!"".equals(definition)) {
			return false;
		}

		// Check for a default value in the bean
		Object value = reflection.getFieldValue(field, bean);

		// If there is a value, return true, if not return false
		return value != null;
	}

	/**
	 * Generates the primary key statement.
	 * 
	 * @param keys
	 *            The list of keys to be used for the primary key.
	 * @return A string containing "primary key (...)".
	 */
	private String primaryKey(List<Field> keys) {
		StringBuilder primaryKey = new StringBuilder();

		for (Field key : keys) {
			if (primaryKey.length() > 0) {
				primaryKey.append(", ");
			}
			primaryKey.append(key.getName());
		}

		if (primaryKey.length() > 0) {
			return "primary key(" + primaryKey + ")";
		}

		return "";
	}

	/**
	 * Generates a column definition for the given field. This is equivalent to calling
	 * {@link #columnDefinition(Field, Boolean)} and passing null for the <code>notNullKey</code>
	 * parameter.
	 * 
	 * @param field
	 *            The field for which a column definition is to be generated.
	 * @param bean
	 *            An instance of the bean, which may contain defaults for the table columns.
	 * @return A column definition, based on the parameters of the {@link Column} annotation, or
	 *         default values.
	 */
	private String columnDefinition(Field field, Object bean) {

		return columnDefinition(field, bean, null);
	}

	/**
	 * Generates a column definition for the given field. This is equivalent to calling
	 * {@link #columnDefinition(Field, Boolean)} and passing {@link Boolean#FALSE}.
	 * 
	 * NB {@link Column} specifies 255 as the default for {@link Column#length()}. String fields
	 * (and therefore enums) must specify a length in SQL, so these will default to 255. For all
	 * other types, length will only be processed if no precision/scale has been specified and if
	 * the length is not 255. This means you can't explicitly specify 255 as a length for non
	 * String/enum types.
	 * 
	 * @param field
	 *            The field for which a column definition is to be generated.
	 * @param bean
	 *            An instance of the bean, which may contain defaults for the table columns.
	 * @param notNullKey
	 *            If this parameter is null, it will have no effect. If it is not null, its value
	 *            will be used to determine whether this field can have a null value. This parameter
	 *            is overridden if the {@link Column} annotation of the field specifies
	 *            <code>nullable</code> as false.
	 * @return A column definition, based on the parameters of the {@link Column} annotation, or
	 *         default values.
	 */
	private String columnDefinition(Field field, Object bean, Boolean notNullKey) {

		// Get the column annotation
		Column column = field.getAnnotation(Column.class);
		String name = column.name();
		if ("".equals(name)) {
			name = reflection.camelCaseToDatabase(field.getName());
		}

		// If an explicit column definition was provided, use it as provided.
		String definition = column.columnDefinition();
		if (!"".equals(definition)) {
			return name + " " + definition;
		}

		// If no explicit definition was present, build one up.

		// Get the details of the field:
		SqlType sqlType = SqlType.toSqlMap.get(field.getType());
		if (sqlType == null) {
			// Check for an enum type
			if (Enum.class.isAssignableFrom(field.getType())) {
				sqlType = SqlType.NVARCHAR;
			} else {
				throw new IllegalArgumentException("Field of type " + field.getType()
						+ " could not be mapped to a SQL type. " + "Note that primitives are not supported, "
						+ "you must use primitive wrappers (e.g. Integer) " + "for the declared field type.");
			}
		}

		// This special case prevents MySQL from creating a TIMESTAMP column, which will auto-update on every update of a row:
		String sqlTypeName;
		if (sqlType == SqlType.TIMESTAMP) {
			sqlTypeName = "DATETIME";
		} else {
			sqlTypeName = sqlType.name();
		}

		int scale = column.scale();
		int precision = column.precision();
		int length = column.length();

		// Work out whether this column can be null:

		// Assume the column can be null
		boolean notNull = false;
		// If the parameter was passed, use it.
		if (notNullKey != null) {
			notNull = notNullKey.booleanValue();
		}
		// Check whether the field is annotated as not nullable
		if (!column.nullable()) {
			notNull = true;
		}

		// Build up the length/scale/precision. The length only applies to a
		// String/varchar (and therefore enums too).
		String size = "";
		// if ((String.class.isAssignableFrom(field.getType()) || Enum.class
		// .isAssignableFrom(field.getType()))
		// &&
		// else
		// Only produce output if the scale is set. Specifying "(0,N)" is
		// not useful:

		// Check for scale/precision and length.
		//
		// NB javax.persistence.Column specifies 255 an the default length, so
		// we only use the length if precision/scale are not specified or if it
		// is different from 255. However, Strings (and therefore enums) must
		// specify a length, so for these the length is always added.
		//
		// NB precision can only be specified if scale is >0.
		// Scale (total number of digits) must be >= Precision
		// (number of digits after the decimal point).
		//
		if (scale > 0) {
			// Look for the decimal types:
			if (field.getType().isAssignableFrom(Double.class) || field.getType().isAssignableFrom(Float.class)
					|| field.getType().isAssignableFrom(BigDecimal.class)) {
				size = "(" + scale + "," + precision + ")";
			} else {
				// If it's not a decimal, this is presumably a whole number type
				size = "(" + scale + ")";
			}
		} else if (length != 255 || String.class.isAssignableFrom(field.getType())
				|| Enum.class.isAssignableFrom(field.getType())) {
			// Add the length if it is either not default, or if this is a
			// String/enum
			size = "(" + length + ")";
		}

		// Check for a unique key
		String unique = "";
		if (column.unique()) {
			unique = "unique ";
		}

		// Check for a default value
		Object value = reflection.getFieldValue(field, bean);
		if (field.getType().isAssignableFrom(Timestamp.class) && value != null) {
			value = Long.valueOf(((Timestamp) value).getTime());
		}
		String defaultValue = "";
		if (value != null) {
			defaultValue = " default ?"; // + sql.quoted(value);
		}

		// Return the completed definition
		String nullString;
		if (notNull) {
			nullString = "not null";
		} else {
			nullString = "null";
		}
		return name + " " + sqlTypeName + size + " " + unique + nullString + defaultValue;
	}

	/**
	 * @return the reflection
	 */
	public Reflection getReflection() {
		return reflection;
	}

	/**
	 * @param reflection
	 *            the reflection to set
	 */
	public void setReflection(Reflection reflection) {
		this.reflection = reflection;
	}
}
