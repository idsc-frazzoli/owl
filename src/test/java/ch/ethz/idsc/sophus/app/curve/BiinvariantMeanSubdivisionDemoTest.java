// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import ch.ethz.idsc.sophus.app.subdiv.BiinvariantMeanSubdivisionDemo;
import junit.framework.TestCase;

public class BiinvariantMeanSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BiinvariantMeanSubdivisionDemo());
  }
}
