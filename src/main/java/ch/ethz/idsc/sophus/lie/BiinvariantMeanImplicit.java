// code by ob
package ch.ethz.idsc.sophus.lie;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;

/** Calculates the Biinvariant mean of a sequence in any Lie group by iterating over the implicit
 * formulation of the barycentric equation up until a proximity condition is fulfilled */
public class BiinvariantMeanImplicit implements Serializable {
  private static final int MAX_ITERATIONS = 100;
  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final Chop chop = Chop._12;

  BiinvariantMeanImplicit(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
  }

  /** @param sequence
   * @param weights
   * @param mean estimate
   * @return improved mean estimate */
  private Tensor estimationUpdate(Tensor sequence, Tensor weights, Tensor mean) {
    LieGroupElement lieGroupElement = lieGroup.element(mean);
    return lieGroupElement.combine(lieExponential.exp(weights.dot(Tensor.of(sequence.stream() //
        .map(lieGroupElement.inverse()::combine) //
        .map(lieExponential::log)))));
  }

  public Optional<Tensor> apply(Tensor sequence, Tensor weights) {
    Tensor next = sequence.get(0);
    Tensor prev = next;
    int count = 0;
    while (++count < MAX_ITERATIONS) {
      next = estimationUpdate(sequence, weights, prev);
      if (chop.allZero(lieExponential.log(lieGroup.element(next).inverse().combine(prev))))
        return Optional.of(next);
      prev = next;
    }
    return Optional.empty();
  }
}
