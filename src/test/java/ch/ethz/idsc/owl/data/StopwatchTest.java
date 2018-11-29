// code by jph
package ch.ethz.idsc.owl.data;

import java.io.Serializable;

import junit.framework.TestCase;

public class StopwatchTest extends TestCase {
  public void testSimple() {
    Stopwatch stopwatch = Stopwatch.stopped();
    assertEquals(stopwatch.display_nanoSeconds(), 0);
    assertEquals(stopwatch.display_seconds(), 0.0);
    try {
      stopwatch.stop();
      fail();
    } catch (Exception exception) {
      // ---
    }
    stopwatch.start();
    Math.sin(1);
    assertTrue(0 < stopwatch.display_nanoSeconds());
    stopwatch.stop();
    assertTrue(0 < stopwatch.display_seconds());
    assertEquals(stopwatch.display_seconds(), stopwatch.display_nanoSeconds() * 1e-9);
  }

  public void testStarted() {
    Stopwatch stopwatch = Stopwatch.started();
    assertFalse(stopwatch instanceof Serializable);
    try {
      stopwatch.start();
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertTrue(0 < stopwatch.display_nanoSeconds());
  }

  public void testNonSerializable() {
    Stopwatch stopwatch = Stopwatch.started();
    assertFalse(stopwatch instanceof Serializable);
  }

  public void testReset() {
    Stopwatch stopwatch = Stopwatch.started();
    Math.sin(1);
    stopwatch.stop();
    assertTrue(0 < stopwatch.display_nanoSeconds());
    stopwatch.resetToZero();
    assertTrue(0 == stopwatch.display_nanoSeconds());
  }

  public void testResetFail() {
    Stopwatch stopwatch = Stopwatch.started();
    try {
      stopwatch.resetToZero();
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
