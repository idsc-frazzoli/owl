// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.IntegerTensorFunction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Binomial;
import ch.ethz.idsc.tensor.sca.Power;

enum BinomialWeights implements IntegerTensorFunction {
  INSTANCE;
  // ---
  @Override
  public Tensor apply(Integer i) {
    if (i < 0)
      throw new IllegalArgumentException("i=" + i);
    int two_i = 2 * i;
    return Tensors.vector(k -> Binomial.of(two_i, k), two_i + 1).divide(Power.of(2, two_i));
  }
}
