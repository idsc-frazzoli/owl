// code by ob, jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Abs;

public enum FilterResponse {
  ;
  /** filters out division by zero or near-zero */
  private static final Scalar THRESHOLD = RealScalar.of(10.0);

  static Tensor pdiv(Tensor num, Tensor den) {
    return num.pmul(den.map(Scalar::reciprocal));
  }

  /** @param vector
   * @return max of vector */
  private static Scalar max(Tensor vector) {
    return vector.stream() //
        .map(Scalar.class::cast) //
        .map(Abs.FUNCTION) //
        .reduce(Max::of) //
        .get().Get();
  }

  /** @param filtered
   * @param raw
   * @param spectrogramArray
   * @return */
  public static Tensor of(Tensor filtered, Tensor raw, TensorUnaryOperator spectrogramArray) {
    Tensor tensor = pdiv(spectrogramArray.apply(filtered), spectrogramArray.apply(raw));
    return Mean.of(Tensor.of(tensor.stream() //
        .filter(vector -> Scalars.lessEquals(max(vector), THRESHOLD))));
  }
}