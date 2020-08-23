// code by jph
package ch.ethz.idsc.sophus.app.spi;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class LogarithmicSpiralDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new LogarithmicSpiralDemo());
  }
}
