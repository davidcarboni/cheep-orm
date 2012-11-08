/**
 * 
 */
package net.jirasystems.cheeporm.beans;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author david
 * 
 */
@Table
public class BeanPrecisionScale {

	@Id
	@Column
	private Integer id;

	// Doubles

	@Column
	private Double noneDouble;

	@Column(precision = 2)
	private Double precisionDouble;

	@Column(scale = 4)
	private Double scaleDouble;

	@Column(precision = 2, scale = 4)
	private Double precisionScaleDouble;

	// Floats

	@Column
	private Float noneFloat;

	@Column(precision = 2)
	private Float precisionFloat;

	@Column(scale = 4)
	private Float scaleFloat;

	@Column(precision = 2, scale = 4)
	private Float precisionScaleFloat;

	// Integers

	@Column
	private Integer noneInteger;

	@Column(precision = 2)
	private Integer precisionInteger;

	@Column(scale = 4)
	private Integer scaleInteger;

	@Column(precision = 2, scale = 4)
	private Integer precisionScaleInteger;

	// Longs

	@Column
	private Long noneLong;

	@Column(precision = 2)
	private Long precisionLong;

	@Column(scale = 4)
	private Long scaleLong;

	@Column(precision = 2, scale = 4)
	private Long precisionScaleLong;

	// Shorts

	@Column
	private Short noneShort;

	@Column(precision = 2)
	private Short precisionShort;

	@Column(scale = 4)
	private Short scaleShort;

	@Column(precision = 2, scale = 4)
	private Short precisionScaleShort;

	// Bytes

	@Column
	private Byte noneByte;

	@Column(precision = 2)
	private Byte precisionByte;

	@Column(scale = 4)
	private Byte scaleByte;

	@Column(precision = 2, scale = 4)
	private Byte precisionScaleByte;

	// BigDecimals

	@Column
	private BigDecimal noneBigDecimal;

	@Column(precision = 2)
	private BigDecimal precisionBigDecimal;

	@Column(scale = 4)
	private BigDecimal scaleBigDecimal;

	@Column(precision = 2, scale = 4)
	private BigDecimal precisionScaleBigDecimal;

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
	 * @return the noneDouble
	 */
	public Double getNoneDouble() {
		return noneDouble;
	}

	/**
	 * @param noneDouble
	 *            the noneDouble to set
	 */
	public void setNoneDouble(Double noneDouble) {
		this.noneDouble = noneDouble;
	}

	/**
	 * @return the precisionDouble
	 */
	public Double getPrecisionDouble() {
		return precisionDouble;
	}

	/**
	 * @param precisionDouble
	 *            the precisionDouble to set
	 */
	public void setPrecisionDouble(Double precisionDouble) {
		this.precisionDouble = precisionDouble;
	}

	/**
	 * @return the scaleDouble
	 */
	public Double getScaleDouble() {
		return scaleDouble;
	}

	/**
	 * @param scaleDouble
	 *            the scaleDouble to set
	 */
	public void setScaleDouble(Double scaleDouble) {
		this.scaleDouble = scaleDouble;
	}

	/**
	 * @return the precisionScaleDouble
	 */
	public Double getPrecisionScaleDouble() {
		return precisionScaleDouble;
	}

	/**
	 * @param precisionScaleDouble
	 *            the precisionScaleDouble to set
	 */
	public void setPrecisionScaleDouble(Double precisionScaleDouble) {
		this.precisionScaleDouble = precisionScaleDouble;
	}

	/**
	 * @return the noneFloat
	 */
	public Float getNoneFloat() {
		return noneFloat;
	}

	/**
	 * @param noneFloat
	 *            the noneFloat to set
	 */
	public void setNoneFloat(Float noneFloat) {
		this.noneFloat = noneFloat;
	}

	/**
	 * @return the precisionFloat
	 */
	public Float getPrecisionFloat() {
		return precisionFloat;
	}

	/**
	 * @param precisionFloat
	 *            the precisionFloat to set
	 */
	public void setPrecisionFloat(Float precisionFloat) {
		this.precisionFloat = precisionFloat;
	}

	/**
	 * @return the scaleFloat
	 */
	public Float getScaleFloat() {
		return scaleFloat;
	}

	/**
	 * @param scaleFloat
	 *            the scaleFloat to set
	 */
	public void setScaleFloat(Float scaleFloat) {
		this.scaleFloat = scaleFloat;
	}

	/**
	 * @return the precisionScaleFloat
	 */
	public Float getPrecisionScaleFloat() {
		return precisionScaleFloat;
	}

	/**
	 * @param precisionScaleFloat
	 *            the precisionScaleFloat to set
	 */
	public void setPrecisionScaleFloat(Float precisionScaleFloat) {
		this.precisionScaleFloat = precisionScaleFloat;
	}

	/**
	 * @return the noneInteger
	 */
	public Integer getNoneInteger() {
		return noneInteger;
	}

	/**
	 * @param noneInteger
	 *            the noneInteger to set
	 */
	public void setNoneInteger(Integer noneInteger) {
		this.noneInteger = noneInteger;
	}

	/**
	 * @return the precisionInteger
	 */
	public Integer getPrecisionInteger() {
		return precisionInteger;
	}

	/**
	 * @param precisionInteger
	 *            the precisionInteger to set
	 */
	public void setPrecisionInteger(Integer precisionInteger) {
		this.precisionInteger = precisionInteger;
	}

	/**
	 * @return the scaleInteger
	 */
	public Integer getScaleInteger() {
		return scaleInteger;
	}

	/**
	 * @param scaleInteger
	 *            the scaleInteger to set
	 */
	public void setScaleInteger(Integer scaleInteger) {
		this.scaleInteger = scaleInteger;
	}

