// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Scalar;

@FunctionalInterface
public interface Metric<T> {
  /** a metric satisfies the following conditions
   * 
   * 1. non-negativity or separation axiom
   * 2. identity of indiscernibles
   * 3. symmetry
   * 4. subadditivity or triangle inequality
   * 
   * <p>implementations threat parameters p and q as immutable
   * 
   * @param p
   * @param q
   * @return distance between p and q */
  Scalar distance(T p, T q);
}
