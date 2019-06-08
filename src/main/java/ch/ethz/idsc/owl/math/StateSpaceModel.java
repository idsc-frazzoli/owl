// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Tensor;

/** system dynamics described by a differential constraint
 * 
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/StateSpaceModel.html">StateSpaceModel</a> */
@FunctionalInterface
public interface StateSpaceModel {
  /** flow is function f in
   * x'(t) == f(x(t), u(t))
   * 
   * @param x state coordinate in state space
   * @param u vector of control
   * @return */
  Tensor f(Tensor x, Tensor u);
}
