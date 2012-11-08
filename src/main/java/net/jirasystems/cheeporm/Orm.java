/**
 * 
 */
package net.jirasystems.cheeporm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class provides CRUD functionality to persist a given bean type in a single row in a database
 * via JDBC. It additionally provides "list" functionality to assist the caller in finding groups of
 * related rows.
 * 
 * This class is intentionally simple, focusing on direct mapping of a single bean type to a single
 * database row, using JDBC standard type mappings. It explicitly avoids dealing with relational
 * complexity. It is does not attempt to be high-performance, but focuses instead on simplicity,
 * clarity, pragmatics and a small set of common use-cases.
 * 
 * This class operates directly on instance fields and their declared types, so avoiding the need to
 * deduce getter and setter methods. It uses {@link Field#setAccessible(boolean)} as necessary to
 * directly read and write private fields. You are therefore free to use whatever method names or
 * conventions you wish, and can add validation and/or transformation logic for your application
 * within your methods. This is useful if, for example, you wish to use primitive getters and
 * setters for non-nullable fields (see below), or perhaps protect the setter method for an ID
 * field. It is also useful if you want to, say, convert from Date to Timestamp within the getters
 * and setters to ensure correct mapping to SQL type from a private field type.
 * 
 * This class specifically maps annotated fields to database columns with minimal transformation.
 * The fields in the bean that should be persisted need to be marked with a
 * <code>javax.persistence.Column</code> annotation and those which form the primary key should be
 * marked with an <code>javax.persistence.Id</code> annotation.
 * 
 * The class itself should be marked with a <code>javax.persistence.Table</code> annotation. The
 * <code>javax.persistence.Table.name()</code> may be specified if necessary. If no name is
 * specified, the unqualified class name will be used to generate a default name. For example a
 * class name of <code>MyClass</code> will become a table name of <code>my_class</code>.
 * 
 * Fields may optionally specify additional parameters to the <code>javax.persistence.Column</code>
 * annotation, such as "nullable" and "length". However, note that these will only be taken into
 * account in the {@link CreateTable#createStatement(Object)} method. This is intentional. In order
 * to keep things simple, all validation of field contents is delegated to the database, which
 * already has the necessary functionality to report these kinds of violations.
 * 
 * Bean fields need to be Object types, rather than primitives, so that nulls can be represented.
 * This is necessary even for columns that cannot be null, because the
 * {@link #list(Object, Field...)} method needs to be able to use instances that only specify the
 * fields by which a query should be filtered. In this case, ID fields typically need to be null.
 * 
 * This class intentionally does not provide any relational functionality as this is complex and
 * design-specific. Instead, it delegates relationships upwards to the caller. This is the reason
 * that the {@link #list(Object, Field...)} method is provided. The intention is that this method
 * allows the caller to build up n-to-n relationships as necessary, by listing related beans in
 * order to build up relationships.
 * 
 * For example, consider:
 * 
 * <code>Person (1)---(N) Company</code>
 * 
 * To build up this relationship, you would use:
 * 
 * <code>person = Orm.read(Person)</code> (giving the ID of the Person row in the Person bean)
 * <code>companies = Orm.list(Company)</code> (giving the ID of the Person row in the Company bean)
 * <code>Person.setCompanies(companies)</code>
 * 
 * In this way, the application business logic is responsible for the domain-specific relationships
 * and this class can focus on direct mapping between a Java object and a database row.
 * 
 * @author david
 * @param <B>
 *            The type of bean that this instance will operate on.
 * 
 */
public class Orm<B> {

	/**
	 * @param <B>
	 *            The type of bean that the instance will map.
	 * @param connection
	 *            The database connection that is to be used.
	 * @return A new Orm instance for the given type.
	 */
	public static <B> Orm<B> newInstance(Connection connection) {

		return new Orm<B>(connection);
	}

	/**
	 * A {@link Reflection} instance through which to execute queries.
	 */
	private Reflection reflection;

	/**
	 * A {@link Sql} instance through which to execute queries.
	 */
	private Sql sql;

	/**
	 * A {@link Jdbc} instance through which to execute queries.
	 */
	private Jdbc jdbc;

	/**
	 * Constructs a new instance.
	 * 
	 * @param connection
	 *            The database connection that is to be used by this instance.
	 */
	public Orm(Connection connection) {

		reflection = new Reflection();
		sql = new Sql();
		jdbc = new Jdbc(connection);
	}

	/**
	 * Inserts a new row.
	 * 
	 * @param bean
	 *            The bean whose fields are to be inserted.
	 * @return The ID of the inserted record, or -1 if no insert ID is available.
	 * @throws SQLException
	 *             If an error occurs at the database level, it is passed up directly.
	 */
	public int create(B bean) throws SQLException {

		String table = reflection.getTable(bean);
		List<Field> fields = reflection.listInsertFields(bean);
		List<Field> keys = reflection.listInsertKeys(bean);

		// Only add non-null key fields to the insert statement.
		// MSSQL isn't too happy with attempting to insert null into an auto-increment field:
		for (Field key : keys) {

			key.setAccessible(true);
			try {
				if (key.get(bean) != null) {
					fields.add(key);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Unable to access value of key field " + key.getName() + " on instance of "
						+ bean.getClass().getName()
						+ " in order to determine whether it should be added to the insert.");
			}
			key.setAccessible(false);
		}

		String query = sql.insert(table, fields);
		PreparedStatement preparedStatement = jdbc.newPreparedStatement(query, true);
		try {

			jdbc.setParameters(preparedStatement, fields, bean);
			int id = jdbc.insert(preparedStatement);

			// System.out.println(preparedStatement);

			return id;

		} finally {
			preparedStatement.close();
		}
	}

	/**
	 * Reads a row from the database. The values from the row are placed into a new instance of the
	 * given bean type. If the row was not found, null is returned. In both cases, the bean passed
	 * in remains unchanged.
	 * 
	 * @param bean
	 *            A bean containing the key(s) that identify the row to be read.
	 * @return The instance which was passed in, with all of its
	 *         <code>javax.persistence.Column</code> fields set to the values read from the database
	 *         row. If the row was not found, null is returned.
	 * @throws SQLException
	 *             If an error occurs at the database level, it is passed up directly.
	 */
	public B read(B bean) throws SQLException {

		String table = reflection.getTable(bean);
		List<Field> keys = reflection.listKeys(bean);
		List<Field> all = reflection.listAll(bean);

		String query = sql.select(table, keys);
		PreparedStatement preparedStatement = jdbc.newPreparedStatement(query);

		try {

			jdbc.setParameters(preparedStatement, keys, bean);
			return read(bean, preparedStatement, all);

		} finally {
			preparedStatement.close();
		}
	}

	/**
	 * 
	 * Reads a row from the database. The values from the row are placed into a new instance of the
	 * type of the given bean. If the row was not found, null is returned. In both cases, the bean
	 * passed in remains unchanged.
	 * 
	 * @param bean
	 *            The bean whose type will be returned.
	 * @param preparedStatement
	 *            A {@link PreparedStatement} with all parameters set, ready to be run. It is the
	 *            caller's responsibility to close the statement.
	 * @param fields
	 *            The fields to be set in the returned bean from the result of the query.
	 * @return A populated instance of the given bean class, or null if no row was returned.
	 * @throws SQLException
	 *             If an error occurs in running the query, or if more than one row is returned.
	 */
	protected B read(B bean, PreparedStatement preparedStatement, List<Field> fields) throws SQLException {

		Map<Field, Object> row = jdbc.selectOne(preparedStatement, fields);

		// If the row was not found, return null
		if (row == null) {
			return null;
		}

		// If the row was found, populate a new bean
		B read = reflection.newInstance(bean);
		for (Field field : row.keySet()) {
			Object value = row.get(field);
			value = Jdbc.doJdbcStandardConversions(field, value);
			// Now that we have the right type, set the field:
			reflection.setFieldValue(field, read, value);
		}

		// System.out.println(preparedStatement);

		return read;
	}

	/**
	 * Updates a row in the database.
	 * 
	 * @param bean
	 *            A bean containing the key(s) that identify the row to be updated and the values
	 *            that the row will contain after the update.
	 * @throws SQLException
	 *             If an error occurs at the database level, it is passed up directly, or if the
	 *             update count is not 1.
	 * @return The update count, which will be 1. If the update count is not 1, a
	 *         {@link SQLException} is thrown in order to ensure the caller is made aware that
	 *         either no rows were updated, or more than one row was updated. The purpose of this
	 *         method is to update the row that the bean represents and no others, so it should
	 *         never complete successfully unless the update count was exactly 1.
	 */
	public int update(B bean) throws SQLException {

		String table = reflection.getTable(bean);
		List<Field> fields = reflection.listUpdateFields(bean);
		List<Field> keys = reflection.listKeys(bean);

		// Create a combined list of fields and keys
		List<Field> parameters = new ArrayList<Field>();
		parameters.addAll(fields);
		parameters.addAll(keys);

		String query = sql.update(table, fields, keys);

		if (query == null) {
			throw new SQLException("Bean " + bean.getClass().getSimpleName() + " has no updateable fields.");
		}

		PreparedStatement preparedStatement = jdbc.newPreparedStatement(query);

		try {

			jdbc.setParameters(preparedStatement, parameters, bean);
			int updateCount = jdbc.update(preparedStatement);
			if (updateCount != 1) {
				throw new SQLException("Update count was " + updateCount + ". Expected exatly 1.");
			}

			// System.out.println(preparedStatement);

			return updateCount;

		} finally {
			preparedStatement.close();
		}
	}

	/**
	 * Deletes a row from the database. This method throws an exception if the update count is not
	 * 1.
	 * 
	 * @param bean
	 *            A bean containing the key(s) that identify the rows to be deleted.
	 * @throws SQLException
	 *             If an error occurs in deletion.
	 */
	public void delete(B bean) throws SQLException {

		int deleteCount = deleteMany(bean);
		if (deleteCount != 1) {
			throw new SQLException("Delete count was " + deleteCount + ". Expected exatly 1.");
		}
	}

	/**
	 * Deletes a set of rows from the database. This is typically used for deleting foreign-key
	 * records. This method will not throw an exception if the update count is not 1.
	 * 
	 * @param bean
	 *            A bean containing the key(s) that identify the rows to be deleted.
	 * @throws SQLException
	 *             If an error occurs in deletion.
	 * @return The number of rows deleted.
	 */
	public int deleteMany(B bean) throws SQLException {

		String table = reflection.getTable(bean);
		List<Field> keys = reflection.listKeys(bean);

		String query = sql.delete(table, keys);
		PreparedStatement preparedStatement = jdbc.newPreparedStatement(query);

		try {

			jdbc.setParameters(preparedStatement, keys, bean);
			int deleteCount = jdbc.delete(preparedStatement);
			return deleteCount;

			// System.out.println(preparedStatement);

		} catch (SQLException e) {
			throw new SQLException(e.getMessage() + " (query: " + query + ")");
		} finally {
			preparedStatement.close();
		}
	}

	/**
	 * This method lists records from the bean table in the database. Any non-null fields in the
	 * bean are used to filter the results using a where clause.
	 * 
	 * @param bean
	 *            A bean instance whose field values provide the filter for returned rows.
	 * @return A list of bean instances that match the criteria specified in the parameter instance.
	 * @throws SQLException
	 *             If an error occurs at the database level, it is passed up directly.
	 */
	public int count(B bean) throws SQLException {

		String table = reflection.getTable(bean);
		List<Field> fields = reflection.listAll(bean);

		// Check all fields for values. Populated fields will be used in the
		// where clause.

		List<Field> filters = new ArrayList<Field>();
		for (Field field : fields) {
			Object value = reflection.getFieldValue(field, bean);
			if (value != null) {
				filters.add(field);
			}
		}

		String query = sql.count(table, filters);
		PreparedStatement preparedStatement = jdbc.newPreparedStatement(query);

		try {

			jdbc.setParameters(preparedStatement, filters, bean);

			preparedStatement.execute();
			ResultSet resultSet = preparedStatement.getResultSet();

			int result = 0;
			if (resultSet.next()) {
				result = resultSet.getInt(0);
			}

			System.out.println(preparedStatement);

			return result;

		} finally {
			preparedStatement.close();
		}
	}

	/**
	 * Gets the record with the maximum value if the field defined by maxValueField and with the
	 * filters defined by the values in the given bean.
	 * 
	 * @param bean
	 *            the bean with filter values added
	 * @param maxValueField
	 *            the field for the max value
	 * @return the top record ever
	 * @throws SQLException
	 *             An {@link SQLException}
	 */
	public B getMax(B bean, Field maxValueField) throws SQLException {

		String table = reflection.getTable(bean);
		List<Field> fields = reflection.listAll(bean);

		// Check all fields for values. Populated fields will be used in the
		// where clause.

		List<Field> filters = new ArrayList<Field>();
		for (Field field : fields) {
			Object value = reflection.getFieldValue(field, bean);
			if (value != null) {
				filters.add(field);
			}
		}

		String query = sql.max(table, filters, maxValueField);
		PreparedStatement preparedStatement = jdbc.newPreparedStatement(query);

		try {
			jdbc.setParametersRepeat(preparedStatement, filters, bean, 2);
			return read(bean, preparedStatement, fields);
		} finally {
			preparedStatement.close();
		}
	}

	/**
	 * This method lists records from the bean table in the database. Any non-null fields in the
	 * bean are used to filter the results using a where clause.
	 * 
	 * @param bean
	 *            A bean instance whose field values provide the filter for returned rows.
	 * @param orderBy
	 *            Zero or more fields of the bean, which will be used to order the results.
	 * @return A list of bean instances that match the criteria specified in the parameter instance.
	 * @throws SQLException
	 *             If an error occurs at the database level, it is passed up directly.
	 */
	public List<B> list(B bean, Field... orderBy) throws SQLException {

		String table = reflection.getTable(bean);
		List<Field> fields = reflection.listAll(bean);

		// Check all fields for values. Populated fields will be used in the
		// where clause.

		List<Field> filters = new ArrayList<Field>();
		for (Field field : fields) {
			Object value = reflection.getFieldValue(field, bean);
			if (value != null) {
				filters.add(field);
			}
		}

		String query = sql.list(table, filters, Arrays.asList(orderBy));
		PreparedStatement preparedStatement = jdbc.newPreparedStatement(query);

		try {
			jdbc.setParameters(preparedStatement, filters, bean);
			return list(preparedStatement, fields, bean);
		} finally {
			preparedStatement.close();
		}
	}

	/**
	 * This method lists records from the bean table in the database. Any non-null fields in the
	 * bean are used to filter the results using a where clause.
	 * 
	 * @param bean
	 *            A bean instance whose field values provide the filter for returned rows.
	 * @param orderBy
	 *            Zero or more fields of the bean, which will be used to order the results.
	 * @return A list of bean instances that match the criteria specified in the parameter instance.
	 * @throws SQLException
	 *             If an error occurs at the database level, it is passed up directly.
	 */
	public OrmIterator<B> iterate(B bean, Field... orderBy) throws SQLException {

		String table = reflection.getTable(bean);
		List<Field> fields = reflection.listAll(bean);

		// Check all fields for values. Populated fields will be used in the
		// where clause.

		List<Field> filters = new ArrayList<Field>();
		for (Field field : fields) {
			Object value = reflection.getFieldValue(field, bean);
			if (value != null) {
				filters.add(field);
			}
		}

		String query = sql.list(table, filters, Arrays.asList(orderBy));
		PreparedStatement preparedStatement = jdbc.newPreparedStatement(query);
		jdbc.setParameters(preparedStatement, filters, bean);

		return new OrmIterator<B>(preparedStatement, fields, bean);
	}

	/**
	 * Does the work of executing a {@link PreparedStatement} and mapping the resuls to a bean
	 * instance.
	 * 
	 * @param preparedStatement
	 *            The {@link PreparedStatement} to run.
	 * @param fields
	 *            The fields to be mapped into the returned beans.
	 * @param bean
	 *            An instance of the bean type.
	 * @return A list of beans, populated with the selected data.
	 * @throws SQLException
	 *             If an error occurs at the database level, it is passed up directly.
	 */
	protected List<B> list(PreparedStatement preparedStatement, List<Field> fields, B bean) throws SQLException {

		try {

			List<Map<Field, Object>> rows = jdbc.selectMany(preparedStatement, fields);
			List<B> result = new ArrayList<B>();

			for (Map<Field, Object> row : rows) {

				B item = reflection.newInstance(bean);
				for (Field field : row.keySet()) {
					Object value = row.get(field);
					value = Jdbc.doJdbcStandardConversions(field, value);
					reflection.setFieldValue(field, item, value);
				}
				result.add(item);
			}

			// System.out.println(preparedStatement);

			return result;

		} finally {
			preparedStatement.close();
		}
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

	/**
	 * @return the sql
	 */
	public Sql getSql() {
		return sql;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(Sql sql) {
		this.sql = sql;
	}

	/**
	 * @return the jdbc
	 */
	public Jdbc getJdbc() {
		return jdbc;
	}

	/**
	 * @param jdbc
	 *            the jdbc to set
	 */
	public void setJdbc(Jdbc jdbc) {
		this.jdbc = jdbc;
	}
}
