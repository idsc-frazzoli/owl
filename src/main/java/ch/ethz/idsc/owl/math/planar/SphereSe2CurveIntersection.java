// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/** intersection of SE2 curve with 2-dimensional sphere centered at (0, 0) and given radius. */
public class SphereSe2CurveIntersection extends AssistedCurveIntersection {
  /** @param radius non-negative */
  public SphereSe2CurveIntersection(Scalar radius) {
    super(radius, Se2Geodesic.INSTANCE);
  }

  @Override // from SimpleCurveIntersection
  protected Scalar distance(Tensor tensor) {
    return Norm._2.ofVector(Extract2D.FUNCTION.apply(tensor));
  }
}
