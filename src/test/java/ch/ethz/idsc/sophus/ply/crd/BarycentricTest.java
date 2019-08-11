// code by jph
package ch.ethz.idsc.sophus.ply.crd;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class BarycentricTest extends TestCase {
  public void testAll() {
    for (Barycentric barycentric : Barycentric.values()) {
      Scalar scalar = barycentric.apply(RealScalar.of(4));
      ExactScalarQ.require(scalar);
    }
  }

  public void testWachspress() {
    Scalar scalar = Barycentric.WACHSPRESS.apply(RealScalar.of(3));
    assertEquals(scalar, RationalScalar.of(1, 3));
    ExactScalarQ.require(scalar);
  }

  public void testDiscreteHarmonic() {
    Scalar scalar = Barycentric.DISCRETE_HARMONIC.apply(RealScalar.of(3));
    assertEquals(scalar, RealScalar.ONE);
  }
}
