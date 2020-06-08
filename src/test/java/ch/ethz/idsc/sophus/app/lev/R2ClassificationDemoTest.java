// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class R2ClassificationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R2ClassificationDemo());
  }
}
