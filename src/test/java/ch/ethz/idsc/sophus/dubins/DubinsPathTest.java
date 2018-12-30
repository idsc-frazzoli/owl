// code by jph
package ch.ethz.idsc.sophus.dubins;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DubinsPathTest extends TestCase {
  public void testWithoutUnits() {
    DubinsPath dubinsPath = new DubinsPath(DubinsPathType.LSR, RealScalar.of(2), Tensors.vector(3, 2, 1));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(Tensors.vector(0, 0, 0));
    assertTrue(Chop._10.close(scalarTensorFunction.apply(RealScalar.of(0.3)), //
        Tensors.fromString("{0.29887626494719843, 0.022457844127915516, 0.15}")));
    assertTrue(Chop._10.close(scalarTensorFunction.apply(RealScalar.of(4.7)), //
        Tensors.fromString("{2.1152432160432038, 3.554267073891487, 1.5}")));
    assertTrue(Chop._10.close(scalarTensorFunction.apply(RealScalar.of(5.8)), //
        Tensors.fromString("{2.349039629628753, 4.6192334093884515, 1.1}")));
  }

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
