// code by jph
package ch.ethz.idsc.sophus.app.srf;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class CatmullClarkSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new CatmullClarkSubdivisionDemo());
  }
}
