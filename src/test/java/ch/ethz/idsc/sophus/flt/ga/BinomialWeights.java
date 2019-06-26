// code by jph
package ch.ethz.idsc.sophus.flt.ga;

import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Binomial;
import ch.ethz.idsc.tensor.sca.Power;

public enum BinomialWeights implements Function<Integer, Tensor> {
  INSTANCE;
  // ---
  @Override
  public Tensor apply(Integer i) {
    if (i <= 0)
      throw new IllegalArgumentException("i=" + i);
    return Tensors.vector(k -> Binomial.of(i - 1, k), i).divide(Power.of(2, i - 1));
  }
}
