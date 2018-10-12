// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;

/** interface maps tensor coordinate to an element of a lie group */
public interface LieGroup {
  /** @param tensor
   * @return lie group element */
  LieGroupElement element(Tensor tensor);
}
