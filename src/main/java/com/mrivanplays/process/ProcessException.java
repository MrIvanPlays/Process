package com.mrivanplays.process;

/**
 * Represents an exception, which is used to hold exceptions, thrown by {@link Process Processes}
 *
 * @since 0.0.1
 * @author <a href="mailto:ivan@mrivanplays.com">Ivan Pekov</a>
 */
public class ProcessException extends RuntimeException {

  public ProcessException(String message) {
    super(message);
  }

  public ProcessException(String message, Throwable stack) {
    super(message, stack);
  }

}
