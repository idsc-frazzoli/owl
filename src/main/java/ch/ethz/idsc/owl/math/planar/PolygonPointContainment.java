// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.owl.math.TensorPredicate;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

// API design experiment
/* package */ class PolygonPointContainment implements TensorPredicate {
  /** @param polygon
   * @return predicate that checks containment of a point inside given polygon */
  public static TensorPredicate of(Tensor polygon) {
    return new PolygonPointContainment(polygon);
  }

  // ---
  private final Tensor polygon;

  private PolygonPointContainment(Tensor polygon) {
    this.polygon = polygon;
  }

  @Override // from Predicate
  public boolean test(Tensor point) {
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
}
