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
public class BeanInsertable {

	@Id
	@Column
	private Integer id;

	@Column(insertable = false)
	private String notInsertable;

	@Column(insertable = true)
	private String insertable;

	/**
	 * @return the id
	 */
	public int getId() {
		return id.intValue();
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = Integer.valueOf(id);
	}

	/**
	 * @return the notInsertable
	 */
	public String getNotInsertable() {
		return notInsertable;
	}

	/**
	 * @param notInsertable
	 *            the notInsertable to set
	 */
	public void setNotInsertable(String notInsertable) {
		this.notInsertable = notInsertable;
	}

	/**
	 * @return the insertable
	 */
	public String getInsertable() {
		return insertable;
	}

	/**
	 * @param insertable
	 *            the insertable to set
	 */
	public void setInsertable(String insertable) {
		this.insertable = insertable;
	}
}
