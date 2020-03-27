// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class NonuniformSplineDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new NonuniformSplineDemo());
  }
}
