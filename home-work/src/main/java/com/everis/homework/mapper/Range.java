package com.everis.homework.mapper;

import java.math.BigDecimal;

import javax.persistence.Embeddable;

import com.everis.homework.dto.Matches;

import lombok.Data;

@Data
@Embeddable
public class Range implements Matches{
	BigDecimal top;
	BigDecimal bottom;

	public Range() {
	}

	public Range(BigDecimal top, BigDecimal bottom) {
		this.top = top;
		this.bottom = bottom;
	}

	public Range(int top, int bottom) {
		this.top = new BigDecimal(top);
		this.bottom = new BigDecimal(bottom);
	}

	public boolean matches(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Range other = (Range) obj;
		if (bottom == null) {
			if (other.bottom != null)
				return false;
		} else if (bottom.compareTo(other.bottom)!=0)
			return false;
		if (top == null) {
			if (other.top != null)
				return false;
		} else if (top.compareTo(other.top)!=0)
			return false;
		return true;
	}
	
	
}
