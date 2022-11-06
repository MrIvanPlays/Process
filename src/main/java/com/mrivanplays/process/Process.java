package com.mrivanplays.process;

/**
 * Represents a process that's going to be run.
 *
 * @since 0.0.1
 * @author <a href="mailto:ivan@mrivanplays.com">Ivan Pekov</a>
 */
public abstract class Process {

  /**
   * Creates a new {@code Process} from a {@link Runnable}
   *
   * @param runnable input runnable
   * @return process
   */
  public static Process fromRunnable(Runnable runnable) {
    return new Process() {
      @Override
      protected void run() {
        runnable.run();
      }
    };
  }

  /**
   * Main logic for running this process.
   */
  protected abstract void run();

  // ===============================================
  private int processNum;

  final void processNum(int processNum) {
    this.processNum = processNum;
  }

  final void runProcess(ProcessesCompletion completion) {
    try {
      this.run();
    } catch (Throwable error) {
      completion.countDownWithError(new ProcessException("in Process #" + this.processNum, error));
      return;
    }
    completion.countDown();
  }

}
