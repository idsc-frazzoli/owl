// code by jph
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.Pi;
import junit.framework.TestCase;

public class So2GroupElementTest extends TestCase {
  public void testInverse() {
    So2GroupElement so2GroupElement = new So2GroupElement(RealScalar.ONE);
    Scalar scalar = so2GroupElement.inverse().combine(RealScalar.ONE);
    assertEquals(scalar, RealScalar.ZERO);
  }

  public void testAdjoint() {
    So2GroupElement so2GroupElement = new So2GroupElement(RealScalar.ONE);
    assertEquals(so2GroupElement.adjoint(Pi.VALUE), Pi.VALUE);
  }
}
