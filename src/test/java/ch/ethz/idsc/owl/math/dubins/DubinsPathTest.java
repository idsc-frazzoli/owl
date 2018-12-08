// code by jph
package ch.ethz.idsc.owl.math.dubins;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DubinsPathTest extends TestCase {
  public void testUnits() {
    DubinsPath dubinsPath = new DubinsPath(DubinsPathType.LRL, Quantity.of(2, "m"), Tensors.fromString("{1[m], 10[m], 1[m]}"));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(Tensors.fromString("{1[m], 2[m], 3}"));
    Tensor tensor = scalarTensorFunction.apply(Quantity.of(.3, "m"));
    assertTrue(Chop._10.close(tensor, Tensors.fromString("{0.7009454891459682[m], 2.0199443237417927[m], 3.15}")));
  }

  public void testOutsideFail() {
    DubinsPath dubinsPath = new DubinsPath(DubinsPathType.LRL, RealScalar.ONE, Tensors.vector(1, 10, 1));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(Tensors.vector(1, 2, 3));
    try {
      scalarTensorFunction.apply(RealScalar.of(-.1));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      scalarTensorFunction.apply(RealScalar.of(13));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
