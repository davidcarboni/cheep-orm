/**
 * 
 */
package net.jirasystems.cheeporm.beans;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This is a minimal reference bean. It declares an Integer ID and a String name, marking these as
 * {@link Column} and the ID as {@link Id}. The class itself is marked as {@link Table}. No
 * parameters are given for any of these annotations, so this provides a lightweight base bean
 * configuration.
 * 
 * @author david
 * 
 */
@Table
public class BeanMinimal {

	@Id
	@Column
	private Integer id;

	@Column
	private String name;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
