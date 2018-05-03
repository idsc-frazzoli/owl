// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** https://en.wikipedia.org/wiki/Metric_(mathematics) */
public interface TensorMetric {
  /** a metric satisfies the following conditions
   * 
   * 1. non-negativity or separation axiom
   * 2. identity of indiscernibles
   * 3. symmetry
   * 4. subadditivity or triangle inequality
   * 
   * @param p
   * @param q
   * @return distance between p and q */
  Scalar distance(Tensor p, Tensor q);
}
