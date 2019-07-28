// code by jph
package ch.ethz.idsc.sophus.crv.dubins;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.dubins.DubinsPath.Type;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DubinsPathGeneratorTest extends TestCase {
  public void testSimple() {
    DubinsPath dubinsPath = new DubinsPath(Type.LSR, Quantity.of(1, "m"), Tensors.fromString("{" + Math.PI / 2 + "[m], 10[m], " + Math.PI / 2 + "[m]}"));
    Tensor g0 = Tensors.fromString("{0[m], 0[m], 0}").unmodifiable();
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(g0);
    Chop._12.requireClose(scalarTensorFunction.apply(Quantity.of(0, "m")), g0);
    {
      Tensor tensor = scalarTensorFunction.apply(Quantity.of(1.5707963267948966, "m"));
      Chop._12.requireClose(tensor, Tensors.fromString("{1[m], 1[m], 1.5707963267948966}"));
    }
    {
      Tensor tensor = scalarTensorFunction.apply(Quantity.of(11.570796326794897, "m"));
      Chop._12.requireClose(tensor, Tensors.fromString("{1[m], 11[m], 1.5707963267948966}"));
    }
    {
      Tensor tensor = scalarTensorFunction.apply(Quantity.of(13.141592653589793, "m"));
      Chop._12.requireClose(tensor, Tensors.fromString("{2[m], 12[m], 0}"));
    }
  }

  public void testFail() throws ClassNotFoundException, IOException {
    DubinsPath dubinsPath = new DubinsPath(Type.LSR, Quantity.of(1, "m"), Tensors.fromString("{" + Math.PI / 2 + "[m], 10[m], " + Math.PI / 2 + "[m]}"));
    Tensor g0 = Tensors.fromString("{0[m], 0[m], 0}").unmodifiable();
    ScalarTensorFunction scalarTensorFunction = Serialization.copy(dubinsPath.sampler(g0));
    try {
      scalarTensorFunction.apply(Quantity.of(-0.1, "m"));
      fail();
    } catch (Exception exception) {
      // ---
    }
    Scalar exceed = Quantity.of(0.1, "m").add(dubinsPath.length());
    try {
      scalarTensorFunction.apply(exceed);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
