// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.planar.ClothoidDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** DO NOT USE THIS ON A REAL ROBOT */
public class PseudoSe2CurveIntersection extends AssistedCurveIntersection {
  /** @param radius non-negative */
  public PseudoSe2CurveIntersection(Scalar radius) {
    super(radius, Se2Geodesic.INSTANCE);
  }

  @Override // from SimpleCurveIntersection
  protected Scalar distance(Tensor tensor) {
    return ClothoidDistance.INSTANCE.norm(tensor);
  }
}
