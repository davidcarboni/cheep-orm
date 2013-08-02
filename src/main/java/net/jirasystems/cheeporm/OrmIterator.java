package net.jirasystems.cheeporm;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;

/**
 * This {@link Iterator} implementation allows {@link Orm} results to be iterated, so avoiding
 * constructing a bean for every result row, which could produce an excessively large list.
 * <p>
 * This class adds two public methods over and above {@link Iterator}: {@link #skip()} and
 * {@link #close()}. {@link #skip()} allows you to move forward one row in the {@link ResultSet}
 * without generating a bean for that row and {@link #close()} allows you to explicitly release the
 * underlying {@link ResultSet} and {@link PreparedStatement}.
 * <p>
 * It is safe to only use the {@link Iterator} interface, providing you iterate all the way to the
 * end. Reaching the end of the iterator (when {@link #hasNext()} returns false) will automatically
 * release resources. As a fallback, a {@link #finalize()} implementation is provided to ensure
 * these are released, but it's probably best not to rely on it.
 * 
 * @author David Carboni
 * 
 * @param <B>
 *            The bean class to be iterated.
 */
public class OrmIterator<B> implements Iterator<B>, Closeable {

	private final List<Field> fields;
	private final B bean;
	private final ResultSet resultSet;
	private final PreparedStatement preparedStatement;
	private Reflection reflection = new Reflection();

	// Whether the next row has already been pre-fetched:
	private boolean prefetched;

	/**
	 * If this constructor completes successfully, the {@link PreparedStatement} passed in will be
	 * closed automatically once the last row has been iterated. Alternatively, the close method
	 * defined by this class can be called explicitly.
	 * 
	 * @param preparedStatement
	 *            The {@link PreparedStatement} whose results will be iterated by this instance.
	 * @param fields
	 *            The fields to be mapped to result beans.
	 * @param bean
	 *            An instance of the bean type. This is used as a basis for generating returned
	 *            instances.
	 * @throws SQLException
	 *             If an error occurs calling {@link PreparedStatement#execute()} or
	 *             {@link PreparedStatement#getResultSet()}. If an exception is thrown, the caller
	 *             is responsible for catching it and closing the {@link PreparedStatement}.
	 */
	public OrmIterator(PreparedStatement preparedStatement, List<Field> fields, B bean) throws SQLException {

		this.fields = fields;
		this.bean = bean;
		this.preparedStatement = preparedStatement;

		// Run the query:
		try {
			preparedStatement.execute();
			resultSet = preparedStatement.getResultSet();
		} catch (SQLException e) {
			close();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation returns true if {@link #resultSet} is not null, not closed and not after
	 * the last record. If a {@link SQLException} is thrown, it is thrown wrapped in a
	 * {@link RuntimeException}.
	 */
	@Override
	public boolean hasNext() {
		boolean result = prefetched;
		try {
			if (!result && !resultSet.isClosed()) {
				// Do we have a next record:
				result = resultSet.next();

				// If not, close:
				if (result) {
					prefetched = true;
				} else {
					// It's safe to call this on a closed ResultSet:
					close();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error checking for next record in " + ResultSet.class.getSimpleName(), e);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation gets the next bean from the {@link #resultSet}. If a {@link SQLException}
	 * is thrown, it is thrown wrapped in a {@link RuntimeException}.
	 */
	@Override
	public B next() {

		B result = null;

		try {
			if (prefetched || resultSet.next()) {
				prefetched = false;

				// Map the row:
				Map<Field, Object> row = new HashMap<Field, Object>();
				for (Field field : fields) {
					Object value = resultSet.getObject(reflection.getColumnName(field));
					row.put(field, value);
				}

				// Generate the bean:
				B item = reflection.newInstance(bean);
				for (Field field : row.keySet()) {
					Object value = row.get(field);
					value = Jdbc.doJdbcStandardConversions(field, value);
					reflection.setFieldValue(field, item, value);
				}
				result = item;
			} else {
				close();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error getting next record from " + ResultSet.class.getSimpleName(), e);
		}

		return result;
	}

	/**
	 * Skips the next record in {@link #resultSet}. This allown you to avoid constructing bean
	 * instances for rows you're not interested in (e.g. if you're implementing pagination). This
	 * method calls {@link #hasNext()} and then sets the {@link #prefetched} to <code>false</code>.
	 */
	public void skip() {

		if (hasNext()) {
			// Skip the pre-fetched record,
			// which hasNext will have loaded:
			prefetched = false;
		}
	}

	/**
	 * This optional operation is not implemented as in does not make sense to "remove" a row from a
	 * {@link ResultSet}. This method does nothing.
	 */
	@Override
	public void remove() {
		// No implementation.
	}

	/**
	 * As a fallback, this <code>finalize()</code> implementation will call {@link #close()}.
	 */
	@Override
	protected void finalize() {
		close();
	}

	/**
	 * Quitetly closes the {@link PreparedStatement} and thereforen the {@link ResultSet} as well.
	 * 
	 * @see DbUtils#closeQuietly(java.sql.Statement)
	 * @see ResultSet#close()
	 */
	@Override
	public void close() {
		// This will also close the result set:
		DbUtils.closeQuietly(preparedStatement);
	}

}
