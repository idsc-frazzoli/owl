// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class PolarClothoid3Test extends TestCase {
  public void testAngles() {
    Tensor pxy = Tensors.vector(0, 0).unmodifiable();
    Tensor qxy = Tensors.vector(1, 0).unmodifiable();
    Tensor angles = Range.of(-3, 4).map(Pi.TWO::multiply);
    for (Tensor angle : angles) {
      ScalarTensorFunction curve = //
          PolarClothoids.INSTANCE.curve(pxy.copy().append(angle), qxy.copy().append(angle));
      Tensor r = curve.apply(RationalScalar.HALF);
      Chop._12.requireClose(r, Tensors.vector(0.5, 0).append(angle));
    }
  }
}
