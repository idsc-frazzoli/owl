// code by ob, jph
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum FilterResponse {
  ;
  /** filters out division by zero or near-zero */
  private static final Scalar THRESHOLD = RealScalar.of(10.0);

  public static Tensor pdiv(Tensor num, Tensor den) {
    return num.pmul(den.map(Scalar::reciprocal));
  }

  /** @param filtered
   * @param raw
   * @param spectrogramArray
   * @return */
  public static Tensor of(Tensor filtered, Tensor raw, TensorUnaryOperator spectrogramArray) {
    Tensor tensor = pdiv(spectrogramArray.apply(filtered), spectrogramArray.apply(raw));
    return Mean.of(Tensor.of(tensor.stream() //
        .filter(vector -> Scalars.lessEquals(Norm.INFINITY.ofVector(vector), THRESHOLD))));
  }
}