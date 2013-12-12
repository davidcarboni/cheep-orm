/**
 * 
 */
package net.jirasystems.cheeporm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author david
 * 
 */
public class Database {

	public static boolean useTransaction = true;

	public static String databaseCreationUser;
	public static String databaseCreationPassword;
	public static String testDatabaseName;

	public static String testUser;
	public static String testPassword;

	private static Reflection reflection = new Reflection();

	public static void beforeSuite() throws ClassNotFoundException,
			SQLException {

		// Attempt to load configuration values:
		try {
			Properties properties = DatabaseProperties.getProperties();

			databaseCreationUser = properties.getProperty(
					"databaseCreationUser", "");
			databaseCreationPassword = properties.getProperty(
					"databaseCreationPassword", "");
			testDatabaseName = properties.getProperty("testDatabaseName",
					"cheeporm");

			testUser = properties.getProperty("testUser", "travis");
			testPassword = properties.getProperty("testPassword", "");
		} catch (RuntimeException e) {
			System.out.println("Unable to load database properties: "
					+ e.getMessage());
			System.out.println("Using default values");
		}

		// Create the database if credentials aren't specified in
		// database.properties
		// By default Travis-ci provides a database:
		if (StringUtils.isNotBlank(databaseCreationUser)) {
			Connection connection;

			Class.forName("com.mysql.jdbc.Driver");

			// Set up the database

			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/", databaseCreationUser,
					databaseCreationPassword);

			PreparedStatement createDatabase = connection
					.prepareStatement("create database if not exists "
							+ testDatabaseName + ";");
			try {
				createDatabase.execute();
			} finally {
				createDatabase.close();
			}

			PreparedStatement grant = connection
					.prepareStatement("grant all on " + testDatabaseName
							+ ".* to " + testUser
							+ "@localhost identified by '" + testPassword
							+ "';");
			try {
				grant.execute();
			} finally {
				grant.close();
			}
			connection.close();
		}
	}

	public static void afterSuite() throws ClassNotFoundException, SQLException {

		// Create the database if credentials aren't specified in
		// database.properties
		// By default Travis-ci provides a database:
		if (StringUtils.isNotBlank(databaseCreationUser)) {

			if (useTransaction) {

				Connection connection;

				// Drop the database

				connection = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/", databaseCreationUser,
						databaseCreationPassword);

				PreparedStatement dropDatabase = connection
						.prepareStatement("drop database if exists "
								+ testDatabaseName + ";");
				try {
					dropDatabase.execute();
				} finally {
					dropDatabase.close();
				}
				connection.close();
			}
		}
	}

	/**
	 * @param bean
	 *            The bean for which to create a test table.
	 * @throws Exception
	 *             If creating the Connection fails.
	 */
	public static Connection beforeTest(Object bean) throws Exception {
		Connection connection = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/" + testDatabaseName, testUser,
				testPassword);

		// Create the test table if necessary

		CreateTable createTable = new CreateTable();
		String sql = createTable.createStatement(bean);
		sql = sql.replace("create table", "create table if not exists");
		createTable.createTable(sql, bean, connection);

		// Remove any leftover test data

		PreparedStatement deleteFromTable = connection
				.prepareStatement("delete from " + reflection.getTable(bean)
						+ ";");
		try {
			deleteFromTable.execute();
		} finally {
			deleteFromTable.close();
		}

		// Set up a transaction in order to try and roll back any changes
		if (useTransaction) {
			connection.setAutoCommit(false);
		}

		return connection;
	}

	/**
	 * @param connection
	 *            The database Connection, which will be rolled back.
	 * @throws java.lang.Exception
	 *             If the Connection rollback fails.
	 */
	public static void afterTest(Connection connection) throws Exception {
		if (useTransaction) {
			DbUtils.rollbackAndCloseQuietly(connection);
		} else {
			DbUtils.closeQuietly(connection);
		}
	}

}
