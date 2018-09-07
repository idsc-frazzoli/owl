// code by ynager
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public enum Pareto {
  ;
  /** @param first vector
   * @param second vector
   * @return true only all entries in second surpass the corresponding entry in first
   * @throws Exception if input parameters are not vectors of the same length */
  public static boolean isDominated(Tensor first, Tensor second) {
    return first.subtract(second).stream() //
        .map(Scalar.class::cast) //
        .allMatch(Sign::isNegative);
  }
}
