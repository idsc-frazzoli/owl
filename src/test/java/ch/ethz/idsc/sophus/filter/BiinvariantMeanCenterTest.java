// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.Se2BiinvariantMean;
import junit.framework.TestCase;

public class BiinvariantMeanCenterTest extends TestCase {
  public void testSimple() {
    try {
      BiinvariantMeanCenter.of(Se2BiinvariantMean.INSTANCE, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
