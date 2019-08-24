// code by jph
package ch.ethz.idsc.sophus.sym;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class PolarScalarTest extends TestCase {
  public void testSimple() {
    Scalar s1 = PolarScalar.of(RealScalar.of(2), RealScalar.of(1.2));
    Scalar s2 = s1.reciprocal();
    PolarScalar scalar = (PolarScalar) s1.multiply(s2);
    assertEquals(scalar.abs(), RealScalar.ONE);
    assertEquals(scalar.arg(), RealScalar.ZERO);
  }

  public void testZero() {
    PolarScalar scalar = PolarScalar.of(RealScalar.of(2), RealScalar.of(1.2));
    PolarScalar zero = scalar.zero();
    assertEquals(zero.abs(), RealScalar.ZERO);
    assertEquals(zero.arg(), RealScalar.ZERO);
  }

  public void testNegate() {
    PolarScalar p = PolarScalar.of(RealScalar.of(2), RealScalar.of(1.2));
    PolarScalar q = p.negate();
    assertEquals(q.abs(), RealScalar.of(-2));
    assertEquals(q.arg(), RealScalar.of(1.2));
    PolarScalar r = (PolarScalar) p.add(q);
    assertEquals(r.abs(), RealScalar.ZERO);
    assertEquals(r.arg(), RealScalar.ZERO);
  }
}
