// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;

public interface Se2PseudoDistanceInterface {
  /** @param p
   * @param q
   * @return returns vector containing angular and lateral distance */
  
 
  static Se2PseudoDistanceInterface se2(Tensor p, Tensor q) {
    return new Se2PseudoDistance(p,q);
  }
  /** @return PseudoDistance */
  Tensor Se2PseudoDistance();

}
