// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Sort;
import junit.framework.TestCase;

public class Split2LoDual3PointCurveSubdivisionTest extends TestCase {
  private static final CurveSubdivision CURVE_SUBDIVISION = //
      Split2LoDual3PointCurveSubdivision.of(RnGeodesic.INSTANCE, RationalScalar.of(1, 3), RationalScalar.of(1, 4));

  public void testCyclic() {
    Tensor cyclic = CURVE_SUBDIVISION.cyclic(Tensors.vector(1, 2, 3, 4));
    assertEquals(cyclic, Tensors.fromString("{11/4, 9/4, 7/4, 9/4, 11/4, 13/4, 11/4, 9/4}"));
    ExactTensorQ.require(cyclic);
  }

  public void testString() {
    Tensor string = CURVE_SUBDIVISION.string(Tensors.vector(1, 2, 3, 4));
    assertEquals(string, Tensors.fromString("{5/4, 7/4, 9/4, 11/4, 13/4, 15/4}"));
    ExactTensorQ.require(string);
    assertEquals(string, Sort.of(string));
  }
}
