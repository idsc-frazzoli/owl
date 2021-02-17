// code by jph
package ch.ethz.idsc.owl.math.pursuit;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;

/** intersection of curve with n-dimensional sphere centered at (0, ..., 0) and given radius.
 * 
 * input to intersection query is either a non-cyclic or cyclic polygon
 * the output is the coordinate of intersection using linear interpolation. */
public class SphereCurveIntersection extends AssistedCurveIntersection {
  /** @param radius non-negative */
  public SphereCurveIntersection(Scalar radius) {
    super(radius, RnGeodesic.INSTANCE);
  }

  @Override // from SimpleCurveIntersection
  protected final Scalar distance(Tensor tensor) {
    return Vector2Norm.of(tensor);
  }
}
