// code by jph and vc
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public enum Polygons {
  ;
  /** @param polygon in the 2-dimensional plane
   * @param point of which only the first two coordinates will be considered
   * @return true, if point is inside polygon, otherwise false
   * @throws Exception if the first two entries of point are not of type {@link Scalar} */
  // adapted from PNPOLY - Point Inclusion in Polygon Test W. Randolph Franklin (WRF)
  public static boolean isInside(Tensor polygon, Tensor point) {
    final Scalar tx = point.Get(0);
    final Scalar ty = point.Get(1);
    int i, j;
    boolean c = false;
    for (i = 0, j = polygon.length() - 1; i < polygon.length(); j = i++) {
      Scalar vyi = polygon.Get(i, 1);
      Scalar vyj = polygon.Get(j, 1);
      if (Scalars.lessThan(ty, vyi) != Scalars.lessThan(ty, vyj)) {
        Scalar div = vyj.subtract(vyi);
        if (Scalars.nonZero(div)) {
          Scalar vxi = polygon.Get(i, 0);
          Scalar vxj = polygon.Get(j, 0);
          Scalar r1 = vxj.subtract(vxi).multiply(ty.subtract(vyi));
          if (Scalars.lessThan(tx, r1.divide(div).add(vxi)))
            c = !c;
        }
      }
    }
    return c;
  }

  /** @param polygon not necessarily convex
   * @return signed area circumscribed by given polygon,
   * area is positive when polygon is in counter-clockwise direction */
  // inspired by https://www.mathopenref.com/coordpolygonarea.html
  public static Scalar area(Tensor polygon) {
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
