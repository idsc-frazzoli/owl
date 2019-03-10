// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ArrowheadTest extends TestCase {
  public void testExact() {
    ExactTensorQ.require(Arrowhead.of(RealScalar.ONE));
  }

  public void testLength() {
    assertEquals(Arrowhead.of(6).length(), 3);
  }

  public void testMean() {
    Tensor tensor = Mean.of(Arrowhead.of(2));
    assertTrue(Chop.NONE.allZero(tensor));
  }
}
