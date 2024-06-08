package com.everis.homework.dto;

import com.everis.homework.dto.VisualProjectTracking.AlertLevel;

import lombok.Data;

/**
 * 
 * @author sgutierc
 *
 */
@Data
public class Alarm {

	private String name;

	private String type;

	private int level;
	private String levelName;

	private String description;

	private int order;

	public void loadLevel(AlertLevel alertLevel) {
		if (alertLevel == null)
			return;
		setLevel(alertLevel.getValue());
		setLevelName(alertLevel.name());
	}

	public void loadLevel(AlertLevel alertLevel, String description) {
		this.loadLevel(alertLevel);
		this.setDescription(description);
	}

	public boolean isNot(AlertLevel alertLevel) {
		return (alertLevel.getValue() != this.getLevel());
	}
}
