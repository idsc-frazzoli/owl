// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class BarycentricRationalInterpolationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BarycentricRationalInterpolationDemo());
  }
}
