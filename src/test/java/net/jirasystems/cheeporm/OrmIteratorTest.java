package net.jirasystems.cheeporm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Id;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test for {@link OrmIterator}.
 * 
 * @author David Carboni
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class OrmIteratorTest {

	private static final int result_set_size = 2;

	private Bean bean = new Bean();
	private Reflection reflection = new Reflection();

	@Mock
	private PreparedStatement preparedStatement;

	@Mock
	private ResultSet resultSet;

	private OrmIterator<Bean> ormIterator;

	/**
	 * Simple bean class for this test.
	 */
	public static class Bean {

		@SuppressWarnings("unused")
		@Id
		@Column
		private Integer id;

		@SuppressWarnings("unused")
		@Column
		private String name;
	}

	/**
	 * Sets up the {@link OrmIterator} instance.
	 * 
	 * @throws SQLException
	 *             If an error occurs in constructing the class under test.
	 */
	@SuppressWarnings("boxing")
	@Before
	public void setUp() throws SQLException {

		// Get the PreparedStatement to return the ResultSet
		when(preparedStatement.getResultSet()).thenReturn(resultSet);

		// ResultSet will have two rows:
		when(resultSet.next()).thenReturn(true, true, false);

		// Instantiate the class under test:
		ormIterator = new OrmIterator<Bean>(preparedStatement, reflection.listAll(Bean.class), bean);
	}

	/**
	 * Verifies that {@link OrmIterator#hasNext()} is true for a newly opened {@link ResultSet} that
	 * contains rows.
	 */
	@Test
	public void shouldHaveNext() {

		// When
		boolean result = ormIterator.hasNext();

		// Then 
		assertTrue(result);
	}

	/**
	 * Verifies that calling {@link OrmIterator#hasNext()} repeatedly does't advance the result set.
	 */
	@Test
	public void shouldAllowMultipleHasNextCalls() {

		// When
		boolean result = ormIterator.hasNext();
		result = ormIterator.hasNext();
		result = ormIterator.hasNext();
		result = ormIterator.hasNext();
		result = ormIterator.hasNext();
		result = ormIterator.hasNext();

		// Then 
		assertTrue(result);
		int count = 0;
		while (ormIterator.hasNext()) {
			assertNotNull(ormIterator.next());
			count++;
		}
		assertEquals(result_set_size, count);
	}

	/**
	 * Verifies that calling {@link OrmIterator#next()} returns the expected number of records and
	 * then null.
	 */
	@Test
	public void shouldGetTwoBeans() {

		// When
		Bean bean1 = ormIterator.next();
		Bean bean2 = ormIterator.next();
		Bean bean3 = ormIterator.next();

		// Then 
		assertNotNull(bean1);
		assertNotNull(bean2);
		assertNull(bean3);
	}

	/**
	 * Verifies that calling {@link OrmIterator#hasNext()} repeatedly does't advance the result set.
	 * 
	 * @throws SQLException
	 *             if an error occurs.
	 */
	@Test
	public void shouldCloseWhenAllRecordsRetrieved() throws SQLException {

		// When
		while (ormIterator.hasNext()) {
			ormIterator.next();
		}

		// Then 
		verify(preparedStatement).close();
	}

	/**
	 * Verifies that the {@link PreparedStatement} is closed if there is an error in constructing
	 * the {@link OrmIterator}.
	 * 
	 * @throws SQLException
	 *             if an error occurs.
	 */
	@SuppressWarnings({"boxing", "unchecked", "unused"})
	@Test
	public void shouldCloseIfErrorInConstruction() throws SQLException {

		// Given
		when(preparedStatement.execute()).thenThrow(SQLException.class);

		// When
		try {
			new OrmIterator<OrmIteratorTest.Bean>(preparedStatement, new ArrayList<Field>(), bean);
			fail("Expected a " + SQLException.class.getSimpleName() + " to be thrown.");
		} catch (SQLException e) {
			// expected.
		}

		// Then 
		verify(preparedStatement).close();
	}

}
