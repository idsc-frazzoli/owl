package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class SemiorderTest extends TestCase {
  public void testIdentity() {
    StrictPartialComparator<Scalar> semiorder = Semiorder.comparator(IdentityUtilityFunction.identity(), RealScalar.ONE);
    assertTrue(semiorder.compare(RealScalar.ONE, RealScalar.of(1.5)).equals(StrictPartialComparison.INCOMPARABLE));
    assertTrue(semiorder.compare(RealScalar.of(21), RealScalar.of(21)).equals(StrictPartialComparison.INCOMPARABLE));
    assertTrue(semiorder.compare(RealScalar.of(2.4), RealScalar.of(1)).equals(StrictPartialComparison.GREATER_THAN));
    assertTrue(semiorder.compare(RealScalar.of(3), RealScalar.of(4)).equals(StrictPartialComparison.LESS_THAN));
  }
}
