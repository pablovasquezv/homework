package com.everis.homework.mapper;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Modification {
	@Column(name = "LAST_MODIFIED")
	private Date lastModified;
	
	@Column(name = "USERNAME")
	private String username;
	
	public Modification() {
		
	}
	
	public Modification(Date lastModified,String username) {
		this.lastModified=lastModified;
		this.username=username;
	}
	
	
}
