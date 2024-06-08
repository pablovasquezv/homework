package com.everis.homework.mapper;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.everis.homework.dto.Matches;

import lombok.Data;

@Data
@Embeddable
public class ServiceFields implements Matches {
	/** The percentage est delivered. */
	@Column(name = "PER_EST_DELIVERED")
	private BigDecimal percentageEstDelivered;

	/** The percentage req resolved. */
	@Column(name = "PER_REQ_RESOLVED")
	private BigDecimal percentageReqResolved;

	/** The total est requested. */
	@Column(name = "TOTAL_EST_REQUESTED")
	private Integer totalEstRequested;

	/** The num est delivered. */
	@Column(name = "NUM_EST_DELIVERED")
	private Integer numEstDelivered;

	/** The total req requested. */
	@Column(name = "TOTAL_REQ_REQUESTED")
	private Integer totalReqRequested;

	/** The num req delivered. */
	@Column(name = "NUM_REQ_DELIVERED")
	private Integer numReqDelivered;
	
	public boolean matches(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			obj=new ServiceFields();
		if (getClass() != obj.getClass())
			return false;
		ServiceFields other = (ServiceFields) obj;
		
		if (numEstDelivered == null) {
			if (other.numEstDelivered != null)
				return false;
		} else if (!numEstDelivered.equals(other.numEstDelivered))
			return false;
		if (numReqDelivered == null) {
			if (other.numReqDelivered != null)
				return false;
		} else if (!numReqDelivered.equals(other.numReqDelivered))
			return false;
		if (totalEstRequested == null) {
			if (other.totalEstRequested != null)
				return false;
		} else if (!totalEstRequested.equals(other.totalEstRequested))
			return false;
		if (totalReqRequested == null) {
			if (other.totalReqRequested != null)
				return false;
		} else if (!totalReqRequested.equals(other.totalReqRequested))
			return false;
		
		
		return true;
	}
}
