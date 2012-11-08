/**
 * 
 */
package net.jirasystems.cheeporm.beans;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author david
 * 
 */
@Table
public class BeanMultipleId {

	@Id
	@Column
	private Integer id1;

	@Id
	@Column
	private Integer id2;

	@Column
	private String name;

	/**
	 * @return the id1
	 */
	public int getId1() {
		return id1;
	}

	/**
	 * @param id1
	 *            the id1 to set
	 */
	public void setId1(int id1) {
		this.id1 = id1;
	}

	/**
	 * @return the id2
	 */
	public int getId2() {
		return id2;
	}

	/**
	 * @param id2
	 *            the id2 to set
	 */
	public void setId2(int id2) {
		this.id2 = id2;
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
