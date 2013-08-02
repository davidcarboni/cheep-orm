package net.jirasystems.cheeporm.beans;

import net.jirasystems.cheeporm.test.BeanTester;

import org.junit.Test;

public class BeanInheritSuperclassTest {

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
		beanTester.testBean(new BeanInheritSuperclass());
	}

}
