package net.jirasystems.cheeporm.helpers;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import net.jirasystems.cheeporm.Reflection;
import net.jirasystems.cheeporm.SqlType;

import org.apache.commons.lang.StringUtils;

/**
 * SQL definition for a column.
 * 
 * @author David
 * 
 */
public class FieldDefinition {

	private Reflection reflection = new Reflection();

	private Field field;

	private String name;
	private String definition;
	private int length;
	private boolean nullable;
	private boolean unique;

	private int precision;
	private int scale;

	private String sqlTypeName;

	private Object instance;

	/**
	 * 
	 * @param field
	 *            The field to be defined.
	 * @param isKey
	 *            If true, the field will always be non-nullable.
	 * @param instance
	 *            Optional bean instance containing a default value for this
	 *            field.
	 */
	public FieldDefinition(Field field, boolean isKey, Object... instance) {

		// Get the column annotation:
		this.field = field;
		Column column = field.getAnnotation(Column.class);

		// Get the values from the annotation:
		definition = column.columnDefinition();
		name = column.name();
		length = column.length();
		nullable = column.nullable() || isKey;
		unique = column.unique();
		precision = column.precision();
		scale = column.scale();

		// Save the default values instance:
		if (instance.length > 0) {
			this.instance = instance[0];
		}

		// Tweak the field name:
		updateColumnName();

		// Set the SQL type:
		setSqlType();
	}

	/**
	 * @return The field definition SQL.
	 */
	public String fieldDefinitionString() {
		String result;

		if (StringUtils.isNotBlank(definition)) {
			result = name + " " + definition;
		} else {

			// Length or Scale/Precision
			String size = "";
			if (field.getType().isAssignableFrom(BigDecimal.class)) {
				size = "(" + scale + "," + precision + ")";
			} else if (String.class.isAssignableFrom(field.getType())
					|| Enum.class.isAssignableFrom(field.getType())) {
				if (length > 0) {
					size = "(" + length + ")";
				}
			}

			// Unique
			String unique = "";
			if (this.unique) {
				unique = " unique";
			}

			// Default value
			String defaultValue = "";
			if (instance != null) {
				Object value = reflection.getFieldValue(field, instance);
				if (field.getType().isAssignableFrom(Timestamp.class)
						&& value != null) {
					value = Long.valueOf(((Timestamp) value).getTime());
				}
				if (value != null) {
					defaultValue = " default " + quoted(value);
				}
			}

			// Nullable:
			String nullString;
			if (nullable) {
				nullString = " null";
			} else {
				nullString = " not null";
			}

			// Build the definition:
			return name + " " + sqlTypeName + size + " " + unique + nullString
					+ defaultValue;
		}

		return result;
	}

	/**
	 * Determines the SQL type name of the column.
	 */
	protected void setSqlType() {

		// Get the details of the field:
		SqlType sqlType = SqlType.toSqlMap.get(field.getType());
		if (sqlType == null) {
			// Check for an enum type
			if (Enum.class.isAssignableFrom(field.getType())) {
				sqlType = SqlType.NVARCHAR;
			} else {
				throw new IllegalArgumentException("Field of type "
						+ field.getType()
						+ " could not be mapped to a SQL type. "
						+ "Note that primitives are not supported, "
						+ "you must use primitive wrappers (e.g. Integer) "
						+ "for the declared field type.");
			}
		}

		// A String length of 0 or less indicates a LONGVARCHAR type.
		// By default this is mapped to "TEXT" as this is used by quite a few
		// databases:
		if (field.getType().equals(String.class) && length <= 0) {
			sqlTypeName = "TEXT";
		} else {
			sqlTypeName = sqlType.name();
		}
	}

	/**
	 * Updates the name of the field if the {@link Column} annotation does not
	 * specify an explicit name by converting the field name to a database
	 * identifier using {@link Reflection#camelCaseToDatabase(String)}.
	 * 
	 */
	private void updateColumnName() {

		if (StringUtils.isBlank(name)) {
			name = reflection.camelCaseToDatabase(field.getName());
		}
	}

	/**
	 * This method is necessary because you can't use prepared statement
	 * parameter placeholders for DDL statements.
	 * 
	 * @param value
	 *            The value to be quoted - if it is of a type that needs quotes.
	 * @return The original value, or, if quotes are needed, a String with
	 *         quotes around it and <code>\</code> and <code>'</code> characters
	 *         escaped with a backslash.
	 */
	private String quoted(Object value) {

		// Generate a list of quotable types
		List<Class<?>> quotables = new ArrayList<Class<?>>();
		quotables.add(String.class);
		quotables.add(java.sql.Time.class);
		quotables.add(java.sql.Date.class);
		quotables.add(java.sql.Timestamp.class);
		quotables.add(Enum.class);

		// Escape the value and enclose it in quotes if necessary:
		for (Class<?> type : quotables) {
			if (type.isAssignableFrom(value.getClass())) {
				return "'"
						+ value.toString().replace("\\", "\\\\")
								.replace("'", "\'") + "'";
			}
		}

		// If not, return the value
		return value.toString();
	}

	/**
	 * @return the name
	 */
	protected String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the definition
	 */
	protected String getDefinition() {
		return definition;
	}

	/**
	 * @param definition
	 *            the definition to set
	 */
	protected void setDefinition(String definition) {
		this.definition = definition;
	}

	/**
	 * @return the length
	 */
	protected int getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	protected void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the nullable
	 */
	protected boolean isNullable() {
		return nullable;
	}

	/**
	 * @param nullable
	 *            the nullable to set
	 */
	protected void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * @return the unique
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * @param unique
	 *            the unique to set
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	/**
	 * @return the precision
	 */
	protected int getPrecision() {
		return precision;
	}

	/**
	 * @param precision
	 *            the precision to set
	 */
	protected void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * @return the scale
	 */
	protected int getScale() {
		return scale;
	}

	/**
	 * @param scale
	 *            the scale to set
	 */
	protected void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * @return the sqlTypeName
	 */
	protected String getSqlTypeName() {
		return sqlTypeName;
	}

	/**
	 * @param sqlTypeName
	 *            the sqlTypeName to set
	 */
	protected void setSqlTypeName(String sqlTypeName) {
		this.sqlTypeName = sqlTypeName;
	}

	/**
	 * @return the reflection
	 */
	protected Reflection getReflection() {
		return reflection;
	}

	/**
	 * @return the field
	 */
	protected Field getField() {
		return field;
	}
}
