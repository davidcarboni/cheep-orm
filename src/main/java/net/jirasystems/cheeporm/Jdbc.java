/**
 * 
 */
package net.jirasystems.cheeporm;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;

/**
 * This class uses a subset of the standard JDBC type mappings. These are
 * expected to be as follows.
 * 
 * Mappings from database types to Java types are determined by
 * {@link ResultSet#getObject(String)}, which follows the JDBC mappings. These
 * are expected to be as follows:
 * <ul>
 * <li>CHAR String</li>
 * <li>VARCHAR String</li>
 * <li>LONGVARCHAR String</li>
 * <li>NUMERIC java.math.BigDecimal</li>
 * <li>DECIMAL java.math.BigDecimal</li>
 * <li>BIT boolean</li>
 * <li>BOOLEAN boolean</li>
 * <li>TINYINT byte</li>
 * <li>SMALLINT short</li>
 * <li>INTEGER int</li>
 * <li>BIGINT long</li>
 * <li>REAL float</li>
 * <li>FLOAT double</li>
 * <li>DOUBLE double</li>
 * <li>DATE java.sql.Date</li>
 * <li>TIME java.sql.Time</li>
 * <li>TIMESTAMP java.sql.Timestamp</li>
 * </ul>
 * 
 * Mappings from Java types to database types are determined by
 * {@link PreparedStatement#setObject(int, Object)}, which follows the JDBC
 * mappings. These are expected to be as follows:
 * <ul>
 * <li>String - VARCHAR</li>
 * <li>Character - VARCHAR</li>
 * <li>Integer- INTEGER</li>
 * <li>Long - BIGINT</li>
 * <li>Short - SMALLINT</li>
 * <li>Byte - TINYINT</li>
 * <li>Double - DOUBLE</li>
 * <li>java.math.BigDecimal - DECIMAL</li>
 * <li>Float - REAL</li>
 * <li>Boolean - BOOLEAN</li>
 * <li>java.sql.Date - DATE</li>
 * <li>java.sql.Time - TIME</li>
 * <li>java.sql.Timestamp - TIMESTAMP</li>
 * </ul>
 * 
 * The following mappings are not explicitly supported: (i.e. not tested)
 * <ul>
 * <li>BINARY byte[]</li>
 * <li>VARBINARY byte[]</li>
 * <li>LONGVARBINARY byte[]</li>
 * <li>CLOB Clob</li>
 * <li>BLOB Blob</li>
 * <li>ARRAY Array</li>
 * <li>DISTINCT mapping of underlying type</li>
 * <li>STRUCT Struct</li>
 * <li>REF Ref</li>
 * <li>DATALINK java.net.URL</li>
 * <li>JAVA_OBJECT underlying Java class</li>
 * </ul>
 * 
 * @author david
 * 
 */
public class Jdbc {

	/**
	 * The database connection to be used.
	 */
	private Connection connection;

	private Reflection reflection;

	/**
	 * @param connection
	 *            The database connection.
	 */
	public Jdbc(Connection connection) {
		this.connection = connection;
		reflection = new Reflection();
	}

	/**
	 * This method is equivalent to calling
	 * {@link #newPreparedStatement(String, boolean)} and passing false for
	 * <code>returnGeneratedKeys</code>.
	 * 
	 * @param sql
	 *            The SQL query to be compiled.
	 * @return A {@link PreparedStatement} for the given query.
	 * @throws SQLException
	 *             Passed up if thrown by the database {@link Connection}.
	 */
	public PreparedStatement newPreparedStatement(String sql)
			throws SQLException {
		return newPreparedStatement(sql, false);
	}

