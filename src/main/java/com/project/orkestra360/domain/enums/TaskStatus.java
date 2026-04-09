package com.project.orkestra360.domain.enums;

import com.project.orkestra360.exception.BusinessException;

/**
 * Represents the possible statuses of a Task in the system. This enum is used
 * to track the lifecycle of a Task, allowing for clear state management and
 * business logic enforcement.
 */
public enum TaskStatus {
	TODO, DOING, DONE, ARCHIVED;

	/**
	 * Converts a string value to a TaskStatus enum constant. The input string is
	 * case-insensitive and will be trimmed of whitespace. If the input is null or
	 * does not match any valid status, a BusinessException is thrown.
	 * 
	 * @param value The string representation of the task status
	 * @return The corresponding TaskStatus enum constant
	 * @throws BusinessException if the input value is null or invalid
	 */
	public static TaskStatus fromString(String value) {
		if (value == null) {
			throw new BusinessException("Status is required");
		}
		try {
			return TaskStatus.valueOf(value.toUpperCase().trim());
		} catch (IllegalArgumentException e) {
			throw new BusinessException("Invalid task status: %s".formatted(value));
		}
	}

	/**
	 * Determines if a transition from the current status to the target status is
	 * valid according to the defined workflow rules.
	 * 
	 * @param target The target status to transition to
	 * @return true if the transition is valid, false otherwise
	 */
	public boolean canTransitionTo(TaskStatus target) {
		if (target == null) {
			return false;
		}
		if (this == target) {
			return true;
		}
		return switch (this) {
			case TODO -> target == DOING || target == DONE || target == ARCHIVED;
			case DOING -> target == TODO || target == DONE || target == ARCHIVED;
			case DONE -> target == ARCHIVED;
			case ARCHIVED -> false;
		};
	}
}