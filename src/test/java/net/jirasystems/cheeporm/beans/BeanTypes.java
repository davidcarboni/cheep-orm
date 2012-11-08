/**
 * 
 */
package net.jirasystems.cheeporm.beans;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author david
 * 
 */
@Table
public class BeanTypes {

	@Id
	@Column
	private Integer id;

	@Column
	private String stringValue;

	@Column
	private BigDecimal bigDecimalValue;

	@Column
	private Boolean booleanValue;

	@Column
	private Byte byteValue;

	@Column
	private Short shortValue;

	@Column
	private Integer integerValue;

	@Column
	private Long longValue;

	@Column
	private Float floatValue;

	@Column
	private Double doubleValue;

	@Column
	private Date dateValue;

	@Column
	private Time timeValue;

	@Column
	private Timestamp timestampValue;

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
	 * @return the stringValue
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * @param stringValue
	 *            the stringValue to set
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * @return the bigDecimalValue
	 */
	public BigDecimal getBigDecimalValue() {
		return bigDecimalValue;
	}

	/**
	 * @param bigDecimalValue
	 *            the bigDecimalValue to set
	 */
	public void setBigDecimalValue(BigDecimal bigDecimalValue) {
		this.bigDecimalValue = bigDecimalValue;
	}

	/**
	 * @return the booleanValue
	 */
	public Boolean getBooleanValue() {
		return booleanValue;
	}

	/**
	 * @param booleanValue
	 *            the booleanValue to set
	 */
	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	/**
	 * @return the byteValue
	 */
	public Byte getByteValue() {
		return byteValue;
	}

	/**
	 * @param byteValue
	 *            the byteValue to set
	 */
	public void setByteValue(Byte byteValue) {
		this.byteValue = byteValue;
	}

	/**
	 * @return the shortValue
	 */
	public Short getShortValue() {
		return shortValue;
	}

	/**
	 * @param shortValue
	 *            the shortValue to set
	 */
	public void setShortValue(Short shortValue) {
		this.shortValue = shortValue;
	}

	/**
	 * @return the integerValue
	 */
	public Integer getIntegerValue() {
		return integerValue;
	}

	/**
	 * @param integerValue
	 *            the integerValue to set
	 */
	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	/**
	 * @return the longValue
	 */
	public Long getLongValue() {
		return longValue;
	}

	/**
	 * @param longValue
	 *            the longValue to set
	 */
	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	/**
	 * @return the floatValue
	 */
	public Float getFloatValue() {
		return floatValue;
	}

	/**
	 * @param floatValue
	 *            the floatValue to set
	 */
	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
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

	/**
	 * @return the dateValue
	 */
	public Date getDateValue() {
		return dateValue;
	}

	/**
	 * @param dateValue
	 *            the dateValue to set
	 */
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	/**
	 * @return the timeValue
	 */
	public Time getTimeValue() {
		return timeValue;
	}

	/**
	 * @param timeValue
	 *            the timeValue to set
	 */
	public void setTimeValue(Time timeValue) {
		this.timeValue = timeValue;
	}

	/**
	 * @return the timestampValue
	 */
	public Timestamp getTimestampValue() {
		return timestampValue;
	}

	/**
	 * @param timestampValue
	 *            the timestampValue to set
	 */
	public void setTimestampValue(Timestamp timestampValue) {
		this.timestampValue = timestampValue;
	}
}