	/**
	 * @param sql
	 *            The SQL query to be compiled.
	 * @param returnGeneratedKeys
	 *            Pass true if this {@link PreparedStatement} is for an insert
	 *            and you wish to retrieve the insert ID later, false otherwise.
	 * @return A {@link PreparedStatement} for the given query.
	 * @throws SQLException
	 *             Passed up if thrown by the database {@link Connection}.
	 */
	public PreparedStatement newPreparedStatement(String sql,
			boolean returnGeneratedKeys) throws SQLException {
		PreparedStatement preparedStatement;
		if (returnGeneratedKeys) {
			try {
				preparedStatement = connection.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
			} catch (SQLException e) {
				// HSQLDB doesn't support returning insert ID:
				preparedStatement = connection.prepareStatement(sql);
			}
		} else {
			preparedStatement = connection.prepareStatement(sql);
		}
		return preparedStatement;
	}

	/**
	 * @param preparedStatement
	 *            The {@link PreparedStatement} into which parameters are to be
	 *            set.
	 * @param fields
	 *            The list of bean fields to set as parameters.
	 * @param bean
	 *            The bean from which field values will be read and set into
	 *            parameter placeholders.
	 * @return The last parameter index that was set. This allows other method
	 *         to set further parameters.
	 * @throws SQLException
	 *             Passed up if thrown by the given {@link PreparedStatement}.
	 */
	public int setParameters(PreparedStatement preparedStatement,
			List<Field> fields, Object bean) throws SQLException {

		return setParametersRepeat(preparedStatement, fields, bean, 1);
	}

	/**
	 * @param preparedStatement
	 *            The {@link PreparedStatement} into which parameters are to be
	 *            set.
	 * @param fields
	 *            The list of bean fields to set as parameters.
	 * @param bean
	 *            The bean from which field values will be read and set into
	 *            parameter placeholders.
	 * @param repeat
	 *            number of times to repeat setting the parameters
	 * @return The last parameter index that was set. This allows other method
	 *         to set further parameters.
	 * @throws SQLException
	 *             Passed up if thrown by the given {@link PreparedStatement}.
	 */
	public int setParametersRepeat(PreparedStatement preparedStatement,
			List<Field> fields, Object bean, int repeat) throws SQLException {
		for (int i = 0; i < (fields.size() * repeat); i++) {
			Field field = fields.get(i % fields.size());
			setParameter(preparedStatement, field, bean, i + 1);
		}
		return fields.size();
	}

	/**
	 * 
	 * @param preparedStatement
	 *            The prepared statement to set the parameter in.
	 * @param field
	 *            A bean field.
	 * @param bean
	 *            The bean instance from which the field will be queried.
	 * @param parameterIndex
	 *            The parameter index in the {@link PreparedStatement} which
	 *            will be set.
	 * @throws SQLException
	 *             Passed up if thrown by the given {@link PreparedStatement}.
	 */
	protected void setParameter(PreparedStatement preparedStatement,
			Field field, Object bean, int parameterIndex) throws SQLException {
		Object value = reflection.getFieldValue(field, bean);

		setParameterValue(preparedStatement, field, value, parameterIndex);
	}

	/**
	 * Sets the given value in one of the parameter placeholders in the given
	 * prepared statement.
	 * 
	 * @param preparedStatement
	 *            The query.
	 * @param field
	 *            Used to determine the SQL type if the value is null.
	 * @param value
	 *            The value to be set in the prepared statement.
	 * @param parameterIndex
	 *            The index of the parameter placeholder.
	 * @throws SQLException
	 *             If an error occurs in setting the value.
	 */
	protected void setParameterValue(PreparedStatement preparedStatement,
			Field field, Object value, int parameterIndex) throws SQLException {

		// Convert Enums and Characters to Strings:
		Object parameter = value;
		if ((value != null)
				&& (Enum.class.isAssignableFrom(field.getType()) || (Character.class
						.isAssignableFrom(field.getType())))) {
			parameter = value.toString();
		}

		if (value == null) {
			setNullParameter(preparedStatement, field.getType(), parameterIndex);
			// } else if (Enum.class.isAssignableFrom(field.getType())) {
			// setEnumParameter(preparedStatement, field, bean, parameterIndex);
			// } else if (Character.class.isAssignableFrom(field.getType())) {
			// // Workaround for problem saving Character values
			// String string = value.toString();
			// preparedStatement.setObject(parameterIndex, string);
		} else {
			preparedStatement.setObject(parameterIndex, parameter);
		}
	}

