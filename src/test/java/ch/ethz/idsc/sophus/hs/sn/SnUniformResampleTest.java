// code by jph
package ch.ethz.idsc.sophus.hs.sn;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.math.Distances;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SnUniformResampleTest extends TestCase {
  public void testSimple() {
    Scalar spacing = Pi.VALUE.divide(RealScalar.of(10));
    CurveSubdivision curveSubdivision = SnUniformResample.of(spacing);
    Tensor tensor = Tensors.fromString("{{1, 0}, {0, 1}, {-1, 0}}");
    Tensor string = curveSubdivision.string(tensor);
    Tensor distances = Distances.of(SnMetric.INSTANCE, string);
    Scalar variance = Variance.ofVector(distances);
    assertTrue(Chop._20.allZero(variance));
  }
}
