// code by vc
// inspired by https://www.mathopenref.com/coordpolygonarea.html
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;

/** polygon not necessarily convex
 * 
 * computes signed area circumscribed by given polygon
 * area is positive when polygon is in counter-clockwise direction */
public enum PolygonArea implements TensorScalarFunction {
  FUNCTION;
  // ---
  @Override
  public Scalar apply(Tensor polygon) {
    int last = polygon.length() - 1;
    Scalar sum = det(polygon.get(last), polygon.get(0));
    for (int index = 0; index < last; ++index)
      sum = sum.add(det(polygon.get(index), polygon.get(index + 1)));
    return sum.multiply(RationalScalar.HALF);
  }

  // helper function
  private static Scalar det(Tensor p, Tensor q) {
    return Cross2D.of(p).dot(q).Get();
  }
}
