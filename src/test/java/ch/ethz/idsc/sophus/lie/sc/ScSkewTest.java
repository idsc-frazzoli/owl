// code by jph
package ch.ethz.idsc.sophus.lie.sc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ScSkewTest extends TestCase {
  public void testSimple() {
    ScSkew scSkew = new ScSkew(RealScalar.ONE);
    Scalar scalar = scSkew.apply(RealScalar.of(3));
    Chop._10.requireClose(scalar, RealScalar.of(0.5493061443340549));
  }

  public void testFailZero() {
    try {
      new ScSkew(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailNull() {
    try {
      new ScSkew(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
