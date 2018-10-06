// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Binomial;
import ch.ethz.idsc.tensor.sca.Power;

public enum BinomialMask implements Function<Integer, Tensor> {
  FUNCTION;
  // ---
  @Override
  public Tensor apply(Integer i) {
    int two_i = 2 * i;
    return Tensors.vector(k -> Binomial.of(two_i, k), two_i + 1).divide(Power.of(2, two_i));
  }
}
