package com.mrivanplays.process;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Represents a completion of all the {@link ResultedProcess resulted processes}
 *
 * @param <T> result type parameter
 * @author <a href="mailto:ivan@mrivanplays.com">Ivan Pekov</a>
 * @since 0.0.1
 */
public final class ResultedProcessesCompletion<T> {

  /**
   * Called when all {@link ResultedProcess Proccesses} are done.
   * <p><b>WARNING: THREAD BLOCKING METHOD.</b>
   *
   * @param callback a {@link Consumer} of the results
   */
  public void whenDone(Consumer<Map<String, ProcessResult<T>>> callback) {
    if (this.latch.getCount() == 0) {
      callback.accept(this.resultMap);
      return;
    }
    try {
      this.latch.await();
      callback.accept(this.resultMap);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Called when all {@link ResultedProcess Processes} are done. The difference from {@link
   * #whenDone(Consumer)} is that the waiting and the {@code callback} call is done asynchronously.
   *
   * @param callback a {@link Consumer} of the results.
   * @see #whenDone(Consumer)
   */
  public void whenDoneAsync(Consumer<Map<String, ProcessResult<T>>> callback) {
    this.asyncExecutor.execute(() -> this.whenDone(callback));
  }

  // ======================================

  private final CountDownLatch latch;
  private final Executor asyncExecutor;
  private Map<String, ProcessResult<T>> resultMap;

  ResultedProcessesCompletion(int processCount, Executor asyncExecutor) {
    this.latch = new CountDownLatch(processCount);
    this.asyncExecutor = asyncExecutor;
    this.resultMap = new ConcurrentHashMap<>();
  }

  void countDown(String identifier, ProcessResult<T> result) {
    this.resultMap.put(identifier, result);
    this.latch.countDown();
  }
}
