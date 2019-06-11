// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LogarithmicSpiralTest extends TestCase {
  public void testSimple() {
    ScalarTensorFunction scalarTensorFunction = new LogarithmicSpiral(RealScalar.of(2), RealScalar.of(0.1759));
    assertEquals(scalarTensorFunction.apply(RealScalar.ZERO), Tensors.vector(2, 0));
    assertTrue(Chop._12.close( //
        scalarTensorFunction.apply(RealScalar.ONE), //
        Tensors.vector(1.2884252164237864, 2.0066033846985687)));
  }
}
