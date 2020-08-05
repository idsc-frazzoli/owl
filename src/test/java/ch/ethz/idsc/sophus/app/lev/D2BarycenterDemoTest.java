// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class D2BarycenterDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new D2BarycenterDemo());
  }
}
