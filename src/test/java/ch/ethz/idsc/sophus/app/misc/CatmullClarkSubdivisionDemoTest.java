// code by jph
package ch.ethz.idsc.sophus.app.misc;

import ch.ethz.idsc.sophus.app.api.DemoHelper;
import junit.framework.TestCase;

public class CatmullClarkSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    DemoHelper.brief(new CatmullClarkSubdivisionDemo());
  }
}
