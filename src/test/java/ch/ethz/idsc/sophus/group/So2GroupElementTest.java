// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class So2GroupElementTest extends TestCase {
  public void testInverse() {
    So2GroupElement so2GroupElement = new So2GroupElement(RealScalar.ONE);
    Scalar scalar = so2GroupElement.inverse().combine(RealScalar.ONE);
    assertEquals(scalar, RealScalar.ZERO);
  }
}
