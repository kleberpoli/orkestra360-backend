package com.project.orkestra360.domain.enums;

import com.project.orkestra360.exception.BusinessException;

/**
 * Represents the possible priorities of a Task in the system. This enum is used
 * to track the importance of a Task, allowing for clear prioritization and
 * business logic enforcement.
 */
public enum TaskPriority {
	LOW, MEDIUM, HIGH, URGENT;

	public static TaskPriority fromString(String value) {
		if (value == null)
			throw new BusinessException("Priority is required");
		try {
			return TaskPriority.valueOf(value.toUpperCase().trim());
		} catch (IllegalArgumentException e) {
			throw new BusinessException("Invalid priority: " + value);
		}
	}
}