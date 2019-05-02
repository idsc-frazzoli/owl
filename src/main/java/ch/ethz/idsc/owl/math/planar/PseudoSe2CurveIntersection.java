// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2ParametricDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class PseudoSe2CurveIntersection extends SimpleCurveIntersection {
  public PseudoSe2CurveIntersection(Scalar radius) {
    super(radius);
  }

  @Override
  protected Scalar distance(Tensor tensor) {
    return Se2ParametricDistance.INSTANCE.norm(tensor);
  }

  @Override
  protected Tensor split(Tensor prev, Tensor next, Scalar scalar) {
    return Se2Geodesic.INSTANCE.split(prev, next, scalar);
  }
}
