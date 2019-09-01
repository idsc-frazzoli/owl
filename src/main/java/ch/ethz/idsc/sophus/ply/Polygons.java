// code by jph
// adapted from PNPOLY - Point Inclusion in Polygon Test W. Randolph Franklin (WRF)
package ch.ethz.idsc.sophus.ply;

import java.util.Iterator;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

public enum Polygons {
  ;
  /** @param polygon in the 2-dimensional plane
   * @param point of which only the first two coordinates will be considered
   * @return true, if point is inside polygon, otherwise false
   * @throws Exception if the first two entries of point are not of type {@link Scalar} */
  public static boolean isInside(Tensor polygon, Tensor point) {
    final Scalar tx = point.Get(0);
    final Scalar ty = point.Get(1);
    if (Tensors.isEmpty(polygon))
      return false;
    boolean c = false;
    Tensor prev = Last.of(polygon);
    Iterator<Tensor> iterator = polygon.iterator();
    while (iterator.hasNext()) {
      Tensor next = iterator.next();
      Scalar py = prev.Get(1);
      Scalar ny = next.Get(1);
      if (Scalars.lessThan(ty, ny) != Scalars.lessThan(ty, py)) {
        Scalar div = py.subtract(ny);
        // assume div == 0 => py == ny => IF-condition above is false; therefore here div != 0
        Scalar px = prev.Get(0);
        Scalar nx = next.Get(0);
        Scalar r1 = px.subtract(nx).multiply(ty.subtract(ny));
        c ^= Scalars.lessThan(tx, r1.divide(div).add(nx));
      }
      prev = next;
    }
    return c;
  }
}
