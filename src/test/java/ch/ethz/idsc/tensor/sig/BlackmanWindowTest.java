// code by jph
package ch.ethz.idsc.tensor.sig;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BlackmanWindowTest extends TestCase {
  public void testSimple() {
    Scalar result = new BlackmanWindow().apply(RealScalar.of(.2));
    Scalar expect = RealScalar.of(0.50978713763747791812); // checked with Mathematica
    assertTrue(Chop._12.close(result, expect));
  }

  public void testFail() {
    try {
      new BlackmanWindow().apply(RealScalar.of(-.51));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
