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
public class BeanNonMappableField {

	@Id
	@Column
	private Integer id;

	@Column
	private String mappableField;
	private BeanNonMappableField nonMappableField;

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
	 * @return the mappableField
	 */
	public String getMappableField() {
		return mappableField;
	}

	/**
	 * @param mappableField
	 *            the mappableField to set
	 */
	public void setMappableField(String mappableField) {
		this.mappableField = mappableField;
	}

	/**
	 * @return the nonMappableField
	 */
	public BeanNonMappableField getNonMappableField() {
		return nonMappableField;
	}

	/**
	 * @param nonMappableField
	 *            the nonMappableField to set
	 */
	public void setNonMappableField(BeanNonMappableField nonMappableField) {
		this.nonMappableField = nonMappableField;
	}
}
