// code by jph
package ch.ethz.idsc.sophus.symlink;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class SymScalarTest extends TestCase {
  public void testSimple() {
    SymScalar.leaf(3).hashCode();
    SymScalar.of(SymScalar.leaf(2), SymScalar.leaf(3), RationalScalar.HALF).hashCode();
  }

  public void testFail() {
    try {
      SymScalar.of(SymScalar.leaf(2), RealScalar.of(3), RationalScalar.HALF);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
