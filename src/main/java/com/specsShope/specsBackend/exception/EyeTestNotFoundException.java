package com.specsShope.specsBackend.exception;

public class EyeTestNotFoundException extends RuntimeException {
  public EyeTestNotFoundException(String id) {
    super("Eye test not found: " + id);
  }
}
