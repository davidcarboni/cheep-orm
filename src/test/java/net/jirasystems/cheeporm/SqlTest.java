/**
 * 
 */
package net.jirasystems.cheeporm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import net.jirasystems.cheeporm.beans.ExampleBean;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author david
 * 
 */
public class SqlTest {

	private Connection connection;
	private Reflection reflection = new Reflection();

	private Sql sql = new Sql();
	private BeanTable bean = new BeanTable();
	private String tableName = reflection.getTable(bean);

	private List<Field> fields = reflection.listFields(bean);
	private String name = reflection.getColumnName(fields.get(0));
	private String description = reflection.getColumnName(fields.get(1));

	private List<Field> keys = reflection.listKeys(bean);
	private String id = reflection.getColumnName(keys.get(0));

	/**
	 * @throws java.lang.Exception
	 *             If any error occurs.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Database.beforeSuite();
	}

	/**
	 * @throws java.lang.Exception
	 *             If any error occurs.
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Database.afterSuite();
	}

	/**
	 * @throws java.lang.Exception
	 *             If any error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		connection = Database.beforeTest(new ExampleBean());
	}

	/**
	 * @throws java.lang.Exception
	 *             If any error occurs.
	 */
	@After
	public void tearDown() throws Exception {
		Database.afterTest(connection);
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Sql#Sql()}.
	 */
	@Test
	public void testSql() {

		// Given

		// When
		Sql sql = new Sql();

		// Then
		Assert.assertNotNull(sql.getReflection());
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Sql#insert(java.lang.String, java.util.List)}
	 * .
	 */
	@Test
	public void testInsert() {

		// Given

		// When
		String query = sql.insert(tableName, fields);

		// Then
		String regex = "insert  into  " + tableName + " ( " + name + " , " + description + " ) values ( ? , ? )";
		Assert.assertTrue(query.toLowerCase().startsWith("insert"));
		Assert.assertTrue(query.contains(tableName));
		Assert.assertTrue(query.contains(name));
		Assert.assertTrue(query.contains(description));
		Assert.assertTrue(checkString(regex, query));
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Sql#select(java.lang.String, java.util.List)}
	 * .
	 */
	@Test
	public void testSelect() {

		// Given

		// When
		String query = sql.select(tableName, fields);

		// Then
		String regex = "select  *  from  " + tableName;
		Assert.assertTrue(query.toLowerCase().startsWith("select"));
		Assert.assertTrue(query.contains("from"));
		Assert.assertTrue(query.contains(tableName));
		Assert.assertTrue(checkString(regex, query));
	}

	/**
	 * Test method for
	 * {@link net.jirasystems.cheeporm.Sql#update(java.lang.String, java.util.List, java.util.List)}
	 * .
	 */
	@Test
	public void testUpdate() {

		// Given

		// When
		String query = sql.update(tableName, fields, keys);

		// Then
		String regex = "update  " + tableName + "  set  " + name + " = ? , " + description + " = ?  where  " + id
				+ " = ?";
		Assert.assertTrue(query.toLowerCase().startsWith("update"));
		Assert.assertTrue(query.contains("set"));
		Assert.assertTrue(query.contains("where"));
		Assert.assertTrue(query.contains(tableName));
		Assert.assertTrue(query.contains(name));
		Assert.assertTrue(query.contains(description));
		Assert.assertTrue(query.contains(id));
		Assert.assertTrue(checkString(regex, query));
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Sql#delete(java.lang.String, java.util.List)}
	 * .
	 */
	@Test
	public void testDelete() {

		// Given

		// When
		String query = sql.delete(tableName, keys);

		// Then
		String regex = "delete  from  " + tableName + "  where  " + id + " = ?";
		Assert.assertTrue(query.toLowerCase().startsWith("delete"));
		Assert.assertTrue(query.contains("from"));
		Assert.assertTrue(query.contains("where"));
		Assert.assertTrue(query.contains(tableName));
		Assert.assertTrue(query.contains(id));
		Assert.assertTrue(checkString(regex, query));
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Sql#list(java.lang.String, java.util.List)} .
	 */
	@Test
	public void testList() {

		// Given

		// When
		String query = sql.list(tableName, fields, new ArrayList<Field>());

		// Then
		String regex = "select  *  from  " + tableName + "  where  " + name + " = ?  and  " + description + " = ?";
		Assert.assertTrue(query.toLowerCase().startsWith("select"));
		Assert.assertTrue(query.contains("from"));
		Assert.assertTrue(query.contains("where"));
		Assert.assertTrue(query.contains("and"));
		Assert.assertTrue(query.contains(tableName));
		Assert.assertTrue(query.contains(name));
		Assert.assertTrue(query.contains(description));
		Assert.assertTrue(checkString(regex, query));
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Sql#list(java.lang.String, java.util.List)} .
	 */
	@Test
	public void testListOrderBy() {

		// Given

		// When
		ArrayList<Field> orderBy = new ArrayList<Field>();
		Field descriptionField = fields.get(1);
		orderBy.add(descriptionField);
		String query = sql.list(tableName, fields, orderBy);

		// Then
		String regex = "select  *  from  " + tableName + "  where  " + name + " = ?  and  " + description + " = ?"
				+ "  order  by  " + description;
		Assert.assertTrue(query.toLowerCase().startsWith("select"));
		Assert.assertTrue(query.contains("from"));
		Assert.assertTrue(query.contains("where"));
		Assert.assertTrue(query.contains("and"));
		Assert.assertTrue(query.contains(tableName));
		Assert.assertTrue(query.contains(name));
		Assert.assertTrue(query.contains(description));
		Assert.assertTrue(query.contains("order by"));
		Assert.assertTrue(checkString(regex, query));
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Sql#sqlTypeName(java.lang.Class)}.
	 */
	@Test
	public void testSqlTypeName() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.jirasystems.cheeporm.Sql#quoted(java.lang.Object)}.
	 */
	@Test
	public void testQuoted() {
		// fail("Not yet implemented");
	}

	/**
	 * This method checks that the given string matches the given simplified regex.
	 * 
	 * @param simplifiedRegex
	 *            A String containing a simplified regex. The string will be matched literally, but
	 *            with flexible white space. A double space means one or more whitespace characters
	 *            and a single space means zero or more whitespace characters.
	 * @param toBeChecked
	 *            The string to be checked against the simplified regex.
	 * @return If the String to be checked matches, true. Otherwise false.
	 */
	private static boolean checkString(String simplifiedRegex, String toBeChecked) {

		String regex = simplifiedRegex;

		// Transform the input String into a proper regex:
		regex = regex.replace("*", "\\*");
		regex = regex.replace("(", "\\(");
		regex = regex.replace(")", "\\)");
		regex = regex.replace("?", "\\?");
		// Double space indicates at least one space must be present
		regex = regex.replace("  ", "[\\s]+");
		// Single space indicates zero or more spaces are valid
		regex = regex.replace(" ", "[\\s]*");

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(toBeChecked);

		return matcher.find();
	}

}
