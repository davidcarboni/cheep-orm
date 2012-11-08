/**
 * 
 */
package net.jirasystems.cheeporm.beans;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author david
 * 
 */
@Table(name = "example_bean")
public class ExampleBean {

	public static enum Select {
		a, b, c;
	}

	@Id
	@Column(name = "id")
	private Integer id;

	@Id
	@Column(name = "id2")
	private Integer id2 = 12;

	@Column(name = "name", columnDefinition = "varchar(10) not null")
	private String name;

	@Column(name = "description", unique = true)
	private String description;

	@Column(name = "bean_number", updatable = false)
	private Integer beanNumber;

	@Column(insertable = false)
	private Timestamp beanTimestamp;

	@Column(nullable = false, precision = 2, scale = 4)
	private Double doubleValue = 12.54;

	@Column()
	private Select selection;

	private ExampleBean notAField;

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + id + "/" + id2 + ") " + name + ", " + description + ". "
				+ beanNumber + " [" + beanTimestamp + "]";
	}

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

	/**
	 * @return the beanNumber
	 */
	public Integer getBeanNumber() {
		return beanNumber;
	}

	/**
	 * @param beanNumber
	 *            the beanNumber to set
	 */
	public void setBeanNumber(Integer beanNumber) {
		this.beanNumber = beanNumber;
	}

	/**
	 * @return the notAField
	 */
	public ExampleBean getNotAField() {
		return notAField;
	}

	/**
	 * @param notAField
	 *            the notAField to set
	 */
	public void setNotAField(ExampleBean notAField) {
		this.notAField = notAField;
	}

	/**
	 * @return the id2
	 */
	public Integer getId2() {
		return id2;
	}

	/**
	 * @param id2
	 *            the id2 to set
	 */
	public void setId2(Integer id2) {
		this.id2 = id2;
	}

	/**
	 * @return the beanTimestamp
	 */
	public Date getBeanTimestamp() {
		return beanTimestamp;
	}

	/**
	 * @param beanTimestamp
	 *            the beanTimestamp to set
	 */
	public void setBeanTimestamp(Date beanTimestamp) {
		this.beanTimestamp = new Timestamp(beanTimestamp.getTime());
	}

	/**
	 * @return the selection
	 */
	public Select getSelection() {
		return selection;
	}

	/**
	 * @param selection
	 *            the selection to set
	 */
	public void setSelection(Select selection) {
		this.selection = selection;
	}

	/**
	 * @return the doubleValue
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}

	/**
	 * @param doubleValue
	 *            the doubleValue to set
	 */
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

}
