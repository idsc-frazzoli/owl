// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public interface Bijection {
  TensorUnaryOperator forward();

  TensorUnaryOperator inverse();
}
