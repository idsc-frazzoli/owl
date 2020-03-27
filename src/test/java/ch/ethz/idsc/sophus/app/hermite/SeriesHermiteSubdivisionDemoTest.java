// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class SeriesHermiteSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SeriesHermiteSubdivisionDemo());
  }
}
