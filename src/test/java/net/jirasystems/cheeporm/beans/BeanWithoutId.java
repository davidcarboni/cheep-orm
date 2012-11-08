/**
 * 
 */
package net.jirasystems.cheeporm.beans;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author david
 * 
 */
@Table
public class BeanWithoutId {

	@Column
	private int notid;

	@Column
	private String name;

	/**
	 * @return the notid
	 */
	public int getNotid() {
		return notid;
	}

	/**
	 * @param notid
	 *            the notid to set
	 */
	public void setNotid(int notid) {
		this.notid = notid;
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
