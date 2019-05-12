// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LexicographicSemiorderMinTracker2Test extends TestCase {
  public void testPermutations() {
    LexicographicSemiorderMinTracker<Scalar> lexicographicSemiorderMinTracker = //
        LexicographicSemiorderMinTracker.withSet(Tensors.vector(1, 2, 0.5));
    // lexicographicSemiorderMinTracker.digest(key, x);
  }
}
