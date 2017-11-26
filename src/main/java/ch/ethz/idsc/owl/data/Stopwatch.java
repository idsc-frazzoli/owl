// code by jph
package ch.ethz.idsc.owl.data;

import java.util.Objects;

/** Stopwatch with nano-seconds precision.
 * 
 * <p>The implementation behaves like a standard digital stopwatch:
 * <ul>
 * <li>the display shows 0:00:00 at the beginning.
 * <li>whenever started, the number on the display continuously increases.
 * <li>whenever stopped, the number on the display is frozen.
 * <li>the number on the display can be read anytime.
 * </ul> */
@DontModify
public class Stopwatch {
  /** @return new instance of {@code Stopwatch} that is started upon creation */
  public static Stopwatch started() {
    Stopwatch stopwatch = new Stopwatch();
    stopwatch.start();
    return stopwatch;
  }

  /** @return new instance of {@code Stopwatch} that is in the stopped state
   * with display reading 0:00:00 */
  public static Stopwatch stopped() {
    return new Stopwatch();
  }
  // ---

  /** last frozen display, initialized at 0:00:00 */
  private long frozen;
  /** last internal {@link System#nanoTime()} time when stopwatch was started */
  private Long tic;

  /** constructor creates an instance that is not started, i.e. the display
   * is frozen at 0:00:00. */
  private Stopwatch() {
  }

  /***************************************************/
  /** start stopwatch
   * @throws Exception if stopwatch is already started */
  public void start() {
    if (!isStopped())
      throw new RuntimeException();
    tic = System.nanoTime();
  }

  /** @throws Exception if stopwatch was not started */
  public void stop() {
    frozen += current();
    tic = null;
    // intended: function does not return anything !
    // use display_...() to obtain absolute time
    // DO NOT MODIFY
  }

  /** @return what is on the display of the stopwatch in nano-seconds:
   * total of all start-until-stop intervals including start-until-now if instance is started */
  public long display_nanoSeconds() {
    return frozen + (isStopped() ? 0 : current());
  }

  /** @return what is on the display of the stopwatch in seconds:
   * total of all start-until-stop intervals including start-until-now if instance is started */
  public double display_seconds() {
    return display_nanoSeconds() * 1e-9;
  }

  /** reset display to 0:00:00
   * @throws Exception if stopwatch is not in stopped state */
  public void resetToZero() { // idea by Jonas
    if (!isStopped())
      throw new RuntimeException();
    frozen = 0;
    // stopped implies: tic == null
  }

  /***************************************************/
  /** @return true if stopwatch is not started */
  // function is private because state of stopwatch can be tracked in the application layer
  private boolean isStopped() {
    return Objects.isNull(tic);
  }

  /** @return internal time difference start-until-now
   * @throws Exception if stopwatch is not started */
  private long current() {
    return System.nanoTime() - tic;
  }
}
