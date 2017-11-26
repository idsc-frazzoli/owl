// code by jph
package ch.ethz.idsc.owl.math;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** system dynamics described by a differential constraint
 * 
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/StateSpaceModel.html">StateSpaceModel</a> */
public interface StateSpaceModel extends Serializable {
  /** flow is function f in
   * (d_t x) |_t == f(x(t), u(t))
   * 
   * @param x
   * @param u
   * @return */
  Tensor f(Tensor x, Tensor u);

  /** | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
   * TODO description need quantifiers: for all / there is ?
   * 
   * @return L */
  Scalar getLipschitz();
}