	// /**
	// * Sets the gven enum field as a String parameter in the given {@link
	// PreparedStatement}.
	// *
	// * @param preparedStatement
	// * The prepared statement to set the enum value in.
	// * @param field
	// * An enum-typed field.
	// * @param bean
	// * The instance from which the field will be queried.
	// * @param parameterIndex
	// * The parameter index in the {@link PreparedStatement} which will be set.
	// * @throws SQLException
	// * Passed up if thrown by the given {@link PreparedStatement}.
	// */
	// private void setEnumParameter(PreparedStatement preparedStatement, Field
	// field, Object bean, int parameterIndex)
	// throws SQLException {
	// @SuppressWarnings("unchecked")
	// Enum<? extends Enum<?>> value = (Enum<? extends Enum<?>>)
	// reflection.getFieldValue(field, bean);
	// // In this case, name() is used, because toString() is sometimes
	// // overridden to give a different value.
	// preparedStatement.setObject(parameterIndex, value.toString());
	// }

	/**
	 * Sets a NULL parameter placeholder.
	 * 
	 * @param preparedStatement
	 *            The {@link PreparedStatement} into which the parameter will be
	 *            set.
	 * @param type
	 *            The type of the field, used to determine the SQL type of the
	 *            parameter.
	 * @param parameterIndex
	 *            The index for the null parameter.
	 * @throws SQLException
	 *             If an error occurs in setting the parameter.
	 */
	protected void setNullParameter(PreparedStatement preparedStatement,
			Class<?> type, int parameterIndex) throws SQLException {

		SqlType sqlType = SqlType.toSqlMap.get(type);

		if (sqlType != null) {
			preparedStatement.setNull(parameterIndex, sqlType.sqlType);
		} else if (Enum.class.isAssignableFrom(type)) {
			preparedStatement.setNull(parameterIndex, java.sql.Types.VARCHAR);
		} else {
			throw new IllegalArgumentException(
					"Un-mapped parameter type: "
							+ type.getSimpleName()
							+ ". If you wish to map this type, please add an entry to the type map in the class "
							+ SqlType.class.getName() + " class.");
		}

	}

