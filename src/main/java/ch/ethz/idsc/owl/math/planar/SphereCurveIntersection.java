// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.red.Norm;

/** intersection of curve with n-dimensional sphere centered at (0, ..., 0) and given radius.
 * 
 * input to intersection query is either a non-cyclic or cyclic polygon
 * the output is the coordinate of intersection using linear interpolation. */
public class SphereCurveIntersection extends SimpleCurveIntersection {
  public SphereCurveIntersection(Scalar radius) {
    super(radius);
  }

  @Override
  protected Scalar distance(Tensor tensor) {
    return Norm._2.ofVector(tensor);
  }

  @Override
  protected Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return LinearInterpolation.of(Tensors.of(p, q)).at(scalar);
  }
}
