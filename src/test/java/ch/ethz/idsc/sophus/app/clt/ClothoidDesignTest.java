// code by jph
package ch.ethz.idsc.sophus.app.clt;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class ClothoidDesignTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClothoidDesign());
  }
}
