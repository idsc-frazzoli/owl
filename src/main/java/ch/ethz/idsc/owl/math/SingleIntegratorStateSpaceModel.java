// code by bapaden and jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Tensor;

/** the name "single" hints that if the state is (position) then control u acts as (velocity).
 * 
 * implementation for arbitrary dimensions
 * 
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * | f(x_1, u) - f(x_2, u) | == | u - u | == 0
 * therefore Lipschitz L == 0
 * 
 * see also {@link DoubleIntegratorStateSpaceModel} */
public enum SingleIntegratorStateSpaceModel implements StateSpaceModel {
  INSTANCE;
  // ---
  /** f(x, u) == u */
  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    return u;
  }
}
