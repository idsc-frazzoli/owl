// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.planar.ClothoidDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** DO NOT USE THIS ON A REAL ROBOT */
public class PseudoSe2CurveIntersection extends SimpleCurveIntersection {
  public PseudoSe2CurveIntersection(Scalar radius) {
    super(radius);
  }

  @Override // from SimpleCurveIntersection
  protected Scalar distance(Tensor tensor) {
    return ClothoidDistance.INSTANCE.norm(tensor);
  }

  @Override // from SimpleCurveIntersection
  protected Tensor split(Tensor prev, Tensor next, Scalar scalar) {
    return Se2Geodesic.INSTANCE.split(prev, next, scalar);
  }
}
