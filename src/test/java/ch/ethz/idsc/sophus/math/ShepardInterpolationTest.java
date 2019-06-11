// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.sophus.group.RnBiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ShepardInterpolationTest extends TestCase {
  public void testSimple() {
    ShepardInterpolation shepardInterpolation = //
        new ShepardInterpolation(Norm2Squared::between, RnBiinvariantMean.INSTANCE);
    Scalar scalar = (Scalar) shepardInterpolation.at( //
        Range.of(0, 10).map(Tensors::of), Range.of(0, 10), Tensors.vector(5.4));
    Chop._12.requireClose(scalar, RealScalar.of(5.268238109178673));
  }
}
