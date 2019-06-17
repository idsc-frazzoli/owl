// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.tensor.Tensor;

/** interface maps tensor coordinate to an element of a lie group */
@FunctionalInterface
public interface LieGroup {
  /** function produces an instance of a lie group element from a given tensor
   * 
   * @param tensor
   * @return lie group element */
  LieGroupElement element(Tensor tensor);
}
