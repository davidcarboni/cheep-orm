package net.jirasystems.cheeporm.beans;

import junit.framework.Assert;
import net.jirasystems.cheeporm.test.BeanTester;

import org.junit.Test;

/**
 * Test for {@link ExampleBean}.
 * 
 * @author David Carboni
 * 
 */
public class ExampleBeanTest {

	/**
	 * This method exercises all of the getters and setters of the bean to assure that the beans are
	 * correct and to clean up test coverage as not all of the methods in the beans are called
	 * explicitly by the tests.
	 * 
	 * @throws Exception
	 *             If the bean test throws any error
	 */
	@Test
	public void testCoverage() throws Exception {

		BeanTester beanTester = new BeanTester();
		beanTester.testBean(new ExampleBean());

		// Exercise the toString method.

		final Integer id1 = Integer.valueOf(2);
		final Integer id2 = Integer.valueOf(3);
		final String name = "testCoverage";
		final String description = "description";
		final Integer beanNumber = Integer.valueOf(4);

		ExampleBean exampleBean = new ExampleBean();
		exampleBean.setId(id1);
		exampleBean.setId2(id2);
		exampleBean.setName(name);
		exampleBean.setDescription(description);
		exampleBean.setBeanNumber(beanNumber);

		String string = exampleBean.toString();

		Assert.assertTrue(string.contains(String.valueOf(id1)));
		Assert.assertTrue(string.contains(String.valueOf(id2)));
		Assert.assertTrue(string.contains(String.valueOf(name)));
		Assert.assertTrue(string.contains(String.valueOf(description)));
		Assert.assertTrue(string.contains(String.valueOf(beanNumber)));
	}

}