	/**
	 * @return the precisionScaleInteger
	 */
	public Integer getPrecisionScaleInteger() {
		return precisionScaleInteger;
	}

	/**
	 * @param precisionScaleInteger
	 *            the precisionScaleInteger to set
	 */
	public void setPrecisionScaleInteger(Integer precisionScaleInteger) {
		this.precisionScaleInteger = precisionScaleInteger;
	}

	/**
	 * @return the noneLong
	 */
	public Long getNoneLong() {
		return noneLong;
	}

	/**
	 * @param noneLong
	 *            the noneLong to set
	 */
	public void setNoneLong(Long noneLong) {
		this.noneLong = noneLong;
	}

	/**
	 * @return the precisionLong
	 */
	public Long getPrecisionLong() {
		return precisionLong;
	}

	/**
	 * @param precisionLong
	 *            the precisionLong to set
	 */
	public void setPrecisionLong(Long precisionLong) {
		this.precisionLong = precisionLong;
	}

	/**
	 * @return the scaleLong
	 */
	public Long getScaleLong() {
		return scaleLong;
	}

	/**
	 * @param scaleLong
	 *            the scaleLong to set
	 */
	public void setScaleLong(Long scaleLong) {
		this.scaleLong = scaleLong;
	}

	/**
	 * @return the precisionScaleLong
	 */
	public Long getPrecisionScaleLong() {
		return precisionScaleLong;
	}

	/**
	 * @param precisionScaleLong
	 *            the precisionScaleLong to set
	 */
	public void setPrecisionScaleLong(Long precisionScaleLong) {
		this.precisionScaleLong = precisionScaleLong;
	}

	/**
	 * @return the noneShort
	 */
	public Short getNoneShort() {
		return noneShort;
	}

	/**
	 * @param noneShort
	 *            the noneShort to set
	 */
	public void setNoneShort(Short noneShort) {
		this.noneShort = noneShort;
	}

	/**
	 * @return the precisionShort
	 */
	public Short getPrecisionShort() {
		return precisionShort;
	}

	/**
	 * @param precisionShort
	 *            the precisionShort to set
	 */
	public void setPrecisionShort(Short precisionShort) {
		this.precisionShort = precisionShort;
	}

	/**
	 * @return the scaleShort
	 */
	public Short getScaleShort() {
		return scaleShort;
	}

	/**
	 * @param scaleShort
	 *            the scaleShort to set
	 */
	public void setScaleShort(Short scaleShort) {
		this.scaleShort = scaleShort;
	}

	/**
	 * @return the precisionScaleShort
	 */
	public Short getPrecisionScaleShort() {
		return precisionScaleShort;
	}

	/**
	 * @param precisionScaleShort
	 *            the precisionScaleShort to set
	 */
	public void setPrecisionScaleShort(Short precisionScaleShort) {
		this.precisionScaleShort = precisionScaleShort;
	}

	/**
	 * @return the noneByte
	 */
	public Byte getNoneByte() {
		return noneByte;
	}

	/**
	 * @param noneByte
	 *            the noneByte to set
	 */
	public void setNoneByte(Byte noneByte) {
		this.noneByte = noneByte;
	}

	/**
	 * @return the precisionByte
	 */
	public Byte getPrecisionByte() {
		return precisionByte;
	}

	/**
	 * @param precisionByte
	 *            the precisionByte to set
	 */
	public void setPrecisionByte(Byte precisionByte) {
		this.precisionByte = precisionByte;
	}

	/**
	 * @return the scaleByte
	 */
	public Byte getScaleByte() {
		return scaleByte;
	}

	/**
	 * @param scaleByte
	 *            the scaleByte to set
	 */
	public void setScaleByte(Byte scaleByte) {
		this.scaleByte = scaleByte;
	}

	/**
	 * @return the precisionScaleByte
	 */
	public Byte getPrecisionScaleByte() {
		return precisionScaleByte;
	}

	/**
	 * @param precisionScaleByte
	 *            the precisionScaleByte to set
	 */
	public void setPrecisionScaleByte(Byte precisionScaleByte) {
		this.precisionScaleByte = precisionScaleByte;
	}

	/**
	 * @return the noneBigDecimal
	 */
	public BigDecimal getNoneBigDecimal() {
		return noneBigDecimal;
	}

	/**
	 * @param noneBigDecimal
	 *            the noneBigDecimal to set
	 */
	public void setNoneBigDecimal(BigDecimal noneBigDecimal) {
		this.noneBigDecimal = noneBigDecimal;
	}

	/**
	 * @return the precisionBigDecimal
	 */
	public BigDecimal getPrecisionBigDecimal() {
		return precisionBigDecimal;
	}

	/**
	 * @param precisionBigDecimal
	 *            the precisionBigDecimal to set
	 */
	public void setPrecisionBigDecimal(BigDecimal precisionBigDecimal) {
		this.precisionBigDecimal = precisionBigDecimal;
	}

	/**
	 * @return the scaleBigDecimal
	 */
	public BigDecimal getScaleBigDecimal() {
		return scaleBigDecimal;
	}

	/**
	 * @param scaleBigDecimal
	 *            the scaleBigDecimal to set
	 */
	public void setScaleBigDecimal(BigDecimal scaleBigDecimal) {
		this.scaleBigDecimal = scaleBigDecimal;
	}

	/**
	 * @return the precisionScaleBigDecimal
	 */
	public BigDecimal getPrecisionScaleBigDecimal() {
		return precisionScaleBigDecimal;
	}

	/**
	 * @param precisionScaleBigDecimal
	 *            the precisionScaleBigDecimal to set
	 */
	public void setPrecisionScaleBigDecimal(BigDecimal precisionScaleBigDecimal) {
		this.precisionScaleBigDecimal = precisionScaleBigDecimal;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
}
