package com.project.orkestra360.domain.enums;

import com.project.orkestra360.exception.BusinessException;

/**
 * Represents the possible priorities of a Task in the system. This enum is used
 * to track the importance of a Task, allowing for clear prioritization and
 * business logic enforcement.
 */
public enum TaskPriority {
	LOW, MEDIUM, HIGH, URGENT;

	/**
	 * Converts a string value to a TaskPriority enum constant. The input string is
	 * case-insensitive and will be trimmed of whitespace. If the input is null or
	 * does not match any valid priority, a BusinessException is thrown.
	 * 
	 * @param value The string representation of the task priority
	 * @return The corresponding TaskPriority enum constant
	 * @throws BusinessException if the input value is null or invalid
	 */
	public static TaskPriority fromString(String value) {
		if (value == null)
			throw new BusinessException("Priority is required");
		try {
			return TaskPriority.valueOf(value.toUpperCase().trim());
		} catch (IllegalArgumentException e) {
			throw new BusinessException("Invalid priority: %s".formatted(value));
		}
	}
}