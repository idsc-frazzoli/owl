// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class S2HermiteSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new S2HermiteSubdivisionDemo());
  }
}
