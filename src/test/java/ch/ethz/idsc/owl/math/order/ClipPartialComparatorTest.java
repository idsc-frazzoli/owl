// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class ClipPartialComparatorTest extends TestCase {
  public void testSimple() {
    PartialComparison partialComparison = ClipPartialComparator.INSTANCE.compare(Clip.function(0, 1), Clip.function(0, 1));
    // TODO results in INCOMPARABLE
    // System.out.println(partialComparison);
  }
}
