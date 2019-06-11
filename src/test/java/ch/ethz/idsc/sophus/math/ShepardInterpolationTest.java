// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.sophus.group.RnBiinvariantMean;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class ShepardInterpolationTest extends TestCase {
  public void testSimple() {
    ShepardInterpolation shepardInterpolation = //
        new ShepardInterpolation(Norm._2::between, RnBiinvariantMean.INSTANCE);
    // Tensor tensor =
    shepardInterpolation.at(Range.of(0, 10).map(Tensors::of), Tensors.vector(5.4));
    // System.out.println(tensor);
  }
}
