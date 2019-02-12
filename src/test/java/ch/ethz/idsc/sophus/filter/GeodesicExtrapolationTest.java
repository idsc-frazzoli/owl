// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class GeodesicExtrapolationTest extends TestCase {
  public void testSimple() {
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(RnGeodesic.INSTANCE, SmoothingKernel.DIRICHLET);
    for (int index = 2; index < 10; ++index) {
      Scalar result = tensorUnaryOperator.apply(Range.of(0, index)).Get();
      System.out.println("expected=" + index + "    result=" + result);
    }
  }
}
