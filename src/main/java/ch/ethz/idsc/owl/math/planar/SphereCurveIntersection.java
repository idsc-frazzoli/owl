// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/** intersection of curve with n-dimensional sphere centered at (0, ..., 0) and given radius.
 * 
 * input to intersection query is either a non-cyclic or cyclic polygon
 * the output is the coordinate of intersection using linear interpolation. */
public class SphereCurveIntersection extends AssistedCurveIntersection {
  public SphereCurveIntersection(Scalar radius) {
    super(radius);
  }

  @Override // from SimpleCurveIntersection
  protected final Scalar distance(Tensor tensor) {
    return Norm._2.ofVector(tensor);
  }

  @Override // from SimpleCurveIntersection
  public final Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return RnGeodesic.INSTANCE.split(p, q, scalar);
  }
}
