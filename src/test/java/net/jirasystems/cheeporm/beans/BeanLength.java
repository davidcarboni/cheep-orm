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
public class BeanLength {

	@Id
	@Column
	private Integer id;

	@Column(length = 2)
	private String lengthLimited;

	@Column
	private String lengthDefault;

	private String lengthNotColumn;

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
	 * @return the lengthLimited
	 */
	public String getLengthLimited() {
		return lengthLimited;
	}

	/**
	 * @param lengthLimited
	 *            the lengthLimited to set
	 */
	public void setLengthLimited(String lengthLimited) {
		this.lengthLimited = lengthLimited;
	}

	/**
	 * @return the lengthDefault
	 */
	public String getLengthDefault() {
		return lengthDefault;
	}

	/**
	 * @param lengthDefault
	 *            the lengthDefault to set
	 */
	public void setLengthDefault(String lengthDefault) {
		this.lengthDefault = lengthDefault;
	}

	/**
	 * @return the lengthNotColumn
	 */
	public String getLengthNotColumn() {
		return lengthNotColumn;
	}

	/**
	 * @param lengthNotColumn
	 *            the lengthNotColumn to set
	 */
	public void setLengthNotColumn(String lengthNotColumn) {
		this.lengthNotColumn = lengthNotColumn;
	}
}
