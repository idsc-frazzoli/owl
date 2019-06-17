// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Split2HiDual3PointCurveSubdivisionTest extends TestCase {
  private static final CurveSubdivision CURVE_SUBDIVISION = //
      Split2HiDual3PointCurveSubdivision.of(RnGeodesic.INSTANCE, RationalScalar.of(1, 3), RationalScalar.of(1, 4));

  public void testCyclic() {
    Tensor cyclic = CURVE_SUBDIVISION.cyclic(Tensors.vector(1, 2, 3, 4));
    assertEquals(cyclic, Tensors.fromString("{37/12, 23/12, 17/12, 31/12, 29/12, 43/12, 37/12, 23/12}"));
    ExactTensorQ.require(cyclic);
  }

  public void testString() {
    Tensor string = CURVE_SUBDIVISION.string(Tensors.vector(1, 2, 3, 4));
    assertEquals(string, Tensors.fromString("{5/4, 17/12, 31/12, 29/12, 43/12, 15/4}"));
    ExactTensorQ.require(string);
  }
}
