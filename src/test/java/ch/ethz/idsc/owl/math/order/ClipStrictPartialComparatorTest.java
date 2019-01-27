// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class ClipStrictPartialComparatorTest extends TestCase {
  public void testSimple() {
    StrictPartialComparison strictPartialComparison = //
        ClipStrictPartialComparator.INSTANCE.compare(Clip.function(0, 1), Clip.function(0, 1));
    assertEquals(strictPartialComparison, StrictPartialComparison.INCOMPARABLE);
  }
}
