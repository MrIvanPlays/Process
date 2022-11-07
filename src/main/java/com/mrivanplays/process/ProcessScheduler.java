package com.mrivanplays.process;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a scheduler of {@link Process Processes}.
 *
 * @since 0.0.1
 * @author <a href="mailto:ivan@mrivanplays.com">Ivan Pekov</a>
 */
public final class ProcessScheduler {

  private final Executor async;
  private final ExecutorService service;

  /** Create a new {@code ProcessScheduler} which also creates a new {@link ExecutorService} */
  public ProcessScheduler() {
    this(
        Executors.newCachedThreadPool(
            new ThreadFactory() {

              private AtomicInteger count = new AtomicInteger(0);

              @Override
              public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("ProcessScheduler Thread #" + count.getAndIncrement());
                return thread;
              }
            }));
  }

  /**
   * Create a new {@code ProcessScheduler}
   *
   * @param async executor in which processes are executed
   */
  public ProcessScheduler(Executor async) {
    this.async = async;
    this.service = async instanceof ExecutorService ? (ExecutorService) async : null;
  }

  /**
   * Runs all the specified {@link Process} {@code processes} whilst also returning a {@link
   * ProcessesCompletion}.
   *
   * @param processes the processes to run
   * @return a process completion
   * @see Process
   * @see ProcessesCompletion
   */
  public ProcessesCompletion runProcesses(Process... processes) {
    ProcessesCompletion ret = new ProcessesCompletion(processes.length, this.async);
    for (int i = 0; i < processes.length; i++) {
      int processNum = i + 1;
      Process process = processes[i];
      this.async.execute(
          () -> {
            process.processNum(processNum);
            process.runProcess(ret);
          });
    }
    return ret;
  }

  /**
   * Runs all the specified {@link ResultedProcess} whilst also returning a {@link
   * ResultedProcessesCompletion}.
   *
   * @param processes the resulted processes to run
   * @return a process completion
   * @param <T> result value type parameter
   * @see ResultedProcess
   * @see ResultedProcessesCompletion
   */
  public <T> ResultedProcessesCompletion<T> runProcesses(ResultedProcess<T>... processes) {
    ResultedProcessesCompletion<T> ret =
        new ResultedProcessesCompletion<>(processes.length, this.async);
    for (ResultedProcess<T> process : processes) {
      this.async.execute(() -> process.runProcess(ret));
    }
    return ret;
  }

  /**
   * If an {@link ExecutorService} has been created/detected whilst this {@code ProcessScheduler}
   * was created, this will call it's {@link ExecutorService#shutdown()} method.
   */
  public void shutdownExecutorService() {
    if (this.service != null) {
      this.service.shutdown();
    }
  }
}
