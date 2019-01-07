// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** @author Andre
 *
 * Searches a totally ordered set for its minimal or maximal element */
public class TotalOrderMinMax {
  /** @param Totally ordered set to be searched for minimal element
   * @return Minimal element of totally ordered set */
  public static Scalar TOmin(Tensor tensor) {
    Scalar min = tensor.Get(0);
    for (int i = 1; i < tensor.length(); i++) {
      min = (Scalars.lessEquals(min, tensor.Get(i))) ? min : tensor.Get(i);
    }
    return min;
  }

  /** @param Totally ordered set to be searched for maximal element
   * @return Maximal element of totally ordered set */
  public static Scalar TOmax(Tensor tensor) {
    Scalar max = tensor.Get(0);
    for (int i = 1; i < tensor.length(); i++) {
      max = (Scalars.lessEquals(tensor.Get(i), max)) ? max : tensor.Get(i);
    }
    return max;
  }
}