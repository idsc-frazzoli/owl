// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** system dynamics described by a differential constraint
 * 
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/StateSpaceModel.html">StateSpaceModel</a> */
public interface StateSpaceModel {
  /** flow is function f in
   * (d_t x) |_t == f(x(t), u(t))
   * 
   * @param x state coordinate in state space
   * @param u vector of control
   * @return */
  Tensor f(Tensor x, Tensor u);

  /** @return L that satisfies | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
   * for any x_1, x_2 and u */
  Scalar getLipschitz();
}
