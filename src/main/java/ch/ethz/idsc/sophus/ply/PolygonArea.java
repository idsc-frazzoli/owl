// code by vc
// inspired by https://www.mathopenref.com/coordpolygonarea.html
package ch.ethz.idsc.sophus.ply;

import java.util.Iterator;

import ch.ethz.idsc.sophus.math.d2.Det2D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/** polygon not necessarily convex
 * 
 * computes signed area circumscribed by given polygon
 * area is positive when polygon is in counter-clockwise direction */
public enum PolygonArea {
  ;
  /** @param polygon
   * @return */
  public static Scalar of(Tensor polygon) {
    if (Tensors.isEmpty(polygon))
      return RealScalar.ZERO;
    Tensor prev = Last.of(polygon);
    Scalar sum = Det2D.of(prev, prev);
    Iterator<Tensor> iterator = polygon.iterator();
    while (iterator.hasNext())
      sum = sum.add(Det2D.of(prev, prev = iterator.next()));
    return sum.multiply(RationalScalar.HALF);
  }
}
