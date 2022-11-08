package com.mrivanplays.process;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Represents a completion of all the processes.
 *
 * @since 0.0.1
 * @author <a href="mailto:ivan@mrivanplays.com">Ivan Pekov</a>
 */
public final class ProcessesCompletion {

  /**
   * Runs the specified {@code callback} when all the specified {@code completions} complete.
   *
   * @param async whether this method to be called asynchronously or not
   * @param callback a {@link Consumer} of the errors (if any). if there are no errors, the value
   *     will be an empty set.
   * @param completions completions to wait for
   */
  public static void whenAllDone(
      boolean async, Consumer<Set<ProcessException>> callback, ProcessesCompletion... completions) {
    CountDownLatch latch = new CountDownLatch(completions.length);
    Set<ProcessException> errors = ConcurrentHashMap.newKeySet();
    for (ProcessesCompletion completion : completions) {
      completion.whenDoneAsync(
          (err) -> {
            errors.addAll(err);
            latch.countDown();
          });
    }
    if (async) {
      // grab an async executor from somewhere
      completions[0].asyncExecutor.execute(
          () -> {
            try {
              latch.await();
              callback.accept(errors);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          });
    } else {
      try {
        latch.await();
        callback.accept(errors);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Called when all {@link Process Proccesses} are done.
   *
   * <p><b>WARNING: THREAD BLOCKING METHOD.</b>
   *
   * @param callback a {@link Consumer} of the errors (if any). if there are no errors, the value
   *     will be an empty set.
   */
  public void whenDone(Consumer<Set<ProcessException>> callback) {
    if (this.latch.getCount() == 0) {
      callback.accept(this.errors);
      return;
    }
    try {
      this.latch.await();
      callback.accept(this.errors);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Called when all {@link Process Processes} are done. The difference from {@link
   * #whenDone(Consumer)} is that the waiting and the {@code callback} call is done asynchronously.
   *
   * @param callback see {@link #whenDone(Consumer)}
   * @see #whenDone(Consumer)
   */
  public void whenDoneAsync(final Consumer<Set<ProcessException>> callback) {
    this.asyncExecutor.execute(() -> this.whenDone(callback));
  }

  // ======================================

  private final CountDownLatch latch;
  private final Executor asyncExecutor;
  private Set<ProcessException> errors;

  ProcessesCompletion(int processCount, Executor asyncExecutor) {
    this.latch = new CountDownLatch(processCount);
    this.asyncExecutor = asyncExecutor;
    this.errors = ConcurrentHashMap.newKeySet();
  }

  void countDown() {
    this.latch.countDown();
  }

  void countDownWithError(ProcessException error) {
    this.errors.add(error);
    this.latch.countDown();
  }
}
