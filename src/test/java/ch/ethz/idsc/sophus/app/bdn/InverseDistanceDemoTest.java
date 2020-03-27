// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class InverseDistanceDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new InverseDistanceDemo());
  }
}
