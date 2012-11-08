/**
 * 
 */
package net.jirasystems.cheeporm;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;

/**
 * @author david
 * 
 */
public class Sql {

	private Reflection reflection;

	/**
	 * The default constructor initialises the internal {@link Reflection} instance.
	 */
	public Sql() {
		reflection = new Reflection();
	}

	/**
	 * Generates an "insert into ..." statement.
	 * 
	 * @param table
	 *            The table to be inserted into.
	 * @param fields
	 *            The fields to be inserted.
	 * @return A suitable SQL String, based on the given parameters.
	 */
	public String insert(String table, List<Field> fields) {

		String sql = "insert into " + table + " (" + fieldList(fields) + ") values (" + placeholderList(fields) + ")";

		return sql;
	}

	/**
	 * Generates a "select * from ..." statement.
	 * 
	 * @param table
	 *            The table to be selected from.
	 * @param filters
	 *            The fields for the WHERE clause, or an empty list if none.
	 * @return A suitable SQL String, based on the given parameters.
	 */
	public String select(String table, List<Field> filters) {

		String whereClause = whereClause(filters);
		return select(table, whereClause);
	}

	/**
	 * Generates a "select * from ..." statement.
	 * 
	 * @param table
	 *            The table to be selected from.
	 * @param whereClause
	 *            The where clause. If empty or null, no WHERE will be added.
	 * @return A suitable SQL String, based on the given parameters.
	 */
	protected String select(String table, String whereClause) {

		String sql = "select * from " + table;
		if (!StringUtils.isEmpty(whereClause)) {
			sql += " where " + whereClause;
		}

		return sql;
	}

	/**
	 * Generates an "update" statement.
	 * 
	 * @param table
	 *            The table to be updated.
	 * @param fields
	 *            The fields to be updated.
	 * @param filters
	 *            The filter fields for the "where" clause.
	 * @return A suitable SQL String, based on the given parameters.
	 */
	public String update(String table, List<Field> fields, List<Field> filters) {

		if (fields.size() == 0) {
			return null;
		}

		String updateList = updateList(fields);
		String whereClause = whereClause(filters);
		String sql = "update " + table + " set " + updateList;
		if (whereClause.length() > 0) {
			sql += " where " + whereClause;
		}

		return sql;
	}

	/**
	 * Generates a "delete from ..." statement.
	 * 
	 * @param table
	 *            The table to delete from.
	 * @param filters
	 *            The fields for the WHERE clause, or an empty list if none.
	 * @return A suitable SQL String, based on the given parameters.
	 */
	public String delete(String table, List<Field> filters) {

		String whereClause = whereClause(filters);
		String sql = "delete from " + table + " where " + whereClause;

		return sql;
	}

	/**
	 * Generates a "select count(*) from ..." statement.
	 * 
	 * @param table
	 *            The table to be listed.
	 * @param filters
	 *            The fields for the WHERE clause, or an empty list if none.
	 * @return A suitable SQL String, based on the given parameters.
	 */
	public String count(String table, List<Field> filters) {

		String sql = "select count(*) from " + table;
		if (filters.size() > 0) {
			sql += " where " + whereClause(filters);
		}

		return sql;
	}

	/**
	 * Generates a "select * from ..." statement.
	 * 
	 * @param table
	 *            The table to be listed.
	 * @param filters
	 *            The fields for the WHERE clause, or an empty list if none.
	 * @param orderBy
	 *            The fields for the ORDER BY clause, or an empty list if none.
	 * @return A suitable SQL String, based on the given parameters.
	 */
	public String list(String table, List<Field> filters, List<Field> orderBy) {

		String sql = "select * from " + table;
		if (filters.size() > 0) {
			sql += " where " + whereClause(filters);
		}
		if (orderBy.size() > 0) {
			sql += " order by " + orderByClause(orderBy);
		}

		return sql;
	}

	/**
	 * Generates a statement to return the record with the maximum value if the field defined by
	 * maxValueField and with the given filters.
	 * 
	 * @param table
	 *            The table to be listed.
	 * @param filters
	 *            The fields for the WHERE clause, or an empty list if none.
	 * @param maxValueField
	 *            The field to get the max value of.
	 * @return A suitable SQL String, based on the given parameters.
	 */
	public String max(String table, List<Field> filters, Field maxValueField) {

		String sql = "select * from " + table;
		if (filters.size() > 0) {
			sql += " where " + whereClause(filters);
		}

		String maxColumn = reflection.getColumnName(maxValueField);

		String maxSql = "select max(" + maxColumn + ") from " + table;
		if (filters.size() > 0) {
			maxSql += " where " + whereClause(filters);
		}

		sql = sql + " and " + maxColumn + "=(" + maxSql + ")";

		return sql;
	}

	// Convenience methods to generate field, parameter and value lists

	/**
	 * @param fields
	 *            The fields to be included in the returned String.
	 * @return A list of field names from the given list.
	 */
	private String fieldList(List<Field> fields) {

		StringBuilder stringBuilder = new StringBuilder();

		for (Field field : fields) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(reflection.getColumnName(field));
		}

		return stringBuilder.toString();
	}

	/**
	 * @param fields
	 *            A list of fields, whose length determines the number of ? placeholders that will
	 *            be included in the returned string.
	 * 
	 * @return A string containing the correct number of ? placeholders to match the given list of
	 *         fields.
	 */
	private String placeholderList(List<Field> fields) {

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < fields.size(); i++) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append("?");
		}

		return stringBuilder.toString();
	}

	/**
	 * This method can make use of the "updateable" property of the {@link Column} annotation. If
	 * updateable has been set to false for a given field it will be omitted from the returned
	 * String.
	 * 
	 * @param fields
	 *            The fields to be included in the returned String.
	 * @return A list of field names and values from the given list, suitable for an update
	 *         statement: field=value, etc.
	 */
	protected String updateList(List<Field> fields) {

		StringBuilder stringBuilder = new StringBuilder();

		for (Field field : fields) {
			// Verify that the column can be updated
			if (reflection.isUpdateable(field)) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}
				stringBuilder.append(reflection.getColumnName(field));
				stringBuilder.append("=?");
			}
		}

		return stringBuilder.toString();
	}

	/**
	 * Generates a WHERE clause, excluding the WHERE, i.e. an AND-separated list of "field=?".
	 * 
	 * @param keys
	 *            The fields to be included in the clause.
	 * @return A list of database field names with JDBC placeholders.
	 */
	protected String whereClause(List<Field> keys) {

		StringBuilder whereClause = new StringBuilder();
		for (Field key : keys) {
			if (whereClause.length() > 0) {
				whereClause.append(" and ");
			}
			whereClause.append(reflection.getColumnName(key));
			whereClause.append("=?");
		}
		return whereClause.toString();
	}

	/**
	 * Generates an ORDER BY clause, excluding the ORDER BY, i.e. a comma-separated list of fields.
	 * 
	 * @param keys
	 *            The fields to be included in the clause.
	 * @return A comma-separated list of database field names.
	 */
	private String orderByClause(List<Field> keys) {

		StringBuilder orderByClause = new StringBuilder();
		for (Field key : keys) {
			if (orderByClause.length() > 0) {
				orderByClause.append(", ");
			}
			orderByClause.append(reflection.getColumnName(key));
		}
		return orderByClause.toString();
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
