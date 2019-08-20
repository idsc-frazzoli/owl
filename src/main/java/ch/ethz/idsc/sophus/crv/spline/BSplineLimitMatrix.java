// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Integers;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;

public enum BSplineLimitMatrix {
  ;
  /** @param n
   * @param degree
   * @return row-stochastic matrix with dimensions n x n */
  public static Tensor string(int n, int degree) {
    Integers.requirePositive(n);
    Tensor domain = Range.of(0, n);
    return Transpose.of(Tensors.vector(k -> //
    domain.map(GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, UnitVector.of(n, k))), n));
  }
  // public static Tensor string(int n, int degree) {
  // Tensor domain = Range.of(0, n);
  // return Tensor.of(IdentityMatrix.of(n).stream() //
  // .map(row -> domain.map(GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, row))));
  // }
}
