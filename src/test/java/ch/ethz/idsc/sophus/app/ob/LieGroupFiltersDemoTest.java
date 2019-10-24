// code by jph
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class LieGroupFiltersDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new LieGroupFiltersDemo());
  }
}
