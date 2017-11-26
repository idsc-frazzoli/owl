// code by jph
package ch.ethz.idsc.owl.math.map;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** the term "family" conveys the meaning that the bijection
 * depends on a single parameter, for instance time */
public interface BijectionFamily extends Serializable {
  /** for rendering
   * 
   * @param scalar
   * @return */
  TensorUnaryOperator forward(Scalar scalar);

  /** for collision checking
   * 
   * @param scalar
   * @return */
  TensorUnaryOperator inverse(Scalar scalar);
}
