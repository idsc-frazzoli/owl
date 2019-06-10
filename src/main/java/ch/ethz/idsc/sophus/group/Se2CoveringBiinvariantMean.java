// code by jph, ob
package ch.ethz.idsc.sophus.group;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum Se2CoveringBiinvariantMean implements BiinvariantMean {
  INSTANCE;
  // ---
  private static final Scalar ZERO = RealScalar.ZERO;
  // ---

  @Override // from BiinvariantMeanInterface
  public Tensor mean(Tensor sequence, Tensor weights) {
    Scalar amean = So2CoveringBiinvariantMean.INSTANCE.mean(sequence.get(Tensor.ALL, 2), weights);
    // make transformation s.t. mean rotation is zero and retransformation after taking mean
    LieGroupElement lieGroupElement = new Se2CoveringGroupElement(Tensors.of(ZERO, ZERO, amean));
    AtomicInteger index = new AtomicInteger(-1);
    Tensor tmean = sequence.stream() //
        .map(lieGroupElement.inverse()::combine) //
        .map(xya -> Se2Skew.of(xya, weights.Get(index.incrementAndGet()))) //
        .reduce(Se2Skew::add) //
        .get().solve();
    return lieGroupElement.combine(tmean.append(ZERO));
  }
}