	/**
	 * Inserts a row.
	 * 
	 * @param preparedStatement
	 *            The {@link PreparedStatement} to be run.
	 * @return The generated key at index 1 of the first row in the generated
	 *         keys result set, or -1 if there is no result set, or there are no
	 *         rows in the result set.
	 * @throws SQLException
	 *             If an error occurs running the query or getting the generated
	 *             key.
	 */
	protected int insert(PreparedStatement preparedStatement)
			throws SQLException {
		int result = -1;

		preparedStatement.execute();

		// Get a generated keys result set:
		ResultSet resultSet = null;
		try {
			resultSet = preparedStatement.getGeneratedKeys();
		} catch (SQLException e) {
			// HSQLDB doesn't support this, so go special:
			PreparedStatement psIdentity = connection
					.prepareStatement("CALL IDENTITY()");
			resultSet = psIdentity.executeQuery();
		}

		// Get the key:
		try {
			if ((resultSet != null) && resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} finally {
			DbUtils.closeQuietly(resultSet);
		}

		return result;
	}

	/**
	 * Selects a single row.
	 * 
	 * @param preparedStatement
	 *            The query to be run.
	 * @param fields
	 *            The fields to be returned from the query.
	 * @return A map of the given fields, associated with the objects returned
	 *         by the query, as returned by {@link ResultSet#getObject(String)}.
	 * @throws SQLException
	 *             If a database error occurs, or if more than one row is
	 *             returned.
	 */
	protected Map<Field, Object> selectOne(PreparedStatement preparedStatement,
			List<Field> fields) throws SQLException {

		preparedStatement.setMaxRows(2);
		List<Map<Field, Object>> rows = selectMany(preparedStatement, fields);

		if (rows.size() > 1) {
			throw new SQLException("More than one row returned.");
		}

		// Return the single row if present
		if (rows.size() > 0) {
			return rows.get(0);
		}

		// Otherwise return null
		return null;
	}

	/**
	 * Selects zero or more rows.
	 * 
	 * @param preparedStatement
	 *            The query to be run.
	 * @param fields
	 *            The fields to be returned from the query.
	 * @return A list of maps, containing the given fields, associated with the
	 *         objects returned by the query, as returned by
	 *         {@link ResultSet#getObject(String)}.
	 * @throws SQLException
	 *             If a database error occurs.
	 */
	public List<Map<Field, Object>> selectMany(
			PreparedStatement preparedStatement, List<Field> fields)
			throws SQLException {

		preparedStatement.execute();
		ResultSet resultSet = preparedStatement.getResultSet();
		List<Map<Field, Object>> result = new ArrayList<Map<Field, Object>>();

		while (resultSet.next()) {
			Map<Field, Object> row = new HashMap<Field, Object>();
			for (Field field : fields) {
				Object value = resultSet.getObject(reflection
						.getColumnName(field));
				row.put(field, value);
			}
			result.add(row);
		}

		return result;
	}

	/**
	 * Runs an update.
	 * 
	 * @param preparedStatement
	 *            The query to be run.
	 * @return The update count.
	 * @throws SQLException
	 *             If a database error occurs.
	 */
	public int update(PreparedStatement preparedStatement) throws SQLException {

		preparedStatement.execute();
		int result = preparedStatement.getUpdateCount();

		return result;
	}

	/**
	 * Runs a delete.
	 * 
	 * @param preparedStatement
	 *            The query to be run.
	 * @return The update count (number of rows deleted).
	 * @throws SQLException
	 *             If a database error occurs.
	 */
	public int delete(PreparedStatement preparedStatement) throws SQLException {

		preparedStatement.execute();
		int result = preparedStatement.getUpdateCount();

		return result;
	}

	/**
	 * Does a number of convenience conversions in order to comply with the JDBC
	 * type-mapping standard. For example FLOAT fields actually come back as a
	 * Double rather than a Float, so the value needs to be converted.
	 * 
	 * @param field
	 *            The field that the value is intended for.
	 * @param value
	 *            The value to be checked.
	 * @return If no conversion is required, the value parameter is returned,
	 *         otherwise and object of the appropriate type is returned.
	 */
	protected static Object doJdbcStandardConversions(Field field, Object value) {
		// Do some conversions to fulfil the JDBC standard for Double,
		// Short and Byte, also BigDecimal if the field type is Double as this
		// is sometimes used:
		Object converted = value;

		if (Float.class.equals(field.getType()) && (converted != null)
				&& Double.class.equals(converted.getClass())) {
			converted = Float.valueOf((float) ((Double) converted)
					.doubleValue());
		} else if (Short.class.equals(field.getType()) && (converted != null)
				&& Integer.class.equals(converted.getClass())) {
			converted = Short.valueOf((short) ((Integer) converted).intValue());
		} else if (Byte.class.equals(field.getType()) && (converted != null)
				&& Integer.class.equals(converted.getClass())) {
			converted = Byte.valueOf((byte) ((Integer) converted).intValue());
		} else if (Byte.class.equals(field.getType()) && (converted != null)
				&& Short.class.equals(converted.getClass())) {
			converted = Byte.valueOf((byte) ((Short) converted).shortValue());
		} else if (Double.class.equals(field.getType()) && (converted != null)
				&& BigDecimal.class.equals(converted.getClass())) {
			converted = Double.valueOf(((BigDecimal) converted).doubleValue());
		} else if (Character.class.equals(field.getType())
				&& (converted != null)
				&& String.class.equals(converted.getClass())) {
			if (((String) converted).length() >= 0) {
				converted = Character.valueOf(((String) converted).charAt(0));
			} else {
				// Nearest Character representation for a zero-length String
				converted = null;
			}
		}

		return converted;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
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
