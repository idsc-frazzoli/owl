// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class ClothoidDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClothoidDemo());
  }
}