// code by jph
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class So2UniformResampleTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = So2UniformResample.of(RealScalar.of(0.1));
    Tensor tensor = curveSubdivision.string(Subdivide.of(0, 10, 20));
    Scalar variance = Variance.ofVector(Differences.of(tensor));
    assertTrue(Chop._20.allZero(variance));
  }
}
