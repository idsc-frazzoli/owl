// code by jph
package ch.ethz.idsc.tensor.demo;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class SphereFitDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SphereFitDemo());
  }
}