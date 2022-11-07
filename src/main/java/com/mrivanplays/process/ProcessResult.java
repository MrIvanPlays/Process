package com.mrivanplays.process;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a result of a {@link ResultedProcess}
 *
 * @param <T> type parameter
 * @author <a href="mailto:ivan@mrivanplays.com">Ivan Pekov</a>
 * @since 0.0.1
 */
public final class ProcessResult<T> {

  /**
   * Create a successful {@code ProcessResult}
   *
   * @param value result value
   * @return new process result
   * @param <T> type parameter
   */
  public static <T> ProcessResult<T> success(T value) {
    return new ProcessResult<>(
        Optional.of(Objects.requireNonNull(value, "value")), Optional.empty());
  }

  /**
   * Create a failure {@code ProcessResult}
   *
   * @param error why has the process failed
   * @return new process result
   * @param <T> type parameter
   */
  public static <T> ProcessResult<T> failure(ProcessException error) {
    return new ProcessResult<>(
        Optional.empty(), Optional.of(Objects.requireNonNull(error, "error")));
  }

  /**
   * Returns whether this {@code ProcessResult} is successful.
   *
   * @return true if successful
   */
  public boolean isSuccessful() {
    return this.value.isPresent() && !this.error.isPresent();
  }

  /**
   * Returns the resulted value if present.
   *
   * @return resulted value
   */
  public Optional<T> getValue() {
    return this.value;
  }

  /**
   * Returns the {@link ProcessException} error if present.
   *
   * @return error
   */
  public Optional<ProcessException> getError() {
    return this.error;
  }

  // ===========================================

  private final Optional<T> value;
  private final Optional<ProcessException> error;

  private ProcessResult(Optional<T> value, Optional<ProcessException> error) {
    this.value = value;
    this.error = error;
  }
}
