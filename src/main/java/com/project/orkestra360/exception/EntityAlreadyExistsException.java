package com.project.orkestra360.exception;

public class EntityAlreadyExistsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Custom unchecked exception used to signal that an attempt was made to create
   * an entity that already exists, violating uniqueness constraints.
   *
   * @param message A descriptive error message explaining the specific entity
   *                that already exists and the context of the violation, such as
   *                the unique identifier or attributes that caused the conflict.
   */
  public EntityAlreadyExistsException(String message) {
    super(message);
  }
}