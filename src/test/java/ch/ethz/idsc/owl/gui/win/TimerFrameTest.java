// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class TimerFrameTest extends TestCase {
  public void testSimple() {
    long convert = TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS);
    assertEquals(convert, 1000);
  }
}
