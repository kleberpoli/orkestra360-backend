package com.project.orkestra360.domain.enums;

import com.project.orkestra360.exception.BusinessException;

/**
 * Represents the possible statuses of a Task in the system. This enum is used
 * to track the lifecycle of a Task, allowing for clear state management and
 * business logic enforcement.
 */
public enum TaskStatus {
	TODO, DOING, DONE, ARCHIVED;

	public static TaskStatus fromString(String value) {
		if (value == null)
			throw new BusinessException("Status is required");
		try {
			return TaskStatus.valueOf(value.toUpperCase().trim());
		} catch (IllegalArgumentException e) {
			throw new BusinessException("Invalid task status: " + value);
		}
	}
}