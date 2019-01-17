// code by jph
package ch.ethz.idsc.owl.ani.api;

import ch.ethz.idsc.tensor.Scalar;

@FunctionalInterface
public interface AnimationInterface {
  /** @param now measure of real time */
  void integrate(Scalar now);
}
