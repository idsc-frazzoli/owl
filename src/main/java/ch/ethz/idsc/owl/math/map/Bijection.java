// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** injective and surjective mapping.
 * 
 * <p>The concatenation of
 * 1) forward and inverse action, and
 * 2) inverse and forward action
 * reduce to the identity. */
public interface Bijection {
  TensorUnaryOperator forward();

  TensorUnaryOperator inverse();
}
