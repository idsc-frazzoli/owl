// code by jph
package ch.ethz.idsc.owl.data;

/** measure length of intervals between invocations of methods
 * {@link #seconds()} and {@link #hertz()} */
public final class IntervalClock {
  /** started upon construction */
  private long tic = System.nanoTime();

  /** @return period in seconds since last invocation */
  public double seconds() {
    return elapsed() * 1e-9;
  }

  /** @return reciprocal of period in seconds since last invocation */
  public double hertz() {
    return 1.0e9 / elapsed();
  }

  private long elapsed() {
    long toc = System.nanoTime();
    long tac = toc - tic;
    tic = toc;
    return tac;
  }
}
