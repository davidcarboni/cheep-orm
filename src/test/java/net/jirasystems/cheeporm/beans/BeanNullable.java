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
public class BeanNullable {

	@Id
	@Column
	private Integer id;

	@Column(nullable = false)
	private String nonNullableField;

	@Column(nullable = true)
	private String nullableField;

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
	 * @return the nonNullableField
	 */
	public String getNonNullableField() {
		return nonNullableField;
	}

	/**
	 * @param nonNullableField
	 *            the nonNullableField to set
	 */
	public void setNonNullableField(String nonNullableField) {
		this.nonNullableField = nonNullableField;
	}

	/**
	 * @return the NullableField
	 */
	public String getNullableField() {
		return nullableField;
	}

	/**
	 * @param nullableField
	 *            the nullableField to set
	 */
	public void setNullableField(String nullableField) {
		this.nullableField = nullableField;
	}
}
