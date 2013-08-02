package net.jirasystems.cheeporm.beans;

import javax.persistence.Column;
import javax.persistence.Table;

@Table
public class BeanInheritSubclass extends BeanInheritSuperclass {

	@Column
	private String description;

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
