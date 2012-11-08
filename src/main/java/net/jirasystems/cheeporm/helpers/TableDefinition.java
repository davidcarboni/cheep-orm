package net.jirasystems.cheeporm.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.jirasystems.cheeporm.Reflection;

import org.apache.commons.lang.StringUtils;

/**
 * SQL definition of a table.
 * 
 * @param <B>
 *            The bean type.
 * @author David
 * 
 */
public class TableDefinition<B> {

	private Reflection reflection = new Reflection();
	private String tableName;
	private List<Field> keys;
	private List<Field> fields;
	private B defaults;

	/**
	 * Creates a table definition with no default values.
	 * 
	 * @param beanClass
	 *            The class to generate a table definition for.
	 */
	public TableDefinition(Class<B> beanClass) {
		tableName = reflection.getTable(beanClass);
		keys = reflection.listKeys(beanClass);
		fields = reflection.listFields(beanClass);
	}

	/**
	 * Creates a table definition with default values specified by the fields in the given bean.
	 * 
	 * @param bean
	 *            The bean to generate a table definition for.
	 */
	@SuppressWarnings("unchecked")
	public TableDefinition(B bean) {
		this((Class<B>) bean.getClass());
		this.defaults = bean;
	}

	/**
	 * @return The create table SQL.
	 */
	public String createTable() {

		String createTable = createTableString();

		List<String> keys = keyStrings();
		List<String> fields = fieldStrings();
		String keysString = StringUtils.join(keys, ",\n");
		String fieldsString = StringUtils.join(fields, ",\n");

		return String.format(createTable, keysString + ",\n" + fieldsString);
	}

	/**
	 * Returns the surrounding "create table" sql, including brackets, with a %s format marker where
	 * the content will go.
	 * <p>
	 * Override this method if you want to use a different form of create table statement, such as
	 * adding "if not exists".
	 * 
	 * @return create table &lt;tableName&gt; (<br>
	 *         %s<br>
	 *         )
	 */
	protected String createTableString() {
		return "create table " + tableName + " (\n\n%s\n)";
	}

	/**
	 * Returns a list of Strings, each containing the SQL to define one of the key fields.
	 * <p>
	 * If there are one or more Integer type fields in the list of keys, this method will define the
	 * first one it finds as an auto increment field.
	 * <p>
	 * Override this method if you would like to change this behaviour.
	 * 
	 * @return A list of SQL field definitions for the key fields.
	 */
	protected List<String> keyStrings() {

		List<String> result = new ArrayList<String>();

		boolean autoincrementDefined = false;
		for (Field key : keys) {
			FieldDefinition fieldDefinition = new FieldDefinition(key, true, defaults);
			String sql = fieldDefinition.fieldDefinitionString();
			if (key.getType().equals(Integer.class) && !autoincrementDefined) {
				sql += " primary key auto_increment";
				autoincrementDefined = true;
			}
			result.add(indentString() + sql);
		}

		return result;
	}

	/**
	 * Returns a list of Strings, each containing the SQL to define one of the key fields.
	 * <p>
	 * If there are one or more Integer type fields in the list of keys, this method will define the
	 * first one it finds as an auto increment field.
	 * <p>
	 * Override this method if you would like to change this behaviour.
	 * 
	 * @return A list of SQL field definitions for the key fields.
	 */
	protected List<String> fieldStrings() {

		List<String> result = new ArrayList<String>();

		for (Field field : fields) {
			FieldDefinition fieldDefinition = new FieldDefinition(field, false, defaults);
			String sql = fieldDefinition.fieldDefinitionString();
			result.add(indentString() + sql);
		}

		return result;
	}

	/**
	 * Returns the string used to indent field definitions within the create table statement.
	 * 
	 * @return Two spaces.
	 */
	protected String indentString() {
		return "  ";
	}
}
