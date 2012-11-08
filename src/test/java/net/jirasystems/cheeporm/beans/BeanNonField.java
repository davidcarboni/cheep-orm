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
public class BeanNonField {

	@Id
	@Column
	private Integer id;

	@Column
	private String isColumn;

	private String nonColumn;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the nonColumn
	 */
	public String getNonColumn() {
		return nonColumn;
	}

	/**
	 * @param nonColumn
	 *            the nonColumn to set
	 */
	public void setNonColumn(String nonColumn) {
		this.nonColumn = nonColumn;
	}

	/**
	 * @return the isColumn
	 */
	public String getIsColumn() {
		return isColumn;
	}

	/**
	 * @param isColumn
	 *            the isColumn to set
	 */
	public void setIsColumn(String isColumn) {
		this.isColumn = isColumn;
	}
}
